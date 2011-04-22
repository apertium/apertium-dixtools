/*
 * Copyright 2011 European Commission
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package dictools.columnar;

import dics.elements.dtd.TextElement;
import dics.elements.dtd.E;
import dics.elements.dtd.I;
import dics.elements.dtd.P;
import dics.elements.dtd.L;
import dics.elements.dtd.R;
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
public class ColumnarTest {

    public ColumnarTest() {
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
     * Test of doColumnar method, of class Columnar.
     */
    //@Test
    public void testDoColumnar() {
        System.out.println("doColumnar");
        Columnar instance = new Columnar();
//        instance.doColumnar();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTemplate method, of class Columnar.
     */
    //@Test
    public void testGetTemplate() {
        System.out.println("getTemplate");
        String parleft = "";
        String parright = "";
        String lRestrict = "";
        Columnar instance = new Columnar();
        ArrayList expResult = null;
        ArrayList result = instance.getTemplate(parleft, parright, lRestrict);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isSimplePEntry method, of class Columnar.
     */
    //@Test
    public void testIsSimplePEntry() {
        System.out.println("isSimplePEntry");
        E e = null;
        Columnar instance = new Columnar();
        boolean expResult = false;
        boolean result = instance.isSimplePEntry(e);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isSimpleIEntry method, of class Columnar.
     */
    @Test
    public void testIsSimpleIEntry() {
        System.out.println("isSimpleIEntry");
        E pass = new E();
        E failP = new E();
        E failIComplicated = new E();
        I testi = new I();
        testi.children.add(new TextElement("test"));
        P p = new P();
        L l = new L();
        R r = new R();
        l.children.add(new TextElement("test"));
        r.children.add(new TextElement("test"));
        p.l = l;
        p.r = r;
        pass.children.add(testi);
        failP.children.add(p);
        failIComplicated.children.add(testi);
        failIComplicated.children.add(p);

        Columnar instance = new Columnar();
        boolean result_pass = instance.isSimpleIEntry(pass);
        boolean result_fail = instance.isSimpleIEntry(failP);
        boolean result_fail_comp = instance.isSimpleIEntry(failIComplicated);
        assertEquals(true, result_pass);
        assertEquals(false, result_fail);
        assertEquals(false, result_fail_comp);
    }

    boolean checkSALists (ArrayList<S> l, ArrayList<S> r) {
        if (l.size() != r.size()) {
            return false;
        }
        for (S s1 : l) {
            for (S s2 : r) {
                if (!s1.name.equals(s2.name)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Test of getISymbols method, of class Columnar.
     */
    @Test
    public void testGetISymbols() {
        System.out.println("getISymbols");
        E e = new E();
        I i = new I();
        i.children.add(new TextElement("text"));
        i.children.add(new S("n"));
        i.children.add(new S("sg"));
        e.children.add(i);
        
        Columnar instance = new Columnar();
        ArrayList<S> expResult = new ArrayList<S>();
        expResult.add(new S("n"));
        expResult.add(new S("sg"));

        ArrayList<S> expFail = new ArrayList<S>();
        expFail.add(new S("vblex"));
        expFail.add(new S("past"));
        
        ArrayList<S> result = instance.getISymbols(e);

        boolean passCheck = checkSALists(result, expResult);
        boolean failCheck = checkSALists(result, expFail);
        assertEquals(true, passCheck);
        assertEquals(false, failCheck);
    }

    /**
     * Test of getLSymbols method, of class Columnar.
     */
    //@Test
    public void testGetLSymbols() {
        System.out.println("getLSymbols");
        E e = null;
        Columnar instance = new Columnar();
        ArrayList expResult = null;
        ArrayList result = instance.getLSymbols(e);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRSymbols method, of class Columnar.
     */
    //@Test
    public void testGetRSymbols() {
        System.out.println("getRSymbols");
        E e = null;
        Columnar instance = new Columnar();
        ArrayList expResult = null;
        ArrayList result = instance.getRSymbols(e);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of populateEntries method, of class Columnar.
     */
    //@Test
    public void testPopulateEntries() {
        System.out.println("populateEntries");
        ArrayList<E> tpl = null;
        String lemLeft = "";
        String lemRight = "";
        Columnar instance = new Columnar();
        ArrayList expResult = null;
        ArrayList result = instance.populateEntries(tpl, lemLeft, lemRight);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setInFiles method, of class Columnar.
     */
    //@Test
    public void testSetInFiles() {
        System.out.println("setInFiles");
        String l = "";
        String r = "";
        String b = "";
        Columnar instance = new Columnar();
        instance.setInFiles(l, r, b);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setInput method, of class Columnar.
     */
    //@Test
    public void testSetInput() {
        System.out.println("setInput");
        String in = "";
        Columnar instance = new Columnar();
        instance.setInput(in);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setConfig method, of class Columnar.
     */
    //@Test
    public void testSetConfig() {
        System.out.println("setConfig");
        String cfg = "";
        Columnar instance = new Columnar();
        instance.setConfig(cfg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setupArgs method, of class Columnar.
     */
    //@Test
    public void testSetupArgs() {
        System.out.println("setupArgs");
        Columnar instance = new Columnar();
        instance.setupArgs();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toolHelp method, of class Columnar.
     */
    //@Test
    public void testToolHelp() {
        System.out.println("toolHelp");
        Columnar instance = new Columnar();
        String expResult = "";
        String result = instance.toolHelp();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}