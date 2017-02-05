package DAO;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable {
	private static final long serialVersionUID = 3319852351272211903L;
	UserDAOImpl uDAO = new UserDAOImpl();
	private int id;
	public String name;
	private Timestamp lastLogin;
	private String password;
	private String email;

	public User(int id, String name, String password, String email, Timestamp lastLogin) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.lastLogin = lastLogin;
		this.setEmail(email);
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
		uDAO.update(this, lastLogin);
	}

	public Timestamp getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Timestamp lastLogin) {
		this.lastLogin = lastLogin;
		uDAO.update(this, lastLogin);
	}

	public String getUserName() {
		return name;
	}

	public void setUserName(String userName) {
		this.name = userName;
		uDAO.update(this, lastLogin);
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

}
