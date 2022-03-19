package smartboardServer;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogCreator {

	public void create(String date, String ip, String name, String surname,  String action) {
		String path = System.getProperty("user.dir")+"\\Kayýtlar";
		
		File directory = new File(path);
		if(!directory.exists()) {
			directory.mkdir();
		}
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("DD.MM.YY");
		LocalDateTime now = LocalDateTime.now();
		String time = dtf.format(now);
		
		path = path + "\\"+time;
		
		directory = new File(path);
		// tarih ile oluþturulmuþ txt dosyasý var mý kontrol et, yoksa yenisini oluþtur ve log tutmaya baþla. Varsa üzerine yaz.
		if(!directory.exists()) {
			
		}
		
	}
}
