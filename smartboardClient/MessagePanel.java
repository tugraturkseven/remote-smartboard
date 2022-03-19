package smartboardClient;

import javax.swing.JFrame;
import java.awt.Window.Type;
import java.awt.Color;
import javax.swing.JLabel;

import javax.swing.ImageIcon;
import java.awt.Font;

public class MessagePanel implements Runnable{

	private JFrame frame;
	String type;
	String message;

	public MessagePanel(String type, String message) {
		this.type = type;
		this.message = message;
		
	}

	
	/**
	 * @wbp.parser.entryPoint
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setAlwaysOnTop(true);
		frame.getContentPane().setBackground(new Color(40, 40, 40));
		frame.getContentPane().setLayout(null);
		
		JLabel lblWarning = new JLabel("");
		lblWarning.setBounds(20, 0, 40, 79);
		lblWarning.setIcon(new ImageIcon(MessagePanel.class.getResource("/icons/critical40.png")));
		frame.getContentPane().add(lblWarning);
		
		JLabel lblMessage = new JLabel("mesaj");
		lblMessage.setForeground(new Color(255, 255, 255));
		lblMessage.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblMessage.setBounds(70, 29, 0, 0);
		frame.getContentPane().add(lblMessage);
		
		JLabel lblInformation = new JLabel("");
		lblInformation.setIcon(new ImageIcon(MessagePanel.class.getResource("/icons/information40.png")));
		lblInformation.setBounds(20, 0, 40, 79);
		frame.getContentPane().add(lblInformation);
		
		if(type.matches("information")) {
			lblWarning.hide();
			
		}else
			lblInformation.hide();
		
		
		lblMessage.setText(message);
		int width = (int) lblMessage.getPreferredSize().getWidth();
		int height = (int) lblMessage.getPreferredSize().getHeight();
		int x = lblMessage.getX();
		int y = lblMessage.getY();
		
		lblMessage.setBounds(x, y, width, height);
		
		frame.setTitle("Müdürden mesaj var!");
		
		int expand = x+width+50;
		
		frame.setType(Type.UTILITY);
		if(expand>180)
		frame.setBounds(100, 100, expand , 113);
		else
			frame.setBounds(100, 100, 180 , 113);
		frame.setResizable(false);
		
	}


	@Override
	public void run() {
		initialize();
		frame.setLocationRelativeTo(null);
		frame.show();
		
		
		int i = 0;
		
		while(i<10) {
			try {
				Thread.sleep(1000);
				i++;
			} catch (Exception e) {
				
			}
		}
		frame.hide();
		frame = null;
		System.gc();
	}
}
