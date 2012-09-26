/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dictools;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.E;
import dics.elements.dtd.L;
import dics.elements.dtd.P;
import dics.elements.dtd.R;
import dics.elements.dtd.S;
import dics.elements.dtd.Section;
import dics.elements.dtd.TextElement;
import dictools.utils.DictionaryReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author vmsanchez
 */
public class ShortenRestrictions extends AbstractDictTool{

     private Dictionary dicOrig;
     private String reverseFileName;
     private boolean rightSide=false;
    
    public ShortenRestrictions() {
    }
    
     private void processArguments() {
        reverseFileName = "short-dic.dix";
        
        int posArgument=1;
        if (arguments[posArgument].equals("-right"))
        {
            rightSide=true;
            posArgument++;
        }
        
        DictionaryReader dicReader = new DictionaryReader(arguments[posArgument]);
        Dictionary bil = dicReader.readDic();
        dicReader = null;
        setDicOrig(bil);
        posArgument++;
        
        if (arguments.length > posArgument)
                reverseFileName = arguments[posArgument];
    }
     
       private void setDicOrig(Dictionary dicOrig) {
        this.dicOrig = dicOrig;
    }
    
    public void doShorten()
    {
        processArguments();
        
        for (Section section : dicOrig.sections) {
            
            String prevLemma="";
            String prevPos="";
            List<E> prevElements=new ArrayList<E>();
            
            List<E> elementsToAdd=new ArrayList<E>();
            
            Iterator<E> elementIterator=section.elements.iterator();
            while(elementIterator.hasNext()) {
                E e=elementIterator.next();
                if (!e.containsRegEx() && ( (e.is_LR_or_LRRL() && !rightSide) || (e.is_RL_or_LRRL() && rightSide) ) && ("cat".equals(e.v) || "".equals(e.v) || null==e.v ) && ("std".equals(e.alt) || "".equals(e.alt) || null==e.alt )) {
                       String lLemma=e.getLemmaWithBGForSide("L");
                       String rLemma=e.getLemmaWithBGForSide("R");
                       List<S> ltags=e.getSymbols("L");
                       List<S> rtags=e.getSymbols("R");
                       
                       //ignore entries with no tags
                       if(ltags.size()>0 && rtags.size()>0)
                       {
                           String lemma=(rightSide?rLemma:lLemma);
                           List<S> tags=(rightSide?rtags:ltags);
                           if (!lemma.equals(prevLemma) || !tags.get(0).getValue().equals(prevPos))
                           {
                               elementsToAdd.addAll(reduceGroup(prevElements));
                               prevElements.clear();
                               prevLemma=lemma;
                               prevPos=tags.get(0).getValue();
                           }
                           prevElements.add(e);
                           elementIterator.remove();
                       }
                }
            }
            elementsToAdd.addAll(reduceGroup(prevElements));
            
            for(E e : elementsToAdd)
            {
                section.elements.add(e);
            }
        }
        
                  
        this.dicOrig.printXMLToFile(reverseFileName,opt);
        
    }

    private Collection<? extends E> reduceGroup(List<E> prevElements) {
        List<E> returnedElements = new ArrayList<E>();
        List<String> lLemmas=new ArrayList<String>();
        List<String> rLemmas=new ArrayList<String>();
        List<List<S>> lTags= new ArrayList<List<S>>();
        List<List<S>> rTags= new ArrayList<List<S>>();
        
        for(E e: prevElements)
        {
           String lLemma=e.getLemmaWithBGForSide("L");
           lLemmas.add(lLemma);
           String rLemma=e.getLemmaWithBGForSide("R");
           rLemmas.add(rLemma);
           List<S> ltags=e.getSymbols("L");
           lTags.add(ltags);
           List<S> rtags=e.getSymbols("R");
           rTags.add(rtags);
        }
        
        List<List<S>> thisSideTags=lTags;
        List<String> thisSideLemmas=lLemmas;
        List<List<S>> otherSideTags=rTags;
        List<String> otherSideLemmas=rLemmas;
        if(rightSide)
        {
            thisSideTags=rTags;
            otherSideTags=lTags;
            thisSideLemmas=rLemmas;
            otherSideLemmas=lLemmas;
        }
        
        int numElements=prevElements.size();
        
        boolean allCanBeRemoved=true;
        
        while(allCanBeRemoved)
        {        
            //Compute longest length
            int longestEntryLength=0;
            for(int i=0; i<numElements; i++)
            {
                if(thisSideTags.get(i).size()>longestEntryLength)
                    longestEntryLength=thisSideTags.get(i).size();
            }

            
            //Check if the last tag of all the entries with the longest length can be removed
            allCanBeRemoved=false;
            if(longestEntryLength>1)
            {
                allCanBeRemoved=true;
                for(int i=0; i<numElements; i++)
                {
                    if(thisSideTags.get(i).size()==longestEntryLength)
                    {
                        if(!thisSideTags.get(i).get(longestEntryLength-1).getValue().equals( otherSideTags.get(i).get( otherSideTags.get(i).size() -1 ).getValue() ))
                        {
                            allCanBeRemoved=false;
                            break;
                        }
                    }  
                }
            }
            
            
            if(allCanBeRemoved)
            {
                //Check if, after removing the last tag from the longest entry, there are
                //entries with the same left side and different right side
                List<List<S>> thisSideTagsAfterRlastTag=new ArrayList<List<S>>();
                List<List<S>> otherSideTagsAfterRlastTag=new ArrayList<List<S>>();
                for(int i=0; i<numElements; i++)
                {
                    if(thisSideTags.get(i).size()==longestEntryLength)
                    {
                        List<S> thisSide=thisSideTags.get(i).subList(0, longestEntryLength-1);
                        List<S> otherSide=otherSideTags.get(i).subList(0, otherSideTags.get(i).size()-1);
                        
                        thisSideTagsAfterRlastTag.add(thisSide);
                        otherSideTagsAfterRlastTag.add(otherSide);
                    }
                    else
                    {
                        thisSideTagsAfterRlastTag.add(thisSideTags.get(i));
                        otherSideTagsAfterRlastTag.add(otherSideTags.get(i));
                    }
                }
                
                for(int i=0; i<numElements; i++)
                {
                    List<S> curThisSide=thisSideTagsAfterRlastTag.get(i);
                    for(int j=0; j<i; j++)
                    {
                        if(thisSideTagsAfterRlastTag.get(j).equals(curThisSide))
                        {
                            if(!otherSideLemmas.get(i).equals(otherSideLemmas.get(j)) || !otherSideTagsAfterRlastTag.get(i).equals(otherSideTagsAfterRlastTag.get(j)))
                            {
                                allCanBeRemoved=false;
                                break;
                            }
                        }
                    }
                    if(!allCanBeRemoved)
                        break;
                }

                
                if(allCanBeRemoved)
                {
                    for(int i=0; i<numElements; i++)
                    {
                        if(thisSideTags.get(i).size()==longestEntryLength)
                        {
                            thisSideTags.get(i).remove(longestEntryLength-1);
                            otherSideTags.get(i).remove(otherSideTags.get(i).size()-1);
                        }
                    }
                }
            }
        }
        
        
        //Remove duplicated entries
         for(int i=0; i<numElements; i++)
         {
             boolean isDuplicated=false;
             for(int  j=0; j< i; j++)
             {
                 if(thisSideTags.get(i).equals(thisSideTags.get(j)) && otherSideLemmas.get(i).equals(otherSideLemmas.get(j)) && otherSideTags.get(i).equals(otherSideTags.get(j)))
                     isDuplicated=true;
             }
             
             if(!isDuplicated)
             {
                 E newElement= prevElements.get(i);
                 newElement.children.clear();
                 L l = new L();
                 l.children.add(new TextElement(lLemmas.get(i)));
                 l.children.addAll(lTags.get(i));
                 
                 R r = new R();
                 r.children.add(new TextElement(rLemmas.get(i)));
                 r.children.addAll(rTags.get(i));
                 P newP = new P(l, r);
                 
                 newElement.children.add(newP);
                 
                 returnedElements.add(newElement);
             }
         }
               
        
        return returnedElements;
    }
    
}
