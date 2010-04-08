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

import dictools.cross.DicCross;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import dics.elements.dtd.ContentElement;
import dics.elements.dtd.Dictionary;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.E;
import dics.elements.dtd.Par;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.S;
import dics.elements.dtd.Sdef;
import dics.elements.dtd.Section;
import dics.elements.dtd.TextElement;
import dictools.utils.DicOpts;
import dictools.utils.DictionaryReader;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Set;

/**
 *
 * @author Enrique Benimeli Bofarull
 *
 */
public class DicStemCheck extends AbstractDictTool {


    public void check(Dictionary dic) {
      int wrongIds  = 0;
      for(Section sect : dic.sections){
          for(E ee : sect.elements){
              // check only simple entries
              if(ee.children.size() == 2){
                  
                String identity = ee.children.get(0).getValue();
                String paradigm = ee.children.get(1).getValue();


                // only check stemmed paradigms
                if(paradigm.contains("/") && ee.lemma.equals(identity)){
                    msg.err("Lemma \"" + ee.lemma + "\" has equal identity, but paradigm is stemmed (" + paradigm + ")\n");
                    wrongIds++;
                }
              }
              
          }
      }
      msg.err("In total,  " + wrongIds + " malformed lemmas were found. \n");
    }


    public void doCheck() {
        Dictionary dic = new DictionaryReader(arguments[1]).readDic();

        check(dic);
    }


}
