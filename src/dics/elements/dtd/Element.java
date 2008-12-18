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
public class Element implements Cloneable {

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
     * XML comments originating from the processing if the file. Will be added as <!--   --> just before the XML elemen
     */
    protected String comments;

    /**
     * blanks, newlines and XML comments originating from a original loaded file. Will be added before the XML elemen (before comments)
     */
    protected String prependCharacterData="";
    
    /**
     * XML comments, blanks and newlines originating from a loaded file. Will be added before the XML elemen (before comments)
     */
    public void setPrependCharacterData(String prependCharacterData) {
      this.prependCharacterData = prependCharacterData;
    }
    
    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    protected void printXML(final Appendable dos, final DicOpts opt) throws IOException {
        // write blank lines and comments from original file
        dos.append(prependCharacterData);
        if (comments!=null) dos.append("<!--" + comments + "-->");
        dos.append("<" + getTagName() + "/>");
    }

    /**
     * 
     * @param nTabs
     * @return Undefined         */
    protected String tab(final int nTabs) {
        /*
        String sTabs = "";
        for (int i = 0; i < nTabs; i++) {
            sTabs += "  ";
        }
        return sTabs;
         */
        return "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t".substring(0,nTabs);
    }

    /**
     * 
     * @param value
     */
    protected void setTagName(final String value) {
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
  public static final String maskNull(String str) {
        return (str == null?"":str);
  }
    
    /**
     * Appends to 
     * @param value
     */
    public void addComments(final String value) {
        comments = maskNull(comments);

        comments += tab(3) + value + "\n";
    }

    /**
     * XML comments originating from the processing if the file. Will be added as <!--   --> just before the XML elemen
     */
    public void setComments(final String value) {
        comments = value;
    }


    /**
     * XML comments originating from the processing if the file. Will be added as <!--   --> just before the XML elemen
     */
    public String getComments() {
        return comments;
    }

    /**
     * 
     */
    @Override
    public Object clone() {
        try {
            final Element cloned = (Element) super.clone();
            return cloned;
        } catch (final CloneNotSupportedException ex) {
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
    public void setValueNoTags(String valueNoTags) {
        this.valueNoTags = valueNoTags;
    }

    /**
     * @return the temp
     */
    public final String getTemp() {
        return temp;
    }

    /**
     * @param temp
     *                the temp to set
     */
    public final void setTemp(String temp) {
        this.temp = temp;
    }

    /**
     * 
     */
    @Override
    public String toString() {
        String str = "";
        str += getValue();
        return str;
    }
}
