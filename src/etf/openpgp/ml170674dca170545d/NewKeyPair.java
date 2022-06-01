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
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.KeyFlags;
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
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

enum KeyType {
	master,
	sub
}

class NewKeyPair {
	static public Boolean generateNewKeyPair(String name,
					  String email,
					  int rsaSize,
					  char[] passPhrase,
					  PGPPublicKeyRingCollection publicKeyRingCollection,
					  PGPSecretKeyRingCollection secretKeyRingCollection,
					  File publicKeyRingFile,
					  File secretKeyRingFile) {
		try {
			KeyPairGenerator keyPairGeneratorRsa;
			keyPairGeneratorRsa = KeyPairGenerator.getInstance("RSA");
			keyPairGeneratorRsa.initialize(rsaSize);
			PGPKeyPair masterKeyPair = createKeyPair(keyPairGeneratorRsa, KeyType.master);
			PGPKeyPair subKeyPair = createKeyPair(keyPairGeneratorRsa, KeyType.sub);
			PGPSignatureSubpacketGenerator keyRingSignatureGenerator = createSignatureForKeyRing();
			PGPSignatureSubpacketGenerator keySignatureGenerator = createSignatureForKey();
			PGPKeyRingGenerator keyRingGenerator = createKeyRingGenerator(name,
																		  email, 
																		  passPhrase, 
					  													  masterKeyPair,
					  													  keyRingSignatureGenerator.generate());
			keyRingGenerator.addSubKey(subKeyPair, keySignatureGenerator.generate(), null);
			PGPSecretKeyRing privateKeyRing = keyRingGenerator.generateSecretKeyRing();
			PGPPublicKeyRing publicKeyRing = keyRingGenerator.generatePublicKeyRing();
			
			secretKeyRingCollection = PGPSecretKeyRingCollection.addSecretKeyRing(secretKeyRingCollection, privateKeyRing);
			publicKeyRingCollection = PGPPublicKeyRingCollection.addPublicKeyRing(publicKeyRingCollection, publicKeyRing);
			
			writeKeysToFiles(publicKeyRingFile, 
							 secretKeyRingFile, 
							 publicKeyRingCollection, 
							 secretKeyRingCollection);
			
			return true;
		} catch (NoSuchAlgorithmException | PGPException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static PGPKeyPair createKeyPair(KeyPairGenerator generator, KeyType keyType) {
		try {
			KeyPair keyPair = generator.generateKeyPair();
			switch(keyType) {
			case master:
				return new JcaPGPKeyPair(PGPPublicKey.RSA_SIGN, keyPair, new Date());
			case sub:
				return new JcaPGPKeyPair(PGPPublicKey.RSA_ENCRYPT, keyPair, new Date());
			}
		} catch (PGPException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	private static PGPKeyRingGenerator createKeyRingGenerator(String name,
															  String email,
															  char[] passPhrase,
															  PGPKeyPair masterKeyPair,
															  PGPSignatureSubpacketVector signature) {
		try {
			PGPDigestCalculator sha1DigestCalculator = new JcaPGPDigestCalculatorProviderBuilder()
															 .build()
															 .get(HashAlgorithmTags.SHA1);
			PBESecretKeyEncryptor encryptor = new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha1DigestCalculator)
													.setProvider("BC")
													.build(passPhrase);
			JcaPGPContentSignerBuilder signerBuilder = new JcaPGPContentSignerBuilder(PGPPublicKey.RSA_SIGN, HashAlgorithmTags.SHA1);
			
			return new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION, 
										   masterKeyPair, 
										   name + "<" + email + ">",
										   sha1DigestCalculator, 
										   signature, 
										   null,
										   signerBuilder,
										   encryptor);
		} catch (PGPException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static PGPSignatureSubpacketGenerator createSignatureForKeyRing() {
		PGPSignatureSubpacketGenerator signatureSubpacketGenerator = new PGPSignatureSubpacketGenerator();
		signatureSubpacketGenerator.setKeyFlags(false, KeyFlags.SIGN_DATA | KeyFlags.CERTIFY_OTHER);
		signatureSubpacketGenerator.setPreferredSymmetricAlgorithms(false, new int[] {
		    SymmetricKeyAlgorithmTags.TRIPLE_DES,
		    SymmetricKeyAlgorithmTags.IDEA
		});
		signatureSubpacketGenerator.setPreferredHashAlgorithms(false, new int[] {
		    HashAlgorithmTags.SHA1
		});
		
		return signatureSubpacketGenerator;
	}
	
	private static PGPSignatureSubpacketGenerator createSignatureForKey() {
		PGPSignatureSubpacketGenerator signatureSubpacketGenerator = new PGPSignatureSubpacketGenerator();
		signatureSubpacketGenerator.setKeyFlags(false, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE);

		return signatureSubpacketGenerator;
	}
	
	private static void writeKeysToFiles(File publicKeyRingFile, 
										 File secretKeyRingFile,
										  PGPPublicKeyRingCollection publicKeyRingCollection,
										  PGPSecretKeyRingCollection secretKeyRingCollection) {
		try {
			ArmoredOutputStream aos1 = new ArmoredOutputStream(new FileOutputStream(secretKeyRingFile));
			secretKeyRingCollection.encode(aos1);
	        aos1.close();
	        
	        ArmoredOutputStream aos2 = new ArmoredOutputStream(new FileOutputStream(publicKeyRingFile));
	        publicKeyRingCollection.encode(aos2);
	        aos2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
} 

