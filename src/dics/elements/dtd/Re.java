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

import java.io.IOException;

import dictools.utils.DicOpts;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class Re extends DixElement {

    
    private String expression;

    /**
     * 
     * @param value
     */
    public Re(String value) {
        super("re");
        this.expression = value;
    }

    /**
     * 
     * @return Undefined         */
    @Override
    public String getValue() {
        return expression;
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    public void printXML(Appendable dos, DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        dos.append(prependCharacterData);
        
        expression = maskNull(expression);

        dos.append( (opt.nowAlign?"":tab(3)) + "<" + TAGNAME + ">" + getValue() + "</" + TAGNAME + ">"+appendCharacterData.trim()+  (opt.nowAlign?"":"\n"));
    }

    
    @Override
    public String toString() {
        return getValue();
    }

  @Override
  public String getStreamContent() {
     return "[REGEXP"+expression+"]";
  }
}
