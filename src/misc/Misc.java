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

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.IElement;
import dics.elements.dtd.LElement;
import dics.elements.dtd.PElement;
import dics.elements.dtd.ParElement;
import dics.elements.dtd.PardefElement;
import dics.elements.dtd.RElement;
import dics.elements.dtd.SElement;
import dics.elements.dtd.SectionElement;
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
    private DictionaryElement dic1;
    /**
     * 
     */
    private DictionaryElement dic2;
    /**
     * 
     */
    private DictionaryElement dic3;
    /**
     * 
     */
    private DictionaryElement dic4;

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
        DictionaryElement bil_n = this.dic1;


        DictionaryElement morph_n_en = new DictionaryElement();
        SectionElement section = new SectionElement("main", "standard");
        morph_n_en.addSection(section);

        for (EElement ee : bil_n.getAllEntries()) {
            EElement ne = new EElement();
            ne.setLemma(ee.getValue("R"));
            IElement iE = new IElement();
            iE.setValue(ee.getValue("R"));
            ne.addChild(iE);
            ParElement parE = new ParElement();
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
        DictionaryElement morph_en_adj = this.dic1;
        DictionaryElement bil_en_es_adj = this.dic2;
        DictionaryElement morph_en_n = this.dic3;
        DictionaryElement bil_en_es_n = this.dic4;

        HashMap<String, PElement> trans_n = new HashMap<String, PElement>();
        for (EElement ee : bil_en_es_n.getAllEntries()) {
            String left = ee.getValue("L");
            LElement lE = ee.getLeft();
            String right = ee.getValue("R");
            RElement rE = ee.getRight();
            PElement pE = new PElement();
            pE.setLElement(lE);
            pE.setRElement(rE);
            trans_n.put(left, pE);
        }


        HashMap<String, EElement> entries = new HashMap<String, EElement>();
        for (EElement ee : bil_en_es_adj.getAllEntries()) {
            String value = ee.getValue("L");
            entries.put(value, ee);
        }

        DictionaryElement ndic = new DictionaryElement();
        SectionElement section = new SectionElement();
        ndic.addSection(section);
        for (EElement ee : morph_en_adj.getAllEntries()) {
            String lemma = ee.getLemma();
            if (!entries.containsKey(lemma)) {
                System.err.println("Falta " + lemma + " en el bilingüe");
                PElement p = trans_n.get(lemma);
                if (p != null) {
                    System.err.println("Nuevo: " + p.getR().getValueNoTags() + " / " + lemma);
                    EElement ne = new EElement();
                    ne.setComment("check");
                    PElement pE = new PElement();
                    ne.addChild(pE);

                    RElement rE = new RElement();
                    for (SElement sE : p.getL().getSElements()) {
                        if (sE.getValue().equals("adj")) {
                            sE.setValue("n");
                        }
                    }
                    for (SElement sE : p.getR().getSElements()) {
                        if (sE.getValue().equals("adj")) {
                            sE.setValue("n");
                        }
                    }

                    pE.setLElement(p.getL());

                    //rE.addChild(new TextElement(p.getR().getValueNoTags()));
                    //rE.addChild(new SElement("n"));
                    pE.setRElement(p.getR());

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
        DictionaryElement morph_es = new DictionaryElement();
        SectionElement s1 = new SectionElement("main", "standard");
        morph_es.addSection(s1);
        DictionaryElement morph_en = new DictionaryElement();
        SectionElement s2 = new SectionElement("main", "standard");
        morph_en.addSection(s2);


        DictionaryElement bil = dic1;

        for (EElement ee : bil.getAllEntries()) {
            System.err.println(ee.getValue("L") + " / " + ee.getValue("R"));

            EElement es = new EElement();
            es.setLemma(ee.getValue("R"));
            IElement iE = new IElement();
            iE.setValue(ee.getValue("R"));
            es.addChild(iE);
            ParElement parE = new ParElement();
            String par = "";
            if (ee.contains("adv")) {
                par = "ahora__adv";
            }
            parE.setValue(par);
            es.addChild(parE);
            s1.addEElement(es);

            EElement en = new EElement();
            en.setLemma(ee.getValue("L"));
            IElement iEen = new IElement();
            iEen.setValue(ee.getValue("L"));
            en.addChild(iEen);
            ParElement parEen = new ParElement();
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
        DictionaryElement es_pardefs = dic1;
        DictionaryElement es_adjs = dic2;
        DictionaryElement en_es_adjs = dic3;

        HashMap<String, String> mfpars = new HashMap<String, String>();
        for (PardefElement pardef : es_pardefs.getPardefsElement().getPardefElements()) {
            String parName = pardef.getName();
            if (parName != null) {
                boolean is_mf = false;
                for (EElement ee : pardef.getEElements()) {
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
        for (EElement ee : es_adjs.getAllEntries()) {
            String lemma = ee.getLemma();
            String parName = ee.getParadigmValue();
            if (mfpars.containsKey(parName)) {
                adjpars.put(lemma, parName);
                System.err.println(lemma + " is mf");
            }
        }

        for (EElement ee : en_es_adjs.getAllEntries()) {
            RElement rE = ee.getRight();
            String rv = rE.getValueNoTags();
            if (adjpars.containsKey(rv)) {
                if (!rE.contains("mf")) {
                    rE.addChild(new SElement("mf"));
                }
            }

        }

        en_es_adjs.printXML("new-adjs-mf.dix", "UTF-8", dics.elements.utils.DicOpts.STD);
    }

    /**
     * 
     */
    public void doMisc3() {
        DictionaryElement ca_morph = dic1;
        DictionaryElement es_morph = dic2;
        //DictionaryElement en_es_bil = dic3;      

        HashMap<String, String> pars = new HashMap<String, String>();

        for (EElement ee : ca_morph.getAllEntries()) {
            String lemma = ee.getLemma();
            if (lemma != null) {
                ParElement parE = ee.getParadigm();
                if (parE != null) {
                    if (parE.getValue().equals("Marc__np") || parE.getValue().equals("Maria__np")) {
                        pars.put(lemma, parE.getValue());
                    }
                }
            }
        }

        for (EElement ee : es_morph.getAllEntries()) {
            String lemma = ee.getLemma();
            if (lemma != null) {
                ParElement parE = ee.getParadigm();
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
        for (EElement ee : dic1.getAllEntries()) {
            RElement re = ee.getRight();
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

        for (EElement ee : dic3.getAllEntries()) {
            String lemma = ee.getLemma();
            if (lemma != null) {
                ParElement parE = ee.getParadigm();
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
