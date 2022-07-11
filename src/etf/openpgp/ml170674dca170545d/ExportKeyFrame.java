package etf.openpgp.ml170674dca170545d;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;

import javafx.stage.DirectoryChooser;
import sun.swing.FilePane;



public class ExportKeyFrame extends JFrame implements ActionListener{
		private JFrame frame;
	    private MainFrame mainFrame;
	    private Object exportKeyRing;
	    private ExportKeyRingType exportKeyRingType;
	    private CheckboxGroup exportAsCheckboxGroup = new CheckboxGroup();
	    private JFileChooser directoryChooser;
	    private JTextField importFilePath;
	    private JTextField fileName;
	    private JButton exportButton;
	    private JLabel errorMessage;
	    
	    enum ExportKeyRingType {
	    	PUBLIC,
	    	SECRET
	    }
		
	    public ExportKeyFrame(Object exportKeyRing) {
	    	super("Export key");
	    	this.exportKeyRing = exportKeyRing;
	    	this.exportButton = new JButton("Export key");
	    	this.exportButton.addActionListener(this);
			this.frame = this;
			this.frame.setLayout(new BorderLayout());
		    this.errorMessage = new JLabel("Please fill out all fields");
			this.errorMessage.setForeground(Color.RED);
			this.errorMessage.setVisible(false);
	    	if (exportKeyRing instanceof PGPPublicKeyRing) {
	    		this.exportKeyRingType = ExportKeyRingType.PUBLIC;
	    		initPublicFrame();
	    	} else {
	    		this.exportKeyRingType = ExportKeyRingType.SECRET;
	    		initSecretFrame();
	    	}	
		}
	    
		private void initSecretFrame() {
			frame = this;
			frame.setLayout(new BorderLayout());
			PGPSecretKeyRing secretKeyRing = (PGPSecretKeyRing) exportKeyRing;
	    	Iterator<String> userIDs = secretKeyRing.getSecretKey().getUserIDs();
        	String userID = userIDs.next();
        	String[] nameAndEmail = userID.split("<");
        	String name = nameAndEmail[0] ;
			String email = nameAndEmail[1].replace(">", "");
    		String keyID = Long.toHexString(secretKeyRing.getSecretKey().getKeyID()).toUpperCase();
			StringBuilder stringBuilder = new StringBuilder(keyID);
			stringBuilder.insert(4, " ")
						 .insert(9, " ")
						 .insert(14, " ");
			String formatedKeyId = stringBuilder.toString();
			
			JPanel exportInfoPanel = new JPanel();
			exportInfoPanel.setLayout(new GridLayout(6,2));
			

			exportInfoPanel.add(new JLabel("Exporting: ", JLabel.CENTER));
			exportInfoPanel.add(new JLabel("Secret key", JLabel.LEFT));

			exportInfoPanel.add(new JLabel("Name: ", JLabel.CENTER));
			exportInfoPanel.add(new JLabel(name, JLabel.LEFT));
			
			exportInfoPanel.add(new JLabel("Email: ", JLabel.CENTER));
			exportInfoPanel.add(new JLabel(email, JLabel.LEFT));
			
			exportInfoPanel.add(new JLabel("Key ID: ", JLabel.CENTER));
			exportInfoPanel.add(new JLabel(formatedKeyId, JLabel.LEFT));

			exportInfoPanel.add(new JLabel("Export as: ", JLabel.CENTER));
			JPanel checkBoxPanel = new JPanel();
			checkBoxPanel.add(new Checkbox("Public", 
										   exportAsCheckboxGroup,
										   true));
			checkBoxPanel.add(new Checkbox("Secret", 
											exportAsCheckboxGroup,
											false));
			exportInfoPanel.add(checkBoxPanel);
			frame.add(exportInfoPanel, BorderLayout.CENTER);
			
			JPanel savingToDirectoryPanel = new JPanel();
			savingToDirectoryPanel.setLayout(new GridLayout(1, 3));
	
			savingToDirectoryPanel.add(new JLabel("Save file to directory: "));
			
		    importFilePath = new JTextField("");
	        importFilePath.setEnabled(false);
			savingToDirectoryPanel.add(importFilePath);
			
	        
			JPanel southPanel = new JPanel();
			southPanel.setLayout(new GridLayout(4,1));
			
	        JButton chooseDirectoryButton = new JButton("Choose directory");
	        chooseDirectoryButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					directoryChooser = new JFileChooser();
					directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int res = directoryChooser.showOpenDialog(mainFrame);
					if(res == JFileChooser.APPROVE_OPTION) {
						File f = directoryChooser.getSelectedFile();
						String importPathString = f.getPath();
						importFilePath.setText(importPathString);
	
					}
				}
			});
	        savingToDirectoryPanel.add(chooseDirectoryButton);
	        
	        JPanel fileNamePanel = new JPanel();
	        fileNamePanel.setLayout(new GridLayout(1, 2));
	          
	        fileNamePanel.add(new JLabel("File name: "));
	        fileName = new JTextField("");
	        fileNamePanel.add(fileName);
	        
	        southPanel.add(savingToDirectoryPanel);
	        southPanel.add(fileNamePanel);
	        southPanel.add(exportButton);
	        southPanel.add(errorMessage);
	        frame.add(southPanel, BorderLayout.SOUTH);
	        
			frame.setSize(300, 500);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
		
		private void initPublicFrame() {
			PGPPublicKeyRing secretKeyRing = (PGPPublicKeyRing) exportKeyRing;
	    	Iterator<String> userIDs = secretKeyRing.getPublicKey().getUserIDs();
        	String userID = userIDs.next();
        	String[] nameAndEmail = userID.split("<");
        	String name = nameAndEmail[0] ;
			String email = nameAndEmail[1].replace(">", "");
    		String keyID = Long.toHexString(secretKeyRing.getPublicKey().getKeyID()).toUpperCase();
			StringBuilder stringBuilder = new StringBuilder(keyID);
			stringBuilder.insert(4, " ")
						 .insert(9, " ")
						 .insert(14, " ");
			String formatedKeyId = stringBuilder.toString();
			
			JPanel exportInfoPanel = new JPanel();
			exportInfoPanel.setLayout(new GridLayout(6,2));
			

			exportInfoPanel.add(new JLabel("Exporting: ", JLabel.CENTER));
			exportInfoPanel.add(new JLabel("Public key", JLabel.LEFT));

			exportInfoPanel.add(new JLabel("Name: ", JLabel.CENTER));
			exportInfoPanel.add(new JLabel(name, JLabel.LEFT));
			
			exportInfoPanel.add(new JLabel("Email: ", JLabel.CENTER));
			exportInfoPanel.add(new JLabel(email, JLabel.LEFT));
			
			exportInfoPanel.add(new JLabel("Key ID: ", JLabel.CENTER));
			exportInfoPanel.add(new JLabel(formatedKeyId, JLabel.LEFT));
			frame.add(exportInfoPanel, BorderLayout.CENTER);
			
			JPanel savingToDirectoryPanel = new JPanel();
			savingToDirectoryPanel.setLayout(new GridLayout(1, 3));
	
			savingToDirectoryPanel.add(new JLabel("Save file to directory: "));
			
		    importFilePath = new JTextField("");
	        importFilePath.setEnabled(false);
			savingToDirectoryPanel.add(importFilePath);
			
	        
			JPanel southPanel = new JPanel();
			southPanel.setLayout(new GridLayout(4,1));
			
	        JButton chooseDirectoryButton = new JButton("Choose directory");
	        chooseDirectoryButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					directoryChooser = new JFileChooser();
					directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int res = directoryChooser.showOpenDialog(mainFrame);
					if(res == JFileChooser.APPROVE_OPTION) {
						File f = directoryChooser.getSelectedFile();
						String importPathString = f.getPath(); 
						importFilePath.setText(importPathString);
	
					}
				}
			});
	        savingToDirectoryPanel.add(chooseDirectoryButton);
	        
	        JPanel fileNamePanel = new JPanel();
	        fileNamePanel.setLayout(new GridLayout(1, 2));
	          
	        fileNamePanel.add(new JLabel("File name: "));
	        fileName = new JTextField("");
	        fileNamePanel.add(fileName);
	        
	        southPanel.add(savingToDirectoryPanel);
	        southPanel.add(fileNamePanel);
	        southPanel.add(exportButton); 
	        southPanel.add(errorMessage);
	        frame.add(southPanel, BorderLayout.SOUTH);
	   
			frame.setSize(300, 500);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
	    @Override
		public void actionPerformed(ActionEvent e) { 
			String fileName = this.fileName.getText();
	    	
			if(!fileName.equalsIgnoreCase("")) {
				String filePath = getFilePath();
				try {
					ArmoredOutputStream file = createFileOutputStream(filePath, fileName);
						if(exportKeyRingType == ExportKeyRingType.PUBLIC) {
							PGPPublicKeyRing publicKeyRing = (PGPPublicKeyRing) exportKeyRing;
							publicKeyRing.encode(file);
						} else {
							String selectedCheckBoxLabel = exportAsCheckboxGroup.getSelectedCheckbox().getLabel();
							PGPSecretKeyRing secretKeyRing = (PGPSecretKeyRing) exportKeyRing;
							
							if(selectedCheckBoxLabel.equalsIgnoreCase("Public")) {
								Iterator<PGPPublicKey> publicKeyIterator = secretKeyRing.getPublicKeys();
								ArrayList<PGPPublicKey> publicKeys = new ArrayList<PGPPublicKey>();
								
								while(publicKeyIterator.hasNext()) {
									publicKeys.add(publicKeyIterator.next());							
								}
								
								if(!publicKeys.isEmpty()) {
			                        PGPPublicKeyRing publicKeyRing = new PGPPublicKeyRing(publicKeys);
			                        publicKeyRing.encode(file);
								} else {
									errorMessage.setText("Error while exporting public key ring");
									errorMessage.setVisible(true);
									return ;
								}
							} else {
								secretKeyRing.encode(file);
							}
					}
					file.close();
					this.dispose();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				errorMessage.setText("Please enter file name");
				errorMessage.setVisible(true);
			}	
	    }
	    
	    private String getFilePath() {
	    	String fileDir = this.importFilePath.getText();
	    	if (fileDir.equalsIgnoreCase("")) {
	    		fileDir = "src/ExportedKeys/";
	    	}
	    	return fileDir;
	    }
	    
	    private String getSelectedCheckboxLabel() {
	    	return  exportAsCheckboxGroup.getSelectedCheckbox().getLabel();
	    }
	    
	    private ArmoredOutputStream createFileOutputStream(String filePath, String fileName) {
	    		try {
	    			return new ArmoredOutputStream(new FileOutputStream(new File(filePath + fileName)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return null;
	    }
}

