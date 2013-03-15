
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;


import  org.apache.commons.codec.binary.Base64;
import javax.imageio.ImageIO;


public class ImageOpsTest extends MyBaseTestCase {

    public static final String GNU_JPG = "gnu.jpg";
    public static final String SRC_TEST_RESOURCES = "src/test/resources/";

    public void testImageReadAndWrite() throws IOException {


        byte[] allbytes = ImageOps.readAllBytes(SRC_TEST_RESOURCES, GNU_JPG);
        log.info(String.format("file len is %1d", allbytes.length));
        assertTrue(allbytes.length > 290000);

        ImageOps.writeAllBytes(SRC_TEST_RESOURCES, "copy.gnu.jpg", allbytes);

        int oldSize = allbytes.length;
        byte tenthByte = allbytes[10 - 1];

        allbytes = ImageOps.readAllBytes(SRC_TEST_RESOURCES, "copy.gnu.jpg");

        assertTrue("compare file lengths", oldSize == allbytes.length);
        assertTrue("compare tenth byte content (just random choice of mine)", tenthByte == allbytes[10 - 1]);

    }



    public void testFindByteSequence() throws IOException {
        byte[] seq = new byte[]{ImageOps.JPEG_MARKER, ImageOps.JPEG_EOI};
        assertTrue(ImageOps.findByteSequence(seq, SRC_TEST_RESOURCES, GNU_JPG) > 0);
    }

    public static void insertBinaryPayloadBeforeMarker(String path, String fnameIn, String fnameOut,
                                                       byte[] payload, byte[] seqMarker) throws IOException {
        //read file content
        //find  location of Marker
        //write original file up to the marker
        //write payload
        //write remainder of the original file


        File inFile= new File(path+fnameIn);

        FileReader reader = new FileReader(inFile);

        int  i;
        byte[] buffer = seqMarker.clone();
        int offset=0;
        while ((i= reader.read())!=-1) {
         offset++;
         pushToEndOfArrayDropFirst(buffer,(byte)i);
         if (offset>=seqMarker.length){

         }

         if(offset%seqMarker.length==0)
         {
             //save buffer
         }

        }
    }



    // adds byte to the end of the array
    // shifts all values left and drops [0]th element
    public static byte[] pushToEndOfArrayDropFirst(byte[] array, byte value)
    {
       for(int i=0;array.length-1>i;i++)
       {
           array[i]=array[i+1];
           array[array.length-1]=value;
           return array;
       }
        return null;
    }

    public void testImageAddComment() throws IOException {

        byte[] sampleComment = new byte[] {ImageOps.JPEG_MARKER,ImageOps.JPEG_COMMENT,
                0x69,0x69,0x69,0x69,0x69,0x69,0x69,0x69,0x69,0x69,0x69,0x69,0x69,0x68};
        String fileOutName=SRC_TEST_RESOURCES + "comments." +GNU_JPG;


        log.info("before read entire source file");
        byte[] sourceBytes=ImageOps.readAllBytes(SRC_TEST_RESOURCES,GNU_JPG);
        log.info("after read entire source file");
        byte[] markerSeq = new byte[] {ImageOps.JPEG_MARKER, ImageOps.JPEG_EOI};
        log.info("before marker search");
        int insertionOffset = ImageOps.findByteSequence(markerSeq,SRC_TEST_RESOURCES,GNU_JPG);
        log.info(String.format("afer marker found at location",insertionOffset));

        FileOutputStream writer = new FileOutputStream(fileOutName );
        //writing pre-marker
        for (int i=0;i<insertionOffset;i++)
        {
            writer.write(sourceBytes[i]);
        }
        //inserting payload
        for (byte b:sampleComment){
            writer.write(b);
        }

        //writing marker and post post-marker
        for (int i = insertionOffset;i<sourceBytes.length;i++){
            writer.write(sourceBytes[i]);
        }

        writer.flush();
        writer.close();

          byte[] seq=new byte[]{0x69, 0x69};
        int sequenceFoundAt=ImageOps.findByteSequence(seq,SRC_TEST_RESOURCES,"comments."+GNU_JPG);
        assertTrue(sequenceFoundAt>55);
        log.info(String.format("sequence found at %1d",sequenceFoundAt));



    }

    public void testBase64EncodeDecode() {

        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        //                 ABCDEFGHIJKLMNOPQRSTUVWXYZ

         byte[] alphabet64= Base64.encodeBase64(alphabet.getBytes());
         log.info("encoded string: " + new String(alphabet64));
         log.info("decoded string: " + new String(Base64.decodeBase64(alphabet64)));
         assertTrue("string are the same", (new String(Base64.decodeBase64(alphabet64))).equals(alphabet));
    }

    public void testBase64Decode() {

    }

    public void testAesEncode() {

    }

    public void testAesEncryptDecrypt() throws Exception {
        byte[] allbytes = ImageOps.readAllBytes(SRC_TEST_RESOURCES, GNU_JPG);
        String outStr=new String(Base64.encodeBase64(CryptOps.encrypt(allbytes,"password1".getBytes("UTF-8"))));

        assertTrue(Arrays.equals(allbytes,CryptOps.decrypt(Base64.decodeBase64(outStr),"password1".getBytes("UTF-8"))));
        log.info(String.format("%1d",outStr.length()));

        ImageOps.writeAllBytes(SRC_TEST_RESOURCES,GNU_JPG + ".aes",outStr.getBytes());
        allbytes=ImageOps.readAllBytes(SRC_TEST_RESOURCES, GNU_JPG + ".aes");
        ImageOps.writeAllBytes(SRC_TEST_RESOURCES, GNU_JPG + ".aes.jpg",
                CryptOps.decrypt(Base64.decodeBase64(new String(allbytes)), "password1".getBytes("UTF-8")));
    }

    public void testMake640x480Image() throws IOException {

        BufferedImage img = ImageIO.read(new File(SRC_TEST_RESOURCES + GNU_JPG));
        img = ImageOps.changeImageWidth(img,640);
        for (int i=0;i<11;i++)
        {

            img = ImageOps.blurImage(img,4);
            img = ImageOps.changeImageWidth(img,3000);
            img = ImageOps.changeImageWidth(img,640);
        }

        ImageIO.write(img,"JPEG",new File(SRC_TEST_RESOURCES+"blurr."+ GNU_JPG));

    }

    public void testMessedImage() throws IOException {

        uMashIt("http://www.maniacworld.com/Bruce-Lee-Photo-15.jpg",SRC_TEST_RESOURCES+"messed.bruce.jpg");
        uMashIt(" http://simoncareyholt.files.wordpress.com/2012/03/last-supper.jpg",
                SRC_TEST_RESOURCES+"messed."+ "last-supper.jpg");


    }

    /**
     * 1. drop the size of the original image to something small around 400px in width
     * 2. move through 2d matrix in steps of around 30px and
     * 3. draw and black cirle or black square diameter/width little less than step above (25px)
     *
     *
     *
     *
     *
     * @param url  http:// or file:// style resource URL string
     * @param fileoutName  local fileName you want written to disk
     * @throws IOException
     */
    public static void uMashIt(String url, String fileoutName) throws IOException {

         BufferedImage img = ImageIO.read(new URL(url));//SRC_TEST_RESOURCES + GNU_JPG));
         img = ImageOps.changeImageWidth(img,600);
         img = ImageOps.blurImage(img,8);
         img = punchHoles(img,100,80,80,Color.BLACK,"SQUARE") ;
        img = punchHoles(img,50,66,66,Color.BLACK ,"CIRCLE") ;

         ImageIO.write(img,"JPEG",new File(fileoutName));
     }

    /**
     *
     * move through image 2d matrix left-right-down pattern (typical english book reading pattern)
     * use square step (x==y)
     * draw a shape circle/square of equal diameter/width
     * bullet:  specific  pattern punched out of the image. for example oval or rectangle
     *
     * @param img       image to be manipulate
     * @param step      square size. impacts distance between bullets
     * @param bulletWidth    width of bullet
     * @param bulletHeight   width of bullet
     * @param color          java. Color     this is  color of your bullets
     * @param shape          for now its a string must be CIRCLE or SQUARE
     * @return          image with bullets punched out of the original image
     */
    public static BufferedImage punchHoles(BufferedImage img,
                                           int step, int bulletWidth,
                                           int bulletHeight, Color color,
                                           String shape)    {

        Graphics g = img.getGraphics();
        g.setColor(color);


        for (int i=0;i<img.getWidth();i=i+step+1)
            for (int j=0;j<img.getHeight();j=j+step+1)
            {
                if (shape.equalsIgnoreCase("CIRCLE"))
                {
                    g.fillOval(i,j,bulletWidth,bulletHeight);
                }
                if ((shape.equalsIgnoreCase("SQUARE")) )
                {

                    g.fillRect(i,j,bulletWidth,bulletHeight);
                }

            }
      return img;
    }

}
