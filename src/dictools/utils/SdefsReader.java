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

package dictools.utils;

import org.w3c.dom.Element;

import dics.elements.dtd.Sdefs;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class SdefsReader extends XMLReader {

    /**
     * 
     * @param fileName
     */
    public SdefsReader(String fileName) {
        super(fileName);
    }

    /**
     * 
     * @return Undefined         */
    public Sdefs readSdefs() {
        analize();
        Element root = document.getDocumentElement();
        String elementName = root.getNodeName();
        Sdefs sdefsElement = null;

        // Symbol definitions
        if (elementName.equals("sdefs")) {
            sdefsElement = readSdefs(root);
        }

        root = null;
        this.document = null;
        return sdefsElement;
    }
}
