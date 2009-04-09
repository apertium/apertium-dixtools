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

import dictools.*;
import dictools.xml.XMLReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import dics.elements.dtd.AlphabetElement;
import dics.elements.dtd.CharacterDataNeighbour;
import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.GElement;
import dics.elements.dtd.HeaderElement;
import dics.elements.dtd.IElement;
import dics.elements.dtd.LElement;
import dics.elements.dtd.PElement;
import dics.elements.dtd.ParElement;
import dics.elements.dtd.PardefElement;
import dics.elements.dtd.PardefsElement;
import dics.elements.dtd.RElement;
import dics.elements.dtd.ReElement;
import dics.elements.dtd.SElement;
import dics.elements.dtd.SdefElement;
import dics.elements.dtd.SdefsElement;
import dics.elements.dtd.SectionElement;
import dics.elements.utils.EElementList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import javax.swing.JProgressBar;
import org.w3c.dom.Comment;
import org.w3c.dom.CharacterData;
import org.w3c.dom.NamedNodeMap;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DictionaryReader extends XMLReader {

    /**
     * 
     */
    private DictionaryElement dic;
    /**
     * 
     */
    private boolean readParadigms = true;
    /**
     * 
     */
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

    /**
     * 
     * 
     */
    public DictionaryReader() {

    }

    /**
     * 
     * @return The DictionaryElement object
     */
    public DictionaryElement readDic() {
        analize();
        DictionaryElement dic = new DictionaryElement();
        String encoding = getDocument().getInputEncoding();
        dic.setXmlEncoding(encoding);

        Document doc = getDocument();
        String xmlEncoding = doc.getXmlEncoding();
        String xmlVersion = doc.getXmlVersion();

        dic.setXmlEncoding(xmlEncoding);
        dic.setXmlVersion(xmlVersion);

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
                ProcessingInstruction pi = (ProcessingInstruction) child;
                String data = pi.getData();
            // System.err.println("Data pi: " + data);
            } else

            if (child instanceof Element) {
                Element childElement = (Element) child;
                String childElementName = childElement.getNodeName();

                // Header with meta info about the dictionary
                if (childElementName.equals("header")) {
                    HeaderElement headerElement = readHeader(childElement);
                    dic.setHeaderElement(headerElement);
                } else

                // Alphabet
                if (childElementName.equals("alphabet")) {
                    AlphabetElement alphabetElement = readAlphabet(childElement);
                    dic.setAlphabet(alphabetElement);
                } else
                  
                // Symbol definitions
                if (childElementName.equals("sdefs")) {
                    SdefsElement sdefsElement = readSdefs(childElement);
                    dic.setSdefs(sdefsElement);
                } else

                if (childElementName.equals("section")) {
                    SectionElement sectionElement = readSection(childElement);
                    dic.addSection(sectionElement);
                } else

                if (childElementName.equals("pardefs")) {
                    if (isReadParadigms()) {
                        PardefsElement pardefsElement = readPardefs(childElement);
                        dic.setPardefs(pardefsElement);
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
                        SdefsElement sdefs = sdefsReader.readSdefs();
                        dic.setSdefs(sdefs);
                    } else
                    if (includeFileName.endsWith("pardefs.dix")) {
                        System.err.println("Paradigm definitions: " + includeFileNameAndPath);
                        DictionaryReader reader = new DictionaryReader(includeFileNameAndPath );
                        DictionaryElement dic2 = reader.readDic();
                        PardefsElement pardefs = dic2.getPardefsElement();
                        dic.setPardefs(pardefs);
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

        dics.elements.dtd.Element enclosingElement;

        public InsideTag(dics.elements.dtd.Element enclosingElement) {
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
    public static AlphabetElement readAlphabet(Element e) {
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
        AlphabetElement alphabetElement = new AlphabetElement(alphabet);

        return alphabetElement;
    }


    /**
     * 
     * @param e
     */
    public static PardefsElement readPardefs(Element e) {
        PardefsElement pardefsElement = new PardefsElement();

        StringBuilder characterData = new StringBuilder();
        dics.elements.dtd.Element previousElement = null;
        
        if (e.hasChildNodes()) {
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                String childElementName = child.getNodeName();
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    if (childElementName.equals("pardef")) {
                        PardefElement pardefElement = readPardef(childElement);
                        pardefsElement.addPardefElement(pardefElement);
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
    public static PardefElement readPardef(Element e) {
        String n = getAttributeValue(e, "n");
        PardefElement pardefElement = new PardefElement(n);

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
                        EElement eElement = readEElement(childElement);
                        pardefElement.addEElement(eElement);
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
    public SectionElement readSection(Element e) {
        String id = getAttributeValue(e, "id");
        String type = getAttributeValue(e, "type");
        SectionElement sectionElement = new SectionElement(id, type);
        
        StringBuilder characterData = new StringBuilder();
        dics.elements.dtd.Element previousElement = null;

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
                        EElement eElement = readEElement(childElement);
                        sectionElement.addEElement(eElement);
                        
                        prependOrAppendCharacterData(characterData, eElement, previousElement);
                        previousElement = eElement;
                        
                    } else
                    if (childElementName.equals("xi:include")) {
                        String fileName = getAttributeValue(childElement, "href");
                        System.err.println("XInclude (" + fileName + ")");
                        DictionaryReader reader = new DictionaryReader(fileName);
                        DictionaryElement dic = reader.readDic();
                        EElementList eList = dic.getAllEntries();
                        for (EElement e2 : eList) {
                            sectionElement.addEElement(e2);
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
    public DictionaryElement getDic() {
        return dic;
    }

    /**
     * @param dic
     *                the dic to set
     */
    public void setDic(DictionaryElement dic) {
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
