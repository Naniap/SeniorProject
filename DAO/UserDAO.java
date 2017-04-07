package DAO;

import java.sql.Timestamp;
import java.util.ArrayList;

public interface UserDAO {
	public void insert(User user);

	public void update(User user, Timestamp time, boolean updateTimeStamp);

	public User login(String user, String password);

	public ArrayList<User> selectAll();
	
	public User select(String user); // usernames are unique, so we can use them as a primary key of sorts.
	
	public void acceptRequest(int originUser, int targetUser);
}
