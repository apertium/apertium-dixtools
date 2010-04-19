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
package dictools.cross;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import dics.elements.dtd.ContentElement;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.E;
import dics.elements.dtd.L;
import dics.elements.dtd.P;
import dics.elements.dtd.R;
import dics.elements.dtd.S;
import dics.elements.dtd.T;
import dics.elements.dtd.TextElement;
import dics.elements.dtd.V;
import dictools.utils.DicOpts;
import dictools.utils.Msg;
import dictools.cmproc.Variables;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class CrossAction implements Comparable<CrossAction> {

    
    private String id;
    
    private Pattern pattern;
    
    private Variables vars;
    
    private ActionSet actionSet;
    
    private int occurrences;
    
    private int x = 0;
    
    private int s = 0;

    
    public CrossAction() {
        pattern = new Pattern();
        actionSet = new ActionSet();
    }

    /**
     * 
     * @param p
     */
    public void setPattern(Pattern p) {
        pattern = p;
    }

    /**
     * 
     * @return Undefined     
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * 
     * @return Undefined     
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    
    public void print(Msg msg) {
        if (pattern != null) {
            getPattern().print(msg);
        }
        if (actionSet != null) {
            getActionSet().print(msg);
        }
    }

    /**
     * 
     * @param dos
     * @param id
     * @throws java.io.IOException
     */
    public void printXML(OutputStreamWriter dos, int id, DicOpts opt) throws IOException {
        dos.append("<cross-action id=\"ND-" + id + "\">\n");
        getPattern().printXML(dos, opt);
        if (actionSet != null) {
            getActionSet().printXML(dos, opt);
        }
        dos.append("</cross-action>\n");
        dos.append("<!-- " + getOccurrences() + " entries like this -->\n\n");
    }

    /**
     * 
     * @return Undefined     */
    public ActionSet getActionSet() {
        return actionSet;
    }

    /**
     * 
     * @param actionSet
     */
    public void setActionSet(ActionSet actionSet) {
        this.actionSet = actionSet;
        actionSet.setName(getId());
    }

    /**
     * @return the occurrences
     */
    public int getOccurrences() {
        return occurrences;
    }

    /**
     * @param occurrences
     *                the occurrences to set
     */
    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }

    
    public void incrementOccurrences() {
        occurrences++;
    }

    
    public int compareTo(CrossAction anotherEElement)
            throws ClassCastException {
        if (anotherEElement == null) {
            return -1;
        }
        if (!(anotherEElement instanceof CrossAction)) {
            throw new ClassCastException("A CrossAction object expected.");
        }
        int occ1 = getOccurrences();
        int occ2 = (anotherEElement).getOccurrences();
        if (occ1 == occ2) {
            return 0;
        }
        if (occ1 > occ2) {
            return -1;
        }
        return 1;
    }

    
    public CrossAction rename() {
        // Renamed objects
        CrossAction rCrossAction = new CrossAction();
        rCrossAction.setId(this.getId());
        Pattern rPattern = new Pattern();
        ActionSet rActionSet = new ActionSet();
        rCrossAction.setPattern(rPattern);
        rCrossAction.setActionSet(rActionSet);

        HashMap<String, String> valueMap = new HashMap<String, String>();
        ContentElement leftAB = this.getPattern().getAB().getFirstPartAsL();
        ContentElement rightAB = this.getPattern().getAB().getFirstPartAsR();
        ContentElement leftBC = this.getPattern().getBC().getFirstPartAsL();
        ContentElement rightBC = this.getPattern().getBC().getFirstPartAsR();

        // Rename patterns
        ContentElement rLeftAB = this.renameContentElement(leftAB, valueMap);
        ContentElement rRightAB = this.renameContentElement(rightAB, valueMap);
        E rAB = new E();
        rAB.restriction=this.getPattern().getAB().restriction;
        rAB.v=this.getPattern().getAB().v;

        rAB.children.add(new P(new L(rLeftAB), new R(rRightAB)));

        ContentElement rLeftBC = this.renameContentElement(leftBC, valueMap);
        ContentElement rRightBC = this.renameContentElement(rightBC, valueMap);
        E rBC = new E();
        rBC.restriction=this.getPattern().getBC().restriction;
        rBC.v=this.getPattern().getBC().v;

        rBC.children.add(new P(new L(rLeftBC), new R(rRightBC)));

        rPattern.setAB(rAB);
        rPattern.setBC(rBC);

        // Rename actions
        for (Action a : this.getActionSet()) {
            ContentElement leftA = a.getE().getFirstPartAsL();
            ContentElement rightA = a.getE().getFirstPartAsR();
            ContentElement rLeftA = this.renameContentElement(leftA, valueMap);
            ContentElement rRightA = this.renameContentElement(rightA, valueMap);
            E rA = new E();
            if (a.getE().isRestrictionAuto()) {
                rA.restriction="auto";
            } else {
                if (a.getE().hasRestriction()) {
                    rA.restriction=a.getE().restriction;
                }
            }
            if (a.getE().v != null && !a.getE().v.equals("")) {
                rA.v = a.getE().v;
            }
            rA.children.add(new P(new L(rLeftA), new R(rRightA)));
            rActionSet.add(new Action(rA));
        }
        return rCrossAction;
    }

    /**
     * 
     * @param source
     * @param valueMap
     * @return A content element (l or r) with renamed variables
     */
    private ContentElement renameContentElement(ContentElement source, HashMap<String, String> valueMap) {
        ContentElement rContentElement;//  new ContentElement();
        try {
          rContentElement=source.getClass().newInstance();
        } catch (Exception ex) {
          Logger.getLogger(CrossAction.class.getName()).log(Level.SEVERE, null, ex);
          throw new IllegalStateException(ex);
        }
        for (DixElement e : source.children) {

            // text element
            if (e instanceof TextElement) {
                String v = ((TextElement) e).text;
                TextElement tE = new TextElement("");
                if (v.startsWith("$")) {
                    if (valueMap.containsKey(v)) {
                        tE.text = valueMap.get(v);
                    } else {
                        String nV = "X" + x;
                        x++;
                        valueMap.put(v, nV);
                        tE = new TextElement(nV);
                    }
                } else {
                    tE = new TextElement(v);
                }
                rContentElement.children.add(tE);
            }

            // 'v' element
            if (e instanceof V) {
                S rSE = new S();
                String v = ((V) e).getValue();
                if (v == null) {
                    rSE.setValue("?");
                } else {
                    if (valueMap.containsKey(v)) {
                        rSE.setValue(valueMap.get(v));
                    } else {
                        String nV = "X" + x;
                        x++;
                        valueMap.put(v, nV);
                        rSE.setValue(nV);
                    }
                }
                rContentElement.children.add(rSE);
            }

            // 't' element
            if (e instanceof T) {
                S rSE = new S();
                String v = ((T) e).name;
                if (v == null) {
                    rSE.setValue("*");
                } else {
                    if (valueMap.containsKey(v)) {
                        rSE.setValue(valueMap.get(v));
                    } else {
                        String nV = "S" + x;
                        s++;
                        valueMap.put(v, nV);
                        rSE.setValue(nV);
                    }
                }
                rContentElement.children.add(rSE);
            }

            // 's' element
            if (e instanceof S) {
                S rSE = new S();
                String v = ((S) e).getValue();
                if (valueMap.containsKey(v)) {
                    rSE.setValue(valueMap.get(v));
                } else {
                    String nV = "";
                    switch (this.getTypeOfVariable(v)) {
                        case 0:
                            nV = "X" + x;
                            x++;
                            break;
                        case 1:
                            nV = "S" + s;
                            s++;
                            break;
                        default:
                            nV = v;
                            break;
                    }
                    valueMap.put(v, nV);
                    rSE.setValue(nV);
                }
                rContentElement.children.add(rSE);
            }
        }
        return rContentElement;
    }

    /**
     * 
     * @param value
     * @return
     */
    private int getTypeOfVariable(String value) {
        if (this.stringMatchesPattern(value, "(\\$)[A-Za-z0-9]+")) {
            return 0;
        }
        if (this.stringMatchesPattern(value, "(\\@)[A-Za-z0-9]+")) {
            return 1;
        }
        if (value.equals("*")) {
            return 2;
        }
        if (value.equals("?")) {
            return 3;
        }
        return -1;
    }

    /**
     * 
     * @param value
     * @param patternString
     * @return
     */
    private boolean stringMatchesPattern(String value, String patternString) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(patternString);
        Matcher matcher = p.matcher(value);
        return (matcher.find());
    }

    /**
     * 
     * @return The variables
     */
    public Variables getVars() {
        return vars;
    }

    /**
     * 
     * @param vars
     */
    public void setVars(Variables vars) {
        this.vars = vars;
    }

    /**
     * 
     * @return true if the cross action isFirstSymbol valid
     */
    public boolean isValid() {
        if (!pattern.isValid()) {
            return false;
        }
        HashMap<String, String> definedVars = pattern.getDefinedVariables();
        if (!actionSet.isValid(definedVars, getId())) {
            return false;
        } else {
            return true;
        }
    }
}
