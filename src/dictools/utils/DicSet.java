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
package dictools.utils;

import java.util.ArrayList;

import dics.elements.dtd.Dictionary;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicSet extends ArrayList<Dictionary> {

    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Dictionary mon1;
    
	public Dictionary bil1;
    
	public Dictionary mon2;
    
	public Dictionary bil2;

    /**
     * 
     * @param mon1
     * @param bil1
     * @param mon2
     * @param bil2
     */
    public DicSet(Dictionary mon1, Dictionary bil1,
            Dictionary mon2, Dictionary bil2) {

        this.add(mon1);
        this.add(mon2);
        this.add(bil1);
        this.add(bil2);

        this.mon1 = mon1;
        this.mon1.type = Dictionary.MONOL;
        this.bil1 = bil1;
        this.bil1.type = Dictionary.BIL;
        this.mon2 = mon2;
        this.mon2.type = Dictionary.MONOL;
        this.bil2 = bil2;
        this.bil2.type = Dictionary.BIL;
    }

    /**
     * 
     * @param bilAB
     * @param monA
     * @param monB
     */
    public DicSet(Dictionary bilAB, Dictionary monA,
            Dictionary monB) {
        this.add(monA);
        this.add(monB);
        this.add(bilAB);

        mon1 = monA;
        bil1 = bilAB;
        mon2 = monB;
        bil2 = null;
    }

    /**
     * 
     * @param suffix
     */
    public void printXML(String suffix, DicOpts opt) {
        printMonolXML(suffix, opt);
        bil1.printXML(suffix + "-bilAB.dix", opt);
        //DicTools.removeExtension(getBil1().getFileName()) + "-" + suffix + ".dix");
        bil2.printXML(suffix + "-bilBC.dix", opt);
    //	DicTools.removeExtension(getBil2().getFileName()) + "-" + suffix + ".dix");
    }

    /**
     * 
     * @param suffix
     */
    public void printMonolXML(String suffix, DicOpts opt) {
        mon1.printXML(suffix + "-monA.dix", opt);
        //DicTools.removeExtension(getMon1().getFileName()) + "-"		+ suffix + ".dix");
        mon2.printXML(suffix + "-monC.dix", opt);
    //DicTools.removeExtension(getMon2().getFileName()) + "-"		+ suffix + ".dix");
    }
}
