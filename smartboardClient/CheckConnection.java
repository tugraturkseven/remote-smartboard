package smartboardClient;



import javax.swing.JFrame;
import java.awt.Window.Type;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;

public class CheckConnection implements Runnable{

	private JFrame frmConnectionCheck;
	JLabel countdown = new JLabel("Að'a baðlanmazsanýz 15 saniye sonra bilgisayar kapatýlacak!");
	int count = 15;

	public CheckConnection() {
		initialize();
	}

	private void initialize() {
		frmConnectionCheck = new JFrame();
		frmConnectionCheck.getContentPane().setForeground(Color.WHITE);
		frmConnectionCheck.getContentPane().setBackground(new Color(51, 51, 51));
		frmConnectionCheck.setTitle("Akýllý Tahta Kontrol Sistemi");
		frmConnectionCheck.setAlwaysOnTop(true);
		frmConnectionCheck.setEnabled(true);
		frmConnectionCheck.setResizable(false);
		frmConnectionCheck.setType(Type.UTILITY);
		frmConnectionCheck.setBounds(100, 100, 450, 110);
		frmConnectionCheck.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmConnectionCheck.getContentPane().setLayout(null);
		countdown.setForeground(Color.WHITE);
		
		
		countdown.setFont(new Font("Tahoma", Font.PLAIN, 15));
		countdown.setBounds(21, 27, 398, 26);
		frmConnectionCheck.getContentPane().add(countdown);
	}

	@Override
	public void run() {
		while(true) {
			boolean isConnected = true;
			try {
				
				InetAddress check = InetAddress.getLocalHost();
				String[] ip = check.toString().split("/");
				while(ip[1].matches("127.0.0.1")) { //checks if board connected or not
					isConnected = false;
					
					frmConnectionCheck.setVisible(true);
					check = InetAddress.getLocalHost();
					ip = check.toString().split("/"); //updates each second connection status
					countdown.setText("Að'a baðlanmazsanýz "+count +" saniye sonra bilgisayar kapatýlacak!");
					if(count==0)
						Runtime.getRuntime().exec("shutdown -s");
					else
						count--;
					
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						
					}
				}
				if(!isConnected) {
					//since connection is gone the admin panel will delete the online data, this part re-adds the data
					Match.Search(LoginPage.id);
					isConnected=true;
				}
				frmConnectionCheck.setVisible(false);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			} catch (UnknownHostException e) {
				
				
			} catch (IOException e1) {
				
				
			}
		}
		
	}
}
