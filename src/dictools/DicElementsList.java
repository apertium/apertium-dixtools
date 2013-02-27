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

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.E;
import dics.elements.dtd.I;
import dics.elements.dtd.P;
import dics.elements.dtd.Par;
import dics.elements.dtd.S;
import dictools.utils.DictionaryReader;
import dictools.utils.Msg;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Enrique Benimeli Bofarull
 *
 */
public class DicElementsList {

    public Dictionary dic;
    public String action;
    public String paradigm;
    public String stem;
    protected Msg msg = Msg.inst();

    public DicElementsList() {
    }

    private List<String> processListE() {
        List<StringBuilder> listgeneratedByElement = new LinkedList<StringBuilder>();
        listgeneratedByElement.add(new StringBuilder(""));

        List<E> parElements = dic.pardefs.getParadigmDefinition(paradigm).elements;
        List<String> leftList = processListELeft(parElements, listgeneratedByElement);
        List<String> rightList = processListERight(parElements, listgeneratedByElement, true);

        List<String> finalList = new LinkedList<String>();
        for (int i = 0; i < leftList.size(); i++) {
            finalList.add(stem + leftList.get(i) + ":" + stem + rightList.get(i));
        }
        return finalList;
    }

    private List<String> processListELeft(List<E> elements, List<StringBuilder> currentLexicalForms) {

        List<String> localList = new LinkedList<String>();
        for (E element : elements) {
            List<StringBuilder> listgeneratedByElement = new LinkedList<StringBuilder>();
            listgeneratedByElement.add(new StringBuilder(""));
            for (DixElement e : element.children) {
                if (e instanceof I) {
                    for (StringBuilder b : listgeneratedByElement) {
                        b.append(e.getValueNoTags());
                    }
                } else if (e instanceof P) {
                    for (StringBuilder b : listgeneratedByElement) {
                        b.append(((P) e).l.getValueNoTags());
                    }
                } else if (e instanceof Par) {
                    List<E> parElements = dic.pardefs.getParadigmDefinition(((Par) e).name).elements;
                    //msg.err("Paradigm "+((Par)e).name+" has "+parElements.size()+" elements");
                    List<String> resultList = processListELeft(parElements, listgeneratedByElement);
                    listgeneratedByElement.clear();
                    for (String r : resultList) {
                        listgeneratedByElement.add(new StringBuilder(r));
                    }
                }
            }
            for (StringBuilder b : listgeneratedByElement) {
                localList.add(b.toString());
            }
        }

        //Combine lists
        List<String> finalList = new LinkedList<String>();
        for (StringBuilder lexHead : currentLexicalForms) {
            for (String lexTail : localList) {
                finalList.add(lexHead + lexTail);
            }
        }

        return finalList;
    }

    private List<String> processListERight(List<E> elements, List<StringBuilder> currentLexicalForms, boolean firstcall) {

        List<String> localList = new LinkedList<String>();
        for (E element : elements) {
            List<StringBuilder> listgeneratedByElement = new LinkedList<StringBuilder>();
            listgeneratedByElement.add(new StringBuilder(""));
            for (DixElement e : element.children) {
                if (e instanceof I) {
                    for (StringBuilder b : listgeneratedByElement) {
                        if (!firstcall) {
                            b.append("+");
                        }
                        b.append(e.getValueNoTags());
                    }
                } else if (e instanceof P) {
                    for (StringBuilder b : listgeneratedByElement) {
                        if (!firstcall) {
                            b.append("+");
                        }
                        b.append(((P) e).r.getValueNoTags());
                        for (DixElement s : ((P) e).r.children) {
                            if (s instanceof S) {
                                b.append("<");
                                b.append(((S) s).name);
                                b.append(">");
                            }
                        }
                    }
                } else if (e instanceof Par) {
                    List<E> parElements = dic.pardefs.getParadigmDefinition(((Par) e).name).elements;
                    //msg.err("Paradigm "+((Par)e).name+" has "+parElements.size()+" elements");
                    List<String> resultList = processListERight(parElements, listgeneratedByElement, false);
                    listgeneratedByElement.clear();
                    for (String r : resultList) {
                        listgeneratedByElement.add(new StringBuilder(r));
                    }
                }
            }
            for (StringBuilder b : listgeneratedByElement) {
                localList.add(b.toString());
            }
        }

        //Combine lists
        List<String> finalList = new LinkedList<String>();
        for (StringBuilder lexHead : currentLexicalForms) {
            for (String lexTail : localList) {
                finalList.add(lexHead + lexTail);
            }
        }

        return finalList;
    }

    private void expandElement() {
        List<String> formlist = processListE();
        for (String form : formlist) {
            System.out.println(form);
        }
    }

    public static List<String> expandElement(Dictionary dic, String stem, String paradigm) {
        DicElementsList dicElementsList = new DicElementsList();
        dicElementsList.dic = dic;
        dicElementsList.stem = stem;
        dicElementsList.paradigm = paradigm;

        return dicElementsList.processListE();
    }

    public void doit() {
        if (action.endsWith("element")) {
            expandElement();
        }
    }

    /**
     *
     * @param fileName
     */
    public void setDic(String fileName) {
        DictionaryReader dicReader = new DictionaryReader(fileName);
        this.dic = dicReader.readDic();

    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        try {
            URL theUrl = new URL(url);
            InputStream is = theUrl.openStream();
            DictionaryReader dicReader = new DictionaryReader();
            dicReader.urlDic = true;
            dicReader.is = is;
            this.dic = dicReader.readDic();
        } catch (MalformedURLException mfue) {
            msg.err("Error: URL not well formed");
            System.exit(-1);
        } catch (IOException ioe) {
            msg.err("Errr: Input/Output error");
            System.exit(-1);
        }
    }
}
