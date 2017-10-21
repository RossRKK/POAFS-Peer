package poafs.cryto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import poafs.file.EncryptedFileBlock;
import poafs.file.FileBlock;
import poafs.lib.Reference;

public class HybridDecrypter implements IDecrypter{
	/**
	 * The rsa cipher object that is used.
	 */
	private Cipher rsa;
	
	public HybridDecrypter(PrivateKey rsaKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		rsa = Cipher.getInstance(Reference.RSA_CIPHER);
		
		rsa.init(Cipher.UNWRAP_MODE, rsaKey);
	}
	
	/**
	 * Unwrap the aes key.
	 * @return The aes key.
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	private SecretKey unwrapKey(byte[] wrappedKey) throws InvalidKeyException, NoSuchAlgorithmException {
		return (SecretKey)rsa.unwrap(wrappedKey, Reference.AES_CIPHER, Cipher.SECRET_KEY);
	}

	/**
	 * Decrypt the given file block.
	 * @param block The file block to be decrypted.
	 * @return The un encrypted file block.
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	@Override
	public FileBlock decrypt(EncryptedFileBlock block) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException  {
		SecretKey aesKey = unwrapKey(block.getWrappedKey());
		
		Cipher aes = Cipher.getInstance(Reference.AES_CIPHER);
		
		aes.init(Cipher.DECRYPT_MODE, aesKey);
		
		byte[] encryptedContent = aes.doFinal(block.getContent());
		
		return new FileBlock(block.getOriginPeerId(),encryptedContent, block.getIndex());
	}
}
