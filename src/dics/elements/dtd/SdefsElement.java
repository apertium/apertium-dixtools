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
import java.util.HashMap;

import dictools.DicTools;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class SdefsElement extends Element {

    /**
     * 
     */
    private ArrayList<SdefElement> sdefsElements;

    /**
     * 
     * 
     */
    public SdefsElement() {
        setTagName("sdefs");
        sdefsElements = new ArrayList<SdefElement>();
    }

    /**
     * 
     * @param value
     */
    public final void addSdefElement(final SdefElement value) {
        setTagName("sdefs");
        sdefsElements.add(value);
    }

    /**
     * 
     * @return Undefined         */
    public final ArrayList<SdefElement> getSdefsElements() {
        return sdefsElements;
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    public final void printXML(final Appendable dos, final DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        dos.append(prependCharacterData);
        dos.append(makeCommentIfData(processingComments));
        dos.append(tab(1) + "<" + getTagName() + ">\n");

        final HashMap<String, String> descriptions = DicTools.getSdefDescriptions();
        for (final SdefElement e : sdefsElements) {
            final String d = descriptions.get(e.getValue());
            if (d != null) {
            // e.setProcessingComments("\t<!-- " + d + "-->");
            }
            e.printXML(dos, opt);
        }
        dos.append(tab(1) + "</" + getTagName() + ">\n");
        /*
        if (processingComments != null  && !processingComments.isEmpty()) {
            dos.append(tab(1) + "<!-- \n");
            dos.append(tab(1) + getProcessingComments());
            dos.append(tab(1) + " -->\n");
        }*/
        dos.append(appendCharacterData.trim());
    }

    /**
     * 
     * @param fileName
     */
    public final void printXML(final String fileName, DicOpts opt) {
        this.printXML(fileName, "UTF-8", opt);
    }

    /**
     * 
     * @param fileName
     * @param encoding
     */
    public void printXML(final String fileName, final String encoding, DicOpts opt) {
        BufferedOutputStream bos;
        FileOutputStream fos;
        OutputStreamWriter dos;

        try {
            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, encoding);
            dos.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
            dos.append("<dictionary>\n");
            printXML(dos, DicOpts.std);
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
     * 
     * @return Undefined         */
    public final ArrayList<String> getAllCategories() {
        final ArrayList<String> categories = new ArrayList<String>();

        for (final SdefElement sdef : getSdefsElements()) {
            categories.add(sdef.getValue());
        }
        return categories;
    }

    /**
     * 
     */
    @Override
    public final String toString() {
        String str = "";
        for (final SdefElement sdef : getSdefsElements()) {
            str += sdef.toString();
        }
        return str;
    }
}
