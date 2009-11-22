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

import java.io.IOException;

import dictools.utils.DicOpts;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DixElement implements Cloneable, CharacterDataNeighbour {

    
    private String value;
    
    private String valueNoTags = "";
    
    public final String TAGNAME;

    public DixElement(String tagName) {
      TAGNAME = tagName;
    }

    /**
     * XML processingComments originating from the processing if the file. Will be added as <!--   --> just before the XML element
     */
    public String processingComments="";

    /**
     * blanks, newlines and XML processingComments originating from a original loaded file. Will be added before the XML element (before processingComments)
     */
    public String prependCharacterData="";

    
    /**
     * XML processingComments, blanks and newlines originating from a loaded file. Will be added before the XML elemen (before processingComments)
     */
    public void setPrependCharacterData(String prependCharacterData) {
      this.prependCharacterData = prependCharacterData;
    }
    
    public String getPrependCharacterData() {
      return prependCharacterData;
    }

    public String getAppendCharacterData() {
      return appendCharacterData;
    }


    /**
     * blanks, newlines and XML processingComments originating from a original loaded file. Will be added after the XML elemen (before processingComments)
     */
    public String appendCharacterData="";
    
    /**
     * XML processingComments, blanks and newlines originating from a loaded file. Will be added after the XML elemen (before processingComments)
     */
    public void setAppendCharacterData(String appendCharacterData) {
      this.appendCharacterData = appendCharacterData;
    }

    public void addAppendCharacterData(String moreappendCharacterData) {
      this.appendCharacterData = appendCharacterData + moreappendCharacterData;
    }



   /**
     * blanks, newlines and XML processingComments originating from a original loaded file. Will be added inside the XML element (before processingComments)
     */
    public String justInsideStartTagCharacterData="";

    public void setJustInsideStartTagCharacterData(String justInsideStartTagCharacterData) {
        this.justInsideStartTagCharacterData = justInsideStartTagCharacterData;
    }

   
    protected void printXML(Appendable dos, DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        dos.append(prependCharacterData);
        if (!opt.noProcessingComments) dos.append(makeTabbedCommentIfData(processingComments));
        dos.append("<" + TAGNAME + "/>");
        dos.append(appendCharacterData);
    }

    @Override
    public String toString() {
      try {
        StringBuilder b=new StringBuilder();
        printXML(b, DicOpts.STD_COMPACT.setNowAlign(true));
        while (b.length()>0 && Character.isWhitespace(b.charAt(b.length()-1))) b.deleteCharAt(b.length()-1);
        return b.toString();
      } catch (IOException ex) {
        ex.printStackTrace();
        return ex.toString();
      }
    }

    protected static String tab(int nTabs) {
        return "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t".substring(0,nTabs);
    }

    public static String makeTabbedCommentIfData(String commentContent) {
    if (commentContent.isEmpty()) return "";
    return tab(2)+"<!-- "+commentContent.trim()+" -->\n";
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

    
    @Override
    public Object clone() {
        try {
            DixElement cloned = (DixElement) super.clone();
            return cloned;
        } catch (CloneNotSupportedException ex) {
            return null;
        }

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueNoTags() {
        return valueNoTags;
    }

  /** returns the stream format. F.eks. "naŭdek unu<num>" */
  public String getStreamContent() {
    System.err.println("WARN: getStreamContent() called on non-stream tag");
    return toString();
  }

    void setValueNoTags(String valueNoTags) {
        this.valueNoTags = valueNoTags;
    }
}
