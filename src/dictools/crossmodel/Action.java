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

import dics.elements.dtd.ContentElement;
import java.io.IOException;

import dics.elements.dtd.EElement;
import dics.elements.dtd.Element;
import dics.elements.dtd.SElement;
import dics.elements.dtd.TextElement;
import dics.elements.utils.DicOpts;
import dics.elements.utils.Msg;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class Action {

    /**
     * 
     */
    private EElement e;
    /**
     * 
     */
    private String name;
    /**
     * 
     */
    private Integer patternLength;
    /**
     * 
     */
    private int numberOfConstants = 0;

    /**
     * 
     * 
     */
    public Action() {

    }

    /**
     * 
     * @param e
     */
    public Action(final EElement e) {
        this.e = e;
    }

    /**
     * 
     * @param action
     */
    public void setAction(final EElement action) {
        e = action;
    }

    /**
     * 
     * 
     */
    public final void print(Msg msg) {
        if (e != null) {
            msg.log("action:\n");
            getE().print("L", msg);
            getE().print("R", msg);
        }
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    public final void printXML(Appendable dos, DicOpts opt) throws IOException {
        if (e != null) {
            dos.append("\t<action>\n");
            getE().printXML(dos, opt);
            dos.append("\t</action>\n");
        }

    }

    /**
     * 
     * @return Undefined         */
    public EElement getE() {
        return e;
    }

    /**
     * 
     * @param e
     */
    public void setE(final EElement e) {
        this.e = e;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @param name
     *                the name to set
     */
    public final void setName(String name) {
        this.name = name;
    }

    public final Integer getPatternLength() {
        return patternLength;
    }

    public final void setPatternLength(Integer patternLength) {
        this.patternLength = patternLength;
    }

    /**
     * 
     * @return Undefined         */
    public final int getNumberOfConstants() {
        return numberOfConstants;
    }

    /**
     * 
     * @param numberOfConstants
     */
    public final void setNumberOfConstants(int numberOfConstants) {
        this.numberOfConstants = numberOfConstants;
    }

    /**
     * 
     * 
     */
    public final void incrementNumberOfConstants() {
        numberOfConstants++;
    }

    /**
     * 
     * @param definedVars
     * @param patternID
     * @return true if the Action is valid
     */
    public final boolean isValid(HashMap<String, String> definedVars, final String patternID) {
        boolean errorsFound = false;
        HashMap<String, String> definedVarsInAction = new HashMap<String, String>();

        getDefinedVarsElement(getE().getLeft(), definedVarsInAction);
        getDefinedVarsElement(getE().getRight(), definedVarsInAction);

        Iterator it = definedVarsInAction.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (!definedVars.containsKey(key)) {
                System.err.print("\nError in pattern '" + patternID + "': variable '" + key + "' was not defined in the pattern.");
                errorsFound = true;
            }
        }

        if (errorsFound) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 
     * @param ce
     * @param definedVars
     */
    private final void getDefinedVarsElement(ContentElement ce, HashMap<String, String> definedVars) {
        for (Element e : ce.getChildren()) {
            if (e instanceof TextElement) {
                String v = ((TextElement) e).getValue();
                if (v.startsWith("$") || v.startsWith("@")) {
                    definedVars.put(v, v);
                }
            }
            if (e instanceof SElement) {
                String v = ((SElement) e).getValue();
                if (v.startsWith("$") || v.startsWith("@")) {
                    definedVars.put(v, v);
                }
            }

        }
    }
}
