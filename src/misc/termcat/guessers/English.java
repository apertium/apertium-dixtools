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

/**
 *
 * @author jimregan
 */
public class English extends Guesser {

    @Override
    public String guess(String para, String tags) {

        if ("ij".equals(tags)) {
            return "hello__ij";
        }
        if ("adv".equals(tags)) {
            return "maybe__adv";
        }
        if ("adj".equals(tags)) { // no synthetic check - not available in termcat
            return "expensive__adj";
        }

        if ("n".equals(tags)) {
            // Ugh. Ought to fix this.
            if (para.substring(para.length()-2).matches("[bcdfghjklmnpqrstvwxz]y")) {
                return "bab/y__n";
            } else if (para.endsWith("ization") || para.endsWith("isation")) {
                return "globali/sation__n";
            } else if (para.endsWith("cs")) {
                return "politics__n";
            } else if (para.substring(para.length()-1).matches("[zsx]")
                    || para.substring(para.length()-2).matches("[cs]h")) {
                return "access__n";
            } else {
                return "house__n";
            }
        }

        if ("vblex".equals(tags)) {
            if (para.endsWith("[bcdfghjklmnpqrstvwxz]y")) {
                return "appl/y__vblex";
            } else if (para.endsWith("[aeoiu]y")) {
                return "enjoy__vblex";
            } else if (para.endsWith("i[sz]e")) {
                return "organi/ze__vblex";
            } else if (para.endsWith("[bcdfghjklmnpqrstvwxz]e")) {
                return "liv/e__vblex";
            } else if (para.endsWith("ing")) {
                return "r/ing__vblex";
            } else if (para.endsWith("ink")) {
                return "st/ink__vblex";
            } else if (para.endsWith("([cs]h|ss|x|zz)")) {
                return "approach__vblex";
            } else if (para.endsWith("([rlw][mn]|[lc]k|ff|[lrswpf]t)")) {
                return "accept__vblex";
            } else if (para.endsWith("ee")) {
                return "agree__vblex";
            } else if (para.endsWith("[aeiou]b")) {
                return "dub__vblex";
            } else if (para.endsWith("[aeiou]d")) {
                return "bed__vblex";
            } else if (para.endsWith("[aeiou]g")) {
                return "plug__vblex";
            } else if (para.endsWith("[aeiou]l")) {
                return "control__vblex";
            } else if (para.endsWith("[aeiou]m")) {
                return "program__vblex";
            } else if (para.endsWith("[aeiou]n")) {
                return "plan__vblex";
            } else if (para.endsWith("[aeiou]p")) {
                return "step__vblex";
            } else if (para.endsWith("[aeiou]r")) {
                return "occur__vblex";
            } else if (para.endsWith("[aeiou]t")) {
                return "admit__vblex";
            } else { // I give up!
                return "accept__vblex";
            }
        }
        return "";
    }

}
