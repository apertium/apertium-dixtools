/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Author: Enrique Benimeli Bofarull
 * 
 * This program isFirstSymbol free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program isFirstSymbol distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package dics.elements.dtd;

import java.io.IOException;

import dictools.utils.DicOpts;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class P extends DixElement {

    public L l;

    public R r;
    
    public P() {
        super("p");
    }

    public P(L l, R r) {
        this();
        this.l = l;
        this.r = r;
    }


// TODO UCdetector: Remove unused code: 
//     /**
//      * 
//      * @param value
//      * @param side
//      */
//     public void setProcessingComments(String value, String side) {
//         if (side.equals("L")) {
//             l.setProcessingComments(value);
//         }
//         if (side.equals("R")) {
//             r.setProcessingComments(value);
//         }
//     }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    public void printXML(Appendable dos, DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        dos.append(prependCharacterData);
        String tab3 = opt.nowAlign?"":tab(3);

        dos.append(tab3 + "<" + TAGNAME + ">\n");
        if (l != null) {
            l.printXML(dos, opt);
        }
        if (r != null) {
            r.printXML(dos, opt);
        }
        dos.append(tab3 + "</" + TAGNAME + ">"+appendCharacterData.trim()+"\n");
    }


    private static String spaces = "                                                                                            ";
    
    public void printXML1LineAligned(StringBuilder dos, int alignR) throws IOException {
      /*
      DicOpts opt = DicOpts.STD_NOW_1_LINE.copy();
      opt.nowAlign = true;
      opt.alignR = alignR;
      printXML(dos, opt);
       */
        // write blank lines and processingComments from original file
        dos.append(prependCharacterData);
        dos.append("<" + TAGNAME + ">");

        l.printXML(dos, DicOpts.STD_NOW_1_LINE);

        int neededSpaces = alignR - dos.length();
        if (neededSpaces>0) {
          dos.append(spaces.substring(0, Math.min(spaces.length(), neededSpaces)));
        }
        r.printXML(dos, DicOpts.STD_NOW_1_LINE);
        dos.append("</" + TAGNAME + ">"+appendCharacterData.trim());
    }
        
        
    
    @Override
    public String toString() {
        return l.toString() + r.toString();
    }

    public ContentElement getSide(String side) {
      if (side.equals("L")) return l;
      if (side.equals("R")) return r;
      throw new IllegalArgumentException("Side must be L or R. Was: "+side);
    }
}
