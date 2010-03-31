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
package dictools.formserver.templates;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import dics.elements.dtd.CharacterDataNeighbour;
import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.HeaderElement;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.Pardefs;
import dics.elements.dtd.Sdefs;
import dics.elements.dtd.Section;
import dictools.utils.XMLReader;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class TemplatesReader extends XMLReader {

    
	public Templates tpl;
    
	public boolean readParadigms = true;
    
    /**
     * 
     * @param fileName
     */
    public TemplatesReader(String fileName) {
        super(fileName);
    }

    
    public TemplatesReader() {
    }

    /**
     * 
     * @return The Dictionary object
     */
    public Templates readTpl() {
        analize();
        Templates templates = new Templates();

        String encoding = document.getXmlEncoding();
        if (encoding==null) encoding = document.getInputEncoding();
        //System.err.println("encoding = " + encoding);
        // default to UTF-8 in case of no encoding specified
        if (encoding!=null) templates.xmlEncoding = encoding;

        Element root = document.getDocumentElement();

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
                    tpl.header = headerElement;
                } else

                // Alphabet
                if (childElementName.equals("templates")) {
                    Templates tpls = readTemplates(childElement);
                    tpl = tpls;
                } else
                  
                if (childElementName.equals("xi:include")) {
                    String includeFileName = getAttributeValue(childElement, "href");
                    File f = dicFile;
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

                } else

                System.err.println("Unknown node ignored: " + childElementName);
            }
        }
        root = null;
        this.document = null;
        return templates;
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
                }
            }
        }
        return header;
    }


    static class CharacterDataInsideTag implements CharacterDataNeighbour {

        dics.elements.dtd.DixElement enclosingElement;

        public CharacterDataInsideTag(dics.elements.dtd.DixElement enclosingElement) {
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
     */
    public Templates readTemplates(Element e) {
        Templates tpl = new Templates();

        StringBuilder characterData = new StringBuilder();
        dics.elements.dtd.DixElement previousElement = null;
        
        if (e.hasChildNodes()) {
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                String childElementName = child.getNodeName();
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    if (childElementName.equals("left")) {
                        Left l = readLeft(childElement);
                        tpl.lefts.add(l);
                        prependOrAppendCharacterData(characterData, l, previousElement);
                        previousElement = l;
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
        return tpl;
    }

    /**
     * 
     * @param e
     */
    public Left readLeft(Element e) {
        String id = getAttributeValue(e, "id");
        Left l = new Left(id);

        StringBuilder characterData = new StringBuilder();
        dics.elements.dtd.CharacterDataNeighbour previousElement = new CharacterDataInsideTag(l);

        if (e.hasChildNodes()) {
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                String childElementName = child.getNodeName();
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    if (childElementName.equals("right")) {
                        Right r = readRight(childElement);
                        l.rlist.add(r);
                        prependOrAppendCharacterData(characterData, r, previousElement);
                        previousElement = r;
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

        return l;
    }


    /**
     *
     * @param e
     */
    public Right readRight(Element e) {
        String id = getAttributeValue(e, "id");
        Right r = new Right(id);

        StringBuilder characterData = new StringBuilder();
        dics.elements.dtd.CharacterDataNeighbour previousElement = new CharacterDataInsideTag(r);

        if (e.hasChildNodes()) {
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                String childElementName = child.getNodeName();
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    if (childElementName.equals("template")) {
                        Template tpl = readTemplate(childElement);
                        r.templates.add(tpl);
                        prependOrAppendCharacterData(characterData, tpl, previousElement);
                        previousElement = tpl;
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

        return r;
    }


    /**
     *
     * @param e
     */
    public Template readTemplate(Element e) {
        Template tpl = new Template();

        StringBuilder characterData = new StringBuilder();
        dics.elements.dtd.CharacterDataNeighbour previousElement = new CharacterDataInsideTag(tpl);

        if (e.hasChildNodes()) {
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                String childElementName = child.getNodeName();
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    if (childElementName.equals("e")) {
                        E elem = readEElement(childElement);
                        tpl.elements.add(elem);
                        prependOrAppendCharacterData(characterData, tpl, previousElement);
                        previousElement = tpl;
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

        return tpl;
    }

}
