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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.SectionElement;
import dics.elements.utils.EElementList;
import dics.elements.utils.SElementList;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicSort {

    /**
         * 
         */
    private DictionaryElement dic;

    /**
         * 
         */
    private String[] arguments;

    /**
         * 
         */
    private final int BIL = 0;

    /**
         * 
         */
    private final int MON = 1;

    /**
         * 
         */
    private int dicType;

    /**
         * 
         * 
         */
    public DicSort() {

    }

    /**
         * 
         * @param dic
         */
    public DicSort(final DictionaryElement dic) {
	this.dic = dic;
    }

    /**
         * 
         * @return
         */
    public final DictionaryElement sort() {
	Collections.sort(dic.getEntries());
	return dic;
    }

    /**
         * 
         * 
         */
    public final void doSort() {
	this.processArguments();
	this.actionSort();
    }

    /**
         * 
         * 
         */
    private void processArguments() {
	if (arguments[1].equals("-mon")) {
	    this.dicType = MON;
	} else {
	    this.dicType = BIL;
	}
	DictionaryReader dicReader = new DictionaryReader(arguments[2]);
	DictionaryElement dic = dicReader.readDic();
	dicReader = null;
	setDic(dic);
    }

    /**
         * 
         * 
         */
    private final void actionSort() {
	DictionaryElement dicSorted = null;
	System.out.println("Dictype: " + dicType);
	if (this.dicType == BIL) {
	    dicSorted = sortBil();
	}

	if (this.dicType == MON) {
	    dicSorted = sortMon();
	}

	if (dicSorted != null) {
	    String formattedFileName = "sorted-dic.dix";
	    if (getArguments().length == 4) {
		if (getArguments()[3].equals("out.dix")) {
		    formattedFileName = DicTools
			    .removeExtension(getArguments()[3]);
		    formattedFileName = formattedFileName + "-sorted.dix";
		} else {
		    formattedFileName = getArguments()[3];
		}
		dicSorted.printXML(formattedFileName);
	    }
	}
    }

    /**
         * @return the dicFormatted
         */
    private final DictionaryElement getDic() {
	return dic;
    }

    /**
         * @param dicFormatted
         *                the dicFormatted to set
         */
    private final void setDic(DictionaryElement dic) {
	this.dic = dic;
    }

    /**
         * @return the arguments
         */
    public final String[] getArguments() {
	return arguments;
    }

    /**
         * @param arguments
         *                the arguments to set
         */
    public final void setArguments(String[] arguments) {
	this.arguments = arguments;
    }

    /**
         * 
         * 
         */
    private final DictionaryElement sortBil() {
	for (SectionElement section : dic.getSections()) {
	    EElementList eList = section.getEElements();
	    HashMap<String, EElementList> map = new HashMap<String, EElementList>();

	    for (EElement e : eList) {
		SElementList sList = e.getSElements("L");
		String cat;
		if (sList != null) {
		    if (sList.size() > 0) {
			cat = (String) sList.get(0).getValue();
		    } else {
			cat = "(none)";
		    }
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

	    Set keySet = map.keySet();
	    Iterator it = keySet.iterator();

	    EElementList listAll = new EElementList();
	    while (it.hasNext()) {
		String cat = (String) it.next();
		EElementList list = (EElementList) map.get(cat);
		if (list.size() > 0) {
		    Collections.sort(list);
		    EElement eHead = list.get(0);
		    eHead.addComments("******************************");
		    eHead.addComments("(" + cat + ") group");
		    eHead.addComments("******************************");
		    listAll.addAll(list);
		}
	    }
	    section.setEElements(listAll);

	}
	return dic;
    }

    /**
         * 
         * @return
         */
    private final DictionaryElement sortMon() {
	for (SectionElement section : dic.getSections()) {
	    if (section.getID().equals("main")) {
		EElementList eList = section.getEElements();
		HashMap<String, EElementList> map = new HashMap<String, EElementList>();

		for (EElement e : eList) {
		    String par = e.getParadigmValue();

		    String cat = null;
		    if (par == null) {
			cat = "(none)";
		    } else {
			String[] aux = par.split("__");
			if (aux.length == 2) {
			    cat = aux[1];
			} else {
			    cat = par;
			}
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

		Set keySet = map.keySet();
		Iterator it = keySet.iterator();

		EElementList listAll = new EElementList();
		while (it.hasNext()) {
		    String cat = (String) it.next();
		    EElementList list = (EElementList) map.get(cat);
		    if (list.size() > 0) {
			Collections.sort(list);
			EElement eHead = list.get(0);
			eHead.addComments("******************************");
			eHead.addComments("(" + cat + ") group");
			eHead.addComments("******************************");
			listAll.addAll(list);
		    }
		}
		section.setEElements(listAll);

	    }
	}
	return dic;
    }
}
