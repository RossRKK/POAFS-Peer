package poafs.net;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import poafs.Application;
import poafs.file.EncryptedFileBlock;
import poafs.file.FileManager;

public class RequestHandler implements Runnable {
	
	FileManager fm = Application.getFileManager();

	/**
	 * The socket that the request is coming from.
	 */
	private Socket sock;
	
	public RequestHandler(Socket socket) {
		sock = socket;
	}
	
	@Override
	public void run() {
		try {
			BufferedOutputStream out = new BufferedOutputStream(sock.getOutputStream());
			Scanner in = new Scanner(sock.getInputStream());
			
			//print some headers
			out.write("POAFS Version 0.1\n".getBytes());
			out.write((Application.getPropertiesManager().getPeerId() + "\n").getBytes());
			
			/*String versionDec = in.nextLine();
			String peerId = in.nextLine();*/
			
			//read all requests for file segments
			while (sock.isConnected()) {
				String request = in.nextLine();
				
				//requests should take the form <file id>:<block index>
				String[] info = request.split(":");
				String fileId = info[0];
				int blockIndex = Integer.parseInt(info[1]);
				
				EncryptedFileBlock block = fm.getFileBlock(fileId, blockIndex);
				
				out.write(block.getWrappedKey());
				
				out.write("\n".getBytes());
				
				out.write(block.getContent());
			}
			
			in.close();
			out.close();
			
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

}
