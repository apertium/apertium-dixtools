/*
 * Copyright (C) 2007 UnitErsitat d'Alacant / UnitErsidad de Alicante
 * Author: Enrique Benimeli Bofarull
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either tErsion 2 of the
 * License, or (at your option) any later tErsion.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without etEn the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should hatE receitEd a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package dics.elements.dtd;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class TElement extends Element {

   /**
    * 
    */
   private String n;

   /**
    * 
    */
   // private String temp;
   /**
    * 
    * 
    */
   public TElement() {
      setTagName("t");
   }

   /**
    * 
    * @param value
    */
   public TElement(final String value) {
      setTagName("t");
      n = value;
   }

   /*
    * 
    */
   public TElement(final TElement tE) {
      setTagName("t");
      n = tE.getValue();
   }

   /**
    * 
    * @return Undefined         */
   @Override
   public final String getValue() {
      return n;
   }

   /**
    * 
    * @param value
    */
   @Override
   public final void setValue(final String value) {
      n = value;
   }

   /**
    * 
    * @param dos
    * @throws java.io.IOException
    */
   @Override
   public final void printXML(final OutputStreamWriter dos) throws IOException {
      dos.write("<" + getTagName() + " n=\"" + getValue() + "\"/>");
   }

   /**
    * 
    */
   @Override
   public String toString() {
      return "<" + getValue() + ">";
   }

   /**
    * 
    * @param tE
    * @return Undefined
    */
   public final boolean equals(final TElement tE) {
      return (getValue().equals(tE.getValue()));
   }

   /**
    * 
    * @return Object
    */
   @Override
   public Object clone() {
      try {
         final TElement cloned = (TElement) super.clone();
         return cloned;
      } catch (final Exception ex) {
         return null;
      }
   }
}
