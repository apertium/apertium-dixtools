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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Adds entries like <p><l/><r>%1234</r></p> in the start or end of all entries in a dix files,
 * for profiling purposes
 * @author Jacob Nordfalk
 */
public class DicProfiler  extends AbstractDictTool {
    public boolean insert_before = false;
    public boolean direction_lr = true;
    public String prepend = "%";
    public String append  = "§";
    public int key;

    public DictionaryElement fix(DictionaryElement dic) {

 
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


  private void setProfilerInfo(EElement ee) {
    key++;
    String s = Integer.toString(key, Character.MAX_RADIX);
    s = "0000".substring(Math.min(4,s.length()))+s;
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
  public void createProfilerdirectory(String languagePairDirecory, String direction, List<String> dixfiles) {
    File d = new File(languagePairDirecory);
    File profiler_dir = new File(d,"profiler");
    profiler_dir.mkdirs();
    
    if (dixfiles==null) { // scan dir
      dixfiles = new ArrayList<String>();
      for (String f : d.list()) if (f.startsWith("apertium-") && (f.endsWith("dix") || f.endsWith("dix.xml")) && !f.contains("post-")) dixfiles.add(f);
    }
    for (String f : dixfiles) {
      String dixtype =  f.split("\\.")[1]; // Gives eo-en for ex "apertium-eo-en.eo-en.dix"

      if (dixtype.contains(direction)) this.direction_lr = true; // bidix
      else if (dixtype.length()>4) this.direction_lr = false; // bidix other dir
      else if (dixtype.contains(direction.split("-")[0])) this.direction_lr = true; // source lang
      else this.direction_lr = false; // target lang

      msg.err("Processing "+f+" in direction "+(direction_lr?"LR":"RL"));
      DictionaryElement dic = new DictionaryReader(new File(d,f).getPath()).readDic();
      fix(dic); 
      dic.printXML(new File(profiler_dir,f).getPath(),DicOpts.STD_ALIGNED_MONODIX);
    }
  }

  public static void main(final String[] args) {
    DicProfiler p = new DicProfiler();
    p.createProfilerdirectory("../apertium-eo-en/", "en-eo", null);
    /*
    DictionaryElement dic = new DictionaryReader("../apertium-eo-en/apertium-eo-en.eo.dix.xml").readDic();
    p.fix(dic);
    dic.printXML("../apertium-eo-en/profiler/apertium-eo-en.eo.dix.xml",DicOpts.STD_ALIGNED_MONODIX);
     */
  }

}
