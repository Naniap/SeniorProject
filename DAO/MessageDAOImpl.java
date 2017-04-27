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
	public void insert(Integer loginId, Integer friendId, String message) {
		connection = Database.getConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(
					"INSERT INTO messages (loginid, friendid, message, time) VALUES (?, ?, ?, ?)");
			preparedStatement.setInt(1, loginId);
			preparedStatement.setInt(2, friendId);
			preparedStatement.setString(3, message);
			preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public ArrayList<Message> retrieveMessages(Integer loginId, Integer friendId) {
		connection = Database.getConnection();
		try {
			String query = "SELECT" 
				   + " *"
				   + " FROM"
				   +     " ( SELECT " 
				   +         "messages.id,"
				   +             "loginid,"
				   +             "friendid,"
				   +             "message,"
				   +             "time,"
				   +             "targUser.username AS targUser,"
				   +             "origUser.username AS origUser "
				   +     "FROM "
				   +         "messages "
				   +     "INNER JOIN login origUser ON loginid = origUser.id "
				   +     "INNER JOIN login targUser ON friendid = targUser.id "
				   +     "WHERE "
				   +         "(loginid = ? AND friendid = ?) "
				   +             "OR (friendid = ? AND loginid = ?) "
				   +     "ORDER BY id DESC "
				   +     "LIMIT 30) sub "
				   + "ORDER BY id ASC";
			ArrayList<Message> messages = new ArrayList<>();
			PreparedStatement pstmt = connection
					.prepareStatement(query);
			pstmt.setInt(1, loginId);
			pstmt.setInt(2, friendId);
			pstmt.setInt(3, loginId);
			pstmt.setInt(4, friendId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String originName = rs.getString(7);
				String destName = rs.getString(6);
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
