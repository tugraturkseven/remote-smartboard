package smartboardClient;

import java.io.DataOutputStream;
import java.net.Socket;

public class SendInfo implements Runnable{
	String datas;
	
	
	public SendInfo( String datas) {
		
		this.datas = datas;
		
	}
	@Override
	public void run() {
		while(true) {
			try { //sending online status
				Socket socketInfo = new Socket("Tuðra",9876);
				DataOutputStream dos= new DataOutputStream(socketInfo.getOutputStream());
				
				dos.writeUTF(datas);
				dos.flush();
				dos.close();
				
				socketInfo.close();
				
				
				
				
				break;
			} catch (Exception e) {
				
			}
			
		}
		
	}

}
