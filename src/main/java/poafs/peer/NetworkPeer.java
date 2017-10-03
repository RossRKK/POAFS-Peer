package poafs.peer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

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
	
	private BufferedOutputStream out;
	
	private BufferedInputStream in;
	
	public NetworkPeer(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void openConnection() {
		try {
			s = new Socket(host, port);
			
			out = new BufferedOutputStream(s.getOutputStream());
			
			in = new BufferedInputStream(s.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public FileBlock requestBlock(String fileId, int index) {
		String request = fileId + ":" + index + "\n";
		
		FileBlock block = null;
		
		try {
			out.write(request.getBytes());
			
			block = readBlock();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return block;
	}

	/**
	 * Read a block from an input stream.
	 * @return The required block.
	 */
	private FileBlock readBlock() {
		// TODO Auto-generated method stub
		return null;
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
