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

package dictools.rebase;
import java.util.ArrayList;
import dics.elements.dtd.E;
import dics.elements.dtd.I;
import dics.elements.dtd.L;
import dics.elements.dtd.P;
import dics.elements.dtd.Par;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.R;
import dics.elements.dtd.S;
import dics.elements.dtd.TextElement;

/**
 *
 * @author jimregan
 */
public class Rebase {

    /**
     * Get the stem from an E
     * @param in The E element to check
     * @return The common stem
     */
    String getEStem(E in) {
        String out = "";
        String l = in.getValueNoTags("L");
        String r = in.getValueNoTags("R");
        out = dictools.speling.SpelingParadigm.getStem(l, r);
        return out;
    }

    /**
     * FIXME
     * @param base
     * @param check
     * @return
     */
    boolean pardefsRoughlyEquivalent(Pardef base, Pardef check) {
        boolean ret = false;
        if (check.elements.size() < base.elements.size())
            return false;
        
        return ret;
    }

    ArrayList<EntryPair> alignEntries (ArrayList<E> base, ArrayList<E> check) {
        ArrayList<EntryPair> out = new ArrayList<EntryPair>();

        for (E e : base) {
            for (E ee : check) {
                if (entriesRoughlyEquivalent(e, ee)) {
                    out.add(new EntryPair(e, ee));
                }
            }
        }
        return out;
    }

    /**
     * "roughly equivalent" means that the L, R, and S elements of check
     * end with those of base. The entries are also required to have the
     * same restriction.
     * 
     * @param base the E element of the base paradigm
     * @param check the E element to be checked
     * @return true if roughly equivalent
     */
    static boolean entriesRoughlyEquivalent(E base, E check) {
        boolean ret = true;
        String baseL = base.getValueNoTags("L");
        String baseR = base.getValueNoTags("R");
        String checkL = check.getValueNoTags("L");
        String checkR = check.getValueNoTags("R");

        if (!"".equals(baseL) && !checkL.endsWith(baseL))
            return false;
        if (!"".equals(baseR) && !checkR.endsWith(baseR))
            return false;
        if (!sdefsRoughlyEquivalent(base.getSymbols("R"), check.getSymbols("R"))) {
            return false;
        }
        if (!base.restriction.equals(check.restriction)) {
            return false;
        }

        return ret;
    }

    static boolean entriesRoughlyEquivalent(EntryPair ep) {
        return entriesRoughlyEquivalent(ep.getFirst(), ep.getSecond());
    }

    /**
     * "roughly equivalent" means that check ends with the same S elements
     * as base: e.g., if base is <tt>&lt;n&gt;&lt;f&gt;&lt;sg&gt;&lt;nom&gt;</tt>
     * and check is <tt>&lt;sg&gt;&lt;nom&gt;</tt>,
     * they are roughly equivalent
     * @param base the S elements from the base paradigm
     * @param check the S elements from the paradigm to be checked
     * @return true if roughly equivalent
     */
    static boolean sdefsRoughlyEquivalent(ArrayList<S> base, ArrayList<S> check) {
        boolean ret = true;
        if (base.size() > check.size())
            return false;
        for (int i = check.size(), j = base.size(); j > 0; i--, j--) {
            if (!check.get(i-1).name.equals(base.get(j-1).name)) {
                return false;
            }
        }

        return ret;
    }

}
