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

import java.util.Properties;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class HeaderElement extends Properties {

    /**
     * 
     * @return The type of dictionary: morph or bil
     */
    public String getDictionaryType() {
        String type = (String) this.get("type");
        return type;
    }

    /**
     * 
     * @return Is it a bilingual dictionary?
     */
    public boolean isBilingual() {
        String type = (String) this.get("type");
        return (type.equals("bil"));
    }

    /**
     * 
     * @return Is it a morphological dictionary?
     */
    public boolean isMorphological() {
        String type = (String) this.get("type");
        return (type.equals("mon"));
    }

    /**
     * 
     * @return Source language code
     */
    public String getSL() {
        String sl = (String) this.get("sl");
        return sl;
    }

    /**
     * 
     * @return Full name for source language
     */
    public String getSLFull() {
        String slFull = (String) this.get("sl-full");
        return slFull;
    }

    /**
     * 
     * @return Target language code
     */
    public String getTL() {
        String tl = (String) this.get("tl");
        return tl;
    }

    /**
     * Return the full name for target language
     * @return Full name for target language
     */
    public String getTLFull() {
        String tlFull = (String) this.get("tl-full");
        return tlFull;
    }

    /**
     * 
     * @param i
     * @return Alternative i
     */
    public String getAlternative(int i) {
        String alti = (String) this.get("alt" + i);
        return alti;
    }
}
