package View;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import DAO.Message;
import DAO.MessageDAOImpl;
import DAO.User;
import DAO.UserDAOImpl;
import Database.Database;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Choice;
import java.awt.Point;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.awt.event.ItemEvent;
import java.awt.BorderLayout;

public class FriendsList extends JFrame{
	private static final long serialVersionUID = -2401808601296366940L;
	private static final boolean DEBUG = false;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss");
	private ArrayList<String> chatSessions = new ArrayList<>();
	private ArrayList<ChatWindow> chatWindows = new ArrayList<>();
	private JPanel contentPane;
	private JTable table;
	private FriendsList friendsList;
	private Socket socket;
	private ArrayList<User> friends;
	private ArrayList<User> pendingFriends;
	private DefaultTableModel dtm;
	private Thread inputThread;
	private OutputStreamWriter osw;
	private User user;
	private JFrame frame;
	private UserDAOImpl uDAO;

	/**
	 * Create the frame.
	 */
	public FriendsList(User user) {
		/*
		 * Connect to the server
		 */		            
		uDAO = new UserDAOImpl();
		frame = this;
        InputStream serverInput = null;
        OutputStream serverOutput = null;
        osw = null;
        
        try
        {
            socket = new Socket("phantomelite.com", 5000);
            serverOutput = socket.getOutputStream();
            serverInput = socket.getInputStream();
            osw = new OutputStreamWriter(serverOutput);
    		user.setOnlineStatus(Database.ONLINE);
    		user.setLastLogin(new Timestamp(System.currentTimeMillis()));
            
        }
        catch (IOException e)
        {
            System.out.println("Error connecting to Server");
        }
        
		/*
		 * End connect
		 */
		friendsList = this;
		this.user = user;
		setTitle(user.getUserName() + " Friend's List");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 280, 619);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);

		JMenuItem mntmLogout = new JMenuItem("Logout");
		mntmLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(frame, "Are you sure you wish to logout?", "Logout confirmation", JOptionPane.YES_NO_OPTION)) 
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		});
		mnSettings.add(mntmLogout);
		
		JMenuItem mntmAddFriend = new JMenuItem("Add Friend");
		mntmAddFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String friend = JOptionPane.showInputDialog("Enter friend's name");
				User targetFriend = uDAO.select(friend);
				if (targetFriend == null)
					JOptionPane.showMessageDialog(frame, "That user does not exist!");
				else {
					if (friends.size() == 0) {
						JOptionPane.showMessageDialog(frame, "You have sent a friend request to " + targetFriend.getUserName() + ". When they accept you will see them as online.");
						Database.addFriend(user.getId(), targetFriend.getId());
						updateUsers(user, targetFriend);
						return;
					}
		            if (checkFriendAlready(friends, targetFriend)) {
						JOptionPane.showMessageDialog(frame, "You are already friends with that person!");
		            	return;
		            }
					JOptionPane.showMessageDialog(frame, "You have sent a friend request to " + targetFriend.getUserName() + ". When they accept you will see them as online.");
					Database.addFriend(user.getId(), targetFriend.getId());
					updateUsers(user, targetFriend);
				}
			}
		});
		mnSettings.add(mntmAddFriend);
		Choice choice = new Choice();
		menuBar.add(choice);
		choice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				switch (arg0.getItem().toString()) {
				case "Online":
					user.setOnlineStatus(Database.ONLINE);
					try {
						osw.write("Status changed.\r\n");
						osw.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case "Away":
					user.setOnlineStatus(Database.AWAY);
					break;
				case "Show as offline":
					user.setOnlineStatus(Database.SHOWASOFFLINE);
					try {
						osw.write("Status changed.\r\n");
						osw.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		});
		choice.add("Online");
		choice.add("Away");
		choice.add("Show as offline");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		friends = Database.selectFriends(user);
		dtm = new DefaultTableModel(0, 0) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1109174466769474675L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		String header[] = new String[] { "Friends" };
		dtm.setColumnIdentifiers(header);
		
				table = new JTable();
				contentPane.add(table, BorderLayout.CENTER);
				table.addMouseListener(new MouseAdapter() {
				    public void mousePressed(MouseEvent me) {
				        JTable table =(JTable) me.getSource();
				        Point p = me.getPoint();
				        int row = table.rowAtPoint(p);
				        if(me.getButton() == MouseEvent.BUTTON3) {
				        	Object o = table.getModel().getValueAt(row, 0);
				            User targetUser = uDAO.select(o.toString());
				            if (targetUser == null) //sanity check if user clicks on an invalid user
				            	return;
				            if (checkFriendAlready(friends, targetUser))
				            	return;
				        	if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(frame, "Would you like to add this user as a friend?", "Add user?", JOptionPane.OK_CANCEL_OPTION)) {
				        		uDAO.acceptRequest(user.getId(), targetUser.getId());
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								updateUsers(user, targetUser);
				        	}
				        }
				        if (me.getClickCount() == 2) {
				        	Object o = table.getModel().getValueAt(row, 0);
				            User targetUser = uDAO.select(o.toString());
				            if (targetUser == null) //sanity check if user clicks on an invalid user
				            	return;
				            if (!checkFriendAlready(friends, targetUser))
				            	return;
				            if (chatSessions.contains(targetUser.getUserName())) {
				            	if (DEBUG)
				            		System.out.println("[" + sdf.format(new Date()) + "] " + "User already has chat window open.");
				            	return;
				            }
				            if (socket == null)
				            	return;
				            ChatWindow chatSession = new ChatWindow(socket, user, targetUser, friendsList);
				            if (chatSession != null) {
				            	MessageDAOImpl mDAO = new MessageDAOImpl();
				            	ArrayList<Message> messages = mDAO.retrieveMessages(user.getId(), targetUser.getId());
				            	if (messages != null) 
				            		chatSession.setMessages(messages);
				            	chatSessions.add(targetUser.getUserName());
				            	chatWindows.add(chatSession);
				            	chatSession.setVisible(true);
				            }
				        }
				    }
				});
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				logout();
			}
		});
		final Scanner scan = new Scanner(serverInput);
		inputThread = new Thread() {
			public void run() {
				while (true) {
					String message = scan.nextLine();
	            	if (DEBUG)
	            		System.out.println("[" + sdf.format(new Date()) + "] " +  "Friendslist receieved: " + message);
					if (message.equalsIgnoreCase("Force Refresh")) {
						updateList();
					}
					if (message.startsWith("What is your username?")) {
						userName();
					}
					if (message.contains("Message from: ")) {
						boolean sentMessage = false;
						String messageTo = message.split("Message from: ")[1].split(",")[1];
						int i = message.indexOf(',', 1 + message.indexOf(','));
						String messageContents = message.substring(i+1);
						for (ChatWindow c : chatWindows) {
							if (c.getTargetUser().equals(messageTo)) {
								c.receiveMessage(messageTo, messageContents);
								sentMessage = true;
							}
						}
						if (!sentMessage) {
				            UserDAOImpl UDAO = new UserDAOImpl();
				            User targetUser = UDAO.select(messageTo);
				            if (DEBUG)
				            	System.out.println("[" + sdf.format(new Date()) + "] " + targetUser.getUserName() + "\n" + user.getUserName());
				            ChatWindow chatSession = new ChatWindow(socket, user, targetUser, friendsList);
				            if (chatSession != null) {
				            	chatSessions.add(targetUser.getUserName());
				            	chatWindows.add(chatSession);
				            	MessageDAOImpl mDAO = new MessageDAOImpl();
				            	ArrayList<Message> messages = mDAO.retrieveMessages(user.getId(), targetUser.getId());
				            	if (messages != null) 
				            		chatSession.setMessages(messages);
				            	chatSession.setVisible(true);
				            }
						}
					}
				}
			}
		};
		inputThread.start();
	}
	public ArrayList<String> getChatSession () {
		return chatSessions;
	}
	public ArrayList<ChatWindow> getChatWindow () {
		return chatWindows;
	}
	public void updateList() {
		friends = Database.selectFriends(user);
		pendingFriends = Database.selectPendingFriends(user);
		int rowCount = dtm.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
		    dtm.removeRow(i);
		}
		dtm.addRow(new Object[] { "Online Friends" });
		for (User u : friends) {
			if (u.getOnlineStatus() <= Database.AWAY && u.getStatus() != 0) // user is AWAY or ONLINE
														// are the only ways to
														// see if the user is
														// online
				dtm.addRow(new Object[] { u.getUserName() });
		}
		dtm.addRow(new Object[] { "Offline Friends" });
		for (User u : friends) {
			if (u.getOnlineStatus() > Database.AWAY) {
				if (u.getStatus() != 0)
					dtm.addRow(new Object[] { u.getUserName() });
			}
		}
		dtm.addRow(new Object[] { "Pending Friends" });
		for (User u : pendingFriends) {
			if (u.getStatus() == 0)
				dtm.addRow(new Object[] { u.getUserName() });
		}
		table.setModel(dtm);
	}
	public void userName() {
		try {
			osw.write("Username: " + user.getUserName() + "\r\n");
			osw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void logout() {
		try {
			osw.write("Logout\r\n");
			osw.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		user.setOnlineStatus(Database.OFFLINE);
		Database.closeConnection();
	}
	private boolean checkFriendAlready(ArrayList<User> friends, User targetUser) {
		boolean foundUser = false;
        for (User u : friends) {
            if (u.getUserName().equals(targetUser.getUserName()) && u.getStatus() == 1) {
            	foundUser = true;
            	break;
            }
        }
        return foundUser;
	}
	private void updateUsers(User user, User targetFriend) {
		try {
			osw.write("UPDATE USERS: " + user.getUserName() + "," + targetFriend.getUserName() + "\r\n");
			osw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
