package poafs.net;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import poafs.Application;
import poafs.file.EncryptedFileBlock;
import poafs.file.FileManager;

public class RequestHandler implements Runnable {
	
	FileManager fm;
	/**
	 * The socket that the request is coming from.
	 */
	private Socket sock;
	
	BufferedOutputStream out;
	
	Scanner in;
	
	public RequestHandler(Socket socket, FileManager fm) throws SocketException {
		sock = socket;
		this.fm = fm;
		sock.setKeepAlive(true);
		
		try {
			out = new BufferedOutputStream(sock.getOutputStream());
			in = new Scanner(sock.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			
			//print some headers
			out.write("POAFS Version 0.1\n".getBytes());
			System.out.println("Server: " + "POAFS Version 0.1");
			out.write((Application.getPropertiesManager().getPeerId() + "\n").getBytes());
			System.out.println("Server: " + Application.getPropertiesManager().getPeerId());
			
			out.flush();
			
			String versionDec = in.nextLine();
			String peerId = in.nextLine();
			
			//read all requests for file segments
			while (in.hasNextLine()) {
				String request = in.nextLine();
				System.out.println("Server Recieved: " + request);
				
				//requests should take the form <file id>:<block index>
				String[] info = request.split(":");
				String fileId = info[0];
				int blockIndex = Integer.parseInt(info[1]);
				
				EncryptedFileBlock block = (EncryptedFileBlock) fm.getFileBlock(fileId, blockIndex);
				
				println("key length:" + block.getWrappedKey().length);
				System.out.println("Server: " + "key length:" + block.getWrappedKey().length);
				
				out.write(block.getWrappedKey());
				System.out.println("Server: " + new String(block.getWrappedKey()));
				
				println("block length:" + block.getContent().length);
				System.out.println("Server: " + "block length:" + block.getContent().length);
				
				out.write(block.getContent());
				System.out.println("Server: " + new String(block.getContent()));
				
				out.flush();
			}
			
			in.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void println(String str) throws IOException {
		out.write((str + "\r\n").getBytes());
		
		out.flush();
	}

}
