package etf.openpgp.ml170674dca170545d;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;

public class ImportKeyFrame {
	private MainFrame mainFrame;
	
	public ImportKeyFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		
		initFileChooser();
	}
	
	private void initFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("*.asc", "asc");
		fileChooser.setFileFilter(filter);
		int res = fileChooser.showOpenDialog(mainFrame);
		
		if(res == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();

			importKeyRingFromFile(selectedFile.getPath());
			mainFrame.populateKeysTableIfNeeded();
		}
	}
	
	private void importKeyRingFromFile(String filePath) {
		try {
			ArmoredInputStream aos = new ArmoredInputStream(new FileInputStream(filePath));
			BcKeyFingerprintCalculator fingerprint = new BcKeyFingerprintCalculator();
			PGPPublicKeyRingCollection publicKeyRingCollection = new PGPPublicKeyRingCollection(aos, fingerprint);
			addAllNewPublicKeyRings(publicKeyRingCollection);
		} catch (IOException e) {
			System.err.print("No such file");
			e.printStackTrace();
		} catch (PGPException e) {
			try {
				ArmoredInputStream aos = new ArmoredInputStream(new FileInputStream(filePath));
				BcKeyFingerprintCalculator fingerprint = new BcKeyFingerprintCalculator();
				PGPSecretKeyRingCollection imporedSecretKeyRingCollection = new PGPSecretKeyRingCollection(aos, fingerprint);
				addAllNewSecretKeyRings(imporedSecretKeyRingCollection);
			} catch (IOException | PGPException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("static-access")
	private void addAllNewPublicKeyRings(PGPPublicKeyRingCollection importedPublicKeyRingCollection) {
 		PGPPublicKeyRingCollection localPublicKeyRingCollection = mainFrame.getPublicKeyRingColletion();
		for(PGPPublicKeyRing importedPublicKeyRing : importedPublicKeyRingCollection) {
			String importedKeyID = Long.toHexString(importedPublicKeyRing.getPublicKey().getKeyID()).toUpperCase();
			Boolean shouldAddKey = true;
			for(PGPPublicKeyRing localPublicKeyRing : localPublicKeyRingCollection) {
				String localKeyID = Long.toHexString(localPublicKeyRing.getPublicKey().getKeyID()).toUpperCase();
				if(importedKeyID.equalsIgnoreCase(localKeyID)) {
					shouldAddKey = false;
					break;
				}
			}
			
			if(shouldAddKey) {
				localPublicKeyRingCollection = localPublicKeyRingCollection.addPublicKeyRing(localPublicKeyRingCollection, 
															  importedPublicKeyRing);
			}
		}
		
		mainFrame.setPublicKeyRingColletion(localPublicKeyRingCollection);
		Util.writeKeyRingsToFile("src/OnLoadFiles/public/publicKeyRing.asc", localPublicKeyRingCollection);
	}
	
	@SuppressWarnings("static-access")
	private void addAllNewSecretKeyRings(PGPSecretKeyRingCollection importedSecretKeyRingCollection) {
 		PGPSecretKeyRingCollection localSecretKeyRingCollection = mainFrame.getSecretKeyRingColletion();
		for(PGPSecretKeyRing importedSecretKeyRing : importedSecretKeyRingCollection) {
			String importedKeyID = Long.toHexString(importedSecretKeyRing.getPublicKey().getKeyID()).toUpperCase();
			Boolean shouldAddKey = true;
			for(PGPSecretKeyRing localSecretKeyRing : localSecretKeyRingCollection) {
				String localKeyID = Long.toHexString(localSecretKeyRing.getPublicKey().getKeyID()).toUpperCase();
				if(importedKeyID.equalsIgnoreCase(localKeyID)) {
					shouldAddKey = false;
					break;
				}
			}
			
			if(shouldAddKey) {
				localSecretKeyRingCollection = localSecretKeyRingCollection.addSecretKeyRing(localSecretKeyRingCollection, 
															  importedSecretKeyRing);
			}
		}
		
		mainFrame.setSecretKeyRingColletion(localSecretKeyRingCollection);
		Util.writeKeyRingsToFile("src/OnLoadFiles/secret/secretKeyRing.asc", localSecretKeyRingCollection);
	}
}
