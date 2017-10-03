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
	private Scanner fancyIn;
	
	public NetworkPeer(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void openConnection() {
		try {
			s = new Socket(host, port);
			
			out = new PrintWriter(s.getOutputStream());
			
			
			in = new BufferedInputStream(s.getInputStream());
			fancyIn = new Scanner(s.getInputStream());
			
			//print some headers
			out.println("POAFS Version 0.1");
			System.out.println("Peer: " + "POAFS Version 0.1");
			out.println(Application.getPropertiesManager().getPeerId());
			System.out.println("Peer: " + Application.getPropertiesManager().getPeerId());
			
			String versionDec = fancyIn.nextLine();
			System.out.println("Peer Recieve Version: " + versionDec);
			String peerId = fancyIn.nextLine();
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
		String response = fancyIn.nextLine();
		System.out.println("Peer Recieved Request: " + response);
		
		boolean isKey = response.contains("key");
		
		int length = Integer.parseInt(response.split(":")[1]);
		
		if (isKey) {
			byte[] wrappedKey = new byte[length];
			in.read(wrappedKey);
			fancyIn.nextLine();
			
			response = fancyIn.nextLine();
			
			length = Integer.parseInt(response.split(":")[1]);
			
			byte[] content = new byte[length];
			
			in.read(content);
			
			return new EncryptedFileBlock("origin-peer", content, index, wrappedKey);
		} else {
			byte[] content = new byte[length];
			in.read(content);
			
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
