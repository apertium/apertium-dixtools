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

import dics.elements.utils.DicOpts;
import java.io.IOException;

import dics.elements.utils.ElementList;
import dics.elements.utils.Msg;
import dics.elements.utils.SElementList;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public abstract class ContentElement extends Element implements Cloneable {

    /**
     * 
     */
    protected ElementList children = new ElementList();
    /**
     * 
     */
    protected String value;
    /**
     * 
     */
    protected String sElem;

    /**
     * 
     * 
     */
    public ContentElement() {
    }

    /**
     * 
     * @param value
     */
    public ContentElement(String value) {
        setValue(value);
    }

    /**
     * 
     * @param cE
     */
    public ContentElement(ContentElement cE) {
        children = (ElementList) cE.getChildren().clone();
        value = new String(cE.getValue());
    }

    /**
     * 
     * @param e
     */
    public void addChild(Element e) {
        children.add(e);
    }

    /**
     * 
     * @return Undefined         */
    public SElementList getSElements() {
        SElementList sEList = new SElementList();
        for (Element e : children) {
            if (e instanceof SElement) {
                SElement sE = (SElement) e;
                sEList.add(sE);
            }
        }
        return sEList;
    }

    /**
     * 
     * @return Undefined         
     */
    @Override
    public String getValueNoTags() {
        String str = "";
        for (Element e : children) {
            if (!(e instanceof SElement)) {
                if (e instanceof TextElement) {
                    TextElement tE = (TextElement) e;
                    str += tE.getValue();
                } else {
                    str += e.getValueNoTags();
                }
            }
        }
        return str;
    }

    /**
     * 
     * @return Undefined         
     */
    @Override
    public String getValue() {
        String str = "";
        for (Element e : children) {
            if (!(e instanceof SElement)) {
                if (e instanceof GElement) {
                    str += "<g>" + ((GElement) e).getValue() + "</g>";
                } else {
                    str += e.getValue();
                }
            }
        }
        return str;
    }

    /**
     * 
     * @param value
     * @return Undefined         */
    public boolean is(String value) {
        if (getSElements().size() > 0) {
            SElement sE = getSElements().get(0);
            if (sE != null) {
                if (sE.is(value)) {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 
     * @param def
     * @return true is the element contains a certain definition ('m', 'adj', etc)
     */
    public boolean contains(String def) {
        for (SElement sE : this.getSElements()) {
            if (sE.getValue().equals(def)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    protected void printXML(Appendable dos, DicOpts opt) throws IOException {

      String tagName = getTagName();

        if (!opt.nowAlign) {
                // write blank lines and processingComments from original file
                dos.append(prependCharacterData);
                // write blank lines and processingComments from original file
                if (!opt.noProcessingComments) dos.append(makeCommentIfData(processingComments));
                dos.append(tab(4) + "<" + tagName + ">");  
        } else {
                dos.append(prependCharacterData.trim()); 
                  // write blank lines and processingComments from original file
                if (!opt.noProcessingComments) dos.append(makeCommentIfData(processingComments));
                dos.append("<" + tagName + ">");
        }  

          for (Element e : children) {
              if (e != null) {
                  e.printXML(dos, opt);
              }
          }

        dos.append("</" + tagName + ">" + (opt.nowAlign?"":"\n"));    

        if (!opt.nowAlign) {
                // write blank lines and processingComments from original file
                dos.append(appendCharacterData);
        } else {
                dos.append(appendCharacterData.trim()); 
        }  

        
        //dos.append("</"); dos.append(tagName); dos.append(">"); dos.append(c); dos.append("\n");
    }

    /**
     * 
     * @param value
     */
    @Override
    public void setValue(String value) {
        boolean textE = false;
        for (Element e : getChildren()) {
            if (e instanceof TextElement) {
                textE = true;
                ((TextElement) e).setValue(value);
            }
        }
        if (!textE) {
            TextElement tE = new TextElement(value);
            getChildren().add(0, tE);
        }
        this.value = value;
    }

    /**
     * 
     * @return Undefined         */
    public ElementList getChildren() {
        return children;
    }

    /**
     * 
     * @param value
     */
    public void setChildren(ElementList value) {
        children = value;
    }

    /**
     * 
     * @param value
     */
    public void changeFirstSElement(String value) {
        SElement sE2 = new SElement(value);
        getSElements().set(0, sE2);
        int j = 0;
        for (int i = 0; i < children.size(); i++) {
            Element e = children.get(i);
            if (e instanceof SElement) {
                if (j == 0) {
                    children.set(i, sE2);
                    return;
                }
                j++;
            }

        }
    }

    /**
     * 
     * @return Undefined         */
    public String getString() {
        String str = "";
        for (SElement s : getSElements()) {
            str += s.toString();
        }
        return str;
    }

    /**
     * 
     * @return Undefined         */
    public String getInfo() {
        String str = "(";
        int i = 0;
        for (SElement s : getSElements()) {
            // para que no se considere la primera etiqueta, la de
            // categoria,
            // para encontrar paradigmas equivalentes.
            if (i != 0) {
                str += s.getValue() + ",";
            }
            i++;
        }
        str += ")";
        return str;
    }

    /**
     * 
     */
    public String toStringOld() {
        String tagName = getTagName();
        if (tagName == null) {
            tagName = "";
        }

        String v = getValue();
        if (v == null) {
            v = "";
        }

        String sList = getString();
        if (sList == null) {
            sList = "";
        }

        String str = "<" + tagName + ">" + v + sList + "</" + tagName + ">";
        return str;
    }

    /**
     * 
     */
    @Override
    public String toString() {
        String str = "";

        String tagName = getTagName();
        if (tagName == null) {
            tagName = "";
        }

        str += "<" + tagName + ">";
        for (Element e : getChildren()) {
          if (e==null) continue;
            String v = e.toString();
            str += v;
        }
        str += "</" + tagName + ">";

        return str;
    }

    /**
     * toString() without lemma
     * 
     * @return Undefined         */
    public String toString2() {
        String tagName = getTagName();
        if (tagName == null) {
            tagName = "";
        }
        String sList = getString();
        if (sList == null) {
            sList = "";
        }
        String str = sList;
        return str;
    }

    /**
     * 
     * 
     */
    public void print(Msg msg) {
        msg.log(value + " / ");
        SElementList sList = getSElements();
        if (sList != null) {
            for (SElement s : getSElements()) {
                msg.log(s.toString());
            }
        }
        msg.log("\n");
    }

    /**
     * 
     */
    @Override
    public Object clone() {
        ContentElement cloned = null;
        try {
            cloned = (ContentElement) super.clone();
            cloned.children = (ElementList) children.clone();
        } catch (Exception ex) {
            return null;
        }
        return cloned;
    }

    /**
     * 
     * @return A sequence of elements (text and 's' elements)
     */
    public ElementList getSequence() {
        ElementList eList = new ElementList();
        String str = "";
        boolean hasSElements = false;
        for (Element e : this.children) {
            if (e instanceof TextElement) {
                str += e.getValue();
            }
            if (e instanceof BElement) {
                str += "<b/>";
                // Argn this is really ugly, adding "<b/>" as TEXT into another text element.
                // This makes it impossible to escape < and > properly when outputting text elements.
                // Jacob Nordfalk 3sept 2009
            }
            if (e instanceof GElement) {
                str += processGElement(e);
            }

            if (e instanceof SElement) {
                if (!hasSElements) {
                    eList.add(new TextElement(str));
                }
                str = "";
                hasSElements = true;
                String v = ((SElement) e).getValue();
                if (v.equals("*")) {
                    eList.add(new SElement("^*"));
                } else {
                    if (v.equals("?")) {
                        eList.add(new SElement("^?"));
                    } else {
                        eList.add(new SElement(v));
                    }
                }
            }
        }
        if (!hasSElements && !str.equals("")) {
            eList.add(new TextElement(str));
        }
        return eList;
    }

    /**
     * 
     * @param e
     * @return Content of 'g' element (string)
     */
    private String processGElement(Element e) {
        GElement gE = (GElement) e;
        String str = "";
        str += "<g>";
        for (Element e1 : gE.getChildren()) {
            if (e1 instanceof TextElement) {
                TextElement tE = (TextElement) e1;
                str += tE.getValue();
            }
            if (e1 instanceof BElement) {
                str += "<b/>";
            }
        }
        str += "</g>";
        return str;
    }
}
