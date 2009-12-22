/*
 * Copyright (C) 2008 Dana Esperanta Junulara Organizo http://dejo.dk/
 * Author: Jacob Nordfalk
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package dictools;

import junit.framework.Assert;

import org.junit.Test;



/**
 *
 * @author j
 */
public class ZCrossDictTest extends TestTools {
  
  @Test
  public void testBigCrossing() throws Exception {
    if (DixtoolsTestSuite.preferShellTest) {
      Process p=exep("regression_test_data/crossdict", "./do_test.sh");
      Assert.assertEquals("Exit value", 0, p.exitValue());
      return;
    }
    String outfile = rm("dix");

    String path = "regression_test_data/crossdict/input/";
    String[] argsx = {"cross-param", 
    "-bilAB", "-r", path+"apertium-es-ca.es-ca.dix",
    "-bilBC", "-r", path+"apertium-en-ca.en-ca.dix", 
    "-monA", path+"apertium-es-ca.es.dix", 
    "-monC", path+"apertium-en-ca.en.metadix", 
    "-cross-model", path+"../../../../apertium-dixtools/schemas/cross-model.xml"};
     dictools.ProcessDics.main(argsx);  

    String diff=exec( "diff  -bBw -x .svn -I processed "+path+"../expected_output "+outfile);
    Assert.assertEquals("Difference", "", diff);
    rm(outfile);  
  }
}
