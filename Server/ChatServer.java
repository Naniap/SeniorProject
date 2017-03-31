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
	private static ArrayList<ClientConnectionHandler> connections;
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
	public static void sendMessageTo(String originUser, String targetUser, String message) {
		for (ClientConnectionHandler c : connections) {
			System.out.println(targetUser);
			System.out.println(c.getUserName());
			if (c.getUserName().equals(targetUser)) {
				System.out.println("Found target user!!" );
				c.sendMessage(message, targetUser, originUser);
			}
		}
	}
	/*public static void removeUser(String user) {
		System.out.println(connections.size());
		if (connections == null)
			return;
		for (ClientConnectionHandler c : connections ) {
			System.out.println("User: " + c.getName());
			//if (c.getUserName().equals(user)) {
			//	connections.remove(c);
			//}
		}
	}*/
	public void acceptConnection() throws IOException {
		clientConnection = server.accept();

		ClientConnectionHandler cch = new ClientConnectionHandler(clientConnection, this.users, this.loggedInUsers);

		//if (checkActiveConnections() < maxClients) {
			connections.add(cch);
			cch.start();
			System.out.println("User accepted.");
		//}
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

	/*public int checkActiveConnections() throws IOException {
		// Check how many threads are still active
		ClientConnectionHandler cch = null;
		int activeConnections = 0;

		for (int i = 0; i < connections.size(); i++) {
			cch = connections.get(i);
			if (cch.isConnected())
				activeConnections++;
		}
		return activeConnections;
	}*/
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
			}

		} catch (IOException e) {

			System.out.println("Unable to connect");
		} finally {
			BBS.terminate();
		}

	}
	public static ArrayList<ClientConnectionHandler> getConnections() {
		return connections;
	}

}
