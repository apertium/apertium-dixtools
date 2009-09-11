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
public class ReversebilTest extends TestTools {



  @Test
  public void test_reverse_bil() throws Exception {
    if (DixtoolsTestSuite.preferShellTest) {
      Process p=exep("regression_test_data/reverse-bil", "./do_test.sh");
      Assert.assertEquals("Exit value", 0, p.exitValue());
      return;
    }

    dictools.ProcessDics.main(new String[] {"reverse-bil", "test/sample.eo-en.dix", rm("regression_test_data/reverse-bil/actual_output.dix")});
    String diff=exec( "diff -bBw -I apertium-dixtools regression_test_data/reverse-bil/expected_output.dix regression_test_data/reverse-bil/actual_output.dix");
    Assert.assertEquals("Difference", "", diff);
  }


}
