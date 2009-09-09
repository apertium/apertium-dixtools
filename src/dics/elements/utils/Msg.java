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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Enrique Benimeli Bofarull
 *
 */
public class Msg {

    public PrintStream log;
    public PrintStream out = System.out;
    public PrintStream err = System.err;

    private Msg() {
    }
    
    private static Msg instance =new Msg();

    public static Msg inst() {
      return instance;
    }

    public void err(String text) {
        err.println(text);
    }

    public void out(String text) {
        out.print(text);
    }

    /**
     *
     * @param text
     */
    public void log(String text) {
        if (log!=null) {
            //System.out.println(text);
            log.println(text);
        }
    }

  public void setDebug(boolean b) {
    if (b && log==null) log = System.err;
    if (!b && log!=null) log = null;
  }

    /**
     * @param logFileName
     *                the logFileName to set
     */
    public void setLogFileName(String logFileName) {
        try {
          log=new PrintStream(logFileName);
          log.println("Logging started "+new Date());
          Runtime.getRuntime().addShutdownHook(new Thread() { public void run() {
            log.close();
          }});
        } catch (Exception ex) {
          err("Cannot log to "+logFileName);
        }
    }
}