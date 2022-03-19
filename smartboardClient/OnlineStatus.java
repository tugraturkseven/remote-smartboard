package smartboardClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class OnlineStatus implements Runnable {

	public OnlineStatus() {
		
	}
	
	@Override
	public void run() {
		try {
			while(true) { //accepts conections from admin panel for checking online status
				ServerSocket serverOnline = new ServerSocket(2828);
				serverOnline.setReuseAddress(true);
				Socket socket = serverOnline.accept();
				socket.close();
				serverOnline.close();
				Thread.sleep(100);
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		
	}

}
