/*
 * To change this template, choose TestTools | Templates
 * and open the template in the editor.
 */

package dictools;

import java.io.PrintStream;
import junit.framework.Assert;

import org.junit.Test;

/**
 *
 * @author Björn Löfroth
 */
public class StemCheckTest extends TestTools {

  @Test
  public void test_stem_check() throws Exception {
    String act = rm("regression_test_data/stemcheck/actual_output.txt");

    PrintStream prevErr = System.err;

    PrintStream checkerOutputFile = new PrintStream(act);
    System.setErr(checkerOutputFile);

    dictools.ProcessDics.main(new String[] {"stemcheck", "test/stem-checker-test.dix"});
    checkerOutputFile.close();
    System.setErr(prevErr);

    String diff=exec( "diff -bBw regression_test_data/stemcheck/expected_output.txt "+act);
    Assert.assertEquals("Difference", "", diff);
  }


}
