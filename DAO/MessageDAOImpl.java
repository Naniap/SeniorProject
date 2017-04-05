package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import Database.Database;

public class MessageDAOImpl implements MessageDAO {
	Connection connection = null;
	@Override
	public void insert(String originUser, String destUser, String message) {
		connection = Database.getConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(
					"INSERT INTO messages (originUser, destinationUser, message, time) VALUES (?, ?, ?, ?)");
			preparedStatement.setString(1, originUser);
			preparedStatement.setString(2, destUser);
			preparedStatement.setString(3, message);
			preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public ArrayList<Message> retrieveMessages(String originUser, String destUser) {
		connection = Database.getConnection();
		try {
			ArrayList<Message> messages = new ArrayList<>();
			PreparedStatement pstmt = connection
					.prepareStatement("SELECT id, originUser, destinationUser, message, time FROM messages WHERE (originUser = ? AND destinationUser = ?) OR (originUser = ? AND destinationUser = ?) ORDER BY id ASC LIMIT 30");
			pstmt.setString(1, originUser);
			pstmt.setString(2, destUser);
			pstmt.setString(3, destUser);
			pstmt.setString(4, originUser);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String originName = rs.getString(2);
				String destName = rs.getString(3);
				String message = rs.getString(4);
				Timestamp time = rs.getTimestamp(5);
				Message m = new Message(id, originName, destName, message, time);
				messages.add(m);
			}
			return messages;
		} catch (SQLException e) {
		}
		return null;

	}

}
