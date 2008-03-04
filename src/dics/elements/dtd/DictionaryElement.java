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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import dics.elements.utils.EElementList;
import dics.elements.utils.EElementMap;
import dics.elements.utils.ElementList;
import dictools.DicEquivPar;
import dictools.DicTools;
import java.io.OutputStreamWriter;

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
    public DictionaryElement(final EElementMap elementMap, final DictionaryElement dic) {
        sections = new ArrayList<SectionElement>();
        final SectionElement sectionElement = new SectionElement("main", "standard");
        addSection(sectionElement);

        setAlphabet(dic.getAlphabet());
        setSdefs(dic.getSdefs());

        final Set keySet = elementMap.keySet();
        final Iterator it = keySet.iterator();

        while (it.hasNext()) {
            final String key = (String) it.next();
            final EElementList eList = elementMap.get(key);
            for (final EElement e : eList) {
                addEElement(e);
            }
        }
    }

    /**
     *
     * @param dic
     */
    public DictionaryElement(final DictionaryElement dic) {
        sections = new ArrayList<SectionElement>();
        final SectionElement sectionElement = new SectionElement("main", "standard");
        addSection(sectionElement);
        setAlphabet(dic.getAlphabet());
        setSdefs(dic.getSdefs());

        if (dic.isMonol()) {
            setPardefs(dic.getPardefsElement());
        }

        final SectionElement sectionElementMain = dic.getSection("main");
        final ArrayList<EElement> eList = sectionElementMain.getEElements();
        for (final EElement e : eList) {
            if (!e.isShared()) {
                addEElement(e);
            }
        }
    }

    /**
     *
     * @param eList
     */
    public final void setMainSection(final EElementList eList) {
        for (final SectionElement section : sections) {
            if (section.getID().equals("main")) {
                final SectionElement sectionElementMain = new SectionElement(section.getID(), section.getType());
                sections.remove(section);
                for (final EElement e : eList) {
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
    public final void setType(final String value) {
        type = value;
    }

    /**
     *
     * @return Undefined     */
    public final String getType() {
        return type;
    }

    /**
     *
     * @param value
     */
    public final void setFileName(final String value) {
        fileName = value;
    }

    /**
     *
     * @return Undefined     */
    public final String getFileName() {
        return fileName;
    }

    /**
     *
     */
    public final void setFilePath(final String path) {
        this.filePath = path;
    }

    /**
     *
     */
    public final String getFilePath() {
        return this.filePath;
    }

    /**
     *
     * @param value
     */
    public final void setLeftLanguage(final String value) {
        leftLanguage = value;
    }

    /**
     *
     * @return Undefined     */
    public final String getLeftLanguage() {
        return leftLanguage;
    }

    /**
     *
     * @param value
     */
    public final void setRightLanguage(final String value) {
        rightLanguage = value;
    }

    /**
     *
     * @return Undefined     
     */
    public final String getRightLanguage() {
        return rightLanguage;
    }

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
            if (type.equals("BIL")) {
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
    public void setHeaderElement(final HeaderElement header) {
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
    public void setAlphabet(final AlphabetElement value) {
        alphabet = value;
    }

    /**
     *
     * @param value
     */
    public void setSdefs(final SdefsElement value) {
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
    public void setPardefs(final PardefsElement value) {
        pardefs = value;
    }

    /**
     *
     * @param value
     */
    public void addSection(final SectionElement value) {
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
    public SectionElement getSection(final String id) {
        for (final SectionElement section : sections) {
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
    public void printXML(final String fileName) {
        printXML(fileName, this.getXmlEncoding());
    }

    /**
     * 
     * @param fileName
     * @param encoding
     */
    public void printXML(final String fileName, final String encoding) {
        BufferedOutputStream bos;
        FileOutputStream fos;
        OutputStreamWriter dos;
        setFileName(fileName);
        try {
            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, encoding);
            dos.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
            dos.write("<!--\n\tDictionary:\n");
            if (sections != null) {
                if (isBil()) {
                    dos.write("\tBilingual dictionary: " + getLeftLanguage() + "-" + getRightLanguage() + "\n");
                }
                dos.write("\tSections: " + sections.size() + "\n");
                int ne = 0;
                for (SectionElement section : sections) {
                    ne += section.getEElements().size();
                }
                dos.write("\tEntries: " + ne);
            }

            if (sdefs != null) {
                dos.write("\n\tSdefs: " + sdefs.getSdefsElements().size() + "\n");
            }
            if (pardefs != null) {
                dos.write("\tParadigms: " + pardefs.getPardefElements().size() + "\n");
            }

            if (comments != null) {
                dos.write(comments);
            }
            dos.write("\n-->\n");
            dos.write("<dictionary>\n");
            if (alphabet != null) {
                alphabet.printXML(dos);
            }
            if (sdefs != null) {
                sdefs.printXML(dos);
            }
            if (pardefs != null) {
                pardefs.printXML(dos);
            }
            if (sections != null) {
                for (final SectionElement s : sections) {
                    s.printXML(dos);
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

    /**
     * 
     * @param fileName
     */
    public void printXMLXInclude(final String fileName) {
        this.printXMLXInclude(fileName, this.getXmlEncoding());
    }

    /**
     * 
     * @param fileName
     * @param encoding
     */
    public void printXMLXInclude(final String fileName, final String encoding) {
        BufferedOutputStream bos;
        FileOutputStream fos;
        OutputStreamWriter dos;
        setFileName(fileName);

        try {
            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, encoding);
            dos.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
            dos.write("<!--\n\tDictionary:\n");
            if (sections != null) {
                if (isBil()) {
                    dos.write("\tBilingual dictionary: " + getLeftLanguage() + "-" + getRightLanguage() + "\n");
                }
                dos.write("\tSections: " + sections.size() + "\n");
                dos.write("\tEntries: " + (sections.get(0)).getEElements().size());
            }
            if (sdefs != null) {
                dos.write("\n\tSdefs: " + sdefs.getSdefsElements().size() + "\n");
            }
            dos.write("");

            if (comments != null) {
                dos.write(comments);
            }
            dos.write("\n-->\n");
            dos.write("<dictionary>\n");
            if (alphabet != null) {
                alphabet.printXML(dos);
            }
            String includeStr = "<xi:include xmlns:xi=\"http://www.w3.org/2001/XInclude\"";
            if (sdefs != null) {
                dos.write("\t" + includeStr + " href=\"" + getFolder() + "/sdefs.dix\"/>\n");

                sdefs.printXML(getFolder() + "/sdefs.dix");
            }
            if (pardefs != null) {
                dos.write("\t" + includeStr + " href=\"" + getFolder() + "/pardefs.dix\"/>\n");
                pardefs.printXML(getFolder() + "/pardefs.dix");
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
                dos.write(tab(1) + "<" + section.getTagName() + "" + attributes + ">\n");
                if (includes != null) {
                    for (final String s : includes) {
                        dos.write("\t" + s + "\n");
                    }
                }
                dos.write(tab(1) + "</" + section.getTagName() + ">\n");
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

    /**
     *
     * @return The 'e' elements
     */
    public EElementList getEntries() {
        for (final SectionElement s : sections) {
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
        for (final SectionElement s : sections) {
            return s.getEElements();
        }
        return null;
    }

    /**
     *
     * @param sectionID
     * @return Undefined     */
    public EElementList getEntries(final String sectionID) {
        for (final SectionElement s : sections) {
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
    public final void addEElement(final EElement e) {
        (getEntries()).add(e);
    }

    /**
     *
     * @param e
     */
    public final void removeEElement(final EElement e) {
        (getEntries()).remove(e);
    }

    /**
     *
     *
     */
    public final void countEntries() {
        nForeignEntries = 0;
        nEntries = 0;
        nShared = 0;
        nDifferent = 0;
        final ArrayList<EElement> list = getEntries();
        for (final EElement e : list) {
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
    public final int getNForeignEntries() {
        return nForeignEntries;
    }

    /**
     *
     * @return Undefined     */
    public final int getNEntries() {
        return nEntries;
    }

    /**
     *
     * @return Undefined     */
    public final int getSharedEntries() {
        return nShared;
    }

    /**
     *
     * @return Undefined     */
    public final int getDifferentEntries() {
        return nDifferent;
    }

    /**
     *
     *
     */
    public final void reportMetrics() {
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
    public final void removeNotCommon() {
        final ArrayList<EElement> elements = getEntries();
        final ArrayList<EElement> elementsCopy = new ArrayList<EElement>(elements);

        for (final EElement e : elementsCopy) {
            if (!e.isShared()) {
                elements.remove(e);
            }
        }
    }

    /**
     *
     * @param parName
     * @return Undefined     */
    public final PardefElement getParadigmDefinition(final String parName) {
        return pardefs.getParadigmDefinition(parName);
    }

    /**
     *
     * @param pardefE
     */
    public final void addParDef(final PardefElement pardefE) {
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
    public final void searchEquivalentParadigms() {
        final DicEquivPar dicEquivPar = new DicEquivPar(this);
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
    public final EElement getEElement(String entry) {
        final ArrayList<EElement> elements = getEntries();

        for (final EElement e : elements) {
            final String lemma = e.getLemma();
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
            final ArrayList<EElement> elements = section.getEElements();

            for (final EElement ee : elements) {
                final ArrayList<Element> children = ee.getChildren();

                if (ee.getRestriction() != null) {
                    if (ee.getRestriction().equals("LR")) {
                        ee.setRestriction("RL");
                    } else {
                        if (ee.getRestriction().equals("RL")) {
                            ee.setRestriction("LR");
                        }
                    }
                }

                for (final Element e : children) {
                    if (e instanceof PElement) {
                        LElement lE = ((PElement) e).getL();
                        RElement rE = ((PElement) e).getR();

                        // final String auxValue = lE.getValue();
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
    public final String getFolder() {
        return folder;
    }

    /**
     * @param folder
     *                the folder to set
     */
    public final void setFolder(String folder) {
        this.folder = folder;
    }

    /**
     * 
     * @return XML encoding
     */
    public final String getXmlEncoding() {
        return xmlEncoding;
    }

    /**
     * 
     * @param xmlEncoding
     */
    public final void setXmlEncoding(String xmlEncoding) {
        this.xmlEncoding = xmlEncoding;
    }

    /**
     * 
     * @return XML version of the document
     */
    public final String getXmlVersion() {
        return xmlVersion;
    }

    /**
     * 
     * @param xmlVersion
     */
    public final void setXmlVersion(String xmlVersion) {
        this.xmlVersion = xmlVersion;
    }

    /**
     * 
     * @return Is there a header defined?
     */
    public final boolean isHeaderDefined() {
        return (header != null);
    }
}
