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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import dics.elements.dtd.AlphabetElement;
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
    public DictionaryReader(final String fileName) {
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
        final DictionaryElement dic = new DictionaryElement();
        String encoding = getDocument().getInputEncoding();
        dic.setXmlEncoding(encoding);

        Document doc = getDocument();
        String xmlEncoding = doc.getXmlEncoding();
        String xmlVersion = doc.getXmlVersion();

        dic.setXmlEncoding(xmlEncoding);
        dic.setXmlVersion(xmlVersion);

        Element root = doc.getDocumentElement();

        final NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);

            /*
             * if (child instanceof Comment) { Comment comment =
             * (Comment)child; System.out.println("Comment: " +
             * comment.getTextContent()); }
             */

            if (child instanceof ProcessingInstruction) {
                ProcessingInstruction pi = (ProcessingInstruction) child;
                String data = pi.getData();
            // System.out.println("Data pi: " + data);
            } else

            if (child instanceof Element) {
                final Element childElement = (Element) child;
                final String childElementName = childElement.getNodeName();

                // Header with meta info about the dictionary
                if (childElementName.equals("header")) {
                    final HeaderElement headerElement = readHeader(childElement);
                    dic.setHeaderElement(headerElement);
                } else

                // Alphabet
                if (childElementName.equals("alphabet")) {
                    final AlphabetElement alphabetElement = readAlphabet(childElement);
                    dic.setAlphabet(alphabetElement);
                } else
                  
                // Symbol definitions
                if (childElementName.equals("sdefs")) {
                    final SdefsElement sdefsElement = readSdefs(childElement);
                    dic.setSdefs(sdefsElement);
                } else

                if (childElementName.equals("section")) {
                    final SectionElement sectionElement = readSection(childElement);
                    dic.addSection(sectionElement);
                } else

                if (childElementName.equals("pardefs")) {
                    if (isReadParadigms()) {
                        final PardefsElement pardefsElement = readPardefs(childElement);
                        dic.setPardefs(pardefsElement);
                    }
                } else

                if (childElementName.equals("xi:include")) {
                    String fileName = getAttributeValue(childElement, "href");
                    System.err.println("xi:include (" + fileName + ")");
                    DictionaryReader reader = new DictionaryReader(fileName);
                    DictionaryElement dic2 = reader.readDic();
                    if (fileName.endsWith("sdefs.dix") || fileName.endsWith("symbols.xml")) {
                        SdefsReader sdefsReader = new SdefsReader(fileName);
                        SdefsElement sdefs = sdefsReader.readSdefs();
                        //System.out.println("Symbol definitions: " + fileName);
                        dic.setSdefs(sdefs);
                    } else
                    if (fileName.endsWith("pardefs.dix")) {
                        PardefsElement pardefs = dic2.getPardefsElement();
                        dic.setPardefs(pardefs);
                    } else
                    System.err.println("Unknown xi:include href type ignored: " + fileName);
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
    public HeaderElement readHeader(final Element e) {
        HeaderElement header = new HeaderElement();
        if (e.hasChildNodes()) {
            final NodeList nodeList = e.getChildNodes();
            for (int j = 0; j < nodeList.getLength(); j++) {
                final Node node = nodeList.item(j);
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

    /**
     * 
     * @param e
     * @return Undefined         */
    public AlphabetElement readAlphabet(final Element e) {
        String alphabet = "";
        if (e.hasChildNodes()) {
            final NodeList nodeList = e.getChildNodes();
            for (int j = 0; j < nodeList.getLength(); j++) {
                final Node node = nodeList.item(j);
                if (node instanceof Text) {
                    final Text textNode = (Text) node;
                    alphabet = textNode.getData().trim();
                }
            }
        }
        final AlphabetElement alphabetElement = new AlphabetElement(alphabet);

        return alphabetElement;
    }

    /**
     * 
     * @param e
     */
    public SdefsElement readSdefs(final Element e) {
        final SdefsElement sdefsElement = new SdefsElement();

        for (final Element childElement : readChildren(e)) {
            final String childElementName = childElement.getNodeName();
            if (childElementName.equals("sdef")) {
                final SdefElement sdefElement = readSdef(childElement);
                final SElement sE = SElement.get(sdefElement.getValue());
                sdefsElement.addSdefElement(sdefElement);
            }
        }

        return sdefsElement;
    }

    /**
     * 
     * @param e
     */
    public SdefElement readSdef(final Element e) {
        final String n = getAttributeValue(e, "n");
        final String c = getAttributeValue(e, "c");
        final SdefElement sdefElement = new SdefElement(n);
        sdefElement.setComment(c);
        return sdefElement;
    }

    /**
     * 
     * @param e
     */
    public PardefsElement readPardefs(final Element e) {
        final PardefsElement pardefsElement = new PardefsElement();

        for (final Element childElement : readChildren(e)) {
            final String childElementName = childElement.getNodeName();
            if (childElementName.equals("pardef")) {
                final PardefElement pardefElement = readPardef(childElement);
                pardefsElement.addPardefElement(pardefElement);
            }
            else System.err.println("readPardefs(): Unknown node ignored: " + childElementName);
        }

        return pardefsElement;

    }

    /**
     * 
     * @param e
     */
    public PardefElement readPardef(final Element e) {
        final String n = getAttributeValue(e, "n");
        final PardefElement pardefElement = new PardefElement(n);

        for (final Element childElement : readChildren(e)) {
            final String childElementName = childElement.getNodeName();
            if (childElementName.equals("e")) {
                final EElement eElement = readEElement(childElement);
                pardefElement.addEElement(eElement);
            }
            else System.err.println("readPardef(): Unknown node ignored: " + childElementName);
        }

        return pardefElement;
    }

    /**
     * 
     * @param e
     * @return Undefined         */
    public SectionElement readSection(final Element e) {
        final String id = getAttributeValue(e, "id");
        final String type = getAttributeValue(e, "type");
        final SectionElement sectionElement = new SectionElement(id, type);
        
        StringBuilder prependCharacterData = new StringBuilder();

        // Si contiene elementos 'e'
        if (e.hasChildNodes()) {
            final NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);
                if (child instanceof Element) {
                    final Element childElement = (Element) child;
                    final String childElementName = childElement.getNodeName();
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
                        final EElement eElement = readEElement(childElement);
                        
                        prependCharacterData(prependCharacterData, eElement);
                        
                        sectionElement.addEElement(eElement);
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
                  prependCharacterData.append("<!--").append(child.getNodeValue()).append("-->");
                  
                } else
                if (child instanceof CharacterData) {
                  prependCharacterData.append(child.getNodeValue());
                  
                } else
                  System.err.println("Unhandled child = " + child);
            }
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
    @Override
    public IElement readIElement(final Element e) {
        final IElement iElement = new IElement();
        final IElement iE = (IElement) readContentElement(e, iElement);
        return iE;
    }

    /**
     * 
     * @param e
     * @return Undefined         */
    @Override
    public LElement readLElement(final Element e) {
        final LElement lElement = new LElement();
        final LElement lE = (LElement) readContentElement(e, lElement);
        return lE;
    }

    /**
     * 
     * @param e
     * @return Undefined         */
    @Override
    public RElement readRElement(final Element e) {
        final RElement rElement = new RElement();
        final RElement rE = (RElement) readContentElement(e, rElement);
        return rE;
    }

    /**
     * 
     * @param e
     * @return Undefined         */
    @Override
    public GElement readGElement(final Element e) {
        final GElement gElement = new GElement();
        final GElement gE = (GElement) readContentElement(e, gElement);
        return gE;
    }

    /**
     * 
     * @param e
     * @return Undefined         */
    @Override
    public PElement readPElement(final Element e) {
        final PElement pElement = new PElement();

        // Si contiene elementos 'e'
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
                    }
                    if (childElementName.equals("r")) {
                        final RElement rElement = readRElement(childElement);
                        pElement.setRElement(rElement);
                    }

                }
            }
        }
        return pElement;
    }

    /**
     * 
     * @param e
     * @return Undefined         */
    @Override
    public ParElement readParElement(final Element e) {
      
        final String n = getAttributeValue(e, "n");
        final String sa = this.getAttributeValue(e, "sa");
        final ParElement parElement = new ParElement(n);
        parElement.setSa(sa);

        if (e.hasAttributes()) {
            final NamedNodeMap attributes = e.getAttributes();
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
     * @return Undefined         */
    @Override
    public ReElement readReElement(final Element e) {
        String value = "";
        // Si contiene elementos 'e'
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
     * @return the dic
     */
    public final DictionaryElement getDic() {
        return dic;
    }

    /**
     * @param dic
     *                the dic to set
     */
    public final void setDic(DictionaryElement dic) {
        this.dic = dic;
    }

    /**
     * @return the readParadigms
     */
    public final boolean isReadParadigms() {
        return readParadigms;
    }

    /**
     * @param readParadigms
     *                the readParadigms to set
     */
    public final void setReadParadigms(boolean readParadigms) {
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

    /**
     * Takes all collected character data (comments, newlines, blanks...), chops off the necesarry and stores in the element.
     * Some warnings will be printed if the exact character data won't (can't) be reproduced when writing the output later on.
     * 
     * @param prependCharacterData A StringBuilder with all characters. This will be emptied so its ready to collect for next tag.
     * @param eElement The element will got set its prependCharacterData.
     */
  private static void prependCharacterData(StringBuilder prependCharacterData, final EElement eElement) {

    String txt=prependCharacterData.toString();

    int chopFrom=0;
    int chopTo=txt.length();

    // when printing indenting will be done by the element itself, so chop off all whitespace after the last newline
    char ch=0;
    while (chopFrom < chopTo && (ch = txt.charAt(chopFrom)) <= ' ' && ch != '\n') {
      chopFrom++;
    }
    
    if (chopFrom==chopTo) {
      System.err.println("Two elements on same line. Element will be moved to next line: "+eElement);
    } else {
      if (ch=='\n') {
        chopFrom++;
      } else {
        System.err.println("Comment before element "+eElement+" probably belongs to previous element.");
      }
      // when printing a newline will be generated by the previout element, so chop off all whitespace from last tag to the newline
    if(chopFrom > 0) {
      while ((ch=txt.charAt(chopTo-1))<=' '&&ch!='\n'&&chopFrom<chopTo) {
        chopTo--;
      }
    }
      if (ch>' ') {
        System.err.println("Comment before element "+eElement+" will probably disturb indenting.");
      }
      //System.err.println("txt.substring(chopFrom, chopTo) = '" + txt.substring(chopFrom, chopTo)+"'");
      eElement.setPrependCharacterData(txt.substring(chopFrom, chopTo));
    }
    prependCharacterData.setLength(0);
  }
}
