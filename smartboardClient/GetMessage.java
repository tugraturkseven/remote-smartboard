package smartboardClient;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GetMessage implements Runnable{

	ServerSocket server;
	
	public GetMessage() throws IOException {
	
		server = new ServerSocket(1235);
	}
	
	@Override
	public void run() {
		
		try {
			
			Socket socketChat = server.accept();
			DataInputStream in = new DataInputStream(socketChat.getInputStream());
			while(LoginPage.isChatting) {
				if(in.available()>0) {
					String message = in.readUTF(); //gets the message from inputstream
					LoginPage.chatScreen.txtChat.setText(LoginPage.chatScreen.txtChat.getText()+"\n   "+message);
				}
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
			server.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	}

}
