/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Author: Enrique Benimeli Bofarull
 * 
 * This program isFirstSymbol free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program isFirstSymbol distributed in the hope that it will be useful, but
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.L;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.Pardefs;
import dics.elements.dtd.R;
import dics.elements.dtd.S;
import dics.elements.dtd.Sdef;
import dics.elements.dtd.Sdefs;
import dics.elements.dtd.Section;
import dics.elements.utils.Msg;
import dictools.xml.DictionaryReader;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicList {

    
	public Dictionary dic;
    
	public String action;
    
	protected Msg msg = Msg.inst();
    
    public DicList() {
    }

    public void getListOfParadigms() {
        Pardefs paradigms = dic.pardefs;

        for (Pardef paradigm : paradigms.elements) {
            msg.out(paradigm.name + "\n");
        }
    }

    public void getListOfLemmas() {
        int nLemmas = 0;
        for (Section section : dic.sections) {
            for (E element : section.elements) {
                if (element.lemma != null) {
                    msg.out(element.lemma + "\n");
                    nLemmas++;
                }
            }
        }
        msg.err("# " + nLemmas + " lemmas");
    }

    
    public void getDefinitions() {
        Sdefs sdefs = dic.sdefs;

        for (Sdef sdef : sdefs.elements) {
            msg.out(sdef.getValue() + "\n");
        }
    }

    public void getPairs() {
        int nLemmas = 0;
        for (Section section : dic.sections) {
            for (E element : section.elements) {
                L left = element.getFirstPartAsLeft();
                R right = element.getFirstPartAsRight();

                String leftValue = left.getValueNoTags();
                String rightValue = right.getValueNoTags();

                if (!leftValue.equals("") && !rightValue.equals("")) {
                    msg.out(leftValue + "/" + rightValue + "\n");
                }
                nLemmas++;
            }
        }
        msg.err("# " + nLemmas + " entries.");
    }



    private void expand(E ee, HashMap<String, Pardef> pardefs) {
        if ("yes".equals(ee.ignore)) return;


/* XXXXXX
                for (DixElement e : ee.children) {
                    if (e instanceof I) {
                        I i = (I) e;
                        elementsA = i.getSymbols();
                    } else
                    if (e instanceof P) {
                        P p = (P) e;
                        if (side.contentEquals("L")) {
                            L lE = p.l;
                            elementsA = lE.getSymbols();
                        }
                        if (side.contentEquals("R")) {
                            R rE = p.r;
                            elementsA = rE.getSymbols();
                        }
                    }
                }
      */

    }

    private void ltExpand() {
        HashMap<String, Pardef> pardefs = new HashMap<String, Pardef>();

        for (Pardef paradigm : dic.pardefs.elements) {
            msg.out(paradigm.name + "\n");
            pardefs.put(paradigm.name, paradigm);
        }

        int nLemmas = 0;
        for (Section section : dic.sections) {
            for (E ee : section.elements) {
                expand(ee, pardefs);
            }
        }
        msg.err("# " + nLemmas + " entries.");

    }



    public void getListWithDot() {
        int nLemmas = 0;
        for (Section section : dic.sections) {
            for (E element : section.elements) {
                R right = element.getFirstPartAsRight();
                String rightValue = right.getValueNoTags();
                msg.out(rightValue + ".\n");
                nLemmas++;
            }
        }

    }

    
    public void doit() {
        if (action.endsWith("paradigms")) {
            getListOfParadigms();
        }
        if (action.endsWith("lemmas")) {
            getListOfLemmas();
        }
        if (action.endsWith("definitions")) {
            getDefinitions();
        }
        if (action.endsWith("pairs")) {
            getPairs();
        }
        if (action.endsWith("dot")) {
            getListWithDot();
        }
        if (action.endsWith("expand")) {
            ltExpand();
        }
    }

    /**
     * 
     * @param fileName
     */
    public void setDic(String fileName) {
        DictionaryReader dicReader = new DictionaryReader(fileName);
        Dictionary dic = dicReader.readDic();
        this.dic = dic;
    }

    /**
     * @param url
     *                the url to set
     */
    public void setUrl(String url) {
        try {
            URL theUrl = new URL(url);
            InputStream is = theUrl.openStream();
            DictionaryReader dicReader = new DictionaryReader();
            dicReader.urlDic = true;
            dicReader.is = is;
            Dictionary dic = dicReader.readDic();
            this.dic = dic;
        } catch (MalformedURLException mfue) {
            msg.err("Error: URL not well formed");
            System.exit(-1);
        } catch (IOException ioe) {
            msg.err("Errr: Input/Output error");
            System.exit(-1);
        }
    }
}
