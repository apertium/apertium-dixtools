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
package dictools.crossmodel;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;

import dics.elements.utils.DicOpts;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class CrossModel {

    
    private CrossActionList crossActions;
    
    private String fileName;
    
    private String filePath;
    
    private String encoding = "UTF-8";

    
    public CrossModel() {
        crossActions = new CrossActionList();
    }

    /**
     * 
     * @param crossAction
     */
    public void addCrossAction(CrossAction crossAction) {
        crossActions.add(crossAction);
    }

    /**
     * 
     * @return Undefined         */
    public CrossActionList getCrossActions() {
        return crossActions;
    }

    /**
     * 
     * @param id
     * @return Undefined         */
    public CrossAction getCrossAction(String id) {
        for (CrossAction ca : getCrossActions()) {
            if (ca.getId().equals(id)) {
                return ca;
            }
        }
        return null;
    }

    /**
     * 
     * @param fileName
     */
    public void printXML(String fileName, DicOpts opt) {
        this.printXML(fileName, this.getEncoding(), opt);
    }

    /**
     * 
     * @param fileName
     * @param encoding
     */
    public void printXML(String fileName, String encoding, DicOpts opt) {
        BufferedOutputStream bos;
        FileOutputStream fos;
        OutputStreamWriter dos;

        try {
            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, encoding);
            dos.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
            dos.append("<!-- Examples of patterns not found -->\n");
            dos.append("<cross-model>\n");
            int i = 0;
            CrossActionList cal = getCrossActions();
            Collections.sort(cal);
            for (CrossAction crossAction : cal) {
                crossAction.printXML(dos, i, opt);
                i++;
            }
            dos.append("</cross-model>\n");
            dos.append("<!-- " + i + " cross actions. -->\n");

            fos = null;
            bos = null;
            dos.close();
            dos = null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception eg) {
            eg.printStackTrace();
        }
    }

    
    public String getFileName() {
        return this.fileName;
    }

    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    
    public String getFilePath() {
        return this.filePath;
    }

    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 
     * @return Undefined
     */
    public String getEncoding() {
        return this.encoding;
    }

    /**
     * 
     * @param encoding
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * 
     * @param crossActions
     */
    public void setCrossActionList(CrossActionList crossActions) {
        this.crossActions = crossActions;
    }

    
    public void rename() {
        CrossActionList rList = new CrossActionList();
        for (CrossAction cA : this.getCrossActions()) {
            rList.add(cA.rename());
        }
        this.setCrossActionList(rList);
    }

    /**
     * 
     * @return true if the cross model is valid
     */
    public boolean isValid() {
        boolean errorsFound = false;
        for (CrossAction cA : this.getCrossActions()) {
            if (!cA.isValid()) {
                errorsFound = true;
            }
        }
        if (errorsFound) {
            return false;
        } else {
            return true;
        }
    }
}
