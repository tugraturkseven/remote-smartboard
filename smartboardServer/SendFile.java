package smartboardServer;

import java.awt.FileDialog;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

import javax.swing.JFrame;


public class SendFile implements Runnable {
	String ip;
	JFrame jframe = new JFrame();
	public SendFile(String ip) {
		this.ip = ip;
		
	}
	@Override
	public void run() {
		while(controlPanel.isSendingFile) {
			
			// TO DO dosyay� byte byte b�l�p g�ndermeyi dene.
			try {
				
				FileDialog fd = new FileDialog(jframe, "Choose a file", FileDialog.LOAD);
				
				
				fd.setDirectory("C:\\");
				
				fd.setVisible(true);
				
				if(fd.getDirectory()!=null) { // if admin cancels the selection
					
					
					String fileName = fd.getDirectory()+fd.getFile();
					File file = new File(fileName);
					double megaBytes = file.length()/(1024*1024);
					
					
					if(megaBytes<1000.0) {
						DataOutputStream fileSender = new DataOutputStream(controlPanel.socketConnect.getOutputStream());
						fileSender.writeUTF("dosya");
						fileSender.flush();
		
						Socket socketFile = new Socket(ip, 9999);
						controlPanel.lblDosyaStatus.setText("Dosya se�ildi! Kar�� tarafa aktar�l�yor...");
						controlPanel.btnDosya.hide();
						
						
						FileInputStream newFile = new FileInputStream(fileName);
						
						BufferedInputStream in = new BufferedInputStream(newFile);
						DataOutputStream dataOut = new DataOutputStream(socketFile.getOutputStream());
							
						dataOut.writeUTF(fd.getFile()); //sends the file's name to create it properly on the other side
						dataOut.flush();
								
						BufferedOutputStream out = new BufferedOutputStream(socketFile.getOutputStream());

								
						int nRead;
						byte[] buffer = new byte[4096];
						while((nRead = in.read(buffer))!=-1) {
							out.write(buffer,0,nRead);
							out.flush();
						}
								
						
						out.close();
						in.close();
						socketFile.close();
						
						controlPanel.lblDosyaStatus.setText("Dosya ba�ar�yla aktar�ld�!..");
						controlPanel.isSendingFile=false;
						
					}else
						controlPanel.lblDosyaStatus.setText("L�tfen 1 GB'dan d���k boyutlu dosyalar se�iniz.");
						controlPanel.btnDosya.show();
						
				}
			} catch (Exception e) {
				e.printStackTrace();
				
				controlPanel.lblDosyaStatus.setText("Aktarma ba�ar�s�z!");
				controlPanel.isSendingFile = false;
				
			}
		}
		
		
	}
	

}
