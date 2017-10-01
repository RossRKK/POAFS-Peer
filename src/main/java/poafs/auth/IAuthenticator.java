package poafs.auth;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * An interface that represents the auth service for the network.
 * @author rossrkk
 *
 */
public interface IAuthenticator {
	/**
	 * Get the public key for this peer.
	 * @return The public key for the peer.
	 */
	public PublicKey getPublicKey();
	
	/**
	 * Get the private key for a given peer.
	 * @param fileId The id of the peer we want to decrypt.
	 * @return The private key for the given peer.
	 */
	public PrivateKey getPrivateKeyForPeer(String peerId);
	
	/**
	 * Authorise the user TODO
	 * @return Whether the user is authorised.
	 */
	public boolean authoriseUser();
}
