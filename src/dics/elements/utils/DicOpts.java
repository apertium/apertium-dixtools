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
    public boolean now1line = false;
    public boolean nowAlign = false;
  
    
    /**
     * Settings from user choice
     */
    public boolean pardefElementsOn1line = false;
    public boolean sectionElementsOn1line = false;

    public boolean sectionElementsAligned = false;
    public boolean pardefElementsAligned = false;
    public int alignP = 10;
    public int alignR = 55;

  
  
  public static final DicOpts std = new DicOpts();
  public static final DicOpts std1line = new DicOpts(false, true, 0, 0);
  public static final DicOpts stdaligned1line = new DicOpts(false, true, 10, 55);
  public static final DicOpts stdnow1line = std1line.copy().setNow1line(true);
  
  public  DicOpts() {
  }
  
  public DicOpts(boolean pardef1line, boolean entries1line) {
    this.pardefElementsOn1line = pardef1line;
    this.sectionElementsOn1line = entries1line;
  }

  public DicOpts(boolean pardef1line, boolean entries1line, int alignP, int alignR) {
    this.pardefElementsAligned = pardef1line;
    this.sectionElementsAligned = entries1line;
    this.pardefElementsOn1line = pardef1line;
    this.sectionElementsOn1line = entries1line;
    this.alignP = alignP;
    this.alignR = alignR;
  }

  
  public DicOpts copy() {
    try {
      return (DicOpts) clone();
    } catch (CloneNotSupportedException ex) {
      Logger.getLogger(DicOpts.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
  }

  public DicOpts setNow1line(boolean b) {
    now1line = b;
    return this;
  }

  public DicOpts setNowAlign(boolean b) {
    nowAlign = b;
    return this;
  }

}
