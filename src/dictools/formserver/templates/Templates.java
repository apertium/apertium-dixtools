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
package dictools.formserver.templates;

import dics.elements.dtd.*;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import dictools.utils.DicOpts;
import dictools.utils.ElementList;

/**
 *
 * @author Enrique Benimeli Bofarull
 *
 */
public class Templates extends DixElement {

    public HeaderElement header;
    
    public ArrayList<Left> lefts;
    
    public int nEntries;
    
    public int nShared;
    
    public int nDifferent;
    
    public String type;
    
    public String fileName;
    
    
    public String leftLanguage;
    public String rightLanguage;
    
    
    public String xmlEncoding = "UTF-8";
    
    public Templates() {
      super("templates");
        lefts = new ArrayList<Left>();
    }



    /**
     *
     * @param dic
     */
    public Templates(Templates dic) {
        this();
        Left sectionElement = new Left("");
        lefts.add(sectionElement);

    }

   @Override
    public void printXML(Appendable dos, DicOpts opt) throws IOException {
        dos.append("<templates>\n");
        if (lefts != null) {
            DicOpts optNow = opt.copy().setNowAlign(opt.sectionElementsAligned);
            for (Left l : lefts) {
                // FIXME
                //l.printXML(dos, optNow);
            }
        }
        dos.append("</templates>\n");
    }


    /**
     * 
     * @param fileName
     * @param encoding
     */
    public void printXMLToFile(String fileName,DicOpts opt) {
        if (opt.detectAlignmentFromSource) {
          System.err.println("Note: You didnt specify any alignment options, so a good alignment will be detected from the data.\n"+
                                    "      (use -noalign to get the old unaligned multiline-XML-ish behaviour)");
          opt = opt.copy();
          opt.detectAlignmentFromSource = false;
        }

        this.fileName = fileName;
        try {
            Writer dos;
              if ("-".equals(fileName)) {
                 dos = new OutputStreamWriter(System.out);
              } else {          
                System.err.println("Writing file " + fileName);
                FileOutputStream fos = new FileOutputStream(fileName);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                dos = new OutputStreamWriter(bos, xmlEncoding);
              }
            dos.append("<?xml version=\"1.0\" encoding=\"" + xmlEncoding + "\"?>\n");
            if (!opt.noHeaderAtTop) {
              dos.append("<!--\n\tTemplates:\n");
              if (lefts != null) {
                  dos.append("\tSections: " + lefts.size() + "\n");
                  int ne = 0;
                  for (Left left : lefts) {
                      //ne += left.elements.size();
                  }
                  dos.append("\tEntries: " + ne);
              }
              if (opt.originalArguments != null) {
                  dos.append("\tLast processed by: apertium-dixtools");
                  for (String s : opt.originalArguments) dos.append(' ').append(s);
                  dos.append("\n");
              }
              dos.append(processingComments);

              dos.append("\n-->\n");
            }
            this.printXML(dos, opt);
            dos.close();
            dos = null;
        } catch (Exception eg) {
            eg.printStackTrace();
        }
    }


    /**
    *
    * @param id
    * @return Undefined     */
   public Left getLeft(String id) {
       for (Left left : lefts) {
           if (left.id.equals(id)) {
               return left;
           }
       }
       return null;
   }

    
    /**
     * 
     * @return Is there a header defined?
     */
    public boolean isHeaderDefined() {
        return (header != null);
    }

}