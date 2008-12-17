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

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.SectionElement;
import java.util.HashMap;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class DicFilter  extends AbstractDictTool{

    /**
     * 
     */
    private DictionaryElement dicA;
    /**
     * 
     */
    private DictionaryElement dicB;
    
    /**
     * 
     */
    private String[] arguments;
    
    /**
     * 
     */
    private String outFileName;

    /**
     * 
     */
    public DicFilter() {
        
    }    
    
    public final void doFilter() {
        this.processArguments();
        
        
        
        // recorremos en-es
        HashMap<String,EElement> map = new HashMap<String,EElement>();
        
        for(SectionElement section : dicB.getSections()) {
            for( EElement ee : section.getEElements()) {
                String key = ee.getLeft().getValueNoTags();
                System.out.println(key);
                map.put(key, ee);
            }            
        }

        DictionaryElement dicFilt = new DictionaryElement();
        SectionElement sectionFilt = new SectionElement();
        dicFilt.addSection(sectionFilt);
        
        for(SectionElement section : dicA.getSections()) {
            for( EElement ee : section.getEElements()) {
                String key = ee.getRight().getValueNoTags();
                if(map.containsKey(key)) {
                    sectionFilt.addEElement(ee);
                }                
            }            
        }
        
        dicFilt.printXML("apertium-de-en.de-en-basic.dix", opt);
        
        
    }
    
    /**
     * 
     */
    private void processArguments() {
        String fileNameA = this.arguments[1];
        DictionaryReader dicReaderA = new DictionaryReader(fileNameA);
        dicA = dicReaderA.readDic();
        
        String fileNameB = this.arguments[2];
        DictionaryReader dicReaderB = new DictionaryReader(fileNameB);
        dicB = dicReaderB.readDic();
        
        System.out.println(fileNameA + "/" + fileNameB);
     
        
    }

    /**
     * 
     * @return the arguments
     */
    public String[] getArguments() {
        return arguments;
    }

    /**
     * 
     * @param arguments
     */
    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    public String getOutFileName() {
        return outFileName;
    }

    public void setOutFileName(String outFileName) {
        this.outFileName = outFileName;
    }
}
