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

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class VElement extends Element {

    /**
     * 
     */
    private String n;

    /**
     * 
     * 
     */
    public VElement() {
        setTagName("v");
    }

    /**
     * 
     * @param value
     */
    public VElement(final String value) {
        setTagName("v");
        n = value;
    }

    /*
     * 
     */
    public VElement(final VElement vE) {
        setTagName("v");
        n = vE.getValue();
    }

    /**
     * 
     * @return Undefined         */
    @Override
    public final String getValue() {
        return n;
    }

    /**
     * 
     * @param value
     */
    @Override
    public final void setValue(final String value) {
        n = value;
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    public final void printXML(final Appendable dos, final DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        dos.append(prependCharacterData);
        dos.append("<" + getTagName() + " n=\"" + getValue() + "\"/>");
        dos.append(appendCharacterData);
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
     * @param vE
     * @return Undefined
     */
    public final boolean equals(final VElement vE) {
        return (getValue().equals(vE.getValue()));
    }

    /**
     * 
     * @return Object
     */
    @Override
    public Object clone() {
        try {
            final VElement cloned = (VElement) super.clone();
            return cloned;
        } catch (final Exception ex) {
            return null;
        }
    }
}
