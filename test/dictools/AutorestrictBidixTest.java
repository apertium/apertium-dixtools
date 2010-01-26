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

import dictools.AutorestrictBidix;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dics.elements.dtd.E;
import dictools.TestTools;
import dictools.utils.DicOpts;
import java.io.File;
import junit.framework.Assert;
import org.junit.BeforeClass;

/**
 *
 * @author j
 */
public class AutorestrictBidixTest extends TestTools {

  static String expdir = "regression_test_data/autorestrict/expected_output/";
  static String actdir = "regression_test_data/autorestrict/actual_output/";
  private String input = "test/missing_restrictions.eo-en.dix";
  private String input2 = "test/sample.eo-en.dix";

  @BeforeClass
  public static void setUpClass() throws Exception {
    new File(expdir).mkdirs();
    new File(actdir).mkdirs();
    for (File f2 : new File(actdir).listFiles()) f2.delete();
  }

  @Test
  public void test_autorestrict_noparms() throws Exception {
    String name = "noparms.eo-en.dix";
    dictools.ProcessDics.main(new String[] {"autorestrict", input, actdir+name});
    String diff=exec( "diff -bBw "+expdir+name+" "+actdir+name);
    Assert.assertEquals("Difference", "", diff);
  }

  @Test
  public void test_autorestrict_r() throws Exception {
    String name = "r.eo-en.dix";
    dictools.ProcessDics.main(new String[] {"autorestrict",  "-noheader",  "-r", "test/hitparade-en.txt", input, actdir+name});
    String diff=exec( "diff -bBw "+expdir+name+" "+actdir+name);
    Assert.assertEquals("Difference", "", diff);
  }

  @Test
  public void test_autorestrict_l() throws Exception {
    String name = "l.eo-en.dix";
    dictools.ProcessDics.main(new String[] {"autorestrict",  "-noheader", "-commentsbefore",  "-l", "test/hitparade-eo.txt", input, actdir+name});
    String diff=exec( "diff -bBw "+expdir+name+" "+actdir+name);
    Assert.assertEquals("Difference", "", diff);
  }

  @Test
  public void test_autorestrict_both() throws Exception {
    String name = "both.eo-en.dix";
    dictools.ProcessDics.main(new String[] {"autorestrict",   "-noheader", "-l", "test/hitparade-eo.txt", "-r", "test/hitparade-en.txt", input, actdir+name});
    String diff=exec( "diff -bBw "+expdir+name+" "+actdir+name);
    Assert.assertEquals("Difference", "", diff);
  }

  @Test
  public void test_autorestrict_noparms2() throws Exception {
    String name = "noparms2.eo-en.dix";
    dictools.ProcessDics.main(new String[] {"autorestrict",  "-noheader", input2, actdir+name});
    String diff=exec( "diff -bBw "+expdir+name+" "+actdir+name);
    Assert.assertEquals("Difference", "", diff);
  }

  @Test
  public void test_autorestrict_l2() throws Exception {
    String name = "l2.eo-en2.dix";
    dictools.ProcessDics.main(new String[] {"autorestrict",  "-noheader",  "-l", "test/hitparade-eo.txt", input2, actdir+name});
    String diff=exec( "diff -bBw "+expdir+name+" "+actdir+name);
    Assert.assertEquals("Difference", "", diff);
  }


  @Test
  public void test_autorestrict_l2lift() throws Exception {
    String name = "l2lift.eo-en2.dix";
    dictools.ProcessDics.main(new String[] {"autorestrict",  "-noheader",  "-lift", "-l", "test/hitparade-eo.txt", input2, actdir+name});
    String diff=exec( "diff -bBw "+expdir+name+" "+actdir+name);
    Assert.assertEquals("Difference", "", diff);
  }


  @Test
  public void test_autorestrict_l2redo() throws Exception {
    String name = "l2redo.eo-en2.dix";
    dictools.ProcessDics.main(new String[] {"autorestrict",  "-nocomments", "-noheader",  "-redo",  "-l", "test/hitparade-eo.txt", input2, actdir+name});
    String diff=exec( "diff -bBw "+expdir+name+" "+actdir+name);
    Assert.assertEquals("Difference", "", diff);
  }

  /**
   * Test of isAllowed method, of class AutorestrictBidix.
   */
  @Test
  public void testIsRestricted() {
    E ee=new E();

    ee.restriction="LR";
    assertEquals(AutorestrictBidix.isAllowed("LR", ee), true);
    assertEquals(AutorestrictBidix.isAllowed("RL", ee), false);

    ee.ignore="yes";
    assertEquals(AutorestrictBidix.isAllowed("LR", ee), false);
    assertEquals(AutorestrictBidix.isAllowed("RL", ee), false);

    ee.restriction=null;
    assertEquals(AutorestrictBidix.isAllowed("LR", ee), false);
    assertEquals(AutorestrictBidix.isAllowed("RL", ee), false);
  
    ee.ignore=null;
    assertEquals(AutorestrictBidix.isAllowed("LR", ee), true);
    assertEquals(AutorestrictBidix.isAllowed("RL", ee), true);
  }

  /**
   * Test of setYesIsAllowed method, of class AutorestrictBidix.
   */
  @Test
  public void testRemoveRestriction() {
    E ee=new E();

    ee.ignore="yes";
    AutorestrictBidix.setYesIsAllowed(ee, "LR");
    assertEquals(AutorestrictBidix.isAllowed("LR", ee), true);
    assertEquals(AutorestrictBidix.isAllowed("RL", ee), false);

    AutorestrictBidix.setYesIsAllowed(ee, "RL");
    assertEquals(AutorestrictBidix.isAllowed("LR", ee), true);
    assertEquals(AutorestrictBidix.isAllowed("RL", ee), true);
  }

}