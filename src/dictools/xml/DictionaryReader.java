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
package dictools.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.JProgressBar;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import dics.elements.dtd.Alphabet;
import dics.elements.dtd.CharacterDataNeighbour;
import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.HeaderElement;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.Pardefs;
import dics.elements.dtd.Sdefs;
import dics.elements.dtd.Section;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DictionaryReader extends XMLReader {

    
    private Dictionary dic;
    
    private boolean readParadigms = true;
    
    private JProgressBar progressBar;
    private double nEntries;
    private double nElements = 0;
    private int perc = 0;
    private int oldPerc = 0;

    /**
     * 
     * @param fileName
     */
    public DictionaryReader(String fileName) {
        super(fileName);
    }

    
    public DictionaryReader() {

    }

    /**
     * 
     * @return The Dictionary object
     */
    public Dictionary readDic() {
        analize();
        Dictionary dic = new Dictionary();
        String encoding = getDocument().getInputEncoding();
        dic.xmlEncoding = encoding;

        Document doc = getDocument();
        String xmlEncoding = doc.getXmlEncoding();
        String xmlVersion = doc.getXmlVersion();

        dic.xmlEncoding = xmlEncoding;
        dic.xmlVersion = xmlVersion;

        Element root = doc.getDocumentElement();

        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            /*
             * if (child instanceof Comment) { Comment comment =
             * (Comment)child; System.err.println("Comment: " +
             * comment.getTextContent()); }
             */

            if (child instanceof ProcessingInstruction) {
            	// System.err.println("Data pi: " + child);
            } else

            if (child instanceof Element) {
                Element childElement = (Element) child;
                String childElementName = childElement.getNodeName();

                // Header with meta info about the dictionary
                if (childElementName.equals("header")) {
                    HeaderElement headerElement = readHeader(childElement);
                    dic.header = headerElement;
                } else

                // Alphabet
                if (childElementName.equals("alphabet")) {
                    Alphabet alphabetElement = readAlphabet(childElement);
                    dic.alphabet = alphabetElement;
                } else
                  
                // Symbol definitions
                if (childElementName.equals("sdefs")) {
                    Sdefs sdefsElement = readSdefs(childElement);
                    dic.sdefs = sdefsElement;
                } else

                if (childElementName.equals("section")) {
                    Section sectionElement = readSection(childElement);
                    dic.sections.add(sectionElement);
                } else

                if (childElementName.equals("pardefs")) {
                    if (isReadParadigms()) {
                        Pardefs pardefsElement = readPardefs(childElement);
                        dic.pardefs = pardefsElement;
                    }
                } else

                if (childElementName.equals("xi:include")) {
                    String includeFileName = getAttributeValue(childElement, "href");
                    File f = getDicFile();
                    String parent = f.getParent();
                    if (parent == null) parent = ".";
                    String includeFileNameAndPath = parent+File.separator+ includeFileName;
                    if (!new File(includeFileNameAndPath).exists()) try {
                        includeFileNameAndPath = f.getCanonicalFile().getParent()+File.separator+ includeFileName;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.err.println("xi:include (" + includeFileName + ")   -> "+includeFileNameAndPath);
                    if (!new File(includeFileNameAndPath).exists()) {
                        new FileNotFoundException(includeFileNameAndPath).printStackTrace();
                        continue;
                    }

                    if (includeFileName.endsWith("sdefs.dix") || includeFileName.endsWith("symbols.xml")) {
                        System.err.println("Symbol definitions: " + includeFileNameAndPath);
                        SdefsReader sdefsReader = new SdefsReader(includeFileNameAndPath);
                        Sdefs sdefs = sdefsReader.readSdefs();
                        dic.sdefs = sdefs;
                    } else
                    if (includeFileName.endsWith("pardefs.dix")) {
                        System.err.println("Paradigm definitions: " + includeFileNameAndPath);
                        DictionaryReader reader = new DictionaryReader(includeFileNameAndPath );
                        Dictionary dic2 = reader.readDic();
                        Pardefs pardefs = dic2.pardefs;
                        dic.pardefs = pardefs;
                    } else
                    System.err.println("Unknown xi:include href type ignored: " + includeFileName);
                } else

                System.err.println("Unknown node ignored: " + childElementName);
            }
        }
        root = null;
        setDocument(null);
        setDic(dic);
        return dic;
    }

    /**
     * 
     * @param e
     * @return The header element
     */
    public HeaderElement readHeader(Element e) {
        HeaderElement header = new HeaderElement();
        if (e.hasChildNodes()) {
            NodeList nodeList = e.getChildNodes();
            for (int j = 0; j < nodeList.getLength(); j++) {
                Node node = nodeList.item(j);
                if (node.getNodeName().equals("property")) {
                    Element element = (Element) node;
                    String name = getAttributeValue(element, "name");
                    String value = getAttributeValue(element, "value");
                    header.put(name, value);
                    if (name.equals("size")) {
                        this.setNEntries((new Double(value)).doubleValue());
                    }
                }
            }
        }
        return header;
    }


    static class InsideTag implements CharacterDataNeighbour {

        dics.elements.dtd.DixElement enclosingElement;

        public InsideTag(dics.elements.dtd.DixElement enclosingElement) {
            this.enclosingElement=enclosingElement;
        }

        public void setPrependCharacterData(String prependCharacterData) {
            throw new IllegalStateException("Can never happen. "+prependCharacterData);
        }

        /**
         * XML processingComments, blanks and newlines originating from a loaded file. Will be added after the XML elemen (before processingComments)
         */
        public void setAppendCharacterData(String appendCharacterData) {
            enclosingElement.setJustInsideStartTagCharacterData(appendCharacterData);
        }
    }

    /**
     * 
     * @param e
     * @return Undefined         */
    public static Alphabet readAlphabet(Element e) {
        String alphabet = "";
        if (e.hasChildNodes()) {
            NodeList nodeList = e.getChildNodes();
            for (int j = 0; j < nodeList.getLength(); j++) {
                Node node = nodeList.item(j);
                if (node instanceof Text) {
                    Text textNode = (Text) node;
                    alphabet = textNode.getData().trim();
                }
            }
        }
        Alphabet alphabetElement = new Alphabet(alphabet);

        return alphabetElement;
    }


    /**
     * 
     * @param e
     */
    public static Pardefs readPardefs(Element e) {
        Pardefs pardefsElement = new Pardefs();

        StringBuilder characterData = new StringBuilder();
        dics.elements.dtd.DixElement previousElement = null;
        
        if (e.hasChildNodes()) {
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                String childElementName = child.getNodeName();
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    if (childElementName.equals("pardef")) {
                        Pardef pardefElement = readPardef(childElement);
                        pardefsElement.elements.add(pardefElement);
                        prependOrAppendCharacterData(characterData, pardefElement, previousElement);
                        previousElement = pardefElement;
                    }
                    else System.err.println("readPardefs(): Unknown node ignored: " + childElementName);
                } else
                if (child instanceof Comment) {
                  characterData.append("<!--").append(child.getNodeValue()).append("-->");
                } else
                if (child instanceof CharacterData) {
                  characterData.append(child.getNodeValue());              
                } else
                  System.err.println("Unhandled child = " + child);
            }
        }

        prependOrAppendCharacterData(characterData, null, previousElement);
        return pardefsElement;
    }

    /**
     * 
     * @param e
     */
    public static Pardef readPardef(Element e) {
        String n = getAttributeValue(e, "n");
        Pardef pardefElement = new Pardef(n);

        StringBuilder characterData = new StringBuilder();
        dics.elements.dtd.CharacterDataNeighbour previousElement = new InsideTag(pardefElement);

        if (e.hasChildNodes()) {
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                String childElementName = child.getNodeName();
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    if (childElementName.equals("e")) {
                        E eElement = readEElement(childElement);
                        pardefElement.elements.add(eElement);
                        prependOrAppendCharacterData(characterData, eElement, previousElement);
                        previousElement = eElement;
                  } else
                    System.err.println("Unhandled child = " + child);
                } else
                if (child instanceof Comment) {
                  characterData.append("<!--").append(child.getNodeValue()).append("-->");

                } else
                if (child instanceof CharacterData) {
                  characterData.append(child.getNodeValue());              
                } else
                  System.err.println("Unhandled child = " + child);
            }
        }
        prependOrAppendCharacterData(characterData, null, previousElement);

        return pardefElement;
    }

    /**
     * 
     * @param e
     * @return Undefined         */
    public Section readSection(Element e) {
        String id = getAttributeValue(e, "id");
        String type = getAttributeValue(e, "type");
        Section sectionElement = new Section(id, type);
        
        StringBuilder characterData = new StringBuilder();
        dics.elements.dtd.DixElement previousElement = null;

        // Si contiene elementos 'e'
        if (e.hasChildNodes()) {
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    String childElementName = childElement.getNodeName();
                    if (childElementName.equals("e")) {
                        if (this.progressBar != null) {
                            this.nElements++;
                            double compl = ((this.nElements / this.nEntries)) * 100;
                            perc = (int) compl;
                            if(perc > oldPerc) {
                                if(nElements%10 == 0) {
                            this.progressBar.setValue(perc);
                                }
                            oldPerc = perc;
                            } 
                        }
                        E eElement = readEElement(childElement);
                        sectionElement.elements.add(eElement);
                        
                        prependOrAppendCharacterData(characterData, eElement, previousElement);
                        previousElement = eElement;
                        
                    } else
                    if (childElementName.equals("xi:include")) {
                        String fileName = getAttributeValue(childElement, "href");
                        System.err.println("XInclude (" + fileName + ")");
                        DictionaryReader reader = new DictionaryReader(fileName);
                        Dictionary dic = reader.readDic();
                        ArrayList<E> eList = dic.getEntriesInFirstSection();
                        for (E e2 : eList) {
                            sectionElement.elements.add(e2);
                        }
                    } else
                      System.err.println("readSection(): Unknown childElementName = " + childElementName);
                } else
                if (child instanceof Comment) {
                  characterData.append("<!--").append(child.getNodeValue()).append("-->");
                  
                } else
                if (child instanceof CharacterData) {
                  characterData.append(child.getNodeValue());              
                } else
                  System.err.println("Unhandled child = " + child);
            }

            prependOrAppendCharacterData(characterData, null, previousElement);
        }
        if(this.progressBar!= null) {
        this.progressBar.setValue(100);
        }
        return sectionElement;
    }

    

    
    
       

    /**
     * 
     * @param e
     * @return Undefined         */



    /**
     * @return the dic
     */
    public Dictionary getDic() {
        return dic;
    }

    /**
     * @param dic
     *                the dic to set
     */
    public void setDic(Dictionary dic) {
        this.dic = dic;
    }

    /**
     * @return the readParadigms
     */
    public boolean isReadParadigms() {
        return readParadigms;
    }

    /**
     * @param readParadigms
     *                the readParadigms to set
     */
    public void setReadParadigms(boolean readParadigms) {
        this.readParadigms = readParadigms;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public double getNEntries() {
        return nEntries;
    }

    public void setNEntries(double nEntries) {
        this.nEntries = nEntries;
    }
}
