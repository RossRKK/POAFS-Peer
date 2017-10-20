package poafs.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import poafs.lib.Reference;

public class FileManager {
	
	private HashMap<String, PoafsFile> availableFiles = new HashMap<String, PoafsFile>();

	/**
	 * Get a file from the disk.
	 * @param fileId The id of the file.
	 * @param index The index of the desired block.
	 * @return The relevant file block.
	 */
	public FileBlock getFileBlock(String fileId, int index) {
		if (!availableFiles.containsKey(fileId)) {
			try {
				loadFile(fileId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return availableFiles.get(fileId).getBlocks().get(index);
	}
	
	/**
	 * Utility method to read a line from the input.
	 * @return The line from the input
	 * @throws IOException
	 */
	private String readLine(InputStream in) throws IOException {
		//the input directly from the input stream
		int input;
		//the line that we want to return
		String line = "";
		
		//loop until the character is a new line
		while ((input = in.read()) != '\n') {
			//append the character to the line
			char character = new Character((char) input).charValue();
			line += character;
		}
		
		//tolerate carriage returns
		if (line.endsWith("\r")) {
			line = line.substring(0, line.length() - 1);
		}
		
		//return the line
		return line;
	}
	
	/**
	 * Load a file from the file system.
	 * @param fileId The id of the file.
	 * @throws IOException 
	 */
	private void loadFile(String fileId) throws IOException {
		File holdingFolder = new File(Reference.FILE_PATH + File.separator + fileId);
		
		if (holdingFolder.exists()) {
			PoafsFile file = new PoafsFile(fileId);
			int index = 0;
			for (String blockFilePath:holdingFolder.list()) {				
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(holdingFolder.getPath() + File.separator + blockFilePath));
				
				String originId = readLine(in);
				
				String lengthStr = readLine(in);
				int length = Integer.parseInt(lengthStr.split(":")[1]);
				
				FileBlock block;
				if (lengthStr.contains("key")) {
					//this is an encrypted block
					byte[] wrappedKey = new byte[length];
					in.read(wrappedKey);
					
					lengthStr = readLine(in);
					length = Integer.parseInt(lengthStr.split(":")[1]);
					byte[] content = new byte[length];
					in.read(content);
					
					block = new EncryptedFileBlock(originId, content, index, wrappedKey);
				} else {
					//this is an plain block
					byte[] content = new byte[length];
					in.read(content);
					
					block = new FileBlock(originId, content, index);
				}
				file.addBlock(block);
				
				index++;
			}
			
			availableFiles.put(fileId, file);
		}
	}

	/**
	 * Register a file with the file manager.
	 * @param file The file being registered.
	 */
	public void registerFile(PoafsFile file) {
		availableFiles.put(file.getId(), file);
	}

}
