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

package misc.ruuk;

import java.util.Hashtable;

/**
 *
 * @author jimregan
 */
public class AOT_ru {
    private Hashtable tagmap = new Hashtable ();

    private void fill_tags () {
        tagmap.put("аа", "n.m.sg.nom");
        tagmap.put("аб", "n.m.sg.gen");
        tagmap.put("Эф", "n.m.sg.gen2");
        tagmap.put("ав", "n.m.sg.dat");
        tagmap.put("аг", "n.m.sg.acc");
        tagmap.put("ад", "n.m.sg.ins");
        tagmap.put("ае", "n.m.sg.prp");
        tagmap.put("Эх", "n.m.sg.loc");
        tagmap.put("ас", "n.m.sg.voc");
        tagmap.put("аж", "n.m.pl.nom");
        tagmap.put("аз", "n.m.pl.gen");
        tagmap.put("аи", "n.m.pl.dat");
        tagmap.put("ай", "n.m.pl.acc");
        tagmap.put("ак", "n.m.pl.ins");
        tagmap.put("ал", "n.m.pl.prp");
        tagmap.put("ам", "m");
        tagmap.put("ан", "m.sg");
        tagmap.put("Юо", "n.m.sg.nom.coll");
        tagmap.put("Юп", "n.m.sg.gen.coll");
        tagmap.put("Юр", "n.m.sg.dat.coll");
        tagmap.put("Юс", "n.m.sg.acc.coll");
        tagmap.put("Ют", "n.m.sg.ins.coll");
        tagmap.put("Юф", "n.m.sg.prp.coll");
        tagmap.put("Юх", "n.m.sg.voc.coll");
        tagmap.put("Яб", "n.m.pl.nom.coll");
        tagmap.put("Яа", "n.m.pl.gen.coll");
        tagmap.put("Яв", "n.m.pl.dat.coll");
        tagmap.put("Яг", "n.m.pl.acc.coll");
        tagmap.put("Яд", "n.m.pl.ins.coll");
        tagmap.put("Яж", "n.m.pl.prp.coll");
        tagmap.put("го", "n.m.sg.nom.arch");
        tagmap.put("гп", "n.m.sg.gen.arch");
        tagmap.put("гр", "n.m.sg.dat.arch");
        tagmap.put("гс", "n.m.sg.acc.arch");
        tagmap.put("гт", "n.m.sg.ins.arch");
        tagmap.put("гу", "n.m.sg.prp.arch");
        tagmap.put("гф", "n.m.pl.nom.arch");
        tagmap.put("гх", "n.m.pl.gen.arch");
        tagmap.put("гц", "n.m.pl.dat.arch");
        tagmap.put("гч", "n.m.pl.acc.arch");
        tagmap.put("гш", "n.m.pl.ins.arch");
        tagmap.put("гщ", "n.m.pl.prp.arch");
        tagmap.put("ва", "n.mf.sg.nom");
        tagmap.put("вб", "n.mf.sg.gen");
        tagmap.put("вв", "n.mf.sg.dat");
        tagmap.put("вг", "n.mf.sg.acc");
        tagmap.put("вд", "n.mf.sg.ins");
        tagmap.put("ве", "n.mf.sg.prp");
        tagmap.put("вж", "n.mf.pl.nom");
        tagmap.put("вз", "n.mf.pl.gen");
        tagmap.put("ви", "n.mf.pl.dat");
        tagmap.put("вй", "n.mf.pl.acc");
        tagmap.put("вк", "n.mf.pl.ins");
        tagmap.put("вл", "n.mf.pl.prp");
        tagmap.put("вм", "m");
        tagmap.put("вн", "mf.sg");

    }
    
    private String getTags (String in) {
        Object o = tagmap.get(in);
        if (o instanceof String) {
            return (String) o;
        }
        return null;
    }

    public static void main (String[] args) {
        AOT a = new AOT(args[0]);
        System.err.println("Filename: " + args[0]);
        a.read_aot("Cp1251");
    }
}
