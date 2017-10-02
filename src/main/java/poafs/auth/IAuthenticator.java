package poafs.auth;

import poafs.cryto.IDecrypter;

/**
 * An interface that represents the auth service for the network.
 * @author rossrkk
 *
 */
public interface IAuthenticator {
	/**
	 * Get the decrypter for a given peer.
	 * @param peerId The id of the peer who provided the block we want to decrypt.
	 * @return The decrypter that can decrypt files from the given peer.
	 */
	public IDecrypter getKeyForPeer(String peerId);
	
	/**
	 * Authorise the user TODO
	 * @return Whether the user is authorised.
	 */
	public boolean authoriseUser();
}
