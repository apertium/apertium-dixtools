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

    protected HeaderElement header;
    protected Alphabet alphabet;
    protected Sdefs sdefs;
    protected Pardefs pardefs;
    
    protected ArrayList<Section> sections;
    
    protected int nEntries;
    
    protected int nShared;
    
    protected int nDifferent;
    
    protected HashMap<String, ArrayList<Pardef>> equivPar;
    
    private String type;
    
    private String fileName;
    
    private String filePath;
    
    private String leftLanguage;
    
    private String rightLanguage;
    
    private String folder;
    
    private String xmlEncoding = "UTF-8";
    
    private String xmlVersion;

    
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
        addSection(sectionElement);
        setAlphabet(dic.getAlphabet());
        setSdefs(dic.getSdefs());

        if (dic.isMonol()) {
            setPardefs(dic.getPardefsElement());
        }

        Section sectionElementMain = dic.getSection("main");
        ArrayList<E> eList = sectionElementMain.getEElements();
        for (E e : eList) {
            if (!e.shared) {
                addEElement(e);
            }
        }
    }

    /**
     *
     * @param eList
     */
    public void setMainSection(ArrayList<E> eList) {
        for (Section section : sections) {
            if (section.getID().equals("main")) {
                Section sectionElementMain = new Section(section.getID(), section.getType());
                sections.remove(section);
                for (E e : eList) {
                    sectionElementMain.addEElement(e);
                }
                addSection(sectionElementMain);
            }
        }
    }

    /**
     *
     * @param value
     */
    public void setType(String value) {
        type = value;
    }

    /**
     *
     * @return Undefined     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param value
     */
    public void setFileName(String value) {
        fileName = value;
    }

    /**
     *
     * @return Undefined     */
    public String getFileName() {
        return fileName;
    }

    
    public void setFilePath(String path) {
        this.filePath = path;
    }

    
    public String getFilePath() {
        return this.filePath;
    }

    /**
     *
     * @param value
     */
    public void setLeftLanguage(String value) {
        leftLanguage = value;
    }

    /**
     *
     * @return Undefined     */
    public String getLeftLanguage() {
        return leftLanguage;
    }

    /**
     *
     * @param value
     */
    public void setRightLanguage(String value) {
        rightLanguage = value;
    }

    /**
     *
     * @return Undefined     
     */
    public String getRightLanguage() {
        return rightLanguage;
    }

    
    
    public static final String BIL = "BIL";
    public static final String MONOL = "MONOL";
    /**
     *
     * @return Undefined     
     */
    public boolean isMonol() {
        if (type != null) {
            if (type.equals("MONOL")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     *
     * @return Undefined     
     */
    public boolean isBil() {
        if (type != null) {
            if (type.equals(BIL)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 
     * @return The header element
     */
    public HeaderElement getHeaderElement() {
        return this.header;
    }

    /**
     * 
     * @param header
     */
    public void setHeaderElement(HeaderElement header) {
        this.header = header;
    }

    
    public Alphabet getAlphabet() {
        return alphabet;
    }

    /**
     *
     * @param value
     */
    public void setAlphabet(Alphabet value) {
        alphabet = value;
    }

    /**
     *
     * @param value
     */
    public void setSdefs(Sdefs value) {
        sdefs = value;
    }

    /**
     *
     * @return Undefined     */
    public Sdefs getSdefs() {
        return sdefs;
    }

    /**
     *
     * @param value
     */
    public void setPardefs(Pardefs value) {
        pardefs = value;
    }

    /**
     *
     * @param value
     */
    public void addSection(Section value) {
        sections.add(value);
    }

    /**
     *
     * @return Undefined     */
    public ArrayList<Section> getSections() {
        return sections;
    }

    /**
     *
     * @param id
     * @return Undefined     */
    public Section getSection(String id) {
        for (Section section : sections) {
            if (section.getID().equals(id)) {
                return section;
            }
        }
        return null;
    }

    /**
     * 
     * @param fileName
     */
    public void printXML(String fileName, DicOpts opt) {
        printXML(fileName, this.getXmlEncoding(), opt);
    }

    /**
     * 
     * @param fileName
     * @param encoding
     */
    public void printXML(String fileName, String encoding, DicOpts opt) {
        setFileName(fileName);
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
                    dos.append("\tBilingual dictionary: " + getLeftLanguage() + "-" + getRightLanguage() + "\n");
                }
                dos.append("\tSections: " + sections.size() + "\n");
                int ne = 0;
                for (Section section : sections) {
                    ne += section.getEElements().size();
                }
                dos.append("\tEntries: " + ne);
            }
            if (sdefs != null) {
                dos.append("\n\tSdefs: " + sdefs.sdefsElements.size() + "\n");
            }
            if (pardefs != null) {
                dos.append("\tParadigms: " + pardefs.getPardefElements().size() + "\n");
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
            if (pardefs != null) {
                dos.append("\t" + includeStr + " href=\"" + getFolder() + "/pardefs.dix\"/>\n");
                pardefs.printXML(getFolder() + "/pardefs.dix", opt);
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
    public ArrayList<E> getEntries() {
        for (Section s : sections) {
            if (s.getID().equals("main")) {
                return s.getEElements();
            }
        }
        return null;
    }

    /**
     *
     * @return Undefined     */
    public ArrayList<E> getAllEntries() {
        for (Section s : sections) {
            return s.getEElements();
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
            c += s.getEElements().size();
        }
        return c;
    }

    /**
     *
     * @param e
     */
    public void addEElement(E e) {
        (getEntries()).add(e);
    }

    public void countEntries() {
        nEntries = 0;
        nShared = 0;
        nDifferent = 0;
        ArrayList<E> list = getEntries();
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

    /**
     *
     * @return Undefined     */
    public int getNEntries() {
        return nEntries;
    }

    /**
     *
     * @return Undefined     */
    public int getSharedEntries() {
        return nShared;
    }

    /**
     *
     * @return Undefined     */
    public int getDifferentEntries() {
        return nDifferent;
    }

    
    public void reportMetrics() {
        countEntries();
        System.err.println(nShared + " shared entries.");
        System.err.println(nDifferent + " not shared entries.");
        System.err.println(nEntries + " own entries.");
    }

    
    public void removeNotCommon() {
        ArrayList<E> elements = getEntries();
        ArrayList<E> elementsCopy = new ArrayList<E>(elements);

        for (E e : elementsCopy) {
            if (!e.shared) {
                elements.remove(e);
            }
        }
    }

    /**
     *
     * @return Undefined     */
    public Pardefs getPardefsElement() {
        return pardefs;
    }

    /**
     * 
    public void searchEquivalentParadigms() {
        unused__DicEquivPar dicEquivPar = new unused__DicEquivPar(this);
        equivPar = dicEquivPar.findEquivalentsA();
    }
     */

    /**
     *
     * @return Undefined     */
    public HashMap<String, ArrayList<Pardef>> getEquivalentParadigms() {
        return equivPar;
    }
/*
    public E getEElement(String entry) {
        ArrayList<E> elements = getEntries();

        for (E e : elements) {
            String lemma = e.lemma;
            entry = DicTools.clearTags(entry);

            if (lemma != null) {
                if (lemma.equals(entry)) {
                    return e;
                }
            }
        }
        return null;
    }
*/
    
    public void reverse() {
        for (Section section : getSections()) {
            ArrayList<E> elements = section.getEElements();

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
     * @return the folder
     */
    public String getFolder() {
        return folder;
    }

    /**
     * @param folder
     *                the folder to set
     */
    public void setFolder(String folder) {
        this.folder = folder;
    }

    /**
     * 
     * @return XML encoding
     */
    public String getXmlEncoding() {
        return xmlEncoding;
    }

    /**
     * 
     * @param xmlEncoding
     */
    public void setXmlEncoding(String xmlEncoding) {
        this.xmlEncoding = xmlEncoding;
    }

    /**
     * 
     * @return XML version of the document
     */
    public String getXmlVersion() {
        return xmlVersion;
    }

    /**
     * 
     * @param xmlVersion
     */
    public void setXmlVersion(String xmlVersion) {
        this.xmlVersion = xmlVersion;
    }

    /**
     * 
     * @return Is there a header defined?
     */
    public boolean isHeaderDefined() {
        return (header != null);
    }

    public void setSections(ArrayList<Section> sections) {
        this.sections = sections;
    }
}
