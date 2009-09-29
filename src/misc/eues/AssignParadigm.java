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
package misc.eues;

import java.util.HashMap;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.L;
import dics.elements.dtd.P;
import dics.elements.dtd.Par;
import dics.elements.dtd.R;
import dics.elements.dtd.Section;
import dics.elements.dtd.TextElement;
import dictools.AbstractDictTool;
import dictools.utils.DictionaryReader;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class AssignParadigm extends AbstractDictTool {

    
    private String morphDic;
    
    private String bilDic;
    
    private String out;

    
    public void processArguments() {
        morphDic = arguments[1];
        bilDic = arguments[2];
        out = arguments[3];
    }

    
    public void doAssignParadigm() {
        processArguments();

        DictionaryReader reader = new DictionaryReader(morphDic);
        reader.readParadigms = false;
        System.err.println("Reading morphological '" + morphDic + "'");
        Dictionary dic = reader.readDic();

        HashMap<String, String> np = new HashMap<String, String>();

        for (Section section : dic.sections) {
            for (E ee : section.elements) {
                String parName = ee.getMainParadigmName();
                if (parName != null) {
                    String right = ee.getFirstPart("R").getValue();
                    np.put(right, parName);
                }
            }
        }

        System.err.println(np.size() + " entries read.");
        DictionaryReader reader2 = new DictionaryReader(bilDic);
        Dictionary bil = reader2.readDic();

        for (Section section : bil.sections) {
            for (E ee : section.elements) {
                if (!ee.containsRegEx()) {
                    String left = ee.getFirstPart("L").getValue();
                    String right = ee.getFirstPart("R").getValue();

                    String leftNoTags = cleanTags(left);
                    String rightNoTags = cleanTags(right);
                    String par = np.get(rightNoTags);

                    E e = new E();
                    e.comment="auto";

                    P p = new P();
                    e.children.add(p);
                    if (par == null) {
                        System.err.println("No paradigm for '" + leftNoTags + "'");
                        par = "";
                    }
                    Par parE = new Par(par);
                    e.children.add(parE);

                    L l = new L();
                    R r = new R();
                    TextElement text = new TextElement(leftNoTags);
                    r.children.add(text);
                    p.l = (l);
                    p.r = (r);

                    (dic.getEntriesInMainSection()).add(e);
                }
            }
        }
        System.err.println("Updated morphological dictionary: '" + out + "'");
        dic.printXML(out, dictools.utils.DicOpts.STD);
    }

    /**
     * 
     * @param value
     * @return
     */
    private String cleanTags(String value) {
        String[] vs = value.split("\\[");
        if (vs == null) {
            return value;
        }
        if (vs.length > 1) {
            return vs[0];
        } else {
            return value;
        }

    }

}
