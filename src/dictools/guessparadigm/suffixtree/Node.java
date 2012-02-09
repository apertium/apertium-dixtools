/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dictools.guessparadigm.suffixtree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author miquel
 */
public class Node implements Serializable{
    private HashMap<Character,Node> children;

    private boolean startingsuffix;

    private Set<String> paradigm;

    public Set<String> getParadigmName(){
        return paradigm;
    }

    public void addParadigmName(String name){
        if(paradigm==null)
            this.paradigm=new LinkedHashSet<String>();
        paradigm.add(name.intern());
    }

    public Node(){
        this.startingsuffix=false;
        this.paradigm=null;
        this.children=null;
    }

    public void AddChild(char character, Node n){
        if(children==null)
            this.children=new HashMap<Character, Node>();
        children.put(SuffixTree.GetCharObject(character),n);
    }

    public HashMap<Character, Node> getChildren(){
        return children;
    }

    public Node getChild(char character){
        if(children==null)
            return null;
        else
            return children.get(character);
    }

    public boolean isStartingsuffix() {
        return startingsuffix;
    }

    public void setStartingsuffix(boolean startingsuffix) {
        this.startingsuffix = startingsuffix;
    }

    /*public void InsertWord(String word, int currentpos, boolean debug){
        if(currentpos>=0){
            Node n=getChild(word.charAt(currentpos));
            if(n!=null){
                n.InsertWord(word, currentpos-1, debug);
                if(debug)
                    System.out.println("node "+word.charAt(currentpos)+" found");
            }
            else{
                if(debug)
                    System.out.println("node "+word.charAt(currentpos)+" not found");
                n=new Node();
                n.InsertWord(word, currentpos-1, debug);
                AddChild(word.charAt(currentpos),n);
            }
        }
    }*/

    public void InsertWord(String word, int currentpos, int startingsuffixpos, String paradigm, boolean debug){
        if(currentpos>=0){
            Node n=getChild(word.charAt(currentpos));
            if(n!=null){
                if(debug)
                    System.out.println("node "+word.charAt(currentpos)+" found");
                if(startingsuffixpos==currentpos){
                    if(debug)
                        System.out.println("it is an starting suffix position");
                    n.setStartingsuffix(true);
                    //n.InsertWord(word, currentpos-1, debug);
                    n.addParadigmName(paradigm);
                }
                else{
                    n.InsertWord(word, currentpos-1, startingsuffixpos, paradigm, debug);
                }
            }
            else {
                if(debug)
                    System.out.println("node "+word.charAt(currentpos)+" not found");
                Node newnode=new Node();
                if(startingsuffixpos==currentpos){
                    if(debug)
                        System.out.println("it is an starting suffix position");
                    newnode.setStartingsuffix(true);
                    //newnode.InsertWord(word, currentpos-1, debug);
                    newnode.addParadigmName(paradigm);
                }
                else{
                    newnode.InsertWord(word, currentpos-1, startingsuffixpos, paradigm, debug);
                }
                AddChild(word.charAt(currentpos),newnode);
                if(debug)
                    System.out.println(word.charAt(currentpos)+" added");
            }
        }
    }
}
