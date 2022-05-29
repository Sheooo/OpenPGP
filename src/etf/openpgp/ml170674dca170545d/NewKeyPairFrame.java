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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

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
	
	private static File publicKeyRingFile;
    private static File secretKeyRingFile;
    private static String path;
    
	public NewKeyPairFrame() {
		super("PGP");
		initPath();
		initPublicKeyRing();
		initSecretKeyRing();
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

	private void initPublicKeyRing() {
		try {
			publicKeyRingFile = new File(path + "/src/publicKeyRing.asc");
			publicKeyRingCollection = new PGPPublicKeyRingCollection(new ArmoredInputStream(new FileInputStream(publicKeyRingFile)), new BcKeyFingerprintCalculator());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PGPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Security.addProvider(new BouncyCastleProvider());
	}
	
	private void initSecretKeyRing() {
		try {
			secretKeyRingFile = new File(path + "/src/secretKeyRing.asc");
			secretKeyRingCollection = new PGPSecretKeyRingCollection(new ArmoredInputStream(new FileInputStream(secretKeyRingFile)), new BcKeyFingerprintCalculator());
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PGPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private void initPath() {
		Path root = Paths.get(".").normalize().toAbsolutePath();
		path = root.toString();
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
			String passPhrase = passPhraseField.getText();
			
			new NewKeyPair(name, 
						   email, 
						   rsaSize, 
						   passPhrase,
						   publicKeyRingCollection,
						   secretKeyRingCollection,
						   publicKeyRingFile,
						   secretKeyRingFile);
		} else {
			errorMessage.setVisible(true);
			

			

		}
	}	
}
