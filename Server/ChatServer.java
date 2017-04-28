package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {

	private ServerSocket server;
	private Socket clientConnection;
	private static ArrayList<ClientConnectionHandler> connections;


	private int portNumber;

	public ChatServer() {
		server = null;
		clientConnection = null;
		this.portNumber = 5000;
		connections = new ArrayList<ClientConnectionHandler>();
	}

	public void listen() throws IOException {
		server = new ServerSocket(portNumber);
	}
	public static void sendMessageTo(String originUser, String targetUser, String message) {
		for (ClientConnectionHandler c : connections) {
			if (c.getUserName().equals(targetUser)) {
				c.sendMessage(message, targetUser, originUser);
			}
		}
	}
	/**
	 * Currently no connection threshold, most likely won't impose one as this will likely not be large scale,
	 * easy to impose later if necessary.
	 * @throws IOException
	 */
	public void acceptConnection() throws IOException {
		clientConnection = server.accept();
		ClientConnectionHandler cch = new ClientConnectionHandler(clientConnection);
		connections.add(cch);
		cch.start();
		System.out.println("User accepted.");
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

	public static void main(String[] args) {
		ChatServer CS = new ChatServer();
		System.out.println("Message Server Initialized");

		try {
			// Make the server listen on the given port
			CS.listen();

			while (true) {
				// Wait until a client connects
				CS.acceptConnection();
			}

		} catch (IOException e) {

			System.out.println("Unable to connect");
		} finally {
			CS.terminate();
		}

	}
	public static ArrayList<ClientConnectionHandler> getConnections() {
		return connections;
	}

}
