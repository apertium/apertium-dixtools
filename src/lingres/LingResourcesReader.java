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
package lingres;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class LingResourcesReader {

    
    private DocumentBuilderFactory factory;
    
    private DocumentBuilder builder;
    
    private Document document;
    
    private File dicFile;
    
    private InputStream is;
    
    private boolean urlDic = false;
    
    public static int URL = 0;
    
    public static int FILE = 1;
    
    private LingResources lingRes;

    
    public LingResourcesReader(String source, int type) {
        if (type == LingResourcesReader.FILE) {
            setDicFile(new File(source));
        }
        if (type == LingResourcesReader.URL) {
            this.setUrlDic(true);
            try {
                URL url = new URL(source);
                is = url.openStream();
            } catch (MalformedURLException mfue) {
                System.err.println("Error: malformed URL exception!");
                System.exit(-1);
            } catch (IOException ioe) {
                System.err.println("Error: I/O exception!");
                System.exit(-1);
            }
        }
        init();
    }

    
    private void init() {
        try {
            setFactory(DocumentBuilderFactory.newInstance());
            getFactory().setXIncludeAware(true);
            setBuilder(getFactory().newDocumentBuilder());
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void analize() {
        try {
            if (isUrlDic()) {
                // case: url
                setDocument(getBuilder().parse(getIs()));
                is.close();
            } else {
                // case: file
                setDocument(getBuilder().parse(getDicFile()));
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println("Error: could not find '" + getDicFile() + "' file.");
            System.exit(-1);
        } catch (SAXException saxE) {
            saxE.printStackTrace();
            System.err.println("Error: could not parse '" + getDicFile() + "'");
        } catch (IOException ioE) {
            ioE.printStackTrace();
            System.err.println("I/O error");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: the XML document is probably not well-formed");
        } finally {
            setBuilder(null);
            setFactory(null);
        }
    }

    /**
     * 
     * @return Linguistic resources object
     */
    public LingResources readLingResources() {
        this.lingRes = new LingResources();
        this.analize();
        Element root = document.getDocumentElement();
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child instanceof Element) {
                Element childElement = (Element) child;
                String childElementName = childElement.getNodeName();
                if (childElementName.equals("name")) {
                    String name = this.readSimpleElement(childElement);
                    lingRes.setName(name);
                }
                if (childElementName.equals("description")) {
                    String description = this.readSimpleElement(childElement);
                    lingRes.setDescription(description);
                }
                if (childElementName.equals("resource")) {
                    Resource resourceElement = readResourceElement(childElement);
                    lingRes.add(resourceElement);
                }
                if (childElementName.equals("resource-set")) {
                    ResourceSet resourceSetElement = readResourceSetElement(childElement);
                    lingRes.add(resourceSetElement);
                }
            }
        }
        return this.lingRes;
    }

    /**
     * 
     * @param element
     * @return Simple element
     */
    private String readSimpleElement(Element element) {
        String text = null;
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Text) {
                Text textNode = (Text) child;
                text = textNode.getData().trim();
                return text;
            }
        }
        return text;
    }

    /**
     * 
     * @param element
     * @return resouce element
     */
    private Resource readResourceElement(Element element) {
        Resource resourceElement = new Resource();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                Element childElement = (Element) child;
                String childElementName = childElement.getNodeName();
                if (childElementName.equals("property")) {
                    String name = this.getAttributeValue(childElement, "name");
                    String value = this.getAttributeValue(childElement, "value");
                    //System.err.println("property: (" + name + ", " + value + ")");
                    resourceElement.put(name, value);
                }
            }
        }
        return resourceElement;
    }

    /**
     * 
     * @param element
     * @return Resource set element
     */
    private ResourceSet readResourceSetElement(Element element) {
        ResourceSet resourceSetElement = new ResourceSet();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                Element childElement = (Element) child;
                String childElementName = childElement.getNodeName();
                if (childElementName.equals("name")) {
                    String name = this.readSimpleElement(childElement);
                    resourceSetElement.setName(name);
                }
                if (childElementName.equals("description")) {
                    resourceSetElement.setDescription(childElementName);
                }
                if (childElementName.equals("resource")) {
                    Resource resourceElement = this.readResourceElement(childElement);
                    resourceSetElement.add(resourceElement);
                }
            }
        }
        return resourceSetElement;
    }

    /**
     * 
     * @param e
     * @param attrName
     * @return The value of the attribute
     */
    private String getAttributeValue(Element e, String attrName) {
        String value = "";
        if (e.hasAttributes()) {
            NamedNodeMap attributes = e.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                String name = attribute.getNodeName();
                value = attribute.getNodeValue();
                if (name.equals(attrName)) {
                    return value;
                } // end-if
            } // end-for
        } // end-if
        return null;
    }

    /**
     * 
     * @return The document builder factory
     */
    public DocumentBuilderFactory getFactory() {
        return factory;
    }

    /**
     * 
     * @param factory
     */
    public void setFactory(DocumentBuilderFactory factory) {
        this.factory = factory;
    }

    /**
     * 
     * @return The DocumentBuilder object
     */
    public DocumentBuilder getBuilder() {
        return builder;
    }

    /**
     * 
     * @param builder
     */
    public void setBuilder(DocumentBuilder builder) {
        this.builder = builder;
    }

    /**
     * 
     * @return 'true' if source file is URL
     */
    public boolean isUrlDic() {
        return urlDic;
    }

    /**
     * 
     * @param urlDic
     */
    public void setUrlDic(boolean urlDic) {
        this.urlDic = urlDic;
    }

    /**
     * 
     * @return The DictionaryElement object
     */
    public File getDicFile() {
        return dicFile;
    }

    /**
     * 
     * @param dicFile
     */
    public void setDicFile(File dicFile) {
        this.dicFile = dicFile;
    }

    /**
     * 
     * @return The InputStream object
     */
    public InputStream getIs() {
        return is;
    }

    /**
     * 
     * @param is
     */
    public void setIs(InputStream is) {
        this.is = is;
    }

    /**
     * 
     * @return The Document object
     */
    public Document getDocument() {
        return document;
    }

    /**
     * 
     * @param document
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * 
     * @return Linguistic resources object
     */
    public LingResources getLingRes() {
        return lingRes;
    }

    /**
     * 
     * @param lingRes
     */
    public void setLingRes(LingResources lingRes) {
        this.lingRes = lingRes;
    }
}
