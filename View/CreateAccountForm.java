package View;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import DAO.User;
import DAO.UserDAOImpl;
import Database.Database;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CreateAccountForm extends JFrame {

	private static final long serialVersionUID = 7997339360411526729L;
	private JPanel contentPane;
	private JTextField txt_User;
	private JTextField txt_Email;
	private JPasswordField txt_Confirm;
	private JPasswordField txt_Pass;
	private JFrame frame;
	/**
	 * Create the frame.
	 */
	public CreateAccountForm(JFrame parent) {
		frame = this;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 402, 251);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txt_User = new JTextField();
		txt_User.setColumns(10);
		txt_User.setBounds(119, 29, 188, 22);
		contentPane.add(txt_User);

		JLabel lblUserName = new JLabel("User Name:");
		lblUserName.setBounds(43, 33, 66, 14);
		contentPane.add(lblUserName);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(43, 69, 66, 14);
		contentPane.add(lblPassword);

		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setBounds(43, 134, 46, 14);
		contentPane.add(lblEmail);

		txt_Email = new JTextField();
		txt_Email.setColumns(10);
		txt_Email.setBounds(119, 130, 188, 22);
		contentPane.add(txt_Email);

		txt_Confirm = new JPasswordField();
		txt_Confirm.setColumns(10);
		txt_Confirm.setBounds(154, 98, 188, 22);
		contentPane.add(txt_Confirm);

		JLabel lblPassword_1 = new JLabel("Retype Password:");
		lblPassword_1.setBounds(43, 98, 112, 14);
		contentPane.add(lblPassword_1);

		JButton btn_Submit = new JButton("Submit");
		btn_Submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!txt_User.getText().matches("\\w{1,20}")) {
					JOptionPane.showMessageDialog(null, "Invalid username, username can only contain letters and numbers and have a maximum length of 20.");
					txt_User.grabFocus();
					return;
				}
				if (!Arrays.equals(txt_Confirm.getPassword(), txt_Pass.getPassword())) {
					JOptionPane.showMessageDialog(null, "Passwords are not the same, please re-enter.",
							"Password Verification Error", JOptionPane.ERROR_MESSAGE);
				} else {
					UserDAOImpl UDAO = new UserDAOImpl();
					UDAO.insert(new User(txt_User.getText(), Database.sha512_Encrpyt(new String(txt_Pass.getPassword()),
							new String(txt_User.getText()).substring(1)), txt_Email.getText()));
					JOptionPane.showMessageDialog(null, "Your account has been successfully created.");
					frame.setVisible(false);
					parent.setVisible(true);
				}
			}
		});
		btn_Submit.setBounds(253, 174, 89, 23);
		contentPane.add(btn_Submit);

		txt_Pass = new JPasswordField();
		txt_Pass.setColumns(10);
		txt_Pass.setBounds(119, 62, 188, 22);
		contentPane.add(txt_Pass);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.setVisible(false);
				parent.setVisible(true);
			}
		});
	}
}
