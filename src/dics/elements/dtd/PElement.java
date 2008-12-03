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
import java.io.StringWriter;
import java.io.Writer;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class PElement extends Element {

    /**
     * 
     */
    private LElement l;
    /**
     * 
     */
    private RElement r;

    /**
     * 
     * 
     */
    public PElement() {
        setTagName("p");
    }

    /**
     * 
     * @param pE
     */
    public PElement(final PElement pE) {
        setTagName("p");
        // l = new LElement(pE.getL());
        l = (LElement) pE.getL().clone();
        // r = new RElement(pE.getR());
        r = (RElement) pE.getR().clone();
    }

    /**
     * 
     * @param l
     * @param r
     */
    public PElement(final LElement l, final RElement r) {
        setTagName("p");
        this.l = l;
        this.r = r;
    }

    /**
     * 
     * @param l
     */
    public final void setLElement(final LElement l) {
        this.l = l;
    }

    /**
     * 
     * @param r
     */
    public final void setRElement(final RElement r) {
        this.r = r;
    }

    /**
     * 
     * @return Undefined         */
    public final LElement getL() {
        return l;
    }

    /**
     * 
     * @return Undefined         */
    public final RElement getR() {
        return r;
    }

    /**
     * 
     * @param value
     * @param side
     */
    public final void setComments(final String value, final String side) {
        if (side.equals("L")) {
            l.setComments(value);
        }
        if (side.equals("R")) {
            r.setComments(value);
        }
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
        dos.write(tab(3) + "<" + getTagName() + ">\n");
        if (l != null) {
            l.printXML(dos);
        }
        if (r != null) {
            r.printXML(dos);
        }
        dos.write(tab(3) + "</" + getTagName() + ">\n");
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    public final void printXML1Line(final Writer dos)
            throws IOException {
        dos.write("<" + getTagName() + ">");
        l.printXML1Line(dos);
        r.printXML1Line(dos);
        dos.write("</" + getTagName() + ">");
    }
    

    private final static String spaces = "                      ";
    
    public final void printXML1LineAligned(final StringWriter dos, int alignR)
            throws IOException {
        dos.write("<" + getTagName() + ">");

        l.printXML1Line(dos);

        int neededSpaces = alignR - dos.getBuffer().length();
        if (neededSpaces>0) {
          dos.write(spaces.substring(0, Math.min(spaces.length(), neededSpaces)));
        }
        r.printXML1Line(dos);
        dos.write("</" + getTagName() + ">");
    }


    /**
 ** 
 ** @param dos
 ** @throws java.io.IOException
 **/
    @Override
    public final void printXML(final OutputStreamWriter dos) throws IOException {
        dos.write(tab(3) + "<" + getTagName() + ">\n");
        if (l != null) {
            l.printXML(dos);
        }
        if (r != null) {
            r.printXML(dos);
        }
        dos.write(tab(3) + "</" + getTagName() + ">\n");
    }

    /**
 ** 
 ** @param dos
 ** @throws java.io.IOException
 **/
    @Override
    public final void printXML1Line(final OutputStreamWriter dos)
            throws IOException {
        dos.write("<" + getTagName() + ">");
        l.printXML1Line(dos);
        r.printXML1Line(dos);
        dos.write("</" + getTagName() + ">");
    }


        
        
    /**
     * 
     */
    @Override
    public final String toString() {
        String str = "";

        str += getL().toString();
        str += getR().toString();

        return str;
    }
}
