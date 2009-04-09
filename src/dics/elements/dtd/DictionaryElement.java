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

import dics.elements.utils.DicOpts;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import dics.elements.utils.EElementList;
import dics.elements.utils.EElementMap;
import dics.elements.utils.ElementList;
import dictools.DicEquivPar;
import dics.elements.utils.DicTools;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 *
 * @author Enrique Benimeli Bofarull
 *
 */
public class DictionaryElement extends Element {

    /**
     * 
     */
    protected HeaderElement header;
    /**
     *
     */
    protected AlphabetElement alphabet;
    /**
     *
     */
    protected SdefsElement sdefs;
    /**
     *
     */
    protected PardefsElement pardefs;
    /**
     *
     */
    protected ArrayList<SectionElement> sections;
    /**
     *
     */
    protected int nForeignEntries;
    /**
     *
     */
    protected int nEntries;
    /**
     *
     */
    protected int nShared;
    /**
     *
     */
    protected int nDifferent;
    /**
     *
     */
    protected HashMap<String, ArrayList<PardefElement>> equivPar;
    /**
     *
     */
    private String type;
    /**
     *
     */
    private String fileName;
    /**
     *
     */
    private String filePath;
    /**
     *
     */
    private String leftLanguage;
    /**
     *
     */
    private String rightLanguage;
    /**
     *
     */
    private String folder;
    /**
     *
     */
    private String xmlEncoding = "UTF-8";
    /**
     *
     */
    private String xmlVersion;

    /**
     *
     *
     */
    public DictionaryElement() {
        sections = new ArrayList<SectionElement>();
    }

    /**
     *
     * @param elementMap
     */
    public DictionaryElement(EElementMap elementMap, DictionaryElement dic) {
        sections = new ArrayList<SectionElement>();
        SectionElement sectionElement = new SectionElement("main", "standard");
        addSection(sectionElement);
        setAlphabet(dic.getAlphabet());
        setSdefs(dic.getSdefs());

        Set keySet = elementMap.keySet();
        Iterator it = keySet.iterator();

        while (it.hasNext()) {
            String key = (String) it.next();
            EElementList eList = elementMap.get(key);
            for (EElement e : eList) {
                addEElement(e);
            }
        }
    }

    /**
     *
     * @param dic
     */
    public DictionaryElement(DictionaryElement dic) {
        sections = new ArrayList<SectionElement>();
        SectionElement sectionElement = new SectionElement("main", "standard");
        addSection(sectionElement);
        setAlphabet(dic.getAlphabet());
        setSdefs(dic.getSdefs());

        if (dic.isMonol()) {
            setPardefs(dic.getPardefsElement());
        }

        SectionElement sectionElementMain = dic.getSection("main");
        ArrayList<EElement> eList = sectionElementMain.getEElements();
        for (EElement e : eList) {
            if (!e.isShared()) {
                addEElement(e);
            }
        }
    }

    /**
     *
     * @param eList
     */
    public void setMainSection(EElementList eList) {
        for (SectionElement section : sections) {
            if (section.getID().equals("main")) {
                SectionElement sectionElementMain = new SectionElement(section.getID(), section.getType());
                sections.remove(section);
                for (EElement e : eList) {
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

    /**
     *
     */
    public void setFilePath(String path) {
        this.filePath = path;
    }

    /**
     *
     */
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

    /**
     *
     *
     */
    public AlphabetElement getAlphabet() {
        return alphabet;
    }

    /**
     *
     * @param value
     */
    public void setAlphabet(AlphabetElement value) {
        alphabet = value;
    }

    /**
     *
     * @param value
     */
    public void setSdefs(SdefsElement value) {
        sdefs = value;
    }

    /**
     *
     * @return Undefined     */
    public SdefsElement getSdefs() {
        return sdefs;
    }

    /**
     *
     * @param value
     */
    public void setPardefs(PardefsElement value) {
        pardefs = value;
    }

    /**
     *
     * @param value
     */
    public void addSection(SectionElement value) {
        sections.add(value);
    }

    /**
     *
     * @return Undefined     */
    public ArrayList<SectionElement> getSections() {
        return sections;
    }

    /**
     *
     * @param id
     * @return Undefined     */
    public SectionElement getSection(String id) {
        for (SectionElement section : sections) {
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
                for (SectionElement section : sections) {
                    ne += section.getEElements().size();
                }
                dos.append("\tEntries: " + ne);
            }
            if (sdefs != null) {
                dos.append("\n\tSdefs: " + sdefs.getSdefsElements().size() + "\n");
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
                for (SectionElement s : sections) {
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

    /**
     * 
     * @param fileName
     */
    public void printXMLXInclude(String fileName, DicOpts opt) {
        this.printXMLXInclude(fileName, this.getXmlEncoding(), opt);
    }

    /**
     * 
     * @param fileName
     * @param encoding
     */
    public void printXMLXInclude(String fileName, String encoding, DicOpts opt) {
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

            for (SectionElement section : sections) {
                //section.printXMLXInclude(this.getFolder() + "/" , "UTF-8");
                ArrayList<String> includes = section.getIncludes();
                String attributes = "";
                if (section.getID() != null) {
                    attributes += " id=\"" + section.getID() + "\"";
                }
                if (section.getType() != null) {
                    attributes += " type=\"" + section.getType() + "\"";
                }
                dos.append(tab(1) + "<" + section.getTagName() + "" + attributes + ">\n");
                if (includes != null) {
                    for (String s : includes) {
                        dos.append("\t" + s + "\n");
                    }
                }
                dos.append(tab(1) + "</" + section.getTagName() + ">\n");
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

    /**
     *
     * @return The 'e' elements
     */
    public EElementList getEntries() {
        for (SectionElement s : sections) {
            if (s.getID().equals("main")) {
                return s.getEElements();
            }
        }
        return null;
    }

    /**
     *
     * @return Undefined     */
    public EElementList getAllEntries() {
        for (SectionElement s : sections) {
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
        for (SectionElement s : sections) {
            c += s.getEElements().size();
        }
        return c;
    }

    /**
     *
     * @param sectionID
     * @return Undefined     */
    public EElementList getEntries(String sectionID) {
        for (SectionElement s : sections) {
            if (s.getID().equals(sectionID)) {
                return s.getEElements();
            }
        }
        return null;
    }

    /**
     *
     * @param e
     */
    public void addEElement(EElement e) {
        (getEntries()).add(e);
    }

    /**
     *
     * @param e
     */
    public void removeEElement(EElement e) {
        (getEntries()).remove(e);
    }

    /**
     *
     *
     */
    public void countEntries() {
        nForeignEntries = 0;
        nEntries = 0;
        nShared = 0;
        nDifferent = 0;
        ArrayList<EElement> list = getEntries();
        for (EElement e : list) {
            if (e.isForeign()) {
                nForeignEntries++;
            } else {
                if (!e.isForeign()) {
                    nEntries++;
                }
            }
            if (e.isShared()) {
                nShared++;
            } else {
                if (!e.isShared()) {
                    nDifferent++;
                }
            }
        }
    }

    /**
     *
     * @return Undefined     */
    public int getNForeignEntries() {
        return nForeignEntries;
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

    /**
     *
     *
     */
    public void reportMetrics() {
        countEntries();
        System.err.println(nShared + " shared entries.");
        System.err.println(nDifferent + " not shared entries.");
        System.err.println(nEntries + " own entries.");
        System.err.println(nForeignEntries + " foreign entries.");
    }

    /**
     *
     *
     */
    public void removeNotCommon() {
        ArrayList<EElement> elements = getEntries();
        ArrayList<EElement> elementsCopy = new ArrayList<EElement>(elements);

        for (EElement e : elementsCopy) {
            if (!e.isShared()) {
                elements.remove(e);
            }
        }
    }

    /**
     *
     * @param parName
     * @return Undefined     */
    public PardefElement getParadigmDefinition(String parName) {
        return pardefs.getParadigmDefinition(parName);
    }

    /**
     *
     * @param pardefE
     */
    public void addParDef(PardefElement pardefE) {
        pardefs.addPardefElement(pardefE);
    }

    /**
     *
     * @return Undefined     */
    public PardefsElement getPardefsElement() {
        return pardefs;
    }

    /**
     * 
     */
    public void searchEquivalentParadigms() {
        DicEquivPar dicEquivPar = new DicEquivPar(this);
        equivPar = dicEquivPar.findEquivalentsA();
    }

    /**
     *
     * @return Undefined     */
    public HashMap<String, ArrayList<PardefElement>> getEquivalentParadigms() {
        return equivPar;
    }

    /**
     *
     * @param entry
     * @return Undefined     */
    public EElement getEElement(String entry) {
        ArrayList<EElement> elements = getEntries();

        for (EElement e : elements) {
            String lemma = e.getLemma();
            entry = DicTools.clearTags(entry);

            if (lemma != null) {
                if (lemma.equals(entry)) {
                    return e;
                }
            }
        }
        return null;
    }

    /**
     *
     *
     */
    public void reverse() {
        for (SectionElement section : getSections()) {
            ArrayList<EElement> elements = section.getEElements();

            for (EElement ee : elements) {
                ArrayList<Element> children = ee.getChildren();

                if (ee.getRestriction() != null) {
                    if (ee.getRestriction().equals("LR")) {
                        ee.setRestriction("RL");
                    } else {
                        if (ee.getRestriction().equals("RL")) {
                            ee.setRestriction("LR");
                        }
                    }
                }

                String currentSlr = null;
                String currentSrl = null;
                if (ee.getSlr() != null) {
                    currentSlr = ee.getSlr();
                    ee.setSlr(null);
                }
                if (ee.getSrl() != null) {
                    currentSrl = ee.getSrl();
                    ee.setSrl(null);
                }

                if (currentSlr != null) {
                    ee.setSrl(currentSlr);

                }
                if (currentSrl != null) {
                    ee.setSlr(currentSrl);
                }


                for (Element e : children) {
                    if (e instanceof PElement) {
                        LElement lE = ((PElement) e).getL();
                        RElement rE = ((PElement) e).getR();
                        // String auxValue = lE.getValue();
                        ElementList auxChildren = lE.getChildren();
                        // lE.setValue(rE.getValue());
                        lE.setChildren(rE.getChildren());
                        // rE.setValue(auxValue);
                        rE.setChildren(auxChildren);
                        ((PElement) e).setLElement(lE);
                        ((PElement) e).setRElement(rE);
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

    public void setSections(ArrayList<SectionElement> sections) {
        this.sections = sections;
    }
}
