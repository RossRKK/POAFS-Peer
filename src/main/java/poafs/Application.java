package poafs;

import java.util.UUID;

public class Application {
	
	/**
	 * The id of this peer.
	 */
	private static String peerId;
	
	public static void main(String[] args) {
		//get a new peerId
		peerId = UUID.randomUUID().toString();
		//TODO reload the old one if there is one
	}
	
	public static String getPeerId() {
		return peerId;
	}
}
