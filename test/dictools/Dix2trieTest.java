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
public class Dix2trieTest extends TestTools {

    @Test
  public void test_dix2trie() throws Exception {
    if (DixtoolsTestSuite.preferShellTest) {
      Process p=exep("regression_test_data/dix2trie", "./do_test.sh");
      Assert.assertEquals("Exit value", 0, p.exitValue());
      return;
    }

    dictools.ProcessDics.main(new String[] {"dix2trie", "test/sample.eo-en.dix", "lr", rm("regression_test_data/dic-reader/actual_output.txt")});
    String diff=exec( "diff -bBw regression_test_data/dix2trie/expected_output.txt regression_test_data/dix2trie/actual_output.txt");
    Assert.assertEquals("Difference", "", diff);
  }


}
