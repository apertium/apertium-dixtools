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

import dics.elements.utils.SElementList;
import dictools.xml.DictionaryReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.Element;
import dics.elements.dtd.IElement;
import dics.elements.dtd.LElement;
import dics.elements.dtd.PElement;
import dics.elements.dtd.PardefElement;
import dics.elements.dtd.PardefsElement;
import dics.elements.dtd.RElement;
import dics.elements.dtd.SdefElement;
import dics.elements.dtd.SdefsElement;
import dics.elements.dtd.SectionElement;
import dics.elements.utils.Msg;
import java.util.HashMap;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicList {

    /**
     * 
     */
    private DictionaryElement dic;
    /**
     * 
     */
    private String action;
    /**
     * 
     */
    private boolean urlDic;
    /**
     * 
     */
    private String url;

      protected Msg msg = new Msg();
    private SElementList elementsA;

    public DicList(String fileName) {
        DictionaryReader dicReader = new DictionaryReader(fileName);
        DictionaryElement d = dicReader.readDic();
        setDic(d);
    }

    public DicList() {
    }

    public void getListOfParadigms() {
        DictionaryElement dic = getDic();
        PardefsElement paradigms = dic.getPardefsElement();

        for (PardefElement paradigm : paradigms.getPardefElements()) {
            msg.out(paradigm.getName() + "\n");
        }
    }

    public void getListOfLemmas() {
        DictionaryElement dic = getDic();

        int nLemmas = 0;
        for (SectionElement section : dic.getSections()) {
            for (EElement element : section.getEElements()) {
                if (element.getLemma() != null) {
                    msg.out(element.getLemma() + "\n");
                    nLemmas++;
                }
            }
        }
        msg.err("# " + nLemmas + " lemmas");
    }

    /**
     * 
     * 
     */
    public void getDefinitions() {
        DictionaryElement dic = getDic();

        SdefsElement sdefs = dic.getSdefs();

        for (SdefElement sdef : sdefs.getSdefsElements()) {
            msg.out(sdef.getValue() + "\n");
        }
    }

    public void getPairs() {
        DictionaryElement dic = getDic();

        int nLemmas = 0;
        for (SectionElement section : dic.getSections()) {
            for (EElement element : section.getEElements()) {
                LElement left = element.getLeft();
                RElement right = element.getRight();

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



    private void expand(EElement ee, HashMap<String, PardefElement> pardefs) {
        if ("yes".equals(ee.getIgnore())) return;


/* XXXXXX
                for (Element e : ee.getChildren()) {
                    if (e instanceof IElement) {
                        IElement i = (IElement) e;
                        elementsA = i.getSElements();
                    } else
                    if (e instanceof PElement) {
                        PElement p = (PElement) e;
                        if (side.contentEquals("L")) {
                            LElement lE = p.getL();
                            elementsA = lE.getSElements();
                        }
                        if (side.contentEquals("R")) {
                            RElement rE = p.getR();
                            elementsA = rE.getSElements();
                        }
                    }
                }
      */

    }

    private void ltExpand() {
        DictionaryElement dic = getDic();
        HashMap<String, PardefElement> pardefs = new HashMap<String, PardefElement>();

        for (PardefElement paradigm : dic.getPardefsElement().getPardefElements()) {
            msg.out(paradigm.getName() + "\n");
            pardefs.put(paradigm.getName(), paradigm);
        }

        int nLemmas = 0;
        for (SectionElement section : dic.getSections()) {
            for (EElement ee : section.getEElements()) {
                expand(ee, pardefs);
            }
        }
        msg.err("# " + nLemmas + " entries.");

    }



    public void getListWithDot() {
        DictionaryElement dic = getDic();

        int nLemmas = 0;
        for (SectionElement section : dic.getSections()) {
            for (EElement element : section.getEElements()) {
                RElement right = element.getRight();
                String rightValue = right.getValueNoTags();
                msg.out(rightValue + ".\n");
                nLemmas++;
            }
        }

    }

    /**
     * 
     * 
     */
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
     * @return the dic
     */
    public DictionaryElement getDic() {
        return dic;
    }

    /**
     * @param dic
     *                the dic to set
     */
    public void setDic(DictionaryElement dic) {
        this.dic = dic;
    }

    /**
     * 
     * @param fileName
     */
    public void setDic(String fileName) {
        DictionaryReader dicReader = new DictionaryReader(fileName);
        DictionaryElement dic = dicReader.readDic();
        setDic(dic);
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action
     *                the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the urlDic
     */
    public boolean isUrlDic() {
        return urlDic;
    }

    /**
     * @param urlDic
     *                the urlDic to set
     */
    public void setUrlDic(boolean urlDic) {
        this.urlDic = urlDic;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *                the url to set
     */
    public void setUrl(String url) {
        this.url = url;
        try {
            URL theUrl = new URL(url);
            InputStream is = theUrl.openStream();
            DictionaryReader dicReader = new DictionaryReader();
            dicReader.setUrlDic(true);
            dicReader.setIs(is);
            DictionaryElement dic = dicReader.readDic();
            setDic(dic);
        } catch (MalformedURLException mfue) {
            msg.err("Error: URL not well formed");
            System.exit(-1);
        } catch (IOException ioe) {
            msg.err("Errr: Input/Output error");
            System.exit(-1);
        }
    }
}
