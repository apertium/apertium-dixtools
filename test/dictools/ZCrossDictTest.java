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
public class ZCrossDictTest {
  private static String rm(String filename) {
    //System.err.println("filename = " + filename);
    File f = new File(filename);
    if (f.isDirectory()) for (File f2 : f.listFiles()) rm(f2.getPath());
    new File(filename).delete();
    return filename;
  }
  
  public static String exec(String cmd) throws IOException, InterruptedException {
    return ReadAndWriteMonodixTest.exec(cmd);
  }
  
  
  @Test
  public void testBigCrossing() throws Exception {
    if (DixtoolsTestSuite.onlyCLI) return;
    String outfile = rm("dix");
    new File(outfile).mkdir();

    String path = "regression_test_data/crossdict/input/";
    String[] argsx = {"cross-param", 
    "-bilAB", "-r", path+"apertium-es-ca.es-ca.dix",
    "-bilBC", "-r", path+"apertium-en-ca.en-ca.dix", 
    "-monA", path+"apertium-es-ca.es.dix", 
    "-monC", path+"apertium-en-ca.en.metadix", 
    "-cross-model", path+"../../../../apertium-dixtools/schemas/cross-model.xml"};
     dictools.ProcessDics.main(argsx);  

    String diff=exec( "diff  -bBw -x .svn  "+path+"../expected_output "+outfile);    
    Assert.assertEquals("Difference", "", diff);
    rm(outfile);  
  }

}