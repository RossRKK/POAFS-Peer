package poafs;

import poafs.auth.DummyAuthenticator;
import poafs.auth.IAuthenticator;
import poafs.cryto.IEncrypter;
import poafs.local.PropertiesManager;

public class Application {
	/**
	 * The authenticator being used by this peer.
	 */
	private static IAuthenticator auth;
	
	/**
	 * The encrypter to be used for all local files.
	 */
	private static IEncrypter localEncrypter;
	

	/**
	 * The application properties manager.
	 */
	private static PropertiesManager pm = new PropertiesManager();
	
	public static void main(String[] args) {
		pm.loadProperties();
		
		auth = new DummyAuthenticator();
		
		auth.authoriseUser();
	}
	
	public static IAuthenticator getAuth() {
		return auth;
	}
	
	public static IEncrypter getLocalEncrypter() {
		return localEncrypter;
	}
	
	public static PropertiesManager getPropertiesManager() {
		return pm;
	}
}
