/*
 * Copyright 2010 Jimmy O'Regan
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

package misc.termcat;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author jimregan
 */
public class Fitxa {
    ArrayList<Denominacio> denom;

    Fitxa () {
        denom = new ArrayList<Denominacio>();
    }

    /**
     * Convert an ArrayList of Denominacio into a hash, where the language
     * is the key
     * @param d 
     * @return
     */
    Map<String, ArrayList<Denominacio>> getHash (ArrayList<Denominacio> d) {
        Map<String, ArrayList<Denominacio>> hash = new HashMap<String, ArrayList<Denominacio>>();
        ArrayList<Denominacio> cur = new ArrayList<Denominacio>();
        for (Denominacio den : d) {
            if (!hash.containsKey(den.lang)) {
                cur.clear();
                cur.add(den);
                hash.put(den.lang, cur);
            } else {
                cur = hash.get(den.lang);
                cur.add(den);
                hash.put(den.lang, cur);
            }
        }
        return hash;
    }
}
