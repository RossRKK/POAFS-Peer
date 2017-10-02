import static org.junit.Assert.assertEquals;

import java.io.IOException;
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

import poafs.Application;
import poafs.cryto.HybridDecrypter;
import poafs.cryto.HybridEncrypter;
import poafs.file.EncryptedFileBlock;
import poafs.file.FileBlock;
import poafs.file.PoafsFile;
import poafs.peer.DummyPeer;
import poafs.peer.IPeer;

public class Tests {
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
	
	
	@Test
	public void peerTest() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		KeyPair keys = buildRSAKeyPair();
		
		IPeer p = new DummyPeer("test-peer", keys.getPublic(), keys.getPrivate());
		
		byte[] data = randomData(1024);
		
		FileBlock input = new FileBlock("test-block", data, 0);
		
		p.sendBlock("test-file", input);
		
		FileBlock returned = p.requestBlock("test-file", 0);
		
		for (int i = 0; i < data.length; i++) {
			assertEquals(data[i], returned.getContent()[i]);
		}
	}
	
	@Test
	public void saveTest() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		byte[] data = "Hello, World!".getBytes();
		
		FileBlock input = new FileBlock("test", data, 0);
		
		PoafsFile file = new PoafsFile("normal");
		
		file.addBlock(input);
		
		file.saveFile();
	}
	
	@Test
	public void saveEncryptedTest() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		KeyPair keys = buildRSAKeyPair();
		
		byte[] data = "Hello, World!".getBytes();
		
		HybridEncrypter e = new HybridEncrypter(keys.getPublic());
		
		FileBlock input = new FileBlock("test", data, 0);
		
		EncryptedFileBlock encrypted = e.encrypt(input);
		
		PoafsFile file = new PoafsFile("encrypted");
		
		file.addBlock(encrypted);
		
		file.saveFile();
	}
}
