package smartboardClient;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.net.ServerSocket;
import java.net.Socket;


public class GetFile implements Runnable {
	
	static ServerSocket server;
	static Socket socketFile;
	static DataInputStream in;
	static String file;
	static BufferedInputStream buffIn;
	static ByteArrayOutputStream baos;
	static byte[] buffer;
	static FileOutputStream fileOut;
	
	
	static boolean isCleaned;
	public GetFile() {
		
	}
	
	
	public static void cleanUp() {
			if(!isCleaned) {
				try {
					
					server.close();
					socketFile.close();
					baos.close();
					
					buffIn.close();
					in.close();
					
					
					
					fileOut = null;
					baos = null;
					buffIn = null;
					in = null;
					socketFile = null;
					server = null;
					buffer = null;
					System.gc();
					System.runFinalization();
					
					
					isCleaned = true;
				} catch (Exception e) {
					e.printStackTrace();
					LoginPage.isGettingFile = false;
				}
			
			}
			
		
	}
	
	@Override
	public void run() {
			
				try { //gets file from admin		
					boolean go = true;
					isCleaned = false;
					
					server = new ServerSocket(9999);
					socketFile = server.accept();
					in = new DataInputStream(socketFile.getInputStream());
					file = in.readUTF();
					
					String filePath = System.getProperty("user.home")+"\\Desktop\\" + file;
					File newFile = new File(filePath);
					
					buffIn = new BufferedInputStream(socketFile.getInputStream());
					baos = new ByteArrayOutputStream();
					
					
					int nRead;
					buffer = new byte[4096];
					try {
						
						while((nRead = in.read(buffer))!=-1) {
								baos.write(buffer,0,nRead);
								
						}
						
					} catch (Exception e) {
						e.printStackTrace();						
						go = false;
					}
					
					if(go) {
						fileOut = new FileOutputStream(newFile);
						fileOut.write(baos.toByteArray());
						fileOut.flush();
						fileOut.close();
					}
					
					cleanUp();
					LoginPage.isGettingFile = false;
				} catch (Exception e) {
					e.printStackTrace();
					
					cleanUp();
				}
			}
				
			
	
	}


