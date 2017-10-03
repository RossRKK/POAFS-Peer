package poafs.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * The internal server of a peer.
 * @author rossrkk
 *
 */
public class Server implements Runnable {
	
	/**
	 * The port that this server listens on.
	 */
	private int port;
	
	/**
	 * All of the open requests.
	 */
	private List<RequestHandler> rhs = new ArrayList<RequestHandler>();

	public Server(int port) {
		this.port = port;
	}
	
	@Override
	public void run() {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(port);
			
			while (!ss.isClosed()) {
				Socket s = ss.accept();
				
				RequestHandler rh = new RequestHandler(s);
				
				rhs.add(rh);
				
				new Thread(rh).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
