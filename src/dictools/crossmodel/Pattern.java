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
package dictools.crossmodel;

import dics.elements.dtd.ContentElement;
import java.io.IOException;

import dics.elements.dtd.E;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.S;
import dics.elements.dtd.TextElement;
import dics.elements.utils.DicOpts;
import dics.elements.utils.ElementList;
import dics.elements.utils.ElementList;
import dics.elements.utils.Msg;
import java.io.OutputStreamWriter;
import java.util.HashMap;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class Pattern {

    /**
     * 
     */
    private E e1;
    /**
     * 
     */
    private E e2;
    /**
     * 
     */
    private int length = 0;
    private int nConstants = 0;

    /**
     * 
     * 
     */
    public Pattern() {

    }

    /**
     * 
     * @param ab
     * @param bc
     */
    public Pattern(E ab, E bc) {
        e1 = ab;
        e2 = bc;
    }

    /**
     * 
     * @return Undefined         */
    public E getAB() {
        return e1;
    }

    /**
     * 
     * @return Undefined         */
    public E getBC() {
        return e2;
    }

    /**
     * 
     * @param ab
     */
    public void setAB(E ab) {
        e1 = ab;
    }

    /**
     * 
     * @param bc
     */
    public void setBC(E bc) {
        e2 = bc;
    }

    /**
     * 
     * @param msg
     */
    public void print(Msg msg) {
        msg.log("Pattern:\n");
        getAB().print("L", msg);
        getAB().print("R", msg);
        getBC().print("L", msg);
        getBC().print("R", msg);
    }

    /**
     * 
     */
    @Override
    public String toString() {
        String e1 = getAB().toPatternString();
        String e2 = getBC().toPatternString();
        String str = e1 + "/" + e2;
        return str;
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    protected void printXML(Appendable dos, DicOpts opt) throws IOException {
        dos.append("\t<pattern>\n");
        e1.printXML(dos, opt);
        e2.printXML(dos, opt);
        dos.append("\t</pattern>\n");
    }

    /**
     * 
     * @return The list of elements
     */
    public ElementList getElementList() {
        ElementList list = new ElementList();
        list.add(e1.getLeft());
        list.add(e1.getRight());
        list.add(e2.getLeft());
        list.add(e2.getRight());
        return list;
    }

    /**
     * 
     * @return Sequence of elements in pattern
     */
    public ElementList getSequence() {
        ElementList eList = new ElementList();

        eList = this.getSequenceR(this.getAB(), eList);
        eList = this.getSequenceCE(this.getAB().getLeft(), eList);
        eList = this.getSequenceCE(this.getAB().getRight(), eList);

        eList = this.getSequenceR(this.getBC(), eList);
        eList = this.getSequenceCE(this.getBC().getLeft(), eList);
        eList = this.getSequenceCE(this.getBC().getRight(), eList);

        eList.add(new S("^end"));
        return eList;
    }

    /**
     * 
     * @param ee
     * @param eList
     */
    private ElementList getSequenceR(E ee, ElementList eList) {
        if (ee != null) {
            if (ee.hasRestriction()) {
                String r = ee.restriction;
                if (!r.equals("")) {
                    eList.add(new S("^" + r));
                }
            } else {
                eList.add(new S("^LRRL"));
            }
        }
        return eList;
    }

    /**
     * 
     * @param ce
     * @param eList
     */
    private ElementList getSequenceCE(ContentElement ce, ElementList eList) {
        if (ce != null) {
            ElementList ceSeq = ce.getSequence();
            eList = eList.concat(ceSeq);
        }
        eList.add(new S("^b"));
        return eList;
    }

    /**
     * 
     * @return The pattern length
     */
    public int getLength() {
        return length;
    }

    /**
     * 
     * @param length
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * 
     */
    public void incrementLength() {
        this.length++;
    }

    /**
     * 
     * @return Number of constants (literals)
     */
    public int getNConstants() {
        return nConstants;
    }

    /**
     * 
     * @param nConstants
     */
    public void setNConstants(int nConstants) {
        this.nConstants = nConstants;
    }

    /**
     * 
     */
    public void incrementNConstants() {
        this.nConstants++;
    }

    /**
     * 
     * @return true if the pattern is valid
     */
    public boolean isValid() {
        return true;
    }

    /**
     * 
     * @return The names of the variables defined in the pattern
     */
    public HashMap<String, String> getDefinedVariables() {
        HashMap<String, String> definedVars = new HashMap<String, String>();

        getDefinedVarsElement(getAB().getLeft(), definedVars);
        getDefinedVarsElement(getAB().getRight(), definedVars);
        getDefinedVarsElement(getBC().getLeft(), definedVars);
        getDefinedVarsElement(getBC().getRight(), definedVars);

        return definedVars;
    }

    /**
     * 
     * @param ce
     * @param definedVars
     */
    private void getDefinedVarsElement(ContentElement ce, HashMap<String, String> definedVars) {
        for (DixElement e : ce.children) {
            if (e instanceof TextElement) {
                String v = ((TextElement) e).getValue();
                if (v.startsWith("$") || v.startsWith("@")) {
                    definedVars.put(v, v);
                }
            }
            if (e instanceof S) {
                String v = ((S) e).getValue();
                if (v.startsWith("$") || v.startsWith("@")) {
                    definedVars.put(v, v);
                }
            }

        }
    }
}
