import static org.junit.Assert.assertEquals;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Test;

import poafs.cryto.AESCipher;
import poafs.cryto.ICipher;
import poafs.cryto.IDecrypter;
import poafs.cryto.IEncrypter;
import poafs.cryto.RSADecrypter;
import poafs.cryto.RSAEncrypter;

public class Crypto {
	/**
	 * Generate a key pair.
	 * @return A key pair.
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPair buildRSAKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);      
        return keyPairGenerator.genKeyPair();
    }
	
	/**
	 * Generate a key pair.
	 * @return A key pair.
	 * @throws NoSuchAlgorithmException
	 */
	public static SecretKey buildAESKeyPair() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256); // for example
		return keyGen.generateKey();
    }
	
	/**
	 * Generate random data to run tests on.
	 * @param length The length of the random byte array.
	 * @return A random byte array.
	 */
	public static byte[] randomData(int length) {
		Random r = new Random();
		byte[] data = new byte[length];
		
		r.nextBytes(data);
		
		return data;
	}
	
	/**
	 * Test that if we encrypt something we can decrypt it.
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testAESEncryption() throws NoSuchAlgorithmException {
		SecretKey key = buildAESKeyPair();
		
		byte[] data = randomData(264201946);
		
		ICipher c = new AESCipher(key);
		
		byte[] encrypted = c.encrypt(data);
		byte[] decrypted = c.decrypt(encrypted);
		
		for (int i = 0; i < data.length; i++) {
			assertEquals(data[i], decrypted[i]);
		}
	}
	
	/**
	 * Test that if we encrypt something we can decrypt it.
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testRSAEncryption() throws NoSuchAlgorithmException {
		KeyPair keys = buildRSAKeyPair();
		
		byte[] data = randomData(245);
		
		IEncrypter e = new RSAEncrypter(keys.getPublic());
		IDecrypter d = new RSADecrypter(keys.getPrivate());
		
		byte[] encrypted = e.encrypt(data);
		byte[] decrypted = d.decrypt(encrypted);
		
		for (int i = 0; i < data.length; i++) {
			assertEquals(data[i], decrypted[i]);
		}
	}
}
