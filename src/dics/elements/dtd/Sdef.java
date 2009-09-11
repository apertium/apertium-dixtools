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
package dics.elements.dtd;

import dics.elements.utils.DicOpts;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class Sdef extends DixElement {

    /**
     * 
     */
    private String n;
    /**
     * 
     */
    private String comment;

    /**
     * 
     * @param value
     */
    public Sdef(String value) {
        setTagName("sdef");
        n = value;
    }

    /**
     * 
     * @return Undefined         */
    @Override
    public String getValue() {
        return n;
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    public void printXML(Appendable dos, DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        dos.append(prependCharacterData);
        if (!opt.noProcessingComments) dos.append(makeCommentIfData(processingComments));
        String comment = "";
        if (this.comment != null) {
            comment = "\tc=\"" + getComment() + "\"";

        }
        dos.append(tab(2) + "<" + getTagName() + " n=\"" + getValue() + "\" " + comment + "/> "  +appendCharacterData.trim()+"\n");
    }

    /**
     * 
     */
    @Override
    public String toString() {
        String str = "<" + getValue() + ">";
        return str;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment
     *                the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}