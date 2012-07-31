/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dictools;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.L;
import dics.elements.dtd.P;
import dics.elements.dtd.R;
import dics.elements.dtd.S;
import dics.elements.dtd.Section;
import dics.elements.dtd.TextElement;
import dictools.utils.DictionaryReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author vmsanchez
 */
class AddRestrictionsToSL extends AbstractDictTool {

    private Dictionary dicOrig;

    public Dictionary getDicOrig() {
        return dicOrig;
    }

    public void setDicOrig(Dictionary dicOrig) {
        this.dicOrig = dicOrig;
    }
    
      private void processArguments() {
        DictionaryReader dicReader = new DictionaryReader(arguments[1]);
        Dictionary bil = dicReader.readDic();
        dicReader = null;
        setDicOrig(bil);
    }
    
    void doAddRestrictions() {
        processArguments();
        
        for (Section section : dicOrig.sections) {
            
            Iterator<E> elementIterator=section.elements.iterator();
            while(elementIterator.hasNext()) {
                E e=elementIterator.next();
                if (!e.containsRegEx() && e.is_LR_or_LRRL() && ("cat".equals(e.v) || "".equals(e.v) || null==e.v ) && ("std".equals(e.alt) || "".equals(e.alt) || null==e.alt )) {
                       String lLemma=e.getLemmaWithBGForSide("L");
                       String rLemma=e.getLemmaWithBGForSide("R");
                       List<S> ltags=e.getSymbols("L");
                       List<S> rtags=e.getSymbols("R");
                       
                       e.children.clear();
                       L l = new L();
                       l.children.add(new TextElement(lLemma));
                       
                       for( S rtag: rtags)
                           l.children.add(new S("RES"+rtag.getValue()));
                       l.children.addAll(ltags);
                       
                       R r = new R();
                       r.children.add(new TextElement(rLemma));
                       r.children.addAll(rtags);
                       P newP = new P(null, null);
                       e.children.add(newP);
                       
                }
                
            } 
        }
        
        String reverseFileName = "dic-with-restrictions-in-sl.dix";
        if (arguments.length == 3)
                reverseFileName = arguments[2];
            
        this.dicOrig.printXMLToFile(reverseFileName,opt);
    }
    
}
