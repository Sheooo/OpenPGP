package etf.openpgp.ml170674dca170545d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyRing;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;


public class MainFrame extends JFrame{
	private JFrame frame;
	private JTable keysTable;
	private DefaultTableModel tableModel;
	private PGPPublicKeyRingCollection publicKeyRingCollection; 
	private PGPSecretKeyRingCollection secretKeyRingCollection;
    public static final String[] columnNames = {"Name",
												"Email",
												"Key ID",
												"Type"};
    private JPanel p;
    
	public MainFrame() {
		super("OpenPGP");
		
		try {
			this.publicKeyRingCollection = new PGPPublicKeyRingCollection(new ArrayList<PGPPublicKeyRing>());
			this.secretKeyRingCollection = new PGPSecretKeyRingCollection(new ArrayList<PGPSecretKeyRing>());
		} catch (IOException | PGPException e) {
			e.printStackTrace();
		}
		
		setupMainFrame();
		setupMenuBar();
		importPublicKeyRingsFromFile("src/OnLoadFiles/public/publicKeyRing.asc");
		importSecretKeyRingsFromFile("src/OnLoadFiles/secret/secretKeyRing.asc");
		setupKeysTable();
		populateKeysTableIfNeeded();
		
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
        MainFrame mainFrame = this;
        generateNewKeyPair.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new NewKeyPairFrame(mainFrame,
									publicKeyRingCollection,
									secretKeyRingCollection);
			}
		});
        menu.add(generateNewKeyPair);
        menuBar.add(menu);
        frame.add(menuBar, BorderLayout.NORTH);
	}
	
	public boolean importPublicKeyRingsFromFile(String filePath) {
		try {
			this.publicKeyRingCollection = new PGPPublicKeyRingCollection(new ArmoredInputStream(new FileInputStream(filePath)),
																								new BcKeyFingerprintCalculator());
			return true;
		} catch(IOException | PGPException e) {
			
		}
		return false;
	}
	
	public boolean importSecretKeyRingsFromFile(String filePath) {
		try {
			this.secretKeyRingCollection = new PGPSecretKeyRingCollection(new ArmoredInputStream(new FileInputStream(filePath)),
																								new BcKeyFingerprintCalculator());
			return true;
		} catch(IOException | PGPException e) {
			
		}
		return false;
	}
	
	private void setupKeysTable() {
		tableModel = new DefaultTableModel(columnNames,0);
		
		keysTable = new JTable(tableModel);
		keysTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                String status = (String)table.getModel().getValueAt(row, 3);
                if ("Secret".equals(status)) {
                    setBackground(Color.BLACK);
                    setForeground(Color.WHITE);
                } 
                return this;
            }   
    	});
			
        keysTable.setPreferredScrollableViewportSize(new Dimension(450,63));
        keysTable.setFillsViewportHeight(true);

        JScrollPane js=new JScrollPane(keysTable);
        js.setVisible(true);
        keysTable.setVisible(true);
        frame.add(js, BorderLayout.CENTER);
    
	}

	public void setPublicKeyRingColletion(PGPPublicKeyRingCollection publicKeyRingCollection) {
		this.publicKeyRingCollection = publicKeyRingCollection;
	}

	public void setSecretKeyRingColletion(PGPSecretKeyRingCollection secretKeyRingCollection) {
		this.secretKeyRingCollection = secretKeyRingCollection;
	}
	
	 public void populateKeysTableIfNeeded() {
		 int rowCount = keysTable.getRowCount();
		 if(secretKeyRingCollection != null) { 
	    	String keyID;
	    	
	    	for(PGPSecretKeyRing secretKeyRing: secretKeyRingCollection) {
	    		boolean shouldAddToTable = true;
	    		keyID = Long.toHexString(secretKeyRing.getSecretKey().getKeyID()).toUpperCase();
				StringBuilder stringBuilder = new StringBuilder(keyID);
				stringBuilder.insert(4, " ").insert(9, " ").insert(14, " ");
				String formatedKeyId = stringBuilder.toString();
			    for (int i = 0; i < rowCount; i++) {
			      
			      if(formatedKeyId.equals(keysTable.getValueAt(i, 2)) && keysTable.getValueAt(i, 3).equals("Secret")) {
			    	  shouldAddToTable = false;
			    	  break;
			      } else if(formatedKeyId.equals(keysTable.getValueAt(i, 2)) && keysTable.getValueAt(i, 3).equals("Public")){
			    	  tableModel.removeRow(i);
			      }
			    }
			    
			    if (shouldAddToTable) {
			    	rowCount++;
			    	Iterator<String> userIDs = secretKeyRing.getSecretKey().getUserIDs();
	            	String userID = userIDs.next();
	            	String[] nameAndEmail = userID.split("<");
	            	String name = nameAndEmail[0] ;
					String email = nameAndEmail[1].replace(">", "");
					
					tableModel.addRow(new Object[] {name, email, formatedKeyId, "Secret"});
					
			    }
	    	}
		 }
		 if(publicKeyRingCollection != null) {
			Iterator<PGPPublicKeyRing> publicKeyRingIter = this.publicKeyRingCollection.iterator();
	    	PGPPublicKeyRing publicKeyRing;
	    	String keyID;
	    	while(publicKeyRingIter.hasNext()) {
	    		boolean shouldAddToTable = true;
	    		publicKeyRing = publicKeyRingIter.next();
	    		keyID = Long.toHexString(publicKeyRing.getPublicKey().getKeyID()).toUpperCase();
				StringBuilder stringBuilder = new StringBuilder(keyID);
				stringBuilder.insert(4, " ").insert(9, " ").insert(14, " ");
				String formatedKeyId = stringBuilder.toString();
			    for (int i = 0; i < rowCount; i++) {
			      if(formatedKeyId.equals(keysTable.getValueAt(i, 2))) {
			    	  shouldAddToTable = false;
			    	  break;
			      }
			    }
			    
			    if (shouldAddToTable) {
			    	rowCount++;
			    	Iterator<String> userIDs = publicKeyRing.getPublicKey().getUserIDs();
	            	String userID = userIDs.next();
	            	String[] nameAndEmail = userID.split("<");
	            	String name = nameAndEmail[0] ;
					String email = nameAndEmail[1].replace(">", "");
					
					tableModel.addRow(new Object[] {name, email, formatedKeyId, "Public"});
					
			    }
	    	}
		 }
	 }
}
