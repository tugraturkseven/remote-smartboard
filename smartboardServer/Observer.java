package smartboardServer;


import java.awt.Toolkit;

import javax.swing.JFrame;

import javax.swing.JLabel;

import javax.swing.ImageIcon;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import javax.swing.JPopupMenu;
import java.awt.Component;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Observer{
	JLabel label = new JLabel();
	
	JFrame frmObserver;
	boolean saveScreen = false;

	public Observer() {
		initialize();
	}


	private void initialize() {
		frmObserver = new JFrame();
		frmObserver.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				controlPanel.isObserving = false;
				
			}
		});
		frmObserver.setIconImage(Toolkit.getDefaultToolkit().getImage(Observer.class.getResource("/icons/atom.png")));
		frmObserver.setTitle("Ekran Ýzleme Paneli");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frmObserver.setBounds(100, 100, screenSize.width, screenSize.height);
		frmObserver.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frmObserver.getContentPane().setLayout(new BorderLayout(0, 0));
		
		frmObserver.getContentPane().add(label,BorderLayout.CENTER);
		
		JPopupMenu popupMenu = new JPopupMenu();
		
		
		JMenuItem takeScreenshot = new JMenuItem("Ekran Görüntüsü Al");
		takeScreenshot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				controlPanel.screen.saveScreen = true;
			}
		});
		
		takeScreenshot.setIcon(new ImageIcon(Observer.class.getResource("/icons/screenshot.png")));
		popupMenu.add(takeScreenshot);
		
		addPopup(label, popupMenu);
	}

	private  void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
					if(e.getButton()==3)
						showMenu(e);
				
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
			
		});
	}
}
