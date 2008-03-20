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

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class ParElement extends Element {

    /**
     * 
     */
    private String n;
    /**
     * 
     */
    private String sa;

    /**
     * 
     * 
     */
    public ParElement() {
        setTagName("par");

    }

    /**
     * 
     * @param value
     */
    public ParElement(final String value) {
        setTagName("par");
        n = value;
    }

    /**
     * 
     * @param pE
     */
    public ParElement(final ParElement pE) {
        n = new String(pE.getValue());
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
     * @return Undefined         */
    @Override
    public final String getValue() {
        return n;
    }

    /**
     * 
     * @param sa
     */
    public final void setSa(final String sa) {
        this.sa = sa;
    }

    /**
     * 
     * @return 'sa' attribute
     */
    public final String getSa() {
        return this.sa;
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    public final void printXML(final OutputStreamWriter dos) throws IOException {
        String saAttr = "";
        if (comments == null) {
            comments = "";
        }
        if (this.sa != null) {
            saAttr = " sa=\"" + sa + "\" ";
        }
        dos.write(tab(4) + "<" + getTagName() + " n=\"" + n + "\" " + saAttr + "/> " + getComments() + "\n");
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    public void printXML1Line(final OutputStreamWriter dos) throws IOException {
        String saAttr = "";
        if (comments == null) {
            comments = "";
        }
        if (this.sa != null) {
            saAttr = " sa=\"" + sa + "\" ";
        }
        dos.write("<" + getTagName() + " n=\"" + n + "\" " + saAttr + "/> " + getComments());
    }

    /**
     * 
     */
    @Override
    public final String toString() {
        return "<" + getTagName() + " n=\"" + n + "\"/> ";
    }
}
