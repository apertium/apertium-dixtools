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

import dics.elements.dtd.S;
import java.util.ArrayList;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Entry {

    /**
     * 
     */
    private String key;
    
    /**
     * 
     */
    private ArrayList<S> keyAttr;
    
    /**
     * 
     */
    private String value;
    
    /**
     * 
     */
    private ArrayList<S> valueAttr;

    /**
     * 
     * @param key
     * @param value
     */
    public Entry(String key, String value) {
        this.key = key;
        keyAttr = new ArrayList<S>();
        this.value = value;
        valueAttr = new ArrayList<S>();
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
     * @param e
     */
    public void addKeyAttr(S e) {
        this.keyAttr.add(e);
    }

    /**
     * 
     * @param keyAttr
     */
    public void setKeyAttr(ArrayList<S> keyAttr) {
        this.keyAttr = keyAttr;
    }

    /**
     * 
     * @return The attributes
     */
    public ArrayList<S> getKeyAttr() {
        return this.keyAttr;
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
     * @param e
     */
    public void addValueAttr(S e) {
        this.valueAttr.add(e);
    }

    /**
     * 
     * @param valueAttr
     */
    public void setValueAttr(ArrayList<S> valueAttr) {
        this.valueAttr = valueAttr;
    }

    /**
     * 
     * @return The list of attributes
     */
    public ArrayList<S> getValueAttr() {
        return this.valueAttr;
    }
}
