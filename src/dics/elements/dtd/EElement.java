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
import java.util.ArrayList;

import dics.elements.utils.ElementList;
import dics.elements.utils.Msg;
import dics.elements.utils.SElementList;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Comparator;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class EElement extends Element implements Cloneable {

    /**
     * 
     */
    private ElementList children = new ElementList();
;
    /**
     * 
     */
    private String r;
    /**
     * 
     */
    private String slr;
    /**
     * 
     */
    private String srl;
    /**
     * 
     */
    private String lm;
    /**
     * 
     */
    private String a;
    /**
     * 
     */
    private String c;
    /**
     * 
     */
    private String i;
    /**
     * 
     */
    private String aversion;
    /**
     * 
     */
    private String alt;
    /**
     * 
     */
    private boolean shared = false;
    /**
     * 
     */
    private boolean common = true;
    /**
     * 
     */
    private boolean foreign = false;
    /**
     * 
     */
    private String patternApplied;
    /**
     * 
     */
    private boolean locked = false;

    /**
     * 
     * 
     */
    public EElement() {
    }

    /**
     * 
     * @param r
     * @param lm
     * @param a
     * @param c
     */
    public EElement(String r, String lm, String a,  String c) {
        this.r = r;
        this.lm = lm;
        this.a = a;
        this.c = c;
    }

    /**
     * 
     * @return Undefined         */
    public ElementList getChildren() {
        return children;
    }

    public boolean hasPrependorAppendData() {
        return !(prependCharacterData.trim().isEmpty() && appendCharacterData.trim().isEmpty());
    }

    /**
     * 
     * @param value
     */
    public void setLemma(String value) {
        lm = value;
    }

    /**
     * 
     * @return Undefined         */
    public String getLemma() {
        return lm;
    }

    /**
     * 
     * @param value
     */
    public void setRestriction(String value) {
        r = value;
    }

    /**
     * 
     * @return Undefined         */
    public String getRestriction() {
        return r;
    }

    /**
     * 
     * @param comment
     */
    public void setComment(String comment) {
        c = comment;
    }

    /**
     * 
     * @return Undefined         */
    public String getComment() {
        return c;
    }

    /**
     * 
     * @return The 'slr' attribute
     */
    public String getSlr() {
        return this.slr;
    }

    /**
     * 
     * @return The 'srl' attribute
     */
    public String getSrl() {
        return this.srl;
    }

    /**
     * 
     * @param slr
     */
    public void setSlr(String slr) {
        this.slr = slr;
    }

    /**
     * 
     * @param srl
     */
    public void setSrl(String srl) {
        this.srl = srl;
    }

    /**
     * 
     * @param value
     * @param side
     */
    public void setProcessingComments(String value, String side) {
        for (Element e : children) {
            if (e instanceof IElement) {
                ((IElement) e).setProcessingComments(value);
            }
            if (e instanceof PElement) {
                ((PElement) e).setProcessingComments(value, side);
            }
        }
    }

    /**
     * 
     * @param i
     */
    public void setIgnore(String i) {
        this.i = i;
    }

    /**
     * 
     * @return 'i' attribute
     */
    public String getIgnore() {
        return this.i;
    }

    /**
     * 
     * @param value
     */
    public void setAuthor(String value) {
        a = value;
    }

    /**
     * 
     * @return Undefined         */
    public String getAuthor() {
        return a;
    }

    /**
     * @return Undefined         */
    public String getHash() {
        String str = "";
        if (hasRestriction()) {
            str += str + getRestriction() + "---";
        }
        str += getValue("L") + "---" + getSElementsString("L") + "---" + getValue("R") + "---" + getSElementsString("R");
        return str;
    }

    /**
     * 
     * @param value
     */
    public void setTranslation(String value, String side) {
        for (Element e : children) {
            if (e instanceof IElement) {
                ((IElement) e).setValue(value);
            }
            if (e instanceof PElement) {
                if (side.equals("L")) {
                    ((PElement) e).getL().setValue(value);
                }
                if (side.equals("R")) {
                    ((PElement) e).getR().setValue(value);
                }
            }
        }
    }

    /**
     * 
     * @param e
     */
    public void addChild(Element e) {
        children.add(e);
    }

    /**
     * Comprueba si dos entradas son iguales (de la lengua comn a los dos
     * elementos)
     * 
     * @param e
     * @return Undefined         */
    public boolean equalsBil(EElement e) {
        String value1 = getValue("L");
        String value2 = e.getValue("L");

        if (value1.equals(value2)) {
            return true;
        }
        return false;
    }

    /**
     * 
     * @return Undefined         */
    public PElement getP() {
        for (Element e : children) {
            if (e instanceof PElement) {
                return (PElement) e;
            }
        }
        return null;
    }

    /**
     * 
     * @return Undefined         
     */
    public IElement getI() {
        for (Element e : children) {
            if (e instanceof IElement) {
                return (IElement) e;
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
        for (Element e : children) {
            if (e instanceof IElement) {
                return ((IElement) e).getValue();
            }
            if (e instanceof PElement) {
                if (side.equals("L")) {
                    return ((PElement) e).getL().getValue();
                }
                if (side.equals("R")) {
                    return ((PElement) e).getR().getValue();
                }
            }
        }
        return getLemma();
    }

    /**
     * 
     * @param side
     * @return Value without tags
     */
    public String getValueNoTags(String side) {
        for (Element e : children) {
            if (e instanceof IElement) {
                return ((IElement) e).getValueNoTags();
            }
            if (e instanceof PElement) {
                if (side.equals("L")) {
                    return ((PElement) e).getL().getValueNoTags();
                }
                if (side.equals("R")) {
                    return ((PElement) e).getR().getValueNoTags();
                }
            }
        }
        return getLemma();
    }

    /**
     * Returns the first part of the left or right side of an entry (or the first invariant section).
     * Examples:
     * <pre>
     * <e><i>Ameriko</i><par n="Barcelono__np"/> </e>   gives 'Ameriko'
     * <e><i>Al</i><par n="ĝ"/> <i>erio</i><par n="Barcelono__np"/> </e> gives just 'Al'
     * <e><l>mi</l><r>mi<prn><ref><p1><mf><sg></r></e> give 'mi'
     * </pre>
     * @param side can be R or L
     * @return A ContentElement object
     */
    public ContentElement getSide(String side) {
        for (Element e : children) {
            if (e instanceof IElement) {
                return ((IElement) e);
            }
            if (e instanceof PElement) {
                if (side.equals("L")) {
                    return ((PElement) e).getL();
                }
                if (side.equals("R")) {
                    return ((PElement) e).getR();
                }
            }
        }
        return null;
    }

    /**
     * 
     * @return Undefined         */
    public LElement getLeft() {
        ContentElement cE = getSide("L");
        LElement lE = null;
        if (cE instanceof IElement) {
            lE = new LElement(cE);
            return lE;
        }
        return (LElement) cE;
    }

    /**
     * 
     * @return Undefined         */
    public RElement getRight() {
        ContentElement cE = getSide("R");
        RElement rE = null;
        if (cE instanceof IElement) {
            rE = new RElement(cE);
            return rE;
        }
        return (RElement) cE;
    }

    /**
     * 
     * @param side
     * @return Undefined         */
    public ElementList getChildren(String side) {
        for (Element e : children) {
            if (e instanceof IElement) {
                return ((IElement) e).getChildren();
            }
            if (e instanceof PElement) {
                if (side.equals("L")) {
                    return ((PElement) e).getL().getChildren();
                }
                if (side.equals("R")) {
                    return ((PElement) e).getR().getChildren();
                }
            }
        }

        return null;
    }

    /**
     * 
     * @param side
     * @param value
     * @return Undefined         */
    public String setValue(String side, String value) {
        for (Element e : children) {
            if (e instanceof IElement) {
                ((IElement) e).setValue(value);
            }
            if (e instanceof PElement) {
                if (side.equals("L")) {
                    ((PElement) e).getL().setValue(value);
                }
                if (side.equals("R")) {
                    ((PElement) e).getR().setValue(value);
                }
            }
        }

        return null;
    }

    /**
     * 
     * @param side
     * @param value
     * @return Undefined         */
    public String setChildren(String side, ElementList value) {
        for (Element e : children) {
            if (e instanceof IElement) {
                ((IElement) e).setChildren(value);
            }
            if (e instanceof PElement) {
                if (side.equals("L")) {
                    ((PElement) e).getL().setChildren(value);
                }
                if (side.equals("R")) {
                    ((PElement) e).getR().setChildren(value);
                }
            }
        }

        return null;
    }

    /**
     * 
     * @return Undefined         */
    public boolean isRegularExpr() {
        for (Element e : children) {
            if (e instanceof ReElement) {
                return true;
            }
        }
        return false;
    }

    private static String spaces = "                                                                                                                 ";
    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
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
            String pc = processingComments;
            if (!isCommon()) {
                pc = pc + "\n"+tab(2) + "esta entrada no aparece en el otro morfolgico\n";
            }
            dos.append(makeCommentIfData(pc));
        }

        String attributes = this.getAttrString();
        if (!opt.nowAlign) {
            dos.append(tab(2) + "<e" + attributes + ">\n");

            for (Element e : children) {
                e.printXML(dos, opt);
            }

            dos.append(tab(2) + "</e>"+appendCharacterData+"\n\n");
        } else { 
            StringBuilder dosy = new StringBuilder(120);
            dosy.append(spaces.substring(0,opt.alignE));
            dosy.append("<e").append(attributes).append(">");
            int neededSpaces = opt.alignP - dosy.length();
            if (neededSpaces>0) {
              dosy.append(spaces.substring(0, Math.min(spaces.length(), neededSpaces)));
            }        

            if (children != null) {
                for (Element e : children) {                
                    if (e instanceof PElement) {
                      ((PElement) e).printXML1LineAligned(dosy, opt.alignR);
                    } else {
                      e.printXML(dosy, opt);
                    }
                }
            }
            dosy.append("</e>"+appendCharacterData+"\n");
            dos.append(dosy.toString());
        }
    }

    
    /**
     * 
     * @return String of attributes
     */
    private String getAttrString() {
        StringBuilder attributes = new StringBuilder();
        if (r != null) {
            attributes.append(" r=\"" + r + "\"");
        }
        if (slr != null) {
            attributes.append(" slr=\"" + slr + "\"");
        }
        if (srl != null) {
            attributes.append(" srl=\"" + srl + "\"");
        }
        if (lm != null) {
            attributes.append(" lm=\"" + lm + "\"");
        }
        if (a != null) {
            attributes.append(" a=\"" + a + "\"");
        }
        if (c != null) {
            attributes.append(" c=\"" + c + "\"");
        }
        if (i != null && !i.isEmpty()) {
            attributes.append(" i=\"" + i + "\"");
        }
        if (aversion != null) {
            attributes.append(" aversion=\"" + aversion + "\"");
        }
        if (alt != null) {
            attributes.append(" alt=\"" + alt + "\"");
        }
        return attributes.toString();
    }

    /**
     * 
     * @param side
     * @return Undefined         */
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
            if (is("L", s)) {
                return s;
            }
        }
        return null;
    }

    /**
     * 
     * @param side
     * @param categories
     * @return Undefined         */
    public String getCategory(String side,
            ArrayList<String> categories) {
        for (String s : categories) {
            if (is("L", s)) {
                return s;
            }
        }
        return null;
    }

    /**
     * 
     * @param side
     * @param value
     * @return Undefined         
     */
    public boolean is(String side, String value) {
        for (Element e : children) {
            if (e instanceof IElement) {
                IElement ie = (IElement) e;
                return ie.is(value);
            }
            if (e instanceof PElement) {
                PElement p = (PElement) e;
                if (side.equals("L")) {
                    LElement lE = p.getL();
                    return lE.is(value);
                }
                if (side.equals("R")) {
                    RElement rE = p.getR();
                    return rE.is(value);
                }
            }
        }
        return false;
    }

    public int getNumberOfSElements(String side) {
        for (Element e : children) {
            if (e instanceof IElement) {
                IElement i = (IElement) e;
                return i.getSElements().size();
            }
            if (e instanceof PElement) {
                PElement p = (PElement) e;
                if (side.equals("L")) {
                    LElement lE = p.getL();
                    return lE.getSElements().size();
                }
                if (side.equals("R")) {
                    RElement rE = p.getR();
                    return rE.getSElements().size();
                }
            }
        }
        return 0;
    }

    /**
     * 
     * @param side
     * @return Undefined         */
    public boolean isAdj(String side) {
        return is(side, "adj");
    }

    /**
     * 
     * @param side
     * @return Undefined         */
    public boolean isNoun(String side) {
        return is(side, "n");
    }

    /**
     * 
     * @return Undefined         */
    public boolean isLR() {
        if (r.equals("LR")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @return Undefined         */
    public boolean isRL() {
        if (r.equals("RL")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @param value
     * @return Undefined         */
    public boolean hasRestriction(String value) {
        if (r == null || this.isRestrictionAuto()) {
            return true;
        } else {
            if (r.equals(value)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Has restriction LR, RL or LR/RL
     * 
     * @return Undefined         */
    public boolean hasRestriction() {
        if (r == null || this.isRestrictionAuto()) {
            return false;
        } else {
            if (r.equals("LR") || r.equals("RL")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return Undefined         */
    public boolean is_LR_or_LRRL() {
        if (r == null) {
            return true;
        } else {
            if (r.equals("LR")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return Undefined         */
    public boolean is_RL_or_LRRL() {
        if (r == null) {
            return true;
        } else {
            if (r.equals("RL")) {
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
     */
    public boolean containsSElements(String side,
            SElementList elementsB) {
        SElementList elementsA = getSElements(side);
        if (elementsA.size() != elementsB.size()) {
            return false;
        } else {
            int i = 0;
            for (SElement s1 : elementsA) {
                boolean exists = false;
                for (SElement s2 : elementsB) {
                    if (s1.equals(s2) && (exists == false)) {
                        exists = true;
                    }
                }
                if (exists) {
                    i++;
                }
            }
            if (i == elementsA.size()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param side
     * @return Undefined         
     */
    public SElementList getSElements(String side) {
        SElementList elementsA = null;
        for (Element e : children) {
            if (e instanceof IElement) {
                IElement i = (IElement) e;
                elementsA = i.getSElements();
            }
            if (e instanceof PElement) {
                PElement p = (PElement) e;
                if (side.equals("L")) {
                    LElement lE = p.getL();
                    elementsA = lE.getSElements();
                }
                if (side.equals("R")) {
                    RElement rE = p.getR();
                    elementsA = rE.getSElements();
                }
            }
        }
        return elementsA;
    }

    /**
     * 
     * @param side
     * @param msg
     */
    public void printSElements(String side, Msg msg) {
        SElementList elements = getSElements(side);
        if (elements != null) {
            for (SElement s : elements) {
                msg.log(s.toString());
            }
        }
    }

    /**
     * 
     * @param side
     * @param msg
     */
    public void print(String side, Msg msg) {
        msg.log(getSide(side).getValue() + " / ");
        printSElements(side, msg);
        msg.log("\n");
    }

    /**
     * 
     * @param side
     * @return Undefined         */
    public String getSElementsString(String side) {
        SElementList elements = getSElements(side);
        String str = "";
        if (elements != null) {
            for (SElement s : elements) {
                str += "<s n=\"" + s.getValue() + "\"/>";
            }
        }
        return str;
    }

    /**
     * 
     * @param side
     * @return Undefined         */
    public String getInfo(String side) {
        SElementList elements = getSElements(side);
        String str = "( ";
        for (SElement s : elements) {
            str += s.getValue() + " ";
        }
        str += ")";
        return str;
    }

    /**
     * 
     * @return Undefined         */
    public String getMainParadigmName() {
        // Returns value of main paradigm
        ParElement parE = null;
        for (Element e : children) {
            if (e instanceof ParElement) {
                parE = (ParElement) e;

                if (parE.getValue().contains("__"))
                    return parE.getValue();
            }
        }
        // no main paradigm (containing __) was found.
        // assume last met paradigm is the main one, then
        if (parE!=null) return parE.getValue();
        // no paradimgs at all
        return null;
    }

    /**
     * 
     * @return Undefined         */
    public ParElement getParadigm() {
        // Returns value of first paradigm
        for (Element e : children) {
            if (e instanceof ParElement) {
                ParElement parE = (ParElement) e;
                return parE;
            }
        }
        return null;
    }

    /**
     * 
     * @param value
     */
    public void setShared(boolean value) {
        shared = value;
    }

    /**
     * 
     * @return Undefined         */
    public boolean isShared() {
        return shared;
    }

    /**
     * 
     * @param value
     */
    public void setCommon(boolean value) {
        common = value;
    }

    /**
     * 
     * @return Undefined         */
    public boolean isCommon() {
        return common;
    }

    /**
     * 
     * @param value
     */
    public void setForeign(boolean value) {
        foreign = value;
    }

    /**
     * 
     * @return Undefined         */
    public boolean isForeign() {
        return foreign;
    }

    /**
     * 
     * @param value
     */
    public void setLocked(boolean value) {
        locked = value;
    }

    /**
     * 
     * @return Undefined         */
    public boolean isLocked() {
        return locked;
    }

    /**
     * 
     * @param side
     * @param newCategory
     */
    public void changeCategory(String side, String newCategory) {
        for (Element e : children) {
            if (e instanceof IElement) {
                IElement i = (IElement) e;
                i.changeFirstSElement(newCategory);
            }
            if (e instanceof PElement) {
                PElement p = (PElement) e;
                if (side.equals("L")) {
                    LElement lE = p.getL();
                    lE.changeFirstSElement(newCategory);
                }
                if (side.equals("R")) {
                    RElement rE = p.getR();
                    rE.changeFirstSElement(newCategory);
                }
            }
        }

    }

    /**
     * 
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(50);
        str.append("<e");
        if (this.i != null) {
            str.append(" i=\"" + i + "\"");
        }
        if (this.hasRestriction()) {
            str.append(" r=\"" + getRestriction() + "\"");
        }
        str.append(">");
        for (Element e : children) {
            if (e instanceof IElement) {
                IElement i = (IElement) e;
                str.append(i.toString());
            }
            if (e instanceof PElement) {
                PElement p = (PElement) e;

                LElement lE = p.getL();
                str.append(lE.toString());

                RElement rE = p.getR();
                str.append(rE.toString());
            }
            if (e instanceof ParElement) {
                ParElement par = (ParElement) e;
                str.append(par.toString());
            }
            if (e instanceof ReElement) {
                ReElement re = (ReElement) e;
                str.append(re.toString());
            }

        }
        str.append("</e>");
        return str.toString();
    }

    /**
     * 
     * @return Undefined         
     */
    public String toStringAll() {
        String str = "";
        String r = "";
        if (this.hasRestriction()) {
            r = " r=\"" + getRestriction() + "\"";
        }
        str += "<e" + r + ">";
        for (Element e : children) {
            str += e.toString();
        }
        str += "</e>";
        return str;
    }

    /**
     * 
     * @return Undefined         */
    public String toStringNoParadigm() {
        String str = "";
        String r = "";
        if (this.hasRestriction()) {
            r = " r=\"" + getRestriction() + "\"";
        }
        str += "<e" + r + ">";
        for (Element e : children) {
            if (e instanceof IElement) {
                IElement i = (IElement) e;
                str += i.toString();
            }
            if (e instanceof PElement) {
                PElement p = (PElement) e;

                LElement lE = p.getL();
                str += lE.toString();

                RElement rE = p.getR();
                str += rE.toString();
            }
            if (e instanceof ReElement) {
                ReElement re = (ReElement) e;
                str += re.toString();
            }

        }
        str += "</e>";
        return str;
    }

    /**
     * 
     * @return Undefined         */
    public String lemmaAndCategory() {
        String str = "";
        String r = "";
        if (this.hasRestriction()) {
            r = " r=\"" + getRestriction() + "\"";
        }
        str += "<e" + r + ">";
        str += getLemma();
        for (Element e : children) {
            /*
             * if (e instanceof IElement) { IElement i = (IElement) e;
             * str += i.toString(); } if (e instanceof PElement) { final
             * PElement p = (PElement) e;
             * 
             * LElement lE = p.getL(); str += lE.toString();
             * 
             * RElement rE = p.getR(); str += rE.toString(); }
             * 
             * if (e instanceof ReElement) { ReElement re =
             * (ReElement) e; str += re.toString(); }
             */
            if (e instanceof ParElement) {
                ParElement par = (ParElement) e;
                String parValue = par.getValue();
                String[] parts = parValue.toString().split("__");
                String category = "";
                for (String element : parts) {
                    // System.err.print("(" + parts[i] + ")");
                    category = element;
                }
                str += "/" + category;
            }
        }
        str += "</e>";
        return str;
    }

    /**
     * 
     * @return Undefined         */
    public String toString2() {
        String str = "";
        for (Element e : children) {
            if (e instanceof IElement) {
                IElement i = (IElement) e;
                str += i.toString2();
                str += "/";
                str += i.toString2();
            }
            if (e instanceof PElement) {
                PElement p = (PElement) e;

                LElement lE = p.getL();
                str += lE.toString2();
                str += "/";
                RElement rE = p.getR();
                str += rE.toString2();
            }

        }
        return str;
    }

    /**
     * 
     * @return Undefined         */
    public boolean isRegEx() {
        for (Element e : children) {
            if (e instanceof ReElement) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return Undefined         */
    public ReElement getRegEx() {
        for (Element e : children) {
            if (e instanceof ReElement) {
                return (ReElement) e;
            }
        }
        return null;
    }

    /**
     * 
     */
    @Override
    public Object clone() {
        try {
            EElement cloned = (EElement) super.clone();
            cloned.children = (ElementList) children.clone();
            return cloned;
        } catch (Exception ex) {
            return null;
        }
    }


    public static class EElementComparator implements Comparator<EElement> {
        String side;
        public EElementComparator(String side) {
            this.side = side;
        }

        public boolean ignoreCase=false;

        @Override
        public int compare(EElement e1, EElement anotherEElement) {
            if (anotherEElement == null) return -1;
            if (e1.isRegEx()) return 0;
            if (!(anotherEElement instanceof EElement))  throw new ClassCastException("An EElement object expected.");


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

    public int compareTo(EElement anotherEElement)  throws ClassCastException {
        return eElementComparatorL.compare(this, anotherEElement);
    }

    /**
     * 
     * @return Undefined         
     */
    public EElement reverse() {
        // EElement eRev = (EElement) this.clone();
        EElement eRev = new EElement();
        if (getRestriction() != null) {
            if (getRestriction().equals("LR")) {
                eRev.setRestriction("RL");
            } else {
                if (getRestriction().equals("RL")) {
                    eRev.setRestriction("LR");
                }
            }
        }
        for (Element e : getChildren()) {
            if (e instanceof PElement) {
                PElement pE = new PElement();
                eRev.addChild(pE);
                LElement newLE = new LElement();
                RElement newRE = new RElement();
                pE.setLElement(newLE);
                pE.setRElement(newRE);

                LElement lE = ((PElement) e).getL();
                RElement rE = ((PElement) e).getR();

                ElementList auxChildren = lE.getChildren();

                eRev.getSide("L").setChildren(rE.getChildren());
                eRev.getSide("R").setChildren(auxChildren);
            }
            if (e instanceof IElement) {
                IElement iE = new IElement();
                iE.setChildren(((IElement) e).getChildren());
                eRev.addChild(iE);
            }
        }
        return eRev;
    }

    /**
     * @return the patternApplied
     */
    public String getPatternApplied() {
        return patternApplied;
    }

    /**
     * @param patternApplied
     *                the patternApplied to set
     */
    public void setPatternApplied(String patternApplied) {
        this.patternApplied = patternApplied;
    }

    /**
     * @return the aversion
     */
    public String getAversion() {
        return aversion;
    }

    /**
     * @param aversion
     *                the aversion to set
     */
    public void setAversion(String aversion) {
        this.aversion = aversion;
    }

    /**
     * 
     * @return Get attribute 'alt'
     */
    public String getAlt() {
        return this.alt;
    }

    /**
     * 
     * @param alt
     */
    public void setAlt(String alt) {
        this.alt = alt;
    }

    /**
     * 
     * @param def
     * @return true if the element contains certain definition ('adj', 'n', etc.)
     */
    public boolean contains(String def) {
        return (getLeft().contains(def) || this.getRight().contains(def));
    }

    /**
     * 
     * @return True if restriction will be solved automatically
     */
    public boolean isRestrictionAuto() {
        if (r == null) {
            return false;
        } else {
            return this.r.equals("auto");
        }
    }
}
