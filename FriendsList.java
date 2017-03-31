import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import DAO.User;
import DAO.UserDAOImpl;
import Database.Database;
import Server.ChatServer;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
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
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.event.ItemEvent;

public class FriendsList extends JFrame{
	private ArrayList<String> chatSessions = new ArrayList<>();
	private ArrayList<ChatWindow> chatWindows = new ArrayList<>();
	private JPanel contentPane;
	private JTable table;
	private FriendsList friendsList;
	private Socket socket;
	private ArrayList<User> friends;
	private DefaultTableModel dtm;
	private Thread inputThread;
	private OutputStreamWriter osw;
	User user;

	/**
	 * Create the frame.
	 */
	public FriendsList(User user) {
		/*
		 * Connect to the server
		 */
        InputStream serverInput = null;
        OutputStream serverOutput = null;
        osw = null;
        
		
        try
        {
            socket = new Socket("localhost", 5000);
            serverOutput = socket.getOutputStream();
            serverInput = socket.getInputStream();
            osw = new OutputStreamWriter(serverOutput);
            
        }
        catch (IOException e)
        {
            System.out.println("error connecting to Server");
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
				System.out.println("Logout pressed.");
				user.setOnlineStatus(Database.OFFLINE);
			}
		});
		mnSettings.add(mntmLogout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		table = new JTable();
		table.setBounds(0, 87, 264, 472);
		contentPane.add(table);
		friends = Database.selectFriends(user);
		dtm = new DefaultTableModel(0, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent me) {
		        JTable table =(JTable) me.getSource();
		        Point p = me.getPoint();
		        int row = table.rowAtPoint(p);
		        if (me.getClickCount() == 2) {
		        	Object o = table.getModel().getValueAt(row, 0);
		            UserDAOImpl UDAO = new UserDAOImpl();
		            User targetUser = UDAO.select(o.toString());
		            if (chatSessions.contains(targetUser.getUserName())) {
		            	System.out.println("User already has chat window open.");
		            	return;
		            }
		            if (socket == null)
		            	return;
		            ChatWindow chatSession = new ChatWindow(socket, user, targetUser, friendsList);
		            if (chatSession != null) {
		            	chatSessions.add(targetUser.getUserName());
		            	chatWindows.add(chatSession);
		            	chatSession.setVisible(true);
		            }
		        }
		    }
		});
		String header[] = new String[] { "Friends" };
		dtm.setColumnIdentifiers(header);
		//updateList();
		Choice choice = new Choice();
		choice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				switch (arg0.getItem().toString()) {
				case "Online":
					user.setOnlineStatus(Database.ONLINE);
					break;
				case "Away":
					user.setOnlineStatus(Database.AWAY);
					break;
				case "Show as offline":
					user.setOnlineStatus(Database.SHOWASOFFLINE);
					break;
				}
			}
		});
		choice.add("Online");
		choice.add("Away");
		choice.add("Show as offline");
		choice.setBounds(10, 50, 87, 20);
		contentPane.add(choice);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				user.setOnlineStatus(Database.OFFLINE);
				Database.closeConnection();
				ChatServer.removeUser(user.getUserName());
			}
		});
		final Scanner scan = new Scanner(serverInput);
		inputThread = new Thread() {
			public void run() {
				while (true) {
					String message = scan.nextLine();
					System.out.println(message);
					if (message.equalsIgnoreCase("Force Refresh")) {
						updateList();
					}
					if (message.equalsIgnoreCase("What is your username?")) {
						userName();
					}
				}
			}
		};
		inputThread.start();
	}
	public ArrayList<String> getChatSession () {
		return chatSessions;
	}
	public void updateList() {
		friends = Database.selectFriends(user);
		int rowCount = dtm.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
		    dtm.removeRow(i);
		}
		for (User u : friends) {
			if (u.getOnlineStatus() <= Database.AWAY) // user is AWAY or ONLINE
														// are the only ways to
														// see if the user is
														// online
				dtm.addRow(new Object[] { u.getUserName() });
		}
		dtm.addRow(new Object[] { "----------------------------" });
		dtm.addRow(new Object[] { "Offline Friends" });
		dtm.addRow(new Object[] { "----------------------------" });
		for (User u : friends) {
			if (u.getOnlineStatus() > Database.AWAY) {
				dtm.addRow(new Object[] { u.getUserName() });
			}
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
}
