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

import org.w3c.dom.Element;

import dics.elements.dtd.SElement;
import dics.elements.dtd.SdefElement;
import dics.elements.dtd.SdefsElement;

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
    public SdefsReader(final String fileName) {
        super(fileName);
    }

    /**
     * 
     * @return Undefined         */
    public SdefsElement readSdefs() {
        analize();
        Element root = getDocument().getDocumentElement();
        String elementName = root.getNodeName();
        SdefsElement sdefsElement = null;

        // Symbol definitions
        if (elementName.equals("sdefs")) {
            sdefsElement = readSdefs(root);
        }

        root = null;
        setDocument(null);
        return sdefsElement;
    }

    /**
     * 
     * @param e
     * @return Undefined         */
    public SdefsElement readSdefs(final Element e) {
        final SdefsElement sdefsElement = new SdefsElement();

        for (final Element childElement : readChildren(e)) {
            final String childElementName = childElement.getNodeName();
            if (childElementName.equals("sdef")) {
                final SdefElement sdefElement = readSdef(childElement);
                final SElement sE = SElement.get(sdefElement.getValue());
                sdefsElement.addSdefElement(sdefElement);
            }
        }

        return sdefsElement;
    }

    /**
     * 
     * @param e
     * @return Undefined         */
    public SdefElement readSdef(final Element e) {
        final String n = getAttributeValue(e, "n");
        final String c = getAttributeValue(e, "c");
        final SdefElement sdefElement = new SdefElement(n);
        sdefElement.setComment(c);
        return sdefElement;
    }
}
