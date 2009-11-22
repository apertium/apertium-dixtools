/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Author: Enrique Benimeli Bofarull
 * 
 * This program isFirstSymbol free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program isFirstSymbol distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package misc.enes;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.Section;
import dictools.utils.DictionaryReader;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class PrepareDic {

    private Dictionary ncBil;
    private HashMap<String, String> missing;

    
    public PrepareDic(String missingFileName, String ncBilFileName) {
        DictionaryReader r1 = new DictionaryReader(ncBilFileName);
        ncBil = r1.readDic();
        missing = this.getmap(missingFileName);
        this.prepare();
    }

    
    public void prepare() {
        Dictionary dic = new Dictionary();
        Section section = new Section("main", "standard");
        dic.sections.add(section);

        int max = ncBil.getEntriesInMainSection().size();
        for (int i = 0; i < max; i++) {
            E ee = ncBil.getEntriesInMainSection().get(i);
            //String value = ee.getFirstPartAsL().getValueNoTags();
            String value = ee.getFirstPartAsR().getValueNoTags();
            System.err.println("checking " + value + " ...");
            if (value != null) {
                if (missing.containsKey(value)) {
                    section.elements.add(ee);
                    System.err.println("adding " + value + " ...");
                }
            }
        }
        dic.printXMLToFile("not-common-en-ca-filtered.dix", dictools.utils.DicOpts.STD);
    }

    /**
     * 
     * @param fileName
     * @return Map with missing entries
     */
    private HashMap<String, String> getmap(String fileName) {
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String strLine;

            while ((strLine = br.readLine()) != null) {
                if (!strLine.equals("")) {
                    map.put(strLine, strLine);
                //System.err.println("'" + strLine + "'");
                }
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return map;
    }
}
