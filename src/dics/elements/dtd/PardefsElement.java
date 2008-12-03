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
import java.io.Writer;
import java.util.ArrayList;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class PardefsElement extends Element {

    /**
     * 
     */
    private ArrayList<PardefElement> pardefElements;

    /**
     * 
     * 
     */
    public PardefsElement() {
        pardefElements = new ArrayList<PardefElement>();
    }

    /**
     * 
     * @param value
     */
    public final void addPardefElement(final PardefElement value) {
        pardefElements.add(value);
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    public final void printXML(final Writer dos) throws IOException {
        // write blank lines and comments from original file
        dos.write(prependCharacterData);
        dos.write(tab(1) + "<pardefs>\n");
        for (final PardefElement e : pardefElements) {
            e.printXML(dos);
        }
        dos.write(tab(1) + "</pardefs>\n\n");
    }

    /**
     * 
     * @param fileName
     */
    public final void printXML(final String fileName) {
        this.printXML(fileName, "UTF-8");
    }

    /**
     * 
     * @param fileName
     * @param encoding
     */
    public void printXML(final String fileName, final String encoding) {
        BufferedOutputStream bos;
        FileOutputStream fos;
        OutputStreamWriter dos;

        try {
            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, encoding);
            dos.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
            dos.write("<dictionary>\n");
            printXML(dos);
            dos.write("</dictionary>\n");

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
     * 
     * @param parName
     * @return Undefined         */
    public final PardefElement getParadigmDefinition(final String parName) {
        for (final PardefElement pardefE : pardefElements) {
            if (pardefE.getName().equals(parName)) {
                return pardefE;
            }
        }
        return null;
    }

    /**
     * 
     * @return Undefined         */
    public ArrayList<PardefElement> getPardefElements() {
        return pardefElements;
    }

    /**
     * 
     * @param par
     */
    public final void remove(PardefElement par) {
        getPardefElements().remove(par);
    }
}
