import static org.junit.Assert.assertEquals;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import poafs.auth.DummyAuthenticator;
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
	public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);      
        return keyPairGenerator.genKeyPair();
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
	public void testEncryption() throws NoSuchAlgorithmException {
		KeyPair keys = buildKeyPair();
		
		byte[] data = randomData(10);
		
		IEncrypter e = new RSAEncrypter(keys.getPublic());
		IDecrypter d = new RSADecrypter(keys.getPrivate());
		
		byte[] encrypted = e.encrypt(data);
		byte[] decrypted = d.decrypt(encrypted);
		
		for (int i = 0; i < data.length; i++) {
			assertEquals(data[i], decrypted[i]);
		}
	}
	
	/**
	 * Test that you can pass stuff into the dummy authenticator and get it back.
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testAutheticator() throws NoSuchAlgorithmException {
		KeyPair keys = buildKeyPair();
		
		byte[] data = randomData(10);
		
		DummyAuthenticator auth = new DummyAuthenticator(keys.getPublic());
		
		String testId = UUID.randomUUID().toString();
		auth.registerPeer(testId, keys.getPrivate());
		
		IEncrypter e = new RSAEncrypter(auth.getPublicKey());
		IDecrypter d = new RSADecrypter(auth.getPrivateKeyForPeer(testId));
		
		byte[] encrypted = e.encrypt(data);
		byte[] decrypted = d.decrypt(encrypted);
		
		for (int i = 0; i < data.length; i++) {
			assertEquals(data[i], decrypted[i]);
		}
	}
}
