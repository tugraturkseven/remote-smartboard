package smartboardServer;


import java.net.Socket;

import javax.swing.JOptionPane;

public class OnlineCheck implements Runnable{

	public OnlineCheck() {
		
	}
	
	@Override
	public void run() {
		while(true) {
			int length = controlPanel.model.getRowCount();
			if(length>0) {
				for (int i = 0; i < length; i++) {
					String ip = (String) controlPanel.tblOnline.getValueAt(i, 0); //gets all online board IP one by one
					if(!ip.isEmpty()) {
						try {
							Socket socketOnline = new Socket(ip, 2828); //connects to check is board online
							socketOnline.close();
						} catch (Exception e) {
							// if its not online delete it's datas
							int index = controlPanel.tblOnline.getSelectedRow();
							if(index!=-1) {
								boolean matches = ip.matches((String)controlPanel.tblOnline.getValueAt(index, 0));
								boolean connected = controlPanel.lblStatus.getText().matches("Baðlandý!..");
								try {
									if(matches&&connected) { //if connected board is offline
										
										controlPanel.socketConnect.close();
										if(controlPanel.isObserving)
											controlPanel.screen.frmObserver.hide();
											controlPanel.isObserving = false;
										if(controlPanel.isChatting) {
											controlPanel.socketChat.close();
											controlPanel.txtChat.setText("");
											controlPanel.isChatting = false;
										}
											
										if(!controlPanel.btnDosya.isShowing()) {
											controlPanel.btnDosya.show();
											
										}
										controlPanel.txtURL.setText("URL Giriniz...");
										controlPanel.txtPopupMessage.setText("Mesaj Giriniz...");
										controlPanel.lblDosyaStatus.setText("Dosya seçimi için bekleniyor...");
										controlPanel.btnDisconnect.hide();
										controlPanel.btnConnect.show();
										controlPanel.lblStatus.setText("Baðlantý yok!");
										controlPanel.tblOnline.setEnabled(true);
										controlPanel.rdbShutdown.setSelected(true);
										controlPanel.rdbInformation.setSelected(true);
										
										JOptionPane.showMessageDialog(null, "Baðlý olduðunuz akýllý tahta kapatýldý.", "", JOptionPane.INFORMATION_MESSAGE);
										
									}
									}catch(Exception e1) {
										e1.printStackTrace();
									}
							}
												
							if(controlPanel.model.getRowCount()<=13) { //adds empty rows to table
								controlPanel.model.removeRow(i);
								Object[] addRow = new Object[3];
								addRow[0] = "";
								addRow[1] = "";
								addRow[2] = "";
								
								controlPanel.model.addRow(addRow);

		
							}else
								controlPanel.model.removeRow(i);
							

						}
						
					}
				}
			}
			
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				
			}
			
		}
		
	}

}
