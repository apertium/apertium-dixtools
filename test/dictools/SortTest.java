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
public class SortTest extends TestTools {




 @Test
  public void test_sort() throws Exception {
    if (DixtoolsTestSuite.preferShellTest) {
      Process p=exep("regression_test_data/sort", "./do_test.sh");
      Assert.assertEquals("Exit value", 0, p.exitValue());
      return;
    }

    String dir = rm("regression_test_data/sort/actual_output");
    new File(dir).mkdirs();

    dictools.ProcessDics.main(new String[] {"sort", "-mon", "test/sample.metadix", dir+"/mon.dix"});
    dictools.ProcessDics.main(new String[] {"sort", "-mon", "test/sample.eo-en.dix", dir+"/bil.dix"});

    String diff=exec( "diff  -x .svn  -I apertium-dixtools -r regression_test_data/sort/expected_output regression_test_data/sort/actual_output");
    Assert.assertEquals("Difference", "", diff);
  }


}
