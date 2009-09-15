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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import dics.elements.dtd.S;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Variables extends HashMap<String, Object> {

    /**
     * 
     */
    public Variables() {
        super();
    }

    /**
     * 
     * @param copy
     */
    public Variables(Variables copy) {
        super();
        Iterator it = copy.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            Object obj = copy.get(key);
            if (obj instanceof String) {
                put(new String(key), new String((String) obj));
            }
            if (obj instanceof ArrayList) {  // <S>
                ArrayList<S> sEList = (ArrayList<S>) obj;
                put(new String(key), new ArrayList<S>(sEList));
            }
        }
    }

    /**
     * 
     */
    public void print() {
        Iterator it = this.keySet().iterator();
        System.out.print("{ ");
        while (it.hasNext()) {
            String key = (String) it.next();
            Object obj = this.get(key);
            if (obj instanceof String) {
                System.out.println(key + ": " + ((String) obj));
            }
            if (obj instanceof ArrayList) { // <S>
                System.out.print(key + ": ");
                for (S e : (ArrayList<S>) obj) {
                    System.out.print(e.getValue() + ", ");
                }
                System.out.println("");
            }
        }
        System.out.println(" }");
    }
}
