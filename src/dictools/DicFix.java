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

import dics.elements.dtd.ContentElement;
import dictools.xml.DictionaryReader;
import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.Element;
import dics.elements.dtd.IElement;
import dics.elements.dtd.LElement;
import dics.elements.dtd.PardefElement;
import dics.elements.dtd.RElement;
import dics.elements.dtd.SectionElement;
import dics.elements.dtd.TextElement;
import dics.elements.utils.EElementList;
import dics.elements.utils.EHashMap;
import dics.elements.utils.Msg;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicFix  extends AbstractDictTool{

    /**
     * 
     */
    private DictionaryElement dic;
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
    public DicFix(DictionaryElement dic) {
        dic = dic;
    }

    /**
     * 
     * @return Undefined         
     */
    public DictionaryElement fix() {

 
        // Check for duplicate entries in paradigm
        for (PardefElement par :  dic.getPardefsElement().getPardefElements()) {
            HashSet<String> ees = new HashSet<String>();
            EElement eePrevious = null;
            boolean removed = false;
            for (Iterator<EElement> eei = par.getEElements().iterator(); eei.hasNext(); ) {
                EElement ee = eei.next();
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


        DicCross.addMissingLemmas(dic);


        EHashMap eMap = new EHashMap();
        for (SectionElement section : dic.getSections()) {
            int duplicated = 0;
            EElement eePrevious = null;

            for (Iterator<EElement> ei =section.getEElements().iterator(); ei.hasNext(); ) {
                EElement ee = ei.next();

                String e1Key = ee.toString();
                if (!eMap.containsKey(e1Key)) {
                    eMap.put(e1Key, ee);

                    for (Element irlelem : ee.getChildren()) {
                        if (irlelem instanceof ContentElement) {
                            for (Element child : ((ContentElement)irlelem).getChildren()) {
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

    public void moveCommentsToPrevious(EElement eePrevious, EElement ee) {
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
        DictionaryElement dic = dicReader.readDic();
        dicReader = null;
        setDic(dic);
        this.setOut(arguments[2]);
    }

    /**
     * 
     * 
     */
    private void actionFix() {
        DictionaryElement dicFormatted = fix();
        msg.err("Writing fixed dictonary to " + getOut());
        dicFormatted.printXML(getOut(),getOpt());
    }

    /**
     * @param dicFormatted
     *                the dicFormatted to set
     */
    private void setDic(DictionaryElement dicFormatted) {
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
