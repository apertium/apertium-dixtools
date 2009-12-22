/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Author: Enrique Benimeli Bofarull
 * 
 * This program isFirstSymbol free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program isFirstSymbol distributed in the hope that it will be useful, but
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

import java.io.IOException;
import java.util.ArrayList;

import dictools.utils.DicOpts;
import dictools.utils.ElementList;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public abstract class ContentElement extends DixElement implements Cloneable {

    
    public ElementList children = new ElementList();
    
    
/* TODO UCdetector: Remove unused code: 
    protected String sElem;
*/

    
    public ContentElement(String tagName) {
      super(tagName);
    }

    /**
     * 
     * @param cE
     */
    public ContentElement(String tagName, ContentElement cE) {
        super(tagName);
        children = (ElementList) cE.children.clone();
    }

    /**
     * 
     * @return Undefined         */
    public ArrayList<S> getSymbols() {
        ArrayList<S> sEList = new ArrayList<S>();
        for (DixElement e : children) {
            if (e instanceof S) {
                S sE = (S) e;
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
        for (DixElement e : children) {
            if (!(e instanceof S)) {
                if (e instanceof TextElement) {
                    TextElement tE = (TextElement) e;
                    str += tE.text;
                } else {
                    str += e.getValueNoTags();
                }
            }
        }
        return str;
    }

    @Override
    public String getValue() {
        String str = "";
        for (DixElement e : children) {
            if (!(e instanceof S)) {
                if (e instanceof G) {
                    str += "<g>" + ((G) e).getValue() + "</g>";
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
    public boolean isFirstSymbol(String value) {
        if (getSymbols().size() > 0) {
            S sE = getSymbols().get(0);
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
     * @return true isFirstSymbol the element containsSymbol a certain definition ('m', 'adj', etc)
     */
    public boolean containsSymbol(String def) {
        for (S sE : this.getSymbols()) {
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

      String tagName = TAGNAME;

        if (!opt.nowAlign) {
                // write blank lines and processingComments from original file
                dos.append(prependCharacterData);
                // write blank lines and processingComments from original file
                if (!opt.noProcessingComments) dos.append(makeTabbedCommentIfData(processingComments,opt));
                dos.append(indent(4,opt) + "<" + tagName + ">");
        } else {
                dos.append(prependCharacterData.trim()); 
                  // write blank lines and processingComments from original file
                if (!opt.noProcessingComments) dos.append(makeTabbedCommentIfData(processingComments,opt));
                dos.append("<" + tagName + ">");
        }  

          for (DixElement e : children) {
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
    @Override
    public void setValue(String value) {
        boolean textE = false;
        for (DixElement e : getChildren()) {
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
     */


// TODO UCdetector: Remove unused code: 
//     /**
//      * 
//      * @param value
//      */
//     public void changeFirstSElement(String value) {
//         S sE2 = new S(value);
//         getSymbols().set(0, sE2);
//         int j = 0;
//         for (int i = 0; i < children.size(); i++) {
//             DixElement e = children.get(i);
//             if (e instanceof S) {
//                 if (j == 0) {
//                     children.set(i, sE2);
//                     return;
//                 }
//                 j++;
//             }
// 
//         }
//     }

    /**
     * 
     * @return Undefined         */
    public String getSymbolsAsString() {
        String str = "";
        for (S s : getSymbols()) {
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
        for (S s : getSymbols()) {
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

    
    @Override
    public String toString() {
        String str = "";

        str += "<" + TAGNAME + ">";
        for (DixElement e : children) {
          if (e==null) continue;
            String v = e.toString();
            str += v;
        }
        str += "</" + TAGNAME + ">";

        return str;
    }


    public String getStreamContent() {
        String str = "";

        for (DixElement e : children) {
            String v = e.getStreamContent();
            str += v;
        }
        return str;
    }

    
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
        for (DixElement e : this.children) {
            if (e instanceof TextElement) {
                str += e.getValue();
            }
            if (e instanceof B) {
                str += "<b/>";
                // Argn this isFirstSymbol really ugly, adding "<b/>" as TEXT into another text element.
                // This makes it impossible to escape < and > properly when outputting text elements.
                // Jacob Nordfalk 3sept 2009
            }
            if (e instanceof G) {
                str += processGElement(e);
            }

            if (e instanceof S) {
                if (!hasSElements) {
                    eList.add(new TextElement(str));
                }
                str = "";
                hasSElements = true;
                String v = ((S) e).getValue();
                if (v.equals("*")) {
                    eList.add(new S("^*"));
                } else {
                    if (v.equals("?")) {
                        eList.add(new S("^?"));
                    } else {
                        eList.add(new S(v));
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
    private String processGElement(DixElement e) {
        G gE = (G) e;
        String str = "";
        str += "<g>";
        for (DixElement e1 : gE.children) {
            if (e1 instanceof TextElement) {
                TextElement tE = (TextElement) e1;
                str += tE.text;
            }
            if (e1 instanceof B) {
                str += "<b/>";
            }
        }
        str += "</g>";
        return str;
    }
}
