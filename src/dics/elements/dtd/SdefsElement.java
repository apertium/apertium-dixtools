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

import dics.elements.utils.DicTools;
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
    public void addSdefElement(SdefElement value) {
        setTagName("sdefs");
        sdefsElements.add(value);
    }

    /**
     * 
     * @return Undefined         */
    public ArrayList<SdefElement> getSdefsElements() {
        return sdefsElements;
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    public void printXML(Appendable dos, DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        dos.append(prependCharacterData);
        dos.append(makeCommentIfData(processingComments));
        dos.append(tab(1) + "<" + getTagName() + ">\n");

        HashMap<String, String> descriptions = DicTools.getSdefDescriptions();
        for (SdefElement e : sdefsElements) {
            String d = descriptions.get(e.getValue());
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
            printXML(dos, DicOpts.STD);
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
     * @return Undefined         */
    public ArrayList<String> getAllCategories() {
        ArrayList<String> categories = new ArrayList<String>();

        for (SdefElement sdef : getSdefsElements()) {
            categories.add(sdef.getValue());
        }
        return categories;
    }

    /**
     * 
     */
    @Override
    public String toString() {
        String str = "";
        for (SdefElement sdef : getSdefsElements()) {
            str += sdef.toString();
        }
        return str;
    }
}
