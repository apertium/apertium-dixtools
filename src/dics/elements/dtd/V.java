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

import dictools.utils.DicOpts;

/**
<t/> and <v/> are only in crossdix
t = template, v = variable
t matches any single tag, v is like + in regexes (0 or more)

 * http://wiki.apertium.org/wiki/List_of_symbols#XML_tags
 * @author Enrique Benimeli Bofarull
 * 
 */
public class V extends DixElement {

    public V() {
        super("v");
    }


    public V(String value) {
        super("v");
        setValue(value);
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
