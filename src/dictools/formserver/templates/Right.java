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
import dictools.utils.DicOpts;
import java.io.IOException;

/**
 *
 * @author jimregan
 *
 */
public class Right extends DixElement {

    public ArrayList<Template> templates = new ArrayList<Template>();
    public String id;
    /**
     *
     * @param id
     */
    public Right(String id) {
        super("right");
        this.id = id;
        setValue(id);
    }

    public void printXML(Appendable dos) throws IOException {
        dos.append("<right id=\"" + this.id + "\">\n");
        if (templates != null) {
            for (Template t : templates) {
                t.printXML(dos);
            }
        }
        dos.append("</right>\n");
    }

}
