/*
 * To change this template, choose TestTools | Templates
 * and open the template in the editor.
 */

package dictools;

import java.io.FileOutputStream;
import java.io.PrintStream;

import junit.framework.Assert;

import org.junit.Test;

import dictools.utils.Msg;

/**
 * @author Jacob Nordfalk
 */
public class DicReaderTest extends TestTools {

  @Test
  public void test_dic_reader() throws Exception {
    if (DixtoolsTestSuite.preferShellTest) {
      Process p=exep("regression_test_data/dic-reader", "./do_test.sh");
      Assert.assertEquals("Exit value", 0, p.exitValue());
      return;
    }

    // Redirect output
    String actout;
    Msg.inst().out = new PrintStream(new FileOutputStream(actout=rm("regression_test_data/dic-reader/actual_output.txt")));

    dictools.ProcessDics.main(new String[] {"list", "paradigms", "test/sample.metadix"});
    dictools.ProcessDics.main(new String[] {"list", "lemmas", "test/sample.metadix"});
    dictools.ProcessDics.main(new String[] {"list", "definitions", "test/sample.metadix"});
    dictools.ProcessDics.main(new String[] {"list", "pairs", "test/sample.eo-en.dix"});

    Msg.inst().out.close();
    Msg.inst().out = System.out;
    String diff=exec( "diff -bBw regression_test_data/dic-reader/expected_output.txt "+actout);
    Assert.assertEquals("Difference", "", diff);
  }


}
