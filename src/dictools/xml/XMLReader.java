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
import dics.elements.dtd.SdefElement;
import dics.elements.dtd.SdefsElement;
import dics.elements.dtd.TElement;
import dics.elements.dtd.TextElement;
import dics.elements.dtd.VElement;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class XMLReader {

    /** Takes trailing whitespace and the last newline away */
    private static String trimNewlineOnEnd(String str) {
        int i = str.length()-1;
        while (str.charAt(i)==' ' || str.charAt(i)=='\t') i--;
        if (str.charAt(i)=='\n') i--;
        return str.substring(0,i+1);
    }
/*
    public static void main(String[] args) {

        System.err.println("s = '" + trimNewlineOnEnd("xxx  ")+"'");

        System.err.println("s = '" + trimNewlineOnEnd("xxx  \n   ")+"'");
        System.err.println("s = '" + trimNewlineOnEnd("xxx  \n\n")+"'");
}*/

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


    // These tags have no internal data and can therefore be re-used
    protected static final BElement bElementConstant  = new BElement();
    protected static final AElement aElementConstant = new AElement();
    protected static final JElement jElementConstant = new JElement();
    protected static final SaElement saElementConstant = new SaElement();
    protected static final PrmElement prmElementNonumberConstant = new PrmElement();

    
    
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
    public XMLReader(String fileName) {      
        setDicFile(new File(fileName));
        init();
    }

    /**
     * 
     * 
     */
    private void init() {
        // getFactory().setXIncludeAware(true);
        try {
            setFactory(DocumentBuilderFactory.newInstance());
            this.factory.setXIncludeAware(true);
            setBuilder(getFactory().newDocumentBuilder());
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * 
     */
    protected void analize() {
        try {
            if (isUrlDic()) {
                // case: url
                System.err.println("Reading URL");
                setDocument(getBuilder().parse(getIs()));

            } else {
                // case: standard input
              if (getDicFile().equals(new File("-"))) {
                System.err.println("Reading from standard input");
                setDocument(getBuilder().parse(System.in));
              } else {
                // case: file
                System.err.println("Reading file " + getDicFile());
                setDocument(getBuilder().parse(getDicFile()));
              }
                
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println("Error: could not find '" + getDicFile() + "' file.");
            System.exit(-1);
        } catch (SAXException saxE) {
            System.err.println("Error: could not parse '" + getDicFile() + "'. " + saxE.getMessage());
            System.exit(-1);
        } catch (IOException ioE) {
            System.err.println("I/O error (" + getDicFile() + "): " + ioE.getMessage());
            System.exit(-1);
        } catch (Exception e) {
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
    protected static ArrayList<org.w3c.dom.Element> readChildren(Element e) {
        ArrayList<org.w3c.dom.Element> eList = new ArrayList<org.w3c.dom.Element>();
        if (e.hasChildNodes()) {
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                /*
                if (child instanceof Comment) {
                Element comment = (Element) child;
                eList.add(comment);
                }
                 * */
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    eList.add(childElement);
                }
            }
        }
        return eList;
    }

    /**
     * 
     * @param e
     * @param attrName
     * @return Undefined         */
    protected static String getAttributeValue(Element e, String attrName) {
      
      Attr attr = e.getAttributeNode(attrName);
      if (attr==null) return null;
      return attr.getValue();
/*      
      String value1 = null;
      if (e.hasAttribute(attrName)) value1 = e.getAttribute(attrName);
      return value1;
*/       
 /*       
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
  */
    }

    
    /**
     * 
     * @param e
     */
    public static SdefsElement readSdefs(Element e) {
        SdefsElement sdefsElement = new SdefsElement();

        for (Element childElement : readChildren(e)) {
            String childElementName = childElement.getNodeName();
            if (childElementName.equals("sdef")) {
                SdefElement sdefElement = readSdef(childElement);
                SElement sE = SElement.getInstance(sdefElement.getValue());
                sdefsElement.addSdefElement(sdefElement);
            }
        }

        return sdefsElement;
    }

    /**
     * 
     * @param e
     */
    public static SdefElement readSdef(Element e) {
        String n = getAttributeValue(e, "n");
        String c = getAttributeValue(e, "c");
        SdefElement sdefElement = new SdefElement(n);
        sdefElement.setComment(c);
        return sdefElement;
    }
    
    
    /**
     * 
     * @param e
     * @return Undefined         
     */
    protected static SElement readSElement(Element e) {
        String n = getAttributeValue(e, "n");
        return SElement.getInstance(n);
    }

    /**
     * 
     * @param e
     * @return A 'v' element
     */
    protected static VElement readVElement(Element e) {
        String n = getAttributeValue(e, "n");
        VElement vE = new VElement(n);
        return vE;
    }

    /**
     * 
     * @param e
     * @return A 't' element
     */
    protected static TElement readTElement(Element e) {
        String n = getAttributeValue(e, "n");
        TElement tE = new TElement(n);
        return tE;
    }

    /**
     * 
     * @param child
     * @return Undefined         */
    protected static String loadGElementText(Node child) {
        String text = "<g>";
        if (child.hasChildNodes()) {
            NodeList children = child.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node instanceof Text) {
                    Text textNode = (Text) node;
                    text += textNode.getData().trim();
                } else {
                    String tag = node.getNodeName();
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
    protected static IElement readIElement(Element e) {
        IElement iElement = new IElement();
        IElement iE = (IElement) readContentElement(e, iElement);
        return iE;
    }

    /**
     * 
     * @param e
     * @return Undefined         
     */
    protected static ContentElement readContentElement(Element e,  ContentElement cElement) {
        try {
            if (e.hasChildNodes()) {
                NodeList children = e.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child instanceof Text) {
                        Text textNode = (Text) child;
                        String str = textNode.getData().trim();
                        TextElement tE = new TextElement(str);
                        cElement.addChild(tE);
                    } else {
                        if (!(child instanceof Comment)) {
                            Element childElement = (Element) child;
                            String tag = childElement.getNodeName();
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
    protected static String processTagText(String tag, Node child) {
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
    protected static dics.elements.dtd.Element processTagE(String tag, Node child) {
        if (tag.equals("s")) {
            Element childElement = (Element) child;
            SElement sElement = readSElement(childElement);
            return sElement;
        }
        if (tag.equals("v")) {
            Element childElement = (Element) child;
            VElement vElement = readVElement(childElement);
            return vElement;
        }
        if (tag.equals("t")) {
            Element childElement = (Element) child;
            TElement tElement = readTElement(childElement);
            return tElement;
        }
        if (tag.equals("b")) {
            return bElementConstant;
        }
        if (tag.equals("j")) {
            return jElementConstant;
        }
        if (tag.equals("a")) {
            return aElementConstant;
        }
        if (tag.equals("sa")) {
            return saElementConstant;
        }
        if (tag.equals("g")) {
            Element childElement = (Element) child;
            GElement gElement = readGElement(childElement);
            return gElement;
        }
        if (tag.equals("prm")) {
            return prmElementNonumberConstant;
        }
        if (tag.startsWith("prm") && '0'<=tag.charAt(3)  && tag.charAt(3)<='9') {
            // We can't use the singleton here, so make a new element
            return new PrmElement(tag.substring(3));
        }
        System.err.println("processTagE(): Unknown tag "+tag+" ignored in: " + child);

        return null;
    }

    
    
    
    
    

    /**
     * Takes all collected character data (comments, newlines, blanks...), chops off the necesarry and stores in the element.
     * Some warnings will be printed if the exact character data won't (can't) be reproduced when writing the output later on.
     * 
     * @param prependCharacterData A StringBuilder with all characters. This will be emptied so its ready to collect for next tag.
     * @param eElement The element will got set its prependCharacterData.
     */
  public static void prependOrAppendCharacterData(StringBuilder characterData, dics.elements.dtd.CharacterDataNeighbour eElement, dics.elements.dtd.CharacterDataNeighbour previousElement) {
    if (characterData.length()==0) return;

    String txt=characterData.toString();

    int chopFrom=0;
    int chopTo=txt.length();

    // when printing indenting will be done by the element itself, so chop off all whitespace after the last newline
    char ch=0;
    while (chopFrom < chopTo && (ch = txt.charAt(chopFrom)) <= ' ' && ch != '\n') {
      chopFrom++;
    }
    
    if (chopFrom==chopTo) {
      //System.err.println("Two elements on same line. Element will be moved to next line: "+eElement);
      characterData.setLength(0);
      return; // no important character data
    } else {
      if (ch=='\n') {
        chopFrom++;
      } else {
        //if (txt.substring(chopFrom, chopTo).trim().length()>0)
        //  System.err.println("Comment '"+txt.substring(chopFrom, chopTo).trim()+"' before element:"+eElement+" probably belongs to previous element:"+previousElement);
        if (previousElement!=null)  {
          previousElement.setAppendCharacterData(trimNewlineOnEnd(txt.substring(0, chopTo)));
          characterData.setLength(0);
          return;
        } else {
        }
      }
      // when printing a newline will be generated by the previout element, so chop off all whitespace from last tag to the newline
    if(chopFrom > 0) {
      while ((ch=txt.charAt(chopTo-1))<=' '&&ch!='\n'&&chopFrom<chopTo) {
        chopTo--;
      }
    }

    if (chopFrom>=chopTo) {
      characterData.setLength(0);
      return; // no important character data
    }
      
      if (ch>' ') {
        //System.err.println("Comment '"+txt.substring(chopFrom, chopTo).trim()+"' before element "+eElement+" will probably disturb indenting.");
      }
      //System.err.println("txt.substring(chopFrom, chopTo) = '" + txt.substring(chopFrom, chopTo)+"'");
      if (eElement!=null)
        eElement.setPrependCharacterData(txt.substring(chopFrom, chopTo));
      else if (previousElement!=null) {
        //System.err.println("Comment '"+txt.substring(chopFrom, chopTo)+"' will be appended to previous element:"+previousElement);
        previousElement.setAppendCharacterData(trimNewlineOnEnd(txt.substring(0, chopTo)));
      } else {
        if (txt.substring(chopFrom, chopTo).trim().length()>0)
          System.err.println("Comment '"+txt.substring(chopFrom, chopTo).trim()+"' will be discarded no element could be found to attach it to.");
      }
    }
    characterData.setLength(0);
  }
    
    
    /**
     * 
     * @param e
     * @return Undefined         */
    protected static EElement readEElement(Element e) {
        String a = getAttributeValue(e, "a");
        String c = getAttributeValue(e, "c");
        String ign = getAttributeValue(e, "i");
        String r = getAttributeValue(e, "r");
        String slr = getAttributeValue(e, "slr");
        String srl = getAttributeValue(e, "srl");
        String lm = getAttributeValue(e, "lm");
        String aversion = getAttributeValue(e, "aversion");
        String alt = getAttributeValue(e, "alt");

        StringBuilder characterData = new StringBuilder();
        dics.elements.dtd.Element previousElement = null;


        EElement eElement = new EElement(r, lm, a, c);
        eElement.setAversion(aversion);
        eElement.setAlt(alt);
        eElement.setSlr(slr);
        eElement.setSrl(srl);
        eElement.setIgnore(ign);

        if (e.hasChildNodes()) {
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    String childElementName = childElement.getNodeName();
                    if (childElementName.equals("i")) {
                        IElement iElement = readIElement(childElement);
                        eElement.addChild(iElement);

                        prependOrAppendCharacterData(characterData, iElement, previousElement);
                        previousElement = iElement;
                    } else
                    if (childElementName.equals("p")) {
                        PElement pElement = readPElement(childElement);
                        eElement.addChild(pElement);

                        prependOrAppendCharacterData(characterData, pElement, previousElement);
                        previousElement = pElement;
                    } else
                    if (childElementName.equals("par")) {
                        ParElement parElement = readParElement(childElement);
                        eElement.addChild(parElement);

                        prependOrAppendCharacterData(characterData, parElement, previousElement);
                        previousElement = parElement;
                    } else
                    if (childElementName.equals("re")) {
                        ReElement reElement = readReElement(childElement);
                        eElement.addChild(reElement);

                        prependOrAppendCharacterData(characterData, reElement, previousElement);
                        previousElement = reElement;
                    } else
                    System.err.println("readEElement(): Unknown node ignored: " + childElementName);
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
        return eElement;
    }

    
    
    
    
    
    
    
    
    
    /**
     * 
     * @param e
     * @return Undefined         
     */
    protected static LElement readLElement(Element e) {
        LElement lElement = new LElement();
        LElement lE = (LElement) readContentElement(e, lElement);
        return lE;
    }

    /**
     * 
     * @param e
     * @return Undefined        
     */
    protected static RElement readRElement(Element e) {
        RElement rElement = new RElement();
        RElement rE = (RElement) readContentElement(e, rElement);
        return rE;
    }

    /**
     * 
     * @param e
     * @return Undefined         
     */
    protected static GElement readGElement(Element e) {
        GElement gElement = new GElement();
        GElement gE = (GElement) readContentElement(e, gElement);
        return gE;
    }

    /**
     * 
     * @param e
     * @return Undefined         
     */
    protected static PElement readPElement(Element e) {
        PElement pElement = new PElement();

        if (e.hasChildNodes()) {
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    String childElementName = childElement.getNodeName();
                    if (childElementName.equals("l")) {
                        LElement lElement = readLElement(childElement);
                        pElement.setLElement(lElement);
                    } else
                    if (childElementName.equals("r")) {
                        RElement rElement = readRElement(childElement);
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
    public static ParElement readParElement(Element e) {
      
        String n = getAttributeValue(e, "n");
        String sa = getAttributeValue(e, "sa");
        ParElement parElement = new ParElement(n);
        parElement.setSa(sa);

        if (e.hasAttributes()) {
            NamedNodeMap attributes = e.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                String name = attribute.getNodeName();
                String value = attribute.getNodeValue();
                if (name.startsWith("prm")) {
                  int parn=0;
                  if (name.length()==4 && '0'<=name.charAt(3) && name.charAt(3)<='9') {
                    parn = name.charAt(3)-'0';
                  }
                  parElement.setPrm(parn, value);
                } // end-if
            } // end-for
        } // end-if
        
        
        return parElement;
    }

    /**
     * 
     * @param e
     * @return Undefined
     */
    protected static ReElement readReElement(Element e) {
        String value = "";

        if (e.hasChildNodes()) {
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child instanceof Text) {
                    Text textNode = (Text) child;
                    value += textNode.getData().trim();
                }
            }
        }
        ReElement reElement = new ReElement(value);
        return reElement;
    }
    
    
    /**
     * @return the builder
     */
    protected DocumentBuilder getBuilder() {
        return builder;
    }

    /**
     * @param builder
     *                the builder to set
     */
    protected void setBuilder(DocumentBuilder builder) {
        this.builder = builder;
    }

    /**
     * @return the dicFile
     */
    protected File getDicFile() {
        return dicFile;
    }

    /**
     * @param dicFile
     *                the dicFile to set
     */
    protected void setDicFile(File dicFile) {
        this.dicFile = dicFile;
    }

    /**
     * @return the document
     */
    protected Document getDocument() {
        return document;
    }

    /**
     * @param document
     *                the document to set
     */
    protected void setDocument(Document document) {
        this.document = document;
    }

    /**
     * @return the factory
     */
    protected DocumentBuilderFactory getFactory() {
        return factory;
    }

    /**
     * @param factory
     *                the factory to set
     */
    protected void setFactory(DocumentBuilderFactory factory) {
        this.factory = factory;
    }

    /**
     * @return the is
     */
    public InputStream getIs() {
        return is;
    }

    /**
     * @param is
     *                the is to set
     */
    public void setIs(InputStream is) {
        this.is = is;
    }

    /**
     * @return the urlDic
     */
    public boolean isUrlDic() {
        return urlDic;
    }

    /**
     * @param urlDic
     *                the urlDic to set
     */
    public void setUrlDic(boolean urlDic) {
        this.urlDic = urlDic;
    }
}
