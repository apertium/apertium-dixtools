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
package dics.elements.dtd;

import dics.elements.utils.DicOpts;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class PElement extends Element {

    /**
     * 
     */
    private LElement l;
    /**
     * 
     */
    private RElement r;

    /**
     * 
     * 
     */
    public PElement() {
        setTagName("p");
    }

    /**
     * 
     * @param pE
     */
    public PElement(final PElement pE) {
        setTagName("p");
        // l = new LElement(pE.getL());
        l = (LElement) pE.getL().clone();
        // r = new RElement(pE.getR());
        r = (RElement) pE.getR().clone();
    }

    /**
     * 
     * @param l
     * @param r
     */
    public PElement(final LElement l, final RElement r) {
        setTagName("p");
        this.l = l;
        this.r = r;
    }

    /**
     * 
     * @param l
     */
    public final void setLElement(final LElement l) {
        this.l = l;
    }

    /**
     * 
     * @param r
     */
    public final void setRElement(final RElement r) {
        this.r = r;
    }

    /**
     * 
     * @return Undefined         */
    public final LElement getL() {
        return l;
    }

    /**
     * 
     * @return Undefined         */
    public final RElement getR() {
        return r;
    }

    /**
     * 
     * @param value
     * @param side
     */
    public final void setProcessingComments(final String value, final String side) {
        if (side.equals("L")) {
            l.setProcessingComments(value);
        }
        if (side.equals("R")) {
            r.setProcessingComments(value);
        }
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    public final void printXML(final Appendable dos, final DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        dos.append(prependCharacterData);
        String tab3 = opt.now1line?"":tab(3);

        dos.append(tab3 + "<" + getTagName() + ">\n");
        if (l != null) {
            l.printXML(dos, opt);
        }
        if (r != null) {
            r.printXML(dos, opt);
        }
        dos.append(tab3 + "</" + getTagName() + ">"+appendCharacterData.trim()+"\n");
    }


    private final static String spaces = "                                                                                            ";
    
    public final void printXML1LineAligned(final StringBuilder dos, int alignR) throws IOException {
      /*
      DicOpts opt = DicOpts.stdnow1line.copy();
      opt.nowAlign = true;
      opt.alignR = alignR;
      printXML(dos, opt);
       */
        // write blank lines and processingComments from original file
        dos.append(prependCharacterData);
        dos.append("<" + getTagName() + ">");

        l.printXML(dos, DicOpts.stdnow1line);

        int neededSpaces = alignR - dos.length();
        if (neededSpaces>0) {
          dos.append(spaces.substring(0, Math.min(spaces.length(), neededSpaces)));
        }
        r.printXML(dos, DicOpts.stdnow1line);
        dos.append("</" + getTagName() + ">"+appendCharacterData.trim());
    }
        
        
    /**
     * 
     */
    @Override
    public final String toString() {
        String str = "";

        str += getL().toString();
        str += getR().toString();

        return str;
    }
}
