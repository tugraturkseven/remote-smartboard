package smartboardServer;


import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;



public class GetScreen implements Runnable{
	Socket socketScreen;
	String ip;
	BufferedInputStream buffIn;
	ByteArrayOutputStream baos;
	byte[] buffer;
	BufferedImage image;
	
	public GetScreen(String ip) {
		this.ip = ip;
	}
	
	public void cleanUp() {
		try {
			buffIn.close();
			baos.close();
			socketScreen.close();
			
			socketScreen = null;
			baos = null;
			buffIn = null;
			ip = null;
			buffer = null;
			image = null;
			
			System.gc();
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void run() {
		while(controlPanel.screen.frmObserver.isShowing()) {
			try {
				
				socketScreen = new Socket(ip, 1239); //connect to send screen port of board
				//get the image from input stream and handle it				
				buffIn = new BufferedInputStream(socketScreen.getInputStream());
				baos = new ByteArrayOutputStream();
				
				int nRead;
				buffer = new byte[4096];
				while((nRead = buffIn.read(buffer, 0, buffer.length))!= -1) {
					baos.write(buffer,0,nRead);
				}
				
				image = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
				int height = controlPanel.screen.frmObserver.getContentPane().getHeight(); //height
				int width = controlPanel.screen.frmObserver.getContentPane().getWidth(); //width
				
				if(height!=image.getHeight() || width!=image.getWidth()) { // fits the image if screen size smaller then image
					Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
					
					controlPanel.screen.label.setIcon(new ImageIcon(resizedImage));
					
					
					
				}else {

					controlPanel.screen.label.setIcon(new ImageIcon(image));
					
				}
					
				
				if(controlPanel.screen.saveScreen) { //saves the current screen of board to desktop of admin
					int row = controlPanel.tblOnline.getSelectedRow();
					String name = (String) controlPanel.model.getValueAt(row, 1);
					String surname = (String) controlPanel.model.getValueAt(row, 2);
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH.mm.ss");
					LocalDateTime now = LocalDateTime.now();
					String time = dtf.format(now);
					
					String identify = name+" "+surname+" "+time;
					String filePath = System.getProperty("user.home")+"\\Desktop\\"+identify+".png";
					File save = new File(filePath);
					ImageIO.write(image, "png",save);
					controlPanel.screen.saveScreen=false;
				}
			Thread.sleep(500);
			} catch (Exception e) {
				try {
					socketScreen.close();
					controlPanel.screen.frmObserver.hide();
					if(!controlPanel.lblStatus.getText().matches("Baðlantý yok!"))
						JOptionPane.showMessageDialog(null, "Ekran izlerken hata oluþtu.", "Hata!", JOptionPane.ERROR_MESSAGE);
					
				} catch (IOException e1) {
					
				}
				
			}
			
		}
		
		cleanUp();
	}

}
