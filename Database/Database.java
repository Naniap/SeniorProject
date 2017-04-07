package Database;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import DAO.User;

/***
 * 
 * @author Mike
 * This database file would never exist in the real-world, rather would use an intermediary to interact with database server
 * rather than giving access to the database directly; since this is project isn't concerned with security necessarily, I've just made a database file
 * so that all the information can be grabbed from a central file.
 * 
 ***/
public final class Database {
	/*
	 * members are public because they're static and final, intended to be accessed from other classes, but not modified.
	 */
	public final static int ONLINE = 1;
	public final static int AWAY = 2;
	public final static int SHOWASOFFLINE = 3;
	public final static int OFFLINE = 4;
	
	private final static String dbName = "messagesystem"; 												//database name
	private final static String dbPassword = "Zxflk;S]2aAS34#(za)1A2xD5T2A3$6("; 						//database password
	private final static String dbLoginUser = "WSU";													//database login user
	private final static String dbAddress = "phantomelite.com";											//domain name or host of database server
	private final static String connectionString = "jdbc:mysql://" + dbAddress + ":3306/" + dbName;		//database connection string
	private static Connection connection = null;
	public Database() {
		
	}

	public static Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			if (connection == null) {
				connection = DriverManager.getConnection(connectionString, dbLoginUser, dbPassword);
				System.out.println("Connection to database successful!");
			}

		} catch (ClassNotFoundException e) {

			e.printStackTrace();

		} catch (SQLException e) {

			e.printStackTrace();

		}
		return connection;
	}
	public static void closeConnection() {
		try {
			if (connection != null) {
				connection.close();
				System.out.println("Connection closed.");
			}
		} catch (Exception e) {
		}
	}
	/*
	 * Function taken from stack overflow:
	 * http://stackoverflow.com/questions/33085493/hash-a-password-with-sha-512-
	 * in-java 
	 * just a function to encrypt in sha512, not the most secure, but better than nothing
	 */
	public static String sha512_Encrpyt(String passwordToHash, String salt) {
		String generatedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(salt.getBytes("UTF-8"));
			byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}
	/**
	 * By default these users are mutual friends, even if the other friend hasn't "accepted"
	 * @param currentUser
	 * @return
	 */
	public static ArrayList<User> selectFriends(User currentUser) {
		ArrayList<User> users = new ArrayList<>();
		PreparedStatement pstmt;
		Connection con = getConnection();
		try {
			pstmt = con.prepareStatement("SELECT username, onlinestatus, login.id, status FROM friendslist INNER JOIN login ON friendid = login.id WHERE loginid = ?");
			pstmt.setInt(1, currentUser.getId());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				users.add(new User(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}
	public static ArrayList<User> selectPendingFriends(User currentUser) {
		ArrayList<User> users = new ArrayList<>();
		PreparedStatement pstmt;
		Connection con = getConnection();
		try {
			pstmt = con.prepareStatement("SELECT username, onlinestatus, login.id, status FROM friendslist t_out INNER JOIN login ON loginid = login.id WHERE friendid = ? AND NOT EXISTS (SELECT 1 FROM friendslist t_in WHERE t_in.friendid = t_out.loginid AND t_in.loginid = t_out.friendid)");
			pstmt.setInt(1, currentUser.getId());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				users.add(new User(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}
	public static void addFriend(int originId, int friendId) {
		connection = Database.getConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(
					"INSERT INTO friendslist (loginid, friendid) VALUES (?, ?)");
			preparedStatement.setInt(1, originId);
			preparedStatement.setInt(2, friendId);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

/*
 * 
 *	  loginid: 3					 friendid: 1 
 *     (TestTwo)   is friends with 	   (Mike)
 *    friendid: 1   is friends with   loginid: 3
 *      (Mike)							(TestTwo)
 *      Mike is friends with Test and Mike (1 and 2)
*/