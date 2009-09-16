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

import dics.elements.utils.DicOpts;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class V extends DixElement {

    
    private String name;

    
    public V() {
        super("v");
    }

    /**
     * 
     * @param value
     */
    public V(String value) {
        this();
        name = value;
    }

    /**
     * 
     * @return Undefined         */
    @Override
    public String getValue() {
        return name;
    }

    /**
     * 
     * @param value
     */
    @Override
    public void setValue(String value) {
        name = value;
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    public void printXML(Appendable dos, DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        dos.append(prependCharacterData);
        dos.append("<" + TAGNAME + " n=\"" + getValue() + "\"/>");
        dos.append(appendCharacterData);
    }

    
    @Override
    public String toString() {
        return "<" + getValue() + ">";
    }

    /**
     * 
     * @param vE
     * @return Undefined
    public boolean equals(V vE) {
        return (getValue().equals(vE.getValue()));
    }
     */
}
