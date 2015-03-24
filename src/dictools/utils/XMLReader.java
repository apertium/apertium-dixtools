/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Author: Enrique Benimeli Bofarull
 * 
 * This program isFirstSymbol free software; you can redistribute it and/or
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
package dictools.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import dics.elements.dtd.A;
import dics.elements.dtd.B;
import dics.elements.dtd.ContentElement;
import dics.elements.dtd.E;
import dics.elements.dtd.G;
import dics.elements.dtd.I;
import dics.elements.dtd.J;
import dics.elements.dtd.L;
import dics.elements.dtd.P;
import dics.elements.dtd.Par;
import dics.elements.dtd.Prm;
import dics.elements.dtd.R;
import dics.elements.dtd.Re;
import dics.elements.dtd.S;
import dics.elements.dtd.Sa;
import dics.elements.dtd.Sdef;
import dics.elements.dtd.Sdefs;
import dics.elements.dtd.T;
import dics.elements.dtd.TextElement;
import dics.elements.dtd.V;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import org.xml.sax.InputSource;
import dictools.utils.DicOpts;
import java.util.Map;

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

    
    protected Document document;
    
    protected File dicFile;
    
    public InputStream is;
    
    public boolean urlDic;

    static boolean discardComments = false;

    // These tags have no internal data and can therefore be re-used
    protected static final B bElementConstant  = new B();
    protected static final A aElementConstant = new A();
    protected static final J jElementConstant = new J();
    protected static final Sa saElementConstant = new Sa();
    protected static final Prm prmElementNonumberConstant = new Prm();

    /**
     * Map of DOM Nodes to line numbers. Might be empty but is never null,
     * so lineNumbers.get(node) will always succeed (but perhaps return null)
     */
    protected Map<Node, Integer> lineNumbers;

    protected int getLineNo(Element e) {
      Integer res = lineNumbers.get(e);
      if (res==null) return 0; // mask null
      return res;
    }


    
    
    
    public XMLReader() {
    }

    /**
     * 
     * @param fileName
     */
    public XMLReader(String fileName) {      
        this.dicFile = new File(fileName);
    }

    /**
     * 
     * @param fileName
     */
    public XMLReader(String fileName, DicOpts opt) {      
        this.dicFile = new File(fileName);
        if (opt.discardComments) this.discardComments = true;
    }


    protected void analize() {
        // getFactory().setXIncludeAware(true);
        try {
            KeepTrackOfLocationDOMParser parser = new KeepTrackOfLocationDOMParser();
            //parser.parse("test/sample.eo-en.dix" );
            //parser.getDocument();

            if (urlDic) {
                // case: url
                System.err.println("Reading URL");
                parser.parse(new InputSource(is));
            } else {
                // case: standard input
              if (dicFile.equals(new File("-"))) {
                System.err.println("Reading from standard input");
                parser.parse(new InputSource(System.in));
              } else {
                // case: file
                System.err.println("Reading file '" + dicFile+"'");
                //parser.parse(new InputSource(new InputStreamReader(new FileInputStream(dicFile), "UTF-8")));
                parser.parse(dicFile.getPath());
              }

            }
            this.document = parser.getDocument();
            this.lineNumbers = parser.lineNumbers;
        } catch (Exception e) {
            System.err.println("Error (" + dicFile + "): " + e.getMessage());
						e.printStackTrace();
            System.exit(-1);
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
                DixElement comment = (DixElement) child;
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
     * @return Undefined
     */
    protected static String getAttributeValue(Element e, String attrName) {      
      Attr attr = e.getAttributeNode(attrName);
      if (attr==null) return null;
      return attr.getValue();
    }

    
    /**
     * 
     * @param e
     */
    public static Sdefs readSdefs(Element e) {
        Sdefs sdefsElement = new Sdefs();

        for (Element childElement : readChildren(e)) {
            String childElementName = childElement.getNodeName();
            if (childElementName.equals("sdef")) {
                Sdef sdefElement = readSdef(childElement);
                sdefsElement.elements.add(sdefElement);
            }
        }

        return sdefsElement;
    }


    /**
     * 
     * @param e
     */
    public static Sdef readSdef(Element e) {
        String n = getAttributeValue(e, "n");
        String c = getAttributeValue(e, "c");
        Sdef sdefElement = new Sdef(n);
        sdefElement.comment=c;
        return sdefElement;
    }
    
    
    /**
     * 
     * @param e
     * @return Undefined         
     */
    protected static S readSElement(Element e) {
        String n = getAttributeValue(e, "n");
        return S.getInstance(n);
    }

    /**
     * 
     * @param e
     * @return A 'v' element
     */
    protected static V readVElement(Element e) {
        String n = getAttributeValue(e, "n");
        V vE = new V(n);
        return vE;
    }

    /**
     * 
     * @param e
     * @return A 't' element
     */
    protected static T readTElement(Element e) {
        String n = getAttributeValue(e, "n");
        T tE = new T(n);
        return tE;
    }


// TODO UCdetector: Remove unused code: 
//     /**
//      * 
//      * @param child
//      * @return Undefined         */
//     protected static String loadGElementText(Node child) {
//         String text = "<g>";
//         if (child.hasChildNodes()) {
//             NodeList children = child.getChildNodes();
//             for (int i = 0; i < children.getLength(); i++) {
//                 Node node = children.item(i);
//                 if (node instanceof Text) {
//                     Text textNode = (Text) node;
//                     text += textNode.getData().trim();
//                 } else {
//                     String tag = node.getNodeName();
//                     if (tag.equals("b")) {
//                         text += "<b/>";
//                     }
//                     if (tag.equals("j")) {
//                         text += "<j/>";
//                     }
//                     if (tag.equals("a")) {
//                         text += "<a/>";
//                     }
//                 }
//             }
//         }
//         text += "</g>";
//         return text;
//     }

    /**
     * 
     * @param e
     * @return Undefined         */
    protected static I readIElement(Element e) {
        I iElement = new I();
        I iE = (I) readContentElement(e, iElement);
        return iE;
    }

    /**
     * 
     * @param e
     * @return Undefined         
     */
    protected static ContentElement readContentElement(Element e,  final ContentElement cElement) {
        try {
            if (e.hasChildNodes()) {
                NodeList children = e.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child instanceof Text) {
                        Text textNode = (Text) child;
                        String str = textNode.getData().trim();
                        TextElement tE = new TextElement(str);
                        cElement.children.add(tE);
                    } else {
                        if (!(child instanceof Comment)) {
                            Element childElement = (Element) child;
                            String tag = childElement.getNodeName();
                            dics.elements.dtd.DixElement element = processTagE(tag, child);
                            cElement.children.add(element);
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


// TODO UCdetector: Remove unused code: 
//     /**
//      * 
//      * @param tag
//      * @param child
//      * @return Undefined         
//      */
//     protected static String processTagText(String tag, Node child) {
//         String text = "";
//         if (tag.equals("g")) {
//             text = text + loadGElementText(child);
//         }
//         return text;
//     }

    /**
     * 
     * @param tag
     * @param child
     * @return Undefined        
     */
    protected static dics.elements.dtd.DixElement processTagE(String tag, Node child) {
        if (tag.equals("s")) {
            Element childElement = (Element) child;
            S sElement = readSElement(childElement);
            return sElement;
        }
        if (tag.equals("v")) {
            Element childElement = (Element) child;
            V vElement = readVElement(childElement);
            return vElement;
        }
        if (tag.equals("t")) {
            Element childElement = (Element) child;
            T tElement = readTElement(childElement);
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
            G gElement = readGElement(childElement);
            return gElement;
        }
        if (tag.equals("prm")) {
            return prmElementNonumberConstant;
        }
        if (tag.startsWith("prm") && '0'<=tag.charAt(3)  && tag.charAt(3)<='9') {
            // We can't use the singleton here, so make a new element
            return new Prm(tag.substring(3));
        }
        System.err.println("processTagE(): Unknown tag "+tag+" ignored in: " + child);

        return null;
    }

    
    
    
    
    

    /**
     * Takes all collected character data (comments, newlines, blanks...), chops off the necessary and stores in the element.
     * Some warnings will be printed if the exact character data won't (can't) be reproduced when writing the output later on.
     * 
     * @param prependCharacterData A StringBuilder with all characters. This will be emptied so its ready to collect for next tag.
     * @param eElement The element will got set its prependCharacterData.
     */
  public static void prependOrAppendCharacterData(StringBuilder characterData, dics.elements.dtd.CharacterDataNeighbour eElement, dics.elements.dtd.CharacterDataNeighbour previousElement) {
    if (discardComments) return;
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
      //System.err.println("Two elements on same line. DixElement will be moved to next line: "+eElement);
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
      // when printing a newline will be generated by the previous element, so chop off all whitespace from last tag to the newline
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
    protected E readEElement(Element e) {
        String a = getAttributeValue(e, "a");
        String c = getAttributeValue(e, "c");
        String ign = getAttributeValue(e, "i");
        String r = getAttributeValue(e, "r");
        String slr = getAttributeValue(e, "slr");
        String srl = getAttributeValue(e, "srl");
        String lm = getAttributeValue(e, "lm");
        String aversion = getAttributeValue(e, "aversion");
        String alt = getAttributeValue(e, "alt");
        String v = getAttributeValue(e, "v");
        String vl = getAttributeValue(e, "vl");
        String vr = getAttributeValue(e, "vr");

        StringBuilder characterData = new StringBuilder();
        dics.elements.dtd.DixElement previousElement = null;


        E eElement = new E(r, lm, a, c);
        eElement.aversion = aversion;
        eElement.alt = (alt);
        eElement.slr = (slr);
        eElement.srl = (srl);
        eElement.v = (v);
        eElement.vl = (vl);
        eElement.vr = (vr);
        eElement.ignore=ign;
        eElement.lineNo = getLineNo(e);

        if (e.hasChildNodes()) {
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    String childElementName = childElement.getNodeName();
                    if (childElementName.equals("i")) {
                        I iElement = readIElement(childElement);
                        eElement.children.add(iElement);

                        prependOrAppendCharacterData(characterData, iElement, previousElement);
                        previousElement = iElement;
                    } else
                    if (childElementName.equals("p")) {
                        P pElement = readPElement(childElement);
                        eElement.children.add(pElement);

                        prependOrAppendCharacterData(characterData, pElement, previousElement);
                        previousElement = pElement;
                    } else
                    if (childElementName.equals("par")) {
                        Par parElement = readParElement(childElement);
                        eElement.children.add(parElement);

                        prependOrAppendCharacterData(characterData, parElement, previousElement);
                        previousElement = parElement;
                    } else
                    if (childElementName.equals("re")) {
                        Re reElement = readReElement(childElement);
                        eElement.children.add(reElement);

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
    protected static L readLElement(Element e) {
        L lElement = new L();
        L lE = (L) readContentElement(e, lElement);
        return lE;
    }

    /**
     * 
     * @param e
     * @return Undefined        
     */
    protected static R readRElement(Element e) {
        R rElement = new R();
        R rE = (R) readContentElement(e, rElement);
        return rE;
    }

    /**
     * 
     * @param e
     * @return Undefined         
     */
    protected static G readGElement(Element e) {
        G gE = (G) readContentElement(e, new G());
        return gE;
    }

    /**
     * 
     * @param e
     * @return Undefined         
     */
    protected static P readPElement(Element e) {
        P pElement = new P();

        if (e.hasChildNodes()) {
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    String childElementName = childElement.getNodeName();
                    if (childElementName.equals("l")) {
                        L lElement = readLElement(childElement);
                        pElement.l = lElement;
                    } else
                    if (childElementName.equals("r")) {
                        R rElement = readRElement(childElement);
                        pElement.r = (rElement);
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
    public static Par readParElement(Element e) {
      
        String n = getAttributeValue(e, "n");
        String sa = getAttributeValue(e, "sa");
        Par parElement = new Par(n);
        parElement.sa = sa;

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
    protected static Re readReElement(Element e) {
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
        Re reElement = new Re(value);
        return reElement;
    }
}
