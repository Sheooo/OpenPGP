package etf.openpgp.ml170674dca170545d;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class GenerateNewKeyPairFrame extends JFrame {
	private JFrame frame;
	private JTextField nameField;
	private JTextField mailField;
	private JComboBox rsaSizeBox; 
	private JTextField passPhraseField; 
	private JButton generateButton;

	public GenerateNewKeyPairFrame() {
		super("PGP");
		setupFrame();
	}

	private void setupFrame() {

		frame = this;
		frame.setLayout(new GridLayout(6, 2));

		frame.add(new JLabel("Name", JLabel.CENTER));
		
		frame.add(this.nameField = new JTextField());

		frame.add(new JLabel("Mail", JLabel.CENTER));
		frame.add(this.mailField = new JTextField());

		frame.add(new JLabel("RSA size", JLabel.CENTER));
		Integer elgSizes[] = { 1024, 2048, 4096 };
		frame.add(this.rsaSizeBox = new JComboBox<Integer>(elgSizes));

		frame.add(new JLabel("Passphrase", JLabel.CENTER));
		frame.add(this.passPhraseField = new JTextField());

		frame.add(new JLabel(""));
		this.generateButton = new JButton("Generate");
		this.generateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		frame.add(this.generateButton);

		frame.setSize(300, 200);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		setVisible(true);
	}
}
