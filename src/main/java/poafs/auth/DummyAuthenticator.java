package poafs.auth;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

/**
 * A fake class that acts like an authetication server.
 * @author rossrkk
 *
 */
public class DummyAuthenticator implements IAuthenticator {
	
	private HashMap<String, PrivateKey> keyRegister = new HashMap<String, PrivateKey>();

	/**
	 * The public key for this client.
	 */
	private PublicKey publicKey;
	
	public DummyAuthenticator(PublicKey key) {
		this.publicKey = key;
	}
	
	@Override
	public PublicKey getPublicKey() {
		return publicKey;
	}

	@Override
	public PrivateKey getPrivateKeyForPeer(String peerId) {
		return keyRegister.get(peerId);
	}

	@Override
	public boolean authoriseUser() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void registerPeer(String peerId, PrivateKey key) {
		keyRegister.put(peerId, key);
	}
	
	public void unregisterPeer(String peerId) {
		keyRegister.remove(peerId);
	}

}