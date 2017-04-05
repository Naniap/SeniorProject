package DAO;

import java.util.ArrayList;

public interface MessageDAO {
	public void insert(String originUser, String destUser, String message);
	public ArrayList<Message> retrieveMessages(String originUser, String destUser);
}
