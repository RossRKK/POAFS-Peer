package poafs.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

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
	
	protected String getHeaders() {
		return originPeerId;
	}

	public void save(String path) throws IOException {
		PrintWriter out = new PrintWriter(new FileOutputStream(path + File.separator + index));
		
		out.println(getHeaders());
		
		out.println();
		
		out.println(new String(content));
		
		out.close();
	}
}
