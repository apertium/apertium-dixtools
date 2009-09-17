/*
 * Copyright (C) 2008 Universitat d'Alacant / Universidad de Alicante
 * Copyright (C) 2008 Enrique Benimeli Bofarull
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
package dictools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import dictools.utils.dicmaker.DictCCDictionary;
import dictools.utils.dicmaker.DictionaryWriter;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Dix2Tiny extends AbstractDictTool {

    private String bilFileName;
    private String sltlCode;
    private String sltlFull;
    private String platform;
    private String filter;

    public Dix2Tiny() {
    }

    public void do_tiny() {
        this.processArguments();

        if (this.platform.equals("j2me")) {
            this.do_j2me();
        }

        if (this.platform.equals("palm")) {
            this.do_palm();
        }

        if (this.platform.equals("all")) {
            this.do_j2me();
            this.do_palm();
        }


    }

    private void do_j2me() {
        System.err.println("Packing linguistic data for apertium-tinylex (J2ME)...");

        Dix2MDix dix2mdix = null;
        if (this.filter != null) {
            System.err.println("Filter: " + this.filter);
            dix2mdix = new Dix2MDix(new TinyFilter(this.filter));
        } else {
            System.err.println("Filter: default");
            dix2mdix = new Dix2MDix(new TinyFilter());

        }
        dix2mdix.bilFileName = bilFileName;
        dix2mdix.sltlCode = sltlCode;
        dix2mdix.sltlFull = sltlFull;

        dix2mdix.do_convert();

    }

    private void do_palm() {
        System.err.println("Converting linguistic data for Palm (.cc files)...");
        
        Dix2CC dix2cc = null;
        if (this.filter != null) {
            System.err.println("Filter: " + this.filter);
            dix2cc = new Dix2CC(new TinyFilter(this.filter));
        } else {
            System.err.println("Filter: default");
            dix2cc = new Dix2CC(new TinyFilter());
        }
        dix2cc.bilFileName = bilFileName;
        String outFileName = this.sltlCode + "-data.cc";
        dix2cc.outFileName = outFileName;

        // check Exception
        String[] langs = this.sltlFull.split("-");
        String slFull = langs[0];
        String tlFull = langs[1];


        dix2cc.do_convert();

        System.err.println("Creating PDB file...");
        try {
            DictCCDictionary dicPalm = new DictCCDictionary(new FileInputStream(new File(outFileName)), slFull, tlFull);
            DictionaryWriter dicW = new DictionaryWriter(sltlCode + "-apertium-palm", dicPalm);
            dicW.writeToFile((new File(sltlCode + "-apertium-palm.pdb")));
        } catch (IOException ioe) {
        }

    }

    private void processArguments() {
        if (this.arguments.length >= 5) {
            bilFileName = this.arguments[1];
            this.sltlCode = this.arguments[2];
            this.sltlFull = this.arguments[3];
            this.platform = this.arguments[4];
        }
        if (this.arguments.length > 5) {

            this.filter = this.arguments[5];
        System.out.println("Filter: " +  filter);
        }
    }
}
