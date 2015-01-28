/*
 * Copyright (C) 2013 
 * Author: Xavier Ivars i Ribes
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

package dictools.enhancer;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.Pardef;
import dictools.DicElementsList;
import dictools.guessparadigm.paradigms.Paradigm;

public class StemGuesser {

    private Dictionary _dic;
    DictEnhancerTexts _texts;
    
    public StemGuesser(Dictionary dic, DictEnhancerTexts texts) {
        _dic = dic;
        _texts = texts;
    }
	
	// added boolean for expanded display	
    public String GetStem(String newWord, E existingElement) {
        
        String paradigm = existingElement.getMainParadigmName();
        
        String newStem = suggestStem(newWord, existingElement);
        
        
		/*if (newStem != null) {
			expandElement(newStem, paradigm);
            if (_texts.askConfirmation()) {
                return newStem;
            } 
            
            newStem = newWord;
        }
        
        do{
            expandElement(newStem, paradigm);
            
			if (_texts.askConfirmation()) {
			    return newStem;  
        	} 
            
           	newStem = newStem.substring(0, newStem.length() - 1);
            
        } while (newStem.length() > 0);
        
        return null;
    	*/
		return newStem;
	}
    
    private String suggestStem(String newWord, E element) {
        
        String left = element.getFirstPart("L").getValueNoTags();
        
        int differenceOfSizes = element.lemma.length() - left.length();
        
        if(newWord.length()<= differenceOfSizes) {
            return newWord;
        }
        
        return newWord.substring(0, newWord.length() - differenceOfSizes);
    }

    private void expandElement(String stem, String paradigm) {
        for(String form : DicElementsList.expandElement(_dic, stem, paradigm )) {
            System.out.println("\t"+form);
        }
        
        Pardef pardef = new Pardef("pere");
        E element = new E();
        Paradigm p = new Paradigm(pardef,_dic);
        //p.getSuffixes();
        
        
    }
}
