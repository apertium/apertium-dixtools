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
import java.io.Writer;

/**
 * @author Enrique Benimeli Bofarull
 * 
 */
public class TextElement extends DixElement {

    /**
     * 
     */
    private String text;

    /**
     * 
     * @param str
     */
    public TextElement(String str) {
        super("");
        text = str;
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    public void printXML(Appendable dos, DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        //not necesary: dos.append(prependCharacterData);

      // In ContentElement line 400 "<b/>" is being added as TEXT into another text element,
        // so we can't escape > and < !
        // replaceAll("<", "&lt;").replaceAll(">", "&gt;").

        dos.append(text.replace("&", "&amp;").replace("\"", "&apos;"));

        //not necesary: dos.append(appendCharacterData);
    }

    /**
     * 
     * @return Undefined         */
    public String getValue() {
        return text;
    }

    public void setValue(String v) {
        text = v;
    }
}
