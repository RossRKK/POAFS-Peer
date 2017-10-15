package poafs.peer;

import java.io.IOException;
import java.net.UnknownHostException;

import poafs.file.FileBlock;

public interface IPeer {
	/**
	 * Handshake with another peer.
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public void openConnection() throws UnknownHostException, IOException;
	
	/**
	 * Request a block from a file, the resulting block should be automatically decrypted.
	 * @param fileId The id of the file being requested.
	 * @param index The index of the block being requested.
	 */
	FileBlock requestBlock(String fileId, int index);

	/**
	 * Send a block to this peer.
	 * @param fileId
	 * @param block
	 */
	void sendBlock(String fileId, FileBlock block);
	
	/**
	 * Get this peers Id.
	 * @return The peer's id.
	 */
	String getId();
}
