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

import dics.elements.dtd.DixElement;
import dics.elements.utils.ElementList;
import dics.elements.utils.Msg;
import dictools.crossmodel.CrossAction;
import dictools.crossmodel.CrossModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class CrossModelProcessor {

    /**
     * 
     */
    private State startState;
    /**
     * 
     */
    private CrossActionData bestAction;
    /**
     * 
     */
    private Msg msg;

    /**
     * 
     * @param crossModel
     */
    public CrossModelProcessor(CrossModel crossModel, Msg msg) {
        this.msg = msg;
        startState = new State("^start");
        String str;
        HashMap<String, CrossAction> patterns = new HashMap<String, CrossAction>();

        for (CrossAction crossAction : crossModel.getCrossActions()) {
            crossAction.print(msg);
            ElementList eList = crossAction.getPattern().getSequence();
            str = getElementListString(eList);
            if (!patterns.containsKey(str)) {
                patterns.put(str, crossAction);
                add(eList, crossAction);
            } else {
                CrossAction cA = patterns.get(str);
                msg.err("Duplicated pattern: '" + crossAction.getId() + "' and '" + cA.getId() + "' are the same (will be ignored).");
            }
        }
    }

    /**
     *
     */
    private void printSequence(Msg msg, ArrayList<DixElement> ee) {
        msg.log("[]");
        for (DixElement e : ee) {
            msg.log("<" + e.getValue() + "> ");
        }
        msg.log("]");

    }

    /**
     * 
     * @param eList
     * @param crossAction
     */
    public void add(ElementList eList, CrossAction crossAction) {
        msg.log("Adding pattern '" + crossAction.getId() + "'...");
        printSequence(msg, eList);
        this.startState.add(eList, crossAction, 0);
    }

    /**
     * 
     * @param entries
     * @return The best action set
     */
    public CrossActionData getBestActionSet(CrossAction entries) {
        ElementList patternSequence = entries.getPattern().getSequence();
        //patternSequence.print();
        Variables vars = new Variables();
        CrossActionDataList crossActionDataList = new CrossActionDataList();
        startState.getActionSetList(patternSequence, 0, crossActionDataList, vars);
        if (crossActionDataList.size() > 0) {
            printSequence(msg, entries.getPattern().getSequence());
            msg.log("\n" + crossActionDataList.size() + " candidates: ");
            Iterator it = crossActionDataList.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                CrossActionData cad = crossActionDataList.get(key);
                msg.log(cad.getCrossAction().getId() + ",");
            }
            msg.log("\n");

            bestAction = crossActionDataList.getBestActionSet();
            msg.log("Best action: " + bestAction.getCrossAction().getId() + "\n");
            this.setBestAction(bestAction);
            return getBestAction();
        } else {
            return null;
        }
    }

    /**
     * 
     * @param eList
     * @return A string of elements
     */
    private String getElementListString(ElementList eList) {
        String str = "";
        for (DixElement e : eList) {
            str += "<" + e.getValue() + ">";
        }
        return str;
    }

    /**
     * 
     * @return Undefined
     */
    public Msg getMsg() {
        return msg;
    }

    /**
     * 
     * @param msg
     */
    public void setMsg(Msg msg) {
        this.msg = msg;
    }

    /**
     * 
     * @return Undefined
     */
    public CrossActionData getBestAction() {
        return bestAction;
    }

    /**
     * 
     * @param bestAction
     */
    public void setBestAction(CrossActionData bestAction) {
        this.bestAction = bestAction;
    }
}
