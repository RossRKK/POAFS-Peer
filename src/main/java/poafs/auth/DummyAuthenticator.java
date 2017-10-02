package poafs.auth;

import java.util.HashMap;

import javax.crypto.SecretKey;

/**
 * A fake class that acts like an authetication server.
 * @author rossrkk
 *
 */
public class DummyAuthenticator implements IAuthenticator {
	
	private HashMap<String, SecretKey> keyRegister = new HashMap<String, SecretKey>();

	
	@Override
	public SecretKey getKeyForFile(String fileId) {
		return keyRegister.get(fileId);
	}

	@Override
	public boolean authoriseUser() {
		// TODO Auto-generated method stub
		return true;
	}
	
	//these methods shouldn't exist on an actual autheticator
	
	public void registerFile(String fileId, SecretKey key) {
		keyRegister.put(fileId, key);
	}
	
	public void unregisterFile(String fileId) {
		keyRegister.remove(fileId);
	}
}