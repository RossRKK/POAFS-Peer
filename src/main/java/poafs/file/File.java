package poafs.file;

import java.util.ArrayList;
import java.util.List;

/**
 * A file that represents a file as it was download from the network.
 * @author rossrkk
 *
 */
public class File {
	
	private String id;
	
	public File(String id) {
		this.id = id;
	}
	
	/**
	 * A list of all the blocks in this file.
	 */
	private List<FileBlock> blocks = new ArrayList<FileBlock>();
	
	
	/**
	 * Add a block that was just loaded to this 
	 * @param block The block to be added.
	 */
	public void addBlock(FileBlock block) {
		blocks.add(block.getIndex(), block);
	}
	
	/* Getters and Setters */
	
	public String getId() {
		return id;
	}
	
	public int getNumBlocks() {
		return blocks.size();
	}
	
	public List<FileBlock> getBlocks() {
		return blocks;
	}
}
