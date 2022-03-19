package smartboardServer;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.EventQueue;


import javax.swing.JFrame;


import java.awt.Color;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.JPanel;

import javax.swing.JOptionPane;

import javax.swing.JLabel;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;

import java.awt.Font;


import javax.swing.SwingConstants;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import javax.swing.JTextArea;

import javax.swing.JTable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.border.EmptyBorder;


import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Toolkit;

public class controlPanel {
	
	//connections
	private static ServerSocket server;
	private static int port = 9876;
	
	
	JFrame frmSmartboardControlPanel;
	
	JPanel pnlChat = new JPanel();
	JPanel pnlCommands = new JPanel();
	
	JTextField txtMessage;
	JScrollPane tblBoards = new JScrollPane();
	JLabel lblPage = new JLabel("Ana Sayfa");
	
	static boolean isSendingFile = false;
	static JRadioButton rdbShutdown = new JRadioButton("Kapat");
	static JRadioButton rdbInformation = new JRadioButton("Bilgi Mesajý");
	static  JTextField txtURL;
	static  JTextField txtPopupMessage;
	static JTable tblOnline;
	static JLabel btnDosya = new JLabel("");
	static DefaultTableModel model;
	static JLabel lblStatus = new JLabel("Baðlantý yok!");	
	static JTextArea txtChat = new JTextArea();
	static JLabel btnConnect = new JLabel("");
	static JLabel btnDisconnect = new JLabel("");
	
	
	private static int rowNumber = -1; // keep the selected row number
	
	// sockets
	static Socket socketAccept;
	static Socket socketConnect;
	static Socket socketChat;
	static Observer screen = new Observer(); //creating a new screen observer
	
	static boolean observeScreen = false; //initialize default status
	static boolean isChatting = false;
	static boolean isObserving = false;
	
	
	static JLabel lblDosyaStatus = new JLabel("Dosya seçimi için bekleniyor...");
	
	SendFile sendFile;
	Thread fileSender;
	
	
	
	static ArrayList<String> onlineList = new ArrayList<String>(50); //arraylist for storing online boards data
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
	
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					controlPanel window = new controlPanel();
					window.frmSmartboardControlPanel.setVisible(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Program baþlatýlamadý. Kritik hata! " + e.toString(), "Hata", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
			}
		});
		
		
		
		try {
			//creating server for board connection
			server = new ServerSocket(port);
			server.setReuseAddress(true);
			
			while(true) {
				try { //accepting new connections and handling them with clientHandler as another thread
					socketAccept = server.accept(); 
					DataInputStream dis = new DataInputStream(socketAccept.getInputStream());
					String datas = dis.readUTF();						
					String[] splitDatas = datas.split("-");
					onlineList.add(splitDatas[0]);
						
					ClientHandler clientSock = new ClientHandler(socketAccept,splitDatas);
					new Thread(clientSock).start();
					
					
							
				
				} catch (Exception e) {
					try {
						socketAccept.close();
					} catch (Exception e2) {
						
					}
					
				JOptionPane.showMessageDialog(null, "Hata oluþtu: "+e.toString(), "Hata!", JOptionPane.ERROR_MESSAGE);
				}

				}
			
		} catch (Exception e) {
			
			JOptionPane.showMessageDialog(null, "Program zaten çalýþýyor.", "", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Hata: " + e2.toString(), "Hata", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
	}

	private static class ClientHandler implements Runnable	{
		
		private final Socket clientSocket;
		private final String localIP,userName,userSurname,excelExist;
		
		
		public ClientHandler(Socket clientSocket,String[] datas) {
			this.clientSocket = clientSocket;
			//assigning datas
			this.localIP= datas[0];
			this.userName = datas[1];
			this.userSurname = datas[2];
			this.excelExist = datas[3];
		}
		
		@Override
		public void run() {

			try {
				if(excelExist.matches("yes")) { // if excel exists add the datas to table
					
						Object[] addRow = new Object[3];
						addRow[0] = localIP;
						addRow[1] = userName;
						addRow[2] = userSurname;
						boolean findSpace = true;
						if(findSpace) {
							String ip;
							for (int i = 0; i < 13; i++) { //checks if first added rows empty or not
								while(true) {
									try {
										ip = (String) model.getValueAt(i, 0);
										break;
									} catch (Exception e) {
										try {
											Thread.sleep(500);
										} catch (Exception e2) {
											
										}
									}
								}
								
								if(ip.isEmpty()) {
									
									model.setValueAt(localIP, i, 0);
									model.setValueAt(userName, i, 1);
									model.setValueAt(userSurname, i, 2);
									findSpace = false;
									break;
								}
							}
						}
						
						if(findSpace)
							model.addRow(addRow);
					
					
				}
				
				if(excelExist.matches("no")) {
					while(true) {
						
						File checkConfig = new File(System.getProperty("user.dir")+"\\config.ini");
						if(checkConfig.exists()) {
							FileInputStream newFile = new FileInputStream(System.getProperty("user.dir")+"\\config.ini");
							BufferedInputStream in = new BufferedInputStream(newFile);
							
							BufferedOutputStream buffOut = new BufferedOutputStream(clientSocket.getOutputStream());

							
							int nRead;
							byte[] buffer = new byte[4096];
							while((nRead = in.read(buffer, 0, buffer.length))!=-1) {
								buffOut.write(buffer,0,nRead);
							}
							
							buffOut.close();
							buffOut.flush();
							in.close();
							break;
						}else {// if the board doesn't have database encrypting the data and send it to board
							ArrayList<String> list = new ArrayList<String>(1000);
							
							String id;
							String userName;
							String userSurname;
							//gets the excel
							
							FileInputStream newFile = new FileInputStream(System.getProperty("user.dir")+"\\excel.xlsx");
							//read excel
							XSSFWorkbook wb = new XSSFWorkbook(newFile);   
							XSSFSheet sheet = wb.getSheetAt(0);    
							Iterator<Row> itr = sheet.iterator(); 
						
								while (itr.hasNext())                 
								{  
										Row row = itr.next();  
										Iterator<Cell> cellIterator = row.cellIterator();   
											while (cellIterator.hasNext())   
											{  
												Cell cell = cellIterator.next();  
												if(cell.getCellType()==0) {
													id = new DataFormatter().formatCellValue(cell);
													cell = cellIterator.next();
													userName = cell.getStringCellValue();
													cell = cellIterator.next();
													userSurname = cell.getStringCellValue();
													String datas = id+"-"+userName+"-"+userSurname;
													char[] encrypt = datas.toCharArray();
													int key = 5;
													for (int i = 0; i < encrypt.length; i++) {
														encrypt[i] +=key; //encrypt the data
													}
													datas = new String(encrypt);
													list.add(datas);
												}
											}
											
								}
								
								wb.close();
						
						
						FileOutputStream file = new FileOutputStream(System.getProperty("user.dir")+"\\config.ini");
						ObjectOutputStream out = new ObjectOutputStream(file);
						
						out.writeObject(list); //sending the file
						out.flush();
						out.close();
						}
					}
					
					
				}
				
				
				
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Excel dosyasý bulunamýyor. Lütfen programýn bulunduðu dizinde src klasörüne ekleyiniz.", "Excel yok!", JOptionPane.ERROR_MESSAGE);
				
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Akýllý tahta baðlanmaya çalýþýrken hata oluþtu! " + e.toString(), "Hata", JOptionPane.INFORMATION_MESSAGE);
			}
			
			
		}
	}
	
	public controlPanel() {
		initialize();
	}
	
	public static void connect() {
		if(rowNumber!=-1) {
			String localip = (String) model.getValueAt(rowNumber, 0);
			
			if(!localip.isEmpty()) {
			
			try {
				socketConnect = new Socket(localip,9875);
				if(socketConnect.isConnected()) {
					lblStatus.setText("Baðlandý!..");
					btnConnect.hide();
					btnDisconnect.show();
					tblOnline.setEnabled(false);
				}
					
			} catch (IOException e1) {
				onlineList.remove(rowNumber);
				if(model.getRowCount()<=13) { //if any error occurs deleting that board from table
					model.removeRow(rowNumber);
					Object[] addRow = new Object[3];
					addRow[0] = "";
					addRow[1] = "";
					addRow[2] = "";
					
					model.addRow(addRow);
					
				}else
					model.removeRow(rowNumber);
				
				
				JOptionPane.showMessageDialog(null, "Akýllý tahta beklenmedik þekilde kapatýldý." , "Baðlanamadý!", JOptionPane.ERROR_MESSAGE);
				
			}
			}else
				JOptionPane.showMessageDialog(null, "Lütfen Akýllý Tahta seçin.", "", JOptionPane.INFORMATION_MESSAGE);
		}else
			JOptionPane.showMessageDialog(null, "Lütfen Akýllý Tahta seçin.", "", JOptionPane.INFORMATION_MESSAGE);
		
	}

	public static void disconnect() {
		if(!lblStatus.getText().matches("Baðlantý yok!")) {
			try {
				DataOutputStream out = new DataOutputStream(socketConnect.getOutputStream());
				out.writeUTF("disconnect");
				out.flush();
				
				socketConnect.close();
				if(isObserving)
					screen.frmObserver.hide();
					isObserving = false;
				if(isChatting) {
					socketChat.close();
					socketChat = null;
					txtChat.setText("");
					isChatting = false;
				}
					
				if(!btnDosya.isShowing()) {
					btnDosya.show();
					
				}
				txtURL.setText("URL Giriniz...");
				txtPopupMessage.setText("Mesaj Giriniz...");
				lblDosyaStatus.setText("Dosya seçimi için bekleniyor...");
				btnDisconnect.hide();
				btnConnect.show();
				lblStatus.setText("Baðlantý yok!");
				tblOnline.setEnabled(true);
				rdbShutdown.setSelected(true);
				rdbInformation.setSelected(true);
				
				System.gc();
			} catch (IOException e1) {
				
				int i = tblOnline.getSelectedRow();
				String ip = (String) tblOnline.getValueAt(i, 0);;
				
				int index = onlineList.indexOf(ip);
				if(model.getRowCount()<=13) {
					model.setValueAt("", rowNumber, 0);
					model.setValueAt("", rowNumber, 1);
					model.setValueAt("", rowNumber, 2);
				}else
					model.removeRow(index);
					onlineList.remove(index);
				
				try {
					socketConnect.close();
					if(isObserving)
						screen.frmObserver.hide();
						isObserving = false;
					if(isChatting) {
						socketChat.close();
						txtChat.setText("");
						isChatting = false;
					}
						
					
					btnDisconnect.hide();
					btnConnect.show();
					lblStatus.setText("Baðlantý yok!");
				} catch (Exception e2) {
					
				}
				
				JOptionPane.showMessageDialog(null, "Akýllý tahta zaten kapatýlmýþ.", "Hata", JOptionPane.ERROR_MESSAGE);
				
			}
			
			
		}
		
	}
	@SuppressWarnings("serial")
	private void initialize() {
		frmSmartboardControlPanel = new JFrame();
		frmSmartboardControlPanel.setIconImage(Toolkit.getDefaultToolkit().getImage(controlPanel.class.getResource("/icons/atom.png")));
		frmSmartboardControlPanel.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				String ip = (String) tblOnline.getValueAt(0, 0);
				if(!ip.isEmpty()) {
					Object[] choices = {"Evet","Hayýr"};
					int exit = JOptionPane.showOptionDialog(null, "Aktif tahta verileri kaybolucak, emin misiniz?", "Çýkýþ yap?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, choices, 1);
					if(exit==0)
						frmSmartboardControlPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					else
						frmSmartboardControlPanel.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				}

				disconnect();
				try {
					Thread.sleep(500);
				} catch (Exception e2) {
					
				}
			}
		});
		frmSmartboardControlPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSmartboardControlPanel.getContentPane().setBackground(new Color(64, 64, 64));
		frmSmartboardControlPanel.setBackground(Color.DARK_GRAY);
		frmSmartboardControlPanel.getContentPane().setForeground(Color.BLACK);
		frmSmartboardControlPanel.setAlwaysOnTop(false);
		frmSmartboardControlPanel.setResizable(false);
		frmSmartboardControlPanel.setTitle("Akýllý Tahta Yönetim Paneli");
		frmSmartboardControlPanel.setBounds(100, 100, 900, 524);
		
		frmSmartboardControlPanel.getContentPane().setLayout(null);
		frmSmartboardControlPanel.setLocationRelativeTo(null);
		
		ButtonGroup shutdownGroup = new ButtonGroup();
		
		ButtonGroup popupGroup = new ButtonGroup();
		
		JPanel pnlMain = new JPanel();
		pnlMain.setBackground(new Color(40, 40, 40));
		pnlMain.setBounds(80, 0, 814, 487);
		frmSmartboardControlPanel.getContentPane().add(pnlMain);
		pnlMain.setLayout(null);
		
		pnlCommands.setBounds(0, 123, 815, 364);
		pnlMain.add(pnlCommands);
		pnlCommands.setBackground(new Color(40,40,40));
		pnlCommands.setLayout(null);
		
		
		
		JLabel lblEnerygyIcon = new JLabel("");
		lblEnerygyIcon.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/energy.png")));
		lblEnerygyIcon.setBounds(10, 11, 35, 35);
		pnlCommands.add(lblEnerygyIcon);
		
		JLabel lblGucSecenekleri = new JLabel("Güç Seçenekleri");
		lblGucSecenekleri.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblGucSecenekleri.setForeground(new Color(255, 255, 255));
		lblGucSecenekleri.setBounds(50, 17, 103, 23);
		pnlCommands.add(lblGucSecenekleri);
		
		
		rdbShutdown.setSelected(true);
		rdbShutdown.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbShutdown.setForeground(new Color(255, 255, 255));
		rdbShutdown.setBackground(new Color(40, 40, 40));
		rdbShutdown.setBounds(95, 48, 109, 23);
		rdbShutdown.setBorder(new EmptyBorder(0, 0, 0, 0));
		pnlCommands.add(rdbShutdown);
		
		JLabel lblShutdownIcon = new JLabel("");
		lblShutdownIcon.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/shutdown.png")));
		lblShutdownIcon.setBounds(60, 45, 30, 30);
		pnlCommands.add(lblShutdownIcon);
		
		JRadioButton rdbRestart = new JRadioButton("Yeniden Baþlat");
		rdbRestart.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbRestart.setBorder(BorderFactory.createEmptyBorder());
		rdbRestart.setForeground(new Color(255, 255, 255));
		rdbRestart.setBackground(new Color(40, 40, 40));
		rdbRestart.setBounds(96, 90, 155, 23);
		pnlCommands.add(rdbRestart);
		
		shutdownGroup.add(rdbRestart);
		shutdownGroup.add(rdbShutdown);
		
		JLabel lblRestartIcon = new JLabel("");
		lblRestartIcon.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/restart.png")));
		lblRestartIcon.setBounds(61, 85, 30, 30);
		pnlCommands.add(lblRestartIcon);
		
		JLabel lblWebDirectIcon = new JLabel("");
		lblWebDirectIcon.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/web.png")));
		lblWebDirectIcon.setBounds(438, 11, 35, 35);
		pnlCommands.add(lblWebDirectIcon);
		
		JLabel lblWebSayfasiAc = new JLabel("Web Sayfasý Aç");
		lblWebSayfasiAc.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblWebSayfasiAc.setForeground(new Color(255, 255, 255));
		lblWebSayfasiAc.setBounds(483, 17, 103, 23);
		pnlCommands.add(lblWebSayfasiAc);
		
		JLabel lblWebIcon = new JLabel("");
		lblWebIcon.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/webdirect.png")));
		lblWebIcon.setBounds(483, 45, 30, 30);
		pnlCommands.add(lblWebIcon);
		
		txtURL = new JTextField();
		txtURL.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==10) {
					try {
						if(!lblStatus.getText().matches("Baðlantý yok!")) {
							DataOutputStream dos = new DataOutputStream(socketConnect.getOutputStream());
							if(txtURL.getText().isEmpty()) {
								JOptionPane.showMessageDialog(null, "Lütfen URL Giriniz!", "Hata", JOptionPane.ERROR_MESSAGE);
								
							}else if(txtURL.getText().matches("URL Adresi Giriniz...")){
								JOptionPane.showMessageDialog(null, "Lütfen geçerli bir URL Giriniz!", "Hata", JOptionPane.ERROR_MESSAGE);
						}else {
							dos.writeUTF("openweb");
							dos.flush();
							dos.writeUTF("https://"+txtURL.getText());
							txtURL.setText("");
						}
							
							}else
								JOptionPane.showMessageDialog(null, "Lütfen önce Akýllý tahtaya baðlanýn!", "Hata", JOptionPane.ERROR_MESSAGE);
						
					} catch (Exception e2) {
						if(!lblStatus.getText().matches("Baðlantý yok!"))
						JOptionPane.showMessageDialog(null, "Baðlantýda hata oluþtu!" + e2.toString(), "Hata", JOptionPane.ERROR_MESSAGE);
						else
							JOptionPane.showMessageDialog(null, "Lütfen önce Akýllý tahtaya baðlanýn!", "Hata", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		txtURL.setBorder(new EmptyBorder(0, 0, 0, 0));
		txtURL.setBackground(new Color(40,40,40));
		txtURL.setForeground(Color.WHITE);
		txtURL.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if(txtURL.getText().matches(""))
					txtURL.setText("URL Adresi Giriniz...");
			}
		});
		txtURL.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtURL.getText().matches("URL Adresi Giriniz..."))
					txtURL.setText("");
			}
		});
		txtURL.setText("URL Adresi Giriniz...");
		txtURL.setBounds(539, 52, 229, 14);
		pnlCommands.add(txtURL);
		txtURL.setColumns(10);
		
		JLabel lblAnnouncementIcon = new JLabel("");
		lblAnnouncementIcon.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/announcement.png")));
		lblAnnouncementIcon.setBounds(438, 153, 35, 35);
		pnlCommands.add(lblAnnouncementIcon);
		
		JLabel lblPopUpMesajGonder = new JLabel("Pop Up Mesaj Gönder");
		lblPopUpMesajGonder.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPopUpMesajGonder.setForeground(new Color(255, 255, 255));
		lblPopUpMesajGonder.setBounds(488, 153, 144, 26);
		pnlCommands.add(lblPopUpMesajGonder);
		
		JLabel lblInformationIcon = new JLabel("");
		lblInformationIcon.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/information.png")));
		lblInformationIcon.setBounds(488, 225, 30, 30);
		pnlCommands.add(lblInformationIcon);
		
		
		rdbInformation.setSelected(true);
		rdbInformation.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbInformation.setForeground(new Color(255, 255, 255));
		rdbInformation.setBackground(new Color(40, 40, 40));
		rdbInformation.setBounds(523, 229, 174, 23);
		rdbInformation.setBorder(new EmptyBorder(0, 0, 0, 0));
		pnlCommands.add(rdbInformation);
		
		JLabel lblCriticalIcon = new JLabel("");
		lblCriticalIcon.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/critical.png")));
		lblCriticalIcon.setBounds(488, 270, 30, 30);
		pnlCommands.add(lblCriticalIcon);
		
		JRadioButton rdbCritical = new JRadioButton("Kritik Mesaj");
		rdbCritical.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rdbCritical.setForeground(Color.WHITE);
		rdbCritical.setBackground(new Color(40, 40, 40));
		rdbCritical.setBounds(523, 274, 174, 23);
		rdbCritical.setBorder(new EmptyBorder(0, 0, 0, 0));
		pnlCommands.add(rdbCritical);
		
		popupGroup.add(rdbCritical);
		popupGroup.add(rdbInformation);
		
		txtPopupMessage = new JTextField();
		txtPopupMessage.setBackground(new Color(40,40,40));
		txtPopupMessage.setForeground(Color.WHITE);
		txtPopupMessage.setBorder(new EmptyBorder(0, 0, 0, 0));
		txtPopupMessage.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if(txtPopupMessage.getText().matches(""))
					txtPopupMessage.setText("Mesaj Giriniz...");
			}
		});
		txtPopupMessage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtPopupMessage.getText().matches("Mesaj Giriniz..."))
					txtPopupMessage.setText("");
			}
		});
		txtPopupMessage.setText("Mesaj Giriniz...");
		txtPopupMessage.setBounds(539, 192, 229, 14);
		pnlCommands.add(txtPopupMessage);
		txtPopupMessage.setColumns(10);
		
		JLabel btnEnergy = new JLabel("");
		btnEnergy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					if(!lblStatus.getText().matches("Baðlantý yok!")) {
						DataOutputStream dos = new DataOutputStream(socketConnect.getOutputStream());
						if(rdbShutdown.isSelected()) {
							dos.writeUTF("shutdown");
						}else
							dos.writeUTF("restart");
					}else
						JOptionPane.showMessageDialog(null, "Lütfen önce akýllý tahtaya baðlanýn!", "Baðlantý yok", JOptionPane.ERROR_MESSAGE);
				
				} catch (Exception e2) {
					if(!lblStatus.getText().matches("Baðlantý yok!"))
					JOptionPane.showMessageDialog(null, "Baðlantýda hata oluþtu! "+e2.toString(), "Hata", JOptionPane.ERROR_MESSAGE);
					else
						JOptionPane.showMessageDialog(null, "Lütfen önce Akýllý tahtaya baðlanýn!", "Hata", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnEnergy.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/tamam.png")));
		btnEnergy.setBounds(10, 131, 125, 25);
		pnlCommands.add(btnEnergy);
		
		JLabel btnPopUp = new JLabel("");
		btnPopUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					if(!lblStatus.getText().matches("Baðlantý yok!")) {
						DataOutputStream dos = new DataOutputStream(socketConnect.getOutputStream());
						if(txtPopupMessage.getText().isEmpty() || txtPopupMessage.getText().matches("Mesaj Giriniz...")) {
							JOptionPane.showMessageDialog(null, "Lütfen mesaj giriniz!", "Hata", JOptionPane.ERROR_MESSAGE);
						}else {
							if(rdbInformation.isSelected()) {
								dos.writeUTF("popupinfo");
								dos.flush();
								dos.writeUTF(txtPopupMessage.getText());
								dos.flush();
								
							}else {
								dos.writeUTF("popupcritical");
								dos.flush();
								dos.writeUTF(txtPopupMessage.getText());
								dos.flush();
								
							}
							
						}
					}else
						JOptionPane.showMessageDialog(null, "Lütfen önce akýllý tahtaya baðlanýn!", "Baðlantý yok", JOptionPane.ERROR_MESSAGE);
						
						}catch (Exception e2) {
							if(!lblStatus.getText().matches("Baðlantý yok!"))
								JOptionPane.showMessageDialog(null, "Baðlantýda hata oluþtu! "+e2.toString(), "Hata", JOptionPane.ERROR_MESSAGE);
							else
								JOptionPane.showMessageDialog(null, "Lütfen önce Akýllý tahtaya baðlanýn!", "Hata", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnPopUp.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/tamam.png")));
		btnPopUp.setBounds(488, 311, 125, 25);
		pnlCommands.add(btnPopUp);
		
		JLabel btnWeb = new JLabel("");
		btnWeb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					if(!lblStatus.getText().matches("Baðlantý yok!")) {
						DataOutputStream dos = new DataOutputStream(socketConnect.getOutputStream());
						if(txtURL.getText().isEmpty()) {
							JOptionPane.showMessageDialog(null, "Lütfen URL Giriniz!", "Hata", JOptionPane.ERROR_MESSAGE);
							
						}else if(txtURL.getText().matches("URL Adresi Giriniz...")){
							JOptionPane.showMessageDialog(null, "Lütfen geçerli bir URL Giriniz!", "Hata", JOptionPane.ERROR_MESSAGE);
					}else {
						dos.writeUTF("openweb");
						dos.flush();
						dos.writeUTF("https://"+txtURL.getText());
						txtURL.setText("");
					}
						
						}else
							JOptionPane.showMessageDialog(null, "Lütfen önce akýllý tahtaya baðlanýn!", "Baðlantý yok", JOptionPane.ERROR_MESSAGE);
					
				} catch (Exception e2) {
					if(!lblStatus.getText().matches("Baðlantý yok!"))
					JOptionPane.showMessageDialog(null, "Baðlantýda hata oluþtu!" + e2.toString(), "Hata", JOptionPane.ERROR_MESSAGE);
					else
						JOptionPane.showMessageDialog(null, "Lütfen önce Akýllý tahtaya baðlanýn!", "Hata", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnWeb.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/tamam.png")));
		btnWeb.setBounds(488, 90, 125, 25);
		pnlCommands.add(btnWeb);
		
		JLabel lblDosyaIcon = new JLabel("");
		lblDosyaIcon.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/cloud.png")));
		lblDosyaIcon.setBounds(10, 210, 35, 35);
		pnlCommands.add(lblDosyaIcon);
		
		JLabel lblDosyaAktar = new JLabel("Dosya aktar");
		lblDosyaAktar.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblDosyaAktar.setForeground(Color.WHITE);
		lblDosyaAktar.setBounds(50, 223, 201, 14);
		pnlCommands.add(lblDosyaAktar);
		
		JLabel lblDosyaBekleniyor = new JLabel("");
		lblDosyaBekleniyor.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/choosefile.png")));
		lblDosyaBekleniyor.setBounds(55, 252, 30, 30);
		pnlCommands.add(lblDosyaBekleniyor);
		
		
		lblDosyaStatus.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblDosyaStatus.setForeground(Color.WHITE);
		lblDosyaStatus.setBounds(94, 253, 357, 22);
		pnlCommands.add(lblDosyaStatus);
		
		
		btnDosya.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(lblStatus.getText().matches("Baðlandý!..")) {
					try {
						int i = tblOnline.getSelectedRow();
						String ip = (String) tblOnline.getValueAt(i, 0);;
						isSendingFile = true;
						sendFile = new SendFile(ip);
						fileSender= new Thread(sendFile);
						fileSender.start();
						
						
					} catch (Exception e2) {
						JOptionPane.showMessageDialog(null, "Dosya paylaþma sistemi çalýþtýrýlamadý!", "Hata", JOptionPane.ERROR_MESSAGE);
					}
				}else
					JOptionPane.showMessageDialog(null, "Lütfen önce akýllý tahtaya baðlanýn!", "Baðlantý yok", JOptionPane.ERROR_MESSAGE);
				
				
				
			}
		});
		btnDosya.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/dosya.png")));
		btnDosya.setBounds(10, 304, 125, 25);
		pnlCommands.add(btnDosya);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/letter.png")));
		lblNewLabel.setBounds(483, 180, 30, 30);
		pnlCommands.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		lblNewLabel_1.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/urltextbox.png")));
		lblNewLabel_1.setBounds(523, 49, 250, 20);
		pnlCommands.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("New label");
		lblNewLabel_2.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/urltextbox.png")));
		lblNewLabel_2.setBounds(523, 190, 250, 20);
		pnlCommands.add(lblNewLabel_2);
		
		pnlCommands.hide();
		
		
		pnlChat.setBounds(0, 122, 815, 364);
		pnlMain.add(pnlChat);
		pnlChat.setBackground(new Color(40, 40, 40));
		pnlChat.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setBounds(0, 0, 815, 315);
		scrollPane.setBackground(new Color(40, 40, 40));
		pnlChat.add(scrollPane);
		
		
		txtChat.setEditable(false);
		txtChat.setForeground(Color.WHITE);
		txtChat.setFont(new Font("Tahoma",Font.PLAIN,15));
		scrollPane.setViewportView(txtChat);
		txtChat.setBackground(new Color(48,48,48));
		
		pnlChat.hide();
		txtMessage = new JTextField();
		txtMessage.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if(txtMessage.getText().isEmpty())
					txtMessage.setText("Mesajýnýzý buraya giriniz...");
			}
		});
		txtMessage.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtMessage.setBackground(new Color(40,40,40));
		txtMessage.setForeground(Color.WHITE);
		txtMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==10) {
					try {
						if(!lblStatus.getText().matches("Baðlantý yok!")) {
								DataOutputStream out = new DataOutputStream(socketChat.getOutputStream());
								if(txtChat.getText().isEmpty())
									txtChat.setText("   "+"Siz: "+txtMessage.getText());
								else
									txtChat.setText(txtChat.getText()+"\n   "+"Siz: "+txtMessage.getText());
								out.writeUTF("Müdür: "+txtMessage.getText());
								out.flush();
							txtMessage.setText("");
						}else
							JOptionPane.showMessageDialog(null, "Lütfen önce Akýllý tahtaya baðlanýn!", "Hata", JOptionPane.ERROR_MESSAGE);
					} catch (Exception e2) {
						JOptionPane.showMessageDialog(null, "Mesajýnýz iletilemedi! "+e2.toString(), "Hata", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		txtMessage.setBorder(new EmptyBorder(0, 0, 0, 0));
		txtMessage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(txtMessage.getText().matches("Mesajýnýzý buraya giriniz..."))
					txtMessage.setText("");
			}
		});
		txtMessage.setText("Mesajýnýzý buraya giriniz...");
		txtMessage.setBounds(65, 332, 570, 16);
		pnlChat.add(txtMessage);
		txtMessage.setColumns(10);
		
		JLabel lblMessageIcon = new JLabel("");
		lblMessageIcon.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/message.png")));
		lblMessageIcon.setBounds(10, 322, 35, 35);
		pnlChat.add(lblMessageIcon);
		
		JLabel btnSendMessage = new JLabel("");
		btnSendMessage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					if(!lblStatus.getText().matches("Baðlantý yok!")) {
						DataOutputStream out = new DataOutputStream(socketChat.getOutputStream());
						if(txtChat.getText().isEmpty())
							txtChat.setText("\n   "+"Siz: "+txtMessage.getText());
						else
							txtChat.setText(txtChat.getText()+"\n   "+"Siz: "+txtMessage.getText());
						
						out.writeUTF("Müdür: "+txtMessage.getText());
						out.flush();
						txtMessage.setText("");
						
					}else
						JOptionPane.showMessageDialog(null, "Lütfen önce Akýllý tahtaya baðlanýn!", "Hata", JOptionPane.ERROR_MESSAGE);
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Mesajýnýz iletilemedi! "+e2.toString(), "Hata", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnSendMessage.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/btnSend.png")));
		btnSendMessage.setBounds(669, 328, 125, 25);
		pnlChat.add(btnSendMessage);
		
		JLabel lblNewLabel_3 = new JLabel("New label");
		lblNewLabel_3.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/messagetextbox.png")));
		lblNewLabel_3.setBounds(49, 328, 593, 25);
		pnlChat.add(lblNewLabel_3);
		
		
		model= new DefaultTableModel()
				{
					public boolean isCellEditable(int row, int column) {
						return false;
					}
				};
		
		Object[] column = {"IP Adresi","Ýsim","Soyisim"};
	
		
		model.addColumn(column[0]);
		model.addColumn(column[1]);
		model.addColumn(column[2]);
		
		for (int i = 0; i < 13; i++) { //adding 13 rows to avoid from bad looking
			Object[] row = new Object[1];
			String message = "";
			row[0]=message;
			model.addRow(row);
		}
		
		
		
		
		
		tblBoards.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("çalýþtý");
				rowNumber = tblOnline.getSelectedRow();
				
			}
		});
		tblBoards.setBounds(0, 123, 815, 364);
		tblBoards.setOpaque(false);
		tblBoards.setBackground(new Color(42, 42, 42));
		tblBoards.setBorder(BorderFactory.createEmptyBorder());
		pnlMain.add(tblBoards);
		
		tblOnline = new JTable();
		
		tblOnline.setFont(new Font("Tahoma", Font.PLAIN, 15));
		tblOnline.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				rowNumber = tblOnline.getSelectedRow();
				
				if(e.getClickCount()>1) {
					String ip = (String) model.getValueAt(rowNumber, 0);
					if(!ip.isEmpty()&&lblStatus.getText().matches("Baðlantý yok!")) {
						connect();
					}
					
				}
				
			}
		});
		tblOnline.setOpaque(false);
		
		
		tblOnline.setBorder(BorderFactory.createEmptyBorder());
		tblOnline.setBackground(new Color(55,55,55));
		tblOnline.setForeground(Color.white);
		tblOnline.setRowHeight(27);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		tblOnline.setDefaultRenderer(model.getColumnClass(0), centerRenderer);
		tblOnline.setFocusable(false);
		
		
		tblOnline.getTableHeader().setReorderingAllowed(false);
		tblOnline.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
		tblOnline.getTableHeader().setOpaque(true);
		tblOnline.getTableHeader().setBackground(new Color(45,45,45));
		tblOnline.getTableHeader().setForeground(Color.WHITE);
		tblOnline.getTableHeader().setResizingAllowed(false);
		
		tblOnline.setModel(model);
		tblBoards.setViewportView(tblOnline);
		
		JLabel lblStatusHead = new JLabel("Durum:");
		lblStatusHead.setForeground(new Color(255, 255, 255));
		lblStatusHead.setHorizontalAlignment(SwingConstants.LEFT);
		lblStatusHead.setFont(new Font("Rockwell", Font.PLAIN, 20));
		lblStatusHead.setBounds(701, 11, 68, 40);
		pnlMain.add(lblStatusHead);
		
		
		lblStatus.setForeground(new Color(255, 255, 255));
		lblStatus.setFont(new Font("Century Gothic", Font.PLAIN, 15));
		lblStatus.setBounds(701, 36, 98, 29);
		pnlMain.add(lblStatus);
		
		JLabel lblStatusIcon = new JLabel("");
		lblStatusIcon.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/connectionstatus.png")));
		lblStatusIcon.setBounds(651, 15, 40, 40);
		pnlMain.add(lblStatusIcon);
		
		
		
		lblPage.setForeground(new Color(255, 255, 255));
		lblPage.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 35));
		lblPage.setBounds(24, 36, 442, 47);
		pnlMain.add(lblPage);
		
		
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				connect();
			}
		});
		btnConnect.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/ba\u011Flan.png")));
		btnConnect.setBounds(674, 87, 125, 25);
		pnlMain.add(btnConnect);
		
		
		btnDisconnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(lblDosyaStatus.getText().matches("Dosya seçildi! Karþý tarafa aktarýlýyor...")) {
					Object[] choices = {"Evet","Hayýr"};
					int exit = JOptionPane.showOptionDialog(null, "Dosya aktarma iþlemi tamamlanmadý, emin misiniz?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, choices, 1);
					if(exit==0) {
						isSendingFile = false;
						try {
							Thread.sleep(500);
						} catch (Exception e2) {
							
						}
						
						System.gc();
						
						disconnect();
						
					}
				}else
					disconnect();
			}
		});
		btnDisconnect.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/baglant\u0131y\u0131 kes.png")));
		btnDisconnect.setBounds(674, 87, 125, 25);
		pnlMain.add(btnDisconnect);
		
		JPanel pnlMenu = new JPanel();
		pnlMenu.setBounds(0, 0, 80, 487);
		frmSmartboardControlPanel.getContentPane().add(pnlMenu);
		pnlMenu.setBackground(new Color(32,32,32));
		pnlMenu.setLayout(null);
		
		JLabel lblMainPage = new JLabel("");
		lblMainPage.setAlignmentY(0.0f);
		
		
		lblMainPage.setHorizontalAlignment(SwingConstants.CENTER);
		lblMainPage.setBackground(new Color(64, 64, 64));
		lblMainPage.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/main.png")));
		lblMainPage.setBounds(0, 50, 80, 46);
		pnlMenu.add(lblMainPage);
		lblMainPage.addMouseListener(new MouseAdapter() { 
			@Override
			public void mouseEntered(MouseEvent e) { //adding visual effects for main icons
				lblMainPage.setOpaque(true);
				lblMainPage.setBackground(new Color(45, 45, 45));

			}
			@Override
			public void mouseExited(MouseEvent e) {
				lblMainPage.setOpaque(false);
				lblMainPage.setBackground(new Color(32, 32, 32));
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				pnlChat.hide();
				pnlCommands.hide();
				tblBoards.show();
				lblPage.setText("Ana Sayfa");
			}
		});
		
		JLabel lblScreenObserver = new JLabel("");
		lblScreenObserver.setAlignmentY(0.0f);
		
		lblScreenObserver.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				try {
					if(!lblStatus.getText().matches("Baðlantý yok!")) {
						screen.frmObserver.show();
						if(!isObserving) {
							DataOutputStream out = new DataOutputStream(socketConnect.getOutputStream());
							out.writeUTF("Ekran izle");
							out.flush();
							
							int i = tblOnline.getSelectedRow();
							String ip = (String) tblOnline.getValueAt(i, 0);;
							
							
							GetScreen watch = new GetScreen(ip);
							new Thread(watch).start();
							isObserving = true;
						}
				
					}else
						JOptionPane.showMessageDialog(null, "Lütfen önce Akýllý tahtaya baðlanýn!", "Hata", JOptionPane.ERROR_MESSAGE);
	
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Baðlantýda hata oldu: "+e1.toString(), "Hata!", JOptionPane.ERROR_MESSAGE);
					
				} catch(NullPointerException e2) {
					JOptionPane.showMessageDialog(null, "Baðlantýda hata oldu: "+e2.toString(), "Hata!", JOptionPane.ERROR_MESSAGE);
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) { //adding visual effects for main icons
				lblScreenObserver.setOpaque(true);
				lblScreenObserver.setBackground(new Color(45, 45, 45));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				lblScreenObserver.setOpaque(false);
				lblScreenObserver.setBackground(new Color(32, 32, 32));
			}
		});
		lblScreenObserver.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/eye.png")));
		lblScreenObserver.setBounds(0, 150, 80, 46);
		pnlMenu.add(lblScreenObserver);
		
		JLabel lblChatPage = new JLabel("");
		lblChatPage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) { //adding visual effects for main icons
				lblChatPage.setOpaque(true);
				lblChatPage.setBackground(new Color(45,45,45));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				lblChatPage.setOpaque(false);
				lblChatPage.setBackground(new Color(32,32,32));
			}
			@Override
			public void mouseClicked(MouseEvent e) {			
				tblBoards.hide();
				pnlCommands.hide();
				pnlChat.show();
				lblPage.setText("Sohbet");
				if(!lblStatus.getText().matches("Baðlantý yok!")) {
					try {
						
						DataInputStream in = new DataInputStream(socketConnect.getInputStream());
						DataOutputStream out = new DataOutputStream(socketConnect.getOutputStream());
						out.writeUTF("chat");
						out.flush();
						
						
						if(!isChatting) { //starting new chat with table
							
							
							int i = tblOnline.getSelectedRow();
							String ip = (String) tblOnline.getValueAt(i, 0);
							
							socketChat = new Socket(ip,1235);
							
							
							RecieveMessage recieve = new RecieveMessage(socketConnect,in);
							new Thread(recieve).start();
							isChatting = true;
						}
						
						
					
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Chat baþlatýlamadý! " + e2.toString() , "Hata", JOptionPane.ERROR_MESSAGE);
				}
				}
				
				
					
			}
		});
		lblChatPage.setAlignmentY(0.0f);
		lblChatPage.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/chat.png")));
		lblChatPage.setBackground(new Color(64, 64, 64));
		lblChatPage.setBounds(0, 250, 80, 46);
		pnlMenu.add(lblChatPage);
		
		JLabel lblCommandPage = new JLabel("");
		lblCommandPage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lblCommandPage.setOpaque(true);
				lblCommandPage.setBackground(new Color(45,45,45));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				lblCommandPage.setOpaque(false);
				lblCommandPage.setBackground(new Color(32,32,32));
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				tblBoards.hide();
				pnlChat.hide();
				pnlCommands.show();
				lblPage.setText("Kontrol Paneli");
			}
		});
		lblCommandPage.setAlignmentY(0.0f);
		lblCommandPage.setIcon(new ImageIcon(controlPanel.class.getResource("/icons/admin.png")));
		lblCommandPage.setBackground(new Color(64, 64, 64));
		lblCommandPage.setBounds(0, 350, 80, 46);
		pnlMenu.add(lblCommandPage);
		
		OnlineCheck onlineCheck = new OnlineCheck();
		new Thread(onlineCheck).start();

	}
	
}
