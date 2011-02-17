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
import dics.elements.dtd.I;
import dics.elements.dtd.P;
import dics.elements.dtd.L;
import dics.elements.dtd.R;
import dics.elements.dtd.Par;

/**
 *
 * @author jimregan
 */
public class Guesser {

    public Guesser() {
    }

    public E buildSimpleIEntry(String lem, String i, String par) {
        E e = new E();
        Par paradigm = new Par(par);
        I ient = new I();
        e.lemma = lem;
        e.comment = "check";
        if (i.contains(" ")) {
            i = i.replaceAll(" ", "<b/>");
        }
        ient.setValue(i);
        e.children.add(ient);
        e.children.add(paradigm);
        return e;
    }

    public String stemFromPardef(String lem, String par) {
        String stem="";
        String base=par.split("__")[0];
        if(!base.contains("/")) {
            stem=lem;
        } else {
            String sfx=base.split("/")[1];
            if(!lem.endsWith(sfx)) {
                System.err.println("Error: expected suffix '" + sfx + "'");
                stem=lem;
            } else {
                stem=lem.substring(0, lem.lastIndexOf(sfx));
            }
        }
        return stem;
    }

    public E buildSimpleLREntry (String lem, String left, String right, String par) {
        E e = new E();
        Par paradigm = new Par(par);
        P p = new P();
        L l = new L();
        R r = new R();
        e.lemma = lem;
        e.comment = "check";
        e.restriction = "LR";
        if (right.contains(" "))
            right = right.replaceAll(" ", "<b/>");
        if (left.contains(" "))
            left = left.replaceAll(" ", "<b/>");
        l.setValue(left);
        r.setValue(right);
        p.l = l;
        p.r = r;
        e.children.add(p);
        e.children.add(paradigm);

        return e;
    }

    public String guess(String para, String tags) {
        return "";
    }

    public boolean endsWithRegex (String word, String regex, int length) {
        if (word.length() < length)
            return false;
        if (word.substring(word.length()-length).matches(regex))
            return true;
        return false;
    }

}
