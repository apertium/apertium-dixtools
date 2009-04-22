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
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JLabel;

/**
 *
 * @author Enrique Benimeli Bofarull
 *
 */
public class Msg {

    /**
     *
     */
    static final int LABEL = 1;
    /**
     *
     */
    static final int NORMAL = 0;
    /**
     *
     */
    private int type = 0;
    /**
     *
     */
    private boolean debug = false;
    /**
     *
     */
    private JLabel label;
    /**
     *
     */
    private DataOutputStream log;
    /**
     *
     */
    private String logFileName;

    /**
     *
     *
     */
    public Msg() {
    //debug = false;
    }

    /**
     *
     */
    public Msg(JLabel label) {
        this.label = label;
        this.setType(this.LABEL);
    }

    /**
     *
     *
     */
    public Msg(String logFileName) {
        //debug            = false;
        this.logFileName = logFileName;
    }

    /**
     *
     * @param logFileName
     */
    private void openLogStream(String logFileName) {
        try {
//      Logger.getLogger(CrossAction.class.getName()).log(Level.SEVERE, null, ex);

            // Msg will be change to Logger soon...
            // boolean append = true;
            // FileHandler handler = new FileHandler(logFileName, append);
            // Logger logger = Logger.getLogger("crossdics");
            // logger.addHandler(handler);
            File file = new File(logFileName);
            FileOutputStream fos = new FileOutputStream(file);

            log = new DataOutputStream(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param text
     */
    public void err(String text) {
        System.err.println(text);
    }

    /**
     *
     * @param text
     */
    public void out(String text) {
        switch (this.getType()) {
            case LABEL:
                label.setText(text);

                break;

            default:
                System.out.print(text);
                break;
        }
    }

    /**
     *
     */
    public void msg(String text) {

        // Only for Java components (JLabel, etc)
        switch (this.getType()) {
            case LABEL:
                label.setText(text);
                break;

            default:
                break;
        }
    }

    /**
     *
     * @param text
     */
    public void log(String text) {
        if (isDebug()) {
        System.out.println(text);
            if (log == null) {
                openLogStream(logFileName);
            }

            try {
                log.writeBytes(text);
            } catch (IOException ioe) {
                System.err.println("Error writing log file " + getLogFileName());
            }
        }
    }

    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @param debug
     *                the debug to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * @return the logFileName
     */
    public String getLogFileName() {
        return logFileName;
    }

    /**
     * @param logFileName
     *                the logFileName to set
     */
    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;

        if (isDebug()) {
            openLogStream(logFileName);
        }
    }

    /**
     *
     */
    public void setType(int t) {
        this.type = t;
    }

    /**
     *
     */
    public int getType() {
        return type;
    }

    /**
     *
     */
    public void setLabel(JLabel label) {
        this.label = label;
        this.type = this.LABEL;
    }
}
//~ Formatted by Jindent --- http://www.jindent.com

