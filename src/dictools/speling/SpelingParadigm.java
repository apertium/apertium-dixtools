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

package dictools.speling;
import java.util.ArrayList;
import dics.elements.dtd.E;
import dics.elements.dtd.I;
import dics.elements.dtd.L;
import dics.elements.dtd.P;
import dics.elements.dtd.Par;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.R;
import dics.elements.dtd.S;


/**
 *
 * @author jimregan
 */

public class SpelingParadigm {
    String lemma = "";
    String pos = "";
    ArrayList<SpelingEntry> entries;

    private String name = "";
    private String stem = "";
    private String sfx = "";
    private ArrayList<String> suffixes;

    void purge () {
        lemma = "";
        pos = "";
        if (!entries.isEmpty()){
            entries.clear();
        }
    }

    /**
     * Finds the common stem between a lemma and an inflected form
     *
     * @param lemma Base form
     * @param flexion Inflected form
     * @return Common stem of both words
     */
    public String find_stem (String lemma, String flexion) {
        String mystem = "";
        if (lemma.charAt(0) != flexion.charAt(0))
            return "";
        for (int i=0; i<lemma.length();i++) {
            if (lemma.charAt(i) == flexion.charAt(i)) {
                mystem += lemma.charAt(i);
            }
        }
        return mystem;
    }

    /**
     * Get the shortest string in a list
     *
     * @param list The list of strings
     * @return The shortest string
     */
    public String get_shortest (String[] list) {
        String shortest = "";
        for (int i=0;i<list.length;i++) {
            if (list[i].equals("")) {
                return "";
            }

            if (i==0) {
                shortest = list[0];
            } else if (list[i].length() < shortest.length()) {
                shortest = list[i];
            } else {
                continue;
            }
        }
        return shortest;
    }

    public void SpelingParadigm () {
        lemma = "";
        pos = "";
        entries = new ArrayList<SpelingEntry> ();
    }

    private String strip_stem (String stem, String in) {
        if (!in.startsWith(stem)) {
            return in;
        }
        return (in.substring(0, stem.length()-1));
    }

    private String shortest () {
        ArrayList<String> lem = new ArrayList<String>();
        for (SpelingEntry e : entries) {
            lem.add(find_stem(lemma, e.surface));
        }
        String[] list = new String[lem.size()];
        list = lem.toArray(list);
        return get_shortest(list);
    }

    private void set_stem () {
        stem = shortest();
    }

    private void stripstems () {
        set_stem();
    }

    private void setSuffixes () {
        for (SpelingEntry e : entries) {
            suffixes.add(strip_stem (stem, e.surface));
        }
    }

    private String pardef_name () {
        stripstems();
        String tmp="";

        if (suffixes.get(0).equals("")) {
            tmp = suffixes.get(0);
        } else {
            tmp = stem + "/" + suffixes.get(0);
        }
        return tmp + "__" + pos.replaceAll("\\.", "_");
    }

    public Pardef toPardef () {
        Pardef out = new Pardef(pardef_name());
        ArrayList<E> elist = new ArrayList<E>(entries.size());
        for (int i=0;i<=entries.size();i++) {
            E cur = new E();
            P p = new P();
            L l = new L();
            R r = new R();
            if (entries.get(i).isLR) {
                cur.restriction = "LR";
            }
            l.setValue(suffixes.get(i));
            r.setValue(suffixes.get(0));
            for (String str : entries.get(i).pos.split("\\.")) {
                S s = new S();
                s.setValue(str);
                r.children.add(s);
            }
            p.l = l;
            p.r = r;
            cur.children.add(l);
            cur.children.add(r);
            elist.add(cur);
        }
        return out;
    }

    public E toE() {
        E e = new E();
        e.lemma = stem + suffixes.get(0);
        I i = new I();
        i.setValue(stem);
        Par par = new Par();
        par.setValue(pardef_name());
        e.children.add(i);
        e.children.add(par);
        return e;
    }
}
