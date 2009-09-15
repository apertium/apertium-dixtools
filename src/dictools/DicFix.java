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
import dictools.xml.DictionaryReader;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicFix  extends AbstractDictTool {

    /**
     * 
     */
    private Dictionary dic;
    /**
     * 
     */
    private String out;
    /**
     * 
     * 
     */
    public DicFix() {
    }

    /**
     * 
     * @param dic
     */
    public DicFix(Dictionary dic) {
        this.dic = dic;
    }

    /**
     * 
     * @return Undefined         
     */
    public Dictionary fix() {

 
        // Check for duplicate entries in paradigm
        if (dic.getPardefsElement() != null)
        for (Pardef par :  dic.getPardefsElement().getPardefElements()) {
            HashSet<String> ees = new HashSet<String>();
            E eePrevious = null;
            boolean removed = false;
            for (Iterator<E> eei = par.getEElements().iterator(); eei.hasNext(); ) {
                E ee = eei.next();
                String s = ee.toStringAll();
                boolean alreadyThere = !ees.add(s);
                if (alreadyThere) { // remove if this entry already existed
                    moveCommentsToPrevious(eePrevious, ee);
                    eei.remove();
                    removed = true;
                }  else {
                    eePrevious = ee;
                }
            }
            if (removed) msg.err("Removed duplicate entries in paradigm "+par.getName());
        }

        if (dic.isMonol()) DicCross.addMissingLemmas(dic);

        HashMap<String, E> eMap = new HashMap<String, E>();
        for (Section section : dic.getSections()) {
            int duplicated = 0;
            E eePrevious = null;

            for (Iterator<E> ei =section.getEElements().iterator(); ei.hasNext(); ) {
                E ee = ei.next();

                String e1Key = ee.toString();
                if (!eMap.containsKey(e1Key)) {
                    eMap.put(e1Key, ee);

                    for (DixElement irlelem : ee.children) {
                        if (irlelem instanceof ContentElement) {
                            for (DixElement child : ((ContentElement)irlelem).children) {
                                if (child instanceof TextElement) {
                                    TextElement tE = (TextElement) child;
                                    String v = tE.getValue();
                                    v = v.replaceAll("\\s", "<b/>");
                                    tE.setValue(v);
                                }
                            }
                        }
                    }

                    eePrevious = ee;
                } else {
                    String left = ee.getValue("L");
                    String right = ee.getValue("R");
                    msg.err("Duplicated: " + left + "/" + right + "\n");
                    duplicated++;
                    moveCommentsToPrevious(eePrevious, ee);
                    ei.remove();
                }
            }
            String errorMsg = duplicated + " duplicated entries in section '" + section.getID() + "'\n";
            msg.err(errorMsg);
        }
        dic.printXML(this.getOut(),getOpt());
        return dic;
    }

    /**
     * 
     * 
     */
    public void doFormat() {
        processArguments();
        actionFix();
    }

    private void moveCommentsToPrevious(E eePrevious, E ee) {
        // remove if this entry already existed
        if (eePrevious!=null&&!(ee.getPrependCharacterData()+ee.getAppendCharacterData()).trim().isEmpty()) {
            eePrevious.addAppendCharacterData("\n"+ee.getPrependCharacterData()+ee.getAppendCharacterData());
        }
    }

    /**
     * 
     * 
     */
    private void processArguments() {
      
        msg.err("Reading " + arguments[1]);
        DictionaryReader dicReader = new DictionaryReader(arguments[1]);
        Dictionary dic = dicReader.readDic();
        dicReader = null;
        setDic(dic);
        this.setOut(arguments[2]);
    }

    /**
     * 
     * 
     */
    private void actionFix() {
        Dictionary dicFormatted = fix();
        msg.err("Writing fixed dictonary to " + getOut());
        dicFormatted.printXML(getOut(),getOpt());
    }

    /**
     * @param dicFormatted
     *                the dicFormatted to set
     */
    private void setDic(Dictionary dicFormatted) {
        this.dic = dicFormatted;
    }

    /**
     * @return the out
     */
    public String getOut() {
        return out;
    }

    /**
     * @param out
     *                the out to set
     */
    public void setOut(String out) {
        this.out = out;
    }
}
