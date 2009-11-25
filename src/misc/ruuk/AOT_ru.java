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
    private Hashtable<String, String> tagmap = new Hashtable<String, String> ();

    private void myput (String key, String val) {
        tagmap.put (new String(key), new String (val));
    }

    private void fill_tags () {
        myput("аа", "n.m.sg.nom");
        myput("аб", "n.m.sg.gen");
        myput("Эф", "n.m.sg.gen2");
        myput("ав", "n.m.sg.dat");
        myput("аг", "n.m.sg.acc");
        myput("ад", "n.m.sg.ins");
        myput("ае", "n.m.sg.prp");
        myput("Эх", "n.m.sg.loc");
        myput("ас", "n.m.sg.voc");
        myput("аж", "n.m.pl.nom");
        myput("аз", "n.m.pl.gen");
        myput("аи", "n.m.pl.dat");
        myput("ай", "n.m.pl.acc");
        myput("ак", "n.m.pl.ins");
        myput("ал", "n.m.pl.prp");
        myput("ам", "m");
        myput("ан", "m.sg");
        myput("Юо", "n.m.sg.nom.coll");
        myput("Юп", "n.m.sg.gen.coll");
        myput("Юр", "n.m.sg.dat.coll");
        myput("Юс", "n.m.sg.acc.coll");
        myput("Ют", "n.m.sg.ins.coll");
        myput("Юф", "n.m.sg.prp.coll");
        myput("Юх", "n.m.sg.voc.coll");
        myput("Яб", "n.m.pl.nom.coll");
        myput("Яа", "n.m.pl.gen.coll");
        myput("Яв", "n.m.pl.dat.coll");
        myput("Яг", "n.m.pl.acc.coll");
        myput("Яд", "n.m.pl.ins.coll");
        myput("Яж", "n.m.pl.prp.coll");
        myput("го", "n.m.sg.nom.arch");
        myput("гп", "n.m.sg.gen.arch");
        myput("гр", "n.m.sg.dat.arch");
        myput("гс", "n.m.sg.acc.arch");
        myput("гт", "n.m.sg.ins.arch");
        myput("гу", "n.m.sg.prp.arch");
        myput("гф", "n.m.pl.nom.arch");
        myput("гх", "n.m.pl.gen.arch");
        myput("гц", "n.m.pl.dat.arch");
        myput("гч", "n.m.pl.acc.arch");
        myput("гш", "n.m.pl.ins.arch");
        myput("гщ", "n.m.pl.prp.arch");
        myput("ва", "n.mf.sg.nom");
        myput("вб", "n.mf.sg.gen");
        myput("вв", "n.mf.sg.dat");
        myput("вг", "n.mf.sg.acc");
        myput("вд", "n.mf.sg.ins");
        myput("ве", "n.mf.sg.prp");
        myput("вж", "n.mf.pl.nom");
        myput("вз", "n.mf.pl.gen");
        myput("ви", "n.mf.pl.dat");
        myput("вй", "n.mf.pl.acc");
        myput("вк", "n.mf.pl.ins");
        myput("вл", "n.mf.pl.prp");
        myput("вм", "m");
        myput("вн", "mf.sg");
        myput("во", "n.mf.sg.nom.arch");
        myput("вп", "n.mf.sg.gen.arch");
        myput("вр", "n.mf.sg.dat.arch");
        myput("вс", "n.mf.sg.acc.arch");
        myput("вт", "n.mf.sg.ins.arch");
        myput("ву", "n.mf.sg.prp.arch");
        myput("вф", "n.mf.pl.nom.arch");
        myput("вх", "n.mf.pl.gen.arch");
        myput("вц", "n.mf.pl.dat.arch");
        myput("вч", "n.mf.pl.acc.arch");
        myput("вш", "n.mf.pl.ins.arch");
        myput("вщ", "n.mf.pl.prp.arch");

    }
    
    public String getTags (String in) {
        Object o = tagmap.get(in);
        if (o instanceof String) {
            return (String) o;
        }
        return null;
    }

    public void AOT_ru () {
        this.fill_tags();
    }

    public static void main (String[] args) {
        AOT a = new AOT(args[0]);
        System.err.println("Filename: " + args[0]);
        a.read_aot("Cp1251");
    }
}
