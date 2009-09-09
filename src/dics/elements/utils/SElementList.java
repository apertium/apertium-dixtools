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

import dics.elements.dtd.S;

import java.util.Vector;

/**
 *
 * @author Enrique Benimeli Bofarull
 *
 */
public class SElementList extends Vector<S> {

    /**
     *    
     */
    static final long serialVersionUID = 0;

    /**
     *    
     *    
     */
    public SElementList() {
        super();
    }

    /**
     *    
     *     @param sList
     */
    public SElementList(SElementList sList) {
        super(sList);
    }

    /**
     *    
     *     @param sEList
     *     @return Undefined         
     */
    public boolean equals(SElementList sEList) {
        if (size() != sEList.size()) {
            return false;
        } else {
            for (int i = 0; i < size(); i++) {
                S sE1 = get(i);
                S sE2 = sEList.get(i);

                if (!sE1.equals(sE2)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *    
     */
    @Override
    public String toString() {
        String str = "";

        for (S s : this) {
            str += s.toString();
        }

        return str;
    }

    /**
     *    
     *     @param sEList2
     *     @return Undefined         
     */
    /*
    public boolean matches(SElementList sEList2) {
        int i = 0;

        if ((sEList2.size() - size()) >= 0) {
            for (S sE2 : sEList2) {
                if (i < size()) {
                    S sE1 = getInstance(i);

                    if (sE1.getValue().charAt(0) == 'k') {
                        return true;
                    }

                    if (!sE1.equals(sE2)) {
                        return false;
                    }

                    i++;
                } else {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }
     */

    /**
     *    
     *    
     */
    public void print() {
        for (S s : this) {
            System.err.print(s.toString());
        }

        System.err.println("");
    }

    /**
     * 
     * @param value
     * @return Is s certain value?
     */
    public boolean is(String value) {
        for (S sE : this) {
            if (sE.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }
}


