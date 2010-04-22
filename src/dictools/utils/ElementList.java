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
package dictools.utils;

import java.util.ArrayList;

import dics.elements.dtd.DixElement;

/**
 *
 * @author Enrique Benimeli Bofarull
 *
 */
public class ElementList extends ArrayList<DixElement> implements Cloneable {

    
    static final long serialVersionUID = 0;

    
    public ElementList() {
        super(6);
    }

  public ElementList(ElementList children) {
    super(children);
  }

   /*
    @Override
    public Object clone() {
        try {
            ElementList cloned = (ElementList) super.clone();

            for (int i = 0; i < size(); i++) {
                DixElement eCloned = (DixElement) cloned.get(i).clone();

                cloned.set(i, eCloned);
            }

            return cloned;
        } catch (Exception ex) {
            return null;
        }
    }
*/
    
    @Override
    public String toString() {
        String str = "";
        if (size() > 0) {
            for (DixElement e : this) {
                String tmp = "";


                str += "<" + e.getValue() + "/" + tmp + ">";
            }
        }

        return str;
    }

}


