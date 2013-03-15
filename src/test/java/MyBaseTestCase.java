import junit.framework.TestCase;

import java.util.logging.Logger;

public abstract class MyBaseTestCase extends TestCase {
   Logger log =  Logger.getLogger(this.getClass().getName());
}
