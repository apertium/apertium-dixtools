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
package dics.elements.utils;

import dics.elements.dtd.Element;
import dics.elements.dtd.SElement;

import java.util.ArrayList;

/**
 *
 * @author Enrique Benimeli Bofarull
 *
 */
public class ElementList extends ArrayList<Element> implements Cloneable {

    /**
     *    
     */
    static final long serialVersionUID = 0;

    /**
     *    
     *    
     */
    public ElementList() {
        super();
    }

    /**
     *    
     */
    @Override
    public Object clone() {
        try {
            ElementList cloned = (ElementList) super.clone();

            for (int i = 0; i < size(); i++) {
                Element eCloned = (Element) cloned.get(i).clone();

                cloned.set(i, eCloned);
            }

            return cloned;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     *    
     */
    @Override
    public String toString() {
        String str = "";

        if (size() > 0) {
            for (Element e : this) {
                String tmp = "";

                if (e instanceof SElement) {
                    tmp = ((SElement) e).getTemp();

                    if (tmp == null) {
                        tmp = "";
                    }
                }

                str += "<" + e.getValue() + "/" + tmp + ">";
            }
        }

        return str;
    }

    /**
     * 
     * @param eList
     * @return list1 + list2
     */
    public ElementList concat(ElementList eList) {
        ElementList cList = new ElementList();
        for (Element e1 : this) {
            cList.add(e1);
        }
        for (Element e2 : eList) {
            cList.add(e2);
        }
        return cList;
    }

    /**
     * 
     */
    public void printSequence(Msg msg) {
        msg.log("[]");
        for (Element e : this) {
            msg.log("<" + e.getValue() + "> ");
        }
        msg.log("]");

    }
}


