package etf.openpgp.ml170674dca170545d;

import javax.swing.JFrame;

public class MainFrame extends JFrame{
	private JFrame frame;
	
	public MainFrame() {
		super("OpenPGP");
		
		setupMainFrame();
	}
	
	private void setupMainFrame() {
		frame = this;
		frame.setBounds(450, 200, 600, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
