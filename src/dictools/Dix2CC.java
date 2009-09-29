/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Copyright (C) 2008 Enrique Benimeli Bofarull
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your soption) any later version.
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
package dictools;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.S;
import dics.elements.dtd.Section;
import dictools.utils.DictionaryReader;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Dix2CC {

    
    private Dictionary dic;
    
    public String bilFileName;
    
    public String sltlFull;
    
    public String[] arguments;
    
    public String outFileName;
    
    private TinyFilter tinyFilter;

    
    public Dix2CC() {
    }

    public Dix2CC(TinyFilter tinyFilter) {
        this.tinyFilter = tinyFilter;
    }

    
    public void do_convert() {
        this.processArguments();
        this.processDic();
        System.err.println("Done");
    }

    /**
     * 
     * @param dic
     */
    private void processDic() {
        System.err.println("Building " + this.outFileName + " for Palm...");
        Vector<String> lines = new Vector<String>();

        for (Section section : dic.sections) {
            for (E ee : section.elements) {
                StringBuffer sb = new StringBuffer();
                if (ee.is_LR_or_LRRL() && !ee.containsRegexpr() && this.validLemma(ee.getValueNoTags("L")) && this.validLemma(ee.getValueNoTags("R"))) {
                    String left = ee.getValueNoTags("L");
                    left = tinyFilter.applyToLemma(left);
                    sb.append(left + " ");

                    for (S sE : ee.getSymbols("L")) {
                        if (this.tinyFilter.preserve(sE.getValue())) {
                            String tagName = this.tinyFilter.rename(sE.getValue());
                            sb.append("{" + tagName + "} ");
                        }
                    }

                    sb.append(":: ");
                    String right = ee.getValueNoTags("R");

                    right = this.tinyFilter.applyToLemma(right);
                    sb.append(right + " ");
                    for (S sE : ee.getSymbols("R")) {
                        if (tinyFilter.preserve(sE.getValue())) {
                            String tagName = this.tinyFilter.rename(sE.getValue());
                            sb.append("{" + tagName + "} ");
                        }
                    }
                    sb.append("\n");
                    lines.add(sb.toString());
                }
            }
        }

        this.print(lines);

    }

    private final boolean validLemma(String lm) {
        if(lm.equals("-"))
            return false;
        if(lm.equals("'"))
            return false;
        if(lm.equals(""))
            return false;
        if(lm.equals(","))
            return false;
        return true;
    }

    /**
     * 
     * @param vector
     */
    private void print(Vector<String> lines) {
        try {
            BufferedOutputStream bos;
            FileOutputStream fos;
            OutputStreamWriter dos = null;

            fos = new FileOutputStream(outFileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, "ISO-8859-1");

            for (String line : lines) {
                dos.append(line);
            }
            dos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    
    private void processArguments() {
        if (arguments != null) {
            String fileName = this.arguments[1];
            this.bilFileName = fileName;
            this.outFileName = this.arguments[2];
        }
        if (this.bilFileName != null) {
            DictionaryReader dicReader = new DictionaryReader(this.bilFileName);
            System.err.println("Processing bilingual dictionary: " + this.bilFileName);
            dic = dicReader.readDic();

            if (this.tinyFilter != null) {
                dic = tinyFilter.doFilter(dic);
            }
        }


    //dic.reverse();
    }
}

