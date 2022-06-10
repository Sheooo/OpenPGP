package etf.openpgp.ml170674dca170545d;

import java.io.FileOutputStream;
import java.io.IOException;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;

public class Util {
	public static void writeKeyRingsToFile(String filePath, 
											Object keyRingCollection) {
		try {
			ArmoredOutputStream aos1 = new ArmoredOutputStream(new FileOutputStream(filePath));
			if (keyRingCollection instanceof PGPPublicKeyRingCollection) {
				PGPPublicKeyRingCollection publicKeyRingColletion = (PGPPublicKeyRingCollection) keyRingCollection;
				publicKeyRingColletion.encode(aos1);
				aos1.close();
			} else {
				PGPSecretKeyRingCollection secretKeyRingColletion = (PGPSecretKeyRingCollection) keyRingCollection;
				secretKeyRingColletion.encode(aos1);
				aos1.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
