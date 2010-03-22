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
 * You should have received author copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package dictools.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dictools.utils.DictionaryReader;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicTools {

    /**
     * 
     * @param value
     * @return Undefined         */
    public static String clearTags(String value) {
        if (value != null) {
            value = value.replaceAll("<g>", "");
            value = value.replaceAll("<j/>", "");
            value = value.replaceAll("<a/>", "");
            value = value.replaceAll("</g>", "");
            value = value.replaceAll("<b/>", " ");
        }
        return value;
    }

    /**
     * 
     * @param entries
     * @return Undefined         */
    public static HashMap<String, ArrayList<E>> buildHash(ArrayList<E> entries) {
    	return buildHash(entries, "L");
    }

    /**
     * 
     * @param entries
     * @param side
     * @return Undefined         */
    public static HashMap<String, ArrayList<E>> buildHash(ArrayList<E> entries, String side) {
        HashMap<String, ArrayList<E>> entriesMap = new HashMap<String, ArrayList<E>>();
        for (E e : entries) {
            String value = e.getValue(side);
            String key = DicTools.clearTags(value);

            if (entriesMap.containsKey(key)) {
                ArrayList<E> eList = entriesMap.get(key);
                eList.add(e);
                entriesMap.put(key, eList);
            } else {
                ArrayList<E> eList = new ArrayList<E>();
                eList.add(e);
                entriesMap.put(key, eList);
            }
        }
        return entriesMap;
    }

    /**
     * 
     * @param entries
     * @return Undefined
     */
    public static HashMap<String, ArrayList<E>> buildHashMon(ArrayList<E> entries) {
        HashMap<String, ArrayList<E>> entriesMap = new HashMap<String, ArrayList<E>>();

        for (E e : entries) {
            String lemma = e.lemma;
            if (lemma == null) {
                //lemma = e.getValueNoTags();
                lemma = e.getValue("L");
            }
            String key = DicTools.clearTags(lemma);
            if (entriesMap.containsKey(key)) {
                ArrayList<E> eList = entriesMap.get(key);
                eList.add(e);
                entriesMap.put(key, eList);
            } else {
                ArrayList<E> eList = new ArrayList<E>();
                eList.add(e);
                entriesMap.put(key, eList);
            }
        }
        return entriesMap;
    }

    /**
     * @param name
     * @return Undefined         
     */
    public static String reverseDicName(String name) {
        String[] st = DicTools.getSourceAndTarget(name);
        String newFileName = st[2] + "apertium-" + st[1] + "-" + st[0] + "." + st[1] + "-" + st[0] + ".dix";
        return newFileName;
    }

    /**
     * 
     * @param name
     * @return Undefined         */
    public static String[] getSourceAndTarget(String name) {
        String[] st = new String[3];
        String langCode = "[a-z]+";
        String langPair = langCode + "\\-" + langCode;
        String str = "";

        String[] parts = name.split("apertium-");
        for (String element : parts) {
            str = element;
        }

        String txt = "apertium-" + langPair + "." + langPair + ".dix";
        
        
        String[] paths = str.split(txt);
        
        st[2] = paths[0];
        int k = 0;
        String[] parts2 = str.split("[.]");

        if (paths.length < parts2.length-1 && parts2.length-1>0) {
          System.err.println("WARNING: file name "+str+" does not match the pattern " + txt);
        }

        
        for (int i = 0; i < (parts2.length - 1); i++) {
            k = 0;
            str = parts2[i];
            String[] parts3 = str.split("-");
            for (String element : parts3) {
                str = parts[i];
                if (k == 0) {
                    st[0] = element;
                }
                if (k == 1) {
                    st[1] = element;
                }
                k++;
            }
        }
        return st;
    }

    /**
     * 
     * @param str
     * @return Undefined
     */
    public static String removeExtension(String str) {
        String head = str.replaceAll("\\.dix", "");
        head = head.replaceAll("\\.metadix", "");
        head = head.replaceAll("/dics/", "/dix/");
        return head;
    }

    /**
     * 
     * @param bilAB
     * @param monA
     * @param monB
     */
    public static ArrayList<E>[] makeConsistent(Dictionary bilAB, Dictionary monA, Dictionary monB) {
        ArrayList<E>[] consistentMons = new ArrayList[2];
        ArrayList<E> elements = bilAB.getEntriesInMainSection();

        HashMap<String, ArrayList<E>> bilABMapL = DicTools.buildHash(elements, "L");
        HashMap<String, ArrayList<E>> bilABMapR = DicTools.buildHash(elements, "R");

        HashMap<String, ArrayList<E>> monAMap = DicTools.buildHashMon(monA.getEntriesInMainSection());
        HashMap<String, ArrayList<E>> monBMap = DicTools.buildHashMon(monB.getEntriesInMainSection());

        ArrayList<E> monAConsistent = DicTools.makeConsistent(bilABMapL, monAMap);

        ArrayList<E> monBConsistent = DicTools.makeConsistent(bilABMapR, monBMap);

        Collections.sort(monAConsistent, E.eElementComparatorL);
        consistentMons[0] = monAConsistent;
        Collections.sort(monBConsistent, E.eElementComparatorL);
        consistentMons[1] = monBConsistent;

        return consistentMons;
    }

    /**
     * 
     * @param bilABMap
     * @param side
     * @param monMap
     */
    private static ArrayList<E> makeConsistent(HashMap<String, ArrayList<E>> bilABMap, HashMap<String, ArrayList<E>> monMap) {
        ArrayList<E> consistentMon = new ArrayList<E>();
        Set<String> keySet = monMap.keySet();
        Iterator<String> it = keySet.iterator();

        while (it.hasNext()) {
            String key = it.next();
            ArrayList<E> eList = monMap.get(key);
            for (E e : eList) {
                String lemma = e.lemma;
                // in case no lemma is defined
                if (lemma == null) {
                    lemma = e.getValueNoTags();
                }
                lemma = DicTools.clearTags(lemma);

                if (bilABMap.containsKey(lemma)) {
                    consistentMon.add(e);
                }
            }
        }
        return consistentMon;
    }

    /**
     * 
     * @return Undefined         
     */
    public static HashMap<String, String> getSdefDescriptions() {
        HashMap<String, String> d = new HashMap<String, String>();

        d.put("aa", "adjective-adjective");
        d.put("acr", "acronym");
        d.put("adj", "adjective");
        d.put("al", "others");
        d.put("an", "adjective-noun");
        d.put("ant", "(antroponimo)");
        d.put("adv", "adverb");
        d.put("atn", "?????");
        d.put("cm", "?????");
        d.put("cni", "conditional");
        d.put("cnjadv", "adverbial conjunction");
        d.put("cnjcoo", "coordinative conjunction");
        d.put("cnjsub", "subordinate conjunction");
        d.put("def", "definite");
        d.put("dem", "demostrative");
        d.put("det", "determiner");
        d.put("detnt", "neutral determiner");
        d.put("enc", "(enclítico)");
        d.put("f", "femenin");
        d.put("fti", "future (futuro de indicativo))");
        d.put("fts", "future (subjunctive) (futuro de subjuntivo)");
        d.put("ger", "gerund");
        d.put("ifi", "past (pretérito perfecto o indefinido)");
        d.put("ij", "interjection");
        d.put("imp", "imperative");
        d.put("ind", "indefinite");
        d.put("inf", "infinitive");
        d.put("itg", "(interrogativo)");
        d.put("loc", "(locativo)");
        d.put("lpar", "([");
        d.put("lquest", "¿");
        d.put("m", "masculine");
        d.put("mf", "masculine-femenin");
        d.put("n", "noun");
        d.put("nn", "noun-noun");
        d.put("np", "proper noun");
        d.put("nt", "neuter");
        d.put("num", "numeral");
        d.put("p1", "1p");
        d.put("p2", "2p");
        d.put("p3", "3p");
        d.put("pii", "(pretérito imperfecto de indicativo)");
        d.put("pis", "(pretérito imperfecto de subjuntivo)");
        d.put("pl", "plural");
        d.put("pos", "possessive");
        d.put("pp", "participle");
        d.put("pr", "preposition");
        d.put("preadv", "preadverb");
        d.put("predet", "predeterminer");
        d.put("pri", "present simple (presente de indicativo)");
        d.put("prn", "pronoun");
        d.put("pro", "(proclítico)");
        d.put("prs", "(presente de subjuntivo)");
        d.put("ref", "reflexive");
        d.put("rel", "relative");
        d.put("rpar", ")]");
        d.put("sent", ".?;:!");
        d.put("sg", "singular");
        d.put("sp", "singular-plural");
        d.put("sup", "superlative");
        d.put("tn", "tonic");
        d.put("vaux", "auxiliar verb");
        d.put("vbhaver", "'haver' verb");
        d.put("vblex", "lexical verb");
        d.put("vbmod", "modal verb");
        d.put("vbser", "'ser' verb");
        return d;
    }

    /**
     * 
     * @param sMon
     * @return Undefined         */
    public static Dictionary readMonolingual(String sMonFilename) {
        DictionaryReader dicReader = new DictionaryReader(sMonFilename);
        Dictionary mon = dicReader.readDic();
        mon.fileName = sMonFilename;
        return mon;
    }

    /**
     * 
     * @param sBil
     * @param reverse
     * @return Undefined         */
    public static Dictionary readBilingual(String sBilFilename,  boolean reverse) {
        DictionaryReader dicReaderBil = new DictionaryReader(sBilFilename);
        Dictionary bil = dicReaderBil.readDic();
        bil.fileName = sBilFilename;
        bil.type = Dictionary.BIL;

        if (reverse) {
            bil.reverse();
            String reverseFileName = DicTools.reverseDicName(sBilFilename);
            //bil.printXMLToFile(reverseFileName);
            bil.fileName = reverseFileName;
        }

        String[] st = DicTools.getSourceAndTarget(bil.fileName);
        bil.leftLanguage = st[0];
        bil.rightLanguage = st[1];
        dicReaderBil = null;
        return bil;
    }
}
