/*
 * MIT License
 *
 * Copyright (c) 2009-2011 Jim O'Regan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
    public static String getStem (String lemma, String flexion) {
        String mystem = "";
        int len;
        if (lemma.charAt(0) != flexion.charAt(0)) {
            return "";
        }
        if (lemma.length() <= flexion.length()) {
            len = lemma.length();
        } else {
            len = flexion.length();
        }
        for (int i=0; i<len;i++) {
            if (lemma.charAt(i) == flexion.charAt(i)) {
                mystem += lemma.charAt(i);
            } else {
                break;
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
     * @return The string with stem removed, or in unchanged
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
            throw new IndexOutOfBoundsException("Suffix array not set");
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
            cur.children.add(p);
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
