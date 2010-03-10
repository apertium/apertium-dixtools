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
public class DicFix extends AbstractDictTool {


    public void fix(Dictionary dic) {
      if (dic.isMonol()) DicCross.addMissingLemmas(dic);

      Set<String> missingSymbols = S.getKnownSymbols();
      for (Sdef sdef : dic.sdefs.elements) { missingSymbols.remove(sdef.getValue()); }
      if (!missingSymbols.isEmpty()) msg.err("Adding missing symbols: "+missingSymbols);
      for (String s : missingSymbols) {
        dic.sdefs.elements.add(new Sdef(s));
      }

      // replace whitespace " " with <b/>
      for (Pardef par :  dic.pardefs.elements)
        for (E ee : par.elements) replaceSpaceWithB(ee);

      for (Section par :  dic.sections)
        for (E ee : par.elements) replaceSpaceWithB(ee);

      // Check for duplicate entries in paradigm and in each section
      for (Pardef par :  dic.pardefs.elements)
          removeDuplicates(par.elements, "paradigm "+par.name, false, null);


      // retain list of existingEntries to remove duplicates across different sections of the dictionary
      HashMap<String, E> existingEntries=null;

      for (Section par :  dic.sections) {
          existingEntries = removeDuplicates(par.elements, "section "+par.id, false, existingEntries);
      }
    }

    
    public void doFix() {
        Dictionary dic = new DictionaryReader(arguments[1]).readDic();

        String out = arguments[2];

        fix(dic);

        msg.err("Writing fixed dictonary to " + out);
        dic.printXMLToFile(out,opt);
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

    private HashMap<String,E> removeDuplicates(ArrayList<E> elements, String context, boolean hackyRemoval, HashMap<String,E> existingEntries) {
      if (existingEntries==null) existingEntries=new HashMap<String,E>();
      E ePrevious=null;
      int removed = 0;

      for (ListIterator<E> eIterator=elements.listIterator(); eIterator.hasNext();) {
        E e=eIterator.next();

        StringBuilder str = new StringBuilder(50);

        if (hackyRemoval) {
          str.append(e.lemma);
          if (e.comment != null) continue;
        }

        for (DixElement de : e.children) {
          if (hackyRemoval && de instanceof Par) {
            String parName = e.getMainParadigmName();
            if (parName != null && parName.endsWith("__n"))
              continue;
          }
          str.append(de.toString());
        }

        String key=str.toString();

        E eExisting = existingEntries.get(key);
        if (eExisting != null) {

          if (!eExisting.toString().equals(e.toString())) {
            // Entries differ somehow, find out how
            if ("yes".equals(eExisting.ignore)) continue; // leave new
            if ("yes".equals(e.ignore)) continue; // leave new

            if (eExisting.restriction == null) ; // Use existing, remove new
            else if (eExisting.restriction.equals(e.restriction)) ;  // Use existing, remove new
            else if (e.restriction == null) eExisting.restriction = null; // Use existing w/o restric, remove new
            else if (eExisting.isLR() && e.isRL()) eExisting.restriction = null; // Use existing w/o restric, remove new
            else if (eExisting.isRL() && e.isLR()) eExisting.restriction = null; // Use existing w/o restric, remove new
            else throw new IllegalStateException("Should not happen "+ eExisting + "  " + e);
          }

          // remove entry
          moveCommentsToPrevious(ePrevious, e);
          eIterator.remove();
          msg.err("Removed duplicate "+key+" in "+context);
          removed++;
        } else {
          existingEntries.put(key, e);
          ePrevious=e;
        }
      }

      if (removed>2)
        msg.err("Removed "+removed+" entries in "+context);

      return existingEntries;
    }


}
