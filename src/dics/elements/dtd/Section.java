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

import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class Section extends DixElement {

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
    private ArrayList<E> eElements;
    /**
     * 
     */
    protected ArrayList<String> includes;

    /**
     * 
     * 
     */
    public Section() {
        super("section");
        eElements = new ArrayList<E>();
        includes = new ArrayList<String>();
    }

    /**
     * 
     * @param id
     * @param type
     */
    public Section(String id, String type) {
      this();
        this.id = id;
        this.type = type;
    }

    /**
     * 
     * @param value
     */
    public void setID(String value) {
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
    public void setType(String value) {
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
    public ArrayList<E> getEElements() {
        return eElements;
    }

    /**
     * 
     * @param value
     */
    public void addEElement(E value) {
        eElements.add(value);
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
        String attributes = "";
        if (getID() != null) {
            attributes += " id=\"" + getID() + "\"";
        }
        if (getType() != null) {
            attributes += " type=\"" + getType() + "\"";
        }

        dos.append(tab(1) + "<" + TAGNAME + "" + attributes + ">\n");

        if (eElements != null) {
            for (E e : eElements) {
                e.printXML(dos, opt);
            }
        }

        dos.append(tab(1) + "</" + TAGNAME + ">"+appendCharacterData.trim()+"\n");
    }

    /**
     * 
     * @param fileName
     */
    public void printXMLXInclude(String fileName, DicOpts opt) {
        this.printXMLXInclude(fileName, "UTF-8", opt);
    }

    /**
     * 
     * @param fileName
     * @param encoding
     */
    public void printXMLXInclude(String fileName, String encoding, DicOpts opt) {
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
            for (E e : eElements) {
                e.printXML(dos, opt);
            }
            dos.append("</section>\n");
            dos.append("</dictionary>\n");

            fos = null;
            bos = null;
            dos.close();
            dos = null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception eg) {
            eg.printStackTrace();
        }
    }

    /**
     * @param elements
     *                the eElements to set
     */
    public void setEElements(ArrayList<E> elements) {
        eElements = elements;
    }

    /**
     * @param includes
     *                the includes to set
     */
    public void setIncludes(ArrayList<String> includes) {
        this.includes = includes;
    }

    /**
     * 
     * @param xinclude
     */
    public void addXInclude(String xinclude) {
        includes.add(xinclude);
    }

    /**
     * @return the includes
     */
    public ArrayList<String> getIncludes() {
        return includes;
    }
}
