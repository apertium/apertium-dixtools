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

package dictools.speling;

import dics.elements.dtd.E;
import dics.elements.dtd.Pardef;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jim
 */
public class SpelingParadigmTest {

    public SpelingParadigmTest() {
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
     * Test of find_stem method, of class SpelingParadigm.
     */
    @Test
    public void testFind_stem() {
        SpelingParadigm instance = new SpelingParadigm();
        System.out.println("find_stem");
        String lemma = "index";
        String flexion = "indices";
        String expResult = "ind";
        String result = instance.find_stem(lemma, flexion);
        assertEquals(expResult, result);
        String lemma2 = "go";
        String flexion2 = "went";
        String expResult2 = "";
        String result2 = instance.find_stem(lemma2, flexion2);
        assertEquals(expResult2, result2);
    }

    @Test
    public void testStrip_stem() {
        SpelingParadigm instance = new SpelingParadigm();
        System.out.println("find_stem");
        String in1 = "index";
        String stem1 = "ind";
        String expResult = "ex";
        String result = instance.strip_stem(stem1, in1);
        assertEquals(expResult, result);
        String in2 = "go";
        String stem2 = "went";
        String expResult2 = in2;
        String result2 = instance.strip_stem(stem2, in2);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of get_shortest method, of class SpelingParadigm.
     */
    @Test
    public void testGet_shortest() {
        SpelingParadigm instance = new SpelingParadigm();
        System.out.println("get_shortest");
        String[] list = {"aaa", "aa", "a", "aa"};
        String expResult = "a";
        String result = instance.get_shortest(list);
        assertEquals(expResult, result);
    }

    /**
     * Test of SpelingParadigm method, of class SpelingParadigm.
     */
    @Test
    public void testSpelingParadigm() {
/*
        System.out.println("SpelingParadigm");
        SpelingParadigm instance = new SpelingParadigm();
        instance.SpelingParadigm();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
 */
    }

    /**
     * Test of toPardef method, of class SpelingParadigm.
     */
    @Test
    public void testToPardef() {
   /*
        System.out.println("toPardef");
        SpelingParadigm instance = new SpelingParadigm();
        Pardef expResult = null;
        Pardef result = instance.toPardef();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    */
    }

    /**
     * Test of toE method, of class SpelingParadigm.
     */
    @Test
    public void testToE() {
/*        System.out.println("toE");
        SpelingParadigm instance = new SpelingParadigm();
        E expResult = null;
        E result = instance.toE();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype."); */
    }

}