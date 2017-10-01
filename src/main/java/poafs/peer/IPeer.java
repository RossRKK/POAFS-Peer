package poafs.peer;

public interface IPeer {
	/**
	 * Handshake with another peer.
	 */
	public void openConnection();
	
	/**
	 * Request a segment from a file.
	 */
	public byte[] requestSegment(String fileId, int segment);
}
