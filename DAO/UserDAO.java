package DAO;

import java.sql.Timestamp;
import java.util.ArrayList;

public interface UserDAO {
	public void insert(User user);

	public void update(User user, Timestamp time);

	public User login(String user, String password);

	public ArrayList<User> selectAll();
}
