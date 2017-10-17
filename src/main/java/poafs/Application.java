package poafs;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.NoSuchPaddingException;

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
						net.listFiles();
						break;
					case "load":
						net.fetchFile(sc.nextLine());
						break;
					case "register-file":
					try {
						net.registerFile(sc.nextLine(), sc.nextLine());
					} catch (Exception e) {
						e.printStackTrace();
					}
						break;
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
	public static PropertiesManager getPropertiesManager() {
		return pm;
	}
}
