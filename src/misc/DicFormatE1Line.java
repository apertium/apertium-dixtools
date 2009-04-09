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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.SectionElement;
import dics.elements.utils.DicOpts;
import dictools.AbstractDictTool;
import dictools.xml.DictionaryReader;
import java.io.OutputStreamWriter;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicFormatE1Line extends AbstractDictTool {

    /**
     * 
     */
    private DictionaryElement dic;

    /**
     * 
     * @param dicFileName
     */
    public DicFormatE1Line(String dicFileName) {
        DictionaryReader dicReader = new DictionaryReader(dicFileName);
        dic = dicReader.readDic();
        setOpt(dics.elements.utils.DicOpts.STD_1_LINE);
    }

    
    /**
     * Initializes and prepares write a DictionaryElement 
     * @param dic The dictionary element
     */
    public DicFormatE1Line(DictionaryElement dic) {
        this.dic = dic;
        setOpt(dics.elements.utils.DicOpts.STD_1_LINE);
    }

    /**
     * 
     * @param fileName
     */
    public void printXML(String fileName) {
        this.printXML(fileName, "UTF-8");
        dic.setXmlEncoding("UTF-8");
    }

    /**
     * 
     * @param fileName
     */
    public void printXML(String fileName, String encoding) {
        dic.setFileName(fileName);
        dic.printXML(fileName, opt);
   }
 }
