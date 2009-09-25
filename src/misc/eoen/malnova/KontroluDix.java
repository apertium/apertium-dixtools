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

package misc.eoen.malnova;

import misc.eoen.Iloj;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 *
 * @author j
 */
public class KontroluDix {
  
  private static Set<String> forprenindiajEtikedoj = new HashSet<String>( Arrays.asList(
      "<nom> <acc> <GD>".split(" ")));
  
  public static void main(String[] args) throws IOException {

   
    LinkedHashMap<String, ArrayList<String>> aperEoDix[] =Iloj.leguDix("lt-expand apertium-eo-en.eo.dixtmp1",
        new HashSet<String>( Arrays.asList("<GD>".split(" "))));

    LinkedHashMap<String, ArrayList<String>> aperEnDix[] = Iloj.leguDix("lt-expand apertium-eo-en.en.dix",forprenindiajEtikedoj);
    //LinkedHashMap<String, ArrayList<String>> aperEnDix[]=Iloj.leguDix("zcat en.expanded.dix.gz");

    LinkedHashMap<String, ArrayList<String>> aperEoEnDix[]=Iloj.leguDix("lt-expand apertium-eo-en.eo-en.dix",forprenindiajEtikedoj);
    //LinkedHashMap<String, ArrayList<String>> aperEoEnDix[] = Iloj.leguDix("lt-expand apertium-eo-en.eo-en.dix");
    //LinkedHashMap<String, ArrayList<String>> aperEoEnDix[] = Iloj.leguDix("echo");


    //cxuDuoblajEroj(aperEoDix[0]);
    //cxuDuoblajEroj("eo dix",aperEoDix[1]);
    /*
    cxuDuoblajEroj(aperEnDix[0]);
    cxuDuoblajEroj(aperEnDix[1]);
     */
    //cxuDuoblajEroj("eo->en dix",aperEoEnDix[0]);
    cxuDuoblajEroj("en->eo dix",aperEoEnDix[1]);
    
  }

  private static void cxuDuoblajEroj(String titolo, LinkedHashMap<String, ArrayList<String>> linkedHashMap) {
    for (String k : linkedHashMap.keySet()) {
      ArrayList<String> v = linkedHashMap.get(k);
      if (v.size()>1) System.out.println(titolo+ ": "+k+ " => "+v);
    }      
  }
}
