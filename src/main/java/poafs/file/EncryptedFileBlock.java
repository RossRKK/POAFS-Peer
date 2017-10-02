package poafs.file;

public class EncryptedFileBlock extends FileBlock {
	
	public EncryptedFileBlock(String originPeerId, byte[] content, int index, byte[] wrappedKey) {
		super(originPeerId, content, index);
		this.wrappedKey = wrappedKey;
	}

	/**
	 * The wrapped AES key that was used to encrypt this block.
	 */
	private byte[] wrappedKey;
	
	public byte[] getWrappedKey() {
		return wrappedKey;
	}
	
	@Override
	protected String getHeaders() {
		return super.getHeaders() + "\n" + new String(wrappedKey);
	}
}
