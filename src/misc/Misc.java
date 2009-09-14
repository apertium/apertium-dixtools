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
package misc;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.I;
import dics.elements.dtd.L;
import dics.elements.dtd.P;
import dics.elements.dtd.Par;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.R;
import dics.elements.dtd.S;
import dics.elements.dtd.Section;
import dics.elements.dtd.TextElement;
import dictools.xml.DictionaryReader;
import java.util.HashMap;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Misc {

    /**
     * 
     */
    private Dictionary dic1;
    /**
     * 
     */
    private Dictionary dic2;
    /**
     * 
     */
    private Dictionary dic3;
    /**
     * 
     */
    private Dictionary dic4;

    /**
     * 
     * @param dic1FileName
     * @param dic2FileName
     * @param dic3FileName
     * @param dic4FileName
     */
    public Misc(String dic1FileName, String dic2FileName, String dic3FileName, String dic4FileName) {
        DictionaryReader dicReader1 = new DictionaryReader(dic1FileName);
        this.dic1 = dicReader1.readDic();
        System.err.println("dic1.size() = " + dic1.getAllEntries().size());
        DictionaryReader dicReader2 = new DictionaryReader(dic2FileName);
        this.dic2 = dicReader2.readDic();
        System.err.println("dic2.size() = " + dic2.getAllEntries().size());
        DictionaryReader dicReader3 = new DictionaryReader(dic3FileName);
        this.dic3 = dicReader3.readDic();
        System.err.println("dic3.size() = " + dic3.getAllEntries().size());
        DictionaryReader dicReader4 = new DictionaryReader(dic4FileName);
        this.dic4 = dicReader4.readDic();
        System.err.println("dic4.size() = " + dic4.getAllEntries().size());

    }

    public void doMisc() {
        Dictionary bil_n = this.dic1;


        Dictionary morph_n_en = new Dictionary();
        Section section = new Section("main", "standard");
        morph_n_en.addSection(section);

        for (E ee : bil_n.getAllEntries()) {
            E ne = new E();
            ne.setLemma(ee.getValue("R"));
            I iE = new I();
            iE.setValue(ee.getValue("R"));
            ne.addChild(iE);
            Par parE = new Par();
            ne.addChild(parE);
            section.addEElement(ne);
        }

        morph_n_en.printXML("new-es-nouns.dix", "UTF-8",  dics.elements.utils.DicOpts.STD);
    }

    /**
     * 
     */
    public void doMisc7() {
        System.err.println("Begin");
        Dictionary morph_en_adj = this.dic1;
        Dictionary bil_en_es_adj = this.dic2;
        Dictionary morph_en_n = this.dic3;
        Dictionary bil_en_es_n = this.dic4;

        HashMap<String, P> trans_n = new HashMap<String, P>();
        for (E ee : bil_en_es_n.getAllEntries()) {
            String left = ee.getValue("L");
            L lE = ee.getLeft();
            String right = ee.getValue("R");
            R rE = ee.getRight();
            P pE = new P();
            pE.l = (lE);
            pE.r = (rE);
            trans_n.put(left, pE);
        }


        HashMap<String, E> entries = new HashMap<String, E>();
        for (E ee : bil_en_es_adj.getAllEntries()) {
            String value = ee.getValue("L");
            entries.put(value, ee);
        }

        Dictionary ndic = new Dictionary();
        Section section = new Section();
        ndic.addSection(section);
        for (E ee : morph_en_adj.getAllEntries()) {
            String lemma = ee.getLemma();
            if (!entries.containsKey(lemma)) {
                System.err.println("Falta " + lemma + " en el bilingüe");
                P p = trans_n.get(lemma);
                if (p != null) {
                    System.err.println("Nuevo: " + p.getR().getValueNoTags() + " / " + lemma);
                    E ne = new E();
                    ne.setComment("check");
                    P pE = new P();
                    ne.addChild(pE);

                    R rE = new R();
                    for (S sE : p.getL().getSymbols()) {
                        if (sE.getValue().equals("adj")) {
                            sE.setValue("n");
                        }
                    }
                    for (S sE : p.getR().getSymbols()) {
                        if (sE.getValue().equals("adj")) {
                            sE.setValue("n");
                        }
                    }

                    pE.l = (p.getL());

                    //rE.addChild(new TextElement(p.getR().getValueNoTags()));
                    //rE.addChild(new S("n"));
                    pE.r = (p.getR());

                    section.addEElement(ne);
                }
            }
        }

        ndic.printXML("new-en-es-nouns.xml", "UTF-8", dics.elements.utils.DicOpts.STD);

    }

    /**
     * 
     */
    public void doMisc5() {
        Dictionary morph_es = new Dictionary();
        Section s1 = new Section("main", "standard");
        morph_es.addSection(s1);
        Dictionary morph_en = new Dictionary();
        Section s2 = new Section("main", "standard");
        morph_en.addSection(s2);


        Dictionary bil = dic1;

        for (E ee : bil.getAllEntries()) {
            System.err.println(ee.getValue("L") + " / " + ee.getValue("R"));

            E es = new E();
            es.setLemma(ee.getValue("R"));
            I iE = new I();
            iE.setValue(ee.getValue("R"));
            es.addChild(iE);
            Par parE = new Par();
            String par = "";
            if (ee.contains("adv")) {
                par = "ahora__adv";
            }
            parE.setValue(par);
            es.addChild(parE);
            s1.addEElement(es);

            E en = new E();
            en.setLemma(ee.getValue("L"));
            I iEen = new I();
            iEen.setValue(ee.getValue("L"));
            en.addChild(iEen);
            Par parEen = new Par();
            String cat = "";
            if (ee.contains("adv")) {
                cat = "maybe__adv";
            }
            if (ee.contains("n")) {
                cat = "house__n";
            }
            if (ee.contains("adj")) {
                cat = "expensive__adj";
            }
            if (ee.contains("vblex")) {
                if (ee.getValue("L").endsWith("e")) {
                    cat = "liv/e__vblex";
                } else {
                    if (ee.getValue("L").endsWith("y")) {
                        cat = "appl/y__vblex";
                    } else {
                        cat = "accept__vblex";
                    }
                }
            }

            parEen.setValue(cat);
            en.addChild(parEen);
            s2.addEElement(en);
        }

        morph_es.printXML("es-please-check.dix", "UTF-8", dics.elements.utils.DicOpts.STD);
        morph_en.printXML("en-please-check.dix", "UTF-8", dics.elements.utils.DicOpts.STD);

    }

    /**
     * 
     */
    public void doMisc4() {
        Dictionary es_pardefs = dic1;
        Dictionary es_adjs = dic2;
        Dictionary en_es_adjs = dic3;

        HashMap<String, String> mfpars = new HashMap<String, String>();
        for (Pardef pardef : es_pardefs.getPardefsElement().getPardefElements()) {
            String parName = pardef.getName();
            if (parName != null) {
                boolean is_mf = false;
                for (E ee : pardef.getEElements()) {
                    if (ee.contains("mf")) {
                        is_mf = true;

                    }
                }
                if (is_mf) {
                    //System.err.println(parName + " is mf");
                    mfpars.put(parName, parName);
                }
            }
        }

        HashMap<String, String> adjpars = new HashMap<String, String>();
        for (E ee : es_adjs.getAllEntries()) {
            String lemma = ee.getLemma();
            String parName = ee.getMainParadigmName();
            if (mfpars.containsKey(parName)) {
                adjpars.put(lemma, parName);
                System.err.println(lemma + " is mf");
            }
        }

        for (E ee : en_es_adjs.getAllEntries()) {
            R rE = ee.getRight();
            String rv = rE.getValueNoTags();
            if (adjpars.containsKey(rv)) {
                if (!rE.contains("mf")) {
                    rE.addChild(new S("mf"));
                }
            }

        }

        en_es_adjs.printXML("new-adjs-mf.dix", "UTF-8", dics.elements.utils.DicOpts.STD);
    }

    /**
     * 
     */
    public void doMisc3() {
        Dictionary ca_morph = dic1;
        Dictionary es_morph = dic2;
        //Dictionary en_es_bil = dic3;

        HashMap<String, String> pars = new HashMap<String, String>();

        for (E ee : ca_morph.getAllEntries()) {
            String lemma = ee.getLemma();
            if (lemma != null) {
                Par parE = ee.getParadigm();
                if (parE != null) {
                    if (parE.getValue().equals("Marc__np") || parE.getValue().equals("Maria__np")) {
                        pars.put(lemma, parE.getValue());
                    }
                }
            }
        }

        for (E ee : es_morph.getAllEntries()) {
            String lemma = ee.getLemma();
            if (lemma != null) {
                Par parE = ee.getParadigm();
                if (parE != null) {
                    String npar = pars.get(lemma);
                    if (npar != null) {
                        parE.setValue(pars.get(lemma));
                    }
                }
            }
        }
        es_morph.printXML("new-es-morph.dix", "UTF-8", dics.elements.utils.DicOpts.STD);
    }

    /**
     * 
     */
    public void doMisc2() {
        HashMap<String, String> nps = new HashMap<String, String>();
        for (E ee : dic1.getAllEntries()) {
            R re = ee.getRight();
            if (re != null) {
                if (re.is("np")) {
                    String lemma = re.getValueNoTags();
                    if (re.contains("f")) {
                        System.err.println(lemma + " is 'f'");
                        nps.put(lemma, "f");
                    }
                    if (re.contains("m")) {
                        System.err.println(lemma + " is 'm'");
                        nps.put(lemma, "m");
                    }
                /*
                if (re.contains("mf")) {
                System.err.println(lemma + " is 'mf'");
                }
                 */
                }
            }
        }

        for (E ee : dic3.getAllEntries()) {
            String lemma = ee.getLemma();
            if (lemma != null) {
                Par parE = ee.getParadigm();
                if (parE != null) {
                    if (nps.get(lemma) != null) {
                        if (nps.get(lemma).equals("m")) {
                            parE.setValue("Afganistán__np");
                        }
                        if (nps.get(lemma).equals("f")) {
                            parE.setValue("Barcelona__np");
                        }
                    }
                }
            }
        }

        dic3.printXML("nps-gender.dix", "UTF-8", dics.elements.utils.DicOpts.STD);
    }
}
