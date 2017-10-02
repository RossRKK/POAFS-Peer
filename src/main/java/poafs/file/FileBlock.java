package poafs.file;

/**
 * A class that represents a single block of a file from a remote peer.
 * @author rossrkk
 *
 */
public class FileBlock {

	public FileBlock(String originPeerId, byte[] content, int index) {
		super();
		this.originPeerId = originPeerId;
		this.content = content;
		this.index = index;
	}

	/**
	 * The unique id of the peer this block originated from.
	 */
	private String originPeerId;
	
	/**
	 * The content of the block.
	 */
	private byte[] content;
	
	/**
	 * The position this block has in the file.
	 */
	private int index;

	public String getOriginPeerId() {
		return originPeerId;
	}

	public byte[] getContent() {
		return content;
	}

	public int getIndex() {
		return index;
	}
}
