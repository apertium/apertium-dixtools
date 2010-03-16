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
import dics.elements.dtd.Sdef;

/**
 *
 * @author jimregan
 */
public class Denominacio {
    String lang = "";
    String tipus = "";
    String tags = "";
    String cat = "";
    String jerarquia = "";
    String paraula = "";
    String gd_f = "";
    boolean acronym = false;

    private boolean debug = false;

    Denominacio (String lang, String tipus, String cat, String jerarq, String paraula) {
        this.lang=lang;
        this.paraula = paraula; // May be altered later
        this.acronym = this.isAcronym(paraula);

        if ("ca".equals(lang) || !"equivalent".equals(tipus)) {
            this.tipus = tipus;
        }
        if ("ca".equals(lang) || !"terme pral.".equals(jerarq)) {
            this.jerarquia = jerarq;
        }
        this.cat = cat; // Keep a copy for later, just in case
        if (!"".equals(cat))
            gen_tags(cat, paraula);
    }

    /**
     * Convert termcat pos to Apertium tags
     * @param cat
     * @param paraula
     */
    private void gen_tags(String cat, String paraula) {
        if ("m i f".equals(cat)) {
            String[] s = paraula.split(" ");
            if (!s[1].startsWith("-")) {
                this.tags = "n.mf";
            } else {
                this.tags = "n.GD";
                this.gd_f = s[1].substring(1, s[1].length() - 1);
                // could do some queue handling here; didn't occur to me
                if (s.length > 2) {
                    String tmp = s[0];
                    for (int i = 2; i != s.length; i++) {
                        tmp += " ";
                        tmp += s[i];
                    }
                    this.paraula = tmp;

                    if (debug) {
                        System.err.println("Word was: " + paraula);
                        System.err.println("Word now: " + tmp);
                    }
                } else {
                    this.paraula = s[0];
                }
                
            }
        } else if ("m o f".equals(cat)) {
            this.tags = "n.mf"; //
        } else if ("m".equals(cat)) {
            this.tags = "n.m";
        } else if ("f".equals(cat)) {
            this.tags = "n.f";
        } else if ("m pl".equals(cat)) {
            this.tags = "n.m.pl";
        } else if ("f pl".equals(cat)) {
            this.tags = "n.f.pl";
        } else if ("interj".equals(cat)) {
            this.tags = "ij";
        } else if ("loc adv".equals(cat)) {
            this.tags = "adv";
        } else if ("loc".equals(cat)) {
            this.tags = "adv"; // Maybe
        } else if ("loc adj".equals(cat)) {
            this.tags = "adj.mf.sp";
        } else if ("v intr".equals(cat)) {
            this.tags = "vblex";
        } else if ("v tr".equals(cat)) {
            this.tags = "vblex";
        } else if ("v tr i intr".equals(cat)) {
            this.tags = "vblex";
        } else if ("v pron".equals(cat)) {
            this.tags = "vblex.pron"; // only relevant for en
        } else if ("loc v".equals(cat)) { // has word queue
            this.tags = "vblex";
        }
    }

    /**
     * Checks if the word is a probably acronym
     * @param s the string to check
     * @return true, if all uppercase
     */
    private boolean isAcronym(String s) {
        for (int i=0; i!=s.length();i++) {
            if (!Character.isUpperCase(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public Sdef[] getSdefs () {
        ArrayList<Sdef> sdefs = new ArrayList<Sdef>();
        if ("".equals(this.tags)) {
            return null;
        } else {
            String[] tagset = this.tags.split("\\.");
            for (String s : tagset) {
                sdefs.add(new Sdef(s));
            }
            return sdefs.toArray(new Sdef[sdefs.size()]);
        }
    }
}
