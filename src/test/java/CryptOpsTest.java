import java.util.Arrays;

public class CryptOpsTest extends  MyBaseTestCase {

    public  void testEncryptDescryptAes() throws Exception
    {



        byte[] ExampleKey= new byte[]{  'M','y','y','y','M','y','y','y','M','y','y','y',
                                        'a','s','d','a',
                                        's','d','s','a',
                                        'd','a','s','d' };

        byte[] ExampleData = "Once upon a time there was an old man and old woman. Onde day woman has baked a Gingerbread man cookies. The cookies came out to be delicious".getBytes();


        //to use 256 bits AES you must comply with US Export laws see the following link for more info
        // http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
        // ^^^^ Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 7 Download ^^^
        byte[] encodedData=CryptOps.encrypt(ExampleData,ExampleKey, CryptOps.AES_256);

        assertTrue("data pre and post encryption must match",
                                    Arrays.equals(CryptOps.decrypt(encodedData,ExampleKey, CryptOps.AES_256),
                                    ExampleData));



    }

    //must have JCE installed to use 128
    public void testPaddingAES_128() throws Exception {

        byte[] ExampleKey= new byte[]{  'X',
                'M','y','y','y',
                'a','s','d','a',
                's','d','s','a',
                'd','a','s','d' };
        byte[] ExampleKey2 = new byte[]{ // 'M','y','y','y','q','y','y','y','z','y','y','y', '1',
                'a','s','d','a',
                's','d','s','a',
                'd','a','s','1' };

     byte[] outPadded = CryptOps.padAesKeyToNbytes(ExampleKey, CryptOps.AES_128);

      log.info(new String(outPadded));
      log.info(String.format("length of new padded string %1d", outPadded.length));




    }
}
