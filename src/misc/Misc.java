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
package misc;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.ParElement;
import dics.elements.dtd.RElement;
import dictools.DictionaryReader;
import java.util.HashMap;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Misc {

   /**
    * 
    */
   private DictionaryElement bilA;
   /**
    * 
    */
   private DictionaryElement bilB;
   /**
    * 
    */
   private DictionaryElement morph;

   /**
    * 
    * @param bilAFileName
    * @param bilBFileName
    */
   public Misc(final String bilAFileName, final String morphFileName) {
      DictionaryReader dicReader = new DictionaryReader(bilAFileName);
      this.bilA = dicReader.readDic();
      System.out.println("bilA.size() = " + bilA.getAllEntries().size());
      DictionaryReader dicReader2 = new DictionaryReader(morphFileName);
      this.morph = dicReader2.readDic();
      System.out.println("morph.size() = " + morph.getAllEntries().size());
   }

   /**
    * 
    */
   public final void doMisc() {
      HashMap<String, String> nps = new HashMap<String, String>();
      for (EElement ee : bilA.getAllEntries()) {
         RElement re = ee.getRight();
         if (re != null) {
            if (re.is("np")) {
               String lemma = re.getValueNoTags();
               if (re.contains("f")) {
                  System.out.println(lemma + " is 'f'");
                  nps.put(lemma, "f");
               }
               if (re.contains("m")) {
                  System.out.println(lemma + " is 'm'");
                  nps.put(lemma, "m");
               }
            /*
            if (re.contains("mf")) {
            System.out.println(lemma + " is 'mf'");
            }
             */
            }
         }
      }

      for (EElement ee : morph.getAllEntries()) {
         String lemma = ee.getLemma();
         if (lemma != null) {
            ParElement parE = ee.getParadigm();
            if (parE != null) {
               if(nps.get(lemma)!= null) {
               if (nps.get(lemma).equals("m")) {
                  parE.setValue("Afganistán__np");
               }
               if (nps.get(lemma).equals("f")) {
                  parE.setValue("Barcelona__np");
               }
               }
            }
         }
      }

      morph.printXML("nps-gender.dix", "UTF-8");
   }
}
