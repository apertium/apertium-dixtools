/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package misc.termcat.guessers;

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
public class EnglishTest {

    public EnglishTest() {
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
     * Test of guess method, of class English.
     */
    @Test
    public void testGuess() {
        System.out.println("guess");
        English instance = new English();
        String result = instance.guess("house", "n");
        assertEquals("house__n", result);
        result = instance.guess("politics", "n");
        assertEquals("politics__n", result);
        result = instance.guess("baby", "n");
        assertEquals("bab/y__n", result);
        result = instance.guess("church", "n");
        assertEquals("access__n", result);
    }

}