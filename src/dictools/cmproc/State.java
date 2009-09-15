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
import java.util.Iterator;
import java.util.regex.Matcher;

import dics.elements.dtd.DixElement;
import dics.elements.dtd.S;
import dics.elements.utils.ElementList;
import dictools.crossmodel.CrossAction;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class State {

    /**
     * 
     */
    private String value;
    /**
     * 
     */
    private StateSet states;
    /**
     * 
     */
    private CrossAction crossAction;

    /**
     * 
     */
    public State() {
        states = new StateSet();
    }

    /**
     * 
     * @param value
     */
    public State(String value) {
        this.value = value;
        states = new StateSet();
    }

    /**
     * 
     * @param pattern
     * @param crossAction
     * @param i
     */
    public void add(ElementList pattern, CrossAction crossAction, int i) {
        if (i < pattern.size()) {
            DixElement e = pattern.get(i);
            if (this.isConstant(e)) {
                crossAction.getPattern().incrementNConstants();
            }
            if (this.increments(e)) {
                crossAction.getPattern().incrementLength();
            }
            State state;
            if (!states.containsKey(e.getValue())) {
                state = new State(e.getValue());
                states.put(state.getValue(), state);
            } else {
                state = states.get(e.getValue());
            }
            if (state.getValue().equals("^end")) {
                state.setCrossAction(crossAction);
            } else {
                state.add(pattern, crossAction, i + 1);
            }
        }
    }

    /**
     * 
     * @param e
     * @return true if 'e' is a constant
     */
    private boolean isConstant(DixElement e) {
        String v = e.getValue();
        if (v.startsWith("^")) {
            return false;
        }
        if (this.stringMatchesPattern(v, "X[0-9]+")) {
            return false;
        }
        if (this.stringMatchesPattern(v, "S[0-9]+")) {
            return false;
        }
        return true;
    }

    /**
     * 
     * @param e
     * @return true if 'e' forces the increment of pattern length
     */
    private boolean increments(DixElement e) {
        String v = e.getValue();
        if (v.equals("^b") || v.equals("^end")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 
     * @param v
     * @param patternString
     * @return true if 'value' macthes pattern 'patternString'
     */
    private boolean stringMatchesPattern(String value, String patternString) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(patternString);
        Matcher matcher = p.matcher(value);
        return (matcher.find());
    }

    //public static int freq[] = new int[20];
    // Typical run gives [10278, 10818, 1090, 1104, 18580, 92474, 20438, 61534, 20658, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
   //                                0           1         2         3        4         5         6          7            8
    // Therefore the if statement have beeen reorded accordingly
    
    
    /**
     * 
     * @param patternSequence
     * @param i
     * @param cadl
     * @param varsSrc
     */
    public final void getActionSetList(ElementList patternSequence, int i, CrossActionDataList cadl, Variables varsSrc) {
        if (i < patternSequence.size()) {
            Variables vars = null;
            String v = (patternSequence.get(i)).getValue();
            String state_v = getValue();


            
            //if (state_v.equals("^?") || state_v.equals("^b")) {
            if (state_v == "^?" || state_v=="^b") { // == is OK, state_v is an interned string
                vars = new Variables(varsSrc);
                continue_processing(patternSequence, i, cadl, vars);
                //freq[7]++; // happens 61534 times
                return;
            }

            
            
            if (state_v.startsWith("X")) { //superflous: && !v.startsWith("^")) {
                vars = new Variables(varsSrc);
                if (process_X(state_v, v, vars)) {
                    continue_processing(patternSequence, i, cadl, vars);
                }
                //freq[5]++; // happens 92474 times
                return;
            }

            
            //if (state_v.equals("^*")) {
            if (state_v == "^*") {  // == is OK, state_v is an interned string
                vars = new Variables(varsSrc);
                DixElement e = patternSequence.get(i);
                while (!e.getValue().equals("^b")) {
                    i++;
                    e = patternSequence.get(i);
                }
                continue_processing(patternSequence, i - 1, cadl, vars);
                //freq[8]++; // happens 20658 times
                return;
            }

/*
            if (state_v.equals("^LRRL") && v.equals("^LR")) {
                vars = new Variables(varsSrc);
                continue_processing(patternSequence, i, cadl, vars);
                //freq[2]++; ; // happens 1090 times
                return;
            }

            if (state_v.equals("^LRRL") && v.equals("^RL")) {
                vars = new Variables(varsSrc);
                continue_processing(patternSequence, i, cadl, vars);
                //freq[3]++; // happens 1104 times
                return;
            }

            if (state_v.equals("^LRRL") && v.equals("^LRRL")) {
                vars = new Variables(varsSrc);
                continue_processing(patternSequence, i, cadl, vars);
                //freq[4]++;  // happens 18580 times
                return;
            }
*/

            
            //if (state_v.equals("^LRRL") && (v.equals("^LRRL") || v.equals("^LR") || v.equals("^RL"))) {
            // == is OK, state_v is an interned string
            if (state_v == "^LRRL" && (v.equals("^LRRL") || v.equals("^LR") || v.equals("^RL"))) {
                vars = new Variables(varsSrc);
                continue_processing(patternSequence, i, cadl, vars);
                //freq[4]++;  // happens apporx 20000 times
                return;
            }
            
            
            if (state_v.startsWith("S")) {
                vars = new Variables(varsSrc);
                continue_processing(patternSequence, i, cadl, vars);
                int j = process_S(state_v, v, vars, patternSequence, i);
                if (j != -1) {
                    continue_processing(patternSequence, j, cadl, vars);
                }
                //freq[6]++; // happens 20438 times
                return;
            }
            
            // == is OK, state_v is an interned string
            if (state_v == "^start") {
                vars = new Variables(varsSrc);
                continue_processing(patternSequence, i - 1, cadl, vars);
                //freq[0]++; // happens 10278 times
                return;
            }

            // == is OK, state_v is an interned string
            if (state_v == "^end") {
                vars = new Variables(varsSrc);
                CrossAction ca = getCrossAction();
                if (ca != null) {
                    CrossActionData cad = new CrossActionData(ca, vars);
                    cadl.put(ca.getId(), cad);
                //System.err.println("Patrón aceptado! : " + ca.getId());
                //vars.print();
                }
                //freq[1]++; // happens 10818 times
                return;
            }

            
            if (state_v.equals(v)) {
                vars = new Variables(varsSrc);
                continue_processing(patternSequence, i, cadl, vars);
                //freq[9]++; // happens 0 times in test!
                return;
            }
           
        }
    }

    /**
     * 
     * @param patternSequence
     * @param i
     * @param cadl
     * @param vars
     */
    private final void continue_processing(ElementList patternSequence, int i, CrossActionDataList cadl, Variables vars) {
        Iterator it = states.keySet().iterator();
        while (it.hasNext()) {
            State st = states.get(it.next());
            st.getActionSetList(patternSequence, i + 1, cadl, vars);
        }
    }

    /**
     * 
     * @param state_v
     * @param v
     * @param vars
     * @return true if the process must continue from current state
     */
    private boolean process_X(String state_v, String v, Variables vars) {
        boolean continueProcessing = false;
        if (!vars.containsKey(state_v)) {
            vars.put(state_v, v);
            continueProcessing = true;
        } else {
            String existingValue = (String) vars.get(state_v);
            if (existingValue.equals(v)) {
                continueProcessing = true;
            }
        }
        return continueProcessing;
    }

    /**
     * 
     * @param state_v
     * @param v
     * @param vars
     * @param patternSequence
     * @param i
     * @param cadl
     * @return Index
     */
    private int process_S(String state_v, String v, Variables vars, ElementList patternSequence, int i) {
        boolean continueProcessing = false;
        DixElement e = patternSequence.get(i);
        ArrayList<S> eList = new ArrayList<S>();
        int index = 0;
        while (i < patternSequence.size() && !e.getValue().equals("^b")) {
            S sE = new S(e.getValue());
            eList.add(sE);
            index++;
            i++;
            e = patternSequence.get(i);
        }
        if (!vars.containsKey(state_v)) {
            vars.put(state_v, eList);
            continueProcessing = true;
        } else {
            ArrayList<S> existingValue = (ArrayList<S>) vars.get(state_v);
            if (existingValue.equals(eList)) {
                continueProcessing = true;
            }
        }
        if (continueProcessing) {
            return (i - 1);
        } else {
            return -1;
        }
    }

    /**
     * 
     * @return The state v
     */
    public String getValue() {
        return value;
    }

    /**
     * 
     * @param value
     */
    public void setValue(String value) {
      if (value!=null) value = value.intern(); // interning for optimization
        this.value = value;
    }

    /**
     * 
     * @return Next states
     */
    public StateSet getStates() {
        return states;
    }

    /**
     * 
     * @param states
     */
    public void setStates(StateSet states) {
        this.states = states;
    }

    /**
     * 
     * @return The cross action
     */
    public CrossAction getCrossAction() {
        return crossAction;
    }

    /**
     * 
     * @param crossAction
     */
    public void setCrossAction(CrossAction crossAction) {
        this.crossAction = crossAction;
    }
}
