package DAO;

import java.io.Serializable;
import Database.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class UserDAOImpl implements UserDAO, Serializable {
	private static final long serialVersionUID = 1L;
	Connection connection = null;

	@Override
	public void insert(User user) {
		connection = Database.getConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(
					"INSERT INTO login (username, password, lastlogin, email, onlinestatus) VALUES (?, ?, ?, ?, ?)");
			preparedStatement.setString(1, user.getUserName());
			preparedStatement.setString(2, user.getPassword());
			preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setString(4, user.getEmail());
			preparedStatement.setInt(5, 4);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void update(User user, Timestamp time, boolean updateTimestamp) {
		connection = Database.getConnection();
		try {
			PreparedStatement pstmt;
			if (updateTimestamp) {
				pstmt = connection.prepareStatement(
						"UPDATE login SET username = ?, password = ?, lastlogin = ?, onlinestatus = ? WHERE id = ?");
				pstmt.setString(1, user.getUserName());
				pstmt.setString(2, user.getPassword());
				pstmt.setTimestamp(3, user.getLastLogin());
				pstmt.setInt(4, user.getOnlineStatus());
				pstmt.setInt(5, user.getId());
			} else {
				pstmt = connection
						.prepareStatement("UPDATE login SET username = ?, password = ?, onlinestatus = ? WHERE id = ?");
				pstmt.setString(1, user.getUserName());
				pstmt.setString(2, user.getPassword());
				pstmt.setInt(3, user.getOnlineStatus());
				pstmt.setInt(4, user.getId());
			}
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	@Override
	public User login(String user, String password) {
		connection = Database.getConnection();
		try {
			System.out.println(password);
			PreparedStatement pstmt = connection.prepareStatement(
					"SELECT id, username, password, email, lastlogin, onlinestatus FROM login WHERE username = ? AND password = ?");
			pstmt.setString(1, user);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				return new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getTimestamp(5),
						rs.getInt(6));
			return null;
		} catch (SQLException e) {
			System.out.println(e);
		}
		return null;
	}

	@Override
	public ArrayList<User> selectAll() {
		connection = Database.getConnection();
		try {
			ArrayList<User> usrArr = new ArrayList<>();
			PreparedStatement pstmt = connection
					.prepareStatement("SELECT id, username, password, email, lastlogin, onlinestatus FROM login");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String password = rs.getString(3);
				String email = rs.getString(4);
				Timestamp lastLogin = rs.getTimestamp(5);
				int onlineStatus = rs.getInt(6);
				User usr = new User(id, name, password, email, lastLogin, onlineStatus);
				usrArr.add(usr);
			}
			return usrArr;
		} catch (SQLException e) {
		}
		return null;
	}

	@Override
	public User select(String user) {
		connection = Database.getConnection();
		try {
			User userObj = null;
			PreparedStatement pstmt = connection
					.prepareStatement("SELECT id, username, password, email, lastlogin, onlinestatus FROM login WHERE username = ?");
			pstmt.setString(1, user);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String password = rs.getString(3);
				String email = rs.getString(4);
				Timestamp lastLogin = rs.getTimestamp(5);
				int onlineStatus = rs.getInt(6);
				userObj = new User(id, name, password, email, lastLogin, onlineStatus);
			}
			return userObj;
		} catch (SQLException e) {
		}
		return null;
	}

}