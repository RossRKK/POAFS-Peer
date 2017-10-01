package poafs.cryto;

public interface IDecrypter {
	/**
	 * Encrypt the content with this peers public key.
	 * @param input The encrypted input data.
	 * 
	 * @return The decrypted output data.
	 */
	public byte[] decrypt(byte[] input);
}
