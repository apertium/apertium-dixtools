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
import dics.elements.dtd.TextElement;


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
    private String psfx = "";
    private ArrayList<String> suffixes;

    /**
     * Finds the common stem between a lemma and an inflected form
     *
     * @param lemma Base form
     * @param flexion Inflected form
     * @return Common stem of both words
     */
    public String getStem (String lemma, String flexion) {
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
    public String getShortest (String[] list) {
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

    public SpelingParadigm () {
        lemma = "";
        pos = "";
        entries = new ArrayList<SpelingEntry> ();
        suffixes = new ArrayList<String>();
    }

    /**
     * Remove the stem from a string
     * @param stem The stem
     * @param in The string to remove it from
     * @return The string ith stem removed, or in unchanged
     */
    public String strip_stem (String stem, String in) {
        if (!in.startsWith(stem)) {
            return in;
        }
        return (in.substring(stem.length()));
    }

    private String shortest () {
        ArrayList<String> lem = new ArrayList<String>();
        if (entries != null && !entries.isEmpty()) {
            for (SpelingEntry e : entries) {
                lem.add(getStem(lemma, e.surface));
            }
            String[] list = new String[lem.size()];
            list = lem.toArray(list);
            return getShortest(list);
        }
        return "";
    }

    public void setStem () {
        stem = shortest();
        psfx = strip_stem(stem, lemma);
    }

    public void setSuffixes () {
        if (entries == null || entries.isEmpty()) {
            throw new IndexOutOfBoundsException("Entries array not set: " + entries.size());
        }
        for (SpelingEntry e : entries) {
            //System.err.println("Entry:" + stem +"/"+e.surface);
            suffixes.add(strip_stem (stem, e.surface));
        }
    }

    private String pardef_name () {
        setStem();
        String tmp="";

        if (psfx.equals("")) {
            tmp = lemma;
        } else {
            tmp = stem + "/" + psfx;
        }
        return tmp + "__" + pos.replaceAll("\\.", "_");
    }

    public Pardef toPardef () {
        if (suffixes == null || suffixes.isEmpty()) {
            // Maybe there's a better exception...
            throw new IndexOutOfBoundsException("Suffix array not set: " + suffixes.size());
        }

        Pardef out = new Pardef(pardef_name());
        ArrayList<E> elist = new ArrayList<E>(entries.size());
        for (int i=0;i<entries.size();i++) {
            E cur = new E();
            P p = new P();
            L l = new L();
            R r = new R();
            if (entries.get(i).isLR) {
                cur.restriction = "LR";
            }
            l.children.add (new TextElement (suffixes.get(i)));
            r.children.add (new TextElement (psfx));
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
        out.elements = elist;
        return out;
    }

    public E toE() {
        if (suffixes == null || suffixes.isEmpty()) {
            // Maybe there's a better exception...
            throw new IndexOutOfBoundsException("Suffix array not set: " + suffixes.size());
        }

        E e = new E();
        e.lemma = stem + suffixes.get(0);
        System.err.println(e.lemma);
        I i = new I();
        i.children.add(new TextElement(stem));
        Par par = new Par();
        par.setValue(pardef_name());
        e.children.add(i);
        e.children.add(par);

        return e;
    }
}
