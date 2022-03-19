package smartboardClient;

import java.awt.Desktop;
import java.awt.EventQueue;




import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.ObjectInputStream;


import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;


import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Panel;
import java.awt.Window.Type;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPasswordField;



import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginPage extends Thread{

	private JFrame frame;
	static boolean login = false;
	static LoginPage window;
	private JLabel lblBackground;
	private static JPasswordField txtID;
	private JLabel btnShow;
	private JLabel btn1;
	private JLabel btn2;
	private JLabel btn3;
	private JLabel btn4;
	private JLabel btn5;
	private JLabel btn6;
	private JLabel btn7;
	private JLabel btn8;
	private JLabel btn9;
	private JLabel btn0;
	private JLabel btnShutdown;
	private JLabel btnRestart;
	
	static Socket socketAccept;
	
	ServerSocket serverScreen;
	GetFile getFile = new GetFile();
	static boolean isGettingFile = false;
	static boolean sendScreen = false;
	static Chat chatScreen = new Chat();
	static boolean isChatting = false;
	
	static boolean wait = true;
	
	static String id = "";
	
	MessagePanel messagePanel;
	int count = 0;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new LoginPage();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					
				}
			}
		});
		
		CheckConnection connectionCheck = new CheckConnection(); //creating a connection check thread in order to avoid illegal logins
		new Thread(connectionCheck).start();
		
		OnlineStatus onlineCheck = new OnlineStatus(); //creating a online checker thread in order to satisfy online/offline status
		new Thread(onlineCheck).start();
		
		Registry regCheck = new Registry();
		new Thread(regCheck).start();
		
		while(wait) {

			try {
				
				File checkExcel = new File(System.getProperty("user.dir")+"\\config.ini");
				//checks if does board have database
				if(checkExcel.exists()) {
					if(login) {
						Match.Search(txtID.getText());
						login=false;
					}else {
						// usb ile giriþ
						try {
							File.listRoots();
							File[] deviceList = File.listRoots(); //list of connected devices
							String[] usbList = new String[]{ "E", "F", "G", "H", "I" ,"J","K", "L","M", "N"};
									//list of possible usb names
							for (int i = 0; i < deviceList.length; i++) {
								for (int k = 0; k < usbList.length; k++) {
									String usbName = deviceList[i].toString().split(":")[0];
									if(usbList[k].matches(usbName)) {
										File check = new File(usbName+":\\password.txt");
										if(check.exists()) {
											
											ObjectInputStream usbIn = new ObjectInputStream(new FileInputStream(usbName+":\\password.txt"));
											String readUSB = (String) usbIn.readObject();
											
											Match.Search(readUSB);
											usbIn.close();
																		
										}
									}
								}
							}

						} catch (Exception e) {
							
						}
					}
					
				
				}else { //excel yoksa
				
				GetExcel getExcel = new GetExcel();
				Thread excelThread = new Thread(getExcel);
				excelThread.start();
				excelThread.join();		
				
				}	 
			} catch (Exception e) {
				
			}
			try {
				Thread.sleep(100); // threads sleeps for reducing the cpu usage
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		
	}

	public  void loginComplete() throws IOException{

		LoginPage.window.frame.hide();
		ServerSocket server = new ServerSocket(9875); //creating a server for accepting administration connections
		server.setReuseAddress(true);
		
		while(true) {
			try {
				
				
				socketAccept = server.accept();		
				socketAccept.setReuseAddress(true);
				
				DataInputStream in = new DataInputStream(socketAccept.getInputStream());
				DataOutputStream out = new DataOutputStream(socketAccept.getOutputStream());
				
				while(!socketAccept.isClosed()) {
					
					String komut=""; // getting commands as string and handling them with if string matches method
					if(in.available()>0) // checks if there is a command or not
						komut = in.readUTF();
					
						
						if(komut.matches("Ekran izle")) {
							
							if(!sendScreen) { //starts a thread to send screenshots
								sendScreen = true;
								SendScreen send = new SendScreen();
								new Thread(send).start();
								
							}
							
						}
						if(komut.matches("chat")) { //stars a chat with admin
							
							chatScreen.frmChat.show();
							if(!isChatting) {
								
								GetMessage recieve = new GetMessage();
								new Thread(recieve).start();
								isChatting = true;
								try {
									Runtime.getRuntime().exec("cmd /c C:\\Windows\\System32\\osk.exe");
								} catch (IOException e1) {
									
									e1.printStackTrace();
								}
							}
							
									
						}
						
						if(komut.matches("shutdown")) { //shutdown the board
							Runtime.getRuntime().exec("shutdown -s -t 1");
						}
						
						if(komut.matches("restart")) { //restart the board
							Runtime.getRuntime().exec("shutdown -r -t 1");
						}
						
						if(komut.matches("openweb")) { //open a web site
							
							String url = in.readUTF();
							
							try {
							    Desktop.getDesktop().browse(new URL(url).toURI());
							} catch (Exception e) {
								
							}
						}
						
						if(komut.matches("popupinfo")) { //gets a pop up message from admin
							
							String message = in.readUTF();
							messagePanel = new MessagePanel("information", message);
							new Thread(messagePanel).start();
						}
						
						if(komut.matches("popupcritical")) { //gets a pop up message from admin
							
							String message = in.readUTF();
							messagePanel = new MessagePanel("critical", message);
							new Thread(messagePanel).start();
						}
						
						if(komut.matches("dosya")) { //starts a thread for getting file from admin
														
							Thread fileThread = new Thread(getFile);
							
							fileThread.start();
							
							isGettingFile = true;
						}
						
						
						if(chatScreen.sendMessage) { //sends message on chat to admin
							out.writeUTF(chatScreen.txtMessage.getText());
							chatScreen.txtMessage.setText("");
							out.flush();
							
							chatScreen.sendMessage=false;
						}
						
						
						if(komut.matches("disconnect")) { //handling disconnect and recursion for accepting new connection
							try {
								if(sendScreen) {
									sendScreen = false;
								}
								if(isChatting) {
									isChatting = false;
								}
								
								if(isGettingFile) {
									GetFile.cleanUp();									
									isGettingFile = false;
								}
								
								
								in.close();
								out.close();								
								server.close();
								
								in = null;
								out = null;
								
								server = null;
								
								
								socketAccept.close();
								
							} catch (Exception e) {
								
							}
							
						}
						
						
					Thread.sleep(100);
				}
				
				socketAccept = null;
				System.gc();
				loginComplete();
				
				
			} catch (Exception e) {
				
				
			}

		}
	
		
	}
	public LoginPage() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {

				
			}
		});
		
		frame.setType(Type.UTILITY);
		
		frame.setUndecorated(true);
		frame.getContentPane().setBackground(new Color(45,45,45));
		
		frame.setBounds(100, 100, 1920, 1080);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocation(0, 0);
		frame.setAlwaysOnTop(false);
		
		Panel panel = new Panel();
		panel.setBackground(new Color(42,42,42));
		panel.setBounds(0, 0, 1920, 1080);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel btnC = new JLabel(""); //clear the id number
		btnC.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				txtID.setText("");
			}
		});
		btnC.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/C.png")));
		btnC.setBounds(1321, 753, 50, 50);
		panel.add(btnC);
		
		JLabel btnBack = new JLabel("");
		btnBack.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) { //delete the last index of id number
				String password = txtID.getText();
				if(!password.isEmpty())
					txtID.setText(password.substring(0,password.length()-1));;
			}
		});
		btnBack.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/back.png")));
		btnBack.setBounds(1475, 753, 50, 50);
		panel.add(btnBack);
		
		btnRestart = new JLabel("");
		btnRestart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Runtime.getRuntime().exec("shutdown -r -t 1");
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		btnRestart.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/refresh.png")));
		btnRestart.setBounds(1743, 1005, 75, 75);
		panel.add(btnRestart);
		
		btnShutdown = new JLabel("");
		btnShutdown.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Runtime.getRuntime().exec("shutdown -s -t 1");
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				
			}
		});
		btnShutdown.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/power-button.png")));
		btnShutdown.setBounds(1835, 1005, 75, 75);
		panel.add(btnShutdown);
		
		btn0 = new JLabel("New label");
		btn0.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtID.getText().length()<11)
					txtID.setText(txtID.getText()+"0");
			}
		});
		btn0.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/0.png")));
		btn0.setBounds(1400, 753, 50, 50);
		panel.add(btn0);
		
		btn9 = new JLabel("");
		btn9.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtID.getText().length()<11)
					txtID.setText(txtID.getText()+"9");
			}
		});
		btn9.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/9.png")));
		btn9.setBounds(1475, 684, 50, 50);
		panel.add(btn9);
		
		btn8 = new JLabel("");
		btn8.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtID.getText().length()<11)
					txtID.setText(txtID.getText()+"8");
			}
		});
		btn8.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/8.png")));
		btn8.setBounds(1400, 684, 50, 50);
		panel.add(btn8);
		
		btn7 = new JLabel("");
		btn7.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtID.getText().length()<11)
					txtID.setText(txtID.getText()+"7");
			}
		});
		btn7.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/7.png")));
		btn7.setBounds(1321, 684, 50, 50);
		panel.add(btn7);
		
		btn6 = new JLabel("");
		btn6.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtID.getText().length()<11)
					txtID.setText(txtID.getText()+"6");
			}
		});
		btn6.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/6.png")));
		btn6.setBounds(1475, 615, 50, 50);
		panel.add(btn6);
		
		btn5 = new JLabel("");
		btn5.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtID.getText().length()<11)
					txtID.setText(txtID.getText()+"5");
			}
		});
		btn5.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/5.png")));
		btn5.setBounds(1400, 615, 50, 50);
		panel.add(btn5);
		
		btn4 = new JLabel("");
		btn4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtID.getText().length()<11)
					txtID.setText(txtID.getText()+"4");
			}
		});
		btn4.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/4.png")));
		btn4.setBounds(1321, 615, 50, 50);
		panel.add(btn4);
		
		btn3 = new JLabel("");
		btn3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtID.getText().length()<11)
					txtID.setText(txtID.getText()+"3");
			}
		});
		btn3.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/3.png")));
		btn3.setBounds(1475, 546, 50, 50);
		panel.add(btn3);
		
		btn2 = new JLabel("");
		btn2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtID.getText().length()<11)
					txtID.setText(txtID.getText()+"2");
			}
		});
		btn2.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/2.png")));
		btn2.setBounds(1400, 546, 50, 50);
		panel.add(btn2);
		
		btn1 = new JLabel("");
		btn1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(txtID.getText().length()<11)
					txtID.setText(txtID.getText()+"1");
					
			}
		});
		btn1.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/1.png")));
		btn1.setBounds(1321, 546, 50, 50);
		panel.add(btn1);
		
		
		
		JLabel btnHide = new JLabel("");
		btnHide.addMouseListener(new MouseAdapter() { //hides the id number
			@Override
			public void mouseClicked(MouseEvent e) {
				
				txtID.setEchoChar('•');
				btnHide.hide();
				btnShow.show();
			}
		});
		btnHide.setVisible(false);
		
		btnShow = new JLabel("");
		btnShow.addMouseListener(new MouseAdapter() { //shows the id number clearly
			@Override
			public void mouseClicked(MouseEvent e) {
				txtID.setEchoChar((char)0);
				btnShow.hide();
				btnHide.show();
			}
		});
		btnShow.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/eye.png")));
		btnShow.setBounds(1743, 473, 50, 50);
		btnShow.setVisible(true);
		panel.add(btnShow);
		
		
		btnHide.setVisible(false);
		btnHide.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/hide.png")));
		btnHide.setBounds(1743, 473, 50, 50);
		panel.add(btnHide);
		
		JLabel btnLogin = new JLabel("");
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				login=true;
			}
		});
		btnLogin.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/log-in.png")));
		btnLogin.setBounds(1671, 473, 50, 50);
		panel.add(btnLogin);
		
		txtID = new JPasswordField();
		txtID.setEchoChar('•');
		txtID.setEditable(false);
		txtID.setBorder(BorderFactory.createEmptyBorder());
		txtID.setBackground(new Color(42,42,42));
		txtID.setForeground(Color.WHITE);
		txtID.setBounds(1241, 488, 389, 20);
		panel.add(txtID);
		
		lblBackground = new JLabel("");
		lblBackground.setIcon(new ImageIcon(LoginPage.class.getResource("/icons/whole.png")));
		lblBackground.setBounds(0, 0, 1920, 1080);
		panel.add(lblBackground);
	}
	

}
