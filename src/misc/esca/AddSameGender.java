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
package misc.esca;

import java.util.HashMap;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.E;
import dics.elements.dtd.I;
import dics.elements.dtd.P;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.S;
import dictools.utils.DictionaryReader;
import java.util.ArrayList;

/**
 *
 * @author ebenimeli
 */
public class AddSameGender {

    
    private String morphDic;
    
    private String bilDic;
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

    
    public void addSameGender() {
        DictionaryReader bilReader = new DictionaryReader(this.bilDic);
        bil = bilReader.readDic();
        this.process_nouns();
        this.process_adjs();
        bil.printXMLToFile("dics/apertium-es-ca.es-ca-with-gender.dix", dictools.utils.DicOpts.STD);
    }

    
    private void process_nouns() {
        DictionaryReader morphReader = new DictionaryReader(this.morphDic);
        Dictionary morph = morphReader.readDic();

        HashMap<String, String> parNameGender = new HashMap<String, String>();

        for (Pardef pfe : morph.pardefs.elements) {
            if (pfe.hasCategory("n")) {
                if ((pfe.containsSymbol("m") && pfe.containsSymbol("f"))) {
                    parNameGender.put(pfe.name, "GD");
                //parNameGender.put(pfe.getName(), "mf");
                } else {
                    if (pfe.containsSymbol("mf")) {
                        parNameGender.put(pfe.name, "mf");
                    }
                    if (pfe.containsSymbol("m")) {
                        parNameGender.put(pfe.name, "m");
                    }
                    if (pfe.containsSymbol("f")) {
                        parNameGender.put(pfe.name, "f");
                    }
                }
            }
        }

        HashMap<String, String> lemmaParName = new HashMap<String, String>();
        for (E ee : morph.getEntriesInMainSection()) {
            String lemma = ee.lemma;
            String parName = ee.getMainParadigmName();
            lemmaParName.put(lemma, parName);
        }


        for (E ee : bil.getEntriesInMainSection()) {
            if (ee.isFirstSymbol("L", "n")) {
                String value = ee.getFirstPartAsL().getValueNoTags();
                if (ee.getFirstPartAsL().containsSymbol("GD") || ee.getFirstPartAsL().containsSymbol("m") || ee.getFirstPartAsL().containsSymbol("f") || ee.getFirstPartAsL().containsSymbol("mf")) {

                } else {
                    String parValue = lemmaParName.get(value);
                    if (parValue != null) {
                        //System.out.println("parValue: " +  parValue);
                        String genderValue = parNameGender.get(parValue);
                        if (genderValue != null) {
                            System.out.println(value + " is '" + genderValue + "'");
                            getFirstPartsChildren(ee, "L").add(new S(genderValue));
                        }
                    }
                }
            }
        }
    }


    public ArrayList<DixElement> getFirstPartsChildren(E ee, String side) {
        for (DixElement e : ee.children) {
            if (e instanceof I) {
                return ((I) e).children;
            }
            if (e instanceof P) {
                if (side.equals("L")) {
                    return ((P) e).l.children;
                }
                if (side.equals("R")) {
                    return ((P) e).r.children;
                }
            }
        }
        return null;
    }


    private void process_adjs() {
        DictionaryReader morphReader = new DictionaryReader(this.morphDic);
        Dictionary morph = morphReader.readDic();

        HashMap<String, String> parNameGender = new HashMap<String, String>();

        for (Pardef pfe : morph.pardefs.elements) {
            if (pfe.hasCategory("adj")) {
                if (pfe.containsSymbol("mf")) {
                    parNameGender.put(pfe.name, "mf");
                }
            }
        }


        HashMap<String, String> lemmaParName = new HashMap<String, String>();
        for (E ee : morph.getEntriesInMainSection()) {
            String lemma = ee.lemma;
            String parName = ee.getMainParadigmName();
            lemmaParName.put(lemma, parName);
        }


        for (E ee : bil.getEntriesInMainSection()) {
            if (ee.isFirstSymbol("L", "adj")) {
                String value = ee.getFirstPartAsL().getValueNoTags();
                if (ee.getFirstPartAsL().containsSymbol("GD") || ee.getFirstPartAsL().containsSymbol("m") || ee.getFirstPartAsL().containsSymbol("f") || ee.getFirstPartAsL().containsSymbol("mf")) {

                } else {
                    String parValue = lemmaParName.get(value);
                    if (parValue != null) {
                        //System.out.println("parValue: " +  parValue);
                        String genderValue = parNameGender.get(parValue);
                        if (genderValue != null) {
                            System.out.println(value + " is '" + genderValue + "'");
                            getFirstPartsChildren(ee, "L").add(new S(genderValue));
                        }
                    }
                }
            }
        }
    }
}
