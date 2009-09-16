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
package dics.elements.dtd;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import dics.elements.utils.DicOpts;
import dics.elements.utils.DicTools;
import dics.elements.utils.ElementList;

/**
 *
 * @author Enrique Benimeli Bofarull
 *
 */
public class Dictionary extends DixElement {

    public HeaderElement header;
    public Alphabet alphabet;
    public Sdefs sdefs;
    public Pardefs pardefs;
    
    public ArrayList<Section> sections;
    
    public int nEntries;
    
    public int nShared;
    
    public int nDifferent;
    
    public HashMap<String, ArrayList<Pardef>> equivPar;
    
    public String type;
    
    public String fileName;
    
    public String filePath;
    
    public String leftLanguage;
    
    public String rightLanguage;
    
    public String folder;
    
    public String xmlEncoding = "UTF-8";
    
    public String xmlVersion;

    
    public Dictionary() {
      super("dictionary");
        sections = new ArrayList<Section>();
    }


// TODO UCdetector: Remove unused code: 
//     /**
//      *
//      * @param elementMap
//      */
//     public Dictionary(HashMap<String, ArrayList<E>> elementMap, Dictionary dic) {
//       this();
//         Section sectionElement = new Section("main", "standard");
//         addSection(sectionElement);
//         setAlphabet(dic.getAlphabet());
//         setSdefs(dic.getSdefs());
// 
//         Set keySet = elementMap.keySet();
//         Iterator it = keySet.iterator();
// 
//         while (it.hasNext()) {
//             String key = (String) it.next();
//             ArrayList<E> eList = elementMap.get(key);
//             for (E e : eList) {
//                 addEElement(e);
//             }
//         }
//     }

    /**
     *
     * @param dic
     */
    public Dictionary(Dictionary dic) {
        this();
        Section sectionElement = new Section("main", "standard");
        sections.add(sectionElement);
        alphabet = dic.alphabet;
        sdefs = dic.sdefs;

        if (dic.isMonol()) {
            pardefs = dic.pardefs;
        }

        Section sectionElementMain = dic.getSection("main");
        ArrayList<E> eList = sectionElementMain.elements;
        for (E e : eList) {
            if (!e.shared) {
                (getEntriesInMainSection()).add(e);
            }
        }
    }

    /**
     *
     * @param eList
     */
    public void setMainSection(ArrayList<E> eList) {
        for (Section section : sections) {
            if (section.id.equals("main")) {
                Section sectionElementMain = new Section(section.id, section.type);
                sections.remove(section);
                for (E e : eList) {
                    sectionElementMain.elements.add(e);
                }
                sections.add(sectionElementMain);
            }
        }
    }

    public static final String BIL = "BIL";
    public static final String MONOL = "MONOL";
    /**
     *
     * @return Undefined     
     */
    public boolean isMonol() {
    	return MONOL.equals(type);
    }

    /**
     *
     * @return Undefined     
     */
    public boolean isBil() {
    	return BIL.equals(type);
    }

    /**
     * 
     * @param fileName
     */
    public void printXML(String fileName, DicOpts opt) {
        printXML(fileName, this.xmlEncoding, opt);
    }

    /**
     * 
     * @param fileName
     * @param encoding
     */
    public void printXML(String fileName, String encoding, DicOpts opt) {
        fileName = fileName;
        try {
            Writer dos;
              if ("-".equals(fileName)) {
                 dos = new OutputStreamWriter(System.out);
              } else {          
                System.err.println("Writing file " + fileName);
                FileOutputStream fos = new FileOutputStream(fileName);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                dos = new OutputStreamWriter(bos, encoding);
              }
            dos.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
            dos.append("<!--\n\tDictionary:\n");
            if (sections != null) {
                if (isBil()) {
                    dos.append("\tBilingual dictionary: " + leftLanguage + "-" + rightLanguage + "\n");
                }
                dos.append("\tSections: " + sections.size() + "\n");
                int ne = 0;
                for (Section section : sections) {
                    ne += section.elements.size();
                }
                dos.append("\tEntries: " + ne);
            }
            if (sdefs != null) {
                dos.append("\n\tSdefs: " + sdefs.elements.size() + "\n");
            }
            if (pardefs != null) {
                dos.append("\tParadigms: " + pardefs.elements.size() + "\n");
            }
            if (opt.originalArguments != null) {
                dos.append("\tLast processed by: apertium-dixtools");
                for (String s : opt.originalArguments) dos.append(' ').append(s);
                dos.append("\n");
            }
            dos.append(processingComments);

            dos.append("\n-->\n");
            dos.append("<dictionary>\n");
            if (alphabet != null) {
                alphabet.printXML(dos, opt);
            }
            if (sdefs != null) {
                sdefs.printXML(dos, opt);
            }
            if (pardefs != null) {
                DicOpts optNow = (opt.pardefAlignOpts==null?opt.copy():opt.pardefAlignOpts).setNowAlign(opt.pardefElementsAligned);
                pardefs.printXML(dos, optNow);
            }
            if (sections != null) {
                DicOpts optNow = opt.copy().setNowAlign(opt.sectionElementsAligned);
                for (Section s : sections) {
                    s.printXML(dos, optNow);
                }
            }
            dos.append("</dictionary>\n");
            dos.close();
            dos = null;
        } catch (Exception eg) {
            eg.printStackTrace();
        }
    }


// TODO UCdetector: Remove unused code: 
//     /**
//      * 
//      * @param fileName
//      */
//     public void printXMLXInclude(String fileName, DicOpts opt) {
//         this.printXMLXInclude(fileName, this.getXmlEncoding(), opt);
//     }

    /**
     * 
     * @param fileName
     * @param encoding
    private void printXMLXInclude(String fileName, String encoding, DicOpts opt) {
        BufferedOutputStream bos;
        FileOutputStream fos;
        OutputStreamWriter dos;
        setFileName(fileName);

        try {
            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, encoding);
            dos.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
            dos.append("<!--\n\tDictionary:\n");
            if (sections != null) {
                if (isBil()) {
                    dos.append("\tBilingual dictionary: " + getLeftLanguage() + "-" + getRightLanguage() + "\n");
                }
                dos.append("\tSections: " + sections.size() + "\n");
                dos.append("\tEntries: " + (sections.get(0)).getEElements().size());
            }
            if (sdefs != null) {
                dos.append("\n\tSdefs: " + sdefs.getSdefsElements().size() + "\n");
            }
            dos.append("");

            dos.append(processingComments);

            dos.append("\n-->\n");
            dos.append("<dictionary>\n");
            if (alphabet != null) {
                alphabet.printXML(dos, opt);
            }
            String includeStr = "<xi:include xmlns:xi=\"http://www.w3.org/2001/XInclude\"";
            if (sdefs != null) {
                dos.append("\t" + includeStr + " href=\"" + getFolder() + "/sdefs.dix\"/>\n");

                sdefs.printXML(getFolder() + "/sdefs.dix", opt);
            }
            if (elements != null) {
                dos.append("\t" + includeStr + " href=\"" + getFolder() + "/elements.dix\"/>\n");
                elements.printXML(getFolder() + "/elements.dix", opt);
            }

            for (Section section : sections) {
                //section.printXMLXInclude(this.getFolder() + "/" , "UTF-8");
                ArrayList<String> includes = section.getIncludes();
                String attributes = "";
                if (section.getID() != null) {
                    attributes += " id=\"" + section.getID() + "\"";
                }
                if (section.getType() != null) {
                    attributes += " type=\"" + section.getType() + "\"";
                }
                dos.append(tab(1) + "<" + section.TAGNAME + "" + attributes + ">\n");
                if (includes != null) {
                    for (String s : includes) {
                        dos.append("\t" + s + "\n");
                    }
                }
                dos.append(tab(1) + "</" + section.TAGNAME + ">\n");
            }
            dos.append("</dictionary>\n");
            fos = null;
            bos = null;
            dos.close();
            dos = null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception eg) {
            eg.printStackTrace();
        }
    }
     */

    /**
     *
     * @return The 'e' elements
     */
    public ArrayList<E> getEntriesInMainSection() {
    	return getSection("main").elements;
    }
    
    /**
    *
    * @param id
    * @return Undefined     */
   public Section getSection(String id) {
       for (Section section : sections) {
           if (section.id.equals(id)) {
               return section;
           }
       }
       return null;
   }


    /**
     *
     * @return Undefined     */
    public ArrayList<E> getEntriesInFirstSection() {
        for (Section s : sections) {
            return s.elements;
        }
        return null;
    }

    /**
     * 
     * @return Number of entries
     */
    public int getNumberOfEntries() {
        int c = 0;
        for (Section s : sections) {
            c += s.elements.size();
        }
        return c;
    }

    public void countEntries() {
        nEntries = 0;
        nShared = 0;
        nDifferent = 0;
        ArrayList<E> list = getEntriesInMainSection();
        for (E e : list) {
              nEntries++;
            if (e.shared) {
                nShared++;
            } else {
                if (!e.shared) {
                    nDifferent++;
                }
            }
        }
    }

    public void reportMetrics() {
        countEntries();
        System.err.println(nShared + " shared entries.");
        System.err.println(nDifferent + " not shared entries.");
        System.err.println(nEntries + " own entries.");
    }

    
    public void removeNotCommon() {
        ArrayList<E> elements = getEntriesInMainSection();
        ArrayList<E> elementsCopy = new ArrayList<E>(elements);

        for (E e : elementsCopy) {
            if (!e.shared) {
                elements.remove(e);
            }
        }
    }

    public void reverse() {
        for (Section section : sections) {
            ArrayList<E> elements = section.elements;

            for (E ee : elements) {
                ArrayList<DixElement> children = ee.children;

                if (ee.restriction != null) {
                    if (ee.restriction.equals("LR")) {
                        ee.restriction="RL";
                    } else {
                        if (ee.restriction.equals("RL")) {
                            ee.restriction="LR";
                        }
                    }
                }

                String currentSlr = null;
                String currentSrl = null;
                if (ee.slr != null) {
                    currentSlr = ee.slr;
                    ee.slr = null;
                }
                if (ee.srl != null) {
                    currentSrl = ee.srl;
                    ee.srl = null;
                }

                if (currentSlr != null) {
                    ee.srl = currentSlr;

                }
                if (currentSrl != null) {
                    ee.slr =currentSrl;
                }


                for (DixElement e : children) {
                    if (e instanceof P) {
                        L lE = ((P) e).l;
                        R rE = ((P) e).r;
                        // String auxValue = lE.getValue();
                        ElementList auxChildren = lE.children;
                        // lE.setValue(rE.getValue());
                        lE.children = rE.children;
                        // rE.setValue(auxValue);
                        rE.children =auxChildren;
                        ((P) e).l = lE;
                        ((P) e).r = (rE);
                    }
                }
            }
        }
    }

    /**
     * 
     * @return Is there a header defined?
     */
    public boolean isHeaderDefined() {
        return (header != null);
    }
}
