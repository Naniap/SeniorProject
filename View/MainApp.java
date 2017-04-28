package View;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import DAO.User;
import DAO.UserDAOImpl;
import Database.*;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MainApp {
	private JFrame frame;
	private JTextField txt_UName;
	private JPasswordField txt_PWord;
	private User user = null;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApp window = new MainApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 355, 227);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblLogin = new JLabel("Login:");
		lblLogin.setBounds(10, 48, 46, 14);
		frame.getContentPane().add(lblLogin);

		JLabel lblPassowrd = new JLabel("Password:");
		lblPassowrd.setBounds(10, 79, 67, 14);
		frame.getContentPane().add(lblPassowrd);

		txt_UName = new JTextField();
		txt_UName.setBounds(76, 45, 174, 20);
		frame.getContentPane().add(txt_UName);
		txt_UName.setColumns(10);

		txt_PWord = new JPasswordField();
		txt_PWord.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
		        if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
		        {
		          login();
		        }
			}
		});
		txt_PWord.setBounds(76, 76, 174, 20);
		frame.getContentPane().add(txt_PWord);
		txt_PWord.setColumns(10);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Database.closeConnection();
			}
		});
		JButton btn_Login = new JButton("Login");
		btn_Login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		btn_Login.setBounds(66, 128, 89, 23);
		frame.getContentPane().add(btn_Login);

		JButton btn_CreateAcct = new JButton("Create Account");
		btn_CreateAcct.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame createAcct = new CreateAccountForm(frame);
				createAcct.setVisible(true);
				frame.setVisible(false);
			}
		});
		btn_CreateAcct.setBounds(165, 128, 123, 23);
		frame.getContentPane().add(btn_CreateAcct);
	}
	private void login() {
		if (user != null) {
			System.out.println("User " + user.getUserName() + " is already logged in.");
			return;
		}
		UserDAOImpl UDAO = new UserDAOImpl();
		user = UDAO.login(txt_UName.getText(), Database.sha512_Encrpyt(new String(txt_PWord.getPassword()),
				new String(txt_UName.getText()).substring(1)));
		if (user != null) {
			/*if (user.getOnlineStatus() == Database.ONLINE) {
				System.out.println("User " + user.getUserName() + " is already logged in.");
				return;
			}*/
			System.out.println("Successfully logged in as: " + user.getUserName());
			try {
				FriendsList f = new FriendsList(user);
				f.setVisible(true);
			}
			catch (NullPointerException npe) {
				System.out.println("Unable to connect to login server.");
				JOptionPane.showMessageDialog(frame, "Unable to connect to the server.");
				user = null;
				return;
			}
			frame.dispose();
			// Launch a new window logging the user in.
		} else {
			System.out.println("Invalid password or username...");
			JOptionPane.showMessageDialog(frame, "Invalid password or username, please try again.");
		}
	}
}
