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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import dics.elements.dtd.ContentElement;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.Element;
import dics.elements.dtd.PardefElement;
import dics.elements.dtd.SectionElement;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import junit.framework.Assert;
import misc.DicFormatE1Line;
import misc.eoen.DicFormatE1LineAligned;


/**
 *
 * @author j
 */
public class ReadAndWriteMonodixTest {

    public ReadAndWriteMonodixTest() {
    }

      static DictionaryElement dic;
    
  @BeforeClass
  public static void setUpClass() throws Exception {
    dic = new DictionaryReader("test/sample.metadix").readDic();

  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  private static String rm(String filename) {
    new File(filename).delete();
    return filename;
  }
  
  public static String exec(String cmd) throws IOException, InterruptedException {
    Process p=Runtime.getRuntime().exec(cmd);
    BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
    String diff="";
    String s;
    while ((s=br.readLine())!=null) {
      diff=diff+s+"\n";
    }
    p.waitFor();
    if (p.exitValue()!=0) Assert.fail(cmd+" reported an error");
    return diff;
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
    
    /*
    for (PardefElement pe : dic.getPardefsElement().getPardefElements()) {
      System.err.println("pe = " + pe);
    }*/

    String pe = dic.getPardefsElement().getPardefElements().get(0).toString();
    Assert.assertEquals("<S__encimp><e><l>-en</l><r>en<prn><enc><adv></r></e><e><l>-la</l><r>le<prn><enc><p3><f><sg></r></e>", pe);
    
    /*
    for (SectionElement section : dic.getSections()) {
      for (EElement ee : section.getEElements()) {
        System.err.println("ee = " + ee);
      }
    }*/
    String ee = dic.getSections().get(0).getEElements().get(0).toString();
    Assert.assertEquals("<e><i>am</i><par n=\"ach/e[T]er__vblex\"/> </e>", ee);
  } 

  
  @Test
  public void testDicFormat() throws IOException, InterruptedException {
    String outfile = rm("tmp_testDicFormat.xml");

    DicFormat df = new DicFormat(dic);
    df.setOut(outfile);
    DictionaryElement dicFormatted = df.format();
    //dicFormatted.printXML(outfile,df.getOpt());
    String diff=exec( "diff test/correct_output_DicFormat.xml "+outfile);    
    Assert.assertEquals("Difference", "", diff);
    rm(outfile);
   }

  @Test
  public void testprintXML_std() throws IOException, InterruptedException {
    String outfile = rm("tmp_testprintXML_std.xml");
    dic.printXML(outfile, dics.elements.utils.DicOpts.std);
    String diff=exec( "diff test/correct_output_DicFormat.xml "+outfile);
    Assert.assertEquals("Difference", "", diff);
    rm(outfile);
   }
  
 

  @Test
  public void testDicFormatE1Line() throws IOException, InterruptedException {
    String outfile = rm("tmp_testDicFormatE1Line.xml");
    new DicFormatE1Line(dic).printXML(outfile);
    String diff=exec( "diff -b test/correct_output_DicFormatE1Line.xml "+outfile);
    Assert.assertEquals("Difference", "", diff);
    rm(outfile);
  }

  @Test
  public void testprintXML_std1line() throws IOException, InterruptedException {
    String outfile = rm("tmp_testprintXML_std1line.xml");
    dic.printXML(outfile, dics.elements.utils.DicOpts.std1line);
    String diff=exec( "diff -b test/correct_output_DicFormatE1Line.xml "+outfile);
    Assert.assertEquals("Difference", "", diff);
    rm(outfile);
   }

}