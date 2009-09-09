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

import java.util.ArrayList;
import java.util.HashMap;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.Pardefs;
import dics.elements.utils.DicSet;

/**
 * @author Enrique Benimeli Bofarull
 * 
 */
public class unused__DicEquivPar {

    /**
     * 
     */
    private DicSet dicSet;
    /**
     * 
     */
    private HashMap<String, ArrayList<Pardef>> equivA;
    /**
     * 
     */
    private HashMap<String, ArrayList<Pardef>> equivC;

    /**
     * 
     * @param dics
     */
    public unused__DicEquivPar(DicSet dics) {
        dicSet = dics;
    }

    /**
     * 
     * @param mon
     */
    public unused__DicEquivPar(Dictionary mon) {
        DicSet ds = new DicSet(mon);
        dicSet = ds;
    }

    /**
     * 
     * 
     */
    public void findEquivalents() {
        System.err.println("Searching for equivalents...");
        Dictionary monA = dicSet.getMon1();
        Dictionary monC = dicSet.getMon2();

        Pardefs pardefsA = monA.getPardefsElement();
        Pardefs pardefsC = monC.getPardefsElement();

        System.err.println("Searching for equivalents in monA...");
        equivA = this.findEquivalents(pardefsA);
        System.err.println("Searching for equivalents in monC...");
        equivC = this.findEquivalents(pardefsC);
    }

    public HashMap<String, ArrayList<Pardef>> findEquivalentsA() {
        System.err.println("Searching for equivalents...");
        Dictionary monA = dicSet.getMon1();

        Pardefs pardefsA = monA.getPardefsElement();

        System.err.println("Searching for equivalents in monA...");
        equivA = this.findEquivalents(pardefsA);
        return equivA;
    }

    /**
     * 
     * @return Undefined         */
    public HashMap<String, ArrayList<Pardef>> getEquivalentsA() {
        return equivA;
    }

    /**
     * 
     * @return Undefined         */
    public HashMap<String, ArrayList<Pardef>> getEquivalentsC() {
        return equivC;
    }

    /**
     * 
     * @param pardefs
     */
    private HashMap<String, ArrayList<Pardef>> findEquivalents(
            Pardefs pardefs) {
        ArrayList<Pardef> pardefscopy = new ArrayList<Pardef>(
                pardefs.getPardefElements());
        HashMap<String, ArrayList<Pardef>> equivalents = new HashMap<String, ArrayList<Pardef>>();

        System.err.println(pardefs.getPardefElements().size() + " paradigmas");
        int nEquiv = 0;
        for (Pardef pardefA1 : pardefs.getPardefElements()) {
            // lista de equivalentes

            for (Pardef pardefA2 : pardefscopy) {
                // System.err.println("Comparando " + pardefA1.getName() + " y "
                // + pardefA2.getName() + " ...");
                if (!pardefA1.getName().equals(pardefA2.getName()) && pardefA1.contentEquals(pardefA2)) {
                    System.err.println(pardefA1.getName() + " es equivalente a " + pardefA2.getName());
                    nEquiv++;
                    /*
                     * System.err.println("pardefA1:");
                     * System.err.println(pardefA1.toString());
                     * System.err.println("pardefA2:");
                     * System.err.println(pardefA2.toString());
                     */

                    if (!equivalents.containsKey(pardefA1.getName())) {
                        ArrayList<Pardef> equivList = new ArrayList<Pardef>();
                        equivList.add(pardefA2);
                        equivalents.put(pardefA1.getName(), equivList);
                    } else {
                        ArrayList<Pardef> list = equivalents.get(pardefA1.getName());
                        list.add(pardefA2);
                        equivalents.put(pardefA1.getName(), list);
                    }
                } else {
                // System.err.println(pardefA1.getName() + " NO es
                // equivalente a " + pardefA2.getName() );
                }
            }
        }
        System.err.println((nEquiv / 2) + " paradigmas equivalentes.");
        return equivalents;
    }

    /**
     * 
     * @param parName
     * @param equivPar
     * @param category
     * @return Undefined
     */
    public static Pardef getEquivalentParadigm(String parName,
            HashMap<String, ArrayList<Pardef>> equivPar,
            String category) {
        // System.err.println("Searching for equivalent paradigm with category
        // '" + category + "' for '" + lemma + "'");
        ArrayList<Pardef> list = equivPar.get(parName);
        if (list != null) {
            for (Pardef pE : list) {
                if (pE.hasCategory(category)) {
                    return pE;
                }
            }
        }
        return null;
    }
}
