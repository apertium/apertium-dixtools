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
package dictools.dix2trie;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.LElement;
import dics.elements.dtd.RElement;
import dics.elements.dtd.SectionElement;
import dics.elements.utils.SElementList;
import dictools.xml.DictionaryReader;
import dictools.dix2trie.utils.Entry;
import dictools.dix2trie.utils.EntryList;
import dictools.dix2trie.utils.Node;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Dix2Trie {

    /**
     * 
     */
    private Node root;
    /**
     * 
     */
    private EntryList entryList;
    /**
     * 
     */
    private String fileName;
    /**
     * 
     */
    private String dir;
    /**
     * 
     */
    private String outFileName;

    /**
     * 
     * @param fileName
     * @param dir
     */
    public Dix2Trie(String fileName, String dir) {
        this.fileName = fileName;
        this.dir = dir;
        entryList = new EntryList();
    }

    /**
     * 
     */
    public void buildTrie() {
        this.readDictionary(fileName, dir);
        this.processEntryList();
        this.printXML();
    }

    /**
     * 
     */
    private void processEntryList() {
        root = new Node();
        for (Entry entry : entryList) {
            this.addEntry(entry);
        }
    }

    /**
     * 
     * @param entry
     */
    private void addEntry(Entry entry) {
        int i = 1;
        String key = entry.getKey();
        char c = key.charAt(0);
        if (!key.equals("") && Character.isLetter(c)) {
            String ptr = key.substring(0, i);
            Node childNode = new Node(ptr);
            root.setValue("@root");
            root.addChildNode(childNode, entry, i + 1);
        }
    }

    /**
     * 
     * @param dicFileName
     * @param dir
     */
    private void readDictionary(String dicFileName, String dir) {
        DictionaryReader dicReader = new DictionaryReader(dicFileName);
        DictionaryElement dic = dicReader.readDic();

        for (SectionElement section : dic.getSections()) {
            for (EElement e : section.getEElements()) {
                LElement left = e.getLeft();
                RElement right = e.getRight();

                String lemmaLeft = left.getValueNoTags();
                String lemmaRight = right.getValueNoTags();

                SElementList keyList = left.getSElements();
                SElementList valueList = right.getSElements();

                if (!lemmaLeft.equals("") && !lemmaRight.equals("")) {
                    if (dir.equals("lr")) {
                        if (e.is_LR_or_LRRL()) {
                            Entry entry = new Entry(lemmaLeft, lemmaRight);
                            entry.setKeyAttr(keyList);
                            entry.setValueAttr(valueList);
                            this.entryList.add(entry);
                        }
                    }
                    if (dir.equals("rl")) {
                        if (e.is_RL_or_LRRL()) {
                            Entry entry = new Entry(lemmaRight, lemmaLeft);
                            entry.setKeyAttr(valueList);
                            entry.setValueAttr(keyList);
                            this.entryList.add(entry);
                        }
                    }
                }
            }
        }

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
     * @return The out file name
     */
    public String getOutFileName() {
        return this.outFileName;
    }

    /**
     * 
     */
    public void printXML() {
        this.printXML("UTF-8");
    }

    /**
     * 
     * @param encoding
     */
    public void printXML(String encoding) {
        BufferedOutputStream bos;
        FileOutputStream fos;
        OutputStreamWriter osw;
        int tabs = 0;

        try {
            if (this.getOutFileName() != null) {
                fos = new FileOutputStream(this.getOutFileName());
                bos = new BufferedOutputStream(fos);
            } else {
                bos = new BufferedOutputStream(System.out);
            }
            osw = new OutputStreamWriter(bos, encoding);
            root.print(tabs, osw);
            fos = null;
            bos = null;
            osw.close();
            osw = null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
