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

/**
 *
 * @author jimregan
 */
public class ParaConfig {

    private HashMap<String, ParadigmPair> pairs;

    ParaConfig() {
        pairs = new HashMap<String, ParadigmPair>();
    }

    ParadigmPair get(String name) {
        return pairs.get(name);
    }

    ParadigmPair get(String left, String right) {
        String name = left + "+" + right;
        return pairs.get(name);
    }

    void add (ParadigmPair pair) {
        pairs.put(pair.getIndex(), pair);
    }
}
