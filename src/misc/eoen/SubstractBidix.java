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

package misc.eoen;



import dics.elements.dtd.ContentElement;
import java.util.HashMap;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.LElement;
import dics.elements.dtd.PElement;
import dics.elements.dtd.ParElement;
import dics.elements.dtd.RElement;
import dics.elements.dtd.SectionElement;
import dics.elements.dtd.TextElement;
import dictools.DictionaryReader;
import java.util.HashSet;
import java.util.Iterator;
import misc.DicFormatE1Line;

/**
 *
 * @author j
 */
public class SubstractBidix {

      public static void main(final String[] args) {
//"apertium-forigita-malnova.eo-en.dix"
        
        //DictionaryElement dic = new DictionaryReader("../apertium-eo-en/apertium-lille.eo-en.dix").readDic();
        DictionaryElement dic = new DictionaryReader("../apertium-eo-en/apertium-eo-en.eo-en.dix").readDic();
        dic.reportMetrics();
        new DicFormatE1LineEalign(dic).setAttrSpaces(7).printXML("before-clean.dix");

        HashMap<String, EElement> hmLR = new HashMap<String, EElement>();
        HashMap<String, EElement> hmRL = new HashMap<String, EElement>();
        HashSet<String> restrics = new HashSet<String>();
        
        for (SectionElement section : dic.getSections()) {
            for (EElement ee : section.getEElements()) {
                if (!ee.isRegEx()) {
                    ContentElement l = ee.getSide("L");
                    ContentElement r = ee.getSide("R");
                    //System.out.println("======="+ee.toString()+"========");
                    
                    // L -> R
                    checkEarlierAndRestrict("LR", l, hmLR, ee);

                    // R -> L
                    checkEarlierAndRestrict("RL", r, hmRL, ee);
                }
            }

            // Transfer reasons from temp to comment
            Iterator<EElement> eei = section.getEElements().iterator();
            while (eei.hasNext()) {
                EElement ee = eei.next();
                if (!ee.isRegEx()) {
                    String reasonOfRestriction=ee.getTemp();
                    if (reasonOfRestriction != null) {
                      if ("DELETE".equals(reasonOfRestriction)) {
                        System.out.println("DELETE "+ee);
                        eei.remove();
                      } else {
                        String c = "Already is "+reasonOfRestriction;
                        if (ee.getComments()!=null) c += c + " ; "+ ee.getComments();
                        ee.setComments(c);                        
                      }
                    }
                }
            }        
        }
        
        System.out.println("restrics = " + restrics);
        //System.out.println("Updated morphological dictionary: '" + out + "'");
        //dic.printXML(out);
        
        new DicFormatE1LineEalign(dic).setAttrSpaces(7).printXML("after-clean.dix");
        String fn = dic.getFileName()+"-cleaned.dix";        
        System.out.println("fn = " + fn);
        
      }

  static void checkEarlierAndRestrict(String dir, ContentElement l, HashMap<String, EElement> hmLR, EElement ee) {
    if ("yes".equals(ee.getIgnore())) return;
    String restric = ee.getRestriction();

    
    if (restric==null ||restric.equals(dir)) {
      String key=l.toString();
      EElement existingEe=hmLR.get(key);
      if (existingEe==null) {
        hmLR.put(key, ee);
      } else {
        String oldReasonOfRestriction=ee.getTemp();
        String existingEeStr=existingEe.toString();
        //System.out.println("LR: Dobbelt indgang "+existingEe+"   "+ee);
        if (restric==null) {
          assert (oldReasonOfRestriction==null);
          ee.setTemp(existingEeStr);
          ee.setRestriction("RL");
        } else {
          if (oldReasonOfRestriction == null || existingEeStr.equals(oldReasonOfRestriction)) {
            if (existingEeStr.equals(oldReasonOfRestriction)) {
              // Exactly the same entry has been before. Just delete
              ee.setTemp("DELETE");
            }
            //ee.setComments("Already is "+existingEeStr);
            ee.setTemp(existingEeStr);
          } else {
            //ee.setComments("Already are "+existingEeStr+" "+oldReasonOfRestriction);
            ee.setTemp(existingEeStr+" "+oldReasonOfRestriction);
          }
          ee.setIgnore("yes");
          ee.setRestriction(null);
        }
      }
    }
  }
}
