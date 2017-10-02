package poafs.peer;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import javax.crypto.NoSuchPaddingException;

import poafs.cryto.HybridDecrypter;
import poafs.cryto.HybridEncrypter;
import poafs.cryto.IDecrypter;
import poafs.cryto.IEncrypter;
import poafs.file.EncryptedFileBlock;
import poafs.file.File;
import poafs.file.FileBlock;

public class DummyPeer implements IPeer {
	
	private IDecrypter d;
	
	private IEncrypter e;
	
	private HashMap<String, File> files = new HashMap<String, File>();

	/**
	 * Create a new dummy peer.
	 * @param publicKey Remote peers public key.
	 * @param privateKey Matching private key.
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	public DummyPeer(PublicKey publicKey, PrivateKey privateKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		d = new HybridDecrypter(privateKey);
		e = new HybridEncrypter(publicKey);
	}
	
	@Override
	public void openConnection() {
		/*There isn't a connection*/
	}

	@Override
	public FileBlock requestBlock(String fileId, int index) {
		try {
			return d.decrypt((EncryptedFileBlock) files.get(fileId).getBlocks().get(index));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void sendBlock(String fileId, FileBlock block) {
		//this should really encrypt the block using the local public key and send it to the remote peer
		//it would then be decrypted and encrypted with their local key
		//this skips that encrypt decrypt encrypt and just does the last encrypt
		try {
			//encrypt the block as if we were the remote peer
			EncryptedFileBlock eBlock = e.encrypt(block);
			
			if (files.containsKey(fileId)) {
				files.get(fileId).addBlock(block);
			} else {
				File file = new File(fileId);
				
				file.addBlock(eBlock);
				
				files.put(fileId, file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
