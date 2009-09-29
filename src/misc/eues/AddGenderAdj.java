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

import dics.elements.dtd.ContentElement;
import dics.elements.dtd.Dictionary;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.E;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.Pardefs;
import dics.elements.dtd.R;
import dics.elements.dtd.S;
import dics.elements.dtd.Sdef;
import dics.elements.dtd.Sdefs;
import dics.elements.dtd.Section;
import dictools.utils.DictionaryReader;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class AddGenderAdj {

    
    private String[] arguments;
    
    private String morphDic;
    
    private String bilDic;
    
    private String out;

    
    public void processArguments() {
        morphDic = arguments[1];
        bilDic = arguments[2];
        out = arguments[3];
    }

    
    public void doAddGender() {
        processArguments();

        DictionaryReader reader = new DictionaryReader(morphDic);
        System.err.println("Reading morphological '" + morphDic + "'");
        Dictionary dic = reader.readDic();
        // dic.printXML("morf-es.dix");

        HashMap<String, S> ng = new HashMap<String, S>();

        Pardefs pars = dic.pardefs;

        for (Section section : dic.sections) {
            for (E ee : section.elements) {
                String lemma = ee.lemma;
                if (lemma != null) {
                    String parName = ee.getMainParadigmName();
                    if ((parName != null) && parName.endsWith("__adj")) {
                        Pardef par = pars.getParadigmDefinition(parName);
                        if (par != null) {
                            for (E eepar : par.elements) {
                                R r = eepar.getFirstP().r;
                                for (DixElement er : r.children) {
                                    if (er instanceof S) {
                                        S s = (S) er;
                                        String sv = er.getValue();
                                        if (sv.equals("m") || sv.equals("f") || sv.equals("mf")) {
                                            ng.put(lemma, s);
                                        // System.err.println(lemma + "
                                        // (" + sv + ")");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.err.println(ng.size() + " adjectives read.");

        DictionaryReader reader2 = new DictionaryReader(bilDic);
        Dictionary bil = reader2.readDic();

        Sdefs sdefs = bil.sdefs;
        Sdef n = new Sdef("n");
        Sdef m = new Sdef("m");
        Sdef f = new Sdef("f");
        Sdef mf = new Sdef("mf");
        Sdef gd = new Sdef("GD");
        sdefs.elements.add(n);
        sdefs.elements.add(m);
        sdefs.elements.add(f);
        sdefs.elements.add(mf);
        sdefs.elements.add(gd);

        int genderFound = 0;
        int genderNotFound = 0;
        for (Section section : bil.sections) {
            for (E ee : section.elements) {
                if (!ee.containsRegEx()) {
                    ContentElement rightSide = ee.getFirstPart("R");

                    if (ee.isFirstSymbol("L", "adj")) {
                        String text = rightSide.getValue();

                        S gender = ng.get(text);
                        if (gender != null) {
                            // System.err.println(text + " (" +
                            // gender.getValue() + ")");
                            genderFound++;

                            S newGender = null;

                            if (gender.getValue().equals("mf")) {
                                newGender = new S("mf");
                            }
                            if (gender.getValue().equals("m") || gender.getValue().equals("f")) {
                                ee.restriction="LR";
                                newGender = new S("GD");
                            }

                            rightSide.children.add(newGender);
                        // and remove par element if NC
			    /*
                         * ParElement par = null; for (DixElement e :
                         * ee.children) { if (e instanceof
                         * ParElement) { par = (ParElement)e; } }
                         * 
                         * ee.children.remove(par);
                         */
                        } else {
                            genderNotFound++;
                            System.err.println("(" + genderNotFound + ") I could not find gender for '" + text + "'");
                        }
                    }
                }
            }
        }

        System.err.println("I found gender for " + genderFound + " lemmas.");
        System.err.println("I could not find gender for " + genderNotFound + " lemmas (see addgender.err).");

        System.err.println("Updated bilingual dictionary: '" + out + "'");
        bil.printXML(out, dictools.utils.DicOpts.STD);
    }

    /**
     * @return the arguments
     */
    public String[] getArguments() {
        return arguments;
    }

    /**
     * @param arguments
     *                the arguments to set
     */
    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }
}
