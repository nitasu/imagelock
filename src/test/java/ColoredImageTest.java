import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

public class ColoredImageTest extends MyBaseTestCase {






    public void testOne() throws IOException {

        String path = "src/test/resources/";
        BufferedImage img = ImageIO.read(new File(path + "gnu.jpg"));
        log.info(img.toString());
    }

}