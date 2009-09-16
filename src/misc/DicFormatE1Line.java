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
package misc;

import dics.elements.dtd.Dictionary;
import dictools.AbstractDictTool;
import dictools.xml.DictionaryReader;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicFormatE1Line extends AbstractDictTool {

    
    private Dictionary dic;

    /**
     * 
     * @param dicFileName
     */
    public DicFormatE1Line(String dicFileName) {
        DictionaryReader dicReader = new DictionaryReader(dicFileName);
        dic = dicReader.readDic();
        this.opt = dics.elements.utils.DicOpts.STD_1_LINE;
    }

    
    /**
     * Initializes and prepares write a Dictionary
     * @param dic The dictionary element
     */
    public DicFormatE1Line(Dictionary dic) {
        this.dic = dic;
        this.opt = dics.elements.utils.DicOpts.STD_1_LINE;
    }

    /**
     * 
     * @param fileName
     */
    public void printXML(String fileName) {
        this.printXML(fileName, "UTF-8");
        dic.xmlEncoding = "UTF-8";
    }

    /**
     * 
     * @param fileName
     */
    public void printXML(String fileName, String encoding) {
        dic.fileName = fileName;
        dic.printXML(fileName, opt);
   }
 }
