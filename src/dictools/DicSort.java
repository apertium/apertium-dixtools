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
import dics.elements.dtd.EElement.EElementComparator;
import dics.elements.dtd.SectionElement;
import dics.elements.utils.EElementList;
import dics.elements.utils.Msg;
import dics.elements.utils.SElementList;
import java.util.LinkedHashMap;
import java.util.Map;
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
    private String out;

    public boolean sortAccordingToRightSide = false;
    private boolean ignoreCaseWhenSorting = false;

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
        dicSorted = sortDictionaryAccordingToCategories();
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


    Map<String, String> groupsOfCategoriesToBeSortedTogether = new LinkedHashMap<String, String>();

    private void addGroupOfCategoriesToBeSortedTogether(String commaseparatedList) {
        String[] arr = commaseparatedList.split(",");
        for (String e:arr) groupsOfCategoriesToBeSortedTogether.put(e.trim(), commaseparatedList);
    }

    private String findCategory(EElement e) {
        String par=e.getMainParadigmName();
        String cat=null;
        if (par==null) {
            SElementList sel=e.getSElements("R");
            if (sel!=null&&sel.size()>0) {
                cat=sel.get(0).getValue()+"_symbol";
            } else {
                cat="(none)";
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
        int i=1;
        while (i<arguments.length) {
            String a = arguments[i].toLowerCase();
            if (a.startsWith("-mon")) msg.err("Old option "+a+" is ignored");
            else if (a.startsWith("-bil")) msg.err("Old option "+a+" is ignored");
            else if (a.startsWith("-case") || a.startsWith("-ignorecase")) {
                ignoreCaseWhenSorting = true;
            } else if (a.startsWith("-right") || a.startsWith("-sortright")) {
                sortAccordingToRightSide = true;
            } else if (a.startsWith("-group")) {
                String group = arguments[++i];
                addGroupOfCategoriesToBeSortedTogether(group);
            } else break; // not recognized, must be file name then
            i++;
        }

        DictionaryReader dicReader = new DictionaryReader(arguments[i]);
        DictionaryElement dic = dicReader.readDic();
        dic.setFileName(arguments[i]);
        dicReader = null;
        setDic(dic);
        out = arguments[i+1];
    }


    /**
     * @param dicFormatted
     *                the dicFormatted to set
     */
    private void setDic(DictionaryElement dic) {
        this.dic = dic;
    }


    private EElementList sortSectionAccordingToCategory(HashMap<String, EElementList> map, SectionElement section) {
        if (map.size()>1) {
            msg.err("section \""+section.getID()+ "\" categories: "+map.keySet().toString().replaceAll("[ \\[\\]]", ""));
        }

        EElementList listAll=new EElementList();
        EElementList categoriesWithOnlyOne=new EElementList();

        EElementComparator eElementComparator = new EElementComparator(sortAccordingToRightSide ? "R":"L");
        eElementComparator.ignoreCase = ignoreCaseWhenSorting;

        Iterator it=map.keySet().iterator();
        while (it.hasNext()) {
            String cat=(String) it.next();
            EElementList list=map.get(cat);
            msg.log(cat+": "+list.size());
            if (list.size()>1) {
                Collections.sort(list, eElementComparator);
                EElement eHead=list.get(0);
                eHead.addProcessingComment("******************************");
                eHead.addProcessingComment("    group "+cat);
                eHead.addProcessingComment("******************************");
                listAll.addAll(list);
            } else {
                categoriesWithOnlyOne.addAll(list);
            }
        }
        if (listAll.size()>0 && categoriesWithOnlyOne.size()>0) {
            EElement eHead=categoriesWithOnlyOne.get(0);
            eHead.addProcessingComment("******************************");
            eHead.addProcessingComment("    group(s) with only one element");
            eHead.addProcessingComment("******************************");
        }
        listAll.addAll(categoriesWithOnlyOne);
        return listAll;
    }


    
    private DictionaryElement sortDictionaryAccordingToCategories() {
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
                String catGrp = groupsOfCategoriesToBeSortedTogether.get(cat);
                if (catGrp != null) cat = catGrp;
                EElementList l=getEElementListForCat(map, cat);
                l.add(e);

            }
            msg.log("lemmas: " + n);
            msg.log("LR: " + lrs);
            msg.log("RL: " + rls);

            EElementList listAll=sortSectionAccordingToCategory(map, section);
            section.setEElements(listAll);
        }
        msg.err("(categories are kept order according to appearance in source file,  so you can reorder by");
        msg.err("  putting entries in the top. Categories with only one element will be put in the end)");

        return dic;
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
