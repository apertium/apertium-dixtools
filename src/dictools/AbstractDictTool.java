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
package dictools;

import dictools.utils.DicOpts;
import dictools.utils.Msg;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author j
 */
public class AbstractDictTool {

    /**
     * Options
     */
    public DicOpts opt = DicOpts.STD.copy();

    protected Msg msg = Msg.inst();
    
    protected String[] arguments;

  /** argument pair was removed, adjust rest of args accordingly
   * @param i index
   * @param n number of elements to remove
   */
  protected void removeArgs(int i, int n) {
      ArrayList<String> a = new ArrayList<String>(Arrays.asList(this.arguments));
      while (n-->0) a.remove(i);
      this.arguments =  a.toArray(new String[a.size()]);
  }


  public void executeTool(DicOpts opt, String[] arguments) throws IOException {
     this.opt = opt;
     this.arguments = arguments;
     this.executeTool();
  }

  /** To be overridden by children */
  public void executeTool() throws IOException {
    throw new UnsupportedOperationException("Not yet implemented for this tool");
  }

  protected void failWrongNumberOfArguments(String[] remainingarguments) {
    msg.err("Usage: apertium-dixtools "+toolHelp()+"\n");
    if (remainingarguments.length==1)
      throw new IllegalArgumentException("Missing some more arguments.");
    else
      throw new IllegalArgumentException("One of these arguments had a problem: "+ Arrays.toString(remainingarguments));
  }


  /** To be overridden by children */
  public String toolHelp() {
    throw new UnsupportedOperationException("Not yet implemented for this tool");
  }
}
