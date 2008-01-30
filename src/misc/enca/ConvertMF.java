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

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.PardefElement;
import dics.elements.dtd.PardefsElement;
import dics.elements.dtd.SElement;
import dics.elements.utils.EElementList;
import dics.elements.utils.SElementList;
import dictools.DictionaryReader;
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
    private HashMap<String, PardefElement> mfPardefsNouns;
    /**
     * 
     */
    private HashMap<String, PardefElement> mfPardefsAdjs;

    /**
     * 
     * @param morphDic
     * @param bilDic
     */
    public ConvertMF(final String morphDic, final String bilDic) {
        this.morphDic = morphDic;
        this.bilDic = bilDic;

    }

    /**
     * 
     */
    public final void processArguments() {
        morphDic = arguments[1];
        bilDic = arguments[2];
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
     */
    public final void convert() {
        DictionaryReader morphReader = new DictionaryReader(this.morphDic);
        DictionaryElement morph = morphReader.readDic();
        findMFParadigms(morph);

        HashMap<String, EElement> mfElementsNouns = new HashMap<String, EElement>();
        for (EElement e : morph.getAllEntries()) {
            String lemma = e.getLemma();
            String parName = e.getParadigmValue();
            if (mfPardefsNouns.containsKey(parName)) {
                mfElementsNouns.put(lemma, e);
            }
        }

        HashMap<String, EElement> mfElementsAdjs = new HashMap<String, EElement>();
        for (EElement e : morph.getAllEntries()) {
            String lemma = e.getLemma();
            String parName = e.getParadigmValue();
            if (mfPardefsAdjs.containsKey(parName)) {
                mfElementsAdjs.put(lemma, e);
            }
        }
        DictionaryReader bilReader = new DictionaryReader(this.bilDic);
        DictionaryElement bil = bilReader.readDic();

        this.processNouns(mfElementsNouns, bil);
        this.processAdjs(mfElementsAdjs, bil);

        bil.printXML(this.outFileName);
    }

    /**
     * 
     * @param mfElementsNouns
     * @param bil
     */
    private final void processNouns(HashMap<String, EElement> mfElementsNouns, DictionaryElement bil) {
        boolean isF, isM, isNoun;
        EElementList toRemove = new EElementList();
        for (EElement e : bil.getAllEntries()) {
            if (mfElementsNouns.containsKey(e.getValue("R"))) {
                SElementList attr = e.getRight().getSElements();
                isF = isM = isNoun = false;
                if (attr.is("m")) {
                    isM = true;
                }
                if (attr.is("f")) {
                    isF = true;
                }
                if (attr.is("n")) {
                    isNoun = true;
                }
                if (e.hasRestriction()) {
                    if (e.getRestriction().equals("RL")) {
                        if (isF && isNoun) {
                            toRemove.add(e);
                        }
                    }
                } else {
                    if (isM && isNoun) {
                        SElementList sList = e.getRight().getSElements();
                        for (SElement sE : sList) {
                            if (sE.is("m")) {
                                SElement newSE = new SElement("mf");
                                e.getRight().getChildren().remove(sE);
                                e.getRight().addChild(newSE);
                            }
                        }
                    }
                }
            }
        }
        EElementList list = new EElementList();
        for (EElement e : toRemove) {
            list.add(e);
        }

        for (int i = 0; i < list.size(); i++) {
            EElement e = list.get(i);
            bil.getAllEntries().remove(e);
        }

    }

    /**
     * 
     * @param mfElementsAdjs
     * @param bil
     */
    private final void processAdjs(final HashMap<String, EElement> mfElementsAdjs, DictionaryElement bil) {
        for (EElement e : bil.getAllEntries()) {
            if (mfElementsAdjs.containsKey(e.getValue("R"))) {
                SElementList attributes = e.getRight().getSElements();
                if (attributes.is("adj")) {
                    e.getRight().addChild(new SElement("mf"));
                }
            }
        }
    }

    /**
     * 
     * @param morph
     * @return
     */
    private final void findMFParadigms(DictionaryElement morph) {
        mfPardefsNouns = new HashMap<String, PardefElement>();
        mfPardefsAdjs = new HashMap<String, PardefElement>();
        PardefsElement pardefs = morph.getPardefsElement();
        for (PardefElement pardef : pardefs.getPardefElements()) {
            String parName = pardef.getName();
            boolean isMF = false;
            boolean isNoun = false;
            boolean isAdj = false;
            for (EElement e : pardef.getEElements()) {
                SElementList attr = e.getRight().getSElements();
                if (attr.is("mf")) {
                    isMF = true;
                }
                if (attr.is("n")) {
                    isNoun = true;
                }
                if (attr.is("adj")) {
                    isAdj = true;
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
}