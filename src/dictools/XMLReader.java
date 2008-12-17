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
package dictools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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

import dics.elements.dtd.AElement;
import dics.elements.dtd.BElement;
import dics.elements.dtd.ContentElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.GElement;
import dics.elements.dtd.IElement;
import dics.elements.dtd.JElement;
import dics.elements.dtd.LElement;
import dics.elements.dtd.PElement;
import dics.elements.dtd.ParElement;
import dics.elements.dtd.PrmElement;
import dics.elements.dtd.RElement;
import dics.elements.dtd.ReElement;
import dics.elements.dtd.SElement;
import dics.elements.dtd.SaElement;
import dics.elements.dtd.TElement;
import dics.elements.dtd.TextElement;
import dics.elements.dtd.VElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class XMLReader {

    /**
     * 
     */
    protected DocumentBuilderFactory factory;
    /**
     * 
     */
    protected DocumentBuilder builder;
    /**
     * 
     */
    protected Document document;
    /**
     * 
     */
    protected File dicFile;
    /**
     * 
     */
    protected InputStream is;
    /**
     * 
     */
    protected boolean urlDic;

    /*
     * 
     */
    protected BElement bElement;
    /**
     * 
     */
    protected AElement aElement;
    /**
     * 
     */
    protected JElement jElement;
    /**
     * 
     */
    protected SaElement saElement;
    protected PrmElement prmElement;

    
    
    /**
     * 
     * 
     */
    public XMLReader() {
        init();
    }

    /**
     * 
     * @param fileName
     */
    public XMLReader(final String fileName) {
        setDicFile(new File(fileName));
        init();
    }

    /**
     * 
     * 
     */
    private final void init() {
        // getFactory().setXIncludeAware(true);
        try {
            setFactory(DocumentBuilderFactory.newInstance());
            this.factory.setXIncludeAware(true);
            setBuilder(getFactory().newDocumentBuilder());
        } catch (final ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        bElement = new BElement();
        aElement = new AElement();
        jElement = new JElement();
        saElement = new SaElement();
        prmElement = new PrmElement();
    }

    /**
     * 
     * 
     */
    protected final void analize() {
        try {
            if (isUrlDic()) {
                // case: url
                setDocument(getBuilder().parse(getIs()));

            } else {
                // case: file
                setDocument(getBuilder().parse(getDicFile()));
            }
        } catch (final FileNotFoundException fnfe) {
            System.err.println("Error: could not find '" + getDicFile() + "' file.");
            System.exit(-1);
        } catch (final SAXException saxE) {
            System.err.println("Error: could not parse '" + getDicFile() + "'. " + saxE.getMessage());
            System.exit(-1);
        } catch (final IOException ioE) {
            System.err.println("I/O error (" + getDicFile() + "): " + ioE.getMessage());
            System.exit(-1);
        } catch (final Exception e) {
            System.err.println("Error (" + getDicFile() + "): " + e.getMessage());
            System.exit(-1);
        } finally {
            setBuilder(null);
            setFactory(null);
        }
    }

    /**
     * 
     * @param e
     * @return Undefined
     */
    protected ArrayList<org.w3c.dom.Element> readChildren(final Element e) {
        final ArrayList<org.w3c.dom.Element> eList = new ArrayList<org.w3c.dom.Element>();
        if (e.hasChildNodes()) {
            final NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);
                /*
                if (child instanceof Comment) {
                final Element comment = (Element) child;
                eList.add(comment);
                }
                 * */
                if (child instanceof Element) {
                    final Element childElement = (Element) child;
                    eList.add(childElement);
                }
            }
        }
        return eList;
    }

    /**
     * 
     * @param e
     * @return Undefined         */
    protected EElement readEElement(final Element e) {
        final String a = getAttributeValue(e, "a");
        final String c = getAttributeValue(e, "c");
        final String ign = getAttributeValue(e, "i");
        final String r = getAttributeValue(e, "r");
        final String slr = getAttributeValue(e, "slr");
        final String srl = getAttributeValue(e, "srl");
        final String lm = getAttributeValue(e, "lm");
        final String aversion = getAttributeValue(e, "aversion");
        final String alt = getAttributeValue(e, "alt");

        final EElement eElement = new EElement(r, lm, a, c);
        eElement.setAversion(aversion);
        eElement.setAlt(alt);
        eElement.setSlr(slr);
        eElement.setSrl(srl);
        eElement.setIgnore(ign);

        if (e.hasChildNodes()) {
            final NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);
                if (child instanceof Element) {
                    final Element childElement = (Element) child;
                    final String childElementName = childElement.getNodeName();
                    if (childElementName.equals("i")) {
                        final IElement iElement = readIElement(childElement);
                        eElement.addChild(iElement);
                    } else
                    if (childElementName.equals("p")) {
                        final PElement pElement = readPElement(childElement);
                        eElement.addChild(pElement);
                    } else
                    if (childElementName.equals("par")) {
                        final ParElement parElement = readParElement(childElement);
                        eElement.addChild(parElement);
                    } else
                    if (childElementName.equals("re")) {
                        final ReElement reElement = readReElement(childElement);
                        eElement.addChild(reElement);
                    } else
                    System.err.println("readEElement(): Unknown node ignored: " + childElementName);
                }
            }
        }
        return eElement;
    }

    /**
     * 
     * @param e
     * @param attrName
     * @return Undefined         */
    protected String getAttributeValue(final Element e, final String attrName) {
      
      Attr attr = e.getAttributeNode(attrName);
      if (attr==null) return null;
      return attr.getValue();
/*      
      String value1 = null;
      if (e.hasAttribute(attrName)) value1 = e.getAttribute(attrName);
      return value1;
*/
/*      
      String value1 = null;
      if (e.hasAttribute(attrName)) value1 = e.getAttribute(attrName);
      return value1;
*/       
 /*       
        String value = "";
        if (e.hasAttributes()) {
            final NamedNodeMap attributes = e.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                final Node attribute = attributes.item(i);
                final String name = attribute.getNodeName();
                value = attribute.getNodeValue();
                if (name.equals(attrName)) {
                    return value;
                } // end-if
            } // end-for
        } // end-if
        return null;
  */
    }

    /**
     * 
     * @param e
     * @return Undefined         
     */
    protected SElement readSElement(final Element e) {
        final String n = getAttributeValue(e, "n");
        return SElement.get(n);
    }

    /**
     * 
     * @param e
     * @return A 'v' element
     */
    protected VElement readVElement(final Element e) {
        final String n = getAttributeValue(e, "n");
        VElement vE = new VElement(n);
        return vE;
    }

    /**
     * 
     * @param e
     * @return A 't' element
     */
    protected TElement readTElement(final Element e) {
        final String n = getAttributeValue(e, "n");
        TElement tE = new TElement(n);
        return tE;
    }

    /**
     * 
     * @param child
     * @return Undefined         */
    protected String loadGElementText(final Node child) {
        String text = "<g>";
        if (child.hasChildNodes()) {
            final NodeList children = child.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node node = children.item(i);
                if (node instanceof Text) {
                    final Text textNode = (Text) node;
                    text += textNode.getData().trim();
                } else {
                    final String tag = node.getNodeName();
                    if (tag.equals("b")) {
                        text += "<b/>";
                    }
                    if (tag.equals("j")) {
                        text += "<j/>";
                    }
                    if (tag.equals("a")) {
                        text += "<a/>";
                    }
                }
            }
        }
        text += "</g>";
        return text;
    }

    /**
     * 
     * @param e
     * @return Undefined         */
    protected IElement readIElement(final Element e) {
        final IElement iElement = new IElement();
        final IElement iE = (IElement) readContentElement(e, iElement);
        return iE;
    }

    /**
     * 
     * @param e
     * @return Undefined         
     */
    protected ContentElement readContentElement(final Element e,
            ContentElement cElement) {
        try {
            if (e.hasChildNodes()) {
                final NodeList children = e.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    final Node child = children.item(i);
                    if (child instanceof Text) {
                        final Text textNode = (Text) child;
                        final String str = textNode.getData().trim();
                        final TextElement tE = new TextElement(str);
                        cElement.addChild(tE);
                    } else {
                        if (!(child instanceof Comment)) {
                            final Element childElement = (Element) child;
                            final String tag = childElement.getNodeName();
                            dics.elements.dtd.Element element = processTagE(tag, child);
                            cElement.addChild(element);
                        }
                    }
                }
            } else {
            }
        } catch (Exception exp) {
            exp.printStackTrace();
            System.exit(-1);
        }
        return cElement;
    }

    /**
     * 
     * @param tag
     * @param child
     * @return Undefined         
     */
    protected final String processTagText(final String tag, final Node child) {
        String text = "";
        if (tag.equals("g")) {
            text = text + loadGElementText(child);
        }
        return text;
    }

    /**
     * 
     * @param tag
     * @param child
     * @return Undefined        
     */
    protected final dics.elements.dtd.Element processTagE(final String tag,
            final Node child) {
        if (tag.equals("s")) {
            final Element childElement = (Element) child;
            final SElement sElement = readSElement(childElement);
            return sElement;
        }
        if (tag.equals("v")) {
            final Element childElement = (Element) child;
            final VElement vElement = readVElement(childElement);
            return vElement;
        }
        if (tag.equals("t")) {
            final Element childElement = (Element) child;
            final TElement tElement = readTElement(childElement);
            return tElement;
        }
        if (tag.equals("b")) {
            return getBElement();
        }
        if (tag.equals("j")) {
            return getJElement();
        }
        if (tag.equals("a")) {
            return getAElement();
        }
        if (tag.equals("sa")) {
            return getSaElement();
        }
        if (tag.equals("g")) {
            final Element childElement = (Element) child;
            final GElement gElement = readGElement(childElement);
            return gElement;
        }
        if (tag.equals("prm")) {
            return getPrmElement();
        }
        if (tag.startsWith("prm") && '0'<=tag.charAt(3)  && tag.charAt(3)<='9') {
            // We can't use the singleton here, so make a new element
            return new PrmElement(tag.substring(3));
        }
        System.err.println("processTagE(): Unknown tag "+tag+" ignored in: " + child);

        return null;
    }

    /**
     * 
     * @param e
     * @return Undefined         
     */
    protected LElement readLElement(final Element e) {
        final LElement lElement = new LElement();
        final LElement lE = (LElement) readContentElement(e, lElement);
        return lE;
    }

    /**
     * 
     * @param e
     * @return Undefined        
     */
    protected RElement readRElement(final Element e) {
        final RElement rElement = new RElement();
        final RElement rE = (RElement) readContentElement(e, rElement);
        return rE;
    }

    /**
     * 
     * @param e
     * @return Undefined         
     */
    protected GElement readGElement(final Element e) {
        GElement gElement = new GElement();
        GElement gE = (GElement) readContentElement(e, gElement);
        return gE;
    }

    /**
     * 
     * @param e
     * @return Undefined         
     */
    protected PElement readPElement(final Element e) {
        final PElement pElement = new PElement();

        if (e.hasChildNodes()) {
            final NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);
                if (child instanceof Element) {
                    final Element childElement = (Element) child;
                    final String childElementName = childElement.getNodeName();
                    if (childElementName.equals("l")) {
                        final LElement lElement = readLElement(childElement);
                        pElement.setLElement(lElement);
                    } else
                    if (childElementName.equals("r")) {
                        final RElement rElement = readRElement(childElement);
                        pElement.setRElement(rElement);
                    } else
                    System.err.println("readPElement(): Unknown node ignored: " + childElementName);

                }
            }
        }
        return pElement;
    }

    /**
     * 
     * @param e
     * @return Undefined        
     */
    protected ParElement readParElement(final Element e) {
        final String n = getAttributeValue(e, "n");
        final String sa = getAttributeValue(e, "sa");
        System.out.println("sa = " + sa);
        final ParElement parElement = new ParElement(n);
        parElement.setSa(sa);
        return parElement;
    }

    /**
     * 
     * @param e
     * @return Undefined
     */
    protected ReElement readReElement(final Element e) {
        String value = "";

        if (e.hasChildNodes()) {
            final NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);
                if (child instanceof Text) {
                    final Text textNode = (Text) child;
                    value += textNode.getData().trim();
                }
            }
        }
        final ReElement reElement = new ReElement(value);
        return reElement;
    }

    /**
     * @return the aElement
     */
    protected final AElement getAElement() {
        return aElement;
    }

    /**
     * @param element
     *                the aElement to set
     */
    protected final void setAElement(final AElement element) {
        aElement = element;
    }

    /**
     * @return the bElement
     */
    protected final BElement getBElement() {
        return bElement;
    }

    /**
     * @param element
     *                the bElement to set
     */
    protected final void setBElement(final BElement element) {
        bElement = element;
    }

    /**
     * @return the builder
     */
    protected final DocumentBuilder getBuilder() {
        return builder;
    }

    /**
     * @param builder
     *                the builder to set
     */
    protected final void setBuilder(final DocumentBuilder builder) {
        this.builder = builder;
    }

    /**
     * @return the dicFile
     */
    protected final File getDicFile() {
        return dicFile;
    }

    /**
     * @param dicFile
     *                the dicFile to set
     */
    protected final void setDicFile(final File dicFile) {
        this.dicFile = dicFile;
    }

    /**
     * @return the document
     */
    protected final Document getDocument() {
        return document;
    }

    /**
     * @param document
     *                the document to set
     */
    protected final void setDocument(final Document document) {
        this.document = document;
    }

    /**
     * @return the factory
     */
    protected final DocumentBuilderFactory getFactory() {
        return factory;
    }

    /**
     * @param factory
     *                the factory to set
     */
    protected final void setFactory(final DocumentBuilderFactory factory) {
        this.factory = factory;
    }

    /**
     * @return the jElement
     */
    protected final JElement getJElement() {
        return jElement;
    }

    /**
     * @param element
     *                the jElement to set
     */
    protected final void setJElement(final JElement element) {
        jElement = element;
    }

    /**
     * @return the saElement
     */
    public final SaElement getSaElement() {
        return saElement;
    }

    /**
     * @return the saElement
     */
    public final PrmElement getPrmElement() {
        return prmElement;
    }
    
    
    /**
     * @param saElement
     *                the saElement to set
     */
    public final void setSaElement(SaElement saElement) {
        this.saElement = saElement;
    }

    /**
     * @return the is
     */
    public final InputStream getIs() {
        return is;
    }

    /**
     * @param is
     *                the is to set
     */
    public final void setIs(InputStream is) {
        this.is = is;
    }

    /**
     * @return the urlDic
     */
    public final boolean isUrlDic() {
        return urlDic;
    }

    /**
     * @param urlDic
     *                the urlDic to set
     */
    public final void setUrlDic(boolean urlDic) {
        this.urlDic = urlDic;
    }
}
