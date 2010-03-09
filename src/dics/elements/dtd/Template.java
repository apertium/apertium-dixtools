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
package dics.elements.dtd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dictools.utils.DicOpts;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class Template extends DixElement {
    
    public ArrayList<E> elements = new ArrayList<E>();
    
    public Template()
    {
        super("template");
    }


    /**
     * Equals method. Check equality of pardef elements, regardless of order.
     * @param pardef2
     * @return Undefined
     */
    public boolean contentEquals(Template tpl2) {
        ArrayList<E> eList1 = elements;
        ArrayList<E> eList2 = tpl2.elements;

        if (eList1.size() != eList2.size()) {
            return false;
        }

        HashMap<String, E> elementsTpl1 = new HashMap<String, E>();

        for (E element1 : eList1) {
            elementsTpl1.put(element1.toString(), element1);
        }

        for (E element2 : eList2) {
            if (!elementsTpl1.containsKey(element2.toString())) {
                return false;
            }
        }
        return true;
    }



    /**
     * 
     * @param def
     * @return true if the paradigm contains certain definition ('adj', 'm', etc.)
     */
    public boolean containsSymbol(String def) {
        for (E e : elements) {
            if (e.containsSymbol(def)) {
                return true;
            }
        }
        return false;
    }
}
