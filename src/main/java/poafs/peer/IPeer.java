package poafs.peer;

import poafs.file.FileBlock;

public interface IPeer {
	/**
	 * Handshake with another peer.
	 */
	public void openConnection();
	
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
