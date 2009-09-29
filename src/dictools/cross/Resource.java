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
package dictools.cross;

import java.util.Properties;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Resource extends Properties {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 
     * @return The source
     */
    public String getSource() {
        String source = (String) this.get("src");
        return source;
    }

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
     * @return 'true' if is cross-model
     */
    public boolean isCrossModel() {
        String type = (String) this.get("type");
        return (type.equals("cross-model"));
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
     * @param value
     * @return 'true' if source language is 'value'
     */
    public boolean isSL(String value) {
        String sl = (String) this.get("sl");
        if (sl.equals(value)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @param value
     * @return 'true' if target language is 'value'
     */
    public boolean isTL(String value) {
        String sl = (String) this.get("tl");
        if (sl.equals(value)) {
            return true;
        } else {
            return false;
        }
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
     * @param lang
     * @return 'true' if sl or tl is 'lang'
     */
    public boolean hasLanguage(String lang) {
        String sl = (String) this.get("sl");
        String tl = (String) this.get("tl");
        if (sl.equals(lang) || tl.equals(lang)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @return 'true' is this resource can be used for crossing dictionaries
     */
    public boolean isUseForCrossing() {
        String forcrossing = (String) this.get("for-crossing");
        if (forcrossing != null) {
            if (forcrossing.equals("yes")) {
                return true;
            }
        }
        return false;
    }
}
