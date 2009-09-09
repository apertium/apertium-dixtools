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

import dics.elements.dtd.Alphabet;
import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.Pardefs;
import dics.elements.dtd.Sdef;
import dics.elements.dtd.Sdefs;
import dics.elements.dtd.Section;
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
    private Dictionary bilAB1;
    /**
     * 
     */
    private Dictionary bilAB2;
    /**
     * 
     */
    private Dictionary monA1;
    /**
     * 
     */
    private Dictionary monA2;
    /**
     * 
     */
    private Dictionary monB1;
    /**
     * 
     */
    private Dictionary monB2;
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
    public void setBils(Dictionary bAB1, Dictionary bAB2) {
        bilAB1 = (bAB1);
        bilAB2 = (bAB2);
    }

    /**
     * 
     * @param mA1
     * @param mA2
     */
    public void setMonAs(Dictionary mA1, Dictionary mA2) {
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
        Dictionary morph1 = dicReader1.readDic();
        DictionaryReader dicReader2 = new DictionaryReader(morph2FileName);
        Dictionary morph2 = dicReader2.readDic();
        monA1 = (morph1);
        monA2 = (morph2);
    }

    /**
     * 
     * @param mB1
     * @param mB2
     */
    public void setMonBs(Dictionary mB1, Dictionary mB2) {
        monB1 = (mB1);
        monB2 =(mB2);
    }

    /**
     * 
     * @return Undefined         
     */
    private DicSet merge() {
        Dictionary bilAB = mergeBils(bilAB1, bilAB2);
        String fileName = bilAB1.getFileName();
        fileName = DicTools.removeExtension(fileName);
        bilAB.setFileName(fileName + "-merged.dix");

        bilAB.countEntries();
        bilAB1.countEntries();
        bilAB2.countEntries();
        bilAB.addProcessingComment("\n\tResult of merging 2 dictionaries:");
        bilAB.addProcessingComment("\t" + bilAB.getNEntries() + " entries (" + bilAB1.getNEntries() + " U " + bilAB2.getNEntries() + ")");

        Dictionary monA = mergeMonols(monA1, monA2);
        String monAfn = monA1.getFileName();
        monAfn = DicTools.removeExtension(monAfn);
        monA.setFileName(monAfn + "-merged.dix");

        monA.countEntries();
        monA1.countEntries();
        monA2.countEntries();
        monA.addProcessingComment("\n\tResult of merging 2 dictionaries:");
        monA.addProcessingComment("\t" + monA.getNEntries() + " entries (" + monA1.getNEntries() + " U " + monA2.getNEntries() + ")");

        Dictionary monB = mergeMonols(monB1, monB2);
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
    private Dictionary mergeBils(Dictionary bAB1, Dictionary bAB2) {
        Dictionary dic = new Dictionary();

        Alphabet alphabet = mergeAlphabetElement(bAB1.getAlphabet(), bAB2.getAlphabet());
        dic.setAlphabet(alphabet);

        Sdefs sdefs = new Sdefs();
        sdefs = mergeSdefElements(bAB1.getSdefs(), bAB2.getSdefs());
        dic.setSdefs(sdefs);

        Pardefs pardefs1 = bAB1.getPardefsElement();
        Pardefs pardefs2 = bAB2.getPardefsElement();
        Pardefs pardefs;
        if ((pardefs1 != null) && (pardefs2 != null)) {
            pardefs = mergePardefElements(pardefs1, pardefs2);
        } else if ((pardefs1 != null) && (pardefs2 == null)) {
            pardefs = pardefs1;
        } else if ((pardefs1 == null) && (pardefs2 != null)) {
            pardefs = pardefs2;
        } else {
            pardefs = null;
        }
        dic.setPardefs(pardefs);

        for (Section section1 : bAB1.getSections()) {
            Section section2 = bAB2.getSection(section1.getID());
            if (section2 != null) {
                Section section = mergeSectionElements(section1,
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
    private Dictionary mergeMonols(Dictionary m1, Dictionary m2) {
        Dictionary mon = new Dictionary();
        Alphabet alphabet = mergeAlphabetElement(m1.getAlphabet(), m2.getAlphabet());
        mon.setAlphabet(alphabet);

        Sdefs sdefs = mergeSdefElements(m1.getSdefs(), m2.getSdefs());
        mon.setSdefs(sdefs);

        Pardefs pardefs = mergePardefElements(m1.getPardefsElement(), m2.getPardefsElement());
        mon.setPardefs(pardefs);

        for (Section section1 : m1.getSections()) {
            Section section2 = m2.getSection(section1.getID());
            if (section2 != null) {
                Section section = mergeSectionElements(section1, section2);
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
    private Section mergeSectionElements( Section sectionE1, Section sectionE2) {

        System.err.println("Merging section '" + sectionE1.getID() + "'...");
        Section sectionElement = new Section();
        EHashMap eMap = new EHashMap();

        sectionElement.setID(sectionE1.getID());
        sectionElement.setType(sectionE1.getType());

        int duplicated = 0;

        paradigmsToRemove = new HashMap<String, String>();

        EElementList elements1 = sectionE1.getEElements();
        System.err.println("  monolingual 1 (" + elements1.size() + " lemmas)");
        int fromSec1 = 0;
        for (E e1 : elements1) {
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
        for (E e2 : elements2) {
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

                    E e1 = eMap.get(e2Key);
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
    private static Sdefs mergeSdefElements(Sdefs sdefs1, Sdefs sdefs2) {
        Sdefs sdefs = new Sdefs();
        HashMap<String, Sdef> sdefMap = new HashMap<String, Sdef>();

        for (Sdef sdef1 : sdefs1.getSdefsElements()) {
            String sdef1Key = sdef1.toString();
            if (!sdefMap.containsKey(sdef1Key)) {
                sdefMap.put(sdef1Key, sdef1);
                sdefs.addSdefElement(sdef1);
            }
        }

        for (Sdef sdef2 : sdefs2.getSdefsElements()) {
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
    private static Alphabet mergeAlphabetElement( Alphabet alphabet1, Alphabet alphabet2) {
        Alphabet alphabet = new Alphabet();
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
    private static Pardefs mergePardefElements( Pardefs pardefs1, Pardefs pardefs2) {
        Pardefs pardefs = new Pardefs();
        HashMap<String, Pardef> pardefNameMap = new HashMap<String, Pardef>();
        HashMap<String, Pardef> pardefAllMap = new HashMap<String, Pardef>();


        for (Pardef pardef1 : pardefs1.getPardefElements()) {
            //System.err.println("Paradigm: " + pardef1.getName());

            pardefNameMap.put(pardef1.getName(), pardef1);
            pardefAllMap.put(pardef1.toString(), pardef1);
            pardefs.addPardefElement(pardef1);
        }

        boolean first = true;

        for (Pardef pardef2 : pardefs2.getPardefElements()) {
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
        Dictionary bil = dicSet.getBil1();
        Dictionary monA = dicSet.getMon1();
        Dictionary monB = dicSet.getMon2();

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

        Dictionary bilAB1 = DicTools.readBilingual(sDicBilAB1, bilAB1Reverse);
        Dictionary monA1 = DicTools.readMonolingual(sDicMonA1);
        Dictionary monB1 = DicTools.readMonolingual(sDicMonB1);
        DicSet dicSet1 = new DicSet(bilAB1, monA1, monB1);
        setDicSet1(dicSet1);
        this.bilAB1 =(bilAB1);
        this.monA1 = (monA1);
        this.monB1 = (monB1);

        Dictionary bilAB2 = DicTools.readBilingual(sDicBilAB2, bilAB2Reverse);
        Dictionary monA2 = DicTools.readMonolingual(sDicMonA2);
        Dictionary monB2 = DicTools.readMonolingual(sDicMonB2);
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

        Dictionary monA1 = DicTools.readMonolingual(sDicMonA1);
        this.monA1 =(monA1);

        Dictionary monA2 = DicTools.readMonolingual(sDicMonA2);
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

        Dictionary bilAB1 = DicTools.readBilingual(sDicBilAB1,
                bilAB1Reverse);
        this.bilAB1 =(bilAB1);

        Dictionary bilAB2 = DicTools.readBilingual(sDicBilAB2,
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
        Dictionary morph = mergeMonols(monA1, monA2);
        morph.printXML(getSOut(),getOpt());
    }

    public void mergeMorph__OLD_which_sorts_output_but() {
        Dictionary morph = mergeMonols(monA1, monA2);
        DicSort dicSort = new DicSort(morph);
        Dictionary sorted = dicSort.sort();
        sorted.printXML(getSOut(),getOpt());
    }


    /**
     * 
     * 
     */
    public void mergeBil() {
        Dictionary bil = mergeBils(bilAB1, bilAB2);
        DicSort dicSort = new DicSort(bil);
        Dictionary sorted = dicSort.sort();
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
