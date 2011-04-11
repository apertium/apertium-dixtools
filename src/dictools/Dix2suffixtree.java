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
import dics.elements.dtd.Pardef;
import dics.elements.dtd.S;
import dics.elements.dtd.Section;
import dictools.guessparadigm.suffixtree.AffixSequence;
import dictools.guessparadigm.suffixtree.Pair;
import dictools.guessparadigm.suffixtree.SuffixTree;
import dictools.guessparadigm.paradigms.ParadigmRelatedWords;
import dictools.guessparadigm.paradigms.DictionaryWord;
import dictools.guessparadigm.paradigms.Paradigm;
import dictools.guessparadigm.questions.SortedSetOfCandidates;
import dictools.guessparadigm.paradigms.SurfaceFormsSet;
import dictools.utils.Msg;
import dictools.utils.DictionaryReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class Dix2suffixtree {

    public Dictionary dic;

    public String action;

    protected Msg msg = Msg.inst();

    private SuffixTree st;

    static private Set<String> closedposcat;
    
    public Dix2suffixtree() {
        st=new SuffixTree();
        String[] arrayclosedcat={"vbmod","vbser","vbhaver","adv","preadv","det","prn","pr","numeral"};
        closedposcat=new LinkedHashSet<String>();
        closedposcat.addAll(Arrays.asList(arrayclosedcat));
    }

    public void getListOfLexicalForms() {
        for (Section section : dic.sections)
            BuildSuffixTree(section.elements);
    }

    static private boolean isEmtpyParadigm(Pardef paradigm){
        boolean exit=true;
        for(E e: paradigm.elements){
            for(DixElement de: e.children){
                if(de instanceof P){
                    if(!((P)de).l.getValueNoTags().equals("")){
                        exit=false;
                        break;
                    }
                }
            }
        }
        return exit;
    }

    static private boolean isClosedCategoryParadigm(Pardef paradigm){
        boolean exit=false;
        for(E e: paradigm.elements){
            for(DixElement de: e.children){
                if(de instanceof P){
                    for(S symbol: ((P)de).r.getSymbols()){
                        if(closedposcat.contains(symbol.name)){
                            exit=true;
                            break;
                        }
                    }
                }
            }
        }
        return exit;
    }

    private void BuildSuffixTree(List<E> elements){
        int count=0;
        for(E element: elements){
            if(element.lemma!=null && !element.lemma.contains(" ") && !element.lemma.contains("_")){
                 List<AffixSequence> baseForms=new LinkedList<AffixSequence>();
                 baseForms.add(new AffixSequence());
                 List<AffixSequence> lexForms=processListE(element, baseForms);
                 while(lexForms.size()>0){
                     //System.err.print("\r"+count+" words added");
                     AffixSequence as=lexForms.get(0);
                     if(!as.getSequence().contains(" ")){
                         count++;
                         //as.getSuffixStartingPosition().remove(0);
                         /*System.out.println(as.getSequence()+"\t"+as.getSuffixStartingPosition().size());
                         for(Pair<Integer,String> i: as.getSuffixStartingPosition())
                             System.out.println(i.getFirst()+"\t"+i.getSecond());*/
                         //for(int index=0;index<as.getSuffixStartingPosition().size();index++){
                         for(Pair<Integer,String> affix: as.getSuffixStartingPosition()){
                             Pardef pardef=dic.pardefs.getParadigmDefinition(affix.getSecond());
                             if(isClosedCategoryParadigm(pardef) || isEmtpyParadigm(pardef))
                                 break;
                             st.AddWord(as.getSequence(), affix.getFirst(), affix.getSecond());
                         }
                         as.SetSuffixes(null);
                     }
                     lexForms.remove(0);
                 }
            }
        }
    }

    private List<AffixSequence> processListE(E element, List<AffixSequence> currentLexicalForms)
    {
         List<AffixSequence> listgeneratedByElement=new LinkedList<AffixSequence>();
         listgeneratedByElement.add(new AffixSequence());
         for (DixElement e: element.children)
         {
             if( e instanceof I){
                 for(AffixSequence b: listgeneratedByElement){
                     if(!e.getValueNoTags().equals("")){
                         b.addSteam(e.getValueNoTags());
                         //b.addParadigmName(paradigmname);
                         //System.out.println(e.getValueNoTags()+": "+paradigmname);
                     }
                 }
             }
             else if (e instanceof P){
                 for(AffixSequence b: listgeneratedByElement){
                     b.addSteam(((P)e).l.getValueNoTags());
                     //b.addParadigmName(paradigmname);
                     //System.out.println("\t"+((P)e).l.getValueNoTags()+": "+paradigmname);
                 }
             }
             else if (e instanceof Par)
             {
                 List<E> parElements=dic.pardefs.getParadigmDefinition(((Par)e).name).elements;
                 if(parElements.size()>0){
                     List<AffixSequence> resultList=processListE(parElements, listgeneratedByElement, ((Par)e).name);
                     listgeneratedByElement.clear();
                     for(AffixSequence r: resultList){
                        listgeneratedByElement.add(r);
                        //System.out.println("\t"+r.getSequence()+": "+((Par)e).name);
                     }
                 }
             }
         }
         //Combine lists
         List<AffixSequence> finalList = new LinkedList<AffixSequence>();
         for(AffixSequence lexHead: currentLexicalForms){
             for(AffixSequence lexTail: listgeneratedByElement){
                 AffixSequence lextmp=new AffixSequence(lexHead);
                 lextmp.addAffixSequence(lexTail);
                 finalList.add(lextmp);
                 //System.out.println(lextmp.getSequence());
             }
         }
         return finalList;
    }

    private List<AffixSequence> processListE(List<E> elements, List<AffixSequence> currentLexicalForms, String paradigmname)
    {
         List<AffixSequence> localList=new LinkedList<AffixSequence>();
         for(E element: elements){
             List<AffixSequence> listgeneratedByElement=new LinkedList<AffixSequence>();
             listgeneratedByElement.add(new AffixSequence());
             for (DixElement e: element.children)
             {
                 if( e instanceof I){
                     for(AffixSequence b: listgeneratedByElement){
                         if(!e.getValueNoTags().equals(""))
                             b.addAffix(e.getValueNoTags(),  paradigmname);
                     }
                 }
                 else if (e instanceof P){
                     for(AffixSequence b: listgeneratedByElement)
                         b.addAffix(((P)e).l.getValueNoTags(),paradigmname);
                 }
                 else if (e instanceof Par)
                 {
                     List<E> parElements=dic.pardefs.getParadigmDefinition(((Par)e).name).elements;
                     if(parElements.size()>0){
                         List<AffixSequence> resultList=processListE(parElements, listgeneratedByElement, ((Par)e).name);
                         listgeneratedByElement.clear();
                         for(AffixSequence r: resultList){
                            listgeneratedByElement.add(r);
                         }
                     }
                 }
             }
             for(AffixSequence b: listgeneratedByElement){
                    localList.add(b);
             }
         }
         //Combine lists
         List<AffixSequence> finalList = new LinkedList<AffixSequence>();
         for(AffixSequence lexHead: currentLexicalForms){
             for(AffixSequence lexTail: localList){
                 AffixSequence lextmp=new AffixSequence(lexHead);
                 lextmp.addAffixSequence(lexTail);
                 finalList.add(lextmp);
             }
        }
        return finalList;
    }

    /**
     * 
     * @param fileName
     */
    public void setDic(String fileName) {
        DictionaryReader dicReader = new DictionaryReader(fileName);
        this.dic = dicReader.readDic();
    }

    public SortedSetOfCandidates CeckNewWord(String string, String wordlistpath) {
        Map<String,Integer> wordlist=new HashMap<String, Integer>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(wordlistpath));
            wordlist=ReadWordFile(br);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        Set<Pair<Paradigm,String>> result=st.SegmentWord(string, dic);
        SortedSetOfCandidates candidates=new SortedSetOfCandidates();
        for(Pair<Paradigm,String> p: result){
            Paradigm par=p.getFirst();
            if(par.getSuffixes().size()>0){
                ParadigmRelatedWords plf=new ParadigmRelatedWords(par);
                plf.BuildProfiles(dic, wordlist);
                DictionaryWord lf=new DictionaryWord(p.getSecond());
                lf.setProfile(par, wordlist);
                plf.AddWord(lf);
                SurfaceFormsSet sfs=new SurfaceFormsSet(p.getSecond(), par, plf);
                //System.out.println(plf.getPercentil(lf)+"\t"+plf.getSurfaceFormAparition(lf, 0.1)+"\t"+paradigm.getFirst()+"\t"+paradigm.getSecond());
                candidates.addCandidate(plf.getSurfaceFormAparition(lf, 0.1), sfs);
            }
        }
        return candidates;
    }

    private Map<String,Integer> ReadWordFile(BufferedReader br){
        Map<String,Integer> exit=new HashMap<String, Integer>();
        String line=null;
        try {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(" ");
                exit.put(values[1], Integer.parseInt(values[0]));
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return exit;
    }

    public SuffixTree getSuffixTree(){
        return st;
    }
}
