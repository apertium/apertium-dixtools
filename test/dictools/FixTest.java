/*
 * To change this template, choose TestTools | Templates
 * and open the template in the editor.
 */

package dictools;

import junit.framework.Assert;

import org.junit.Test;

/**
 *
 * @author Jacob Nordfalk
 */
public class FixTest extends TestTools {



  @Test
  public void test_fix() throws Exception {
    String act = rm("regression_test_data/fix/actual_output.dix");
    dictools.ProcessDics.main(new String[] {"fix", "test/sample.metadix", act});
    String diff=exec( "diff -bBw regression_test_data/fix/expected_output.dix "+act);
    Assert.assertEquals("Difference", "", diff);
  }


}
