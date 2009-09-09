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
package misc.esca;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.S;
import dictools.xml.DictionaryReader;
import java.util.HashMap;

/**
 *
 * @author ebenimeli
 */
public class AddSameGender {

    /**
     * 
     */
    private String[] arguments;
    /**
     * 
     */
    private String morphDic;
    /**
     * 
     */
    private String bilDic;
    /**
     * 
     */
    private String outFileName;
    /**
     * 
     */
    private HashMap<String, Pardef> pardefsNouns;
    /**
     * 
     */
    private HashMap<String, Pardef> mfPardefsAdjs;
    private Dictionary bil;

    /**
     * 
     * @param morphDic
     * @param bilDic
     */
    public AddSameGender(String morphDic, String bilDic) {
        this.morphDic = morphDic;
        this.bilDic = bilDic;

    }

    /**
     * 
     */
    public void processArguments() {
        morphDic = arguments[1];
        bilDic = arguments[2];
    }

    /**
     * 
     * @param outFileName
     */
    public void setOutFileName(String outFileName) {
        this.outFileName = outFileName;
    }

    /**
     * 
     */
    public void addSameGender() {
        DictionaryReader bilReader = new DictionaryReader(this.bilDic);
        bil = bilReader.readDic();
        this.process_nouns();
        this.process_adjs();
        bil.printXML("dics/apertium-es-ca.es-ca-with-gender.dix", dics.elements.utils.DicOpts.STD);
    }

    /**
     * 
     */
    private void process_nouns() {
        DictionaryReader morphReader = new DictionaryReader(this.morphDic);
        Dictionary morph = morphReader.readDic();

        HashMap<String, String> parNameGender = new HashMap<String, String>();

        for (Pardef pfe : morph.getPardefsElement().getPardefElements()) {
            if (pfe.hasCategory("n")) {
                if ((pfe.contains("m") && pfe.contains("f"))) {
                    parNameGender.put(pfe.getName(), "GD");
                //parNameGender.put(pfe.getName(), "mf");
                } else {
                    if (pfe.contains("mf")) {
                        parNameGender.put(pfe.getName(), "mf");
                    }
                    if (pfe.contains("m")) {
                        parNameGender.put(pfe.getName(), "m");
                    }
                    if (pfe.contains("f")) {
                        parNameGender.put(pfe.getName(), "f");
                    }
                }
            }
        }

        HashMap<String, String> lemmaParName = new HashMap<String, String>();
        for (E ee : morph.getAllEntries()) {
            String lemma = ee.getLemma();
            String parName = ee.getMainParadigmName();
            lemmaParName.put(lemma, parName);
        }


        for (E ee : bil.getAllEntries()) {
            if (ee.is("L", "n")) {
                String value = ee.getLeft().getValueNoTags();
                if (ee.getLeft().contains("GD") || ee.getLeft().contains("m") || ee.getLeft().contains("f") || ee.getLeft().contains("mf")) {

                } else {
                    String parValue = lemmaParName.get(value);
                    if (parValue != null) {
                        //System.out.println("parValue: " +  parValue);
                        String genderValue = parNameGender.get(parValue);
                        if (genderValue != null) {
                            System.out.println(value + " is '" + genderValue + "'");
                            ee.getChildren("L").add(new S(genderValue));
                        }
                    }
                }
            }
        }



    }

    private void process_adjs() {
        DictionaryReader morphReader = new DictionaryReader(this.morphDic);
        Dictionary morph = morphReader.readDic();

        HashMap<String, String> parNameGender = new HashMap<String, String>();

        for (Pardef pfe : morph.getPardefsElement().getPardefElements()) {
            if (pfe.hasCategory("adj")) {
                if (pfe.contains("mf")) {
                    parNameGender.put(pfe.getName(), "mf");
                }
            }
        }


        HashMap<String, String> lemmaParName = new HashMap<String, String>();
        for (E ee : morph.getAllEntries()) {
            String lemma = ee.getLemma();
            String parName = ee.getMainParadigmName();
            lemmaParName.put(lemma, parName);
        }


        for (E ee : bil.getAllEntries()) {
            if (ee.is("L", "adj")) {
                String value = ee.getLeft().getValueNoTags();
                if (ee.getLeft().contains("GD") || ee.getLeft().contains("m") || ee.getLeft().contains("f") || ee.getLeft().contains("mf")) {

                } else {
                    String parValue = lemmaParName.get(value);
                    if (parValue != null) {
                        //System.out.println("parValue: " +  parValue);
                        String genderValue = parNameGender.get(parValue);
                        if (genderValue != null) {
                            System.out.println(value + " is '" + genderValue + "'");
                            ee.getChildren("L").add(new S(genderValue));
                        }
                    }
                }
            }
        }
    }
}
