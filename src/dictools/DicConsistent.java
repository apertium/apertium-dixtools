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
import java.util.Iterator;
import java.util.Set;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.utils.DicOpts;
import dics.elements.utils.DicSet;
import dics.elements.utils.Msg;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicConsistent extends AbstractDictTool {

    /**
     * 
     */
    private Dictionary mon1;
    /**
     * 
     */
    private Dictionary mon2;
    /**
     * 
     */
    private Dictionary bil1;
    /**
     * 
     */
    private Dictionary bil2;
    /**
     * 
     */
    private HashMap<String, ArrayList<E>> commonA;
    /**
     * 
     */
    private HashMap<String, ArrayList<E>> commonC;
    /**
     * 
     */
    private HashMap<String, ArrayList<E>> differentA;
    /**
     * 
     */
    private HashMap<String, ArrayList<E>> differentC;
    /**
     * 
     */
    private String notCommonSuffix;
    /**
     * 
     */
    private DicSet dicSet;
    /**
     * 
     */
    private String outDir = "dix/";

    /**
     * 
     * 
     */
    public DicConsistent() {
    }

    /**
     * 
     * 
     */
    public DicConsistent(DicSet dicset) {
        setMon1(dicset.getMon1());
        setMon2(dicset.getMon2());
        setBil1(dicset.getBil1());
        setBil2(dicset.getBil2());
        differentA = new HashMap<String, ArrayList<E>>();
        differentC = new HashMap<String, ArrayList<E>>();
        commonA = new HashMap<String, ArrayList<E>>();
        commonC = new HashMap<String, ArrayList<E>>();
        setNotCommonSuffix("not-common-");
    }

    /**
     * 
     * 
     */
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
        HashMap<String, ArrayList<E>> bilABMap = DicTools.buildHash(getBil1().getEntries());
        HashMap<String, ArrayList<E>> bilBCMap = DicTools.buildHash(getBil2().getEntries());
        HashMap<String, ArrayList<E>> monAMap = DicTools.buildHashMon(getMon1().getEntries());
        HashMap<String, ArrayList<E>> monCMap = DicTools.buildHashMon(getMon2().getEntries());
        markCommonEntries(bilABMap, bilBCMap, monAMap, getCommonA(), getDifferentA());
        markCommonEntries(bilBCMap, bilABMap, monCMap, getCommonC(), getDifferentC());
    }

    /**
     * 
     */
    private void buildNotCommonDictionaries() {
        buildNotCommonDictionary(getBil1(),getOpt());
        buildNotCommonDictionary(getBil2(),getOpt());
        buildNotCommonDictionary(getMon1(),getOpt());
        buildNotCommonDictionary(getMon2(),getOpt());
    }

    /**
     * 
     * @param dic
     */
    private void buildNotCommonDictionary(Dictionary dic, DicOpts opt) {
        Dictionary dicNotCommon = new Dictionary(dic);
        String fnDic = dic.getFileName();

        File file = new File(fnDic);

        String fileName = file.getName();

        fnDic = fnDic.replaceAll("\\.dix", "");
        fnDic = fnDic.replaceAll("\\.metadix", "");
        fnDic = fnDic.replaceAll("/dics/", "/dix/");

        //dicNotCommon.printXML(fnDic + getNotCommonSuffix());
        dicNotCommon.printXML(this.getOutDir() + getNotCommonSuffix() + fileName, opt);
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
            e.setShared(true);
            String trad = e.getValue("R");
            String key = DicTools.clearTags(trad);
            ArrayList<E> monAList = mon.get(key);
            if (monAList != null) {
                for (E eMon : monAList) {
                    eMon.setShared(true);
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

    /**
     * 
     * 
     */
    private void removeNotShared() {
        getMon1().removeNotCommon();
        getMon2().removeNotCommon();
        getBil1().removeNotCommon();
        getBil2().removeNotCommon();
    }

    /**
     * 
     * @return Undefined
     */
    public HashMap<String, ArrayList<E>> getDifferentA() {
        return differentA;
    }

    /**
     * 
     * @return Undefined         */
    public HashMap<String, ArrayList<E>> getDifferentC() {
        return differentC;
    }

    /**
     * @return Undefined Undefined the bil1
     */
    public Dictionary getBil1() {
        return bil1;
    }

    /**
     * @param bil1
     *                the bil1 to set
     */
    public void setBil1(Dictionary bil1) {
        this.bil1 = bil1;
    }

    /**
     * @return Undefined Undefined the bil2
     */
    public Dictionary getBil2() {
        return bil2;
    }

    /**
     * @param bil2
     *                the bil2 to set
     */
    public void setBil2(Dictionary bil2) {
        this.bil2 = bil2;
    }

    /**
     * @return the mon1
     */
    public Dictionary getMon1() {
        return mon1;
    }

    /**
     * @param mon1
     *                the mon1 to set
     */
    private void setMon1(Dictionary mon1) {
        this.mon1 = mon1;
    }

    /**
     * @return the mon2
     */
    public Dictionary getMon2() {
        return mon2;
    }

    /**
     * @param mon2
     *                the mon2 to set
     */
    private void setMon2(Dictionary mon2) {
        this.mon2 = mon2;
    }

    /**
     * @return the commonA
     */
    public HashMap<String, ArrayList<E>> getCommonA() {
        return commonA;
    }

    /**
     * @return the commonC
     */
    public HashMap<String, ArrayList<E>> getCommonC() {
        return commonC;
    }

    /**
     * @return the notCommonSuffix
     */
    public String getNotCommonSuffix() {
        return notCommonSuffix;
    }

    /**
     * @param notCommonSuffix
     *                the notCommonSuffix to set
     */
    private void setNotCommonSuffix(String notCommonSuffix) {
        this.notCommonSuffix = notCommonSuffix;
    }

    /**
     * 
     * @param removeNotCommon
     * @return Undefined
     */
    private DicConsistent actionConsistent(String removeNotCommon) {
        DicConsistent dicConsistent = new DicConsistent(getDicSet());
        dicConsistent.makeConsistentDictionaries(removeNotCommon);
        dicSet.printXML("consistent",getOpt());
        return dicConsistent;
    }

    /**
     * 
     * 
     */
    private void processArguments() {
        int nArgs = getArguments().length;
        String sDicMonA, sDicMonC, sDicBilAB, sDicBilBC;
        sDicMonA = sDicMonC = sDicBilAB = sDicBilBC = null;
        boolean bilABReverse, bilBCReverse;
        bilABReverse = bilBCReverse = false;

        for (int i = 1; i < nArgs; i++) {
            String arg = getArguments()[i];
            if (arg.equals("-monA")) {
                i++;
                arg = getArguments()[i];
                sDicMonA = arg;
                msg.err("Monolingual A: '" + sDicMonA + "'");
            }

            if (arg.equals("-monC")) {
                i++;
                arg = getArguments()[i];
                sDicMonC = arg;
                msg.err("Monolingual C: '" + sDicMonC + "'");
            }

            if (arg.equals("-bilAB")) {
                i++;
                arg = getArguments()[i];
                if (arg.equals("-r")) {
                    bilABReverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilABReverse = false;
                    i++;
                }

                arg = getArguments()[i];
                sDicBilAB = arg;
                msg.err("Bilingual A-B: '" + sDicBilAB + "'");
            }

            if (arg.equals("-bilBC")) {
                i++;
                arg = getArguments()[i];

                if (arg.equals("-r")) {
                    bilBCReverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilBCReverse = false;
                    i++;
                }
                arg = getArguments()[i];
                sDicBilBC = arg;
                msg.err("Bilingual B-C: '" + sDicBilBC + "'");
            }

        }

        Dictionary bil1 = DicTools.readBilingual(sDicBilAB, bilABReverse);
        Dictionary bil2 = DicTools.readBilingual(sDicBilBC, bilBCReverse);
        Dictionary mon1 = DicTools.readMonolingual(sDicMonA);
        Dictionary mon2 = DicTools.readMonolingual(sDicMonC);

        DicSet dicSet = new DicSet(mon1, bil1, mon2, bil2);
        setDicSet(dicSet);
    }

    /**
     * @return the dicSet
     */
    private DicSet getDicSet() {
        return dicSet;
    }

    /**
     * @param dicSet
     *                the dicSet to set
     */
    private void setDicSet(DicSet dicSet) {
        this.dicSet = dicSet;
    }

    /**
     * 
     */
    public void setOutDir(String path) {
        this.outDir = path;
    }

    /**
     * 
     */
    public String getOutDir() {
        return this.outDir;
    }
}
