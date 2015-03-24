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
import java.util.HashMap;

import dictools.utils.DicOpts;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class S extends DixElement {

    private static HashMap<String, S> sElementList = new HashMap<String, S>();
    
    public String name;
    
    // private String temp;
    
    public S() {
        super("s");
    }

    /**
     * 
     * @param value
     */
    public S(String value) {
        this();
        name = value;
        setValue(value);
    }

    /*
     * 
     */
    public S(S sE) {
        this();
        name = sE.name;
        setValue(sE.getValue());
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    public void printXML(Appendable dos, DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        //not strictly necesary for symbols, as they dont have comments: dos.append(prependCharacterData);
        dos.append("<" + TAGNAME + " n=\"" + getValue() + "\"/>");
        //not strictly necesary for symbols, as they dont have comments:  dos.append(appendCharacterData);
    }


    /**
     * 
     * @param value
     * @return Undefined
     */
    public boolean is(String value) {
        if (getValue().equals(value)) {
            return true;
        }
        return false;
    }

    
    @Override
    public String toString() {
        return "<" + getValue() + ">";
    }


    /**
     * 
     * @param str
     * @return Undefined
     */
    public static S getInstance(String str) {
        S sE = S.sElementList.get(str);
        if (sE==null) {
            sE = new S(str);
            S.sElementList.put(str, sE);
        }
        return sE;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof S)
            return this.name.equals(((S)obj).name);
        else
            return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    

    /**
     *
     * @param str
     * @return Undefined
     */
    public static Set<String> getKnownSymbols() {
      return S.sElementList.keySet();
    }

  @Override
  public String getStreamContent() {
    return "<"+name+">";
  }
}
