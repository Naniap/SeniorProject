package DAO;

import java.util.ArrayList;

public interface MessageDAO {
	public void insert(Integer loginId, Integer friendId, String message);
	public ArrayList<Message> retrieveMessages(Integer loginId, Integer friendId);
}
