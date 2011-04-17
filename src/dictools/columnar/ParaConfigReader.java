/*
 * Copyright 2011 European Commission
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
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
            pairs = readMappings(childElement);
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

        if (!e.getNodeName().equals("mapping"))
            return null;
        for (Element childElement : readChildren(e)) {
            String childElementName = childElement.getNodeName();
            if (childElementName.equals("left")) {
                for (Element grandchild : readChildren(childElement)) {
                    if (grandchild.getNodeName().equals("paradigm")) {
                        leftPars.add(getAttributeValue(grandchild, "n"));
                    }
                }
            }
            if (childElementName.equals("right")) {
                for (Element grandchild : readChildren(childElement)) {
                    if (grandchild.getNodeName().equals("paradigm")) {
                        rightPars.add(getAttributeValue(grandchild, "n"));
                    }
                }
            }
            if (childElementName.equals("entries")) {
                entries = readEntries(childElement);
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
