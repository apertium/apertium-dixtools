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
import dics.elements.dtd.*;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 *
 * @author jimregan
 */
public class Config {
    public HeaderElement header;
    public String logfile;
    public String fileName;
    public String outFileName;
    public String xmlEncoding = "UTF-8";

    public String getOutFileName() {
        return this.outFileName;
    }

    public void printXML() throws IOException {
        BufferedOutputStream bos;
        FileOutputStream fos;
        OutputStreamWriter osw;

        try {
            if (this.getOutFileName() != null) {
                fos = new FileOutputStream(this.getOutFileName());
                bos = new BufferedOutputStream(fos);
            } else {
                bos = new BufferedOutputStream(System.out);
            }
            osw = new OutputStreamWriter(bos, this.xmlEncoding);

            osw.append("<?xml version=\"1.0\" encoding=\"" + xmlEncoding + "\"?>\n");
            osw.append("<webforms>\n");
            osw.append("  <log-file>" + logfile + "</logfile>\n");
            osw.append("</webforms>\n");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
