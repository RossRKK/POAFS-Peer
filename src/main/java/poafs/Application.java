package poafs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import poafs.auth.IAuthenticator;
import poafs.auth.NetAuthenticator;
import poafs.cryto.IDecrypter;
import poafs.cryto.IEncrypter;
import poafs.file.EncryptedFileBlock;
import poafs.file.FileBlock;
import poafs.file.FileManager;
import poafs.file.FileMeta;
import poafs.file.PoafsFile;
import poafs.lib.Reference;
import poafs.local.PropertiesManager;
import poafs.net.Server;
import poafs.peer.IPeer;
import poafs.peer.NetworkPeer;

public class Application {
	/**
	 * The authenticator being used by this peer.
	 */
	private static IAuthenticator auth;
	
	/**
	 * The encrypter to be used for all local files.
	 */
	private static IEncrypter localEncrypter;
	
	/**
	 * The file system manager.
	 */
	private static FileManager fileManager = new FileManager();
	
	/**
	 * System input scanner.
	 */
	private static Scanner sc = new Scanner(System.in);

	/**
	 * The application properties manager.
	 */
	private static PropertiesManager pm = new PropertiesManager();
	
	private static int blockLength = 1024;
	
	public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		pm.loadProperties();
		
		auth = new NetAuthenticator(args[0], Integer.parseInt(args[1]), false);
		
		//start the local server
		new Thread(new Server(Reference.DEFAULT_PORT, fileManager)).start();
		
		ui();
	}
	
	private static void ui() {
		
		System.out.println("User Name: ");
		String user = sc.nextLine();
		
		System.out.println("Password: ");
		String pass = sc.nextLine();
		
		boolean authorised = auth.authoriseUser(user, pass);
		
		if (authorised) {
			System.out.println("Logged in.");
			localEncrypter = auth.registerPeer();
			System.out.println("Registered");
		
			boolean exit = false;
			
			while (!exit) {
				String command = sc.nextLine();
				switch (command) {
					case "ls":
						listFiles();
						break;
					case "load":
						loadFile();
						break;
					case "register-file":
					try {
						registerFile();
					} catch (Exception e) {
						e.printStackTrace();
					}
						break;
					case "quit":
						exit = true;
						break;
					default:
						System.out.println("Unrecognised Command");
				}
				
			}
		} else {
			System.out.println("Error authenticating");
		}
		
		sc.close();
	}
	
	/**
	 * Register a local file with the network.
	 * @throws IOException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	private static void registerFile() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		System.out.println("Enter path to file: ");
		String path = sc.nextLine();
		String fileName = sc.nextLine();
		String id = UUID.randomUUID().toString();
		System.out.println(id);
		
		//read in the file
		File orig = new File(path);
		int numOfBytes = (int) orig.length();
		FileInputStream inFile = new FileInputStream(orig);
		byte[] bytes = new byte[numOfBytes];
		inFile.read(bytes);
		inFile.close();
		System.out.println("File read");
		
		int noBlocks = (int) Math.ceil((double)bytes.length / blockLength);
		
		PoafsFile file = new PoafsFile(id);
		
		for (int i = 0; i < noBlocks; i++) {
			int remainingBytes = bytes.length - (i * blockLength);
			int thisBlockLength = Math.min(blockLength, remainingBytes);
			
			byte[] contents = Arrays.copyOfRange(bytes, i * blockLength, i * blockLength + thisBlockLength);
			
			FileBlock block = new FileBlock(id, contents, i);
			EncryptedFileBlock encrypted = localEncrypter.encrypt(block);
			
			file.addBlock(encrypted);
		}
		System.out.println("Encrypted");
		
		fileManager.registerFile(file);
		file.saveFile();
		
		auth.registerFile(file, fileName);
		System.out.println("Registered");
	}

	/**
	 * Download a file.
	 */
	private static void loadFile() {
		String fileId = sc.nextLine();
		
		FileMeta info = auth.getInfoForFile(fileId);
		System.out.println("Length: " + info.getLength());
		
		PoafsFile f = new PoafsFile(info.getId());
		
		for (int i = 0; i < info.getLength(); i++) {
			f.addBlock(getBlock(fileId, i));
		}
		System.out.println(f.getNumBlocks() + " blocks recieved");
		
		List<FileBlock> decrypted = new ArrayList<FileBlock>();
		for (FileBlock block:f.getBlocks()) {
			if (block instanceof EncryptedFileBlock) {
				String peerId = block.getOriginPeerId();
				
				IDecrypter d = auth.getKeyForPeer(peerId);
				
				try {
					decrypted.add(d.decrypt((EncryptedFileBlock)block));
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
		}
		System.out.println("Decrypted");
		
		for (FileBlock block:decrypted) {
			System.out.print(new String(block.getContent()));
		}
		System.out.println("End");
	}

	/**
	 * Get a file block off of a peer that has it.
	 * @param fileId The id of the file.
	 * @param block The index of the block.
	 * @return The file block.
	 */
	private static FileBlock getBlock(String fileId, int block) {
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
				System.out.println(peerId + " was unreachable");
				peerIds.remove(peerId);
				
				if (peerIds.size() == 0) {
					break;
				}
			}
		}
		return null;
	}

	private static void listFiles() {
		List<FileMeta> files = auth.listFiles();
		
		for (FileMeta f:files) {
			System.out.println(f.getFileName() + " " + f.getId());
		}
		
		System.out.println("End");
	}

	public static IAuthenticator getAuth() {
		return auth;
	}
	
	public static IEncrypter getLocalEncrypter() {
		return localEncrypter;
	}
	
	public static PropertiesManager getPropertiesManager() {
		return pm;
	}
	
	public static FileManager getFileManager() {
		return fileManager;
	}
}
