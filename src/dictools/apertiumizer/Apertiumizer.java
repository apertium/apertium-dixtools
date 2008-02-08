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
package dictools.apertiumizer;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.LElement;
import dics.elements.dtd.PElement;
import dics.elements.dtd.RElement;
import dics.elements.dtd.SElement;
import dics.elements.dtd.SectionElement;
import dics.elements.dtd.TextElement;
import dics.elements.utils.EElementList;
import dictools.DictionaryReader;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Apertiumizer {

   /**
    * 
    */
   private String fileName;
   /**
    * 
    */
   private String outFileName;
   /**
    * 
    */
   private HashMap<String, String> en;

   /**
    * 
    * @param fileName
    */
   public Apertiumizer(final String fileName) {
      this.fileName = fileName;
   }

   /**
    * 
    */
   public final void apertiumize() {
      /*
      DictionaryReader encaReader = new DictionaryReader("apertium-en-ca.en-ca.dix");
      DictionaryElement enca = encaReader.readDic();
      en = new HashMap<String, String>();
      for (EElement e : enca.getAllEntries()) {
      LElement l = e.getLeft();
      String lv = l.getValueNoTags();
      en.put(lv, lv);
      }
      this.readFormat(2);
       */
      this.readFormat(3);
   }

   /**
    * 
    * @param format
    */
   private final void readFormat(int format) {
      try {
         FileInputStream fstream = new FileInputStream(fileName);
         DataInputStream in = new DataInputStream(fstream);
         BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
         String strLine;

         DictionaryElement dic = new DictionaryElement();
         dic.setXmlEncoding("UTF-8");
         SectionElement section = new SectionElement("main", "standard");
         dic.addSection(section);

         while ((strLine = br.readLine()) != null) {
            //if (!strLine.startsWith("#") && Character.isLetter(strLine.charAt(0))) {
            if (!strLine.startsWith("#")) {
               switch (format) {
                  case 0:
                     EElement e = readElementFormat_0(strLine);
                     section.addEElement(e);
                     break;
                  case 3:
                     EElement e3 = readElementFormat_3(strLine);
                     if (e3 != null) {
                        section.addEElement(e3);
                     }
                     break;
                  case 2:
                     EElementList eList = readElementFormat_2(strLine);
                     for (EElement e1 : eList) {
                        section.addEElement(e1);
                     }
                     break;
               }
            }
         }
         in.close();
         dic.printXML(this.getOutFileName(), "UTF-8");
      } catch (Exception e) {
         System.err.println("Error: " + e.getMessage());
      }

   }

   /**
    * 
    * @param strLine
    * @return The element
    */
   private final EElement readElementFormat_0(final String strLine) {
      StringTokenizer tokenizer = new StringTokenizer(strLine, ":");
      boolean lastToken = false;
      int i = 0;
      EElement e = new EElement();
      LElement left = new LElement();
      RElement right = new RElement();

      while (i < 3 && !lastToken && tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken();
         switch (i) {
            case 0:
               left.addChild(new TextElement(token));
               System.out.println(left);
               break;
            case 1:
               right.addChild(new TextElement(token));
               System.out.println(right);
               break;
            case 2:
               lastToken = true;
               SElement sE = new SElement(token);
               left.addChild(sE);
               right.addChild(sE);
               PElement pE = new PElement();
               pE.setLElement(left);
               pE.setRElement(right);
               e.addChild(pE);
               return e;

            }
         i++;
      }
      return null;
   }

   private final EElement readElementFormat_3(final String strLine) {
      StringTokenizer tokenizer = new StringTokenizer(strLine, "\t");

      boolean lastToken = false;
      int i = 0;
      EElement e = new EElement();
      LElement left = new LElement();
      RElement right = new RElement();

      while (i < 3 && !lastToken && tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken();
         switch (i) {
            case 0:
               left.addChild(new TextElement(token));
               break;
            case 1:
               lastToken = true;
               if (!token.equals("")) {
                  right.addChild(new TextElement(token));
                  PElement pE = new PElement();
                  pE.setLElement(left);
                  pE.setRElement(right);
                  e.addChild(pE);
                  return e;
               } else {
                  return null;
               }
            }
         i++;
      }

      return null;
   }

   /**
    * 
    * @param strLine
    * @return The element
    */
   private final EElement readElementFormat_1(final String strLine) {
      StringTokenizer tokenizer = new StringTokenizer(strLine, "\t");
      boolean lastToken = false;
      int i = 0;
      EElement e = new EElement();
      LElement left = new LElement();
      RElement right = new RElement();

      while (i < 3 && !lastToken && tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken();
         switch (i) {
            case 0:
               left.addChild(new TextElement(token));
               break;
            case 1:
               right.addChild(new TextElement(token));
               break;
            case 2:
               if (token.endsWith(":")) {
                  lastToken = true;
                  String newString = token.substring(0, token.length() - 1);
                  SElement sE = new SElement(newString);
                  left.addChild(sE);
                  right.addChild(sE);
                  PElement pE = new PElement();
                  pE.setLElement(left);
                  pE.setRElement(right);
                  e.addChild(pE);
                  return e;
               }
            }
         i++;
      }
      return null;
   }

   private final String replacePoS(String str) {
      //str = str.replaceAll("\"", "\\&quot;");
      str = str.replaceAll("\'", "");
      str = str.replaceAll("\"", "");
      str = str.replaceAll("\\<", "(");
      str = str.replaceAll("\\>", ")");
      str = str.replaceAll("\\{vi\\}", "<s n=\"vblex\"/>");
      str = str.replaceAll("\\{m\\}", "<s n=\"n\"/><s n=\"m\"/>");
      str = str.replaceAll("\\{f\\}", "<s n=\"n\"/><s n=\"f\"/>");
      str = str.replaceAll("\\{pl\\}", "<s n=\"n\"/><s n=\"pl\"/>");
      str = str.replaceAll("\\{adj\\}", "<s n=\"adj\"/>");
      str = str.replaceAll("\\{num\\}", "<s n=\"num\"/>");
      str = str.replaceAll("\\{adv\\}", "<s n=\"adv\"/>");
      str = str.replaceAll("\\{vt\\}", "<s n=\"vblex\"/>");
      str = str.replaceAll("\\{n\\}", "<s n=\"n\"/>");
      str = str.replaceAll("\\{m,f\\}", "<s n=\"n\"/><s n=\"mf\"/>");
      str = str.replaceAll("\\{f,m\\}", "<s n=\"n\"/><s n=\"mf\"/>");
      str = str.replaceAll("\\{m,n\\}", "<s n=\"n\"/><s n=\"mn\"/>");
      str = str.replaceAll("\\{f,n\\}", "<s n=\"n\"/><s n=\"f\"/>");
      str = str.replaceAll("\\{m,f,n\\}", "<s n=\"n\"/><s n=\"mf\"/>");
      str = str.replaceAll("\\{n,pl\\}", "<s n=\"n\"/><s n=\"pl\"/>");
      str = str.replaceAll("\\&", "\\&amp;");

      Pattern p = Pattern.compile("\\[[a-zA-ZüÜäÄöÖ.]+\\]");
      Matcher m = p.matcher(str);
      str = m.replaceAll("");
      str = str.replaceAll("\\>[\\s]+", "\\>");

      return str;
   }

   /**
    * 
    * @param strLine
    * @return
    */
   private final EElementList readElementFormat_2(String strLine) {

      strLine = strLine.replaceAll("[\\s]+\\{", "\\{");
      strLine = strLine.replaceAll("\\{ ", "\\}");
      strLine = strLine.replaceAll(" ::", "::");
      strLine = strLine.replaceAll(":: ", "::");
      strLine = strLine.replaceAll("; ", ";");
      strLine = strLine.replaceAll(" \\| ", "\\|");
      strLine = strLine.replaceAll("[\\s]+\\[", "\\[");
      strLine = strLine.replaceAll("\\] ", "\\]");
      //System.out.println(strLine);

      EElementList eList = new EElementList();

      StringTokenizer tokenizer = new StringTokenizer(strLine, "::");

      String leftStr = tokenizer.nextToken();
      //System.out.println("Left: "  + leftStr);
      if (!tokenizer.hasMoreTokens()) {
         return eList;
      }
      String rightStr = tokenizer.nextToken();
      //System.out.println("Right: "  + rightStr);	

      StringTokenizer leftTokenizer = new StringTokenizer(leftStr, "|");
      ArrayList<String> leftElements = new ArrayList<String>();
      while (leftTokenizer.hasMoreTokens()) {
         String leftE = leftTokenizer.nextToken();
         leftElements.add(leftE);
      }

      StringTokenizer rightTokenizer = new StringTokenizer(rightStr, "|");
      ArrayList<String> rightElements = new ArrayList<String>();
      while (rightTokenizer.hasMoreTokens()) {
         String rightE = rightTokenizer.nextToken();
         rightElements.add(rightE);
      }

      if (leftElements.size() == 0 || rightElements.size() == 0) {
         return eList;
      }

      for (int i = 0; i < leftElements.size(); i++) {
         String r = "";
         String l = leftElements.get(i);
         if (i < rightElements.size()) {
            r = rightElements.get(i);
         }
         System.out.println("<" + l + "> / <" + r + ">");

         StringTokenizer left2Tokenizer = new StringTokenizer(l, ";");
         ArrayList<String> left2Elements = new ArrayList<String>();
         StringTokenizer right2Tokenizer = new StringTokenizer(r, ";");
         ArrayList<String> right2Elements = new ArrayList<String>();

         while (left2Tokenizer.hasMoreTokens()) {
            left2Elements.add(left2Tokenizer.nextToken());
         }
         while (right2Tokenizer.hasMoreTokens()) {
            right2Elements.add(right2Tokenizer.nextToken());
         }

         for (int j = 0; j < left2Elements.size(); j++) {
            String itemLeft = left2Elements.get(j);
            if (itemLeft.charAt(0) == ' ') {
               itemLeft = itemLeft.substring(1, itemLeft.length());
            }

            String itemRight;
            if (j < right2Elements.size()) {
               itemRight = right2Elements.get(j);
               if (itemRight.charAt(0) == ' ') {
                  itemRight = itemRight.substring(1, itemRight.length());
               }
               if (!itemLeft.contains("{pl}") && !itemLeft.startsWith("+")) {
                  EElement e = new EElement();
                  LElement left = new LElement();
                  itemLeft = this.replacePoS(itemLeft);
                  left.addChild(new TextElement(itemLeft));
                  RElement right = new RElement();
                  itemRight = this.replacePoS(itemRight);
                  if (en.containsKey(itemRight)) {
                     right.addChild(new TextElement(itemRight));
                     PElement pE = new PElement();
                     pE.setLElement(left);
                     pE.setRElement(right);
                     e.addChild(pE);
                     eList.add(e);
                  }
               }
            }
         }
      }
      return eList;
   }

   /**
    * 
    * @param outFileName
    */
   public final void setOutFileName(final String outFileName) {
      this.outFileName = outFileName;
   }

   /**
    * 
    * @return The output file name
    */
   public final String getOutFileName() {
      return this.outFileName;
   }
}
