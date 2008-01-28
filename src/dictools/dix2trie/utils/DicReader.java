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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class DicReader {

    /**
     * 
     */
    private String fileName;
    
    /**
     * 
     */
    private EntryList entryList;

    /**
     * 
     * @param fileName
     */
    public DicReader(final String fileName) {
        this.fileName = fileName;
        entryList = new EntryList();
    }

    /**
     * 
     * @return The list of entries
     */
    public final EntryList read() {
        try {
            FileReader input = new FileReader(fileName);
            BufferedReader bufRead = new BufferedReader(input);
            String line;

            line = bufRead.readLine();
            while (line != null) {
                line = bufRead.readLine();
                if (line != null) {
                    Entry entry = getEntry(line);
                    if (entry != null) {
                        entryList.add(entry);
                    }
                }
            }
            bufRead.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(-1);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(-1);
        }
        return this.entryList;
    }

    /**
     * 
     * @param line
     * @return The entry
     */
    private Entry getEntry(final String line) {
        StringTokenizer strTokenizer = new StringTokenizer(line, ":");
        boolean first = true;
        String left = null;
        String right = null;
        if (strTokenizer != null) {
            while (strTokenizer.hasMoreTokens()) {
                String token = strTokenizer.nextToken();
                if (token != null) {
                    if (first) {
                        left = token;
                        first = false;
                    } else {
                        right = token;
                        Entry entry = new Entry(left, right);
                        return entry;
                    }
                }
            }
        }
        return null;
    }
}
