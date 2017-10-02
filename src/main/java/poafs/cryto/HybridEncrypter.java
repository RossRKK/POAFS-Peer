package poafs.cryto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import poafs.Application;
import poafs.file.EncryptedFileBlock;
import poafs.file.FileBlock;

public class HybridEncrypter implements IEncrypter {
	/**
	 * The rsa cipher object that is used.
	 */
	private Cipher rsa;
	
	public HybridEncrypter(PublicKey rsaKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		rsa = Cipher.getInstance("RSA");
		
		rsa.init(Cipher.WRAP_MODE, rsaKey);
	}
	
	/**
	 * Generate a new aes key.
	 * @return The new aes key.
	 */
	private SecretKey genAesKey() {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(256);
			return keyGen.generateKey();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Encrypt a file block.
	 * @param block The block to be encrypted.
	 * @return The resulting encrypted block.
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	@Override
	public EncryptedFileBlock encrypt(FileBlock block) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		SecretKey aesKey = genAesKey();
		
		Cipher aes = Cipher.getInstance("AES");
		
		aes.init(Cipher.ENCRYPT_MODE, aesKey);
		
		byte[] encryptedContent = aes.doFinal(block.getContent());
		
		byte[] wrappedKey = rsa.wrap(aesKey);
		
		return new EncryptedFileBlock(Application.getPeerId(), encryptedContent, block.getIndex(), wrappedKey);
	}
}
