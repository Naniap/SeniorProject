package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;


public class ClientConnectionHandler extends Thread {
	private String userName;

	private Socket connection;
	private InputStream clientInput;
	private OutputStream clientOutput;
	private Scanner scanner;
	private OutputStreamWriter osw;
	private String option = "";

	public ClientConnectionHandler() {
	}

	public ClientConnectionHandler(Socket clientConnection) {
		connection = clientConnection;
		try {
			clientInput = connection.getInputStream();
			clientOutput = connection.getOutputStream();
			scanner = new Scanner(clientInput);
			osw = new OutputStreamWriter(clientOutput);
		} catch (IOException e) {
			System.out.println("Error reading/writing from/to client");
		}

	}

	@Override
	public void run() {
		// loop to continue asking options
		
		try {
			osw.write("What is your username?\r\n");
			osw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			if (!scanner.hasNextLine()) {
				return;
			}
			option = scanner.nextLine();
			System.out.println(option);
			if (option.startsWith("Chat message: ")) {
				int i = option.indexOf(',', 1 + option.indexOf(','));
				String message = option.substring(i+1);
				String targetUser = option.split(",")[1];
				String originUser = option.split(",")[0].split("Chat message: ")[1];
				ChatServer.sendMessageTo(originUser, targetUser, message);
				System.out.println("Chat message detected. User: " + originUser + " Target user: " + targetUser + " Message contents: " + message);
			}
			if (option.startsWith("Username: ")) {
				userName = option.split("Username: ")[1];
				System.out.println("Username bound to: " + userName);
			}
			if (option.startsWith("Logout")) {
				removeUser();
				for (ClientConnectionHandler c : ChatServer.getConnections())
					c.forceRefresh();
				break;
			}
			if (option.startsWith("Status changed.")) {
				for (ClientConnectionHandler c : ChatServer.getConnections())
					c.forceRefresh();
			}
			if (option.startsWith("UPDATE USERS: ")) {
				String originUser = option.split("UPDATE USERS: ")[1].split(",")[0].trim();
				String targetUser = option.split(",")[1].trim();
				for (ClientConnectionHandler c : ChatServer.getConnections()) {
					if (c.getUserName().equals(originUser) || c.getUserName().equals(targetUser)) {
						c.forceRefresh();
					}
				}
			}
		}

	}
	public void forceRefresh() {
		try {
			osw.write(".\r\n");
			osw.flush();
			osw.write("Force Refresh\r\n");
			osw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendMessage(String message, String originUser, String targetUser) {
		try {
			osw.write("Message from: " + originUser + "," + targetUser + "," + message + "\r\n");
			osw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void removeUser() {
		System.out.println("User: " + userName + " successfully removed from arraylist.");
		ChatServer.getConnections().remove(this);
	}
	public String getUserName() {
		return userName;
	}
}