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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.E;
import dics.elements.dtd.L;
import dics.elements.dtd.P;
import dics.elements.dtd.R;
import dics.elements.dtd.S;
import dics.elements.dtd.Section;
import dics.elements.dtd.TextElement;
import dictools.AbstractDictTool;
import dictools.DicSort;
import dictools.utils.DictionaryReader;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Apertiumizer extends AbstractDictTool {

    
    private String fileName;
    
    private String outFileName;
    /**
     * 
     * @param fileName
     */
    public Apertiumizer(String fileName) {
        this.fileName = fileName;
    }

    
    public void apertiumize() {
        /*
        DictionaryReader encaReader = new DictionaryReader("apertium-en-ca.en-ca.dix");
        Dictionary enca = encaReader.readDic();
        en = new HashMap<String, String>();
        for (E e : enca.getEntriesInMainSection()) {
        L l = e.getFirstPartAsL();
        String lv = l.getValueNoTags();
        en.put(lv, lv);
        }
        this.readFormat(2);
         */
        this.readFormat(2);
    }

    /**
     * 
     * @param format
     */
    private void readFormat(int format) {
        try {
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String strLine;

            Dictionary dic = new Dictionary();
            dic.xmlEncoding = "UTF-8";
            Section section = new Section("main", "standard");
            dic.sections.add(section);

            int priority = 1;
            int c = 1;
            while ((strLine = br.readLine()) != null) {
                //if (!strLine.startsWith("#") && Character.isLetter(strLine.charAt(0))) {
                if (!strLine.startsWith("#")) {
                    switch (format) {
                        case 0:
                            E e = readElementFormat_0(strLine);
                            //e.comment="priority: " + priority);
                            //System.out.println("Adding: "  + e.getFirstPartAsL().getValueNoTags());
                            if (e != null) {
                                section.elements.add(e);
                            }
                            String lm = e.getFirstPartAsL().getValueNoTags();
                            String comments = e.comment;
                            System.out.println("<e lm=\"" + lm + "\" c=\"" + comments + "\"><i>" + lm + "</i><par n=\"ADN__n\"/></e>");
                            break;
                        case 3:
                            E e3 = readElementFormat_3(strLine);
                            if (e3 != null) {
                                section.elements.add(e3);
                            }
                            break;
                        case 2:
                            ArrayList<E> eList = readElementFormat_2(strLine);
                            for (E e1 : eList) {
                                section.elements.add(e1);
                            }
                            break;
                    }
                    if (c % 150 == 0) {
                        priority++;
                    }
                    c++;
                }
            }
            in.close();

            dic.printXML(this.getOutFileName(), "UTF-8",opt);
        DictionaryReader reader = new DictionaryReader("dic.tmp");
        Dictionary bil = reader.readDic();
        this.completeDic(bil);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }

    
    private String removeForms(String strLine) {
        StringBuffer strBuffer = new StringBuffer(strLine);
        StringBuffer newStr = new StringBuffer();
        int lStr = strBuffer.length();
        boolean isIn = false;
        for(int i=0; i<lStr; i++) {
            char c = strBuffer.charAt(i);
            if(c == '{') {
                isIn = true;
            }
            if(!isIn) {
                newStr.append(c);
            }

            if(c == '}') {
                isIn = false;
            }
    }
     return newStr.toString();   
    }
    private void completeDic(Dictionary bil) {

        for (E ee : bil.getEntriesInMainSection()) {
            //System.out.println("completing... " + ee.getFirstPartAsL().getValueNoTags());
            L l = ee.getFirstPartAsL();
            R r = ee.getFirstPartAsR();
            String cat = "";
            int icat = 0;
            for (DixElement e : l.children) {
                if (e instanceof TextElement) {
                    TextElement tE = (TextElement) e;
                    String v = tE.text;
                    //System.out.println(v);
                    v = v.replaceAll("[\\s]", "<b/>");
                    v = v.replaceAll("\\&", "\\&amp;");
                    tE.text = v;
                }
                if (e instanceof S) {
                    S sE = (S) e;
                    String v = sE.getValue();

                    if (icat == 0) {
                        cat = v;
                    }
                    icat++;
                }
            }
            boolean isVerb = false;
            for (DixElement e : r.children) {
                if (e instanceof TextElement) {
                    TextElement tE = (TextElement) e;
                    String v = tE.text;
                    v = this.removeForms(v);
                    v = v.replaceAll("[ ]", "<b/>");
                    v = v.replaceAll("\\&", "\\&amp;");

                    if(v.startsWith("to<b/>")) {
                        System.out.println("Verb: " + v);
                        if(!ee.getFirstPartAsL().containsSymbol("vblex")) {
                        ee.getFirstPartAsL().children.add(new S("vblex"));
                        }
                        isVerb = true;
                        v = v.replaceAll("to<b/>", "");
                        
                        System.out.println("NVerb: " + v);
                        
                    }
                    tE.text = v;
                    //System.out.println(v);
                    //tE.setValue(v);
                }
            }
            S sE = new S(cat);
            if (!sE.getValue().equals("")) {
            r.children.add(sE);
            }
            if(isVerb) {
                if(!ee.getFirstPartAsR().containsSymbol("vblex")) {
                ee.getFirstPartAsR().children.add(new S("vblex"));
                }
            }

        }

        
        Dictionary bilFil = new Dictionary();
        Section sectionFil = new Section();
        bilFil.sections.add(sectionFil);
                for (Section sec : bil.sections) {
            for (E ee : sec.elements) {
                ///if( ee.containsSymbol("f") || ee.containsSymbol("pl")) {
                    
                //} else {
                    sectionFil.elements.add(ee);
                //}
                
            }
                }

        
        DicSort dicSort = new DicSort(bilFil);
        Dictionary sorted = dicSort.sort();

        String prevCat = "";
        Section sectionElement = null;
        Section noneSection = new Section("none", "standard");
        for (Section sec : sorted.sections) {
            for (E ee : sec.elements) {

                ArrayList<S> slist = ee.getFirstPartAsL().getSymbols();

                if (slist.size() > 0) {
                    S sE = slist.get(0);
                    String currentCat = sE.getValue();
                    if (currentCat.equals(prevCat)) {
                        sectionElement.elements.add(ee);
                    } else {
                        if (sectionElement != null) {
                            sectionElement.printXMLXInclude(prevCat, "UTF-8",opt);
                        }
                        prevCat = currentCat;
                        sectionElement = new Section();
                        sectionElement.id = currentCat;
                        sectionElement.type = "standard";
                    }
                } else {
                    noneSection.elements.add(ee);
                }

            }
        }
        if (sectionElement != null) {
            sectionElement.printXMLXInclude(prevCat, "UTF-8",opt);
        }
        noneSection.printXMLXInclude("none.xml", "UTF-8",opt);

        sorted.printXML(this.getOutFileName(), "UTF-8",opt);

    }

    /**
     * 
     * @param strLine
     * @return The element
     */
    private E readElementFormat_0(String strLine) {
        StringTokenizer tokenizer = new StringTokenizer(strLine, ":");
        boolean lastToken = false;
        int i = 0;
        E e = new E();
        L left = new L();
        R right = new R();

        String lV = "";
        while (i < 2 && !lastToken && tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            switch (i) {
                case 0:
                    //lV = token + "<s n=\"n\"/><s n=\"acr\"/>";
                    lV = token;
				left.children.add(new TextElement(lV));
                    //System.out.println(lV);
                    break;
                case 1:
                    if (token.equals("?")) {
                        right.children.add(new TextElement(""));

                    } else {
                        right.children.add(new TextElement(lV));
                    }
                    token = token.replaceAll("\"", "'");
                    e.comment=token;
                    //System.out.println(token);
                    P pE = new P();
                    pE.l = left;
                    pE.r = (right);
				e.children.add(pE);
                    return e;
                case 2:
                    lastToken = true;
                /*
                S sE = new S(token);
                left.addChild(sE);
                right.addChild(sE);
                 */

            }
            i++;
        }
        return null;
    }

    private E readElementFormat_3(String strLine) {
        StringTokenizer tokenizer = new StringTokenizer(strLine, "\t");

        boolean lastToken = false;
        int i = 0;
        E e = new E();
        L left = new L();
        R right = new R();

        while (i < 3 && !lastToken && tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            switch (i) {
                case 0:
				left.children.add(new TextElement(token));
                    break;
                case 1:
                    lastToken = true;
                    if (!token.equals("")) {
                        right.children.add(new TextElement(token));
                        P pE = new P();
                        pE.l = left;
                        pE.r = (right);
                        e.children.add(pE);
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
    private E readElementFormat_1(String strLine) {
        StringTokenizer tokenizer = new StringTokenizer(strLine, "\t");
        boolean lastToken = false;
        int i = 0;
        E e = new E();
        L left = new L();
        R right = new R();

        while (i < 3 && !lastToken && tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            switch (i) {
                case 0:
				left.children.add(new TextElement(token));
                    break;
                case 1:
				right.children.add(new TextElement(token));
                    break;
                case 2:
                    if (token.endsWith(":")) {
                        lastToken = true;
                        String newString = token.substring(0, token.length() - 1);
                        S sE = new S(newString);
                        left.children.add(sE);
                        right.children.add(sE);
                        P pE = new P();
                        pE.l = left;
                        pE.r = (right);
                        e.children.add(pE);
                        return e;
                    }
            }
            i++;
        }
        return null;
    }

    private String replacePoS(String str) {
        //str = str.replaceAll("\"", "\\&quot;");
        str = str.replaceAll("\'", "");
        str = str.replaceAll("\"", "");
        str = str.replaceAll("\\<", "(");
        str = str.replaceAll("\\>", ")");
        str = str.replaceAll("\\{vi\\}", "<s n=\"vblex\"/>");
        str = str.replaceAll("\\{m\\}", "<s n=\"n\"/><s n=\"m\"/>");
        str = str.replaceAll("\\{f\\}", "<s n=\"n\"/><s n=\"f\"/>");
        str = str.replaceAll("\\{n\\}", "<s n=\"n\"/><s n=\"nt\"/>");
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
    private ArrayList<E> readElementFormat_2(String strLine) {
        StringBuffer strBuffer = new StringBuffer(strLine);
        int lStr = strBuffer.length();
        boolean isIn = false;
        for(int i=0; i<lStr; i++) {
            
            char c = strBuffer.charAt(i);
            
            if(c == '{') {
                isIn = true;
            }
            if( c== ';' && isIn) {
                strBuffer.replace(i, i+1, "$");
           }
            if(c == '}') {
                isIn = false;
            }
    }
        strLine = strBuffer.toString();
        System.out.print(strLine);
        System.out.println();
        
        Pattern p = Pattern.compile("\\([^\\(]+\\)");
        Matcher m = p.matcher(strLine);
        strLine = m.replaceAll("");

        strLine = strLine.replaceAll("[\\s]+\\{", "\\{");
        strLine = strLine.replaceAll("\\} ", "\\}");
        strLine = strLine.replaceAll(" ::", "::");
        strLine = strLine.replaceAll(":: ", "::");
        strLine = strLine.replaceAll("; ", ";");
        strLine = strLine.replaceAll(" \\| ", "\\|");
        strLine = strLine.replaceAll("[\\s]+\\[", "\\[");
        strLine = strLine.replaceAll("\\] ", "\\]");


        strLine = strLine.replaceAll("\\+", "::");

        //System.out.println(strLine);

        ArrayList<E> eList = new ArrayList<E>();

        StringTokenizer tokenizer = new StringTokenizer(strLine, "::");

        String leftStr = tokenizer.nextToken();
        //System.out.println("Left: " + leftStr);
        if (!tokenizer.hasMoreTokens()) {
            return eList;
        }
        String rightStr = tokenizer.nextToken();
        //System.out.println("Right: " + rightStr);

        StringTokenizer leftTokenizer = new StringTokenizer(leftStr, "|");
        ArrayList<String> leftElements = new ArrayList<String>();
        int ii = 0;
        while (leftTokenizer.hasMoreTokens()) {
            String leftE = leftTokenizer.nextToken();
            //System.out.println("\tleft[" + ii + "] = " + leftE);
            ii++;
            leftElements.add(leftE);
        }

        StringTokenizer rightTokenizer = new StringTokenizer(rightStr, "|");
        ArrayList<String> rightElements = new ArrayList<String>();
        int jj = 0;
        while (rightTokenizer.hasMoreTokens()) {
            String rightE = rightTokenizer.nextToken();
            //System.out.println("\tright[" + jj + "] = " + rightE);
            jj++;
            rightElements.add(rightE);
        }


        if (leftElements.size() == 0 || rightElements.size() == 0) {
            return eList;
        }


        for (int i = 0; i < leftElements.size(); i++) {
            String r = null;
            String l = leftElements.get(i);
            if (i < rightElements.size()) {
                r = rightElements.get(i);
            }
            if (r == null) {
                return eList;
            }
            //System.out.println("<" + l + "> / <" + r + ">");

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

            int ls = left2Elements.size();
            int rs = right2Elements.size();
            int max = -1;
            if (ls > rs) {
                max = ls;
            } else {
                max = rs;
            }

            String itemLeft = null;
            int li = 0;
            int ri = 0;
            String itemRight = null;
            for (int j = 0; j < max; j++) {
                if (j < left2Elements.size()) {
                    itemLeft = left2Elements.get(li);
                    li++;
                }

                if (itemLeft.charAt(0) == ' ') {
                    itemLeft = itemLeft.substring(1, itemLeft.length());
                }

                if (j < right2Elements.size()) {
                    itemRight = right2Elements.get(ri);
                    ri++;
                }

                //System.out.println("align: " + "<" + itemLeft + "> - <" + itemRight + ">");
                if (itemRight.charAt(0) == ' ') {
                    itemRight = itemRight.substring(1, itemRight.length());
                }
                if (!itemLeft.contains("{pl}") && !itemLeft.startsWith("+")) {
                    if (itemLeft != null && itemRight != null) {
                        E e = new E();
                        L left = new L();
                        left.children.add(new TextElement(replacePoS(new String(itemLeft))));
                        R right = new R();
                        right.children.add(new TextElement(replacePoS(new String(itemRight))));
                        P pE = new P();
                        pE.l = left;
                        pE.r = (right);
                        e.children.add(pE);
                        eList.add(e);
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
    public void setOutFileName(String outFileName) {
        this.outFileName = outFileName;
    }

    /**
     * 
     * @return The output file name
     */
    public String getOutFileName() {
        return this.outFileName;
    }
}
