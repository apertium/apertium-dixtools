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

package dictools.crossmodel;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class Constant {

    /**
         * 
         */
    private String name;

    /**
         * 
         */
    private String value;

    /**
         * 
         * 
         */
    public Constant() {

    }

    /**
         * 
         * @return Undefined         */
    public String getName() {
	return name;
    }

    /**
         * 
         * @param name
         */
    public void setName(final String name) {
	this.name = name;
    }

    /**
         * 
         * @return Undefined         */
    public String getValue() {
	return value;
    }

    /**
         * 
         * @param value
         */
    public void setValue(final String value) {
	this.value = value;
    }

}
