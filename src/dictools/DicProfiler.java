/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Author: Enrique Benimeli Bofarull
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
import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.PardefElement;
import dics.elements.dtd.SectionElement;
import dics.elements.dtd.TextElement;
import dics.elements.utils.DicOpts;
import dictools.xml.DictionaryReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adds entries like <p><l/><r>%1234</r></p> in the start or end of all entries in a dix files,
 * for profiling purposes
 * @author Jacob Nordfalk
 */
public class DicProfiler  extends AbstractDictTool {
  public boolean insert_before = false;
  public boolean direction_lr = true;
  public static final String prepend = "%"; // §
  public static final String append  = " ";
  private static final String appendRegex = "[ \n$]"; // space or newline (in case the space is trimmed away)

  public int sequence;

  Writer profileKeysDataFile;

  public DictionaryElement generateProfileData(DictionaryElement dic) throws IOException {
    if (dic.getPardefsElement() != null)
    for (PardefElement par :  dic.getPardefsElement().getPardefElements()) {
      for (EElement ee: par.getEElements()) {
        setProfilerInfo(ee);
      }
    }

    if (dic.getSections() != null)
    for (SectionElement section : dic.getSections()) {
      for (EElement ee: section.getEElements()) {
        setProfilerInfo(ee);
      }
    }
    return dic;
  }

  private void setProfilerInfo(EElement ee) throws IOException {
    sequence++;
    String s = Integer.toString(sequence, Character.MAX_RADIX);
    s = "000".substring(Math.min(3,s.length()))+s;

    //System.err.println(s+ " " + ee);
    profileKeysDataFile.append(s).append(' ').append(ee.toString()).append('\n');

    s = prepend + s +append;
    if (direction_lr) s = "<p><l/><r>"+s+"</r></p>";
    else s = "<p><l>"+s+"</l><r/></p>";

    // s = "<p><l/><r>%1234</r></p>"
    TextElement te = new TextElement(s);

    if (insert_before) ee.getChildren().add(0, te);
    else ee.getChildren().add(te);
  }


  /**
   * Creates profiling data for a whole directory, given that the naming corresponds more or less to
   * that of the English-Esperanto directory
   * @param languagePairDirecory
   * @param direction "eo-en" or "en-eo"
   * @param dixfiles list of dix files (if null the directory is scanned for them)
   */
  public void createProfilerdirectory(String languagePairDirecory, String direction, List<String> dixfiles) throws IOException {
    File d = new File(languagePairDirecory);
    File profiler_dir = new File(d,"profiler");
    profiler_dir.mkdirs();
    
    if (dixfiles==null) { // scan dir
      dixfiles = new ArrayList<String>();
      for (String f : d.list()) if (f.startsWith("apertium-") && (f.endsWith("dix") || f.endsWith("dix.xml")) && !f.contains("post-")) dixfiles.add(f);
    }

    profileKeysDataFile = new OutputStreamWriter(new FileOutputStream(new File(profiler_dir, "profilekeys.txt")),"UTF-8");

    for (String f : dixfiles) {
      String dixtype =  f.split("\\.")[1]; // Gives eo-en for ex "apertium-eo-en.eo-en.dix"

      if (dixtype.contains(direction)) { this.direction_lr = true; this.insert_before=true; } // bidix
      else if (dixtype.length()>4) { this.direction_lr = false;this.insert_before=true; } // bidix other dir
      else if (dixtype.contains(direction.split("-")[0])) { this.direction_lr = true; this.insert_before=false; }// source lang
      else { this.direction_lr = false; this.insert_before=false; }// target lang

      msg.err("Processing "+f+" in direction "+(direction_lr?"LR":"RL"));
      DictionaryElement dic = new DictionaryReader(new File(d,f).getPath()).readDic();
      generateProfileData(dic);
      dic.printXML(new File(profiler_dir,f).getPath(),DicOpts.STD_ALIGNED_MONODIX);
    }

    profileKeysDataFile.close();
  }


  private static class Count { public int n = 0; }

  public void createResultOfProfilingDix(String profilerKeys, String profileDataFile, String profileResultFile) throws IOException {
    HashMap<String,String> keys = new LinkedHashMap<String,String>();
    HashMap<String,Count> counts= new HashMap<String,Count>();
    String line=null;
    int lineNo=0;

    try {
      msg.err("Reading "+profilerKeys);
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(profilerKeys),"UTF-8"));
      while ( (line=br.readLine())!=null) {
        lineNo++;
        int n = line.indexOf(' ');
        String k = line.substring(0,n);
        keys.put(k, line.substring(n+1));
        counts.put(k, new Count());

        //System.err.println("line.substring(0,n) = " + line.substring(0,n));
      }
      br.close();

      msg.err("Reading "+profileDataFile);
      lineNo=0;
      br = new BufferedReader(new InputStreamReader(new FileInputStream(profileDataFile),"UTF-8"));
      while ( (line=br.readLine())!=null) {
        lineNo++;
        Count count = counts.get(line);
        if (count!=null) {
          count.n++;
          //System.err.println("line = '" + line +"' "+ count.n);
        } //else msg.err("Error in line "+lineNo+" in " +profileDataFile+ "- key '"+line+"' not found in "+profilerKeys);
      }
      br.close();

      msg.err("Writing "+profileResultFile);
      PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(profileResultFile),"UTF-8"));

      for (Entry<String,String> e : keys.entrySet()) {
        String k = e.getKey();

        //System.err.println("k = '" + k + "''");
        int count = counts.get(k).n;

        //System.err.println("count = " + count);
        pw.append(Integer.toString(count)).append(' ').append(k).append(' ').append(e.getValue()).append('\n');
      }
      pw.close();

    } catch (Exception e) {
      e.printStackTrace();
      msg.err("Error at line "+lineNo+" "+line);
    }

  }


  /**
   * This method is called _during_ processing of a text, to split profile tokens from text,
   * It filters profile tokens out of standard input and prints the cleansed text to standart oputput
   * The tokens are written to a seperate file
   * @param profileFileName Filename to where token values are appended
   */
  public void collectProfileData(String profileFileName) {
    try {
      String lin;
      BufferedReader br=new BufferedReader(new InputStreamReader(System.in));

      Writer w = new FileWriter(profileFileName, true);

      Pattern p = Pattern.compile(prepend+"([0-9a-z]+)"+appendRegex);

      while ((lin=br.readLine())!=null) {
        Matcher m = p.matcher(lin);
        int prevEnd = 0;
        while (m.find()) {
          System.out.print(lin.substring(prevEnd, m.start()));
          String key = m.group(1);
          w.write(key);
          w.write("\n");
          prevEnd = m.end();
        }
        System.out.println(lin.substring(prevEnd));
      }
      w.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }


  public static void main(final String[] args) throws IOException {
    DicProfiler p = new DicProfiler();
    
    /*
    DictionaryElement dic = new DictionaryReader("../apertium-eo-en/apertium-eo-en.eo.dix.xml").readDic();
    p.generateProfileData(dic);
    dic.printXML("../apertium-eo-en/profiler/apertium-eo-en.eo.dix.xml",DicOpts.STD_ALIGNED_MONODIX);
     */
    //p.createProfilerdirectory("../apertium-eo-en/", "en-eo", null);

   p.createResultOfProfilingDix( "../apertium-eo-en/profiler/profilekeys.txt", "../apertium-eo-en/dixtools-profiledata.txt","../apertium-eo-en/dixtools-profileresult.txt");

  }

}
