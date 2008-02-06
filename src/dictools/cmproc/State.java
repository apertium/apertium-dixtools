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

import dics.elements.dtd.Element;
import dics.elements.dtd.SElement;
import dics.elements.utils.ElementList;
import dics.elements.utils.SElementList;
import dictools.crossmodel.CrossAction;
import java.util.Iterator;
import java.util.regex.Matcher;

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
    public State(final String value) {
        this.value = value;
        states = new StateSet();
    }

    /**
     * 
     * @param pattern
     * @param crossAction
     * @param i
     */
    public final void add(final ElementList pattern, final CrossAction crossAction, int i) {
        if (i < pattern.size()) {
            final Element e = pattern.get(i);
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
    private final boolean isConstant(final Element e) {
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
    private final boolean increments(final Element e) {
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
    private final boolean stringMatchesPattern(final String value, final String patternString) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(patternString);
        Matcher matcher = p.matcher(value);
        return (matcher.find());
    }

    /**
     * 
     * @param patternSequence
     * @param i
     * @param cadl
     * @param varsSrc
     */
    public final void getActionSetList(final ElementList patternSequence, int i, CrossActionDataList cadl, final Variables varsSrc) {
        if (i < patternSequence.size()) {
            Variables vars = new Variables(varsSrc);
            String v = ((Element) patternSequence.get(i)).getValue();
            String state_v = this.getValue();

            if (state_v.equals("^start")) {
                this.continue_processing(patternSequence, i - 1, cadl, vars);
            }

            if (state_v.equals("^end")) {
                CrossAction ca = getCrossAction();
                if (ca != null) {
                    CrossActionData cad = new CrossActionData(ca, vars);
                    cadl.put(ca.getId(), cad);
                    //System.out.println("Patrón aceptado! : " + ca.getId());
                    //vars.print();
                }
            }

            if (v.equals("^LR") && state_v.equals("^LRRL")) {
                this.continue_processing(patternSequence, i, cadl, vars);
            }

            if (v.equals("^RL") && state_v.equals("^LRRL")) {
                this.continue_processing(patternSequence, i, cadl, vars);
            }

            if (v.equals("^LRRL") && state_v.equals("^LRRL")) {
                this.continue_processing(patternSequence, i, cadl, vars);
            }

            if (!v.startsWith("^") && state_v.startsWith("X")) {
                if (process_X(state_v, v, vars)) {
                    continue_processing(patternSequence, i, cadl, vars);
                }
            }

            if (state_v.startsWith("S")) {
                continue_processing(patternSequence, i, cadl, vars);
                int j = process_S(state_v, v, vars, patternSequence, i);
                if (j != -1) {
                    continue_processing(patternSequence, j, cadl, vars);
                }
            }

            if (state_v.equals("^?") || state_v.equals(v) || state_v.equals("^b")) {
                continue_processing(patternSequence, i, cadl, vars);
            }

            if (state_v.equals("^*")) {
                Element e = patternSequence.get(i);
                while (!e.getValue().equals("^b")) {
                    i++;
                    e = patternSequence.get(i);
                }
                continue_processing(patternSequence, i - 1, cadl, vars);
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
            State st = (State) states.get(it.next());
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
    private final boolean process_X(String state_v, String v, Variables vars) {
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
    private final int process_S(String state_v, String v, Variables vars, ElementList patternSequence, int i) {
        boolean continueProcessing = false;
        Element e = patternSequence.get(i);
        SElementList eList = new SElementList();
        int index = 0;
        while (i < patternSequence.size() && !e.getValue().equals("^b")) {
            SElement sE = new SElement(e.getValue());
            eList.add(sE);
            index++;
            i++;
            e = patternSequence.get(i);
        }
        if (!vars.containsKey(state_v)) {
            vars.put(state_v, eList);
            continueProcessing = true;
        } else {
            SElementList existingValue = (SElementList) vars.get(state_v);
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
