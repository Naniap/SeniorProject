package DAO;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public class User implements Serializable {
	private static final long serialVersionUID = 3319852351272211903L;
	UserDAOImpl uDAO = new UserDAOImpl();
	private int id;
	public String name;
	private Timestamp lastLogin;
	private String password;
	private String email;
	private int onlineStatus;
	private ArrayList<User> friends;
	private int status; //whether or not user has accepted as friend, 0 = no, 1 = yes

	public User(int id, String name, String password, String email, Timestamp lastLogin, int onlineStatus) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.lastLogin = lastLogin;
		this.setEmail(email);
		this.setOnlineStatus(onlineStatus);
	}

	public User(String friendName, int onlineStatus, int id, int status) {
		this.name = friendName;
		this.onlineStatus = onlineStatus;
		this.id = id;
		this.status = status;
	}

	public User(String name, String password, String email) {
		this.name = name;
		this.password = password;
		this.setEmail(email);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		uDAO.update(this, lastLogin, false);
	}

	public Timestamp getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Timestamp lastLogin) {
		this.lastLogin = lastLogin;
		uDAO.update(this, lastLogin, true);
	}

	public String getUserName() {
		return name;
	}

	public void setUserName(String userName) {
		this.name = userName;
		uDAO.update(this, lastLogin, false);
	}

	public int getId() {
		return id;
	}

	public boolean nameMatches(String n) {
		return (this.name.equalsIgnoreCase(n));
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(int onlineStatus) {
		this.onlineStatus = onlineStatus;
		uDAO.update(this, lastLogin, false);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
