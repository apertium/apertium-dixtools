/*
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

package dictools.speling;

import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import junit.framework.Assert;
import dics.elements.dtd.Dictionary;
import dictools.TestTools;
import dictools.utils.DicOpts;
import dictools.utils.DictionaryReader;

public class SpelingTest extends TestTools {

    public SpelingTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of read_speling method, of class Speling.
     */
    @Test
    public void testRead_speling() throws Exception {
        System.out.println("read_speling");
        String infile = "regression_test_data/speling/speling-test";
        String outfilex = "regression_test_data/speling/speling_out.dix";
        String control = "regression_test_data/speling/expected-output.dix";
        Speling instance = new Speling (infile);
        Dictionary out = instance.read_speling();
        DicOpts opt = DicOpts.STD.copy();
        opt.noHeaderAtTop = true;
        out.printXMLToFile(outfilex, opt);

        String diff=exec( "diff -bBw "+control+" "+outfilex);
        Assert.assertEquals("Difference", "", diff);

    }

}
