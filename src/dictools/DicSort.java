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

import dictools.xml.DictionaryReader;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.SectionElement;
import dics.elements.utils.EElementList;
import dics.elements.utils.Msg;
import dics.elements.utils.SElementList;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicSort  extends AbstractDictTool {

    /**
     * 
     */
    private DictionaryElement dic;
    /**
     * 
     */
    private boolean xinclude;
    /**
     * 
     */
    private String dicType;
    /**
     * 
     */
    private String out;

    /**
     * 
     * 
     */
    public DicSort() {
        setXinclude(false);
        msg.setLogFileName("sort.log");
    }

    /**
     * 
     * @param dic
     */
    public DicSort(DictionaryElement dic) {
        this.dic = dic;
        setXinclude(false);
        msg.setLogFileName("sort.log");
    }

    /**
     * 
     * @return Undefined        
     */
    public DictionaryElement sort() {
        DictionaryElement dicSorted = null;
        if (DictionaryElement.BIL.equals(dicType)) {
            dicSorted = sortBil();
        } else
        if (DictionaryElement.MONOL.equals(dicType)) {
            dicSorted = sortMon();
        } else throw new IllegalStateException(dicType);
        return dicSorted;
    }

    /**
     * 
     * 
     */
    public void actionSort() {
        DictionaryElement dicSorted = sort();
        dicSorted.printXML(out,getOpt());

    /*
    if (dicSorted != null) {
    dicSorted.setFolder(getOut() + "-includes");
    if (isXinclude()) {
    dicSorted.printXMLXInclude(out);
    } else {
    dicSorted.printXML(out);
    }
    }
     */
    }
    
    /**
     * 
     * 
     */
    public void doSort() {
        processArguments();
        actionSort();
    }

    /**
     * 
     * 
     */
    private void processArguments() {
        if (arguments[1].equals("-mon")) {
            dicType = DictionaryElement.MONOL;
        } else {
            dicType = DictionaryElement.BIL;
        }
        this.setXinclude(false);

        /*
        if (arguments[2].equals("-xinclude")) {
        setXinclude(true);
        System.out.println("xinclude mode");
        } else {
        setXinclude(false);
        }
         */

        DictionaryReader dicReader = new DictionaryReader(arguments[2]);
        DictionaryElement dic = dicReader.readDic();
        dic.setFileName(arguments[2]);
        dicReader = null;
        setDic(dic);
        out = arguments[3];

    /*
    if (getArguments()[4].equals("out.dix")) {
    out = DicTools.removeExtension(getArguments()[4]);
    out = out + "-sorted.dix";
    } else {
    out = getArguments()[4];
    }
     */

    }


    /**
     * @param dicFormatted
     *                the dicFormatted to set
     */
    private void setDic(DictionaryElement dic) {
        this.dic = dic;
    }

    /**
     * 
     * @return
     */
    private DictionaryElement sortBil() {
        int lrs = 0;
        int rls = 0;
        int n = 0;

        for (SectionElement section : dic.getSections()) {
            EElementList eList = section.getEElements();
            HashMap<String, EElementList> map = new HashMap<String, EElementList>();

            for (EElement e : eList) {
                n++;
                SElementList sList = e.getSElements("L");
                if (e.hasRestriction()) {
                    String r = e.getRestriction();
                    if (r.equals("LR")) {
                        lrs++;
                    }
                    if (r.equals("RL")) {
                        rls++;
                    }
                }
                String cat;
                if (sList != null) {
                    if (sList.size() > 0) {
                        cat = sList.get(0).getValue();
                    } else {
                        cat = "none";
                    }
                    EElementList l;
                    if (map.containsKey(cat)) {
                        l = map.get(cat);
                        l.add(e);
                        map.put(cat, l);
                    } else {
                        l = new EElementList();
                        l.add(e);
                        //System.out.println("category: " + cat);
                        map.put(cat, l);
                    }
                }
            }
            msg.log("lemmas: " + n);
            msg.log("LR: " + lrs);
            msg.log("RL: " + rls);

            Set keySet = map.keySet();
            Iterator it = keySet.iterator();

            EElementList listAll = new EElementList();
            while (it.hasNext()) {
                String cat = (String) it.next();
                EElementList list = map.get(cat);
                msg.log(cat + ": " + list.size());
                if (list.size() > 0) {
                    Collections.sort(list);
                    EElement eHead = list.get(0);
                    eHead.addProcessingComment("******************************");
                    eHead.addProcessingComment("(" + cat + ") group");
                    eHead.addProcessingComment("******************************");
                    listAll.addAll(list);
                }
            }
            section.setEElements(listAll);
        }
        return dic;
    }

    /**
     * 
     * @return Undefined         
     */
    private DictionaryElement sortMon() {
        int lrs = 0;
        int rls = 0;

        int n = 0;
        for (SectionElement section : dic.getSections()) {
            //if (section.getID().equals("main")) {
            EElementList eList = section.getEElements();
            HashMap<String, EElementList> map = new HashMap<String, EElementList>();

            for (EElement e : eList) {
                n++;
                String par = e.getParadigmValue();
                if (e.hasRestriction()) {
                    String r = e.getRestriction();
                    if (r.equals("LR")) {
                        lrs++;
                    }
                    if (r.equals("RL")) {
                        rls++;
                    }
                }

                String cat = null;
                if (par == null) {
                    cat = "none";
                } else {
                    String[] aux = par.split("__");
                    if (aux.length == 2) {
                        cat = aux[1];
                    } else {
                        cat = par;
                    }
                    cat = cat.replaceAll("/", "-");
                }
                if (cat != null) {
                    EElementList l;
                    if (map.containsKey(cat)) {
                        l = map.get(cat);
                        l.add(e);
                        map.put(cat, l);
                    } else {
                        l = new EElementList();
                        l.add(e);
                        map.put(cat, l);
                    }
                }

            }
            msg.log("lemmas: " + n);
            msg.log("LR: " + lrs);
            msg.log("RL: " + rls);

            Set keySet = map.keySet();
            Iterator it = keySet.iterator();

            EElementList listAll = new EElementList();
            String folder = "";
            if (isXinclude()) {
                folder = out + "-includes";
                boolean status = new File(folder).mkdir();
            }
            while (it.hasNext()) {
                DictionaryElement d = new DictionaryElement();
                SectionElement sec = new SectionElement();
                d.addSection(sec);

                String cat = (String) it.next();
                EElementList list = map.get(cat);
                msg.log(cat + ": " + list.size());
                if (isXinclude()) {
                    section.addXInclude("<xi:include xmlns:xi=\"http://www.w3.org/2001/XInclude\" href=\"" + folder + "/" + cat + ".dix\"/>");
                }
                if (list.size() > 0) {
                    Collections.sort(list);
                    EElement eHead = list.get(0);
                    eHead.addProcessingComment("******************************");
                    eHead.addProcessingComment("(" + cat + ") group");
                    eHead.addProcessingComment("******************************");
                    listAll.addAll(list);
                }
                sec.setEElements(list);
                if (isXinclude()) {
                    sec.printXMLXInclude(folder + "/" + cat + ".dix",getOpt());
                }

            }
            section.setEElements(listAll);
        //}
        }
        return dic;
    }

    /**
     * @return the dicType
     */
    public String getDicType() {
        return dicType;
    }

    /**
     * @param dicType
     *                the dicType to set
     */
    public void setDicType(String dicType) {
        this.dicType = dicType;
    }

    /**
     * @return the xinclude
     */
    private boolean isXinclude() {
        return xinclude;
    }

    /**
     * @param xinclude
     *                the xinclude to set
     */
    public void setXinclude(boolean xinclude) {
        this.xinclude = xinclude;
    }

    /**
     * @return the out
     */
    private String getOut() {
        return out;
    }

    /**
     * @return the dic
     */
    public DictionaryElement getDic() {
        return dic;
    }

    /**
     * 
     * @param out
     */
    public void setOut(String out) {
        this.out = out;
    }
}
