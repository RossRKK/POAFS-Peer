package poafs.peer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import poafs.Application;
import poafs.file.EncryptedFileBlock;
import poafs.file.FileBlock;

/**
 * A peer that is somewhere else on the network.
 * @author rossrkk
 *
 */
public class NetworkPeer implements IPeer {

	private String host;
	
	private int port;
	
	private Socket s;
	
	private PrintWriter out;
	
	private BufferedInputStream in;
	
	public NetworkPeer(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	/**
	 * Utility method to read a line from the input.
	 * @return The line from the input
	 * @throws IOException
	 */
	private String readLine() throws IOException {
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
		
		//return the line
		return line;
	}

	@Override
	public void openConnection() {
		try {
			s = new Socket(host, port);
			
			out = new PrintWriter(s.getOutputStream());		
			
			in = new BufferedInputStream(s.getInputStream());
			
			//print some headers
			out.println("POAFS Version 0.1");
			System.out.println("Peer: " + "POAFS Version 0.1");
			out.println(Application.getPropertiesManager().getPeerId());
			System.out.println("Peer: " + Application.getPropertiesManager().getPeerId());
			
			String versionDec = readLine();
			System.out.println("Peer Recieve Version: " + versionDec);

			String peerId = readLine();
			System.out.println("Peer Recieved ID: " + peerId);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public FileBlock requestBlock(String fileId, int index) {
		String request = fileId + ":" + index;
		
		FileBlock block = null;
		
		out.println(request);
		System.out.println("Peer: " + request);
		
		out.flush();
		
		try {
			block = readResponse(index);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return block;
	}

	/**
	 * Read a block from an input stream.
	 * @return The required block.
	 * @throws IOException 
	 */
	private FileBlock readResponse(int index) throws IOException {
		String response = readLine();
		System.out.println("Peer Recieved Response: " + response);
		
		//figure out if this response contains a key we need to parse
		boolean isKey = response.contains("key");
		
		//determine the length of the recieved content (key or block)
		int length = Integer.parseInt(response.split(":")[1]);
		
		if (isKey) {
			//read in the wrapped key
			byte[] wrappedKey = new byte[length];
			in.read(wrappedKey);
			
			//read the info about the actual content
			response = readLine();
			
			System.out.println("Peer Recieved Response: " + response);
			
			//figure out the length of the content
			length = Integer.parseInt(response.split(":")[1]);
			
			//read in the conent
			byte[] content = new byte[length];
			in.read(content);
			
			//return the encrypted block
			return new EncryptedFileBlock("origin-peer", content, index, wrappedKey);
		} else {
			//read in the content of the block
			byte[] content = new byte[length];
			in.read(content);
			
			//return the relevant block
			return new FileBlock("origin-peer", content, index);
		}
	}


	@Override
	public void sendBlock(String fileId, FileBlock block) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}
}
