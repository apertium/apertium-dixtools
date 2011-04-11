/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dictools.guessparadigm.suffixtree;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.E;
import dics.elements.dtd.P;
import dics.elements.dtd.Par;
import dictools.guessparadigm.paradigms.Paradigm;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author miquel
 */
public class SuffixTree implements Serializable{
    public Node rootnode;

    static private HashMap<Character,Character> chars=new HashMap<Character,Character>();

    public SuffixTree(){
        rootnode=new Node();
    }

    static public Character GetCharObject(char character){
        Character c=chars.get(character);
        if(c==null){
            c=new Character(character);
            chars.put(c,c);
            return c;
        }
        else
            return c;
    }

    public void AddWord(String word, int startingsuffixpos, String paradigm){
        if(startingsuffixpos==word.length()){
            rootnode.addParadigmName(paradigm);
            rootnode.InsertWord(word, word.length()-1, false);
        }
        else
            rootnode.InsertWord(word, word.length()-1, startingsuffixpos, paradigm, false);
    }

    public void Print(){
        if(rootnode.getChildren()!=null){
            for(char character: rootnode.getChildren().keySet()){
                Node n=rootnode.getChild(character);
                if(n.isStartingsuffix()){
                    System.out.println("+--"+character);
                    if(n.getChildren()!=null)
                        Print(character+"","|  +--", n);
                }
                else if(n.getChildren()!=null)
                     Print(character+"","", n);
                else
                    System.out.println("+--"+character);
            }
        }
    }

    private void Print(String prefix, String treemark, Node node){
        for(char character: node.getChildren().keySet()){
            Node n=node.getChild(character);
            if(n.isStartingsuffix()){
                System.out.println(treemark+prefix+character);
                if(n.getChildren()!=null)
                    Print(prefix+character,("|  "+treemark), n);
            }
            else if(n.getChildren()!=null)
                Print(prefix+character,(treemark), n);
            else
                System.out.println(treemark+prefix+character);
        }
    }

    public Set<Pair<Paradigm,String>> SegmentWord(String word, Dictionary dic) {
        Set<Pair<Paradigm,String>> exit=new LinkedHashSet<Pair<Paradigm,String>>();
        if(this.rootnode.getParadigmName()!=null){
            for(String s: this.rootnode.getParadigmName()){
                Paradigm paradigm=new Paradigm(dic.pardefs.getParadigmDefinition(s), dic);
                Pair<Paradigm,String> pair=new Pair<Paradigm, String>(paradigm, word);
                exit.add(pair);
            }
        }
        SegmentWord(word, word.length()-1, exit, this.rootnode, dic);
        return exit;
    }

    private void SegmentWord(String word, int currentposition, Set<Pair<Paradigm,String>> steamparadigm, Node currentnode, Dictionary dic) {
        if(currentposition>=0){
            Node node=currentnode.getChild(word.charAt(currentposition));
            if(node!=null){
                SegmentWord(word,currentposition-1,steamparadigm,node,dic);
                if(node.isStartingsuffix() && node.getParadigmName()!=null){
                    for(String s: node.getParadigmName()){
                        String steam=word.substring(0,currentposition);
                        //System.out.println(word.substring(0, currentposition)+"/"+word.charAt(currentposition)+": "+s);
                        /*Inflections inf=new Inflections(steam,s);
                        Set<StringBuilder> inflected_words=GenerateAllInflexions(steam,s,dic);
                        for(StringBuilder inflected_word: inflected_words){
                            inf.addInflection(inflected_word.toString());
                        }
                        boolean inserted=false;
                        for(Inflections prev_inf: inflections){
                            if(prev_inf.CompareInflections(inf)){
                                prev_inf.addParadigm(steam,s);
                                inserted=true;
                                break;
                            }
                        }
                        if(!inserted)
                            inflections.add(inf);*/
                        steamparadigm.add(new Pair<Paradigm, String>(
                                new Paradigm(dic.pardefs.getParadigmDefinition(s), dic), steam));
                    }
                }
            }
        }
    }

    private Set<StringBuilder> GenerateAllInflexions(String steam, String paradigmname, Dictionary dic){
        Set<StringBuilder> exit=new LinkedHashSet<StringBuilder>();
        exit.add(new StringBuilder(steam));
        if(dic.pardefs.getParadigmDefinition(paradigmname)!=null)
            return processListE(dic.pardefs.getParadigmDefinition(paradigmname).elements, exit, dic);
        else
            return new LinkedHashSet<StringBuilder>();
    }


    private Set<StringBuilder> processListE(List<E> elements, Set<StringBuilder> currentLexicalForms, Dictionary dic)
    {
        Set<StringBuilder> localList=new LinkedHashSet<StringBuilder>();
         for(E element: elements){
             Set<StringBuilder> listgeneratedByElement=new LinkedHashSet<StringBuilder>();
             listgeneratedByElement.add(new StringBuilder());
             for (DixElement e: element.children)
             {
                 if (e instanceof P){
                     for(StringBuilder b: listgeneratedByElement){
                         b.append(((P)e).l.getValueNoTags());
                     }
                 }
                 else if (e instanceof Par)
                 {
                     List<E> parElements=dic.pardefs.getParadigmDefinition(((Par)e).name).elements;
                     if(parElements.size()>0){
                         Set<StringBuilder> resultList=processListE(parElements, listgeneratedByElement, dic);
                         listgeneratedByElement.clear();
                         for(StringBuilder r: resultList){
                            listgeneratedByElement.add(r);
                         }
                     }
                 }
             }
             for(StringBuilder b: listgeneratedByElement){
                    localList.add(b);
             }
         }
         //Combine lists
         Set<StringBuilder> finalList = new LinkedHashSet<StringBuilder>();
         for(StringBuilder lexHead: currentLexicalForms){
             for(StringBuilder lexTail: localList){
                 StringBuilder lextmp=new StringBuilder(lexHead);
                 lextmp.append(lexTail);
                 finalList.add(lextmp);
             }
         }
         return finalList;
    }
}
