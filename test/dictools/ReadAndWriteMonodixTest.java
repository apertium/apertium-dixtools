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

import java.io.IOException;

import junit.framework.Assert;
import misc.DicFormatE1Line;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dics.elements.dtd.Dictionary;
import dictools.utils.DictionaryReader;


/**
 *
 * @author j
 */
public class ReadAndWriteMonodixTest extends TestTools {

    public ReadAndWriteMonodixTest() {
    }

      static Dictionary dic;
    
  @BeforeClass
  public static void setUpClass() throws Exception {
    dic = new DictionaryReader("test/sample.metadix").readDic();
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
   * Test of getDic method, of class DicReader.
   */
  @Test
  public void testGetDic() throws IOException, InterruptedException {

    String pe = dic.pardefs.elements.get(0).toString();
    System.err.println("pe = " + pe);
    Assert.assertEquals("<S__encimp><e><l>-en</l><r>en<prn><enc><adv></r></e><e><l>-la</l><r>le<prn><enc><p3><f><sg></r></e>", pe);


    String ee = dic.sections.get(0).elements.get(0).toString();
    System.err.println("ee = " + ee);
    Assert.assertEquals("<e><i>am</i><par n=\"ach/e[T]er__vblex\" prm=\"n\"/></e>", ee);
  } 

/*   
  @Test
  public void testDicFormatE1LineAligned() throws IOException, InterruptedException {
    //new DicFormatE1LineAligned(dic).printXML("tmp_test.xml") 
    String outfile = rm("tmp_testDicFormatE1LineAligned.xml");
    new DicFormatE1LineAligned(dic).printXML(outfile);
    String diff=exec( "diff -wbB test/sample.metadix "+outfile);
    Assert.assertTrue("Difference: "+diff, diff.isEmpty());
    rm(outfile);
  }
*/  

  /*
  @Test
  public void testDicFormat() throws IOException, InterruptedException {
    if (DixtoolsTestSuite.onlyCLI) return;
    String outfile = rm("tmp_testDicFormat.xml");

    DicFix df = new DicFix(dic);
    df.setOut(outfile);
    Dictionary dicFormatted = df.fix();
    //dicFormatted.printXML(outfile,df.getOpt());
    String diff=exec( "diff test/correct_output_DicFormat.xml "+outfile);    

    // dic is changes
    dic = new DictionaryReader("test/sample.metadix").readDic();

    Assert.assertTrue("Difference: "+diff, diff.isEmpty());
    rm(outfile);
   }
*/
  @Test
  public void testprintXML_std() throws IOException, InterruptedException {
    String outfile = rm("tmp_testprintXML_std.xml");
    dic.printXML(outfile, dictools.utils.DicOpts.STD);
    String diff=exec( "diff test/correct_output_DicFormat.xml "+outfile);
    Assert.assertTrue("Difference: "+diff, diff.isEmpty());
    rm(outfile);
   }
  
  @Test
  public void testDicFormatE1Line() throws IOException, InterruptedException {
    String outfile = rm("tmp_testDicFormatE1Line.xml");
    new DicFormatE1Line(dic).printXML(outfile);
    String diff=exec( "diff -b test/correct_output_DicFormatE1Line.xml "+outfile);
    Assert.assertTrue("Difference: "+diff, diff.isEmpty());
    rm(outfile);
  }

  @Test
  public void testprintXML_std1line() throws IOException, InterruptedException {
    String outfile = rm("tmp_testprintXML_std1line.xml");
    dic.printXML(outfile, dictools.utils.DicOpts.STD_1_LINE);
    String diff=exec( "diff -b test/correct_output_DicFormatE1Line.xml "+outfile);
    Assert.assertTrue("Difference: "+diff, diff.isEmpty());
    rm(outfile);
   }

}