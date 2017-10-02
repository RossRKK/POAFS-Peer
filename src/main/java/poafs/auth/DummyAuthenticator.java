package poafs.auth;

import java.security.PrivateKey;
import java.util.HashMap;

import poafs.cryto.HybridDecrypter;
import poafs.cryto.IDecrypter;

/**
 * A fake class that acts like an authetication server.
 * @author rossrkk
 *
 */
public class DummyAuthenticator implements IAuthenticator {
	
	private HashMap<String, PrivateKey> keyRegister = new HashMap<String, PrivateKey>();

	@Override
	public IDecrypter getKeyForPeer(String peerId) {
		try {
			return new HybridDecrypter(keyRegister.get(peerId));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean authoriseUser() {
		// TODO Auto-generated method stub
		return true;
	}
	
	//these methods shouldn't exist on an actual autheticator
	
	public void registerPeer(String peerId, PrivateKey key) {
		keyRegister.put(peerId, key);
	}
	
	public void unregisterPeer(String peerId) {
		keyRegister.remove(peerId);
	}

	
}