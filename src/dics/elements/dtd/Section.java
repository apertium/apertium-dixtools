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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import dictools.utils.DicOpts;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class Section extends DixElement {

    
    public ArrayList<E> elements = new ArrayList<E>();

    public String id;
    
    public String type;
    
    public Section() {
        super("section");
        //includes = new ArrayList<String>();
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
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    public void printXML(Appendable dos, DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        dos.append(prependCharacterData);
        String attributes = "";
        if (id != null) {
            attributes += " id=\"" + id + "\"";
        }
        if (type != null) {
            attributes += " type=\"" + type + "\"";
        }

        dos.append(indent(1,opt) + "<" + TAGNAME + "" + attributes + ">\n");

        if (elements != null) {
            for (E e : elements) {
                e.printXML(dos, opt);
            }
        }

        dos.append(indent(1,opt) + "</" + TAGNAME + ">"+appendCharacterData.trim()+"\n");
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
            for (E e : elements) {
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
}
