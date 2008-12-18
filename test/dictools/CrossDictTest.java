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
public class CrossDictTest {

  
  @Test
  public void testBigCrossingFromCommandline() throws IOException, InterruptedException {
    //if (2>1) return;
//    ProcessDics.main("[cross-param", "-bilAB", "-r", "apertium-es-ca.es-ca.dix", "-bilBC", "-r", "apertium-en-ca.en-ca.dix", "-monA", "apertium-es-ca.es.dix", "-monC", "apertium-en-ca.en.metadix", "-cross-model", "../../../../apertium-dixtools/schemas/cross-model.xml]");
    
    String cmd = "./do_test.sh";
    File wd = new File("regression_test_data/crossdict");


    System.err.println("wd = " + wd.exists());
    
    Process p = Runtime.getRuntime().exec(cmd, null, wd);
    
    class Print extends Thread {
    BufferedReader br;

      private Print(InputStream errorStream) {
         br = new BufferedReader(new InputStreamReader(errorStream));
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
    
    

    Assert.assertEquals("Exit value", 0, p.exitValue());

  }


}
