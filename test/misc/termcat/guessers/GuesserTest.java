/*
 * Copyright 2010 Jimmy O'Regan
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

package misc.termcat.guessers;

import dics.elements.dtd.E;
import dics.elements.dtd.I;
import dics.elements.dtd.Par;
import dics.elements.dtd.P;
import dics.elements.dtd.L;
import dics.elements.dtd.R;
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
public class GuesserTest {

    public GuesserTest() {
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
     * Test of buildSimpleIEntry method, of class Guesser.
     */
    @Test
    public void testBuildSimpleIEntry() {
        System.out.println("buildSimpleIEntry");
        String lem = "foo";
        String i = lem;
        String par = "house__n";
        Guesser instance = new Guesser();
        
        E expResult = new E();
        expResult.lemma=lem;
        expResult.comment="check";
        I testi = new I();
        testi.setValue(i);
        Par testpar = new Par();
        testpar.name = par;
        expResult.children.add(testi);
        expResult.children.add(testpar);

        // This is crufty!
        E result = instance.buildSimpleIEntry(lem, i, par);
        assertEquals (expResult.lemma, result.lemma);
        assertEquals (expResult.children.size(), result.children.size());
        assertEquals (2, result.children.size());
        assertEquals (expResult.children.get(0).toString(), result.children.get(0).toString());
        assertEquals (expResult.children.get(1).toString(), result.children.get(1).toString());

        //assertEquals(expResult, result);
    }

    /**
     * Test of stemFromPardef method, of class Guesser.
     */
    @Test
    public void testStemFromPardef() {
        System.out.println("stemFromPardef");
        Guesser instance = new Guesser();
        String result = instance.stemFromPardef("baby", "bab/y__n");
        assertEquals("bab", result);
        result = instance.stemFromPardef("house", "house__n");
        assertEquals("house", result);
        result = instance.stemFromPardef("house", "bab/y__n");
        assertEquals("house", result);
        result = instance.stemFromPardef("man", "m/an__n");
        assertEquals("m", result);
    }

    /**
     * Test of buildSimpleLREntry method, of class Guesser.
     */
    @Test
    public void testBuildSimpleLREntry() {
    }

}