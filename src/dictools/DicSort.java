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
import java.util.LinkedHashMap;
import java.util.TreeSet;

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
        msg.setLogFileName("sort.log");
    }

    /**
     * 
     * @param dic
     */
    public DicSort(DictionaryElement dic) {
        this.dic = dic;
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
    }
    
    /**
     * 
     * 
     */
    public void doSort() {
        processArguments();
        actionSort();
    }

    private String findCategory(EElement e) {
        String par=e.getMainParadigmName();
        String cat=null;
        if (par==null) {
            SElementList sel=e.getSElements("R");
            if (sel!=null&&sel.size()>0) {
                cat=sel.get(0).getValue()+" symbol";
            } else {
                cat="none";
            }
        } else {
            String[] aux=par.split("__");
            if (aux.length>=2) {
                cat=aux[1];
            } else {
                cat=par;
            }
            cat=cat.replaceAll("/", "-");
        }
        return cat;
    }

    private EElementList getEElementListForCat(HashMap<String, EElementList> map, String cat) {
        EElementList l;
        if (map.containsKey(cat)) {
            l=map.get(cat);
        } else {
            l=new EElementList();
            map.put(cat, l);
        }
        return l;
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

        DictionaryReader dicReader = new DictionaryReader(arguments[2]);
        DictionaryElement dic = dicReader.readDic();
        dic.setFileName(arguments[2]);
        dicReader = null;
        setDic(dic);
        out = arguments[3];

    }


    /**
     * @param dicFormatted
     *                the dicFormatted to set
     */
    private void setDic(DictionaryElement dic) {
        this.dic = dic;
    }


    private EElementList sortElementsAccordingToCategory(HashMap<String, EElementList> map, SectionElement section) {
        if (map.size()>1) {
            msg.err("section \""+section.getID()+ "\" categories: "+map.keySet());
        }

        EElementList listAll=new EElementList();
        EElementList categoriesWithOnlyOne=new EElementList();
        Iterator it=map.keySet().iterator();
        while (it.hasNext()) {
            String cat=(String) it.next();
            EElementList list=map.get(cat);
            msg.log(cat+": "+list.size());
            if (list.size()>1) {
                Collections.sort(list);
                EElement eHead=list.get(0);
                eHead.addProcessingComment("******************************");
                eHead.addProcessingComment("    ("+cat+") group");
                eHead.addProcessingComment("******************************");
                listAll.addAll(list);
            } else {
                categoriesWithOnlyOne.addAll(list);
            }
        }
        if (categoriesWithOnlyOne.size()>1 && listAll.size()>0) {
            EElement eHead=categoriesWithOnlyOne.get(0);
            eHead.addProcessingComment("******************************");
            eHead.addProcessingComment("    group(s) with only one element");
            eHead.addProcessingComment("******************************");
        }
        listAll.addAll(categoriesWithOnlyOne);
        return listAll;
    }


    /**
     * 
     * @return
     */
    private DictionaryElement sortBil() {

        for (SectionElement section : dic.getSections()) {
            HashMap<String, EElementList> map = new LinkedHashMap<String, EElementList>();
            int lrs = 0;
            int rls = 0;
            int n = 0;

            for (EElement e : section.getEElements()) {
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
                /*
                String cat;
                if (sList != null) {
                    if (sList.size() > 0) {
                        cat = sList.get(0).getValue();
                    } else {
                        cat = "none";
                    }
                    if (cat==null) cat = "null";
                }*/
                String cat=findCategory(e);
                EElementList l=getEElementListForCat(map, cat);
                l.add(e);
            }
            msg.log("lemmas: " + n);
            msg.log("LR: " + lrs);
            msg.log("RL: " + rls);

            EElementList listAll=sortElementsAccordingToCategory(map, section);
            section.setEElements(listAll);
        }
        msg.err("(categories are kept order according to appearance in source file,  so you can reorder by");
        msg.err("  putting entries in the top. Categories with only one element will be put in the end)");
        return dic;
    }

    /**
     * 
     * @return Undefined         
     */
    private DictionaryElement sortMon() {
        for (SectionElement section : dic.getSections()) {
            int lrs = 0;
            int rls = 0;
            int n = 0;
            HashMap<String, EElementList> map = new LinkedHashMap<String, EElementList>();

            for (EElement e : section.getEElements()) {
                n++;
                if (e.hasRestriction()) {
                    String r = e.getRestriction();
                    if (r.equals("LR")) {
                        lrs++;
                    }
                    if (r.equals("RL")) {
                        rls++;
                    }
                }
                String cat=findCategory(e);

                EElementList l=getEElementListForCat(map, cat);
                l.add(e);

            }
            msg.log("lemmas: " + n);
            msg.log("LR: " + lrs);
            msg.log("RL: " + rls);

            EElementList listAll=sortElementsAccordingToCategory(map, section);
            section.setEElements(listAll);
        }
        msg.err("(categories are kept order according to appearance in source file,  so you can reorder by");
        msg.err("  putting entries in the top. Categories with only one element will be put in the end)");

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
