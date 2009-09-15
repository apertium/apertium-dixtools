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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.S;
import dics.elements.dtd.Section;
import dics.elements.dtd.E.EElementComparator;
import dictools.xml.DictionaryReader;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicSort  extends AbstractDictTool {

    /**
     * 
     */
    private Dictionary dic;
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
    public DicSort(Dictionary dic) {
        this.dic = dic;
        msg.setLogFileName("sort.log");
    }

    /**
     * 
     * @return Undefined        
     */
    public Dictionary sort() {
        Dictionary dicSorted = null;
        dicSorted = sortDictionaryAccordingToCategories();
        return dicSorted;
    }

    /**
     * 
     * 
     */
    public void actionSort() {
        Dictionary dicSorted = sort();
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

    private String findCategory(E e) {
        String par=e.getMainParadigmName();
        String cat=null;
        if (par==null) {
            ArrayList<S> sel=e.getSymbols("R");
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

    private ArrayList<E> getElementListForCat(HashMap<String, ArrayList<E>> map, String cat) {
        ArrayList<E> l;
        if (map.containsKey(cat)) {
            l=map.get(cat);
        } else {
            l=new ArrayList<E>();
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
        Dictionary dic = dicReader.readDic();
        dic.setFileName(arguments[i]);
        dicReader = null;
        setDic(dic);
        out = arguments[i+1];
    }


    /**
     * @param dicFormatted
     *                the dicFormatted to set
     */
    private void setDic(Dictionary dic) {
        this.dic = dic;
    }


    private ArrayList<E> sortSectionAccordingToCategory(HashMap<String, ArrayList<E>> map, Section section) {
        if (map.size()>1) {
            msg.err("section \""+section.getID()+ "\" categories: "+map.keySet().toString().replaceAll("[ \\[\\]]", ""));
        }

        ArrayList<E> listAll=new ArrayList<E>();
        ArrayList<E> categoriesWithOnlyOne=new ArrayList<E>();

        EElementComparator eElementComparator = new EElementComparator(sortAccordingToRightSide ? "R":"L");
        eElementComparator.ignoreCase = ignoreCaseWhenSorting;

        Iterator it=map.keySet().iterator();
        while (it.hasNext()) {
            String cat=(String) it.next();
            ArrayList<E> list=map.get(cat);
            msg.log(cat+": "+list.size());
            if (list.size()>1) {
                Collections.sort(list, eElementComparator);
                E eHead=list.get(0);
                eHead.addProcessingComment("******************************");
                eHead.addProcessingComment("    group "+cat);
                eHead.addProcessingComment("******************************");
                listAll.addAll(list);
            } else {
                categoriesWithOnlyOne.addAll(list);
            }
        }
        if (listAll.size()>0 && categoriesWithOnlyOne.size()>0) {
            E eHead=categoriesWithOnlyOne.get(0);
            eHead.addProcessingComment("******************************");
            eHead.addProcessingComment("    group(s) with only one element");
            eHead.addProcessingComment("******************************");
        }
        listAll.addAll(categoriesWithOnlyOne);
        return listAll;
    }


    
    private Dictionary sortDictionaryAccordingToCategories() {
        for (Section section : dic.getSections()) {
            int lrs = 0;
            int rls = 0;
            int n = 0;
            HashMap<String, ArrayList<E>> map = new LinkedHashMap<String, ArrayList<E>>();

            for (E e : section.getEElements()) {
                n++;
                if (e.hasRestriction()) {
                    String r = e.restriction;
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
                ArrayList<E> l=getElementListForCat(map, cat);
                l.add(e);

            }
            msg.log("lemmas: " + n);
            msg.log("LR: " + lrs);
            msg.log("RL: " + rls);

            ArrayList<E> listAll=sortSectionAccordingToCategory(map, section);
            section.setEElements(listAll);
        }
        msg.err("(categories are kept order according to appearance in source file,  so you can reorder by");
        msg.err("  putting entries in the top. Categories with only one element will be put in the end)");

        return dic;
    }

    /**
     * @return the dic
     */
    public Dictionary getDic() {
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
