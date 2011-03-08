/*
 * Author: Jimmy O'Regan
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

package dictools.columnar;

import java.util.ArrayList;
import dics.elements.dtd.E;

/**
 *
 * @author jimregan
 */
public class ParadigmPair {
    String left;
    String right;

    ArrayList<E> entries;

    ParadigmPair(String l, String r, ArrayList<E> e) {
        left = l;
        right = r;
        entries = e;
    }

    String getIndex() {
        return left + "+" + right;
    }

    /**
     * Get the list of entries
     * @return All entries
     */
    ArrayList<E> getEntries() {
        return entries;
    }

    /**
     * Filter the list of current entries based on
     * the restriction.
     * @param restrict Restriction to apply
     * @return List of entries for the restriction
     */
    ArrayList<E> getEntries(String restrict) {
        if ("".equals(restrict)) {
            return entries;
        }
        ArrayList<E> tmp = new ArrayList<E>();
        for (E e : entries) {
            if (restrict.equals(e.restriction)) {
                tmp.add(e);
            } else if ("".equals(e.restriction)) {
                e.restriction = restrict;
                tmp.add(e);
            } else {
                continue;
            }
        }
        return tmp;
    }
}
