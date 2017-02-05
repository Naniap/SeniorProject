package Database;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
}
