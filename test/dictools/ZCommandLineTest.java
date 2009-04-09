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
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import misc.eoen.DicFormatE1LineAligned;


/**
 *
 * @author j
 */
public class ZCommandLineTest {

  public Process exe(String dir, String cmd) throws IOException, InterruptedException {

    File wd=new File(dir);

    Process p=Runtime.getRuntime().exec(cmd, null, wd);

    class Print extends Thread {

      BufferedReader br;

      private Print(InputStream errorStream) {
        br=new BufferedReader(new InputStreamReader(errorStream));
        start();
      }

      public void run() {
        try {
          String s;
          while ((s=br.readLine())!=null) {
            System.err.println(s);
          }
        } catch (IOException ex) {
          Logger.getLogger(CrossDictTest.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    new Print(p.getInputStream());
    new Print(p.getErrorStream());
    p.waitFor();



    return p;
  }
  

  @Test
  public void test_dic_reader() throws Exception {
    Process p=exe("regression_test_data/dic-reader", "./do_test.sh");
    Assert.assertEquals("Exit value", 0, p.exitValue());
  }


  @Test
  public void test_dix2tiny() throws Exception {
    Process p=exe("regression_test_data/dix2tiny", "./do_test.sh");
    Assert.assertEquals("Exit value", 0, p.exitValue());
  }

  

  @Test
  public void test_dix2trie() throws Exception {
    Process p=exe("regression_test_data/dix2trie", "./do_test.sh");
    Assert.assertEquals("Exit value", 0, p.exitValue());
  }

  

  @Test
  public void test_merge_morph() throws Exception {
    Process p=exe("regression_test_data/merge-morph", "./do_test.sh");
    Assert.assertEquals("Exit value", 0, p.exitValue());
  }

  

  @Test
  public void test_reverse_bil() throws Exception {
    Process p=exe("regression_test_data/reverse-bil", "./do_test.sh");
    Assert.assertEquals("Exit value", 0, p.exitValue());
  }

  

  @Test
  public void test_sort() throws Exception {
    Process p=exe("regression_test_data/sort", "./do_test.sh");
    Assert.assertEquals("Exit value", 0, p.exitValue());
  }

    

  @Test
  public void testBigCrossing() throws Exception {
    Process p=exe("regression_test_data/crossdict", "./do_test.sh");
    Assert.assertEquals("Exit value", 0, p.exitValue());
  }
  
}
