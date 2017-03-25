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
		try {
			osw.write("Welcome to BBServer Console.\r\n");
			osw.write("This is the development, pre-GUI configuration for testing\r\n");

			osw.flush();
			// loop to continue asking options
			while (!option.equalsIgnoreCase("exit")) {
				displayOptionMenu();
				if (!scanner.hasNextLine()) {
					return;
				}
				option = scanner.nextLine();

				//processOption(option);
			}

		} catch (IOException e) {
			System.out.println("Error reading/writing from/to client IN RUN");
			e.printStackTrace();
		}

	}
	public void forceRefresh() {
		try {
			osw.write("Force Refresh\r\n");
			osw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean isConnected() throws IOException {
		boolean stillConnected = true;
		stillConnected = isAlive();
		return stillConnected;
	}

	public boolean hasMessage() {
		return this.hasMessage;
	}


	public User getNewUser() {
		return this.newUser;
	}


	public void clearNewUser() {
		this.newUser = null;
		this.hasNewUser = false;
	}

	public boolean hasNewUser() {
		return this.hasNewUser;
	}

	public boolean containsLoggedInUser(User u) {

		System.out.println(loggedInUserList.size());

		for (int x = 0; x < loggedInUserList.size(); x++) {
			if (u.nameMatches(loggedInUserList.get(x).name))
				return true;
		}
		return false;
	}

	public void viewActiveUsersOption() throws IOException {
		osw.write("LOGGED IN USERS:\r\n");
		osw.flush();
		for (int x = 0; x < loggedInUserList.size(); x++) {
			osw.write(loggedInUserList.get(x).name + "\r\n");
			// osw.write(loggedInUserList.get(x).password + "\r\n\r\n");

		}
	}

	public void exitOption() throws IOException {
		logUserOut();
		osw.write("Goodbye.\r\n");
		osw.flush();
	}
	// ...end menu options

	public void invalidOption() throws IOException {
		osw.write("\r\n Invalid entry.\r\n");
		osw.flush();
	}

	public int getLoggedInUserListSize() {
		return loggedInUserList.size();
	}

	public void setLoggedInUserList(ArrayList<User> list) {
		this.loggedInUserList = list;
	}

	public User getCurrentUser() {
		return this.currentUser;
	}

	public boolean hasLogout() {
		return hasLogout;
	}

	public void clearLogout() {
		hasLogout = false;
		userToLogOut = null;
	}

	public User getUserToLogOut() {
		return userToLogOut;
	}

	public void logUserOut() throws IOException {
		userToLogOut = currentUser;
		hasLogout = true;
		isLoggedIn = false;
		loggedInUserList.remove(loggedInUserList.indexOf(currentUser));

		//currentUser = null;
	}

	public void displayOptionMenu() throws IOException {
		osw.write("\r\n\r\nPlease enter an option:\r\n");
		osw.write("1. Sign in\r\n");
		osw.write("2. Sign up\r\n");
		osw.write("3. Sign out\r\n");
		osw.write("4. Post Message\r\n");
		osw.write("5. View recent messages\r\n");
		osw.write("6. View all messages\r\n");
		osw.write("7. Search messages by author\r\n");
		osw.write("8. Search messages by topic\r\n");

		// test options
		osw.write("9. VIEW ALL USERS\r\n");
		osw.write("0. VIEW ACTIVE USERS\r\n");

		osw.write("...Or type exit to quit.\r\n");

		osw.flush();
	}

	/*public void processOption(String o) throws IOException {

		o = eliminateSpaces(o);
		System.out.println("Server received: " + o);
		if (o.equals("1") || o.equalsIgnoreCase("signin")) {
			signInOption();
		} else if (o.equals("2") || o.equalsIgnoreCase("signup")) {
			signUpOption();
		} else if (o.equals("3") || o.equalsIgnoreCase("signout")) {
			signOutOption();
		} else if (o.equals("4") || o.equalsIgnoreCase("postmessage") || o.equalsIgnoreCase("post")) {
			postMessageOption();
		} else if (o.equals("5") || o.equalsIgnoreCase("viewrecentmessages") || o.equalsIgnoreCase("viewrecent")) {
			viewRecentMessagesOption();
		} else if (o.equals("6") || o.equalsIgnoreCase("viewallmessages") || o.equalsIgnoreCase("viewall")) {
			viewAllMessagesOption();
		} else if (o.equals("7") || o.equalsIgnoreCase("searchbyauthor") || o.equalsIgnoreCase("searchauthor")) {
			searchByAuthorOption();
		} else if (o.equals("8") || o.equalsIgnoreCase("searchbytopic") || o.equalsIgnoreCase("searchtopic")) {
			searchByTopicOption();
		} else if (o.equals("getarray")) {
			oos.reset();
			oos.writeObject(messageList);
		}
		else if (o.equals("refreshdata")) {
			oos.reset();
			MessageDAOImpl mDAO = new MessageDAOImpl();
			messageList = mDAO.selectAll();
			osw.write("Database Query Select All Messages.\r\n");
			osw.flush();
			oos.writeObject(messageList);
		}

		// test options
		else if (o.equals("9")) {
			viewAllUsersOption();
		} else if (o.equals("0")) {
			viewActiveUsersOption();
		}

		// exit option
		else if (o.equalsIgnoreCase("exit")) {
			exitOption();
		} else {
			invalidOption();
		}
	}*/

	public String eliminateSpaces(String s) {
		return s.replaceAll(" ", "");
	}

}
