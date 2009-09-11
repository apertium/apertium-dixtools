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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class S extends DixElement {

    /**
     * 
     */
    private static HashMap<String, S> sElementList = new HashMap<String, S>();
    /**
     * 
     */
    private String n;

    /**
     * 
     */
    // private String temp;
    /**
     * 
     * 
     */
    public S() {
        setTagName("s");
    }

    /**
     * 
     * @param value
     */
    public S(String value) {
        setTagName("s");
        n = value;
    }

    /*
     * 
     */
    public S(S sE) {
        setTagName("s");
        n = sE.getValue();
    }

    /**
     * 
     * @return Undefined         */
    @Override
    public String getValue() {
        return n;
    }

    /**
     * 
     * @param value
     */
    @Override
    public void setValue(String value) {
        n = value;
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    public void printXML(Appendable dos, DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        //not strictly necesary for symbols, as they dont have comments: dos.append(prependCharacterData);
        dos.append("<" + getTagName() + " n=\"" + getValue() + "\"/>");
        //not strictly necesary for symbols, as they dont have comments:  dos.append(appendCharacterData);
    }

    /**
     * 
     * @return Undefined         */
    public boolean isAdj() {
        return is("adj");
    }

    /**
     * 
     * @return Undefined         */
    public boolean isN() {
        return is("n");
    }

    /**
     * 
     * @param value
     * @return Undefined         */
    public boolean is(String value) {
        if (n.equals(value)) {
            return true;
        }
        return false;
    }

    /**
     * 
     */
    @Override
    public String toString() {
        return "<" + getValue() + ">";
    }

    /**
     * 
     * @param sE
     * @return Undefined         */
    public boolean equals(S sE) {
        return (getValue().equals(sE.getValue()));
    }

    /**
     * 
     * @param sE
     */
    public static void putSElement(S sE) {
        S.sElementList.put(sE.getValue(), sE);
    }

    /**
     * 
     * @param str
     * @return Undefined         */
    public static S getInstance(String str) {
        S sE = S.sElementList.get(str);
        if (sE==null) {
            sE = new S(str);
            S.putSElement(sE);
        }
        return sE;
    }
}
