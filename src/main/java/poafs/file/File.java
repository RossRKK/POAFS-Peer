package poafs.file;

import java.util.List;

/**
 * A file that represents a file as it was download from the network.
 * @author rossrkk
 *
 */
public class File {
	/**
	 * A list of all the blocks in this file.
	 */
	private List<FileBlock> blocks;
	
	
	/**
	 * Add a block that was just loaded to this 
	 * @param block The block to be added.
	 */
	public void addBlock(FileBlock block) {
		blocks.add(block.getIndex(), block);
	}
	
	/* Getters and Setters */
	
	public int getNumBlocks() {
		return blocks.size();
	}
	
	public List<FileBlock> getBlocks() {
		return blocks;
	}
}
