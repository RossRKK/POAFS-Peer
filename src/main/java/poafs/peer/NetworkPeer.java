package poafs.peer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
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
	
	private Scanner sc;
	
	private String id;

	public NetworkPeer(String id, InetSocketAddress addr) {
		this.host = addr.getHostName();
		this.port = addr.getPort();
		this.id = id;
	}
	
	/**
	 * Utility method to read a line from the input.
	 * @return The line from the input
	 * @throws IOException
	 */
	/*private synchronized String readLine() throws IOException {
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
	}*/

	@Override
	public synchronized void openConnection() throws UnknownHostException, IOException {
		s = new Socket(host, port);
		
		out = new PrintWriter(s.getOutputStream());		
		
		//in = new BufferedInputStream(s.getInputStream());
		sc = new Scanner(s.getInputStream());
		
		//print some headers
		out.println("POAFS Version 0.1");
		out.println(Application.getPropertiesManager().getPeerId());
		
		String versionDec = sc.nextLine();

		String peerId = sc.nextLine();

	}

	/**
	 * Request a block from the connected peer.
	 * @param fileId The id of the file you want.
	 * @param index The index of the block you want.
	 * 
	 * @return The relevant block.
	 */
	@Override
	public synchronized FileBlock requestBlock(String fileId, int index) {
		String request = fileId + ":" + index;
		
		FileBlock block = null;
		
		out.println(request);
		
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
	private synchronized FileBlock readResponse(int index) throws IOException {
		
		String response = sc.nextLine();
		
		//figure out if this response contains a key we need to parse
		boolean isKey = response.contains("key");
		
		//determine the length of the recieved content (key or block)
		int length = Integer.parseInt(response.split(":")[1]);
		
		if (isKey) {
			//read in the wrapped key
			//byte[] wrappedKey = new byte[length];
			//in.read(wrappedKey);
			
			String wrappedKey64 = sc.nextLine();
			byte[] wrappedKey = Base64.getDecoder().decode(wrappedKey64);
			
			//read the info about the actual content
			response = sc.nextLine();
			
			
			//figure out the length of the content
			length = Integer.parseInt(response.split(":")[1]);
			
			//read in the conent
			//byte[] content = new byte[length];
			//in.read(content);
			
			String content64 = sc.nextLine();
			byte[] content = Base64.getDecoder().decode(content64);
			
			//return the encrypted block
			return new EncryptedFileBlock(id, content, index, wrappedKey);
		} else {
			//read in the content of the block
			//byte[] content = new byte[length];
			//in.read(content);
			
			String content64 = sc.nextLine();
			byte[] content = Base64.getDecoder().decode(content64);
			
			//return the relevant block
			return new FileBlock(id, content, index);
		}
	}


	@Override
	public synchronized void sendBlock(String fileId, FileBlock block) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized String getId() {
		// TODO Auto-generated method stub
		return null;
	}
}
