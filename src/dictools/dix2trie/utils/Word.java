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
package dictools.dix2trie.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;

import dics.elements.dtd.S;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Word {

    
    private String key;
    
    private String value;
    
    private Entry entry;

    /**
     * 
     * @param entry
     */
    public Word(Entry entry) {
        this.entry = entry;
    }

    /**
     * 
     * @param key
     * @param value
     */
    public Word(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 
     * @param key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 
     * @return The key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * 
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 
     * @return The value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * 
     * @param tabs
     * @param osw
     */
    public void print(int tabs, OutputStreamWriter osw) {
        try {
            String sTabs = "";
            for (int i = 0; i < tabs; i++) {
                sTabs += " ";
            }

            osw.write(sTabs + "<w v=\"" + entry.getKey() + "\">\n");
            osw.write(sTabs + "\t<l>");
            osw.write(entry.getKey());


            for (S sE : entry.getKeyAttr()) {
                osw.write("<s n=\"" + sE.getValue() + "\"/>");
            }

            osw.write("</l>\n");

            osw.write(sTabs + "\t<r>");
            osw.write(entry.getValue());

            for (S sE : entry.getValueAttr()) {
                osw.write("<s n=\"" + sE.getValue() + "\"/>");
            }
            osw.write("</r>\n");

            osw.write(sTabs + "</w>\n");

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
