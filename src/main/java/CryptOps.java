

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.security.Security;

import static javax.crypto.Cipher.DECRYPT_MODE;

/**
 *
 */
class CryptOps {
    //to use 192 or 256 bits AES you must comply with US Export laws see the following link for more info
    // http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
    // ^^^^ Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 7 Download ^^^
    public static final int AES_128 = 16;
    public static final int AES_192 = 24;
    public static final int AES_256 = 32;

    public static byte[] encrypt(byte[] input, byte[] key) throws Exception {

        return      encrypt(input, key, AES_128);
    }
    public static byte[] decrypt(byte[] input, byte[] key) throws Exception {

        return decrypt(input, key, AES_128);
    }
    public static byte[] encrypt(byte[] input, byte[] key, int blockSizeBytes) throws Exception {
        //Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        SecretKeySpec keySpec = new SecretKeySpec(padAesKeyToNbytes(key, blockSizeBytes ), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(input);

    }

    public static byte[] decrypt(byte[] input, byte[] key, int blockSizeBytes) throws Exception {
        //Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        SecretKeySpec keySpec = new SecretKeySpec(padAesKeyToNbytes(key, blockSizeBytes), "AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(DECRYPT_MODE, keySpec);

        return c.doFinal(input);

    }

    // in chunks as large as possible... copy preceding data... and repeat until full
    // this is not a standard way of doing encryption padding for aes, but works
    //TODO: is this padding algo ok?
    public static byte[] padAesKeyToNbytes(byte[] keyIn, int numOfBytes) throws Exception {

        if (keyIn.length > numOfBytes) {
            throw new Exception("Key may not be larger than padding");
        }
        int numOfSub16bytes = keyIn.length % numOfBytes;

        if (numOfSub16bytes == 0) {
            return keyIn;
        }

        byte[] keyOut = new byte[numOfBytes];

        for (int i = 0; i < keyOut.length; i++) {
            keyOut[i] = keyIn[i % keyIn.length];
        }
        return keyOut;

    }

}


