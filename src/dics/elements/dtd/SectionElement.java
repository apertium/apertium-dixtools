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
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import dics.elements.utils.EElementList;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class SectionElement extends Element {

    /**
     * 
     */
    private String id;
    /**
     * 
     */
    private String type;
    /**
     * 
     */
    private EElementList eElements;
    /**
     * 
     */
    protected ArrayList<String> includes;

    /**
     * 
     * 
     */
    public SectionElement() {
        setTagName("section");
        eElements = new EElementList();
        includes = new ArrayList<String>();
    }

    /**
     * 
     * @param id
     * @param type
     */
    public SectionElement(final String id, final String type) {
        setTagName("section");
        eElements = new EElementList();
        includes = new ArrayList<String>();
        this.id = id;
        this.type = type;
    }

    /**
     * 
     * @param value
     */
    public final void setID(final String value) {
        id = value;
    }

    /**
     * 
     * @return Undefined         */
    public String getID() {
        return id;
    }

    /**
     * 
     * @param value
     */
    public final void setType(final String value) {
        type = value;
    }

    /**
     * 
     * @return Undefined         */
    public String getType() {
        return type;
    }

    /**
     * 
     * @return Undefined         */
    public EElementList getEElements() {
        return eElements;
    }

    /**
     * 
     * @param value
     */
    public void addEElement(final EElement value) {
        eElements.add(value);
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    public final void printXML(final Appendable dos, final DicOpts opt) throws IOException {
        // write blank lines and comments from original file
        dos.append(prependCharacterData);
        String attributes = "";
        if (getID() != null) {
            attributes += " id=\"" + getID() + "\"";
        }
        if (getType() != null) {
            attributes += " type=\"" + getType() + "\"";
        }

        dos.append(tab(1) + "<" + getTagName() + "" + attributes + ">\n");

        if (eElements != null) {
            for (final EElement e : eElements) {
                e.printXML(dos, opt);
            }
        }

        dos.append(tab(1) + "</" + getTagName() + ">\n");
    }

    /**
     * 
     * @param fileName
     */
    public void printXMLXInclude(final String fileName, DicOpts opt) {
        this.printXMLXInclude(fileName, "UTF-8", opt);
    }

    /**
     * 
     * @param fileName
     * @param encoding
     */
    public void printXMLXInclude(final String fileName, final String encoding, DicOpts opt) {
        BufferedOutputStream bos;
        FileOutputStream fos;
        OutputStreamWriter dos;

        try {
            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, encoding);
            dos.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");

            dos.append("<dictionary>\n");
            dos.append("<section>\n");
            for (final EElement e : eElements) {
                e.printXML(dos, opt);
            }
            dos.append("</section>\n");
            dos.append("</dictionary>\n");

            fos = null;
            bos = null;
            dos.close();
            dos = null;
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final Exception eg) {
            eg.printStackTrace();
        }
    }

    /**
     * @param elements
     *                the eElements to set
     */
    public final void setEElements(EElementList elements) {
        eElements = elements;
    }

    /**
     * @param includes
     *                the includes to set
     */
    public final void setIncludes(ArrayList<String> includes) {
        this.includes = includes;
    }

    /**
     * 
     * @param xinclude
     */
    public final void addXInclude(String xinclude) {
        includes.add(xinclude);
    }

    /**
     * @return the includes
     */
    public final ArrayList<String> getIncludes() {
        return includes;
    }
}
