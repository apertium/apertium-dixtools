/*
 * To change this template, choose TestTools | Templates
 * and open the template in the editor.
 */

package dictools;

import dics.elements.utils.Msg;
import java.io.PrintStream;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Jacob Nordfalk
 */
public class MergemorphEquivparTest extends TestTools {

  @Test
  public void test_merge_morph__equiv_paradigms() throws Exception {
    String mmact = rm("regression_test_data/merge-morph/actual_output.dix");

    if (DixtoolsTestSuite.preferShellTest) {
       Process p=exep("regression_test_data/merge-morph", "./do_test.sh");
      Assert.assertEquals("Exit value", 0, p.exitValue());
    } else {
      dictools.ProcessDics.main(new String[] {"merge-morph", "test/sample.metadix", "test/sample2.metadix", mmact});
      String diff=exec( "diff -bBw -I apertium-dixtools regression_test_data/merge-morph/expected_output.dix "+mmact);
      Assert.assertEquals("Difference", "", diff);
    }


    String epact = rm("regression_test_data/equiv-paradigms/actual_output.dix");
    dictools.ProcessDics.main(new String[] {"equiv-paradigms", mmact, epact});
    String diff=exec( "diff -bBw  -I apertium-dixtools regression_test_data/equiv-paradigms/expected_output.dix "+epact);
    Assert.assertEquals("Difference", "", diff);

  }


}
