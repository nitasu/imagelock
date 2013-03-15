import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.logging.Logger;

/**
this class is intended to operate on images
 - manipulate
 - persist
 - transform
 -
 */
public class ImageOps {

    public final static byte JPEG_EOI = (byte) 0xD9;
    public final static byte JPEG_SOI = (byte) 0xD8;
    public final static byte JPEG_MARKER = (byte) 0xFF;
    public final static byte JPEG_COMMENT = (byte) 0xFE;


    public static byte[] readAllBytes(String directory, String filename) throws IOException {
        Path path = FileSystems.getDefault().getPath(directory,filename);
        return Files.readAllBytes(path);
    }

    public static Path  writeAllBytes(String directory, String filename, byte[] allbytes) throws IOException {
        Path path = FileSystems.getDefault().getPath(directory,filename);
        return  Files.write(path,allbytes, StandardOpenOption.CREATE);
    }

    public static ConvolveOp getGaussianBlurFilter(int radius, boolean horizontal) {
        if (radius < 1) {
            throw new IllegalArgumentException("Radius must be >= 1");
        }

        int size = radius * 2 + 1;
        float[] data = new float[size];

        float sigma = radius / 3.0f;
        float twoSigmaSquare = 2.0f * sigma * sigma;
        float sigmaRoot = (float)
                Math.sqrt(twoSigmaSquare * Math.PI);
        float total = 0.0f;

        for (int i = -radius; i <= radius; i++) {
            float distance = i * i;
            int index = i + radius;
            data[index] = (float) Math.exp(-distance / twoSigmaSquare)/ sigmaRoot;
            total += data[index];
        }
        for (int i = 0; i < data.length; i++) {
            data[i] /= total;
        }
        Kernel kernel = null;
        if (horizontal) {
            kernel = new Kernel(size, 1, data);
        } else {
            kernel = new Kernel(1, size, data);
        }
        return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    }

    public static BufferedImage blurImage(BufferedImage image, int radius) {
        image = changeImageWidth(image, image.getWidth() / 2);
        image = getGaussianBlurFilter(radius / 2, true).filter(image, null);
        image = getGaussianBlurFilter(radius / 2, false).filter(image, null);
        image = changeImageWidth(image, image.getWidth() * 2);
        return image;
    }

    public static BufferedImage changeImageWidth(BufferedImage image, int width) {
        float ratio = (float) image.getWidth()/(float) image.getHeight();
        int height = (int) (width / ratio);
        BufferedImage temp = new BufferedImage(width, height, image.getType());
        Graphics2D g2 = temp.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, temp.getWidth(), temp.getHeight(), null);
        g2.dispose();
        return temp;
    }

    public static int findByteSequence(byte[] seq, String path, String fname) throws IOException {

        byte[] allbytes = ImageOps.readAllBytes(path, fname);

       return  findByteSequence(seq,allbytes);
    }

    public static int findByteSequence(byte[] seq, byte[] input) throws IOException {

        byte[] allbytes =input;

        for (int i = 0; i < allbytes.length - seq.length + 1; i++) {
            if (Arrays.equals(seq, Arrays.copyOfRange(allbytes, i, seq.length + i))) {
                //log.info(String.format("match found at location %1d", i));
                return i;
            }
        }
        return -1;
    }
}
