/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Author: Enrique Benimeli Bofarull
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
package dictools.formserver.templates;
import dics.elements.dtd.DixElement;
import java.util.ArrayList;
import java.io.IOException;

import dictools.utils.DicOpts;

/**
 *
 * @author jimregan
 *
 */
public class Left extends DixElement {

    public ArrayList<Right> rlist = new ArrayList<Right>();
    public String id;
    /**
     *
     * @param id
     */
    public Left(String id) {
        super("left");
        this.id = id;
        setValue(id);
    }


    public void printXML(Appendable dos) throws IOException {
        dos.append("<left id=\"" + this.id + "\">\n");
        if (rlist != null) {
            for (Right r : rlist) {
                r.printXML(dos);
            }
        }
        dos.append("</left>\n");
    }

}
