package poafs.auth;

import java.net.InetAddress;
import java.net.InetSocketAddress;
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
	
	/**
	 * A register containing keys.
	 */
	private HashMap<String, PrivateKey> keyRegister = new HashMap<String, PrivateKey>();
	
	/**
	 * A register containing host names.
	 */
	private HashMap<String, InetSocketAddress> hostRegister = new HashMap<String, InetSocketAddress>();
	

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
	
	public void registerPeer(String peerId, PrivateKey key, InetSocketAddress host) {
		keyRegister.put(peerId, key);
		hostRegister.put(peerId, host);
	}
	
	public void unregisterPeer(String peerId) {
		keyRegister.remove(peerId);
	}

	@Override
	public InetSocketAddress getHostForPeer(String peerId) {
		return hostRegister.get(peerId);
	}

	
}