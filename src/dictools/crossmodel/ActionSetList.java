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
package dictools.crossmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import dics.elements.utils.Msg;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class ActionSetList extends HashMap<String, ActionSet> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 
     */
    private ActionSet bestActionSet;
    /**
     * 
     */
    private Msg msg;

    /**
     * 
     */
    public ActionSetList() {

    }

    /**
     * Constructor
     */
    public ActionSetList(Msg msg) {
        this.msg = msg;
    }

    /**
     * 
     * 
     */
    public void print() {
        Set keySet = keySet();
        Iterator it = keySet.iterator();
        int max = size();
        int cont = 1;

        msg.log("{ ");
        while (it.hasNext()) {
            String key = (String) it.next();
            ActionSet actionSet = get(key);
            msg.log(actionSet.getName() + " (" + actionSet.getPatternLength() + "/" + actionSet.getNumberOfConstants() + ")");
            if (cont < max) {
                msg.log(", ");
                cont++;
            }
        }
        msg.log(" }\n");
    }

    /**
     * 
     * @return Undefined    
     */
    public ActionSet getBestActionSet() {
        ActionSet defaultActionSet = null;
        boolean defaultCanBeApplied = false;
        ArrayList<ActionSet> actionSetList = new ArrayList<ActionSet>();
        Iterator it = this.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            ActionSet actionSet = get(key);
            if (actionSet.getName().equals("default")) {
                defaultCanBeApplied = true;
                defaultActionSet = actionSet;
            } else {
                actionSetList.add(actionSet);
            }
        }

        ArrayList<ActionSet> longestPatterns = this.getLongestPatterns(actionSetList);
        ArrayList<ActionSet> mostConcrete = this.getMostConcretePatterns(longestPatterns);
        ArrayList<ActionSet> finalList = mostConcrete;

        if (finalList.size() > 0) {
            ActionSet action = finalList.get(0);
            this.setBestActionSet(action);
        } else {
            if (defaultCanBeApplied) {
                setBestActionSet(defaultActionSet);
            }
        }
        return bestActionSet;
    }

    /**
     * 
     * @param actionSetList
     * @return Undefined     
     */
    private ArrayList<ActionSet> getLongestPatterns(ArrayList<ActionSet> actionSetList) {
        int maxLength = 0;
        ArrayList<ActionSet> longestPatterns = new ArrayList<ActionSet>();
        for (ActionSet actionSet : actionSetList) {
            int patternLength = actionSet.getPatternLength();
            if (patternLength > maxLength) {
                longestPatterns = new ArrayList<ActionSet>();
                longestPatterns.add(actionSet);
                this.setBestActionSet(actionSet);
                maxLength = patternLength;
            } else {
                if (patternLength == maxLength) {
                    longestPatterns.add(actionSet);
                }
            }
        }
        return longestPatterns;
    }

    /**
     * 
     * @param actionSetList
     * @return List of most concrete patterns
     */
    private ArrayList<ActionSet> getMostConcretePatterns(ArrayList<ActionSet> actionSetList) {
        int maxNConstants = 0;
        ArrayList<ActionSet> mostConcrete = new ArrayList<ActionSet>();
        for (ActionSet actionSet : actionSetList) {
            int nConstants = actionSet.getNumberOfConstants();
            if (nConstants > maxNConstants) {
                mostConcrete = new ArrayList<ActionSet>();
                mostConcrete.add(actionSet);
                this.setBestActionSet(actionSet);
                maxNConstants = nConstants;
            } else {
                if (nConstants == maxNConstants) {
                    mostConcrete.add(actionSet);
                }
            }
        }
        return mostConcrete;
    }

    /**
     * 
     * @return Undefined     
     */
    private boolean isDefinedBestAction() {
        return this.bestActionSet != null;
    }

    /**
     * 
     * @param action
     */
    private void setBestActionSet(ActionSet action) {
        this.bestActionSet = action;

    }
}
