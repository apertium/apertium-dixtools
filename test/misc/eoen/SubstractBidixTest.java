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

package misc.eoen;

import dics.elements.dtd.ContentElement;
import dics.elements.dtd.EElement;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author j
 */
public class SubstractBidixTest {

    public SubstractBidixTest() {
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
   * Test of isAllowed method, of class SubstractBidix.
   */
  @Test
  public void testIsRestricted() {
    EElement ee=new EElement();

    ee.setRestriction("LR");
    assertEquals(SubstractBidix.isAllowed("LR", ee), true);
    assertEquals(SubstractBidix.isAllowed("RL", ee), false);

    ee.setIgnore("yes");
    assertEquals(SubstractBidix.isAllowed("LR", ee), false);
    assertEquals(SubstractBidix.isAllowed("RL", ee), false);

    ee.setRestriction(null);
    assertEquals(SubstractBidix.isAllowed("LR", ee), false);
    assertEquals(SubstractBidix.isAllowed("RL", ee), false);
  
    ee.setIgnore(null);
    assertEquals(SubstractBidix.isAllowed("LR", ee), true);
    assertEquals(SubstractBidix.isAllowed("RL", ee), true);
  }

  /**
   * Test of setYesIsAllowed method, of class SubstractBidix.
   */
  @Test
  public void testRemoveRestriction() {
    EElement ee=new EElement();

    ee.setIgnore("yes");
    SubstractBidix.setYesIsAllowed(ee, "LR");
    assertEquals(SubstractBidix.isAllowed("LR", ee), true);
    assertEquals(SubstractBidix.isAllowed("RL", ee), false);

    SubstractBidix.setYesIsAllowed(ee, "RL");
    assertEquals(SubstractBidix.isAllowed("LR", ee), true);
    assertEquals(SubstractBidix.isAllowed("RL", ee), true);
  }

  /**
   * Test of checkEarlierAndRestrict method, of class SubstractBidix.
   */
  @Test
  public void testCheckEarlierAndRestrict() {
    System.err.println("checkEarlierAndRestrict");
    String direction="";
    ContentElement l=null;
    HashMap<String, EElement> hmLR=null;
    EElement ee=null;
    //SubstractBidix.checkEarlierAndRestrict(direction, l, hmLR, ee);
    // TODO review the generated test code and remove the default call to fail.
    //fail("The test case is a prototype.");
  }

}