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
import misc.eoen.DicFormatE1LineAligned;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dics.elements.dtd.Dictionary;
import dictools.utils.DicOpts;
import dictools.utils.DictionaryReader;


/**
 *
 * @author j
 */
public class ReadAndWriteBidixTest extends TestTools {
    static Dictionary dic;
    
  @BeforeClass
  public static void setUpClass() throws Exception {
    dic = new DictionaryReader("test/sample.eo-en.dix").readDic();
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Test
  public void testprintXML_std() throws IOException, InterruptedException {
    String outfile = rm("tmp_testprintXML_std-eo-en.xml");
    dic.printXMLToFile(outfile, dictools.utils.DicOpts.STD);
    String diff=exec( "diff test/correct_output_DicFormat-eo-en.xml "+outfile);
    Assert.assertEquals("Difference", "", diff);
    rm(outfile);
   }
  
 

  @Test
  public void testDicFormatE1Line() throws IOException, InterruptedException {
    String outfile = rm("tmp_testDicFormatE1Line-eo-en.xml");
    new DicFormatE1Line(dic).printXML(outfile);
    String diff=exec( "diff -b test/correct_output_DicFormatE1Line-eo-en.xml "+outfile);
    Assert.assertEquals("Difference", "", diff);
    rm(outfile);
  }

  @Test
  public void testprintXML_std1line() throws IOException, InterruptedException {
    String outfile = rm("tmp_testprintXML_std1line-eo-en.xml");
    dic.printXMLToFile(outfile, dictools.utils.DicOpts.STD_1_LINE);
    String diff=exec( "diff test/correct_output_DicFormatE1Line-eo-en.xml "+outfile);
    Assert.assertEquals("Difference", "", diff);
    rm(outfile);
   }

  
  @Test
  public void testDicFormatE1LineAligned() throws IOException, InterruptedException {
    String outfile = rm("tmp_testDicFormatE1LineAligned-eo-en.xml");
    new DicFormatE1LineAligned(dic).printXML(outfile);
    String diff=exec( "diff -bB test/correct_output_DicFormatE1LineAligned-eo-en.xml "+outfile);
    Assert.assertEquals("Difference", "", diff);
    rm(outfile);
  }


  @Test
  public void testprintXML_stdaligned1line() throws IOException, InterruptedException {
    String outfile = rm("tmp_testprintXML_stdaligned1line-eo-en.xml");
    dic.printXMLToFile(outfile, dictools.utils.DicOpts.STD_ALIGNED);
    String diff=exec( "diff test/correct_output_DicFormatE1LineAligned-eo-en.xml "+outfile);
    Assert.assertEquals("Difference", "", diff);
    rm(outfile);
   }  

  

  @Test
  public void testprintXML_aligned20_80_also_pardefs() throws IOException, InterruptedException {
    String outfile = rm("tmp_aligned20_80_also_pardefs.xml");
    DicOpts opt = new DicOpts(true, true, 0, 20, 80);
    dic.printXMLToFile(outfile, opt);
    String diff=exec( "diff test/correct_output_aligned20_80_also_pardefs.xml "+outfile);
    Assert.assertEquals("Difference", "", diff);
    rm(outfile);
   }  
  
}
