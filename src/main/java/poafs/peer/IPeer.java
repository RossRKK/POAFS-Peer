package poafs.peer;

import poafs.file.FileBlock;

public interface IPeer {
	/**
	 * Handshake with another peer.
	 */
	public void openConnection();
	
	/**
	 * Request a block from a file.
	 */
	public FileBlock requestBlock(String fileId, int index);
}
