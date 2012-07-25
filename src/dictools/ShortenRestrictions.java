/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dictools;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.S;
import dics.elements.dtd.Section;
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
                       String lLemma=e.getLemmaForSide("L");
                       String rLemma=e.getLemmaForSide("R");
                       List<S> ltags=e.getSymbols("L");
                       List<S> rtags=e.getSymbols("R");
                       
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
            elementsToAdd.addAll(reduceGroup(prevElements));
            
            for(E e : elementsToAdd)
            {
                section.elements.add(e);
            }
        }
        
    }

    private Collection<? extends E> reduceGroup(List<E> prevElements) {
        List<E> returnedElements = new ArrayList<E>();
        List<String> lLemmas=new ArrayList<String>();
        List<String> rLemmas=new ArrayList<String>();
        List<List<S>> lTags= new ArrayList<List<S>>();
        List<List<S>> rTags= new ArrayList<List<S>>();
        
        for(E e: prevElements)
        {
           String lLemma=e.getLemmaForSide("L");
           lLemmas.add(lLemma);
           String rLemma=e.getLemmaForSide("R");
           rLemmas.add(rLemma);
           List<S> ltags=e.getSymbols("L");
           lTags.add(ltags);
           List<S> rtags=e.getSymbols("R");
           rTags.add(rtags);
        }
        
        int length=prevElements.size();
        
        
        
        
        
        return returnedElements;
    }
    
}
