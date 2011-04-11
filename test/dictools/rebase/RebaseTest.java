/*
 * Author: Jimmy O'Regan
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

package dictools.rebase;

import dics.elements.dtd.E;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.S;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jimregan
 */
public class RebaseTest {

    public RebaseTest() {
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
     * Test of getEStem method, of class Rebase.
     */
/*    @Test
    public void testGetEStem() {
        System.out.println("getEStem");
        E in = null;
        Rebase instance = new Rebase();
        String expResult = "";
        String result = instance.getEStem(in);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
*/
    /**
     * Test of pardefsRoughlyEquivalent method, of class Rebase.
     */
/*    @Test
    public void testPardefsRoughlyEquivalent() {
        System.out.println("pardefsRoughlyEquivalent");
        Pardef base = null;
        Pardef check = null;
        Rebase instance = new Rebase();
        boolean expResult = false;
        boolean result = instance.pardefsRoughlyEquivalent(base, check);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
*/
    /**
     * Test of entriesRoughlyEquivalent method, of class Rebase.
     */
/*    @Test
    public void testEntriesRoughlyEquivalent_E_E() {
        System.out.println("entriesRoughlyEquivalent");
        E base = null;
        E check = null;
        boolean expResult = false;
        boolean result = Rebase.entriesRoughlyEquivalent(base, check);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
*/
    /**
     * Test of entriesRoughlyEquivalent method, of class Rebase.
     */
/*    @Test
    public void testEntriesRoughlyEquivalent_EntryPair() {
        System.out.println("entriesRoughlyEquivalent");
        EntryPair ep = null;
        boolean expResult = false;
        boolean result = Rebase.entriesRoughlyEquivalent(ep);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
*/
    /**
     * Test of sdefsRoughlyEquivalent method, of class Rebase.
     */
    @Test
    public void testSdefsRoughlyEquivalent() {
        System.out.println("sdefsRoughlyEquivalent");
        ArrayList<S> base = new ArrayList<S>();
        ArrayList<S> check = new ArrayList<S>();
        ArrayList<S> check2 = new ArrayList<S>();

        base.add(new S("sg"));
        base.add(new S("nom"));

        check.add(new S("n"));
        check.add(new S("f"));
        check.add(new S("sg"));
        check.add(new S("nom"));

        check2.add(new S("n"));
        check2.add(new S("f"));
        check2.add(new S("pl"));
        check2.add(new S("nom"));

        Rebase instance = new Rebase();
        boolean expResult = true;
        boolean result = instance.sdefsRoughlyEquivalent(base, check);
        assertEquals(expResult, result);

        boolean expResult2 = false;
        boolean result2 = instance.sdefsRoughlyEquivalent(base, check2);
        assertEquals(expResult2, result2);
    }

}