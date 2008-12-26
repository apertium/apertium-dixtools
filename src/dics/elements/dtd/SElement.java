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
public class SElement extends Element {

    /**
     * 
     */
    private static HashMap<String, SElement> sElementList = new HashMap<String, SElement>();
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
    public SElement() {
        setTagName("s");
    }

    /**
     * 
     * @param value
     */
    public SElement(String value) {
        setTagName("s");
        n = value;
    }

    /*
     * 
     */
    public SElement(SElement sE) {
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
        dos.append(prependCharacterData);
        dos.append("<" + getTagName() + " n=\"" + getValue() + "\"/>");
        dos.append(appendCharacterData);
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
    public boolean equals(SElement sE) {
        return (getValue().equals(sE.getValue()));
    }

    /**
     * 
     * @param sE
     */
    public static void putSElement(SElement sE) {
        SElement.sElementList.put(sE.getValue(), sE);
    }

    /**
     * 
     * @param str
     * @return Undefined         */
    public static boolean exists(String str) {
        return SElement.sElementList.containsKey(str);
    }

    /**
     * 
     * @param str
     * @return Undefined         */
    public static SElement get(String str) {
        SElement sE = null;
        if (SElement.exists(str)) {
            sE = SElement.sElementList.get(str);
        } else {
            sE = new SElement(str);
            SElement.putSElement(sE);
        }
        return sE;
    }

    /*
     * public String getTemp() { return temp; }
     * 
     * public void setTemp(String temp) { this.temp = temp; }
     */
    @Override
    public Object clone() {
        try {
            SElement cloned = (SElement) super.clone();
            return cloned;
        } catch (Exception ex) {
            return null;
        }
    }
}
