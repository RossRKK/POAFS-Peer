package poafs.cryto;

public interface IEncrypter {
	/**
	 * Encrypt the content with this peers public key.
	 * @param input The input data.
	 * 
	 * @return The encrypted output data.
	 */
	public byte[] encrypt(byte[] input);
}
