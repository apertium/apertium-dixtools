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
public class GrepTest extends TestTools {



  @Test
  public void test_fix() throws Exception {
    String act = rm("regression_test_data/grep/actual_output.dix");
    dictools.ProcessDics.main(new String[] {"grep", "--par", "MF_GD", "test/sample.eo-en.dix", act});
    String diff=exec( "diff -bBw regression_test_data/grep/expected_output.dix "+act);
    Assert.assertEquals("Difference", "", diff);
  }


}
