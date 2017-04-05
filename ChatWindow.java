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
import java.awt.event.ActionEvent;

public class ChatWindow extends JFrame {
	private static final long serialVersionUID = 5967819167561738505L;
	private JPanel contentPane;
	private JTextField txt_Send;
	private JTextPane txt_Receive;
	private User targetUser;
	private User originUser;
	private Socket sock;
	private SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss");
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
			serverOutput = sock.getOutputStream();
	        osw = new OutputStreamWriter(serverOutput);
	        	        
	        osw.write("Chat message: " + originUser.name + "," + targetUser.name + "," + txt_Send.getText() + "\r\n");
	        osw.flush();
	        MessageDAOImpl mDAO = new MessageDAOImpl();
	        mDAO.insert(originUser.name, targetUser.name, txt_Send.getText());
	        txt_Receive.setText(/*"[" + sdf.format(new Date()) + "]" +*/ txt_Receive.getText() + originUser.name + ": " + txt_Send.getText() + "\n");
	        txt_Send.setText("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public synchronized void setMessages(ArrayList<Message> messages) {
		for (Message m : messages) {
	        txt_Receive.setText(/*"[" + sdf.format(new Date()) + "]" +*/ txt_Receive.getText() + m.getOriginUser() + ": " + m.getMessage() + "\n");
		}
	}
	public String getTargetUser() {
		return targetUser.getUserName();
	}
	public void receiveMessage(String originUser, String message) {
        txt_Receive.setText(/*"[" + sdf.format(new Date()) + "]" + */txt_Receive.getText() + originUser + ": " + message + "\n");
	}
}
