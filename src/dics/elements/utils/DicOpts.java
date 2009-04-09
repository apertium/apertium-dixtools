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

  public DicOpts pardefAlignOpts = null; // null = Use same alignment for pardefs as for other entries
    public String[] originalArguments;
    public void copyAlignSettings(DicOpts source) {
        sectionElementsAligned = source.sectionElementsAligned;
        pardefElementsAligned = source.pardefElementsAligned;
        pardefAlignOpts = source.pardefAlignOpts;
        alignP = source.alignP;
        alignR = source.alignR;
    }

    public DicOpts setPardefAlign(DicOpts pardefAlignOpts) {
      this.pardefAlignOpts = pardefAlignOpts;
      return this;
  }


  /**
   * Standard XML-like format with separate line and indent for each element
   */
  public static final DicOpts STD = new DicOpts();
  public static final DicOpts STD_ALIGNED = new DicOpts(false, true, 10, 55);

  public static final DicOpts STD_ALIGNED_BIDIX = new DicOpts(true, true, 10, 55);
  public static final DicOpts STD_ALIGNED_MONODIX = new DicOpts(true, true, 25, 45).setPardefAlign(new DicOpts(true, true, 10, 30));
  
  public static final DicOpts STD_1_LINE = new DicOpts(false, true, 0, 0);
  public static final DicOpts STD_NOW_1_LINE = STD_1_LINE.copy().setNowAlign(true);

  
  /**
   echo "    -debug                        prints (possibly) some debugging information"
   echo "    -stripEmptyLines    removes empty lines originating from original file"
   echo "    -noProcComments    don't add comments telling which processing was done"
  */
  public boolean debug = false;
  public boolean stripEmptyLines = false;
  public boolean noProcessingComments = false;

  
  public  DicOpts() {
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
