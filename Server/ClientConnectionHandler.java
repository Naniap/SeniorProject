package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import com.mysql.jdbc.Messages;

import DAO.User;
import DAO.UserDAOImpl;

// TODO this class is a stub; Dan said he had a cch written so this is the placeholder.
// for my BBServer to work we need these 3 methods (including the constructor)

public class ClientConnectionHandler extends Thread {

	private User newUser;
	private String userName;

	public boolean hasMessage;
	public boolean hasNewUser;
	public boolean hasLogout;

	private User currentUser;
	private User tempUser;
	private User userToLogOut;
	private boolean isLoggedIn;

	private ArrayList<User> loggedInUserList;

	private Socket connection;
	private InputStream clientInput;
	private OutputStream clientOutput;
	private Scanner scanner;
	private OutputStreamWriter osw;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String option = "";

	public ClientConnectionHandler() {
	}

	public ClientConnectionHandler(Socket clientConnection, ArrayList<User> uL,	ArrayList<User> lIU) {
		connection = clientConnection;
		this.loggedInUserList = lIU;
		this.currentUser = null;

		this.hasMessage = false;
		this.isLoggedIn = false;
		this.hasNewUser = false;

		try {
			clientInput = connection.getInputStream();
			clientOutput = connection.getOutputStream();
			scanner = new Scanner(clientInput);
			osw = new OutputStreamWriter(clientOutput);
			oos = getObjectOutputStream();
		} catch (IOException e) {
			System.out.println("Error reading/writing from/to client");
		}

	}

	public ObjectInputStream getObjectInputStream() {
		if (ois == null) {
			try {
				ois = new ObjectInputStream(connection.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ois;
	}

	public ObjectOutputStream getObjectOutputStream() {
		if (oos == null) {
			try {
				oos = new ObjectOutputStream(connection.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return oos;
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
		while (!option.equalsIgnoreCase("exit")) {
			if (!scanner.hasNextLine()) {
				return;
			}
			option = scanner.nextLine();
			System.out.println(option);
			if (option.contains("Chat message: ")) {
				String message = option.split(",")[2];
				String targetUser = option.split(",")[1];
				String originUser = option.split(",")[0].split("Chat message: ")[1];
				ChatServer.sendMessageTo(originUser, targetUser, message);
				System.out.println("Chat message detected. User: " + originUser + " Target user: " + targetUser + " Message contents: " + message);
			}
			if (option.contains("Username: ")) {
				userName = option.split("Username: ")[1];
				System.out.println("Username bound to: " + userName);
			}
			if (option.contains("Logout")) {
				removeUser();
				for (ClientConnectionHandler c : ChatServer.getConnections())
					c.forceRefresh();
			}
			if (option.contains("Status changed.")) {
				for (ClientConnectionHandler c : ChatServer.getConnections())
					c.forceRefresh();
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
