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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class Pardefs extends DixElement {

    /**
     * 
     */
    private ArrayList<Pardef> pardefElements;

    /**
     * 
     * 
     */
    public Pardefs() {
        pardefElements = new ArrayList<Pardef>();
    }

    /**
     * 
     * @param value
     */
    public void addPardefElement(Pardef value) {
        pardefElements.add(value);
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
        dos.append(tab(1) + "<pardefs>\n");
        for (Pardef e : pardefElements) {
            e.printXML(dos, opt);
        }
        dos.append(tab(1) + "</pardefs>"+appendCharacterData.trim()+"\n\n");
    }

    /**
     * 
     * @param fileName
     */
    public void printXML(String fileName, DicOpts opt) {
        this.printXML(fileName, "UTF-8", opt);
    }

    /**
     * 
     * @param fileName
     * @param encoding
     */
    public void printXML(String fileName, String encoding, DicOpts opt) {
        BufferedOutputStream bos;
        FileOutputStream fos;
        OutputStreamWriter dos;

        try {
            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, encoding);
            dos.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
            dos.append("<dictionary>\n");
            printXML(dos, opt);
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
     * 
     * @param parName
     * @return Undefined         */
    public Pardef getParadigmDefinition(String parName) {
        for (Pardef pardefE : pardefElements) {
            if (pardefE.getName().equals(parName)) {
                return pardefE;
            }
        }
        return null;
    }

    /**
     * 
     * @return Undefined         */
    public ArrayList<Pardef> getPardefElements() {
        return pardefElements;
    }

    /**
     * 
     * @param par
     */
    public void remove(Pardef par) {
        getPardefElements().remove(par);
    }
}
