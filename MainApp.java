import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.awt.event.ActionEvent;

public class MainApp {
	//private Connection connection = Database.getConnection();
	private JFrame frame;
	private JTextField txt_UName;
	private JTextField txt_PWord;

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
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblLogin = new JLabel("Login:");
		lblLogin.setBounds(29, 48, 46, 14);
		frame.getContentPane().add(lblLogin);
		
		JLabel lblPassowrd = new JLabel("Password:");
		lblPassowrd.setBounds(29, 79, 67, 14);
		frame.getContentPane().add(lblPassowrd);
		
		txt_UName = new JTextField();
		txt_UName.setBounds(106, 45, 174, 20);
		frame.getContentPane().add(txt_UName);
		txt_UName.setColumns(10);
		
		txt_PWord = new JTextField();
		txt_PWord.setBounds(106, 76, 174, 20);
		frame.getContentPane().add(txt_PWord);
		txt_PWord.setColumns(10);
		
		JButton btn_Login = new JButton("Login");
		btn_Login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btn_Login.setBounds(106, 128, 89, 23);
		frame.getContentPane().add(btn_Login);
	}
}
