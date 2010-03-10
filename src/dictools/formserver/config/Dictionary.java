/*
 * Copyright (C) 2010 Jimmy O'Regan
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
package dictools.formserver.config;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * @author jimregan
 */
public class Dictionary {
    public String name;
    public String side;
    public String format;
    public String file;


    public void Dictionary (String name) {
        this.name = name;
        this.format = "dix";
    }

    public void Dictionary (String name, String side, String format, String file) {
        this.name = name;
        this.side = side;
        this.format = format;
        this.file = file;
    }

    public void Dictionary (String side, String format, String file) {
        this.name = "";
        this.side = side;
        this.format = format;
        this.file = file;
    }

    public void Dictionary (String side, String file) {
        this.name = "";
        this.side = side;
        this.format = "dix";
        this.file = file;
    }

    public void print(OutputStreamWriter osw) {
        try {
            osw.write("        <dictionary");
            if (!name.equals("")) {
                osw.write(" n=\"" + name + "\"");
            }
            osw.write(" side=\"" + side + "\"");
            osw.write(" format=\"" + format + "\"");
            osw.write(" file=\"" + file + "\"");
            osw.write(">\n");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
