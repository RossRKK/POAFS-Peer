package poafs.auth;

import java.net.InetSocketAddress;
import java.util.List;

import poafs.cryto.IDecrypter;
import poafs.cryto.IEncrypter;
import poafs.file.FileMeta;
import poafs.file.PoafsFile;

/**
 * An interface that represents the auth service for the network.
 * @author rossrkk
 *
 */
public interface IAuthenticator {
	/**
	 * Get the decrypter for a given peer.
	 * @param peerId The id of the peer who provided the block we want to decrypt.
	 * @return The decrypter that can decrypt files from the given peer.
	 */
	public IDecrypter getKeyForPeer(String peerId);
	
	/**
	 * Get the hostname this peer can be accessed on.
	 * @param peerId The id of the peer.
	 * @return The hostname of the peer.
	 */
	public InetSocketAddress getHostForPeer(String peerId);
	
	
	/**
	 * Authorise the user TODO
	 * @return Whether the user is authorised.
	 */
	public boolean authoriseUser(String userName, String password);
	
	/**
	 * List all available files on this auth server.
	 * @return A list of files.
	 */
	public List<FileMeta> listFiles();
	
	public FileMeta getInfoForFile(String fileId);
	
	public List<String> findBlock(String fileId, int blockIndex);

	public void registerFile(PoafsFile file, String fileName);

	public IEncrypter registerPeer();
	
	/**
	 * Register that this peer has received a file block.
	 * @param fileId The id of the file.
	 * @param index The index of the block.
	 */
	public void registerTransfer(String fileId, int index);
}
