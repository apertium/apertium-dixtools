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
 * You should have received author copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package dics.elements.dtd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import dictools.utils.DicOpts;
import dictools.utils.Msg;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class E extends DixElement implements Cloneable {

    public ArrayList<DixElement> children = new ArrayList<DixElement>();

    public String restriction;

    public String slr;
    
    public String srl;
    
    public String lemma;
    
    public String author;
    
    public String comment;
    
    public String ignore;
    
    public String aversion;
    
    public String alt;
    
    public String vl;
    
    public String vr;

    public String v;

    public boolean shared = false;
    
    public String patternApplied;

    /**
     * For temporary data during processing
     */
    public transient String temp;
    
    /** Line number from source file */
    public int lineNo;
    
    public E() {
      super("e");
    }

    public E(String r, String lm, String a,  String c) {
      this();
        this.restriction = r;

        // JimRegan's dubious fix.
        // Should really be in DicFix or somewhere else, and not in the DTD code.
      // don't replace("\'", "&apos;")  - we want entries like  <r>that's<b/>why , not <r>that&apos;s<b/>why
        if (lm != null) lm = lm.replaceAll("\\&", "\\&amp;").replaceAll("\"", "\\&quot;");

        this.lemma = lm;
        this.author = a;
        this.comment = c;
    }


    public boolean hasPrependorAppendData() {
        return !(prependCharacterData.trim().isEmpty() && appendCharacterData.trim().isEmpty());
    }
    
    /**
     * 
     * @return Undefined         */
    public P getFirstP() {
        for (DixElement e : children) {
            if (e instanceof P) {
                return (P) e;
            }
        }
        return null;
    }

    /**
     * 
     * @return Undefined         
     */
    public I getFirstI() {
        for (DixElement e : children) {
            if (e instanceof I) {
                return (I) e;
            }
        }
        return null;
    }

    /**
     * 
     * @param side
     * @return Undefined         
     */
    public String getValue(String side) {
        for (DixElement e : children) {
            if (e instanceof I) {
                return ((I) e).getValue();
            }
            if (e instanceof P) {
                if (side.equals("L")) {
                    return ((P) e).l.getValue();
                }
                if (side.equals("R")) {
                    return ((P) e).r.getValue();
                }
            }
        }
        return lemma;
    }

    /**
     * 
     * @param side
     * @return Value without tags
     */
    public String getValueNoTags(String side) {
        for (DixElement e : children) {
            if (e instanceof I) {
                return ((I) e).getValueNoTags();
            }
            if (e instanceof P) {
                if (side.equals("L")) {
                    return ((P) e).l.getValueNoTags();
                }
                if (side.equals("R")) {
                    return ((P) e).r.getValueNoTags();
                }
            }
        }
        return lemma;
    }

    /**
     * Returns the first part of the left or right side of an entry (or the first invariant section).
     * Examples:
     * <pre>
     * <e><i>Ameriko</i><par n="Barcelono__np"/> </e>   gives 'Ameriko'
     * <e><i>Al</i><par n="ĝ"/> <i>erio</i><par n="Barcelono__np"/> </e> gives just 'Al'
     * <e><p><l>mi</l><r>mi<prn><ref><p1><mf><sg></r></p></e> give 'mi'
     * </pre>
     * @param side can be R or L
     */
    public ContentElement getFirstPart(String side) {
        for (DixElement e : children) {
            if (e instanceof I) {
                return ((I) e);
            }
            if (e instanceof P) {
                if (side.equals("L")) {
                    return ((P) e).l;
                }
                if (side.equals("R")) {
                    return ((P) e).r;
                }
            }
        }
        return null;
    }

    public L getFirstPartAsL() {
        ContentElement cE = getFirstPart("L");
        if (cE instanceof L) {
            return (L) cE;
        }
        return new L(cE);
    }

    public R getFirstPartAsR() {
        ContentElement cE = getFirstPart("R");
        if (cE instanceof R) {
            return (R) cE;
        }
        return new R(cE);
    }




    private static String spaces = "                                                                                                                 ";

    @Override
    public void printXML(Appendable dos, DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        if (opt.stripEmptyLines && prependCharacterData.trim().isEmpty()) {
            ;
        } else {
            dos.append(prependCharacterData);
        }
        
        // prepend processingComments added in this run
        if (!opt.noProcessingComments) {
            dos.append(makeTabbedCommentIfData(processingComments,opt));
        }
        
        String attributes = this.getAttrString();
        if (!opt.nowAlign) {
            dos.append(indent(2,opt) + "<e" + attributes + ">\n");

            for (DixElement e : children) {
                e.printXML(dos, opt);
            }

            dos.append(indent(2,opt) + "</e>"+appendCharacterData+"\n\n");
        } else { 
            StringBuilder dosy = new StringBuilder(120);
            dosy.append(spaces.substring(0,opt.alignE));
            dosy.append("<e").append(attributes).append(">");
            int neededSpaces = opt.alignP - dosy.length();
            if (neededSpaces>0) {
              dosy.append(spaces.substring(0, Math.min(spaces.length(), neededSpaces)));
            }        

            if (children != null) {
                for (DixElement e : children) {
                    if (e instanceof P) {
                      ((P) e).printXML1LineAligned(dosy, opt.alignR);
                    } else {
                      e.printXML(dosy, opt);
                    }
                }
            }
            dosy.append("</e>"+appendCharacterData+"\n");
            dos.append(dosy.toString());
        }
    }

  static void appendXmlAttr(StringBuilder sb, String attrName, String attrValue) {
        if (attrValue != null) {
            //sb.append(" "+attrName+"=\"" + attrValue + "\"");
            sb.append(' ').append(attrName).append("=\"").append(attrValue).append('"');
        }
  }

    
    /**
     * 
     * @return String of attributes
     */
    private String getAttrString() {
        StringBuilder attributes = new StringBuilder();
        appendXmlAttr(attributes, "r", restriction);
        appendXmlAttr(attributes, "slr", slr);
        appendXmlAttr(attributes, "srl", srl);
        appendXmlAttr(attributes, "lm", lemma);
        appendXmlAttr(attributes, "a", author);
        appendXmlAttr(attributes, "c", comment);
        appendXmlAttr(attributes, "i", ignore);
        appendXmlAttr(attributes, "aversion", aversion);
        appendXmlAttr(attributes, "alt", alt);
        appendXmlAttr(attributes, "vl", vl);
        appendXmlAttr(attributes, "vr", vr);
        appendXmlAttr(attributes, "v", v);
        return attributes.toString();
    }

    /**
     * 
     * @param side
     * @return Undefined      
    public String getCategory(String side) {
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("adj");
        categories.add("adv");
        categories.add("preadv");
        categories.add("det");
        categories.add("np");
        categories.add("n");
        categories.add("pr");
        categories.add("prn");
        categories.add("rel");
        categories.add("num");
        categories.add("vblex");
        categories.add("vbser");
        categories.add("vbhaver");
        categories.add("vbmod");
        categories.add("cnjadv");
        categories.add("cnjcoo");
        categories.add("cnjsub");
        categories.add("detnt");
        categories.add("predet");
        categories.add("ij");

        // Añadir otras categorias que se quieran comprobar

        for (String s : categories) {
            if (isFirstSymbol("L", s)) {
                return s;
            }
        }
        return null;
    }
   */

// TODO UCdetector: Remove unused code: 
//     /**
//      * 
//      * @param side
//      * @param categories
//      * @return Undefined         */
//     public String getCategory(String side,
//             ArrayList<String> categories) {
//         for (String s : categories) {
//             if (isFirstSymbol("L", s)) {
//                 return s;
//             }
//         }
//         return null;
//     }

    /**
     * 
     * @param side
     * @param value
     * @return Undefined         
     */
    public boolean isFirstSymbol(String side, String value) {
        for (DixElement e : children) {
            if (e instanceof I) {
                I ie = (I) e;
                return ie.isFirstSymbol(value);
            }
            if (e instanceof P) {
                P p = (P) e;
                if (side.equals("L")) {
                    L lE = p.l;
                    return lE.isFirstSymbol(value);
                }
                if (side.equals("R")) {
                    R rE = p.r;
                    return rE.isFirstSymbol(value);
                }
            }
        }
        return false;
    }

/* TODO UCdetector: Remove unused code: 
    public int getNumberOfSElements(String side) {
        for (DixElement e : children) {
            if (e instanceof I) {
                I i = (I) e;
                return i.getSymbols().size();
            }
            if (e instanceof P) {
                P p = (P) e;
                if (side.equals("L")) {
                    L lE = p.l;
                    return lE.getSymbols().size();
                }
                if (side.equals("R")) {
                    R rE = p.r;
                    return rE.getSymbols().size();
                }
            }
        }
        return 0;
    }
*/

    /**
     * 
     * @param side
     * @return Undefined         */
    public boolean isAdj(String side) {
        return isFirstSymbol(side, "adj");
    }

    /**
     * 
     * @param side
     * @return Undefined         */
    public boolean isNoun(String side) {
        return isFirstSymbol(side, "n");
    }

    public boolean isLR() {
        return "LR".equals(restriction);
    }

    public boolean isRL() {
        return "RL".equals(restriction);
    }

    public boolean isAnyLR() {
        if ("LR".equals(restriction)) return true;
        if (vl != null && !"".equals(vl)) return true;
        if (slr != null && !"".equals(slr)) return true;
        return false;
    }

    public boolean isAnyRL() {
        if ("RL".equals(restriction)) return true;
        if (vr != null && !"".equals(vr)) return true;
        if (srl != null && !"".equals(srl)) return true;
        return false;
    }

    /**
     * Has restriction LR, RL or LR/RL
     * 
     * @return Undefined         */
    public boolean hasRestriction() {
      return isLR() || isRL();
    }

    public boolean is_LR_or_LRRL() {
        if (restriction == null) {
            return true;
        } else {
            if (restriction.equals("LR")) {
                return true;
            }
        }
        return false;
    }

    public boolean is_RL_or_LRRL() {
        if (restriction == null) {
            return true;
        } else {
            if (restriction.equals("RL")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param side
     * @param elementsB
     * @return Undefined9
    public boolean containsSElements(String side, ArrayList<S> elementsB) {
        ArrayList<S> elementsA = getSymbols(side);
        if (elementsA.size() != elementsB.size()) {
            return false;
        } else {
            int ignore = 0;
            for (S s1 : elementsA) {
                boolean exists = false;
                for (S s2 : elementsB) {
                    if (s1.equals(s2) && (exists == false)) {
                        exists = true;
                    }
                }
                if (exists) {
                    ignore++;
                }
            }
            if (ignore == elementsA.size()) {
                return true;
            }
        }
        return false;
    }
     */

    /**
     * 
     * @param side
     * @return Undefined         
     */
    public ArrayList<S> getSymbols(String side) {
        ArrayList<S> elementsA = new ArrayList<S>();
        for (DixElement e : children) {
            if (e instanceof I) {
                I i = (I) e;
                elementsA = i.getSymbols(elementsA);
            }
            if (e instanceof P) {
                P p = (P) e;
                if (side.equals("L")) {
                    L lE = p.l;
                    elementsA = lE.getSymbols(elementsA);
                }
                if (side.equals("R")) {
                    R rE = p.r;
                    elementsA = rE.getSymbols(elementsA);
                }
            }
        }
        return elementsA;
    }

    public void print(String side, Msg msg) {
        msg.log(getFirstPart(side).getValue() + " / ");
        for (S s : getSymbols(side)) {
            msg.log(s.toString());
        }
        msg.log("\n");
    }


    /**
     * 
     * @param side
     * @return Undefined         
    public String getInfo(String side) {
        ArrayList<S> elements = getSymbols(side);
        String str = "( ";
        for (S s : elements) {
            str += s.getValue() + " ";
        }
        str += ")";
        return str;
    }
*/
    /**
     * 
     * @return Undefined         */
    public String getMainParadigmName() {
        // Returns value of main paradigm
        Par parE = null;
        for (DixElement e : children) {
            if (e instanceof Par) {
                parE = (Par) e;

                if (parE.getValue().contains("__"))
                    return parE.getValue();
            }
        }
        // no main paradigm (containing __) was found.
        // assume last met paradigm isFirstSymbol the main one, then
        if (parE!=null) return parE.getValue();
        // no paradimgs at all
        return null;
    }

    /**
     * 
     * @return Undefined         */
    public Par getFirstParadigm() {
        // Returns value of first paradigm
        for (DixElement e : children) {
            if (e instanceof Par) {
                Par parE = (Par) e;
                return parE;
            }
        }
        return null;
    }


// TODO UCdetector: Remove unused code: 
//     /**
//      * 
//      * @param side
//      * @param newCategory
//      */
//     public void changeCategory(String side, String newCategory) {
//         for (DixElement e : children) {
//             if (e instanceof I) {
//                 I i = (I) e;
//                 i.changeFirstSElement(newCategory);
//             }
//             if (e instanceof P) {
//                 P p = (P) e;
//                 if (side.equals("L")) {
//                     L lE = p.l;
//                     lE.changeFirstSElement(newCategory);
//                 }
//                 if (side.equals("R")) {
//                     R rE = p.r;
//                     rE.changeFirstSElement(newCategory);
//                 }
//             }
//         }
// 
//     }

    
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(50);
        str.append("<e");
        appendXmlAttr(str, "i", ignore);
        appendXmlAttr(str, "r", restriction);

        str.append(">");
        for (DixElement e : children) {
          str.append(e.toString());
        }
        str.append("</e>");
        return str.toString();
    }

    public String getStreamContentForSide(String side) {
        StringBuilder str = new StringBuilder(50);
        for (DixElement e : children) {
          if (e instanceof P) {
            str.append( ((P) e).getSide(side).getStreamContent());
          }
          else str.append(e.getStreamContent());
        }
        return str.toString();
    }

    public String getLemmaForSide(String side) {
        StringBuilder str = new StringBuilder(50);
        for (DixElement e : children) {
          if (e instanceof P) {
            str.append( ((P) e).getSide(side).getValueNoTags());
          }
          else if (e instanceof Par) {
            if (((Par) e).name.length()==1) {
              // assume its a par containing a letter, like <par n="ü">
              str.append( ((Par) e).name);
            } else {
              // hmmm.... ignore it
            }
          }
          else str.append(e.getStreamContent());
        }
        return str.toString();
    }

// TODO UCdetector: Remove unused code: 
//     /**
//      * 
//      * @return Undefined         */
//     public String toStringNoParadigm() {
//         String str = "";
//         String r = "";
//         if (this.hasRestriction()) {
//             r = " r=\"" + restriction + "\"";
//         }
//         str += "<e" + r + ">";
//         for (DixElement e : children) {
//             if (e instanceof I) {
//                 I i = (I) e;
//                 str += i.toString();
//             }
//             if (e instanceof P) {
//                 P p = (P) e;
// 
//                 L lE = p.l;
//                 str += lE.toString();
// 
//                 R rE = p.r;
//                 str += rE.toString();
//             }
//             if (e instanceof Re) {
//                 Re re = (Re) e;
//                 str += re.toString();
//             }
// 
//         }
//         str += "</e>";
//         return str;
//     }


// TODO UCdetector: Remove unused code: 
//     /**
//      * 
//      * @return Undefined         */
//     public String lemmaAndCategory() {
//         String str = "";
//         String r = "";
//         if (this.hasRestriction()) {
//             r = " r=\"" + restriction + "\"";
//         }
//         str += "<e" + r + ">";
//         str += lemma;
//         for (DixElement e : children) {
//             /*
//              * if (e instanceof I) { I ignore = (I) e;
//              * str += ignore.toString(); } if (e instanceof P) { final
//              * P p = (P) e;
//              * 
//              * L lE = p.l; str += lE.toString();
//              * 
//              * R rE = p.restriction; str += rE.toString(); }
//              * 
//              * if (e instanceof Re) { Re re =
//              * (Re) e; str += re.toString(); }
//              */
//             if (e instanceof Par) {
//                 Par par = (Par) e;
//                 String parValue = par.getValue();
//                 String[] parts = parValue.toString().split("__");
//                 String category = "";
//                 for (String element : parts) {
//                     // System.err.print("(" + parts[ignore] + ")");
//                     category = element;
//                 }
//                 str += "/" + category;
//             }
//         }
//         str += "</e>";
//         return str;
//     }

    /**
     * 
     * @return Undefined         */
    public String toPatternString() {
        String str = "";
        for (DixElement e : children) {
            if (e instanceof I) {
                I i = (I) e;
                str += i.getSymbolsAsString();
                str += "/";
                str += i.getSymbolsAsString();
            }
            if (e instanceof P) {
                P p = (P) e;
                L lE = p.l;
                str += lE.getSymbolsAsString();
                str += "/";
                R rE = p.r;
                str += rE.getSymbolsAsString();
            }

        }
        return str;
    }

    public boolean containsRegEx() {
        for (DixElement e : children)
          if (e instanceof Re) return true;
        return false;
    }



    public boolean isMultiWord() {
        for (DixElement e : children)
          if (e instanceof B) return true;
        return false;
    }

    /**
     * 
     * @return Undefined         */
    public Re getFirstRegEx() {
        for (DixElement e : children) {
            if (e instanceof Re) {
                return (Re) e;
            }
        }
        return null;
    }

    
    public E copy() {
      try {
        E cloned=(E) super.clone();
        cloned.children= children_copy();
        return cloned;
      } catch (CloneNotSupportedException ex) {
        ex.printStackTrace();
        return null;
      }
    }

  ArrayList<DixElement> children_copy() {
    ArrayList<DixElement> children_copy = new ArrayList<DixElement>(children);
    for (int i = 0; i < children.size(); i++) {
        DixElement eCloned = children.get(i).copy();
        children_copy.set(i, eCloned);
    }
    return children_copy;
  }


    public static class EElementComparator implements Comparator<E> {
        String side;
        public EElementComparator(String side) {
            this.side = side;
        }

        public boolean ignoreCase=false;

        public int compare(E e1, E anotherEElement) {
            if (anotherEElement == null) return -1;
            if (e1.containsRegEx()) return 0;
            // XXX: the following code always returns true
            //if (!(anotherEElement instanceof E))  throw new ClassCastException("An EElement object expected.");


            String lemma1 = e1.getValue(side);

            String lemma2 = anotherEElement.getValue(side);

            if (lemma1 == null || lemma2 == null)  return 0;

            
            int cmp = ignoreCase?  lemma1.compareToIgnoreCase(lemma2) : lemma1.compareTo(lemma2);
            if (cmp!=0) return cmp;

            // TODO equal lemma, check symbols
            return cmp;
        }
    }

    public static final EElementComparator eElementComparatorL = new EElementComparator("L");

/* TODO UCdetector: Remove unused code: 
    public int compareTo(E anotherEElement)  throws ClassCastException {
        return eElementComparatorL.compare(this, anotherEElement);
    }
*/

    /**
     * 
     * @return Undefined         
     */
    public E reverse() {
        // E eRev = (E) this.clone();
        E eRev = new E();
        if (restriction != null) {
            if (restriction.equals("LR")) {
                eRev.restriction="RL";
            } else {
                if (restriction.equals("RL")) {
                    eRev.restriction="LR";
                }
            }
        }
        for (DixElement e : children) {
            if (e instanceof P) {
                P pE = new P();
                eRev.children.add(pE);
                L newLE = new L();
                R newRE = new R();
                pE.l = newLE;
                pE.r = (newRE);

                L lE = ((P) e).l;
                R rE = ((P) e).r;

                ArrayList<DixElement> auxChildren = lE.children;

                eRev.getFirstPart("L").children=rE.children;
                eRev.getFirstPart("R").children=auxChildren;
            }
            if (e instanceof I) {
                I iE = new I();
                iE.children = ((I) e).children;
                eRev.children.add(iE);
            }
        }
        return eRev;
    }


    /**
     * 
     * @param def
     * @return true if the element containsSymbol certain definition ('adj', 'n', etc.)
     */
    public boolean containsSymbol(String def) {
        return (getFirstPartAsL().containsSymbol(def) || this.getFirstPartAsR().containsSymbol(def));
    }

    /**
     * 
     * @return True if restriction will be solved automatically
     */
    public boolean isRestrictionAuto() {
      return "auto".equals(restriction);
    }
}
