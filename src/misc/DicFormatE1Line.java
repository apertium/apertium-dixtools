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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.SectionElement;
import dics.elements.utils.DicOpts;
import dictools.AbstractDictTool;
import dictools.DictionaryReader;
import java.io.OutputStreamWriter;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicFormatE1Line extends AbstractDictTool {

    /**
     * 
     */
    private DictionaryElement dic;

    /**
     * 
     * @param dicFileName
     */
    public DicFormatE1Line(String dicFileName) {
        DictionaryReader dicReader = new DictionaryReader(dicFileName);
        dic = dicReader.readDic();
        setOpt(dics.elements.utils.DicOpts.std1line);
    }

    
    /**
     * Initializes and prepares write a DictionaryElement 
     * @param dic The dictionary element
     */
    public DicFormatE1Line(DictionaryElement dic) {
        this.dic = dic;
        setOpt(dics.elements.utils.DicOpts.std1line);
    }

    /**
     * 
     * @param fileName
     */
    public void printXML(final String fileName) {
        this.printXML(fileName, "UTF-8");
        dic.setXmlEncoding("UTF-8");
    }

    /**
     * 
     * @param fileName
     */
    public void printXML(final String fileName, final String encoding) {
        BufferedOutputStream bos;
        FileOutputStream fos;
        OutputStreamWriter dos;

        dic.setFileName(fileName);
        try {
            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, encoding);
            dos.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
            dos.append("<!--\n\tDictionary:\n");
            if (dic.getSections() != null) {
                if (dic.isBil()) {
                    dos.append("\tBilingual dictionary: " + dic.getLeftLanguage() + "-" + dic.getRightLanguage() + "\n");
                }
                dos.append("\tSections: " + dic.getSections().size() + "\n");
                int ne = 0;
                for (SectionElement section : dic.getSections()) {
                    ne += section.getEElements().size();
                }
                dos.append("\tEntries: " + ne);
            }

            if (dic.getSdefs() != null) {
                dos.append("\n\tSdefs: " + dic.getSdefs().getSdefsElements().size() + "\n");
            }
            if (dic.getPardefsElement() != null) {
                dos.append("\tParadigms: " + dic.getPardefsElement().getPardefElements().size() + "\n");
            }

            if (dic.getComments() != null) {
                dos.append(dic.getComments());
            }
            dos.append("\n-->\n");
            dos.append("<dictionary>\n");
            if (dic.getAlphabet() != null) {
                dic.getAlphabet().printXML(dos,getOpt());
            }
            if (dic.getSdefs() != null) {
                dic.getSdefs().printXML(dos,getOpt());
            }
            if (dic.getPardefsElement() != null) {
                dic.getPardefsElement().printXML(dos,getOpt());
            }
            if (dic.getSections() != null) {
                DicOpts optNow = opt.copy().setNow1line(true);
                for (final SectionElement s : dic.getSections()) {
                    String attributes = "";
                    if (s.getID() != null) {
                        attributes += " id=\"" + s.getID() + "\"";
                    }
                    if (s.getType() != null) {
                        attributes += " type=\"" + s.getType() + "\"";
                    }
                    dos.append("  <section " + attributes + ">\n");
                    for (final EElement e : s.getEElements()) {
                         e. printXML(dos, optNow);
                    }
                    dos.append("  </section>\n");
                }
            }
            dos.append("</dictionary>\n");
            fos = null;
            bos = null;
            dos.close();
            dos = null;
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final Exception eg) {
            eg.printStackTrace();
        }
    }
}
