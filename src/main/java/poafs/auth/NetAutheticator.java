package poafs.auth;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.SSLSocketFactory;

import poafs.Application;
import poafs.cryto.HybridDecrypter;
import poafs.cryto.IDecrypter;

public class NetAutheticator implements IAuthenticator {
	
	private Socket s;
	
	private BufferedInputStream in;
	
	private PrintWriter out;
	
	public NetAutheticator(String host, int port) throws IOException {
		//open an ssl connection to the server
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        s = factory.createSocket(host, port);
        
        //create the input and output streams
        in = new BufferedInputStream(s.getInputStream());
        out = new PrintWriter(s.getOutputStream());
        
        out.println("POAFS Version 0.1");
		out.println(Application.getPropertiesManager().getPeerId());
	}
	
	/**
	 * Utility method to read a line from the input.
	 * @return The line from the input
	 * @throws IOException
	 */
	private String readLine() throws IOException {
		//the input directly from the input stream
		int input;
		//the line that we want to return
		String line = "";
		
		//loop until the character is a new line
		while ((input = in.read()) != '\n') {
			//append the character to the line
			char character = new Character((char) input).charValue();
			line += character;
		}
		
		//tolerate carriage returns
		if (line.endsWith("\r")) {
			line = line.substring(0, line.length() - 1);
		}
		
		//return the line
		return line;
	}

	@Override
	public IDecrypter getKeyForPeer(String peerId) {
		try {
			out.println("private-key " + peerId);
			
			String response = readLine();
			
			int length = Integer.parseInt(response.split(":")[1]);
			
			byte[] key = new byte[length];
			in.read(key);
			
			return new HybridDecrypter(KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(key)));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the hostname this peer can be accessed on.
	 * @param peerId The id of the peer.
	 * @return The hostname of the peer.
	 */
	public InetSocketAddress getHostForPeer(String peerId) {
		try {
			out.println("host " + peerId);
			
			String[] tuple = readLine().split(":");
			
			String host = tuple[0];
			int port = Integer.parseInt(tuple[1]);
			
			return new InetSocketAddress(host, port);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public boolean authoriseUser() {
		// TODO Auto-generated method stub
		return true;
	}

}
