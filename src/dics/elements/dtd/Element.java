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
package dics.elements.dtd;

import dics.elements.utils.DicOpts;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class Element implements Cloneable, CharacterDataNeighbour {

    /**
     * 
     */
    private String value;
    /**
     * 
     */
    private String temp;
    /**
     * 
     */
    private String valueNoTags = "";
    /**
     * 
     */
    private String TAGNAME;
    
    /**
     * XML processingComments originating from the processing if the file. Will be added as <!--   --> just before the XML element
     */
    protected String processingComments="";

    /**
     * blanks, newlines and XML processingComments originating from a original loaded file. Will be added before the XML element (before processingComments)
     */
    protected String prependCharacterData="";

    
    /**
     * XML processingComments, blanks and newlines originating from a loaded file. Will be added before the XML elemen (before processingComments)
     */
    public void setPrependCharacterData(String prependCharacterData) {
      this.prependCharacterData = prependCharacterData;
    }
    
    /**
     * blanks, newlines and XML processingComments originating from a original loaded file. Will be added after the XML elemen (before processingComments)
     */
    protected String appendCharacterData="";
    
    /**
     * XML processingComments, blanks and newlines originating from a loaded file. Will be added after the XML elemen (before processingComments)
     */
    public void setAppendCharacterData(String appendCharacterData) {
      this.appendCharacterData = appendCharacterData;
    }


        /**
     * blanks, newlines and XML processingComments originating from a original loaded file. Will be added inside the XML element (before processingComments)
     */
    protected String justInsideStartTagCharacterData="";

    public void setJustInsideStartTagCharacterData(String justInsideStartTagCharacterData) {
        this.justInsideStartTagCharacterData = justInsideStartTagCharacterData;
    }

   
    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    protected void printXML(Appendable dos, DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        dos.append(prependCharacterData);
        if (!opt.noProcessingComments) dos.append(makeCommentIfData(processingComments));
        dos.append("<" + getTagName() + "/>");
        dos.append(appendCharacterData);
    }

    /**
     * 
     * @param nTabs
     * @return Undefined         */
    protected static String tab(int nTabs) {
        /*
        String sTabs = "";
        for (int i = 0; i < nTabs; i++) {
            sTabs += "  ";
        }
        return sTabs;
         */
        return "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t".substring(0,nTabs);
    }

    public static String makeCommentIfData(String commentContent) {
    if (commentContent.isEmpty()) return "";
    return tab(2)+"<!-- "+commentContent.trim()+" -->\n";
  }

    
    /**
     * 
     * @param value
     */
    protected void setTagName(String value) {
        TAGNAME = value;
    }

    /**
     * 
     * @return Undefined         */
    protected final String getTagName() {
        return TAGNAME;
    }

  /**
   * Utility method for masking a null string
   * @param str
   * @return "" if str==null, else the str itself
   */  
  public final static String maskNull(String str) {
        return (str == null?"":str);
  }

  
  
  
    /**
     * Appends to XML processingComments originating from the processing if the file. Will be added as <!--   --> just before the XML element
     * @param value data to be added
     */
    public void addProcessingComment(String value) {
        processingComments += tab(3) + value + "\n";
    }

    /**
     * XML processingComments originating from the processing if the file. Will be added as <!--   --> just before the XML element
     */
    public void setProcessingComments(String value) {
        processingComments = value;
    }


    /**
     * XML processingComments originating from the processing if the file. Will be added as <!--   --> just before the XML element
     */
    public final String getProcessingComments() {
        return processingComments;
    }

    /**
     * 
     */
    @Override
    public Object clone() {
        try {
            Element cloned = (Element) super.clone();
            return cloned;
        } catch (CloneNotSupportedException ex) {
            return null;
        }

    }

    /**
     * 
     * @return Undefined         
     */
    public String getValue() {
        return value;
    }

    /**
     * 
     * @param value
     */
    protected void setValue(String value) {
        this.value = value;
    }

    /**
     * 
     * @return Undefined         */
    public String getValueNoTags() {
        return valueNoTags;
    }

    /**
     * 
     * @param valueNoTags
     */
    void setValueNoTags(String valueNoTags) {
        this.valueNoTags = valueNoTags;
    }

    /**
     * @return the temp
     */
    public String getTemp() {
        return temp;
    }

    /**
     * @param temp
     *                the temp to set
     */
    public void setTemp(String temp) {
        this.temp = temp;
    }

    /**
     * 
     */
    @Override
    public String toString() {
        String str = "" + getValue();
        return str;
    }
}
