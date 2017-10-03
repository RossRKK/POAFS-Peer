package poafs;

import poafs.auth.DummyAuthenticator;
import poafs.auth.IAuthenticator;
import poafs.cryto.IEncrypter;
import poafs.file.FileManager;
import poafs.lib.Reference;
import poafs.local.PropertiesManager;
import poafs.net.Server;

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
	 * The file system manager.
	 */
	private static FileManager fileManager = new FileManager();
	

	/**
	 * The application properties manager.
	 */
	private static PropertiesManager pm = new PropertiesManager();
	
	public static void main(String[] args) {
		pm.loadProperties();
		
		auth = new DummyAuthenticator();
		
		auth.authoriseUser();
		
		//start the local server
		new Thread(new Server(Reference.DEFAULT_PORT, fileManager)).start();
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
	
	public static FileManager getFileManager() {
		return fileManager;
	}
}
