/*
 * Copyright (C) 2008 Dana Esperanta Junulara Organizo http://dejo.dk/
 * Author: Jacob Nordfalk
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

package misc.eoen;

import java.util.LinkedHashSet;

/**
 *
 * @author j
 */
public class GenerateAlphabetLetters {
  public static void main(String[] args) {
    LinkedHashSet<String> eoDixAldono=new LinkedHashSet<String>();
    LinkedHashSet<String> enDixAldono=new LinkedHashSet<String>();
    LinkedHashSet<String> eoEnDixAldono=new LinkedHashSet<String>();

    for (char ch = 'a'; ch<='z'; ch++) {
      Paro p = new Paro();
      p.setKlasoTag(p.NP);
      p.setAliajTag("<alpha>");
      p.orgEn = p.rootEn = ""+ch;
      p.rootEo = ""+ch;
        eoDixAldono.add(p.apertiumEo());
        enDixAldono.add(p.apertiumEn());
        eoEnDixAldono.add(p.apertiumEoEn());

    }

    Iloj.skribu(eoDixAldono, "ald_tradukunet.eo.dix");
    Iloj.skribu(enDixAldono, "ald_tradukunet.en.dix");
    Iloj.skribu(eoEnDixAldono, "ald_tradukunet.eo-en.dix");

    
  }
}
