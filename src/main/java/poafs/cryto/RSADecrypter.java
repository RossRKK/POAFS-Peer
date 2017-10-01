package poafs.cryto;

import java.security.PrivateKey;

import javax.crypto.Cipher;

public class RSADecrypter implements IDecrypter {

	/**
	 * The key used to decrypt incoming data.
	 */
	private PrivateKey key;
	
	public RSADecrypter(PrivateKey key) {
		this.key = key;
	}
	
	/**
	 * Decrypt the input data with the incoming scheme.
	 */
	@Override
	public byte[] decrypt(byte[] input) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");  
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        
	        return cipher.doFinal(input);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
