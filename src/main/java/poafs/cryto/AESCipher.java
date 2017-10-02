package poafs.cryto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class AESCipher implements ICipher {

	/**
	 * The key that this encrypter uses to encrypt the data.
	 */
	private SecretKey key;
	
	public AESCipher(SecretKey key) {
		this.key = key;
	}

	@Override
	public byte[] encrypt(byte[] input) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			
			cipher.init(Cipher.ENCRYPT_MODE, key);
			
			return cipher.doFinal(input);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public byte[] decrypt(byte[] input) {
		try {
			Cipher cipher = Cipher.getInstance("AES");  
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        
	        return cipher.doFinal(input);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
