package poafs;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.crypto.NoSuchPaddingException;

import poafs.file.FileMeta;
import poafs.local.PropertiesManager;

public class Application {
	
	
	/**
	 * System input scanner.
	 */
	private static Scanner sc = new Scanner(System.in);
	
	/**
	 * The network this peer is connected to.
	 */
	private static Network net;

	/**
	 * The application properties manager.
	 */
	private static PropertiesManager pm = new PropertiesManager();
	
	public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		pm.loadProperties();
		
		net = new Network(args[0], Integer.parseInt(args[1]));
		
		ui();
	}
	
	private static void ui() {
		
		System.out.println("User Name: ");
		String user = sc.nextLine();
		
		System.out.println("Password: ");
		String pass = sc.nextLine();
		
		boolean authorised = net.login(user, pass);
		
		if (authorised) {
			System.out.println("Logged in.");
		
			boolean exit = false;
			
			while (!exit) {
				String command = sc.nextLine();
				switch (command) {
					case "ls":
						listFiles(net.listFiles());
						break;
					case "load":
						printFile(net.fetchFile(sc.nextLine()));
						break;
					case "register-file":
					try {
						net.registerFile(sc.nextLine(), sc.nextLine());
					} catch (Exception e) {
						e.printStackTrace();
					}
						break;
					case "exit":
					case "quit":
						exit = true;
						break;
					default:
						System.out.println("Unrecognised Command");
				}
				
			}
		} else {
			System.out.println("Error authenticating");
		}
		
		sc.close();
	}
	
	private static void printFile(InputStream fileStream) {
		int in;
		try {
			List<Byte> contents = new ArrayList<Byte>();
			in = fileStream.read();
		
			while (in != -1) {
				contents.add((byte) in);
				
				in = fileStream.read();
			}
			
			System.out.println("File Read");
			
			byte[] bytes = new byte[contents.size()];
			for (int i = 0; i < contents.size(); i++) {
				bytes[i] = contents.get(i);
			}
			
			System.out.println(new String(bytes));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print out the list of files.
	 * @param files The files to be listed.
	 */
	private static void listFiles(List<FileMeta> files) {		
		for (FileMeta f:files) {
			System.out.println(f.getFileName() + " " + f.getId());
		}
	}
	
	public static PropertiesManager getPropertiesManager() {
		return pm;
	}
}
