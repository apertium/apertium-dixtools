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
package misc.enca;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.I;
import dics.elements.dtd.L;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.Pardefs;
import dics.elements.dtd.R;
import dics.elements.dtd.S;
import dics.elements.dtd.Section;
import dics.elements.dtd.TextElement;
import dictools.xml.DictionaryReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class ConvertMF {

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
    private HashMap<String, Pardef> mfPardefsNouns;
    /**
     * 
     */
    private HashMap<String, Pardef> mfPardefsAdjs;

    /**
     * 
     * @param morphDic
     * @param bilDic
     */
    public ConvertMF(String morphDic, String bilDic) {
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
    public void convert() {
        //this.generateMon();
        //this.completeBil();

        DictionaryReader morphReader = new DictionaryReader(this.morphDic);
        Dictionary morph = morphReader.readDic();
        findMFParadigms(morph);

        HashMap<String, E> mfElementsNouns = new HashMap<String, E>();
        for (E e : morph.getAllEntries()) {
            String lemma = e.getLemma();
            String parName = e.getMainParadigmName();
            if (mfPardefsNouns.containsKey(parName)) {
                mfElementsNouns.put(lemma, e);
            }
        }

        HashMap<String, E> mfElementsAdjs = new HashMap<String, E>();
        for (E e : morph.getAllEntries()) {
            String lemma = e.getLemma();
            String parName = e.getMainParadigmName();
            if (mfPardefsAdjs.containsKey(parName)) {
                mfElementsAdjs.put(lemma, e);
            }
        }
        DictionaryReader bilReader = new DictionaryReader(this.bilDic);
        Dictionary bil = bilReader.readDic();

        this.processNouns(mfElementsNouns, bil);
        this.processAdjs(mfElementsAdjs, bil);

        bil.printXML(this.outFileName, dics.elements.utils.DicOpts.STD);

    }

    /**
     * 
     * @param mfElementsNouns
     * @param bil
     */
    private void processNouns(HashMap<String, E> mfElementsNouns, Dictionary bil) {
        boolean isF=false, isM=false, isNoun=false;
        ArrayList<E> toRemove = new ArrayList<E>();
        for (E e : bil.getAllEntries()) {
            if (mfElementsNouns.containsKey(e.getValue("R"))) {
                for (S s : e.getRight().getSymbols()) {
                  if ("m".equals(s.getValue())) isM = true;
                  if ("n".equals(s.getValue())) isNoun = true;
                  if ("f".equals(s.getValue())) isF = true;
                }

                if (e.hasRestriction()) {
                    if (e.getRestriction().equals("RL")) {
                        if (isF && isNoun) {
                            toRemove.add(e);
                        }
                    }
                } else {
                    if (isM && isNoun) {
                        ArrayList<S> sList = e.getRight().getSymbols();
                        for (S sE : sList) {
                            if (sE.is("m")) {
                                S newSE = new S("mf");
                                e.getRight().getChildren().remove(sE);
                                e.getRight().addChild(newSE);
                            }
                        }
                    }
                }
            }
        }
        ArrayList<E> list = new ArrayList<E>();
        for (E e : toRemove) {
            list.add(e);
        }

        for (int i = 0; i < list.size(); i++) {
            E e = list.get(i);
            bil.getAllEntries().remove(e);
        }

    }

    /**
     * 
     * @param mfElementsAdjs
     * @param bil
     */
    private void processAdjs(HashMap<String, E> mfElementsAdjs, Dictionary bil) {
        for (E e : bil.getAllEntries()) {
            if (mfElementsAdjs.containsKey(e.getValue("R"))) {
                for (S s : e.getRight().getSymbols()) {
                  if ("adj".equals(s.getValue())) e.getRight().addChild(new S("mf"));
                }
            }
        }
    }


    /**
     * 
     * @param morph
     * @return
     */
    private void findMFParadigms(Dictionary morph) {
        mfPardefsNouns = new HashMap<String, Pardef>();
        mfPardefsAdjs = new HashMap<String, Pardef>();
        Pardefs pardefs = morph.getPardefsElement();
        for (Pardef pardef : pardefs.getPardefElements()) {
            String parName = pardef.getName();
            boolean isMF = false;
            boolean isNoun = false;
            boolean isAdj = false;
            for (E e : pardef.getEElements()) {
                for (S s : e.getRight().getSymbols()) {
                  if ("mf".equals(s.getValue())) isMF = true;
                  if ("n".equals(s.getValue())) isNoun = true;
                  if ("adj".equals(s.getValue())) isAdj = true;
                }
            }
            if (isMF && isNoun) {
                mfPardefsNouns.put(parName, pardef);
            }
            if (isMF && isAdj) {
                mfPardefsAdjs.put(parName, pardef);
            }
        }
    }

    private void generateMon() {
        DictionaryReader bilReader = new DictionaryReader(this.bilDic);
        Dictionary bil = bilReader.readDic();

        Dictionary mon = new Dictionary();
        Section section = new Section();
        section.setID("main");
        section.setType("standard");
        mon.addSection(section);

        for (E e1 : bil.getAllEntries()) {
            E e = new E();
            e.setLemma(e1.getValue("L"));
            I iE = new I();
            iE.addChild(new TextElement(e1.getValue("L")));
            e.addChild(iE);
            section.addEElement(e);
        }
        mon.printXML("apertium-de-en.de.dix", dics.elements.utils.DicOpts.STD);
    }

    private void completeBil() {
        DictionaryReader bilReader = new DictionaryReader(this.bilDic);
        Dictionary bil = bilReader.readDic();

        for (E e1 : bil.getAllEntries()) {
            L lE = e1.getLeft();
            R rE = e1.getRight();

            ArrayList<S> attr = lE.getSymbols();
            if (attr.size() > 0) {
                S attr0 = attr.get(0);
                if (attr0 != null) {
                    String v = attr0.getValue();
                    S newSE = new S(v);
                    rE.addChild(newSE);
                }
            }
        }
        bil.printXML("apertium-de-en.de-en.dix", dics.elements.utils.DicOpts.STD);


    }
}
