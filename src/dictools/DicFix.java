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
import dics.elements.dtd.Pardef;
import dics.elements.dtd.Section;
import dics.elements.dtd.TextElement;
import dictools.utils.DicOpts;
import dictools.utils.DictionaryReader;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicFix extends AbstractDictTool {


    public void fix(Dictionary dic) {
      if (dic.isMonol()) DicCross.addMissingLemmas(dic);

      // replace whitespace " " with <b/>
      for (Pardef par :  dic.pardefs.elements)
        for (E ee : par.elements) replaceSpaceWithB(ee);

      for (Section par :  dic.sections)
        for (E ee : par.elements) replaceSpaceWithB(ee);

      // Check for duplicate entries in paradigm and in each section
      for (Pardef par :  dic.pardefs.elements)
          removeExactDuplicates(par.elements, "paradigm "+par.name);

      for (Section par :  dic.sections)
          removeExactDuplicates(par.elements, "section "+par.id);
    }

    
    public void doFormat() {
        Dictionary dic = new DictionaryReader(arguments[1]).readDic();

        String out = arguments[2];

        fix(dic);

        msg.err("Writing fixed dictonary to " + out);
        dic.printXML(out,opt);
    }

    private static void moveCommentsToPrevious(E eePrevious, E ee) {
        // remove if this entry already existed
        if (eePrevious!=null&&!(ee.getPrependCharacterData()+ee.getAppendCharacterData()).trim().isEmpty()) {
            eePrevious.addAppendCharacterData("\n"+ee.getPrependCharacterData()+ee.getAppendCharacterData());
        }
    }



    public static void replaceSpaceWithB(E ee) {
      for (DixElement irlelem : ee.children) {
        if (irlelem instanceof ContentElement) {
          for (DixElement child : ((ContentElement) irlelem).children) {
            if (child instanceof TextElement) {
              TextElement tE=(TextElement) child;
              tE.text=tE.text.replaceAll("\\s", "<b/>");
            }
          }
        }
      }
    }

    private void removeExactDuplicates(ArrayList<E> elements, String context) {
      HashSet<String> ees=new HashSet<String>();
      E eePrevious=null;

      for (ListIterator<E> eei=elements.listIterator(); eei.hasNext();) {
        E ee=eei.next();
        String s=ee.toString();

        boolean alreadyThere=!ees.add(s);
        if (alreadyThere) {
          // remove if this entry already existed
          moveCommentsToPrevious(eePrevious, ee);
          eei.remove();
          msg.err("Removed duplicate "+s+" in "+context);
        } else {
          eePrevious=ee;
        }
      }
    }


}
