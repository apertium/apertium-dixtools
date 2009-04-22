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
package dictools;

import dics.elements.utils.DicTools;
import dictools.xml.DictionaryReader;
import java.util.HashMap;

import dics.elements.dtd.AlphabetElement;
import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.PardefElement;
import dics.elements.dtd.PardefsElement;
import dics.elements.dtd.SdefElement;
import dics.elements.dtd.SdefsElement;
import dics.elements.dtd.SectionElement;
import dics.elements.utils.DicSet;
import dics.elements.utils.EElementList;
import dics.elements.utils.EHashMap;
import dics.elements.utils.Msg;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicMerge  extends AbstractDictTool{

    /**
     * 
     */
    private DictionaryElement bilAB1;
    /**
     * 
     */
    private DictionaryElement bilAB2;
    /**
     * 
     */
    private DictionaryElement monA1;
    /**
     * 
     */
    private DictionaryElement monA2;
    /**
     * 
     */
    private DictionaryElement monB1;
    /**
     * 
     */
    private DictionaryElement monB2;
    /**
     * 
     */
    private DicSet dicSet1;
    /**
     * 
     */
    private DicSet dicSet2;
    /**
     * 
     */
    private DicSet merged;
    /**
     * 
     */
    private String sOut;
    /**
     * 
     */
    private HashMap<String, String> paradigmsToRemove;

    /**
     * 
     * 
     */
    public DicMerge() {
        msg.setLogFileName("merge.log");
    }

    /**
     * 
     * @param ds1
     * @param ds2
     */
    public DicMerge(DicSet ds1, DicSet ds2) {
        bilAB1 = ds1.getBil1();
        monA1 = (ds1.getMon1());
        monB1 =(ds1.getMon2());

        bilAB2 = (ds2.getBil1());
        monA1 = (ds2.getMon1());
        monA2 = (ds2.getMon2());

        msg.setLogFileName("merge.log");

    }

    /**
     * 
     * @param bAB1
     * @param bAB2
     */
    public void setBils(DictionaryElement bAB1, DictionaryElement bAB2) {
        bilAB1 = (bAB1);
        bilAB2 = (bAB2);
    }

    /**
     * 
     * @param mA1
     * @param mA2
     */
    public void setMonAs(DictionaryElement mA1, DictionaryElement mA2) {
        monA1 = (mA1);
        monA2 = (mA2);
    }

    /**
     * 
     * @param morph1FileName
     * @param morph2FileName
     */
    public void setMonAs(String morph1FileName,
            String morph2FileName) {
        DictionaryReader dicReader1 = new DictionaryReader(morph1FileName);
        DictionaryElement morph1 = dicReader1.readDic();
        DictionaryReader dicReader2 = new DictionaryReader(morph2FileName);
        DictionaryElement morph2 = dicReader2.readDic();
        monA1 = (morph1);
        monA2 = (morph2);
    }

    /**
     * 
     * @param mB1
     * @param mB2
     */
    public void setMonBs(DictionaryElement mB1, DictionaryElement mB2) {
        monB1 = (mB1);
        monB2 =(mB2);
    }

    /**
     * 
     * @return Undefined         
     */
    private DicSet merge() {
        DictionaryElement bilAB = mergeBils(bilAB1, bilAB2);
        String fileName = bilAB1.getFileName();
        fileName = DicTools.removeExtension(fileName);
        bilAB.setFileName(fileName + "-merged.dix");

        bilAB.countEntries();
        bilAB1.countEntries();
        bilAB2.countEntries();
        bilAB.addProcessingComment("\n\tResult of merging 2 dictionaries:");
        bilAB.addProcessingComment("\t" + bilAB.getNEntries() + " entries (" + bilAB1.getNEntries() + " U " + bilAB2.getNEntries() + ")");

        DictionaryElement monA = mergeMonols(monA1, monA2);
        String monAfn = monA1.getFileName();
        monAfn = DicTools.removeExtension(monAfn);
        monA.setFileName(monAfn + "-merged.dix");

        monA.countEntries();
        monA1.countEntries();
        monA2.countEntries();
        monA.addProcessingComment("\n\tResult of merging 2 dictionaries:");
        monA.addProcessingComment("\t" + monA.getNEntries() + " entries (" + monA1.getNEntries() + " U " + monA2.getNEntries() + ")");

        DictionaryElement monB = mergeMonols(monB1, monB2);
        String monBfn = monB1.getFileName();
        monBfn = DicTools.removeExtension(monBfn);
        monB.setFileName(monBfn + "-merged.dix");

        monB.countEntries();
        monB1.countEntries();
        monB2.countEntries();
        monB.addProcessingComment("\n\tResult of merging 2 dictionaries:");
        monB.addProcessingComment("\t" + monB.getNEntries() + " entries (" + monB1.getNEntries() + " U " + monB2.getNEntries() + ")");

        DicSet dicSet = new DicSet(bilAB, monA, monB);
        return dicSet;
    }

    /**
     * 
     * @param bAB1
     * @param bAB2
     * @return Undefined         */
    private DictionaryElement mergeBils(DictionaryElement bAB1, DictionaryElement bAB2) {
        DictionaryElement dic = new DictionaryElement();

        AlphabetElement alphabet = mergeAlphabetElement(bAB1.getAlphabet(), bAB2.getAlphabet());
        dic.setAlphabet(alphabet);

        SdefsElement sdefs = new SdefsElement();
        sdefs = mergeSdefElements(bAB1.getSdefs(), bAB2.getSdefs());
        dic.setSdefs(sdefs);

        for (SectionElement section1 : bAB1.getSections()) {
            SectionElement section2 = bAB2.getSection(section1.getID());
            if (section2 != null) {
                SectionElement section = mergeSectionElements(section1,
                        section2);
                dic.addSection(section);
            }
        }
        return dic;
    }

    /**
     * 
     * @param m1
     * @param m2
     * @return Undefined         
     */
    private DictionaryElement mergeMonols(DictionaryElement m1, DictionaryElement m2) {
        DictionaryElement mon = new DictionaryElement();
        AlphabetElement alphabet = mergeAlphabetElement(m1.getAlphabet(), m2.getAlphabet());
        mon.setAlphabet(alphabet);

        SdefsElement sdefs = mergeSdefElements(m1.getSdefs(), m2.getSdefs());
        mon.setSdefs(sdefs);

        PardefsElement pardefs = mergePardefElements(m1.getPardefsElement(), m2.getPardefsElement());
        mon.setPardefs(pardefs);

        for (SectionElement section1 : m1.getSections()) {
            SectionElement section2 = m2.getSection(section1.getID());
            if (section2 != null) {
                SectionElement section = mergeSectionElements(section1, section2);
                mon.addSection(section);
            } else {
                msg.err("There's no '" + section1.getID() + "' section in monolingual 2");
            }
        }
        return mon;
    }

    /**
     * 
     * @param sectionE1
     * @param sectionE2
     * @return Undefined         
     */
    private SectionElement mergeSectionElements( SectionElement sectionE1, SectionElement sectionE2) {

        System.err.println("Merging section '" + sectionE1.getID() + "'...");
        SectionElement sectionElement = new SectionElement();
        EHashMap eMap = new EHashMap();

        sectionElement.setID(sectionE1.getID());
        sectionElement.setType(sectionE1.getType());

        int duplicated = 0;

        paradigmsToRemove = new HashMap<String, String>();

        EElementList elements1 = sectionE1.getEElements();
        System.err.println("  monolingual 1 (" + elements1.size() + " lemmas)");
        int fromSec1 = 0;
        for (EElement e1 : elements1) {
            //String e1Key = e1.lemmaAndCategory();
            String e1Key = e1.toStringAll();
            if (!eMap.containsKey(e1Key)) {
                eMap.put(e1Key, e1);
                sectionElement.addEElement(e1);
                fromSec1++;
            } else {
                duplicated++;
            }
        }

        EElementList elements2 = sectionE2.getEElements();
        System.err.println("  monolingual 2 (" + elements2.size() + " lemmas)");
        int common = 0;
        int notin = 0;
        int fromSec2 = 0;
        boolean first = true;
        for (EElement e2 : elements2) {
            // String e2Key = e2.toStringNoParadigm();
            //String e2Key = e2.lemmaAndCategory();
            String e2Key = e2.toStringAll();
            if (!eMap.containsKey(e2Key)) {
                notin++;
                msg.log("[" + notin + "] section 1 doesn't contain: " + e2Key);
                eMap.put(e2Key, e2);
                if (first) {
                    e2.setProcessingComments("\n\n ====== HERE AND BELOW ARE ADDITIONS FROM SECOND FILE======\n\n");
                    first = false;
                }
                sectionElement.addEElement(e2);
                fromSec2++;
            } else {
                // System.err.println("'" + e2.getLemma() + "' already
                // exists.");
                common++;
                String parName2 = e2.getMainParadigmName();

                if (parName2 != null) {

                    EElement e1 = eMap.get(e2Key);
                    String parName1 = e1.getMainParadigmName();

                    if (!parName1.equals(parName2)) {
                        msg.log("Paradigms for <" + e1.getLemma() + "> : (" + parName1 + ", " + parName2 + ")");
                        // addParadigmToRemove(parName2);
                        e1.getParadigm().addProcessingComment(
                                "\n\t<!-- also paradigm '" + parName2 + "' -->");
                    } else {
                    // System.err.println("Paradigms are the same");
                    }
                }

            }
        }
        System.err.println("  " + common + " common lemmas");
        //System.err.println("  " + (fromSec1 - common) + " new lemmas from monol. 1");
        //System.err.println("  " + fromSec2 + " new lemmas from monol. 2");
        System.err.println("  " + sectionElement.getEElements().size() + " lemmas in merged dictionary");

        // System.err.println(duplicated + " duplicated entries in sections " +
        // sectionE1.getID() + "/" + sectionE2.getID());

        return sectionElement;
    }

    /**
     * 
     * @param sdefs1
     * @param sdefs2
     * @return Undefined         */
    private static SdefsElement mergeSdefElements(SdefsElement sdefs1, SdefsElement sdefs2) {
        SdefsElement sdefs = new SdefsElement();
        HashMap<String, SdefElement> sdefMap = new HashMap<String, SdefElement>();

        for (SdefElement sdef1 : sdefs1.getSdefsElements()) {
            String sdef1Key = sdef1.toString();
            if (!sdefMap.containsKey(sdef1Key)) {
                sdefMap.put(sdef1Key, sdef1);
                sdefs.addSdefElement(sdef1);
            }
        }

        for (SdefElement sdef2 : sdefs2.getSdefsElements()) {
            String sdef2Key = sdef2.toString();
            if (!sdefMap.containsKey(sdef2Key)) {
                sdefMap.put(sdef2Key, sdef2);
                sdefs.addSdefElement(sdef2);
            }
        }
        return sdefs;
    }

    /**
     * 
     * @param alphabet1
     * @param alphabet2
     * @return Undefined         */
    private static AlphabetElement mergeAlphabetElement( AlphabetElement alphabet1, AlphabetElement alphabet2) {
        AlphabetElement alphabet = new AlphabetElement();
        // We take one of them
        alphabet.setAlphabet(alphabet1.getAlphabet());
        return alphabet;
    }

    /**
     * 
     * @param pardefs1
     * @param pardefs2
     * @return Undefined
     */
    private static PardefsElement mergePardefElements( PardefsElement pardefs1, PardefsElement pardefs2) {
        PardefsElement pardefs = new PardefsElement();
        HashMap<String, PardefElement> pardefNameMap = new HashMap<String, PardefElement>();
        HashMap<String, PardefElement> pardefAllMap = new HashMap<String, PardefElement>();


        for (PardefElement pardef1 : pardefs1.getPardefElements()) {
            //System.err.println("Paradigm: " + pardef1.getName());

            pardefNameMap.put(pardef1.getName(), pardef1);
            pardefAllMap.put(pardef1.toString(), pardef1);
            pardefs.addPardefElement(pardef1);
        }

        boolean first = true;

        for (PardefElement pardef2 : pardefs2.getPardefElements()) {
            String pardef2Key = pardef2.toString();
            if (!pardefAllMap.containsKey(pardef2Key)) {
                System.err.println("Paradigm: " + pardef2.getName());

                while (pardefNameMap.containsKey(pardef2.getName())) {
                    // Rename
                    pardef2.setName( pardef2.getName()+"___mergeTODO");
                    pardef2Key  = pardef2.toString();
                }

                if (first) {
                    pardef2.setProcessingComments("\n\n ====== HERE AND BELOW ARE ADDITIONS FROM SECOND FILE======\n\n");
                    first = false;
                }
                pardefNameMap.put(pardef2.getName(), pardef2);
                pardefAllMap.put(pardef2Key, pardef2);
                pardefs.addPardefElement(pardef2);
            }
        }
        return pardefs;
    }

    /**
     * 
     * 
     */
    public void doMerge() {
        processArguments();
        actionMerge();
    }

    /**
     * 
     * 
     */
    public void doMergeMorph() {
        processArgumentsMorph();
        mergeMorph();
    }

    /**
     * 
     * 
     */
    public void doMergeBil() {
        processArgumentsBil();
        mergeBil();
    }

    /**
     * 
     * 
     */
    public void actionMerge() {
        DicSet dicSet = merge();
        // setMerged(dicSet);
        DictionaryElement bil = dicSet.getBil1();
        DictionaryElement monA = dicSet.getMon1();
        DictionaryElement monB = dicSet.getMon2();

        bil.printXML(bil.getFileName(),getOpt());
        monA.printXML(monA.getFileName(),getOpt());
        monB.printXML(monB.getFileName(),getOpt());
    }

    /**
     * 
     * @param arguments
     */
    private void processArguments() {
        int nArgs = getArguments().length;

        String sDicMonA1, sDicMonA2, sDicMonB1, sDicMonB2;
        sDicMonA1 = sDicMonA2 = sDicMonB1 = sDicMonB2 = null;
        String sDicBilAB1, sDicBilAB2;
        sDicBilAB1 = sDicBilAB2 = null;
        boolean bilAB1Reverse, bilAB2Reverse;
        ;
        bilAB1Reverse = bilAB2Reverse = false;

        for (int i = 1; i < nArgs; i++) {
            String arg = getArguments()[i];
            if (arg.equals("-monA1")) {
                i++;
                arg = getArguments()[i];
                sDicMonA1 = arg;
                System.err.println("Monolingual A1:\t'" + sDicMonA1 + "'");
            }

            if (arg.equals("-monA2")) {
                i++;
                arg = getArguments()[i];
                sDicMonA2 = arg;
                System.err.println("Monolingual A2:\t '" + sDicMonA2 + "'");
            }

            if (arg.equals("-out")) {
                i++;
                arg = getArguments()[i];
                sOut = arg;
                System.err.println("Merged:\t'" + sOut + "'");
            }

            if (arg.equals("-monB")) {
                i++;
                arg = getArguments()[i];
                sDicMonB1 = arg;
                System.err.println("Monolingual B1:\t'" + sDicMonB1 + "'");
            }

            if (arg.equals("-monB2")) {
                i++;
                arg = getArguments()[i];
                sDicMonB2 = arg;
                System.err.println("Monolingual B2:\t'" + sDicMonB2 + "'");
            }

            if (arg.equals("-bilAB")) {
                i++;
                arg = getArguments()[i];
                if (arg.equals("-r")) {
                    bilAB1Reverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilAB1Reverse = false;
                    i++;
                }
                arg = getArguments()[i];
                sDicBilAB1 = arg;
                System.err.println("Bilingual AB1:\t'" + sDicBilAB1 + "'");
            }

            if (arg.equals("-bilAB2")) {
                i++;
                arg = getArguments()[i];
                if (arg.equals("-r")) {
                    bilAB2Reverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilAB2Reverse = false;
                    i++;
                }
                arg = getArguments()[i];
                sDicBilAB2 = arg;
                System.err.println("Bilingual AB2:\t'" + sDicBilAB2 + "'");
            }

        }

        DictionaryElement bilAB1 = DicTools.readBilingual(sDicBilAB1, bilAB1Reverse);
        DictionaryElement monA1 = DicTools.readMonolingual(sDicMonA1);
        DictionaryElement monB1 = DicTools.readMonolingual(sDicMonB1);
        DicSet dicSet1 = new DicSet(bilAB1, monA1, monB1);
        setDicSet1(dicSet1);
        this.bilAB1 =(bilAB1);
        this.monA1 = (monA1);
        this.monB1 = (monB1);

        DictionaryElement bilAB2 = DicTools.readBilingual(sDicBilAB2, bilAB2Reverse);
        DictionaryElement monA2 = DicTools.readMonolingual(sDicMonA2);
        DictionaryElement monB2 = DicTools.readMonolingual(sDicMonB2);
        DicSet dicSet2 = new DicSet(bilAB2, monA2, monB2);
        this.bilAB2 = (bilAB2);
        this.monA2 = (monA2);
        this.monB2 = (monB2);

        setDicSet2(dicSet2);

    }

    /**
     * 
     * 
     */
    private void processArgumentsMorph() {
        int nArgs = getArguments().length;

        String sDicMonA1, sDicMonA2;
        sDicMonA1 = sDicMonA2 = null;
        
        for (int i = 1; i < nArgs; i++) {
            String arg = getArguments()[i];
            if (arg.equals("-monA1")) {
                i++;
                arg = getArguments()[i];
                sDicMonA1 = arg;
                System.err.println("Monolingual A1:\t'" + sDicMonA1 + "'");
            }
            else
            if (arg.equals("-monA2")) {
                i++;
                arg = getArguments()[i];
                sDicMonA2 = arg;
                System.err.println("Monolingual A2:\t '" + sDicMonA2 + "'");
            }
            else
            if (arg.equals("-out")) {
                i++;
                arg = getArguments()[i];
                sOut = arg;
                System.err.println("Merged:\t'" + sOut + "'");
            }
            else
              System.err.println("Uknown option  "+arg);
        }

        DictionaryElement monA1 = DicTools.readMonolingual(sDicMonA1);
        this.monA1 =(monA1);

        DictionaryElement monA2 = DicTools.readMonolingual(sDicMonA2);
        this.monA2 =(monA2);
    }

    /**
     * 
     * 
     */
    private void processArgumentsBil() {
        int nArgs = getArguments().length;

        String sDicMonA1, sDicMonA2;
        sDicMonA1 = sDicMonA2 = null;
        String sDicBilAB1, sDicBilAB2;
        sDicBilAB1 = sDicBilAB2 = null;
        boolean bilAB1Reverse, bilAB2Reverse;
        ;
        bilAB1Reverse = bilAB2Reverse = false;

        for (int i = 1; i < nArgs; i++) {
            String arg = getArguments()[i];
            if (arg.equals("-bilAB1")) {
                i++;
                arg = getArguments()[i];
                if (arg.equals("-r")) {
                    bilAB1Reverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilAB1Reverse = false;
                    i++;
                }
                arg = getArguments()[i];
                sDicBilAB1 = arg;
                System.err.println("Bilingual AB1:\t'" + sDicBilAB1 + "'");
            }

            if (arg.equals("-bilAB2")) {
                i++;
                arg = getArguments()[i];
                if (arg.equals("-r")) {
                    bilAB2Reverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilAB2Reverse = false;
                    i++;
                }
                arg = getArguments()[i];
                sDicBilAB2 = arg;
                System.err.println("Bilingual AB2:\t'" + sDicBilAB2 + "'");
            }

            if (arg.equals("-out")) {
                i++;
                arg = getArguments()[i];
                sOut = arg;
                System.err.println("Merged:\t'" + sOut + "'");
            }

        }

        DictionaryElement bilAB1 = DicTools.readBilingual(sDicBilAB1,
                bilAB1Reverse);
        this.bilAB1 =(bilAB1);

        DictionaryElement bilAB2 = DicTools.readBilingual(sDicBilAB2,
                bilAB2Reverse);
        this.bilAB2 =(bilAB2);

    }


    /**
     * @return the dicSet1
     */
    public DicSet getDicSet1() {
        return dicSet1;
    }

    /**
     * @param dicSet1
     *                the dicSet1 to set
     */
    public void setDicSet1(DicSet dicSet1) {
        this.dicSet1 = dicSet1;
    }

    /**
     * @return the dicSet2
     */
    public DicSet getDicSet2() {
        return dicSet2;
    }

    /**
     * @param dicSet2
     *                the dicSet2 to set
     */
    public void setDicSet2(DicSet dicSet2) {
        this.dicSet2 = dicSet2;
    }

    /**
     * @return the merged
     */
    public DicSet getMerged() {
        return merged;
    }

    /**
     * 
     * @param merged
     */
    public void setMerged(DicSet merged) {
        this.merged = merged;
    }

    /**
     * 
     * 
     */
    public void mergeMorph() {
        DictionaryElement morph = mergeMonols(monA1, monA2);
        morph.printXML(getSOut(),getOpt());
    }

    public void mergeMorph__OLD_which_sorts_output_but() {
        DictionaryElement morph = mergeMonols(monA1, monA2);
        DicSort dicSort = new DicSort(morph);
        dicSort.msg.setDebug(false);
        DictionaryElement sorted = dicSort.sort();
        sorted.printXML(getSOut(),getOpt());
    }


    /**
     * 
     * 
     */
    public void mergeBil() {
        DictionaryElement bil = mergeBils(bilAB1, bilAB2);
        DicSort dicSort = new DicSort(bil);
        DictionaryElement sorted = dicSort.sort();
        sorted.printXML(getSOut(),getOpt());
    }

    /**
     * @return the sOut
     */
    private String getSOut() {
        return sOut;
    }

    /**
     * @return the paradigmsToRemove
     */
    public HashMap<String, String> getParadigmsToRemove() {
        return paradigmsToRemove;
    }

    /**
     * 
     * @param parName
     */
    public void addParadigmToRemove(String parName) {
        paradigmsToRemove.put(parName, parName);
    }
}
