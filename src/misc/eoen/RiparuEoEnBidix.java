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

import java.io.IOException;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.Section;
import dictools.DicFix;
import dictools.utils.DicOpts;
import dictools.utils.DictionaryReader;
import java.util.Iterator;

/**
 *
 * @author j
 */
public class RiparuEoEnBidix {


      public static void main(final String[] args) throws IOException {
        String pado = "../apertium-eo-en/";

        Dictionary dic = new DictionaryReader(pado+"apertium-eo-en.eo-en.dix").readDic();
        dic.reportMetrics();

        dic.printXMLToFile(pado+"before-clean.dix", "UTF-8", dictools.utils.DicOpts.STD_ALIGNED);

        new DicFix().fix(dic);

        dic.printXMLToFile(pado+"after-fix.dix", "UTF-8", dictools.utils.DicOpts.STD_ALIGNED);


      for (Section par :  dic.sections)
        for (Iterator<E> iee = par.elements.iterator(); iee.hasNext(); ) {
          E ee = iee.next();
          if (ee.containsSymbol("np") && !ee.containsRegEx() && ee.getStreamContentForSide("L").equals(ee.getStreamContentForSide("R"))) {
            System.err.println("ee = " + ee);
            System.err.println( ee.getStreamContentForSide("L") +".equals?"+ee.getStreamContentForSide("R"));
            if (iee.hasNext())
              iee.remove();
          }
        }


        /*
        Set<String> esperanto_nouns_with_gender = new HashSet<String>(Iloj.leguTekstDosieron(pado+"res/esperanto_nouns_with_gender.txt"));
      
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


         */
                
        
        SubstractBidix.reviseRestrictions(dic, true, true);

        DicOpts opt =  dictools.utils.DicOpts.STD_ALIGNED.copy();
        //opt.noProcessingComments = true;
        dic.printXMLToFile(pado+"after-clean.dix", "UTF-8", opt);
      }

  private static boolean equals(String paradigmValue, String paradigmValue0) {
    if (paradigmValue == paradigmValue0) return true;
    if (paradigmValue == null) return false;
    return paradigmValue.equals(paradigmValue0);
  }
  
}
