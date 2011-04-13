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
import dics.elements.dtd.P;
import dics.elements.dtd.L;
import dics.elements.dtd.R;
import dics.elements.dtd.TextElement;
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

    E base;
    E check;

    public RebaseTest() {
        base = new E();
        check = new E();

        L baseL = new L();
        L checkL = new L();
        R baseR = new R();
        R checkR = new R();

        baseL.children.add(new TextElement("i"));
        baseR.children.add(new TextElement("a"));
        baseR.children.add(new S("sg"));
        baseR.children.add(new S("gen"));

        checkL.children.add(new TextElement("ki"));
        checkR.children.add(new TextElement("ka"));
        checkR.children.add(new S("n"));
        checkR.children.add(new S("f"));
        checkR.children.add(new S("sg"));
        checkR.children.add(new S("gen"));

        base.children.add(new P(baseL, baseR));
        check.children.add(new P(checkL, checkR));
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
    @Test
    public void testGetEStem() {
        System.out.println("getEStem");
        E in = new E();
        L l = new L();
        l.children.add(new TextElement("man"));
        R r = new R();
        r.children.add(new TextElement("men"));
        r.children.add(new S("n"));
        r.children.add(new S("pl"));
        in.children.add(new P(l, r));
        Rebase instance = new Rebase();
        String expResult = "m";
        String result = instance.getEStem(in);
        assertEquals(expResult, result);
    }

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
    @Test
    public void testEntriesRoughlyEquivalent_E_E() {
        System.out.println("entriesRoughlyEquivalent");
        boolean expResult = true;
        boolean result = Rebase.entriesRoughlyEquivalent(this.base, this.check);
        assertEquals(expResult, result);
    }

    /**
     * Test of entriesRoughlyEquivalent method, of class Rebase.
     */
    @Test
    public void testEntriesRoughlyEquivalent_EntryPair() {
        System.out.println("entriesRoughlyEquivalent");
        EntryPair ep = new EntryPair(this.base, this.check);
        boolean expResult = true;
        boolean result = Rebase.entriesRoughlyEquivalent(ep);
        assertEquals(expResult, result);
    }

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