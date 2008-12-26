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

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.utils.DicOpts;
import dics.elements.utils.DicSet;
import dics.elements.utils.EElementList;
import dics.elements.utils.EElementMap;
import dics.elements.utils.Msg;
import java.io.File;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicConsistent extends AbstractDictTool {

    /**
     * 
     */
    private DictionaryElement mon1;
    /**
     * 
     */
    private DictionaryElement mon2;
    /**
     * 
     */
    private DictionaryElement bil1;
    /**
     * 
     */
    private DictionaryElement bil2;
    /**
     * 
     */
    private EElementMap commonA;
    /**
     * 
     */
    private EElementMap commonC;
    /**
     * 
     */
    private EElementMap differentA;
    /**
     * 
     */
    private EElementMap differentC;
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
        differentA = new EElementMap();
        differentC = new EElementMap();
        commonA = new EElementMap();
        commonC = new EElementMap();
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
        EElementMap bilABMap = DicTools.buildHash(getBil1().getEntries());
        EElementMap bilBCMap = DicTools.buildHash(getBil2().getEntries());
        EElementMap monAMap = DicTools.buildHashMon(getMon1().getEntries());
        EElementMap monCMap = DicTools.buildHashMon(getMon2().getEntries());
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
    private void buildNotCommonDictionary(DictionaryElement dic, DicOpts opt) {
        DictionaryElement dicNotCommon = new DictionaryElement(dic);
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
    private void markCommonEntries(EElementMap bilABMap,
            EElementMap bilBCMap, EElementMap monAMap,
            EElementMap commonA, EElementMap differentA) {

        Set<String> keysBilAB = bilABMap.keySet();
        Iterator<String> itBilAB = keysBilAB.iterator();

        while (itBilAB.hasNext()) {
            String str = itBilAB.next();
            EElementList eList = bilABMap.get(str);
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
    private void markShared(EElementMap common, String str,
            EElementMap mon) {
        String k = DicTools.clearTags(str);
        EElementList list = common.get(k);
        for (EElement e : list) {
            e.setShared(true);
            String trad = e.getValue("R");
            String key = DicTools.clearTags(trad);
            EElementList monAList = mon.get(key);
            if (monAList != null) {
                for (EElement eMon : monAList) {
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
    public EElementMap getDifferentA() {
        return differentA;
    }

    /**
     * 
     * @return Undefined         */
    public EElementMap getDifferentC() {
        return differentC;
    }

    /**
     * @return Undefined Undefined the bil1
     */
    public DictionaryElement getBil1() {
        return bil1;
    }

    /**
     * @param bil1
     *                the bil1 to set
     */
    public void setBil1(DictionaryElement bil1) {
        this.bil1 = bil1;
    }

    /**
     * @return Undefined Undefined the bil2
     */
    public DictionaryElement getBil2() {
        return bil2;
    }

    /**
     * @param bil2
     *                the bil2 to set
     */
    public void setBil2(DictionaryElement bil2) {
        this.bil2 = bil2;
    }

    /**
     * @return the mon1
     */
    public DictionaryElement getMon1() {
        return mon1;
    }

    /**
     * @param mon1
     *                the mon1 to set
     */
    private void setMon1(DictionaryElement mon1) {
        this.mon1 = mon1;
    }

    /**
     * @return the mon2
     */
    public DictionaryElement getMon2() {
        return mon2;
    }

    /**
     * @param mon2
     *                the mon2 to set
     */
    private void setMon2(DictionaryElement mon2) {
        this.mon2 = mon2;
    }

    /**
     * @return the commonA
     */
    public EElementMap getCommonA() {
        return commonA;
    }

    /**
     * @return the commonC
     */
    public EElementMap getCommonC() {
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

        DictionaryElement bil1 = DicTools.readBilingual(sDicBilAB, bilABReverse);
        DictionaryElement bil2 = DicTools.readBilingual(sDicBilBC, bilBCReverse);
        DictionaryElement mon1 = DicTools.readMonolingual(sDicMonA);
        DictionaryElement mon2 = DicTools.readMonolingual(sDicMonC);

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
