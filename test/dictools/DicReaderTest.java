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
import java.io.InputStreamReader;
import junit.framework.Assert;
import misc.eoen.DicFormatE1LineAligned;


/**
 *
 * @author j
 */
public class DicReaderTest {

    public DicReaderTest() {
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
   * Test of getDic method, of class DicReader.
   */
  @Test
  public void testGetDic() throws IOException, InterruptedException {
    System.out.println("getDic");
    DictionaryElement dic = new DictionaryReader("test/sample.metadix").readDic();
    
    for (PardefElement pe : dic.getPardefsElement().getPardefElements()) {
      System.err.println("pe = " + pe);
    }
    
    
    for (SectionElement section : dic.getSections()) {
      for (EElement ee : section.getEElements()) {
        System.err.println("ee = " + ee);
      }
    }

 
    new DicFormatE1LineAligned(dic).printXML("tmp_test.xml");
    String cmd = "diff -bBiw test/sample.metadix tmp_test.xml";
    Process p = Runtime.getRuntime().exec(cmd);
    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String diff = "";
    String s;
    while ((s=br.readLine())!=null) diff = diff + s + "\n";
    Assert.assertEquals("Difference", "", diff);
    //DicFormat df = new DicFormat(dic);
    //df.format().printXML("tmp_test.xml");
    //DicFormatE1LineAligned df = new DicFormat(dic);
    //df.format().printXML("tmp_test.xml");

  }


}