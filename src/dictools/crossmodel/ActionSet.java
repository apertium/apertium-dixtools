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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dics.elements.dtd.S;
import dics.elements.utils.DicOpts;
import dics.elements.utils.ElementList;
import dics.elements.utils.Msg;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class ActionSet extends ArrayList<Action> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 
     */
    private String name;
    /**
     * 
     */
    private int numberOfConstants = 0;
    /**
     * 
     */
    private Integer patternLength = 0;
    /**
     * 
     */
    private HashMap<String, ElementList> tails;
    /**
     * 
     */
    private CrossAction crossAction;

    /**
     * 
     * @return Undefined         
     */
    public int getNumberOfConstants() {
        return numberOfConstants;
    }

    /**
     * 
     * @return Undefined         
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @param numberOfConstants
     */
    public void setNumberOfConstants(int numberOfConstants) {
        this.numberOfConstants = numberOfConstants;
    }

    /**
     * 
     * 
     */
    public void incrementNumberOfConstants() {
        numberOfConstants += 1;
    }

    /**
     * 
     */
    public void incrementPatternLength() {
        this.patternLength += 1;
    }

    /**
     * 
     * 
     */
    public void print(Msg msg) {
        for (Action action : this) {
            action.print(msg);
        }
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    public void printXML(Appendable dos, DicOpts opt) throws IOException {
        dos.append("<action-set>\n");
        for (Action action : this) {
            action.printXML(dos, opt);
        }
        dos.append("</action-set>\n\n");
    }

    /**
     * 
     * @return Undefined         */
    public int getPatternLength() {
        return patternLength;
    }

    /**
     * 
     */
    public void calculatePatternLength() {
        // 3 because of 3 <b> tags
        // 2 because of 2 restrictions
        patternLength = patternLength - getNumberOfTails() - 2 - 3;
    }

    /**
     * 
     * @return Undefined         */
    private Integer getNumberOfTails() {
        int nTails = 0;
        CrossAction cA = getCrossAction();

        ArrayList<S> e1L = cA.getPattern().getAB().getSymbols("L");
        ArrayList<S> e1R = cA.getPattern().getAB().getSymbols("R");
        ArrayList<S> e2L = cA.getPattern().getBC().getSymbols("L");
        ArrayList<S> e2R = cA.getPattern().getBC().getSymbols("R");

        if (containsTail(e1L)) {
            nTails++;
        }
        if (containsTail(e1R)) {
            nTails++;
        }
        if (containsTail(e2L)) {
            nTails++;
        }
        if (containsTail(e2R)) {
            nTails++;
        }

        return new Integer(nTails);
    }

    /**
     * 
     * @param list
     * @return Undefined         
     */
    private boolean containsTail(ArrayList<S> list) {
        for (S s : list) {
            if (s.getValue().equals("0")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return Undefined         
     */
    public int getNumberOfRestrictions() {
        CrossAction cA = getCrossAction();
        int nR = 0;
        if (cA.getPattern().getAB().hasRestriction()) {
            nR++;
        }
        if (cA.getPattern().getBC().hasRestriction()) {
            nR++;
        }
        return nR;
    }

    /**
     * 
     * @param patternLength
     */
    public void setPatternLength(Integer patternLength) {
        this.patternLength = patternLength;
    }

    /**
     * @return the tails
     */
    public HashMap<String, ElementList> getTails() {
        return tails;
    }

    /**
     * @param tails
     *                the tails to set
     */
    public void setTails(HashMap<String, ElementList> tails) {
        this.tails = tails;
    }

    /**
     * @return the crossAction
     */
    public CrossAction getCrossAction() {
        return crossAction;
    }

    /**
     * @param crossAction
     *                the crossAction to set
     */
    public void setCrossAction(CrossAction crossAction) {
        this.crossAction = crossAction;
    }

    /**
     * 
     * @param definedVars
     * @param patternID
     * @return true if the action set is valid
     */
    public boolean isValid(HashMap<String, String> definedVars, String patternID) {
        boolean errorsFound = false;
        for (Action a : this) {
            if (!a.isValid(definedVars, patternID)) {
                errorsFound = true;
            }
        }
        if (errorsFound) {
            return false;
        } else {
            return true;
        }
    }
}
