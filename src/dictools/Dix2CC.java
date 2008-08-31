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

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.SElement;
import dics.elements.dtd.SectionElement;
import dics.elements.utils.SElementList;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Dix2CC {

    /**
     * 
     */
    private DictionaryElement dic;
    /**
     * 
     */
    private String[] arguments;
    /**
     * 
     */
    private String outFileName;

    /**
     * 
     */
    public Dix2CC() {


    }

    /**
     * 
     */
    public final void do_convert() {
        this.processArguments();
        this.processDic();

    }

    /**
     * 
     * @param dic
     */
    private final void processDic() {
        Vector<String> lines = new Vector<String>();

        for (SectionElement section : dic.getSections()) {
            for (EElement ee : section.getEElements()) {
                StringBuffer sb = new StringBuffer();
                if (ee.is_LR_or_LRRL()) {
                    String left = ee.getValueNoTags("L");
                    sb.append(left + " ");
                    SElementList leftS = ee.getSElements("L");
                    for (SElement sE : leftS) {
                        sb.append("{" + sE.getValue() + "} ");
                    }

                    sb.append(":: ");
                    String right = ee.getValueNoTags("R");
                    sb.append(right + " ");
                    SElementList rightS = ee.getSElements("R");
                    for (SElement sE : rightS) {
                        sb.append("{" + sE.getValue() + "} ");
                    }
                    sb.append("\n");
                    lines.add(sb.toString());
                }
            }
        }

        this.print(lines);

    }

    /**
     * 
     * @param vector
     */
    private final void print(Vector<String> lines) {
        try {
            BufferedOutputStream bos;
            FileOutputStream fos;
            OutputStreamWriter dos = null;

            fos = new FileOutputStream(outFileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, "UTF-8");

            for (String line : lines) {
                dos.write(line);

            }
        } catch (IOException ioe) {

        }
    }

    /**
     * 
     */
    private void processArguments() {
        String fileName = this.arguments[1];
        DictionaryReader dicReader = new DictionaryReader(fileName);
        dic = dicReader.readDic();
        this.outFileName = this.arguments[2];
    //dic.reverse();
    }

    /**
     * 
     * @return the  argumentss
     */
    public String[] getArguments() {
        return arguments;
    }

    /**
     * 
     * @param arguments
     */
    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    /**
     * 
     * @return Out file name
     */
    public String getOutFileName() {
        return outFileName;
    }

    /**
     * 
     * @param outFileName
     */
    public void setOutFileName(String outFileName) {
        this.outFileName = outFileName;
    }
}

