package etf.openpgp.ml170674dca170545d;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


public class MainFrame extends JFrame{
	private JFrame frame;
	
	
	public MainFrame() {
		super("OpenPGP");
		
		setupMainFrame();
		setupMenuBar();
		
		frame.setVisible(true);
	}
	
	private void setupMainFrame() {
		frame = this;
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
	}
	
	private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem generateNewKeyPair = new JMenuItem("Generate new key pair");
        generateNewKeyPair.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new GenerateNewKeyPairFrame();
			}
		});
        menu.add(generateNewKeyPair);
        menuBar.add(menu);
        frame.add(menuBar, BorderLayout.NORTH);
	}
}
