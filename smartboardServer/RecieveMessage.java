package smartboardServer;

import java.io.DataInputStream;

import java.net.Socket;

import javax.swing.JOptionPane;



public class RecieveMessage implements Runnable {
	Socket socket;
	DataInputStream in;
	
	public RecieveMessage(Socket socket, DataInputStream in) {
		this.socket=socket;
		this.in=in;
		
	}

	@Override
	public void run() {
		
		int i = controlPanel.tblOnline.getSelectedRow();
		String student = (String) controlPanel.tblOnline.getValueAt(i, 1);
		while(controlPanel.isChatting) {
			
			try {
				if(in.available()!=0) {
					String message = in.readUTF();
					
						if(controlPanel.txtChat.getText().isEmpty())
							controlPanel.txtChat.setText("\n   "+student+": "+message);
						else
						 controlPanel.txtChat.setText(controlPanel.txtChat.getText()+"\n   "+student+": "+message);
					
						
				}
			} catch (Exception e2) {
				if(controlPanel.isChatting)
					JOptionPane.showMessageDialog(null, "Mesaj alýnamadý. "+e2.toString(), "Hata", JOptionPane.INFORMATION_MESSAGE);
			}
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				
			}
			
		}
		
	}

}
