/*
 * Copyright (C) 2008 Dana Esperanta Junulara Organizo http://dejo.dk/
 * Author: Jacob Nordfalk
 * 
 * This program firstSymbolIs free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program firstSymbolIs distributed in the hope that it will be useful, but
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
import java.io.IOException;
import misc.eoen.SubstractBidix;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.L;
import dics.elements.dtd.P;
import dics.elements.dtd.Par;
import dics.elements.dtd.R;
import dics.elements.dtd.Section;
import dics.elements.dtd.TextElement;
import dictools.xml.DictionaryReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import misc.DicFormatE1Line;
import misc.eoen.DicFormatE1LineAligned;

/**
 *
 * @author j
 */
public class RiparuEoEnBidix {

      public static void main(final String[] args) throws IOException {
        //Dictionary dic = new DictionaryReader("../apertium-eo-en/apertium-lille.eo-en.dix").readDic();
        Dictionary dic = new DictionaryReader("../apertium-eo-en/apertium-eo-en.eo-en.dix").readDic();
        dic.reportMetrics();
        new DicFormatE1LineAligned(dic).printXML("before-clean.dix");

        Set<String> esperanto_nouns_with_gender = new HashSet<String>(Iloj.leguTekstDosieron("res/esperanto_nouns_with_gender.txt"));
        

        
        HashMap<String, E> hm=new HashMap<String, E>();

        
        for (Section section : dic.sections) {
          Iterator<E> eei=section.elements.iterator();
          while (eei.hasNext()) {
            E ee=eei.next();
            if (!ee.containsRegEx()) {
              Par par = ee.getFirstParadigm();
              ContentElement l=ee.getFirstPart("L");
              ContentElement r=ee.getFirstPart("R");
              
              String k = l.toString() + r.toString();
              
              E exEe = hm.get(k);
              
              if (exEe == null) hm.put(k, ee);
              else {
                if (SubstractBidix.isAllowed("LR", ee) && SubstractBidix.isAllowed("LR", exEe) ||
                    SubstractBidix.isAllowed("RL", ee) && SubstractBidix.isAllowed("RL", exEe)) {
                  System.out.println("======="+exEe + "    og    " + ee.toString()+"======== hmm!!!");
                //} else if (!equals(exEe.getMainParadigmName(), ee.getMainParadigmName())) {
                //  System.out.println(k+" ======="+exEe + "    og    " + ee.toString()+"======== PARhmm!!!");
                  
                } else {
                  
                  System.out.println("======="+exEe + " findes så SLET " + ee.toString()+"!"); 
                  eei.remove();
                  
                }
              }
            }
          }
        }
          
        
        
        
        SubstractBidix.reviseRestrictions(dic, false, true);

        
        
        
        //System.out.println("Updated morphological dictionary: '" + out + "'");
        //dic.printXML(out);
        
        new DicFormatE1LineAligned(dic).setAlignP(10).setAlignR(55).printXML("after-clean.dix");
        //new DicFormatE1LineAligned(dic).printXML("after-clean.dix");
        //new DicFormatE1LineAligned(dic).setAlignP(10).setAlignR(60).printXML("slet.dix");        
      }

  private static boolean equals(String paradigmValue, String paradigmValue0) {
    if (paradigmValue == paradigmValue0) return true;
    if (paradigmValue == null) return false;
    return paradigmValue.equals(paradigmValue0);
  }
  
}
