package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import DAO.User;
import DAO.UserDAOImpl;

public class ChatServer {

	private ServerSocket server;
	private Socket clientConnection;
	private ArrayList<ClientConnectionHandler> connections;
	private ArrayList<User> users;
	private ArrayList<User> loggedInUsers;

	private int maxClients = 10;

	private int portNumber;

	public ChatServer() {
		server = null;
		clientConnection = null;
		this.portNumber = 5000;
		connections = new ArrayList<ClientConnectionHandler>();
		loggedInUsers = new ArrayList<User>();
	}

	public void listen() throws IOException {
		server = new ServerSocket(portNumber);
	}

	public void acceptConnection() throws IOException {
		clientConnection = server.accept();

		ClientConnectionHandler cch = new ClientConnectionHandler(clientConnection, this.users, this.loggedInUsers);

		if (checkActiveConnections() < maxClients) {
			connections.add(cch);
			cch.start();
			System.out.println("User accepted.");
		}
		for (ClientConnectionHandler connectionHandler : connections) {
			connectionHandler.forceRefresh();
		}
	}

	public void terminate() {
		try {
			server.close();
		} catch (IOException e) {
			System.out.println("Error terminating connection");
		}
	}

	public int checkActiveConnections() throws IOException {
		// Check how many threads are still active
		ClientConnectionHandler cch = null;
		int activeConnections = 0;

		for (int i = 0; i < connections.size(); i++) {
			cch = connections.get(i);
			if (cch.isConnected())
				activeConnections++;
		}
		return activeConnections;
	}


	public void getUsers() throws IOException {
		for (int i = 0; i < connections.size(); i++) {
			if (connections.get(i).isConnected())
				if (connections.get(i).hasNewUser()) {
					users.add(connections.get(i).getNewUser());
					connections.get(i).clearNewUser();
				}
		}
	}

	public void getLoggedInUsers() throws IOException {
		// TODO this might be buggy.......... i hate using .remove, i will
		// probably have to edit this.
		for (int i = 0; i < connections.size(); i++) {
			if (connections.get(i).isConnected()) {
				if ((!containsLoggedInUser(connections.get(i).getCurrentUser()))
						&& connections.get(i).getCurrentUser() != null)
					loggedInUsers.add(connections.get(i).getCurrentUser());
				if (connections.get(i).hasLogout()) {
					loggedInUsers.remove(loggedInUsers.indexOf(connections.get(i).getUserToLogOut()));
					connections.get(i).clearLogout();
				}
			}
		}
	}

	public void setLoggedInUsers() throws IOException {
		for (int i = 0; i < connections.size(); i++) {
			if (connections.get(i).isConnected())
				connections.get(i).setLoggedInUserList(loggedInUsers);
		}
	}

	public boolean containsLoggedInUser(User u) {
		if (loggedInUsers.isEmpty())
			return false;

		for (int x = 0; x < loggedInUsers.size(); x++) {
			if (u.nameMatches(loggedInUsers.get(x).name))
				return true;
		}
		return false;
	}

	public static void main(String[] args) {
		// BBServer BBS = new BBServer(Integer.parseInt(args[0]));
		ChatServer BBS = new ChatServer();
		System.out.println("Message Server Initialized");

		try {
			// Make the server listen on the given port
			BBS.listen();

			while (true) {
				// Wait until a client connects
				BBS.acceptConnection();

				// watch for new users
				BBS.getUsers();

				// watch for logged in users
				BBS.getLoggedInUsers();

				// update all clients' userList
				// BBS.setUsers();

				// update all clients' loggedInUserList
				BBS.setLoggedInUsers();
			}

		} catch (IOException e) {

			System.out.println("Unable to connect");
		} finally {
			BBS.terminate();
		}

	}

}
