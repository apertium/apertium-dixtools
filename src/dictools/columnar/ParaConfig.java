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

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import dics.elements.dtd.E;

/**
 *
 * @author jimregan
 */
public class ParaConfig {

    boolean DEBUGPRINT = true;

    private HashMap<String, ParadigmPair> pairs;

    ParaConfig() {
        pairs = new HashMap<String, ParadigmPair>();
    }

    ParadigmPair get(String name) {
        return pairs.get(name);
    }

    ParadigmPair get(String left, String right) {
        return pairs.get(left + "+" + right);
    }

    ArrayList<ParadigmPair> getAll () {
        ArrayList<ParadigmPair> out = new ArrayList<ParadigmPair>();
        Collection<ParadigmPair> col = pairs.values();
        for (ParadigmPair pp : col) {
            out.add(pp);
        }
        return out;
    }

    void add (ParadigmPair pair) {
        pairs.put(pair.getIndex(), pair);
    }

    void addAll (ArrayList<ParadigmPair> pairs) {
        for (ParadigmPair pp : pairs) {
            add(pp);
        }
    }

    boolean isSameAs (ParaConfig other) {
        if (other.pairs.size() != pairs.size()) {
            return false;
        }
        if (!other.pairs.keySet().equals(pairs.keySet())) {
            return false;
        }
        for (String s : other.pairs.keySet()) {
            ArrayList<E> otherE = other.get(s).getEntries();
            ArrayList<E> ourE = get(s).getEntries();
            if (otherE.size() != ourE.size()) {
                return false;
            }
            for (int i=0; i < otherE.size(); i++) {
                if (!otherE.get(i).toString().equals(ourE.get(i).toString())) {
                    if (DEBUGPRINT) {
                        System.out.println("Other: " + otherE.get(i).toString());
                        System.out.println("Our: " + ourE.get(i).toString());
                        System.out.println("Not E");
                    }
                    return false;
                }
            }
        }
        return true;
    }
}
