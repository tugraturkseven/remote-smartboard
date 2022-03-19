package smartboardClient;


import java.awt.Font;

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JLabel;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;


public class Chat {

	JFrame frmChat;
	JTextArea txtChat = new JTextArea();
	JTextField txtMessage;
	boolean sendMessage = false;
	
	public Chat() {
		initialize();
	}

	
	private void initialize() {
		frmChat = new JFrame();
		frmChat.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmChat.setIconImage(Toolkit.getDefaultToolkit().getImage(Chat.class.getResource("/icons/login.png")));
		frmChat.setTitle("Akýllý Tahta Yönetim Paneli");
		
		frmChat.setResizable(false);
		frmChat.getContentPane().setBackground(new Color(42,42,42));
		frmChat.getContentPane().setLayout(null);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==10) {
					if(txtChat.getText().isEmpty())
						txtChat.setText("\n"+"   Siz: "+txtMessage.getText());
					else
						txtChat.setText(txtChat.getText()+"\n"+"   Siz: "+txtMessage.getText());
					
					sendMessage=true;
				}
			}
		});
		txtMessage.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if(txtMessage.getText().isEmpty())
					txtMessage.setText("Mesaj giriniz...");
			}
		});
		txtMessage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtMessage.getText().matches("Mesaj giriniz..."))
					txtMessage.setText("");
			}
		});
		txtMessage.setText("Mesaj giriniz...");
		txtMessage.setForeground(Color.WHITE);
		txtMessage.setBackground(new Color(42,42,42));
		txtMessage.setBorder(BorderFactory.createEmptyBorder());
		txtMessage.setBounds(68, 418, 564, 16);
		frmChat.getContentPane().add(txtMessage);
		txtMessage.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 806, 393);
		frmChat.getContentPane().add(scrollPane);
		txtChat.setEditable(false);
		txtChat.setForeground(Color.WHITE);
		txtChat.setFont(new Font("Tahoma",Font.PLAIN,15));
		txtChat.setBackground(new Color(40,40,40));
		scrollPane.setViewportView(txtChat);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Runtime.getRuntime().exec("cmd /c C:\\Windows\\System32\\osk.exe");
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
			}
		});
		lblNewLabel.setIcon(new ImageIcon(Chat.class.getResource("/icons/keyboard.png")));
		lblNewLabel.setBounds(10, 409, 30, 30);
		frmChat.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		lblNewLabel_1.setIcon(new ImageIcon(Chat.class.getResource("/icons/messagetextbox.png")));
		lblNewLabel_1.setBounds(52, 413, 593, 25);
		frmChat.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("New label");
		lblNewLabel_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtChat.getText().isEmpty()) {
					txtChat.setText("\n"+"   Siz: "+txtMessage.getText());
				}else
					txtChat.setText(txtChat.getText()+"\n"+"   Siz: "+txtMessage.getText());
				sendMessage=true;
			}
		});
		lblNewLabel_2.setIcon(new ImageIcon(Chat.class.getResource("/icons/btnSend.png")));
		lblNewLabel_2.setBounds(665, 412, 125, 25);
		frmChat.getContentPane().add(lblNewLabel_2);
		frmChat.setBounds(100, 100, 812, 485);
	}
}
