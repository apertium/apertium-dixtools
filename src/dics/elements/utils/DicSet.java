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
package dics.elements.utils;

import java.util.ArrayList;

import dics.elements.dtd.Dictionary;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicSet extends ArrayList<Dictionary> {

    
    private Dictionary mon1;
    
    private Dictionary bil1;
    
    private Dictionary mon2;
    
    private Dictionary bil2;

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
        this.mon1.setType(Dictionary.MONOL);
        this.bil1 = bil1;
        this.bil1.setType(Dictionary.BIL);
        this.mon2 = mon2;
        this.mon2.setType(Dictionary.MONOL);
        this.bil2 = bil2;
        this.bil2.setType(Dictionary.BIL);
    }

    /**
     * 
     * @param mon1
     */
    public DicSet(Dictionary mon1) {
        this.add(mon1);

        this.mon1 = mon1;
        bil1 = null;
        mon2 = null;
        bil2 = null;
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
     * @return Undefined         */
    public Dictionary getMon1() {
        return mon1;
    }

    /**
     * 
     * @return Undefined         */
    public Dictionary getMon2() {
        return mon2;
    }

    /**
     * 
     * @return Undefined         */
    public Dictionary getBil1() {
        return bil1;
    }

    /**
     * 
     * @return Undefined         */
    public Dictionary getBil2() {
        return bil2;
    }

    
    public void reportMetrics() {
        System.err.println("monA");
        mon1.reportMetrics();
        System.err.println("monC");
        mon2.reportMetrics();
        System.err.println("bilAB");
        bil1.reportMetrics();
        System.err.println("bilBC");
        bil2.reportMetrics();
    }

    /**
     * 
     * @param suffix
     */
    public void printXML(String suffix, DicOpts opt) {
        printMonolXML(suffix, opt);
        getBil1().printXML(suffix + "-bilAB.dix", opt);
        //DicTools.removeExtension(getBil1().getFileName()) + "-" + suffix + ".dix");
        getBil2().printXML(suffix + "-bilBC.dix", opt);
    //	DicTools.removeExtension(getBil2().getFileName()) + "-" + suffix + ".dix");
    }

    /**
     * 
     * @param suffix
     */
    public void printMonolXML(String suffix, DicOpts opt) {
        getMon1().printXML(suffix + "-monA.dix", opt);
        //DicTools.removeExtension(getMon1().getFileName()) + "-"		+ suffix + ".dix");
        getMon2().printXML(suffix + "-monC.dix", opt);
    //DicTools.removeExtension(getMon2().getFileName()) + "-"		+ suffix + ".dix");
    }
}
