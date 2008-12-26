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
package dictools.cmproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class CrossActionDataList extends HashMap<String, CrossActionData> {

    /**
     * 
     */
    private CrossActionData bestActionSet;

    /**
     * 
     */
    public CrossActionDataList() {
        super();
    }

    /**
     * 
     * @return Cross action data
     */
    public CrossActionData getBestActionSet() {
        CrossActionData defaultCAD = null;
        boolean defaultCanBeApplied = false;
        ArrayList<CrossActionData> actionList = new ArrayList<CrossActionData>();

        Iterator it = this.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            CrossActionData cad = this.get(key);
            String id = cad.getCrossAction().getId();
            int l = cad.getCrossAction().getPattern().getLength();
            int n = cad.getCrossAction().getPattern().getNConstants();
            if (cad.getCrossAction().getId().equals("default")) {
                defaultCanBeApplied = true;
                defaultCAD = cad;
            } else {
                actionList.add(cad);
            }
        }
        ArrayList<CrossActionData> longestPatterns = this.getLongestPatterns(actionList);
        ArrayList<CrossActionData> mostConcrete = this.getMostConcretePatterns(longestPatterns);
        ArrayList<CrossActionData> finalList = mostConcrete;
        if (finalList.size() > 0) {
            CrossActionData bestCAD = finalList.get(0);
            this.setBestActionSet(bestCAD);
        } else {
            if (defaultCanBeApplied) {
                setBestActionSet(defaultCAD);
            }
        }
        return bestActionSet;
    }

    /**
     * 
     * @param actionList
     * @return Undefined
     */
    private ArrayList<CrossActionData> getLongestPatterns(ArrayList<CrossActionData> actionList) {
        int maxLength = 0;
        ArrayList<CrossActionData> longestPatterns = new ArrayList<CrossActionData>();
        for (CrossActionData cad : actionList) {
            int patternLength = cad.getCrossAction().getPattern().getLength();
            if (patternLength > maxLength) {
                longestPatterns = new ArrayList<CrossActionData>();
                longestPatterns.add(cad);
                this.setBestActionSet(cad);
                maxLength = patternLength;
            } else {
                if (patternLength == maxLength) {
                    longestPatterns.add(cad);
                }
            }
        }
        return longestPatterns;
    }

    /**
     * 
     * @param actionSetList
     * @return Undefined
     */
    private ArrayList<CrossActionData> getMostConcretePatterns(ArrayList<CrossActionData> actionList) {
        int maxNConstants = 0;
        ArrayList<CrossActionData> mostConcrete = new ArrayList<CrossActionData>();
        for (CrossActionData cad : actionList) {
            int nConstants = cad.getCrossAction().getPattern().getNConstants();
            if (nConstants > maxNConstants) {
                mostConcrete = new ArrayList<CrossActionData>();
                mostConcrete.add(cad);
                this.setBestActionSet(cad);
                maxNConstants = nConstants;
            } else {
                if (nConstants == maxNConstants) {
                    mostConcrete.add(cad);
                }
            }
        }
        return mostConcrete;
    }

    /**
     * 
     * @param bestActionSet
     */
    public void setBestActionSet(CrossActionData bestActionSet) {
        this.bestActionSet = bestActionSet;
    }
}
