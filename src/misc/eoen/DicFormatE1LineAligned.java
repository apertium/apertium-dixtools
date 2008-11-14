/*
 * Copyright (C) 2008 Dana Esperanta Junulara Organizo http://dejo.dk/
 * Author: Jacob Nordfalk
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

package misc.eoen;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.SectionElement;
import dictools.DictionaryReader;
import java.io.OutputStreamWriter;

/**
 * Prints a dictionaty with elements aligned.
 * Quick exampled of use:
 * <pre>
 * new DicFormatE1LineAligned(dic).setAlignP(11).setAlignR(60).printXML("after-clean.dix");
 * </pre>
 * 
 * @author Enrique Benimeli Bofarull
 * @author Jacob Nordfalk
 */
public class DicFormatE1LineAligned {

    /**
     * 
     */
    private DictionaryElement dic;
    private int alignP = 10;

    private int alignR = 55;
    
  public DicFormatE1LineAligned setAlignP(int attrSpaces) {
    this.alignP=attrSpaces;
    return this;
  }

  public DicFormatE1LineAligned setAlignR(int spaces) {
    this.alignR = spaces;
    return this;
  }

  public DicFormatE1LineAligned setDic(DictionaryElement dic) {
    this.dic=dic;
    return this;
  }

    
    
    /**
     * Initializes and prepares write a DictionaryElement 
     * @param 
     */
    public DicFormatE1LineAligned(DictionaryElement dic) {
        this.dic = dic;
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
            dos.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
            dos.write("<!--\n\tDictionary:\n");
            if (dic.getSections() != null) {
                if (dic.isBil()) {
                    dos.write("\tBilingual dictionary: " + dic.getLeftLanguage() + "-" + dic.getRightLanguage() + "\n");
                }
                dos.write("\tSections: " + dic.getSections().size() + "\n");
                int ne = 0;
                for (SectionElement section : dic.getSections()) {
                    ne += section.getEElements().size();
                }
                dos.write("\tEntries: " + ne);
            }

            if (dic.getSdefs() != null) {
                dos.write("\n\tSdefs: " + dic.getSdefs().getSdefsElements().size() + "\n");
            }
            if (dic.getPardefsElement() != null) {
                dos.write("\tParadigms: " + dic.getPardefsElement().getPardefElements().size() + "\n");
            }

            if (dic.getComments() != null) {
                dos.write(dic.getComments());
            }
            dos.write("\n-->\n");
            dos.write("<dictionary>\n");
            if (dic.getAlphabet() != null) {
                dic.getAlphabet().printXML(dos);
            }
            if (dic.getSdefs() != null) {
                dic.getSdefs().printXML(dos);
            }
            if (dic.getPardefsElement() != null) {
                dic.getPardefsElement().printXML(dos);
            }
            if (dic.getSections() != null) {
                for (final SectionElement s : dic.getSections()) {
                    String attributes = "";
                    if (s.getID() != null) {
                        attributes += " id=\"" + s.getID() + "\"";
                    }
                    if (s.getType() != null) {
                        attributes += " type=\"" + s.getType() + "\"";
                    }
                    dos.write("  <section " + attributes + ">\n");
                    for (final EElement e : s.getEElements()) {
                        //e.printXML1Line(dos);
                        e.printXML1LineAligned(dos, alignP, alignR);
                    }
                    dos.write("  </section>\n");
                }
            }
            dos.write("</dictionary>\n");
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