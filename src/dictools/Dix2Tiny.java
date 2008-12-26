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

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Dix2Tiny  extends AbstractDictTool{

    private String bilFileName;
    private String sltlCode;
    private String sltlFull;
    private String platform;

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
        System.out.println("Packing linguistic data for apertium-tinylex (J2ME)...");
        Dix2MDix dix2mdix = new Dix2MDix();
        dix2mdix.setBilFileName(bilFileName);
        dix2mdix.setSltlCode(sltlCode);
        dix2mdix.setSltlFull(sltlFull);

        dix2mdix.do_convert();

    }

    private void do_palm() {
        System.out.println("Converting linguistic data for Palm (.cc files)...");
        Dix2CC dix2cc = new Dix2CC();
        dix2cc.setBilFileName(bilFileName);
        dix2cc.setSltlCode(sltlCode);
        dix2cc.setSltlFull(sltlFull);
        dix2cc.setOutFileName(this.sltlCode + "-data.cc");

        dix2cc.do_convert();
    }

    private void processArguments() {
        if (this.arguments.length == 5) {
            bilFileName = this.getArguments()[1];
            this.sltlCode = this.getArguments()[2];
            this.sltlFull = this.getArguments()[3];
            this.platform = this.getArguments()[4];

        }
    }

}
