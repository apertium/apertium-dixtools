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

import java.io.IOException;
import java.util.ArrayList;

import dics.elements.utils.ElementList;
import dics.elements.utils.Msg;
import dics.elements.utils.SElementList;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class EElement extends Element implements Cloneable,
        Comparable<EElement> {

    /**
     * 
     */
    public static int nEElements = 0;
    /**
     * 
     */
    private ElementList children;
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
        children = new ElementList();
        EElement.incrEElements();
    }

    /**
     * 
     * @param r
     * @param lm
     * @param a
     * @param c
     */
    public EElement(final String r, final String lm, final String a,
            final String c) {
        children = new ElementList();
        this.r = r;
        this.lm = lm;
        this.a = a;
        this.c = c;
        EElement.incrEElements();

    }

    /**
     * 
     * @return Undefined         */
    public final ElementList getChildren() {
        return children;
    }

    /**
     * 
     * 
     */
    public static final void incrEElements() {
        EElement.nEElements++;
    }

    /**
     * 
     * @param value
     */
    public final void setLemma(final String value) {
        lm = value;
    }

    /**
     * 
     * @return Undefined         */
    public final String getLemma() {
        return lm;
    }

    /**
     * 
     * @param value
     */
    public final void setRestriction(final String value) {
        r = value;
    }

    /**
     * 
     * @return Undefined         */
    public final String getRestriction() {
        return r;
    }

    /**
     * 
     * @param comment
     */
    public final void setComment(final String comment) {
        c = comment;
    }

    /**
     * 
     * @return Undefined         */
    public final String getComment() {
        return c;
    }

    /**
     * 
     * @return The 'slr' attribute
     */
    public final String getSlr() {
        return this.slr;
    }

    /**
     * 
     * @return The 'srl' attribute
     */
    public final String getSrl() {
        return this.srl;
    }

    /**
     * 
     * @param slr
     */
    public final void setSlr(final String slr) {
        this.slr = slr;
    }

    /**
     * 
     * @param srl
     */
    public final void setSrl(final String srl) {
        this.srl = srl;
    }

    /**
     * 
     * @param value
     * @param side
     */
    public final void setComments(final String value, final String side) {
        for (final Element e : children) {
            if (e instanceof IElement) {
                ((IElement) e).setComments(value);
            }
            if (e instanceof PElement) {
                ((PElement) e).setComments(value, side);
            }
        }
    }

    /**
     * 
     * @param i
     */
    public final void setIgnore(final String i) {
        this.i = i;
    }

    /**
     * 
     * @return 'i' attribute
     */
    public final String getIgnore() {
        return this.i;
    }

    /**
     * 
     * @param value
     */
    public final void setAuthor(final String value) {
        a = value;
    }

    /**
     * 
     * @return Undefined         */
    public final String getAuthor() {
        return a;
    }

    /**
     * @return Undefined         */
    public final String getHash() {
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
    public final void setTranslation(final String value, final String side) {
        for (final Element e : children) {
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
    public final void addChild(final Element e) {
        children.add(e);
    }

    /**
     * Comprueba si dos entradas son iguales (de la lengua comn a los dos
     * elementos)
     * 
     * @param e
     * @return Undefined         */
    public boolean equalsBil(final EElement e) {
        final String value1 = getValue("L");
        final String value2 = e.getValue("L");

        if (value1.equals(value2)) {
            return true;
        }
        return false;
    }

    /**
     * 
     * @return Undefined         */
    public final PElement getP() {
        for (final Element e : children) {
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
    public final IElement getI() {
        for (final Element e : children) {
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
    public final String getValue(final String side) {
        for (final Element e : children) {
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
    public final String getValueNoTags(final String side) {
        for (final Element e : children) {
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
     * 
     * @param side
     * @return Undefined         
     */
    public ContentElement getSide(final String side) {
        for (final Element e : children) {
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
    public final ElementList getChildren(final String side) {
        for (final Element e : children) {
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
    public final String setValue(final String side, final String value) {
        for (final Element e : children) {
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
    public final String setChildren(final String side, final ElementList value) {
        for (final Element e : children) {
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
    public final boolean isRegularExpr() {
        for (final Element e : children) {
            if (e instanceof ReElement) {
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
    public final void printXML(final Writer dos) throws IOException {
        // write blank lines and comments from original file
        dos.write(prependCharacterData);

        String attributes = this.getAttrString();
        if (comments != null) {
            dos.write(tab(2) + "<!-- \n");
            dos.write(comments);
            if (!isCommon()) {
                dos.write(tab(2) + "esta entrada no aparece en el otro morfolgico\n");
            }
            dos.write(tab(2) + "-->\n");
        }
        dos.write(tab(2) + "<e" + attributes + ">\n");
        if (children != null) {
            for (final Element e : children) {
                e.printXML(dos);
            }
        }
        dos.write(tab(2) + "</e>\n\n");
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    public final void printXML1Line(final Writer dos)
            throws IOException {
        // write blank lines and comments from original file
        dos.write(prependCharacterData);
      
        String attributes = this.getAttrString();
        if (comments != null) {
            dos.write(tab(2) + "<!-- \n");
            dos.write(comments);
            if (!isCommon()) {
                dos.write(tab(2) + "esta entrada no aparece en el otro morfolgico\n");
            }
            dos.write(tab(2) + "-->\n");
        }
        dos.write("<e" + attributes + ">");
        if (children != null) {
            for (final Element e : children) {
                e.printXML1Line(dos);
            }
        }
        dos.write("</e>\n");
    }

    private final static String spaces = "                      ";
    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    public final void printXML1LineAligned(final Writer dosx, int alignP, int alignR)
            throws IOException {

        // write blanks line and comments from original file
        dosx.write(prependCharacterData);
               
        // prepend comments added in this run
        if (comments != null) {
            //dosx.write(spaces.substring(0, alignP));
            dosx.write("<!-- ");
            dosx.write(comments);
            if (!isCommon()) {
                dosx.write(tab(2) + "esta entrada no aparece en el otro morfolgico\n");
            }
            dosx.write( "-->\n");
        }

        StringWriter dos = new StringWriter(120);

        String attributes = this.getAttrString();
        //int addSpace = Math.max(0, Math.min(spaces.length(), alignP-attributes.length()));
        //dos.write("<e" + attributes + spaces.substring(0,addSpace)+ ">");              
        //dos.write(spaces.substring(0,addSpace) + "<e" + attributes + ">");        
        dos.write("<e" + attributes + ">");              
        int neededSpaces = alignP - dos.getBuffer().length();
        if (neededSpaces>0) {
          dos.write(spaces.substring(0, Math.min(spaces.length(), neededSpaces)));
        }        
        
        if (children != null) {
            for (final Element e : children) {                
                if (e instanceof PElement) {
                  ((PElement) e).printXML1LineAligned(dos, alignR);
                } else {
                  e.printXML1Line(dos);                
                }
            }
        }
        dos.write("</e>\n");
        dosx.write(dos.getBuffer().toString());
    }


    
    /**
     * 
     * @return String of attributes
     */
    private final String getAttrString() {
        String attributes = "";
        if (r != null) {
            attributes += " r=\"" + r + "\"";
        }
        if (slr != null) {
            attributes += " slr=\"" + slr + "\"";
        }
        if (srl != null) {
            attributes += " srl=\"" + srl + "\"";
        }
        if (lm != null) {
            attributes += " lm=\"" + lm + "\"";
        }
        if (a != null) {
            attributes += " a=\"" + a + "\"";
        }
        if (c != null) {
            attributes += " c=\"" + c + "\"";
        }
        if (i != null) {
            attributes += " i=\"" + i + "\"";
        }
        if (aversion != null) {
            attributes += " aversion=\"" + aversion + "\"";
        }
        if (alt != null) {
            attributes += " alt=\"" + alt + "\"";
        }
        return attributes;
    }

    /**
     * 
     * @param side
     * @return Undefined         */
    public final String getCategory(final String side) {
        final ArrayList<String> categories = new ArrayList<String>();
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

        for (final String s : categories) {
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
    public final String getCategory(final String side,
            final ArrayList<String> categories) {
        for (final String s : categories) {
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
    public final boolean is(final String side, final String value) {
        for (final Element e : children) {
            if (e instanceof IElement) {
                final IElement ie = (IElement) e;
                return ie.is(value);
            }
            if (e instanceof PElement) {
                final PElement p = (PElement) e;
                if (side.equals("L")) {
                    final LElement lE = p.getL();
                    return lE.is(value);
                }
                if (side.equals("R")) {
                    final RElement rE = p.getR();
                    return rE.is(value);
                }
            }
        }
        return false;
    }

    public final int getNumberOfSElements(final String side) {
        for (final Element e : children) {
            if (e instanceof IElement) {
                final IElement i = (IElement) e;
                return i.getSElements().size();
            }
            if (e instanceof PElement) {
                final PElement p = (PElement) e;
                if (side.equals("L")) {
                    final LElement lE = p.getL();
                    return lE.getSElements().size();
                }
                if (side.equals("R")) {
                    final RElement rE = p.getR();
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
    public final boolean isAdj(final String side) {
        return is(side, "adj");
    }

    /**
     * 
     * @param side
     * @return Undefined         */
    public final boolean isNoun(final String side) {
        return is(side, "n");
    }

    /**
     * 
     * @return Undefined         */
    public final boolean isLR() {
        if (r.equals("LR")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @return Undefined         */
    public final boolean isRL() {
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
    public final boolean hasRestriction(final String value) {
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
    public final boolean hasRestriction() {
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
    public final boolean is_LR_or_LRRL() {
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
    public final boolean is_RL_or_LRRL() {
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
    public final boolean containsSElements(final String side,
            final SElementList elementsB) {
        final SElementList elementsA = getSElements(side);
        if (elementsA.size() != elementsB.size()) {
            return false;
        } else {
            int i = 0;
            for (final SElement s1 : elementsA) {
                boolean exists = false;
                for (final SElement s2 : elementsB) {
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
    public final SElementList getSElements(final String side) {
        SElementList elementsA = null;
        for (final Element e : children) {
            if (e instanceof IElement) {
                final IElement i = (IElement) e;
                elementsA = i.getSElements();
            }
            if (e instanceof PElement) {
                final PElement p = (PElement) e;
                if (side.equals("L")) {
                    final LElement lE = p.getL();
                    elementsA = lE.getSElements();
                }
                if (side.equals("R")) {
                    final RElement rE = p.getR();
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
    public final void printSElements(final String side, Msg msg) {
        final SElementList elements = getSElements(side);
        if (elements != null) {
            for (final SElement s : elements) {
                msg.log(s.toString());
            }
        }
    }

    /**
     * 
     * @param side
     * @param msg
     */
    public final void print(final String side, Msg msg) {
        msg.log(getSide(side).getValue() + " / ");
        printSElements(side, msg);
        msg.log("\n");
    }

    /**
     * 
     * @param side
     * @return Undefined         */
    public final String getSElementsString(final String side) {
        final SElementList elements = getSElements(side);
        String str = "";
        if (elements != null) {
            for (final SElement s : elements) {
                str += "<s n=\"" + s.getValue() + "\"/>";
            }
        }
        return str;
    }

    /**
     * 
     * @param side
     * @return Undefined         */
    public final String getInfo(final String side) {
        final SElementList elements = getSElements(side);
        String str = "( ";
        for (final SElement s : elements) {
            str += s.getValue() + " ";
        }
        str += ")";
        return str;
    }

    /**
     * 
     * @return Undefined         */
    public final String getParadigmValue() {
        // Returns value of first paradigm
        for (final Element e : children) {
            if (e instanceof ParElement) {
                final ParElement parE = (ParElement) e;
                return parE.getValue();
            }
        }
        return null;
    }

    /**
     * 
     * @return Undefined         */
    public final ParElement getParadigm() {
        // Returns value of first paradigm
        for (final Element e : children) {
            if (e instanceof ParElement) {
                final ParElement parE = (ParElement) e;
                return parE;
            }
        }
        return null;
    }

    /**
     * 
     * @param value
     */
    public final void setShared(final boolean value) {
        shared = value;
    }

    /**
     * 
     * @return Undefined         */
    public final boolean isShared() {
        return shared;
    }

    /**
     * 
     * @param value
     */
    public final void setCommon(final boolean value) {
        common = value;
    }

    /**
     * 
     * @return Undefined         */
    public final boolean isCommon() {
        return common;
    }

    /**
     * 
     * @param value
     */
    public final void setForeign(final boolean value) {
        foreign = value;
    }

    /**
     * 
     * @return Undefined         */
    public final boolean isForeign() {
        return foreign;
    }

    /**
     * 
     * @param value
     */
    public final void setLocked(final boolean value) {
        locked = value;
    }

    /**
     * 
     * @return Undefined         */
    public final boolean isLocked() {
        return locked;
    }

    /**
     * 
     * @param side
     * @param newCategory
     */
    public final void changeCategory(final String side, final String newCategory) {
        for (final Element e : children) {
            if (e instanceof IElement) {
                final IElement i = (IElement) e;
                i.changeFirstSElement(newCategory);
            }
            if (e instanceof PElement) {
                final PElement p = (PElement) e;
                if (side.equals("L")) {
                    final LElement lE = p.getL();
                    lE.changeFirstSElement(newCategory);
                }
                if (side.equals("R")) {
                    final RElement rE = p.getR();
                    rE.changeFirstSElement(newCategory);
                }
            }
        }

    }

    /**
     * 
     */
    @Override
    public final String toString() {
        String str = "";
        String r = "";
        if (this.hasRestriction()) {
            r = " r=\"" + getRestriction() + "\"";
        }
        str += "<e" + r + ">";
        for (final Element e : children) {
            if (e instanceof IElement) {
                final IElement i = (IElement) e;
                str += i.toString();
            }
            if (e instanceof PElement) {
                final PElement p = (PElement) e;

                final LElement lE = p.getL();
                str += lE.toString();

                final RElement rE = p.getR();
                str += rE.toString();
            }
            if (e instanceof ParElement) {
                final ParElement par = (ParElement) e;
                str += par.toString();
            }
            if (e instanceof ReElement) {
                final ReElement re = (ReElement) e;
                str += re.toString();
            }

        }
        str += "</e>";
        return str;
    }

    /**
     * 
     * @return Undefined         
     */
    public final String toStringAll() {
        String str = "";
        String r = "";
        if (this.hasRestriction()) {
            r = " r=\"" + getRestriction() + "\"";
        }
        str += "<e" + r + ">";
        for (final Element e : children) {
            str += e.toString();
        }
        str += "</e>";
        return str;
    }

    /**
     * 
     * @return Undefined         */
    public final String toStringNoParadigm() {
        String str = "";
        String r = "";
        if (this.hasRestriction()) {
            r = " r=\"" + getRestriction() + "\"";
        }
        str += "<e" + r + ">";
        for (final Element e : children) {
            if (e instanceof IElement) {
                final IElement i = (IElement) e;
                str += i.toString();
            }
            if (e instanceof PElement) {
                final PElement p = (PElement) e;

                final LElement lE = p.getL();
                str += lE.toString();

                final RElement rE = p.getR();
                str += rE.toString();
            }
            if (e instanceof ReElement) {
                final ReElement re = (ReElement) e;
                str += re.toString();
            }

        }
        str += "</e>";
        return str;
    }

    /**
     * 
     * @return Undefined         */
    public final String lemmaAndCategory() {
        String str = "";
        String r = "";
        if (this.hasRestriction()) {
            r = " r=\"" + getRestriction() + "\"";
        }
        str += "<e" + r + ">";
        str += getLemma();
        for (final Element e : children) {
            /*
             * if (e instanceof IElement) { final IElement i = (IElement) e;
             * str += i.toString(); } if (e instanceof PElement) { final
             * PElement p = (PElement) e;
             * 
             * final LElement lE = p.getL(); str += lE.toString();
             * 
             * final RElement rE = p.getR(); str += rE.toString(); }
             * 
             * if (e instanceof ReElement) { final ReElement re =
             * (ReElement) e; str += re.toString(); }
             */
            if (e instanceof ParElement) {
                final ParElement par = (ParElement) e;
                final String parValue = par.getValue();
                String[] parts = parValue.toString().split("__");
                String category = "";
                for (String element : parts) {
                    // System.out.print("(" + parts[i] + ")");
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
    public final String toString2() {
        String str = "";
        for (final Element e : children) {
            if (e instanceof IElement) {
                final IElement i = (IElement) e;
                str += i.toString2();
                str += "/";
                str += i.toString2();
            }
            if (e instanceof PElement) {
                final PElement p = (PElement) e;

                final LElement lE = p.getL();
                str += lE.toString2();
                str += "/";
                final RElement rE = p.getR();
                str += rE.toString2();
            }

        }
        return str;
    }

    /**
     * 
     * @return Undefined         */
    public final boolean isRegEx() {
        for (final Element e : children) {
            if (e instanceof ReElement) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return Undefined         */
    public final ReElement getRegEx() {
        for (final Element e : children) {
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
            final EElement cloned = (EElement) super.clone();
            cloned.children = (ElementList) children.clone();
            return cloned;
        } catch (final Exception ex) {
            return null;
        }
    }

    /**
     * 
     * @param anotherEElement
     * @return int value
     * @throws java.lang.ClassCastException
     */
    public int compareTo(final EElement anotherEElement)
            throws ClassCastException {

        if (anotherEElement == null) {
            return -1;
        }

        if (isRegEx()) {
            return 0;
        }

        if (!(anotherEElement instanceof EElement)) {
            throw new ClassCastException("An EElement object expected.");
        }

        final String lemma1 = getValue("L");

        final String lemma2 = (anotherEElement).getValue("L");

        if (lemma1 == null || lemma2 == null) {
            return 0;
        } else {
            if (lemma1.compareTo(lemma2) == 0) {
                return 0;
            }
            if (lemma1.compareTo(lemma2) < 0) {
                return -1;
            }
        }
        return 1;
    }

    /**
     * 
     * @return Undefined         
     */
    public final EElement reverse() {
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
    public final String getPatternApplied() {
        return patternApplied;
    }

    /**
     * @param patternApplied
     *                the patternApplied to set
     */
    public final void setPatternApplied(String patternApplied) {
        this.patternApplied = patternApplied;
    }

    /**
     * @return the aversion
     */
    public final String getAversion() {
        return aversion;
    }

    /**
     * @param aversion
     *                the aversion to set
     */
    public final void setAversion(String aversion) {
        this.aversion = aversion;
    }

    /**
     * 
     * @return Get attribute 'alt'
     */
    public final String getAlt() {
        return this.alt;
    }

    /**
     * 
     * @param alt
     */
    public final void setAlt(final String alt) {
        this.alt = alt;
    }

    /**
     * 
     * @param def
     * @return true if the element contains certain definition ('adj', 'n', etc.)
     */
    public final boolean contains(final String def) {
        return (getLeft().contains(def) || this.getRight().contains(def));
    }

    /**
     * 
     * @return True if restriction will be solved automatically
     */
    public final boolean isRestrictionAuto() {
        if (r == null) {
            return false;
        } else {
            return this.r.equals("auto");
        }
    }
}
