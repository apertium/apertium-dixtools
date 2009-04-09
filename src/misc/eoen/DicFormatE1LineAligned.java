/*
 * Copyright (C) 2008 Dana Esperanta Junulara Organizo http://dejo.dk/
 * Author: Jacob Nordfalk
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

package misc.eoen;

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
 * Prints a dictionaty with elements aligned.
 * Quick exampled of use:
 * <pre>
 * new DicFormatE1LineAligned(dic).setAlignP(11).setAlignR(60).printXML("after-clean.dix");
 * </pre>
 * 
 * @author Enrique Benimeli Bofarull
 * @author Jacob Nordfalk
 */
public class DicFormatE1LineAligned extends AbstractDictTool {

    /**
     * 
     */
    private DictionaryElement dic;
    
  public DicFormatE1LineAligned setAlignP(int attrSpaces) {
    opt.alignP=attrSpaces;
    return this;
  }

  public DicFormatE1LineAligned setAlignR(int spaces) {
    opt.alignR = spaces;
    return this;
  }

  public DicFormatE1LineAligned setDic(DictionaryElement dic) {
    this.dic=dic;
    return this;
  }

    
    
    /**
     * Initializes and prepares write a DictionaryElement 
     * @param dic The dictionary element
     */
    public DicFormatE1LineAligned(DictionaryElement dic) {
        this.dic = dic;
        setOpt(dics.elements.utils.DicOpts.STD_ALIGNED);
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
