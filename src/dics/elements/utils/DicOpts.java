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

package dics.elements.utils;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple options class to contain general options, especially for output formatting
 * Its fields should be public and no logic should be here
 * @author j
 */
public class DicOpts implements Cloneable {
   
  /**
   * Current (volatile) settings while traversiong a data structure.
   */
  public boolean nowAlign = false;

  /**
   * Settings from user choice
   */
  public boolean sectionElementsAligned = false;
  public boolean pardefElementsAligned = false;
  public int alignP = 10;
  public int alignR = 55;

  public static DicOpts std = new DicOpts();
  public static DicOpts std1line = new DicOpts(false, true, 0, 0);
  public static DicOpts stdaligned = new DicOpts(false, true, 10, 55);
  public static DicOpts stdnow1line = std1line.copy().setNowAlign(true);

  public boolean debug = false;
    public boolean stripEmptyLines = false;

  
  public  DicOpts() {
  }
  
  public DicOpts(boolean alignPardefs, boolean alignEntries) {
    this.pardefElementsAligned = alignPardefs;
    this.sectionElementsAligned = alignEntries;
  }

  public DicOpts(boolean alignPardefs, boolean alignEntries, int alignmentP, int alignmentR) {
    this.pardefElementsAligned = alignPardefs;
    this.sectionElementsAligned = alignEntries;
    this.alignP = alignmentP;
    this.alignR = alignmentR;
  }

  
  public DicOpts copy() {
    try {
      return (DicOpts) clone();
    } catch (CloneNotSupportedException ex) {
      throw new IllegalStateException(ex);
    }
  }


  public DicOpts setNowAlign(boolean b) {
    nowAlign = b;
    return this;
  }
}
