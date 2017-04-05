package DAO;

import java.sql.Timestamp;

public class Message {
	private int id;
	private String originUser;
	private String destUser;
	private String message;
	private Timestamp time;
	public Message(int id, String originUser, String destUser, String message, Timestamp time) {
		this.id = id;
		this.originUser = originUser;
		this.destUser = destUser;
		this.message = message;
		this.time = time;
	}
	public String getDestUser() {
		return destUser;
	}
	public void setDestUser(String destUser) {
		this.destUser = destUser;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getOriginUser() {
		return originUser;
	}
	public void setOriginUser(String originUser) {
		this.originUser = originUser;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}
