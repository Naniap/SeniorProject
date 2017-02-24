import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import DAO.User;
import Database.Database;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

public class ChatWindow extends JFrame {

	private JPanel contentPane;
	private JTextField txt_Send;
	private User targetUser;
	private User originUser;
	/**
	 * Create the frame.
	 */
	public ChatWindow(User originUser, User targetUser, FriendsList friendsList) {
		this.originUser = originUser;
		this.targetUser = targetUser;
		setTitle("Chat with " + targetUser.getUserName());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 460, 322);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JTextPane txt_Receive = new JTextPane();
		txt_Receive.setEditable(false);
		
		txt_Send = new JTextField();
		txt_Send.setColumns(10);
		
		JButton txt_Submit = new JButton("Submit");
		txt_Submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//send message
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(txt_Receive, GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(txt_Send, GroupLayout.PREFERRED_SIZE, 344, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txt_Submit)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(txt_Receive, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(txt_Send, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
						.addComponent(txt_Submit)))
		);
		contentPane.setLayout(gl_contentPane);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				friendsList.getChatSession().remove(targetUser.getUserName());
			}
		});
	}
}
