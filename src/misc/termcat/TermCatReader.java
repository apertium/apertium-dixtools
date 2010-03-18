/*
 * Copyright 2010 Jimmy O'Regan
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

package misc.termcat;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.Comment;

import dictools.utils.XMLReader;

/**
 *
 * @author jimregan
 */
public class TermCatReader extends XMLReader {

    public Fitxa readTermCat() {
        analize();
        Fitxa f = new Fitxa();
        Element root = document.getDocumentElement();
        for (Element childElement : readChildren(root)) {
            String childElementName = childElement.getNodeName();
            if (childElementName.equals("cross-action")) {
                Denominacio den = readDenominacio(childElement);
                f.denom.add(den);
            }
        }
        root = null;
        this.document = null;
        return f;
    }

    public Denominacio readDenominacio(Element e) {
        String lang = getAttributeValue(e, "llengua");
        String tipus = getAttributeValue(e, "tipus");
        String jerar = getAttributeValue(e, "jerarquia");
        String cat = getAttributeValue(e, "categoria");
        String para = readTextContent(e);
        Denominacio d = new Denominacio(lang, tipus, cat, jerar, para);

        return d;
    }

    protected static String readTextContent(Element e) {
        String str="";
        try {
            if (e.hasChildNodes()) {
                NodeList children = e.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child instanceof Text) {
                        Text textNode = (Text) child;
                        str = textNode.getData().trim();
                    } else {
                        if (!(child instanceof Comment)) {
                            continue;
                        }
                    }
                }
            } else {
            }
        } catch (Exception exp) {
            exp.printStackTrace();
            System.exit(-1);
        }
        return str;
    }


}
