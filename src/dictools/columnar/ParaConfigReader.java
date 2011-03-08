/*
 * Author: Jimmy O'Regan
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

package dictools.columnar;

import org.w3c.dom.Element;

import dics.elements.dtd.E;
import dictools.utils.XMLReader;
import java.util.ArrayList;

/**
 *
 * @author jimregan
 */
public class ParaConfigReader extends XMLReader {

    /**
     *
     * @param filename 
     */
    ParaConfigReader(String filename) {
        super(filename);
    }

    public ParaConfig readParaConfig() {
        analize();
        ParaConfig pc = new ParaConfig();
        ArrayList<ParadigmPair> pairs = new ArrayList<ParadigmPair>();
        Element root = document.getDocumentElement();
        for (Element childElement : readChildren(root)) {
            String childElementName = childElement.getNodeName();
            if (childElementName.equals("mapping")) {
                pairs = readMappings(root);
            }
        }
        for (ParadigmPair pair : pairs) {
            pc.add(pair);
        }
        root = null;
        this.document = null;

        return pc;
    }

    private ArrayList<ParadigmPair> readMappings(Element e) {
        ArrayList<ParadigmPair> pairs = new ArrayList<ParadigmPair>();
        ArrayList<String> leftPars = new ArrayList<String>();
        ArrayList<String> rightPars = new ArrayList<String>();
        ArrayList<E> entries = new ArrayList<E>();

        for (Element childElement : readChildren(e)) {
            String childElementName = childElement.getNodeName();
            if (childElementName.equals("left")) {
                for (Element grandchild : readChildren(e)) {
                    if (grandchild.getNodeName().equals("paradigm")) {
                        leftPars.add(getAttributeValue(e, "n"));
                    }
                }
            }
            if (childElementName.equals("right")) {
                for (Element grandchild : readChildren(e)) {
                    if (grandchild.getNodeName().equals("paradigm")) {
                        rightPars.add(getAttributeValue(e, "n"));
                    }
                }
            }
            if (childElementName.equals("entries")) {
                for (Element grandchild : readChildren(e)) {
                    if (grandchild.getNodeName().equals("e")) {
                        entries = readEntries(e);
                    }
                }
            }
        }

        for (String l : leftPars) {
            for (String r : rightPars) {
                ParadigmPair tmp = new ParadigmPair(l, r, entries);
                pairs.add(tmp);
            }
        }
        
        return pairs;
    }

    private ArrayList<E> readEntries(Element e) {
        ArrayList<E> entries = new ArrayList<E>();
        for (Element childElement : readChildren(e)) {
            String childElementName = childElement.getNodeName();
            if (childElementName.equals("e")) {
                E eE = readEElement(childElement);
                entries.add(eE);
            }
        }
        return entries;
    }

}
