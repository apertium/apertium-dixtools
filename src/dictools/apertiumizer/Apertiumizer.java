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
package dictools.apertiumizer;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.LElement;
import dics.elements.dtd.PElement;
import dics.elements.dtd.RElement;
import dics.elements.dtd.SElement;
import dics.elements.dtd.SectionElement;
import dics.elements.dtd.TextElement;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Apertiumizer {

    /**
     * 
     */
    private String fileName;
    /**
     * 
     */
    private String outFileName;

    /**
     * 
     * @param fileName
     */
    public Apertiumizer(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * 
     */
    public final void apertiumize() {
        this.readFormat(0);
    }

    /**
     * 
     * @param format
     */
    private final void readFormat(int format) {
        try {
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(fileName);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String strLine;

            DictionaryElement dic = new DictionaryElement();
            dic.setXmlEncoding("UTF-8");
            SectionElement section = new SectionElement();
            dic.addSection(section);

            while ((strLine = br.readLine()) != null) {
                if (!strLine.startsWith("#")) {
                    EElement e = null;
                    switch (format) {
                        case 0:
                            e = readElementFormat_0(strLine);
                            break;
                    }
                    section.addEElement(e);
                }
            }
            in.close();
            dic.printXML(this.getOutFileName());
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

    }

    /**
     * 
     * @param strLine
     * @return The element
     */
    private final EElement readElementFormat_0(final String strLine) {
        StringTokenizer tokenizer = new StringTokenizer(strLine, ":");
        boolean lastToken = false;
        int i = 0;
        EElement e = new EElement();
        LElement left = new LElement();
        RElement right = new RElement();

        while (i < 3 && !lastToken && tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            switch (i) {
                case 0:
                    left.addChild(new TextElement(token));
                    break;
                case 1:
                    right.addChild(new TextElement(token));
                    break;
                case 2:
                    lastToken = true;
                    SElement sE = new SElement(token);
                    left.addChild(sE);
                    right.addChild(sE);
                    PElement pE = new PElement();
                    pE.setLElement(left);
                    pE.setRElement(right);
                    e.addChild(pE);
                    return e;
            }
            i++;
        }
        return null;
    }

    /**
     * 
     * @param strLine
     * @return The element
     */
    private final EElement readElementFormat_1(final String strLine) {
        StringTokenizer tokenizer = new StringTokenizer(strLine, "\t");
        boolean lastToken = false;
        int i = 0;
        EElement e = new EElement();
        LElement left = new LElement();
        RElement right = new RElement();

        while (i < 3 && !lastToken && tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            switch (i) {
                case 0:
                    left.addChild(new TextElement(token));
                    break;
                case 1:
                    right.addChild(new TextElement(token));
                    break;
                case 2:
                    if (token.endsWith(":")) {
                        lastToken = true;
                        String newString = token.substring(0, token.length() - 1);
                        SElement sE = new SElement(newString);
                        left.addChild(sE);
                        right.addChild(sE);
                        PElement pE = new PElement();
                        pE.setLElement(left);
                        pE.setRElement(right);
                        e.addChild(pE);
                        return e;
                    }
            }
            i++;
        }
        return null;
    }

    /**
     * 
     * @param outFileName
     */
    public final void setOutFileName(final String outFileName) {
        this.outFileName = outFileName;
    }

    /**
     * 
     * @return The output file name
     */
    public final String getOutFileName() {
        return this.outFileName;
    }
}
