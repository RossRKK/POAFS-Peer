package poafs.file;

import java.util.HashMap;

public class FileManager {
	
	private HashMap<String, PoafsFile> availableFiles = new HashMap<String, PoafsFile>();

	/**
	 * Get a file from the disk.
	 * @param fileId The id of the file.
	 * @param index The index of the desired block.
	 * @return The relevant file block.
	 */
	public FileBlock getFileBlock(String fileId, int index) {
		return availableFiles.get(fileId).getBlocks().get(index);
	}
	
	/**
	 * Register a file with the file manager.
	 * @param file The file being registered.
	 */
	public void registerFile(PoafsFile file) {
		availableFiles.put(file.getId(), file);
	}

}
