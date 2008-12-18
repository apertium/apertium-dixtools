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

package misc;

import dictools.cmproc.State;
import java.io.File;
import java.util.Arrays;

/**
 *
 * @author j
 */
public class Optimization {
    public static void main(final String[] args) {
      new File("dix").mkdir();
      dictools.ProcessDics.main(new String[] {"cross-param", 
      "-bilAB", "-r", "apertium-es-ca.es-ca.dix", "-bilBC", "-r", "apertium-en-ca.en-ca.dix", "-monA", "apertium-es-ca.es.dix", "-monC", "apertium-en-ca.en.metadix", "-cross-model", "../../../../apertium-dixtools/schemas/cross-model.xml"});

       // System.err.println(Arrays.toString(State.freq));
      
      ;
    }
  

}
