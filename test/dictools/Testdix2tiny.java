/*
 * To change this template, choose TestTools | Templates
 * and open the template in the editor.
 */

package dictools;

import dics.elements.utils.Msg;
import java.io.File;
import java.io.PrintStream;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Jacob Nordfalk
 */
public class Testdix2tiny extends TestTools {

@Test
  public void test_dix2tiny() throws Exception {
    if (DixtoolsTestSuite.preferShellTest) {
      Process p=exep("regression_test_data/dix2tiny", "./do_test.sh");
      Assert.assertEquals("Exit value", 0, p.exitValue());
      return;
    }

    dictools.ProcessDics.main(new String[] {"dix2tiny", "test/sample.eo-en.dix", "eo-en", "Esperanto-English", "all"});

    String dir = "regression_test_data/dix2tiny/actual_output";
    rm(dir);
    new File(dir).mkdirs();
    exec("mv eo-en-data.cc "+dir);
    exec("rm -f eo-en-apertium-palm.pdb");
    exep(dir,"unzip ../../../eo-en-data.zip");
    new File("eo-en-data.zip").delete();

    String diff=exec( "diff  -x .svn  -r regression_test_data/dix2tiny/expected_output regression_test_data/dix2tiny/actual_output");
    Assert.assertEquals("Difference", "", diff);
  }


}
