package etf.openpgp.ml170674dca170545d;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

public class NewKeyPair {
	public NewKeyPair(String name,
					  String email,
					  int rsaSize,
					  String passPhrase,
					  PGPPublicKeyRingCollection publicKeyRingCollection,
					  PGPSecretKeyRingCollection secretKeyRingCollection,
					  File publicKeyRingFile,
					  File secretKeyRingFile) {
		KeyPairGenerator keyPairGeneratorRsa;
		try {
			keyPairGeneratorRsa = KeyPairGenerator.getInstance("RSA");
			keyPairGeneratorRsa.initialize(rsaSize);
			KeyPair masterKeyPair = keyPairGeneratorRsa.generateKeyPair();
			KeyPair keyPair = keyPairGeneratorRsa.generateKeyPair();
			PGPKeyPair pgpMasterKeyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_SIGN, masterKeyPair, new Date());
			PGPKeyPair pgpKeyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_ENCRYPT, keyPair, new Date());
			 
			// za hash
			PGPDigestCalculator sha1DigestCalculator = new JcaPGPDigestCalculatorProviderBuilder()
					.build().get(HashAlgorithmTags.SHA1);
			 
			PGPKeyRingGenerator keyRingGenerator = new PGPKeyRingGenerator(
					PGPSignature.POSITIVE_CERTIFICATION, pgpMasterKeyPair, name + "#" + email, 
					sha1DigestCalculator, null, null,
					new JcaPGPContentSignerBuilder(PGPPublicKey.RSA_SIGN, HashAlgorithmTags.SHA1),
					new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha1DigestCalculator)
						.setProvider("BC").build(passPhrase.toCharArray())
			);
			
			keyRingGenerator.addSubKey(pgpKeyPair);
			
			PGPSecretKeyRing privateKeyRing = keyRingGenerator.generateSecretKeyRing();
			PGPPublicKeyRing publicKeyRing = keyRingGenerator.generatePublicKeyRing();
			
			secretKeyRingCollection = PGPSecretKeyRingCollection.addSecretKeyRing(secretKeyRingCollection, privateKeyRing);
			publicKeyRingCollection = PGPPublicKeyRingCollection.addPublicKeyRing(publicKeyRingCollection, publicKeyRing);
			
			ArmoredOutputStream aos1 = new ArmoredOutputStream(new FileOutputStream(secretKeyRingFile));
			secretKeyRingCollection.encode(aos1);
	        aos1.close();
	        
	        ArmoredOutputStream aos2 = new ArmoredOutputStream(new FileOutputStream(publicKeyRingFile));
	        publicKeyRingCollection.encode(aos2);
	        aos2.close();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PGPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
	}
}
