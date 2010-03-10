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
package dictools.formserver.config;

import dictools.formserver.templates.*;
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
import dics.elements.dtd.HeaderElement;
import dictools.utils.XMLReader;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class ConfigReader extends XMLReader {

    
    public Templates tpl;
    private static boolean debug = false;
        
    /**
     * 
     * @param fileName
     */
    public ConfigReader(String fileName) {
        super(fileName);
    }

    
    public ConfigReader() {
    }

    /**
     * 
     * @return The Dictionary object
     */
    public Config readConfig() {
        analize();
        Config cfg = new Config();

        String encoding = document.getXmlEncoding();
        if (encoding==null) encoding = document.getInputEncoding();
        //System.err.println("encoding = " + encoding);
        // default to UTF-8 in case of no encoding specified
        if (encoding!=null) cfg.xmlEncoding = encoding;

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
                    cfg.header = headerElement;
                } else

                if (childElementName.equals("log-file")) {
                    cfg.logfile = readTextElement(childElement);
                    if (debug) System.err.println("log-file: " + cfg.logfile);
                } else

                if (childElementName.equals("directories")) {
                    if (debug) System.err.println("directories");
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
        return cfg;
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

    /**
     * Reads the content of a simple text element:
     * <eg>content</eg>
     * @param e
     * @return The content of the element
     */
    public static String readTextElement(Element e) {
        String content = "";
        if (e.hasChildNodes()) {
            NodeList nodeList = e.getChildNodes();
            for (int j = 0; j < nodeList.getLength(); j++) {
                Node node = nodeList.item(j);
                if (node instanceof Text) {
                    Text textNode = (Text) node;
                    content = textNode.getData().trim();
                }
            }
        }
        return content;
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


}
