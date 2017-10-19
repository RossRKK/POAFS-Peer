package poafs;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import poafs.auth.IAuthenticator;
import poafs.cryto.IDecrypter;
import poafs.file.EncryptedFileBlock;
import poafs.file.FileBlock;
import poafs.file.FileMeta;
import poafs.peer.IPeer;
import poafs.peer.NetworkPeer;

/**
 * An input stream that represents a file that exists on the network.
 * @author rkk2
 *
 */
public class PoafsFileStream extends InputStream {
	
	/**
	 * The authenticator being used by this peer.
	 */
	private IAuthenticator auth;
	
	/**
	 * The file that this stream reads from.
	 */
	private String fileId;
	
	/**
	 * The block that is currently being read from.
	 */
	private int currentFetchIndex = 0;
	
	/**
	 * The block that is currently being read from.
	 */
	private int currentReadBlockIndex = 0;
	
	/**
	 * The current position inside the block we are reading from.
	 */
	private int currentReadIndex = 0;
	
	/**
	 * The number of file blocks should be fetched before they are required.
	 */
	private int preloadDistance;
	
	/**
	 * The internal block fetchers.
	 */
	private HashMap<Integer, BlockFetcher> fetchers =  new HashMap<Integer, BlockFetcher>();
	
	/**
	 * The files meta data.
	 */
	private FileMeta info;
	
	/**
	 * The poafs file that is being loaded.
	 */
	private HashMap<Integer, FileBlock> fileContent = new HashMap<Integer, FileBlock>();;
	
	public PoafsFileStream(String fileId, int preloadDistance, IAuthenticator auth) {
		this.auth = auth;
		info = auth.getInfoForFile(fileId);
		this.fileId = fileId;
		this.preloadDistance = Math.min(preloadDistance, info.getLength());
		
		initialFetch();
	}

	/**
	 * Start fetching data.
	 */
	private void initialFetch() {
		for (int i = 0; i < preloadDistance; i++) {
			startFetcher();
			
			currentFetchIndex = i;
		}
	}
	
	/**
	 * Step to the next block.
	 * @return Whether there was a next block to step to.
	 */
	private boolean stepToNextBlock() {
		//check that the next block exists
		if (currentReadBlockIndex + 1 < info.getLength()) {
			//increment the counters
			currentReadBlockIndex ++;
			currentReadIndex = 0;
			
			//keep the fetchers up to date
			if (currentFetchIndex + 1 < info.getLength()) {
				currentFetchIndex ++;
				startFetcher();
			}

			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Start up a new block fetcher.
	 */
	private void startFetcher() {
		//start up a new block fetcher
		BlockFetcher bf = new BlockFetcher(fileId, currentFetchIndex, auth, fileContent);
		new Thread(bf).start();
		fetchers.put(currentFetchIndex, bf);
	}

	@Override
	public int read() throws IOException {
		try {
			//wait until the fetcher has finished getting the block before allowing the read
			BlockFetcher fetcher = fetchers.get(currentReadBlockIndex);
			synchronized (fetcher) {
				while (fileContent.get(currentReadBlockIndex) == null) {
					fetcher.wait();
				}
				
				int output = fileContent.get(currentReadBlockIndex).getContent()[currentReadIndex];
				//move the read head
				currentReadIndex ++;
				
				return output;
			}
		} catch (IndexOutOfBoundsException e) {
			//move to the next block
			if (stepToNextBlock()) {
				return read();
			} else {
				return -1;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return -1;
		}
	}
}
/**
 * A class that fetches a block and decrypts it.
 * @author rkk2
 *
 */
class  BlockFetcher implements Runnable {
	
	private IAuthenticator auth;
	
	private String fileId;
	
	private int index;
	
	private HashMap<Integer, FileBlock> fileContent;
	
	BlockFetcher(String fileId, int index, IAuthenticator auth, HashMap<Integer, FileBlock> fileContent) {
		this.auth = auth;
		this.fileId = fileId;
		this.index = index;
		this.fileContent = fileContent;
	}

	@Override
	public void run() {
		try {
			fileContent.put(index, decryptBlock(getBlock(fileId, index)));
			synchronized (this) {
				this.notifyAll();
			}
		} catch (IOException e) {
			System.err.println("Error fetching block: " + fileId + ":" + index);
		}
	}
	
	/**
	 * Decrypt a file block.
	 * @param block The file block to be decrypted.
	 * @return The decrypted file block.
	 */
	private FileBlock decryptBlock(FileBlock block) {
		if (block instanceof EncryptedFileBlock) {
			String peerId = block.getOriginPeerId();
			
			IDecrypter d = auth.getKeyForPeer(peerId);
			
			try {
				return d.decrypt((EncryptedFileBlock)block);
			} catch (Exception e) {
				System.out.println("Error reading: " + fileId + ":" + index);
				e.printStackTrace();
				return null;
			}
		} else {
			return block;
		}
	}
	
	/**
	 * Get a file block off of a peer that has it.
	 * @param fileId The id of the file.
	 * @param block The index of the block.
	 * @return The file block.
	 * @throws IOException 
	 */
	private FileBlock getBlock(String fileId, int block) throws IOException {
		Random r = new Random();
		List<String> peerIds = auth.findBlock(fileId, block);
		String peerId = null;
		
		boolean connected = false;
		while (!connected) {
			try {
				//choose a random peer
				peerId = peerIds.get(r.nextInt(peerIds.size()));
				
				InetSocketAddress addr = auth.getHostForPeer(peerId);
				
				//get the block off of the peer
				IPeer peer = new NetworkPeer(peerId, addr);
			
				peer.openConnection();
				
				connected = true;
				
				return peer.requestBlock(fileId, block);
			} catch (IOException e) {
				System.err.println(peerId + " was unreachable");
				peerIds.remove(peerId);
				
				if (peerIds.size() == 0) {
					break;
				}
			}
		}
		throw new IOException();
	}
}
