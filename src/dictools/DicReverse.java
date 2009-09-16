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

import dics.elements.dtd.Dictionary;
import dics.elements.utils.DicTools;
import dictools.xml.DictionaryReader;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicReverse  extends AbstractDictTool {

    
    private Dictionary dicOrig;

    
    public DicReverse() {

    }

    /**
     * 
     * @return Undefined         */
    public Dictionary reverse() {
        getDicOrig().reverse();
        // Collections.sort(getDicOrig().getEntries());
        return getDicOrig();
    }

    /**
     * 
     * @return Undefined         */
    private Dictionary getDicOrig() {
        return dicOrig;
    }

    
    private void processArguments() {
        DictionaryReader dicReader = new DictionaryReader(arguments[1]);
        Dictionary bil = dicReader.readDic();
        dicReader = null;
        setDicOrig(bil);
    }

    
    public void doReverse() {
        processArguments();
        actionReverse();
    }

    
    public void actionReverse() {
        Dictionary bil = reverse();
        String reverseFileName = "reversed-dic.dix";
        if (getArguments().length == 3) {
            if (getArguments()[2].equals("out.dix")) {
                reverseFileName = DicTools.reverseDicName(arguments[1]);
            } else {
                reverseFileName = getArguments()[2];
            }
            bil.printXML(reverseFileName,getOpt());
        }
    }


    /**
     * @param dicOrig
     *                the dicOrig to set
     */
    private void setDicOrig(Dictionary dicOrig) {
        this.dicOrig = dicOrig;
    }
}
