package smartboardClient;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.swing.JOptionPane;





public class Match {

	public static void Search(String id) {
		

		try {
			Thread.sleep(1000); //thread waits for initialization of loginComplete method
			
			FileInputStream fileRead = new FileInputStream("C:\\Users\\tugra\\java-projects\\smartboardClient\\config.ini");
			ObjectInputStream in = new ObjectInputStream(fileRead);
			
			@SuppressWarnings("unchecked")
			ArrayList<String> newList = (ArrayList<String>) in.readObject();
			in.close();
			//reads and creates a ArrayList to keep data
			for (int i = 0; i < newList.size(); i++) {
				String read = newList.get(i);
				char[] decrypt = read.toCharArray();
				int key = 5;
				
				for (int j = 0; j < decrypt.length; j++) {
					decrypt[j] -=key; //decrypts the encrypted data
				}
				
				String data = new String(decrypt);
				newList.set(i, data);
				
			}
			
			for (int i = 0; i < newList.size(); i++) {
				String[] data = newList.get(i).split("-");
				if(data[0].matches(id)) { //checks if id number matches any
					
					LoginPage.wait = false;
					InetAddress host = InetAddress.getLocalHost();
	
					String userName = data[1];
					
					String surName = data[2];
					String datas = host.getHostAddress()+"-"+userName+"-"+surName+"-"+"yes";
				
					SendInfo send = new SendInfo(datas);
					new Thread(send).start();
					LoginPage.id = id;
					LoginPage.window.loginComplete();
					break;
				}
				if(i==newList.size()-1&&!data[i].matches(id)) { // if end of the data and still not matched
					
						JOptionPane.showMessageDialog(null, "TC Kimlik eþleþmedi!", "Hatalý giriþ!", JOptionPane.ERROR_MESSAGE);
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
