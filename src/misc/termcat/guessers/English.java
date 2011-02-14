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

package misc.termcat.guessers;
import dics.elements.dtd.E;
import dics.elements.dtd.P;
import dics.elements.dtd.L;
import dics.elements.dtd.R;
import dics.elements.dtd.G;
import dics.elements.dtd.I;
import dics.elements.dtd.Par;
import java.util.ArrayList;

/**
 *
 * @author jimregan
 */
public class English extends Guesser {

    @Override
    public E[] guess(String para, String tags) {
        ArrayList<E> list = new ArrayList<E>();
        String stem = "";

        if ("ij".equals(tags)) {
            list.add(buildSimpleIEntry(para, para, "hello__ij"));
        }
        if ("adv".equals(tags)) {
            list.add(buildSimpleIEntry(para, para, "maybe__adv"));
        }
        if ("adj".equals(tags)) { // no synthetic check - not available in termcat
            list.add(buildSimpleIEntry(para, para, "expensive__adj"));
        }

        if ("n".equals(tags)) {
            if (para.endsWith("[bcdfghjklmnpqrstvwxz]y")) {
                stem = para.substring(0, para.length()-2);
                list.add(buildSimpleIEntry(para, stem, "bab/y__n"));
            } else if (para.endsWith("i[sz]ation")) {
                stem = para.substring(0, para.length()-7);
                list.add(buildSimpleIEntry(para, stem, "globali/sation__n"));
            } else if (para.endsWith("cs")) {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "politics__n"));
            } else if (para.endsWith("([cs]h|[us]s)")) {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "access__n"));
            } else {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "house__n"));
            }
        }

        if ("vblex".equals(tags)) {
            if (para.endsWith("[bcdfghjklmnpqrstvwxz]y")) {
                stem = para.substring(0, para.length()-2);
                list.add(buildSimpleIEntry(para, stem, "appl/y__vblex"));
            } else if (para.endsWith("[aeoiu]y")) {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "enjoy__vblex"));
            } else if (para.endsWith("i[sz]e")) {
                stem = para.substring(0, para.length()-3);
                list.add(buildSimpleIEntry(para, stem, "organi/ze__vblex"));
            } else if (para.endsWith("[bcdfghjklmnpqrstvwxz]e")) {
                stem = para.substring(0, para.length()-2);
                list.add(buildSimpleIEntry(para, stem, "liv/e__vblex"));
            } else if (para.endsWith("ing")) {
                stem = para.substring(0, para.length()-4);
                list.add(buildSimpleIEntry(para, stem, "r/ing__vblex"));
            } else if (para.endsWith("ink")) {
                stem = para.substring(0, para.length()-4);
                list.add(buildSimpleIEntry(para, stem, "st/ink__vblex"));
            } else if (para.endsWith("([cs]h|ss|x|zz)")) {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "approach__vblex"));
            } else if (para.endsWith("([rlw][mn]|[lc]k|ff|[lrswpf]t)")) {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "accept__vblex"));
            } else if (para.endsWith("ee")) {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "agree__vblex"));
            } else if (para.endsWith("[aeiou]b")) {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "dub__vblex"));
            } else if (para.endsWith("[aeiou]d")) {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "bed__vblex"));
            } else if (para.endsWith("[aeiou]g")) {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "plug__vblex"));
            } else if (para.endsWith("[aeiou]l")) {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "control__vblex"));
            } else if (para.endsWith("[aeiou]m")) {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "program__vblex"));
            } else if (para.endsWith("[aeiou]n")) {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "plan__vblex"));
            } else if (para.endsWith("[aeiou]p")) {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "step__vblex"));
            } else if (para.endsWith("[aeiou]r")) {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "occur__vblex"));
            } else if (para.endsWith("[aeiou]t")) {
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "admit__vblex"));
            } else { // I give up!
                stem = para;
                list.add(buildSimpleIEntry(para, stem, "accept__vblex"));
            }
        }
        return list.toArray(new E[list.size()]);
    }

}
