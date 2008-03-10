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
import java.util.HashMap;

import dics.elements.dtd.ContentElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.Element;
import dics.elements.dtd.LElement;
import dics.elements.dtd.PElement;
import dics.elements.dtd.RElement;
import dics.elements.dtd.SElement;
import dics.elements.dtd.TElement;
import dics.elements.dtd.TextElement;
import dics.elements.dtd.VElement;
import dics.elements.utils.Msg;
import dictools.cmproc.Variables;
import java.io.OutputStreamWriter;

import java.util.regex.*;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class CrossAction implements Comparable<CrossAction> {

   /**
    * 
    */
   private String id;
   /**
    * 
    */
   private Pattern pattern;
   /**
    * 
    */
   private Variables vars;
   /**
    * 
    */
   private ActionSet actionSet;
   /**
    * 
    */
   private int occurrences;
   /**
    * 
    */
   private int x = 0;
   /**
    * 
    */
   private int s = 0;

   /**
    * 
    * 
    */
   public CrossAction() {
      pattern = new Pattern();
      actionSet = new ActionSet();
   }

   /**
    * 
    * @param p
    */
   public void setPattern(final Pattern p) {
      pattern = p;
   }

   /**
    * 
    * @return Undefined     
    */
   public final Pattern getPattern() {
      return pattern;
   }

   /**
    * 
    * @return Undefined     
    */
   public final String getId() {
      return id;
   }

   /**
    * 
    * @param id
    */
   public final void setId(final String id) {
      this.id = id;
   }

   /**
    * 
    * 
    */
   public final void print(Msg msg) {
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
   public final void printXML(OutputStreamWriter dos, int id) throws IOException {
      dos.write("<cross-action id=\"ND-" + id + "\">\n");
      getPattern().printXML(dos);
      if (actionSet != null) {
         getActionSet().printXML(dos);
      }
      dos.write("</cross-action>\n");
      dos.write("<!-- " + getOccurrences() + " entries like this -->\n\n");
   }

   /**
    * 
    * @return Undefined     */
   public final ActionSet getActionSet() {
      return actionSet;
   }

   /**
    * 
    * @param actionSet
    */
   public final void setActionSet(ActionSet actionSet) {
      this.actionSet = actionSet;
      actionSet.setName(getId());
   }

   /**
    * @return the occurrences
    */
   public final int getOccurrences() {
      return occurrences;
   }

   /**
    * @param occurrences
    *                the occurrences to set
    */
   public final void setOccurrences(int occurrences) {
      this.occurrences = occurrences;
   }

   /**
    * 
    * 
    */
   public final void incrementOccurrences() {
      occurrences++;
   }

   /**
    * 
    */
   public int compareTo(final CrossAction anotherEElement)
           throws ClassCastException {
      if (anotherEElement == null) {
         return -1;
      }
      if (!(anotherEElement instanceof CrossAction)) {
         throw new ClassCastException("A CrossAction object expected.");
      }
      final int occ1 = getOccurrences();
      final int occ2 = (anotherEElement).getOccurrences();
      if (occ1 == occ2) {
         return 0;
      }
      if (occ1 > occ2) {
         return -1;
      }
      return 1;
   }

   /**
    * 
    */
   public final CrossAction rename() {
      // Renamed objects
      CrossAction rCrossAction = new CrossAction();
      rCrossAction.setId(this.getId());
      Pattern rPattern = new Pattern();
      ActionSet rActionSet = new ActionSet();
      rCrossAction.setPattern(rPattern);
      rCrossAction.setActionSet(rActionSet);

      HashMap<String, String> valueMap = new HashMap<String, String>();
      ContentElement leftAB = this.getPattern().getAB().getLeft();
      ContentElement rightAB = this.getPattern().getAB().getRight();
      ContentElement leftBC = this.getPattern().getBC().getLeft();
      ContentElement rightBC = this.getPattern().getBC().getRight();

      // Rename patterns
      ContentElement rLeftAB = this.renameContentElement(leftAB, valueMap);
      ContentElement rRightAB = this.renameContentElement(rightAB, valueMap);
      EElement rAB = new EElement();
      rAB.setRestriction(this.getPattern().getAB().getRestriction());

      rAB.addChild(new PElement(new LElement(rLeftAB), new RElement(rRightAB)));

      ContentElement rLeftBC = this.renameContentElement(leftBC, valueMap);
      ContentElement rRightBC = this.renameContentElement(rightBC, valueMap);
      EElement rBC = new EElement();
      rBC.setRestriction(this.getPattern().getBC().getRestriction());

      rBC.addChild(new PElement(new LElement(rLeftBC), new RElement(rRightBC)));

      rPattern.setAB(rAB);
      rPattern.setBC(rBC);

      // Rename actions
      for (Action a : this.getActionSet()) {
         ContentElement leftA = a.getE().getLeft();
         ContentElement rightA = a.getE().getRight();
         ContentElement rLeftA = this.renameContentElement(leftA, valueMap);
         ContentElement rRightA = this.renameContentElement(rightA, valueMap);
         EElement rA = new EElement();
         if (a.getE().isRestrictionAuto()) {
            rA.setRestriction("auto");
         } else {
            if (a.getE().hasRestriction()) {
               rA.setRestriction(a.getE().getRestriction());
            }
         }
         rA.addChild(new PElement(new LElement(rLeftA), new RElement(rRightA)));
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
   private final ContentElement renameContentElement(final ContentElement source, HashMap<String, String> valueMap) {
      ContentElement rContentElement = new ContentElement();
      for (Element e : source.getChildren()) {

         // text element
         if (e instanceof TextElement) {
            String v = ((TextElement) e).getValue();
            TextElement tE = new TextElement("");
            if (v.startsWith("$")) {
               if (valueMap.containsKey(v)) {
                  tE.setValue(valueMap.get(v));
               } else {
                  String nV = "X" + x;
                  x++;
                  valueMap.put(v, nV);
                  tE = new TextElement(nV);
               }
            } else {
               tE = new TextElement(v);
            }
            rContentElement.addChild(tE);
         }

         // 'v' element
         if (e instanceof VElement) {
            SElement rSE = new SElement();
            String v = ((VElement) e).getValue();
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
            rContentElement.addChild(rSE);
         }

         // 't' element
         if (e instanceof TElement) {
            SElement rSE = new SElement();
            String v = ((TElement) e).getValue();
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
            rContentElement.addChild(rSE);
         }

         // 's' element
         if (e instanceof SElement) {
            SElement rSE = new SElement();
            String v = ((SElement) e).getValue();
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
            rContentElement.addChild(rSE);
         }
      }
      return rContentElement;
   }

   /**
    * 
    * @param value
    * @return
    */
   private final int getTypeOfVariable(final String value) {
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
   private final boolean stringMatchesPattern(final String value, final String patternString) {
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
    * @return true if the cross action is valid
    */
   public final boolean isValid() {
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
