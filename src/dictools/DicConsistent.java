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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.utils.DicOpts;
import dics.elements.utils.DicSet;
import dics.elements.utils.DicTools;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicConsistent extends AbstractDictTool {

    
    public Dictionary mon1;
    
    public Dictionary mon2;
    
    public Dictionary bil1;
    
    public Dictionary bil2;
    
    public HashMap<String, ArrayList<E>> commonA;
    
    public HashMap<String, ArrayList<E>> commonC;
    
    public HashMap<String, ArrayList<E>> differentA;
    
    public HashMap<String, ArrayList<E>> differentC;
    
    public String notCommonSuffix;
    
    public DicSet dicSet;
    
    public String outDir = "dix/";

    
    public DicConsistent() {
    }

    
    public DicConsistent(DicSet dicset) {
        this.mon1 = dicset.mon1;
        this.mon2 = dicset.mon2;
        this.bil1 = dicset.bil1;
        this.bil2 = dicset.bil2;
        differentA = new HashMap<String, ArrayList<E>>();
        differentC = new HashMap<String, ArrayList<E>>();
        commonA = new HashMap<String, ArrayList<E>>();
        commonC = new HashMap<String, ArrayList<E>>();
        this.notCommonSuffix = "not-common-";
    }

    
    public void doConsistent() {
        processArguments();
        actionConsistent("yes");
    }

    /**
     * 
     * @param entries1
     * @param entries2
     */
    private void compare() {
        HashMap<String, ArrayList<E>> bilABMap = DicTools.buildHash(bil1.getEntriesInMainSection());
        HashMap<String, ArrayList<E>> bilBCMap = DicTools.buildHash(bil2.getEntriesInMainSection());
        HashMap<String, ArrayList<E>> monAMap = DicTools.buildHashMon(mon1.getEntriesInMainSection());
        HashMap<String, ArrayList<E>> monCMap = DicTools.buildHashMon(mon2.getEntriesInMainSection());
        markCommonEntries(bilABMap, bilBCMap, monAMap, commonA, differentA);
        markCommonEntries(bilBCMap, bilABMap, monCMap, commonC, differentC);
    }

    
    private void buildNotCommonDictionaries() {
        buildNotCommonDictionary(bil1,opt);
        buildNotCommonDictionary(bil2,opt);
        buildNotCommonDictionary(mon1,opt);
        buildNotCommonDictionary(mon2,opt);
    }

    /**
     * 
     * @param dic
     */
    private void buildNotCommonDictionary(Dictionary dic, DicOpts opt) {
        Dictionary dicNotCommon = new Dictionary(dic);
        String fnDic = dic.fileName;

        File file = new File(fnDic);

        String fileName = file.getName();

        fnDic = fnDic.replaceAll("\\.dix", "");
        fnDic = fnDic.replaceAll("\\.metadix", "");
        fnDic = fnDic.replaceAll("/dics/", "/dix/");

        //dicNotCommon.printXML(fnDic + getNotCommonSuffix());
        dicNotCommon.printXML(this.outDir + notCommonSuffix + fileName, opt);
        dicNotCommon = null;

    }

    /**
     * 
     * @param bil1Map
     * @param bil2Map
     * @param monMap
     * @param common
     * @param different
     * @param comm
     */
    private void markCommonEntries(HashMap<String, ArrayList<E>> bilABMap,
            HashMap<String, ArrayList<E>> bilBCMap, HashMap<String, ArrayList<E>> monAMap,
            HashMap<String, ArrayList<E>> commonA, HashMap<String, ArrayList<E>> differentA) {

        Set<String> keysBilAB = bilABMap.keySet();
        Iterator<String> itBilAB = keysBilAB.iterator();

        while (itBilAB.hasNext()) {
            String str = itBilAB.next();
            ArrayList<E> eList = bilABMap.get(str);
            if (bilBCMap.containsKey(str)) {
                commonA.put(str, eList);
                markShared(commonA, str, monAMap);
            } else {
                differentA.put(str, eList);
            }
        }
    }

    /**
     * 
     * @param commonA
     * @param str
     * @param monA
     */
    private void markShared(HashMap<String, ArrayList<E>> common, String str,
            HashMap<String, ArrayList<E>> mon) {
        String k = DicTools.clearTags(str);
        ArrayList<E> list = common.get(k);
        for (E e : list) {
            e.shared=true;
            String trad = e.getValue("R");
            String key = DicTools.clearTags(trad);
            ArrayList<E> monAList = mon.get(key);
            if (monAList != null) {
                for (E eMon : monAList) {
                    eMon.shared=true;
                }
            }
        }
    }

    /**
     * 
     * @param removeNotShared
     */
    public void makeConsistentDictionaries(String removeNotShared) {
        compare();
        buildNotCommonDictionaries();
        if (removeNotShared.equals("yes")) {
            removeNotShared();
        }
    }

    
    private void removeNotShared() {
        mon1.removeNotCommon();
        mon2.removeNotCommon();
        bil1.removeNotCommon();
        bil2.removeNotCommon();
    }

    /**
     * 
     * @param removeNotCommon
     * @return Undefined
     */
    private DicConsistent actionConsistent(String removeNotCommon) {
        DicConsistent dicConsistent = new DicConsistent(dicSet);
        dicConsistent.makeConsistentDictionaries(removeNotCommon);
        dicSet.printXML("consistent",opt);
        return dicConsistent;
    }

    
    private void processArguments() {
        int nArgs = arguments.length;
        String sDicMonA, sDicMonC, sDicBilAB, sDicBilBC;
        sDicMonA = sDicMonC = sDicBilAB = sDicBilBC = null;
        boolean bilABReverse, bilBCReverse;
        bilABReverse = bilBCReverse = false;

        for (int i = 1; i < nArgs; i++) {
            String arg = arguments[i];
            if (arg.equals("-monA")) {
                i++;
                arg = arguments[i];
                sDicMonA = arg;
                msg.err("Monolingual A: '" + sDicMonA + "'");
            }

            if (arg.equals("-monC")) {
                i++;
                arg = arguments[i];
                sDicMonC = arg;
                msg.err("Monolingual C: '" + sDicMonC + "'");
            }

            if (arg.equals("-bilAB")) {
                i++;
                arg = arguments[i];
                if (arg.equals("-r")) {
                    bilABReverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilABReverse = false;
                    i++;
                }

                arg = arguments[i];
                sDicBilAB = arg;
                msg.err("Bilingual A-B: '" + sDicBilAB + "'");
            }

            if (arg.equals("-bilBC")) {
                i++;
                arg = arguments[i];

                if (arg.equals("-r")) {
                    bilBCReverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilBCReverse = false;
                    i++;
                }
                arg = arguments[i];
                sDicBilBC = arg;
                msg.err("Bilingual B-C: '" + sDicBilBC + "'");
            }

        }

        Dictionary bil1 = DicTools.readBilingual(sDicBilAB, bilABReverse);
        Dictionary bil2 = DicTools.readBilingual(sDicBilBC, bilBCReverse);
        Dictionary mon1 = DicTools.readMonolingual(sDicMonA);
        Dictionary mon2 = DicTools.readMonolingual(sDicMonC);

        DicSet dicSet = new DicSet(mon1, bil1, mon2, bil2);
        this.dicSet = dicSet;
    }
}
