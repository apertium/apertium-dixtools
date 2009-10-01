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

/**
 * @author Enrique Benimeli Bofarull
 * <jimregan> ~
<jimregan> 'Engage Orthographic Correction!'
<spectie> jacobEo, wake up the postgenerator
<jacobEo> its a tag used in postgen?
<jimregan> no, to start postgen
<jimregan> err... well, yes; it is used in postgen
<jimregan> but postgen doesn't do anything until it sees that mark
 */
public class A extends DixElement {

    
    public A() {
        super("a");
        setValue("<a/>");
    }

}
