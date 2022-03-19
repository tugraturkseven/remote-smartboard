package smartboardClient;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

public class SendScreen implements Runnable {

	ServerSocket serverScreen;
	Socket socketScreen;
	Robot r;
	Rectangle capture;
	BufferedImage image;
	ByteArrayOutputStream baos;
	ByteArrayInputStream bais;
	BufferedInputStream reader;
	BufferedOutputStream buffOut;
	byte[] buffer;
	
	public SendScreen() {
		try {
			serverScreen = new ServerSocket(1239);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cleanUp() {
		try {
			serverScreen.close();
			socketScreen.close();
			baos.close();
			bais.close();
			reader.close();
			buffOut.close();
			
			serverScreen = null;
			socketScreen = null;
			baos = null;
			bais = null;
			reader = null;
			buffOut = null;
			r = null;
			image = null;
			capture = null;
			buffer = null;
			
			System.gc();
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void run() {
			
			while(LoginPage.sendScreen) {
				try {
					
					socketScreen = serverScreen.accept();
					
					r = new Robot();
					//takes screenshot
					capture =	new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()); 
					//creates a bufferedimage of screenshot
					image = r.createScreenCapture(capture); 
					
					baos = new ByteArrayOutputStream(); 
					//writes image into bytearray
					ImageIO.write(image,"jpg",baos);
					
					bais = new ByteArrayInputStream(baos.toByteArray());
					
					reader = new BufferedInputStream(bais);
					
					buffOut = new BufferedOutputStream(socketScreen.getOutputStream());
					
					buffer = new byte[4096];
					int bytesRead;
					
					while((bytesRead = reader.read(buffer))!=-1) {
						buffOut.write(buffer, 0, bytesRead);
						
					}
					buffOut.flush();
					buffOut.close();
					reader.close();
					
					Thread.sleep(500);
				} catch (Exception e) {
					if(!e.getMessage().matches("null"))
						LoginPage.sendScreen = false;
				}
				
				
			}
			cleanUp();
		
		
	}
	
	

}
