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

import dictools.xml.DictionaryReader;
import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.Element;
import dics.elements.dtd.IElement;
import dics.elements.dtd.LElement;
import dics.elements.dtd.RElement;
import dics.elements.dtd.SectionElement;
import dics.elements.dtd.TextElement;
import dics.elements.utils.EElementList;
import dics.elements.utils.EHashMap;
import dics.elements.utils.Msg;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicFix  extends AbstractDictTool{

    /**
     * 
     */
    private DictionaryElement dicFormatted;
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
        dicFormatted = dic;
    }

    /**
     * 
     * @return Undefined         
     */
    public DictionaryElement format() {
        EHashMap eMap = new EHashMap();
        for (SectionElement section : dicFormatted.getSections()) {
            int duplicated = 0;
            EElementList elements = section.getEElements();
            for (EElement e : elements) {
                String e1Key = e.toString();
                if (!eMap.containsKey(e1Key)) {
                    eMap.put(e1Key, e);
                    IElement iE = e.getI();
                    if (iE != null) {
                        for (Element child : e.getI().getChildren()) {
                            if (child instanceof TextElement) {
                                TextElement tE = (TextElement) child;
                                String v = tE.getValue();
                                v = v.replaceAll("\\s", "<b/>");
                                tE.setValue(v);
                            }
                        }
                    }
                    LElement lE = e.getLeft();
                    if (lE != null) {
                        for (Element child : e.getLeft().getChildren()) {
                            if (child instanceof TextElement) {
                                TextElement tE = (TextElement) child;
                                String v = tE.getValue();
                                v = v.replaceAll("\\s", "<b/>");
                                tE.setValue(v);
                            }
                        }
                    }
                    RElement rE = e.getRight();
                    if (rE != null) {

                        for (Element child : e.getRight().getChildren()) {
                            if (child instanceof TextElement) {
                                TextElement tE = (TextElement) child;
                                String v = tE.getValue();
                                v = v.replaceAll("\\s", "<b/>");
                                tE.setValue(v);
                            }
                        }
                    }

                } else {
                    String left = e.getValue("L");
                    String right = e.getValue("R");
                    msg.err("Duplicated: " + left + "/" + right + "\n");
                    duplicated++;
                }
            }
            String errorMsg = duplicated + " duplicated entries in section '" + section.getID() + "'\n";
            msg.err(errorMsg);
        }
        dicFormatted.printXML(this.getOut(),getOpt());
        return dicFormatted;
    }

    /**
     * 
     * 
     */
    public void doFormat() {
        processArguments();
        actionFormat();
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
        setDicFormatted(dic);
        this.setOut(arguments[2]);
    }

    /**
     * 
     * 
     */
    private void actionFormat() {
        DictionaryElement dicFormatted = format();
        msg.err("Writing formatted dictonary to " + getOut());
        dicFormatted.printXML(getOut(),getOpt());
    }

    /**
     * @param dicFormatted
     *                the dicFormatted to set
     */
    private void setDicFormatted(DictionaryElement dicFormatted) {
        this.dicFormatted = dicFormatted;
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
