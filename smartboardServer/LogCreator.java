package smartboardServer;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogCreator {

	public void create(String date, String ip, String name, String surname,  String action) {
		String path = System.getProperty("user.dir")+"\\Kay�tlar";
		
		File directory = new File(path);
		if(!directory.exists()) {
			directory.mkdir();
		}
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("DD.MM.YY");
		LocalDateTime now = LocalDateTime.now();
		String time = dtf.format(now);
		
		path = path + "\\"+time;
		
		directory = new File(path);
		// tarih ile olu�turulmu� txt dosyas� var m� kontrol et, yoksa yenisini olu�tur ve log tutmaya ba�la. Varsa �zerine yaz.
		if(!directory.exists()) {
			
		}
		
	}
}
