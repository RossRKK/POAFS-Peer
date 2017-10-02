import static org.junit.Assert.assertEquals;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.junit.Test;

import poafs.cryto.HybridDecrypter;
import poafs.cryto.HybridEncrypter;
import poafs.file.EncryptedFileBlock;
import poafs.file.FileBlock;

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
	
	@Test
	public void hybridTest() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		KeyPair keys = buildRSAKeyPair();
		
		byte[] data = randomData(1024);
		
		HybridEncrypter e = new HybridEncrypter(keys.getPublic());
		HybridDecrypter d = new HybridDecrypter(keys.getPrivate());
		
		FileBlock input = new FileBlock("test", data, 0);
		
		EncryptedFileBlock encrypted = e.encrypt(input);
		FileBlock decrypted = d.decrypt(encrypted);
		
		for (int i = 0; i < data.length; i++) {
			assertEquals(data[i], decrypted.getContent()[i]);
		}
	}
}
