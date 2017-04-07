package View;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import DAO.Message;
import DAO.MessageDAOImpl;
import DAO.User;
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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatWindow extends JFrame {
	private static final long serialVersionUID = 5967819167561738505L;
	private JPanel contentPane;
	private JTextField txt_Send;
	private JTextPane txt_Receive;
	private User targetUser;
	private User originUser;
	private Socket sock;
	private SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a");
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
		JButton txt_Submit = new JButton("Submit");
		txt_Submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sendMessage();
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txt_Submit)
					.addGap(14))
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 205, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(txt_Submit)
							.addGap(19))
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)))
		);
		
		txt_Send = new JTextField();
		txt_Send.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
		        if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
		        {
		          sendMessage();
		        }
			}
		});
		scrollPane_1.setViewportView(txt_Send);
		txt_Send.setColumns(10);
		
		txt_Receive = new JTextPane();
		scrollPane.setViewportView(txt_Receive);
		txt_Receive.setEditable(false);
		contentPane.setLayout(gl_contentPane);
		/**
		 * Removes chat window and chat session; this fixes the bug that would not spawn a new window if the targetUser closed
		 * and the origin user sent a new message resulting in a window that would not pop up.
		 */
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				friendsList.getChatSession().remove(targetUser.getUserName());
				ChatWindow toRemove = null;
				for (ChatWindow c : friendsList.getChatWindow()) {
					if (c.getTargetUser().equals(targetUser.getUserName())) {
						toRemove = c;
					}
				}
				friendsList.getChatWindow().remove(toRemove);
			}
		});
	}
	private void sendMessage() {
        OutputStream serverOutput = null;
        OutputStreamWriter osw = null;
		try {
			if (txt_Send.getText().equals("")) 
				return;
			serverOutput = sock.getOutputStream();
	        osw = new OutputStreamWriter(serverOutput);
	        	        
	        osw.write("Chat message: " + originUser.name + "," + targetUser.name + "," + txt_Send.getText() + "\r\n");
	        osw.flush();
	        MessageDAOImpl mDAO = new MessageDAOImpl();
	        mDAO.insert(originUser.name, targetUser.name, txt_Send.getText());
			String displayMessage = txt_Receive.getText() + "[" + sdf.format(new Date()) + "] " +  originUser.name + ": " + txt_Send.getText() + "\n";
	        txt_Receive.setText(displayMessage);
	        txt_Send.setText("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setMessages(ArrayList<Message> messages) {
		String message = "";
		for (Message m : messages) {
			message += "[" + sdf.format(m.getTime()) + "] " + txt_Receive.getText() + m.getOriginUser() + ": " + m.getMessage() + "\n";
		}
        txt_Receive.setText(message);
	}
	public String getTargetUser() {
		return targetUser.getUserName();
	}
	public void receiveMessage(String originUser, String message) {
		String displayMessage = txt_Receive.getText() + "[" + sdf.format(new Date()) + "] " +  originUser + ": " + message + "\n";
        txt_Receive.setText(displayMessage);
	}
}
