package poafs.auth;

import java.security.PrivateKey;

/**
 * An interface that represents the auth service for the network.
 * @author rossrkk
 *
 */
public interface IAuthenticator {
	/**
	 * Get the private key for a given peer.
	 * @param peerId The id of the peer who provided the block we want to decrypt.
	 * @return The private key for the given peer.
	 */
	public PrivateKey getKeyForPeer(String peerId);
	
	/**
	 * Authorise the user TODO
	 * @return Whether the user is authorised.
	 */
	public boolean authoriseUser();
}
