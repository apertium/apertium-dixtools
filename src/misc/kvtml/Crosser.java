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

package misc.kvtml;
import java.util.List;
import java.util.ArrayList;
import dictools.AbstractDictTool;
import dics.elements.dtd.E;
import dics.elements.dtd.P;
import dics.elements.dtd.L;
import dics.elements.dtd.R;
import dics.elements.dtd.Section;
import dics.elements.dtd.Dictionary;

/**
 *
 * @author jimregan
 */
public class Crosser extends AbstractDictTool {
    ArrayList<E> elist;
    String fileA = "";
    String fileB = "";
    String output = "";

    Dictionary dic;
    Section main;

    Crosser () {
        elist = new ArrayList<E>();
        dic = new Dictionary();
        main = new Section("main", "standard");
    }

    E genEntry (String common, String left, String right) {
        E e = new E();
        P p = new P();
        L l = new L();
        R r = new R();

        e.comment = common;
        l.setValue(left);
        r.setValue(right);
        p.l = l;
        p.r = r;
        e.children.add(p);
        return e;
    }

    List<E> crossKvtml () throws Exception {
        List<E> list = new ArrayList<E>();
        List<Translation> llist = DataSet.getTranslatedWords(fileA);
        List<Translation> rlist = DataSet.getTranslatedWords(fileB);

        for (Translation t : llist) {
            if (rlist.contains(t.original)) {
                
            }
        }
        return list;
    }
}
