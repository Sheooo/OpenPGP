package etf.openpgp.ml170674dca170545d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;

public class NewKeyPairFrame extends JFrame implements ActionListener{
	private JFrame frame;
	private JTextField nameField;
	private JTextField mailField;
	private JComboBox<Integer> rsaSizeBox; 
	private JTextField passPhraseField; 
	private JButton generateButton;
	private JLabel errorMessage;
	private PGPPublicKeyRingCollection publicKeyRingCollection;
	private PGPSecretKeyRingCollection secretKeyRingCollection;

    private MainFrame mainFrame;
    
	public NewKeyPairFrame(MainFrame mainFrame,
						   PGPPublicKeyRingCollection publicKeyRingCollection,
						   PGPSecretKeyRingCollection secretKeyRingCollection) {
		super("PGP");
		this.mainFrame = mainFrame;
		
		initPublicKeyRing(publicKeyRingCollection);
		initSecretKeyRing(secretKeyRingCollection);
		initFrame();
	}

	private void initFrame() {
		frame = this;
		frame.setLayout(new GridLayout(7,2));

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
		this.generateButton.addActionListener(this);
		frame.add(this.generateButton);
		frame.add(new JLabel(""));
		
	    errorMessage = new JLabel("Please fill out all fields");
		errorMessage.setForeground(Color.RED);
		errorMessage.setVisible(false);
		frame.add(errorMessage);
		
		frame.setSize(300, 200);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	


	private void initPublicKeyRing(PGPPublicKeyRingCollection publicKeyRingCollection) {	 
			this.publicKeyRingCollection = publicKeyRingCollection;
	}
	
	private void initSecretKeyRing(PGPSecretKeyRingCollection secretKeyRingCollection) {
			this.secretKeyRingCollection = secretKeyRingCollection;
	
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(!nameField.getText().isEmpty() &&
		   !mailField.getText().isEmpty() &&
		   !passPhraseField.getText().isEmpty()) {
			errorMessage.setVisible(false);
			
			String name = nameField.getText();
			String email = mailField.getText();
			int rsaSize = (int)rsaSizeBox.getSelectedItem();
			char[] passPhrase = passPhraseField.getText().toCharArray();
			
			Object[] keyRingCollectionsNewKeyPair = NewKeyPair.generateNewKeyPair(name, 
						   email, 
						   rsaSize, 
						   passPhrase,
						   publicKeyRingCollection,
						   secretKeyRingCollection);
			mainFrame.setPublicKeyRingColletion((PGPPublicKeyRingCollection)keyRingCollectionsNewKeyPair[0]);
			mainFrame.setSecretKeyRingColletion((PGPSecretKeyRingCollection)keyRingCollectionsNewKeyPair[1]);
			mainFrame.populateKeysTableIfNeeded();
		} else {
			errorMessage.setVisible(true);
			

			

		}
	}	
}
