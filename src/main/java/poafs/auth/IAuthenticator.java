package poafs.auth;

import javax.crypto.SecretKey;

/**
 * An interface that represents the auth service for the network.
 * @author rossrkk
 *
 */
public interface IAuthenticator {
	/**
	 * Get the private key for a given file.
	 * @param fileId The id of the file we want to decrypt.
	 * @return The private key for the given file.
	 */
	public SecretKey getKeyForFile(String fileId);
	
	/**
	 * Authorise the user TODO
	 * @return Whether the user is authorised.
	 */
	public boolean authoriseUser();
}
