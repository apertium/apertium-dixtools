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

import java.util.ArrayList;
import java.util.HashMap;

import dics.elements.dtd.Alphabet;
import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.Pardefs;
import dics.elements.dtd.Sdef;
import dics.elements.dtd.Sdefs;
import dics.elements.dtd.Section;
import dictools.utils.DicSet;
import dictools.utils.DicTools;
import dictools.utils.DictionaryReader;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicMerge  extends AbstractDictTool {

    
    private Dictionary bilAB1;
    
    private Dictionary bilAB2;
    
    private Dictionary monA1;
    
    private Dictionary monA2;
    
    private Dictionary monB1;
    
    private Dictionary monB2;
    
//    private DicSet dicSet1;
    
//    private DicSet dicSet2;
    
//    private DicSet merged;
    
    private String sOut;
  private DicSet dicSet2;
    
//    private HashMap<String, String> paradigmsToRemove;

    
    public DicMerge() {
        msg.setLogFileName("merge.log");
    }


// TODO UCdetector: Remove unused code: 
//     /**
//      * 
//      * @param ds1
//      * @param ds2
//      */
//     public DicMerge(DicSet ds1, DicSet ds2) {
//         bilAB1 = ds1.getBil1();
//         monA1 = (ds1.getMon1());
//         monB1 =(ds1.getMon2());
// 
//         bilAB2 = (ds2.getBil1());
//         monA1 = (ds2.getMon1());
//         monA2 = (ds2.getMon2());
// 
//         msg.setLogFileName("merge.log");
// 
//     }


// TODO UCdetector: Remove unused code: 
//     /**
//      * 
//      * @param morph1FileName
//      * @param morph2FileName
//      */
//     public void setMonAs(String morph1FileName,
//             String morph2FileName) {
//         DictionaryReader dicReader1 = new DictionaryReader(morph1FileName);
//         Dictionary morph1 = dicReader1.readDic();
//         DictionaryReader dicReader2 = new DictionaryReader(morph2FileName);
//         Dictionary morph2 = dicReader2.readDic();
//         monA1 = (morph1);
//         monA2 = (morph2);
//     }

    /**
     * 
     * @return Undefined         
     */
    private DicSet merge() {
        Dictionary bilAB = mergeBils(bilAB1, bilAB2);
        String fileName = bilAB1.fileName;
        fileName = DicTools.removeExtension(fileName);
        bilAB.fileName = fileName + "-merged.dix";

        bilAB.countEntries();
        bilAB1.countEntries();
        bilAB2.countEntries();
        bilAB.addProcessingComment("\n\tResult of merging 2 dictionaries:");
        bilAB.addProcessingComment("\t" + bilAB.nEntries + " entries (" + bilAB1.nEntries + " U " + bilAB2.nEntries + ")");

        Dictionary monA = mergeMonols(monA1, monA2);
        String monAfn = monA1.fileName;
        monAfn = DicTools.removeExtension(monAfn);
        monA.fileName = monAfn + "-merged.dix";

        monA.countEntries();
        monA1.countEntries();
        monA2.countEntries();
        monA.addProcessingComment("\n\tResult of merging 2 dictionaries:");
        monA.addProcessingComment("\t" + monA.nEntries + " entries (" + monA1.nEntries + " U " + monA2.nEntries + ")");

        Dictionary monB = mergeMonols(monB1, monB2);
        String monBfn = monB1.fileName;
        monBfn = DicTools.removeExtension(monBfn);
        monB.fileName = monBfn + "-merged.dix";

        monB.countEntries();
        monB1.countEntries();
        monB2.countEntries();
        monB.addProcessingComment("\n\tResult of merging 2 dictionaries:");
        monB.addProcessingComment("\t" + monB.nEntries + " entries (" + monB1.nEntries + " U " + monB2.nEntries + ")");

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

        Alphabet alphabet = mergeAlphabetElement(bAB1.alphabet, bAB2.alphabet);
        dic.alphabet = alphabet;

        Sdefs sdefs = new Sdefs();
        sdefs = mergeSdefElements(bAB1.sdefs, bAB2.sdefs);
        dic.sdefs = sdefs;

        Pardefs pardefs1 = bAB1.pardefs;
        Pardefs pardefs2 = bAB2.pardefs;
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
        dic.pardefs = pardefs;

        for (Section section1 : bAB1.sections) {
            Section section2 = bAB2.getSection(section1.id);
            if (section2 != null) {
                Section section = mergeSectionElements(section1,
                        section2);
                dic.sections.add(section);
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
        Alphabet alphabet = mergeAlphabetElement(m1.alphabet, m2.alphabet);
        mon.alphabet = alphabet;

        Sdefs sdefs = mergeSdefElements(m1.sdefs, m2.sdefs);
        mon.sdefs = sdefs;

        Pardefs pardefs = mergePardefElements(m1.pardefs, m2.pardefs);
        mon.pardefs = pardefs;

        for (Section section1 : m1.sections) {
            Section section2 = m2.getSection(section1.id);
            if (section2 != null) {
                Section section = mergeSectionElements(section1, section2);
                mon.sections.add(section);
            } else {
                msg.err("There's no '" + section1.id + "' section in monolingual 2");
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

        System.err.println("Merging section '" + sectionE1.id + "'...");
        Section sectionElement = new Section();
        HashMap<String, E> eMap = new HashMap<String, E>();

        sectionElement.id = sectionE1.id;
        sectionElement.type = sectionE1.type;

        int duplicated = 0;

        //paradigmsToRemove = new HashMap<String, String>();

        ArrayList<E> elements1 = sectionE1.elements;
        System.err.println("  monolingual 1 (" + elements1.size() + " lemmas)");
        int fromSec1 = 0;
        for (E e1 : elements1) {
            //String e1Key = e1.lemmaAndCategory();
            String e1Key = e1.toString();
            if (!eMap.containsKey(e1Key)) {
                eMap.put(e1Key, e1);
                sectionElement.elements.add(e1);
                fromSec1++;
            } else {
                duplicated++;
            }
        }

        ArrayList<E> elements2 = sectionE2.elements;
        System.err.println("  monolingual 2 (" + elements2.size() + " lemmas)");
        int common = 0;
        int notin = 0;
        int fromSec2 = 0;
        boolean first = true;
        for (E e2 : elements2) {
            // String e2Key = e2.toStringNoParadigm();
            //String e2Key = e2.lemmaAndCategory();
            String e2Key = e2.toString();
            if (!eMap.containsKey(e2Key)) {
                notin++;
                msg.log("[" + notin + "] section 1 doesn't contain: " + e2Key);
                eMap.put(e2Key, e2);
                if (first) {
                    e2.prependCharacterData +="\n\n<!-- ====== HERE AND BELOW ARE ADDITIONS FROM SECOND FILE====== -->\n\n";
                    //e2.processingComments += "\n\n ====== HERE AND BELOW ARE ADDITIONS FROM SECOND FILE======\n\n";
                    first = false;
                }
                sectionElement.elements.add(e2);
                fromSec2++;
            } else {
                // System.err.println("'" + e2.lemma + "' already
                // exists.");
                common++;
                String parName2 = e2.getMainParadigmName();

                if (parName2 != null) {

                    E e1 = eMap.get(e2Key);
                    String parName1 = e1.getMainParadigmName();

                    if (!parName1.equals(parName2)) {
                        msg.log("Paradigms for <" + e1.lemma + "> : (" + parName1 + ", " + parName2 + ")");
                        // addParadigmToRemove(parName2);
                        e1.getFirstParadigm().addProcessingComment(
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
        System.err.println("  " + sectionElement.elements.size() + " lemmas in merged dictionary");

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

        for (Sdef sdef1 : sdefs1.elements) {
            String sdef1Key = sdef1.toString();
            if (!sdefMap.containsKey(sdef1Key)) {
                sdefMap.put(sdef1Key, sdef1);
                sdefs.elements.add(sdef1);
            }
        }

        for (Sdef sdef2 : sdefs2.elements) {
            String sdef2Key = sdef2.toString();
            if (!sdefMap.containsKey(sdef2Key)) {
                sdefMap.put(sdef2Key, sdef2);
                sdefs.elements.add(sdef2);
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
        alphabet.alphabet = alphabet1.alphabet;
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


        for (Pardef pardef1 : pardefs1.elements) {
            //System.err.println("Paradigm: " + pardef1.getName());

            pardefNameMap.put(pardef1.name, pardef1);
            pardefAllMap.put(pardef1.toString(), pardef1);
            pardefs.elements.add(pardef1);
        }

        boolean first = true;

        for (Pardef pardef2 : pardefs2.elements) {
            String pardef2Key = pardef2.toString();
            if (!pardefAllMap.containsKey(pardef2Key)) {
                System.err.println("Paradigm: " + pardef2.name);

                while (pardefNameMap.containsKey(pardef2.name)) {
                    // Rename
                    pardef2.name = pardef2.name+"___mergeTODO";
                    pardef2Key  = pardef2.toString();
                }

                if (first) {
                    pardef2.prependCharacterData +="\n\n<!-- ====== HERE AND BELOW ARE ADDITIONS FROM SECOND FILE====== -->\n\n";
                    first = false;
                }
                pardefNameMap.put(pardef2.name, pardef2);
                pardefAllMap.put(pardef2Key, pardef2);
                pardefs.elements.add(pardef2);
            }
        }
        return pardefs;
    }

    
    public void doMerge() {
        processArguments();
        actionMerge();
    }

    
    public void doMergeMorph() {
        processArgumentsMorph();
        mergeMorph();
    }

    
    public void doMergeBil() {
        processArgumentsBil();
        mergeBil();
    }

    
    public void actionMerge() {
        DicSet dicSet = merge();
        // setMerged(dicSet);
        Dictionary bil = dicSet.bil1;
        Dictionary monA = dicSet.mon1;
        Dictionary monB = dicSet.mon2;

        bil.printXMLToFile(bil.fileName,opt);
        monA.printXMLToFile(monA.fileName,opt);
        monB.printXMLToFile(monB.fileName,opt);
    }

    /**
     * 
     * @param arguments
     */
    private void processArguments() {
        int nArgs = arguments.length;

        String sDicMonA1, sDicMonA2, sDicMonB1, sDicMonB2;
        sDicMonA1 = sDicMonA2 = sDicMonB1 = sDicMonB2 = null;
        String sDicBilAB1, sDicBilAB2;
        sDicBilAB1 = sDicBilAB2 = null;
        boolean bilAB1Reverse, bilAB2Reverse;
        ;
        bilAB1Reverse = bilAB2Reverse = false;

        for (int i = 1; i < nArgs; i++) {
            String arg = arguments[i];
            if (arg.equals("-monA1")) {
                i++;
                arg = arguments[i];
                sDicMonA1 = arg;
                System.err.println("Monolingual A1:\t'" + sDicMonA1 + "'");
            }

            if (arg.equals("-monA2")) {
                i++;
                arg = arguments[i];
                sDicMonA2 = arg;
                System.err.println("Monolingual A2:\t '" + sDicMonA2 + "'");
            }

            if (arg.equals("-out")) {
                i++;
                arg = arguments[i];
                sOut = arg;
                System.err.println("Merged:\t'" + sOut + "'");
            }

            if (arg.equals("-monB")) {
                i++;
                arg = arguments[i];
                sDicMonB1 = arg;
                System.err.println("Monolingual B1:\t'" + sDicMonB1 + "'");
            }

            if (arg.equals("-monB2")) {
                i++;
                arg = arguments[i];
                sDicMonB2 = arg;
                System.err.println("Monolingual B2:\t'" + sDicMonB2 + "'");
            }

            if (arg.equals("-bilAB")) {
                i++;
                arg = arguments[i];
                if (arg.equals("-r")) {
                    bilAB1Reverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilAB1Reverse = false;
                    i++;
                }
                arg = arguments[i];
                sDicBilAB1 = arg;
                System.err.println("Bilingual AB1:\t'" + sDicBilAB1 + "'");
            }

            if (arg.equals("-bilAB2")) {
                i++;
                arg = arguments[i];
                if (arg.equals("-r")) {
                    bilAB2Reverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilAB2Reverse = false;
                    i++;
                }
                arg = arguments[i];
                sDicBilAB2 = arg;
                System.err.println("Bilingual AB2:\t'" + sDicBilAB2 + "'");
            }

        }

        Dictionary bilAB1 = DicTools.readBilingual(sDicBilAB1, bilAB1Reverse);
        Dictionary monA1 = DicTools.readMonolingual(sDicMonA1);
        Dictionary monB1 = DicTools.readMonolingual(sDicMonB1);
        DicSet dicSet1 = new DicSet(bilAB1, monA1, monB1);
        //this.dicSet1 = dicSet1;
        this.bilAB1 =(bilAB1);
        this.monA1 = (monA1);
        this.monB1 = (monB1);

        bilAB2 = DicTools.readBilingual(sDicBilAB2, bilAB2Reverse);
        monA2 = DicTools.readMonolingual(sDicMonA2);
        monB2 = DicTools.readMonolingual(sDicMonB2);
        dicSet2 = new DicSet(bilAB2, monA2, monB2);

    }

    
    private void processArgumentsMorph() {
        int nArgs = arguments.length;

        String sDicMonA1, sDicMonA2;
        sDicMonA1 = sDicMonA2 = null;
        
        for (int i = 1; i < nArgs; i++) {
            String arg = arguments[i];
            if (arg.equals("-monA1")) {
                i++;
                arg = arguments[i];
                sDicMonA1 = arg;
                System.err.println("Monolingual A1:\t'" + sDicMonA1 + "'");
            }
            else
            if (arg.equals("-monA2")) {
                i++;
                arg = arguments[i];
                sDicMonA2 = arg;
                System.err.println("Monolingual A2:\t '" + sDicMonA2 + "'");
            }
            else
            if (arg.equals("-out")) {
                i++;
                arg = arguments[i];
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

    
    private void processArgumentsBil() {
        int nArgs = arguments.length;

        String sDicBilAB1, sDicBilAB2;
        sDicBilAB1 = sDicBilAB2 = null;
        boolean bilAB1Reverse, bilAB2Reverse;
        ;
        bilAB1Reverse = bilAB2Reverse = false;

        for (int i = 1; i < nArgs; i++) {
            String arg = arguments[i];
            if (arg.equals("-bilAB1")) {
                i++;
                arg = arguments[i];
                if (arg.equals("-r")) {
                    bilAB1Reverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilAB1Reverse = false;
                    i++;
                }
                arg = arguments[i];
                sDicBilAB1 = arg;
                System.err.println("Bilingual AB1:\t'" + sDicBilAB1 + "'");
            }

            if (arg.equals("-bilAB2")) {
                i++;
                arg = arguments[i];
                if (arg.equals("-r")) {
                    bilAB2Reverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilAB2Reverse = false;
                    i++;
                }
                arg = arguments[i];
                sDicBilAB2 = arg;
                System.err.println("Bilingual AB2:\t'" + sDicBilAB2 + "'");
            }

            if (arg.equals("-out")) {
                i++;
                arg = arguments[i];
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


    public void mergeMorph() {
        Dictionary morph = mergeMonols(monA1, monA2);
        morph.printXMLToFile(sOut,opt);
    }

    public void mergeMorph__OLD_which_sorts_output_but() {
        Dictionary morph = mergeMonols(monA1, monA2);
        DicSort dicSort = new DicSort(morph);
        Dictionary sorted = dicSort.sort();
        sorted.printXMLToFile(sOut,opt);
    }


    
    public void mergeBil() {
        Dictionary bil = mergeBils(bilAB1, bilAB2);
        DicSort dicSort = new DicSort(bil);
        Dictionary sorted = dicSort.sort();
        sorted.printXMLToFile(sOut,opt);
    }
}
