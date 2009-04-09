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
package misc.enes;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.IElement;
import dics.elements.dtd.LElement;
import dics.elements.dtd.PElement;
import dics.elements.dtd.RElement;
import dics.elements.dtd.SElement;
import dics.elements.dtd.TextElement;
import dics.elements.utils.SElementList;
import dictools.xml.DictionaryReader;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class CompleteTranslation {

    /**
     * 
     */
    private DictionaryElement bil;
    /**
     * 
     */
    private HashMap<String, String> translations;

    /**
     * 
     * @param dicFileName
     * @param sourceFileName
     * @param translationFileName
     */
    public CompleteTranslation(String dicFileName, String sourceFileName, String translationFileName) {
        translations = this.readTranslations(sourceFileName, translationFileName);
        DictionaryReader dicReader = new DictionaryReader(dicFileName);
        bil = dicReader.readDic();
    }

    /**
     * 
     */
    public void complete() {
        for (EElement ee : bil.getAllEntries()) {
            LElement left = ee.getLeft();
            if (left != null) {
                System.out.println("Completing " + left.getValueNoTags());
                SElementList slist = left.getSElements();
                String source = left.getValueNoTags();
                if (source != null) {
                    String translation = translations.get(source);
                    if (translation != null) {
                        RElement rE = new RElement();
                        TextElement tE = new TextElement(translation);
                        rE.addChild(tE);
                        for (SElement sE : slist) {
                            rE.addChild(sE);
                        }
                        if (ee.getP() != null) {
                            ee.getP().setRElement(rE);
                        } else {
                            PElement pE = new PElement();
                            pE.setRElement(rE);
                            pE.setRElement(ee.getRight());
                            IElement iE = ee.getI();
                            ee.getChildren().remove(iE);
                            ee.addChild(pE);
                        }
                    }
                }
            }

        }
        bil.printXML("trans-completed-2.dix", dics.elements.utils.DicOpts.STD);
    }

    /**
     * 
     * @param sfn
     * @param tfn
     * @return
     */
    private HashMap<String, String> readTranslations(String sfn, String tfn) {
        HashMap<String, String> map = new HashMap<String, String>();
        ArrayList<String> srcList = this.buildList(sfn);
        ArrayList<String> transList = this.buildList(tfn);

        for (int i = 0; i < srcList.size(); i++) {
            String src = srcList.get(i);
            String trans = transList.get(i);
            map.put(src, trans);
            System.out.println(src + " --> " + trans);
        }
        return map;
    }

    /**
     * 
     * @param fileName
     * @return
     */
    private ArrayList<String> buildList(String fileName) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StreamTokenizer st = new StreamTokenizer(br);
            st.quoteChar('.');

            String strLine;

            while ((strLine = br.readLine()) != null) {
                if (!strLine.equals("")) {
                    //System.err.println("'" + strLine + "'");
                    list.add(strLine);
                }

            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return list;

    }
}
