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
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class AlphabetElement extends Element {

    /**
     * 
     */
    private String alphabet;

    /**
     * 
     * 
     */
    public AlphabetElement() {

    }

    /**
     * 
     * @param value
     */
    public AlphabetElement(final String value) {
        alphabet = value;
    }

    /**
     * 
     * @param value
     */
    public final void setAlphabet(final String value) {
        alphabet = value;
    }

    /**
     * 
     * @return Undefined         */
    public final String getAlphabet() {
        return alphabet;
    }

    /**
     * 
     * @return Undefined         */
    public final boolean isEmpty() {
        if (getAlphabet() == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    public final void printXML(final OutputStreamWriter dos) throws IOException {
        if (isEmpty()) {
            setAlphabet("");
        }
        dos.write(tab(1) + "<alphabet>" + getAlphabet() + "</alphabet>\n");
    }


    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    public final void printXML(final Writer dos) throws IOException {
        // write blank lines and comments from original file
        dos.write(prependCharacterData);
        if (isEmpty()) {
            setAlphabet("");
        }
        dos.write(tab(1) + "<alphabet>" + getAlphabet() + "</alphabet>\n");
    }
}
