/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dictools;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.E;
import dics.elements.dtd.L;
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
    
    
    public ShortenRestrictions() {
    }
    
     private void processArguments() {
        DictionaryReader dicReader = new DictionaryReader(arguments[1]);
        Dictionary bil = dicReader.readDic();
        dicReader = null;
        setDicOrig(bil);
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
                if (!e.containsRegEx() && e.is_LR_or_LRRL() && ("cat".equals(e.v) || "".equals(e.v) || null==e.v ) && ("std".equals(e.alt) || "".equals(e.alt) || null==e.alt )) {
                       String lLemma=e.getLemmaWithBGForSide("L");
                       String rLemma=e.getLemmaWithBGForSide("R");
                       List<S> ltags=e.getSymbols("L");
                       List<S> rtags=e.getSymbols("R");
                       
                       //ignore entries with no tags
                       if(ltags.size()>0 && rtags.size()>0)
                       {
                           if (!lLemma.equals(prevLemma) || !ltags.get(0).getValue().equals(prevPos))
                           {
                               elementsToAdd.addAll(reduceGroup(prevElements));
                               prevElements.clear();
                               prevLemma=lLemma;
                               prevPos=ltags.get(0).getValue();
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
        
        String reverseFileName = "short-dic.dix";
        if (arguments.length == 3)
                reverseFileName = arguments[2];
            
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
        
        int numElements=prevElements.size();
        
        boolean allCanBeRemoved=true;
        
        while(allCanBeRemoved)
        {        
            //Compute longest length
            int longestEntryLength=0;
            for(int i=0; i<numElements; i++)
            {
                if(lTags.get(i).size()>longestEntryLength)
                    longestEntryLength=lTags.get(i).size();
            }

            
            //Check if the last tag of all the entries with the longest length can be removed
            allCanBeRemoved=false;
            if(longestEntryLength>1)
            {
                allCanBeRemoved=true;
                for(int i=0; i<numElements; i++)
                {
                    if(lTags.get(i).size()==longestEntryLength)
                    {
                        if(!lTags.get(i).get(longestEntryLength-1).getValue().equals( rTags.get(i).get( rTags.get(i).size() -1 ).getValue() ))
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
                List<List<S>> lTagsAfterRlastTag=new ArrayList<List<S>>();
                List<List<S>> rTagsAfterRlastTag=new ArrayList<List<S>>();
                for(int i=0; i<numElements; i++)
                {
                    if(lTags.get(i).size()==longestEntryLength)
                    {
                        List<S> leftSide=lTags.get(i).subList(0, longestEntryLength-1);
                        List<S> rightSide=rTags.get(i).subList(0, rTags.get(i).size()-1);
                        
                        lTagsAfterRlastTag.add(leftSide);
                        rTagsAfterRlastTag.add(rightSide);
                    }
                    else
                    {
                        lTagsAfterRlastTag.add(lTags.get(i));
                        rTagsAfterRlastTag.add(rTags.get(i));
                    }
                }
                
                for(int i=0; i<numElements; i++)
                {
                    List<S> curLeftSide=lTagsAfterRlastTag.get(i);
                    for(int j=0; j<i; j++)
                    {
                        if(lTagsAfterRlastTag.get(j).equals(curLeftSide))
                        {
                            if(!rLemmas.get(i).equals(rLemmas.get(j)) || !rTagsAfterRlastTag.get(i).equals(rTagsAfterRlastTag.get(j)))
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
                        if(lTags.get(i).size()==longestEntryLength)
                        {
                            lTags.get(i).remove(longestEntryLength-1);
                            rTags.get(i).remove(rTags.get(i).size()-1);
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
                 if(lTags.get(i).equals(lTags.get(j)))
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
                 newElement.children.add(l);
                 newElement.children.add(r);
                 returnedElements.add(newElement);
             }
         }
               
        
        return returnedElements;
    }
    
}
