package poafs.cryto;

import java.security.PublicKey;

import javax.crypto.Cipher;

public class RSAEncrypter implements IEncrypter {
	
	/**
	 * The key that this encrypter uses to encrypt the data.
	 */
	private PublicKey key;
	
	public RSAEncrypter(PublicKey key) {
		this.key = key;
	}

	@Override
	public byte[] encrypt(byte[] input) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			
			cipher.init(Cipher.ENCRYPT_MODE, key);
			
			return cipher.doFinal(input);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
