import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.awt.event.ActionEvent;

public class ChatWindow extends JFrame {

	private JPanel contentPane;
	private JTextField txt_Send;
	private JTextPane txt_Receive;
	private User targetUser;
	private User originUser;
	private Socket sock;
	/**
	 * Create the frame.
	 */
	public ChatWindow(Socket sock, User originUser, User targetUser, FriendsList friendsList) {
		this.sock = sock;
		this.originUser = originUser;
		this.targetUser = targetUser;
		
		setTitle("Chat with " + targetUser.getUserName());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 460, 322);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		txt_Receive = new JTextPane();
		txt_Receive.setEditable(false);
		
		txt_Send = new JTextField();
		txt_Send.setColumns(10);
		JButton txt_Submit = new JButton("Submit");
		txt_Submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sendMessage();
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(txt_Receive, GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(txt_Send, GroupLayout.PREFERRED_SIZE, 344, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txt_Submit)
					.addContainerGap(19, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(55)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(377, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(txt_Receive, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
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
	private void sendMessage() {
        InputStream serverInput = null;
        OutputStream serverOutput = null;
        Scanner scan = null;
        OutputStreamWriter osw = null;
		try {
			serverOutput = sock.getOutputStream();
	        serverInput = sock.getInputStream();
	        osw = new OutputStreamWriter(serverOutput);
	        	        
	        osw.write("Chat message: " + originUser.name + "," + targetUser.name + "," + txt_Send.getText() + "\r\n");
	        osw.flush();
			SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss");
	        txt_Receive.setText(/*"[" + sdf.format(new Date()) + "]" +*/ txt_Receive.getText() + originUser.name + ": " + txt_Send.getText() + "\n");
	        txt_Send.setText("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String getTargetUser() {
		return targetUser.getUserName();
	}
	public void receiveMessage(String originUser, String message) {
		SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss");
        txt_Receive.setText(/*"[" + sdf.format(new Date()) + "]" + */txt_Receive.getText() + originUser + ": " + message + "\n");
	}
}
