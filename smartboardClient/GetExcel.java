package smartboardClient;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GetExcel implements Runnable{
	
	
	public GetExcel() {
		
	}
	
	@Override
	public void run() {
		
		while(true) {
			try {
				Socket socketExcel = new Socket("Tuðra",9876);
				DataOutputStream out = new DataOutputStream(socketExcel.getOutputStream());
				String datas = "ip"+"-"+"isim"+"-"+"soyisim"+"-"+"no"; // sends information the get database
				out.writeUTF(datas);
				out.flush();
				
				//gets file from admin
				File newFile = new File(System.getProperty("user.dir")+"\\config.ini");
				
				BufferedInputStream buffIn = new BufferedInputStream(socketExcel.getInputStream());
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				int nRead;
				byte[] buffer = new byte[4096];
				while((nRead = buffIn.read(buffer, 0, buffer.length))!=-1) {
					baos.write(buffer,0,nRead);
				}
				
				FileOutputStream fileOut = new FileOutputStream(newFile);
				fileOut.write(baos.toByteArray());
				fileOut.flush();
				fileOut.close();
				
				String configFile = System.getProperty("user.dir")+"\\config.ini";
				Path path = Paths.get(configFile);
				Files.setAttribute(path, "dos:hidden", true);
				
				socketExcel.close();
				
				break;
			} catch (Exception e) {
				
			}
		}
	}

}
