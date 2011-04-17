/*
 * Copyright 2011 European Commission
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
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
