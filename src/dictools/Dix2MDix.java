/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Copyright (C) 2008 Enrique Benimeli Bofarull
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
package dictools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.E;
import dics.elements.dtd.S;
import dics.elements.dtd.Section;
import dics.elements.dtd.TextElement;
import dictools.xml.DictionaryReader;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Dix2MDix {

    
    private Dictionary dic;
    
    public String bilFileName;
    
    public String[] arguments;
    
    private String outFileName;
    
    private HashMap<String, Vector<E>> hm;
    
    public String sltlCode;
    
    public String sltlFull;
    
    private Vector<String> metaInf;
    
    private Vector<String> files;
    
    private TinyFilter tinyFilter;

    
    public Dix2MDix() {
        files = new Vector<String>();
    }

    public Dix2MDix(TinyFilter filter) {
        files = new Vector<String>();
        this.tinyFilter = filter;
    }

    
    public void do_convert() {
        this.processArguments();

        metaInf = new Vector<String>();
        files.add("meta.inf");


        String slCode = sltlCode.split("-")[0];
        String tlCode = sltlCode.split("-")[1];
        String slFull = sltlFull.split("-")[0];
        String tlFull = sltlFull.split("-")[1];

        metaInf.add("@sl-code:" + slCode + "$");
        metaInf.add("@tl-code:" + tlCode + "$");
        metaInf.add("@sl-full:" + slFull + "$");
        metaInf.add("@tl-full:" + tlFull + "$");

        System.err.println("Processing bilingual dictionary: " + dic.fileName);

        this.outFileName = "sltl";
        this.processDic(dic, "left to right");
        dic.reverse();
        this.outFileName = "tlsl";
        this.processDic(dic, "right to left");

        this.printMetaInfFile(metaInf);

        String zipFileName = sltlCode + "-data.zip";
        System.err.println("Building " + zipFileName + " for apertium-tinylex...");
        ZipIt zipIt = new ZipIt(files, zipFileName);
        zipIt.zip();
        System.err.println("Done!");

    }

    /**
     * 
     * @param dic
     */
    private void processDic(Dictionary dic, String dir) {
        hm = new HashMap<String, Vector<E>>();
        Vector<E> values = null;

        for (Section section : dic.sections) {
            for (E ee : section.elements) {
                if (ee.is_LR_or_LRRL() && !ee.containsRegexpr()) {
                    String left = ee.getValueNoTags("L");
                    left = tinyFilter.applyToLemma(left);
                    left = left.toLowerCase();

                    if (left.length() > 1) {
                        if (Character.isLetter(left.charAt(0))) {
                            if (hm.containsKey(left)) {
                                values = (Vector<E>) hm.get(left);
                                values.add(ee);
                                hm.put(left, values);
                            } else {
                                values = new Vector<E>();
                                values.add(ee);
                                hm.put(left, values);
                            }
                        }
                    }
                }
            }
        }

        System.err.println(dir + ": " + hm.size() + " lemmas.");
        Vector<Entry> vector = this.map2vector(hm);
        Collections.sort(vector);
        this.print(vector);
    }

    /**
     * 
     * @param vector
     */
    private void print(Vector<Entry> vector) {
        try {
            BufferedOutputStream bos;
            FileOutputStream fos;
            OutputStreamWriter dos = null;

            int fileNumber = 1;
            String fileName = this.outFileName + "_" + fileNumber + ".dix";
            files.add(fileName);

            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, "UTF-8");

            int n = 0;
            int block = 350;
            Vector<String> fileInfo = new Vector<String>();
            String content = "";
            String first = null;
            String last = null;

            Vector<Entry> partial = new Vector<Entry>();
            for (Entry e : vector) {
                if (e.key.length() == 0) {
                    continue;
                }
                if (n < block) {
                    if (first == null) {
                        first = e.key;
                    }
                    last = e.key;
                    n++;
                    partial.add(e);
                } else {

                    content = first + "." + last + ".";
                    fileInfo.add(new String("@" + fileName + ":" + content + "$"));
                    dos.append("@size:" + n + "$\n");
                    for (Entry pe : partial) {
                        dos.append(pe.value);
                    }
                    dos.flush();
                    dos.close();
                    bos.close();
                    fos.close();

                    partial = new Vector<Entry>();
                    fileNumber++;
                    fileName = this.outFileName + "_" + fileNumber + ".dix";
                    files.add(fileName);
                    fos = new FileOutputStream(fileName);
                    bos = new BufferedOutputStream(fos);
                    dos = new OutputStreamWriter(bos, "UTF-8");
                    first = last = null;
                    n = 0;
                }
            }
            dos.append("@size:" + n + "$\n");
            for (Entry pe : partial) {
                dos.append(pe.value);
            }
            dos.flush();
            dos.close();
            bos.close();
            fos.close();


            content = first + "." + last + ".";
            fileInfo.add(new String("@" + fileName + ":" + content + "$"));
            metaInf.add("@" + this.outFileName + "_n:" + fileInfo.size() + "$");
            for (String c : fileInfo) {
                metaInf.add(c);
            }

        } catch (IOException ioe) {
            System.err.println("Error writing files.");
        }


    }

    /**
     * 
     * @param metaInf
     */
    private void printMetaInfFile(Vector<String> metaInf) {
        BufferedOutputStream bos;
        FileOutputStream fos;
        OutputStreamWriter dos = null;

        String fileName = "meta.inf";
        try {
            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            dos = new OutputStreamWriter(bos, "UTF-8");
            for (String c : metaInf) {
                dos.append(c + "\n");
            }
            dos.close();
            bos.close();
            fos.close();
        } catch (IOException ioe) {
        }
    }

    
    private void processArguments() {
        if (arguments != null) {
            String fileName = this.arguments[1];
            this.bilFileName = fileName;
            //dic.reverse();
            this.sltlCode = this.arguments[2];
            this.sltlFull = this.arguments[3];
        }
        DictionaryReader dicReader = new DictionaryReader(this.bilFileName);
        dic = dicReader.readDic();
        if (this.tinyFilter != null) {
            dic = this.tinyFilter.doFilter(dic);
        }
        dic.fileName = this.bilFileName;
    }

    /**
     * 
     * @param hm
     * @return
     */
    private Vector<Entry> map2vector(HashMap<String, Vector<E>> hm) {
        Vector<Entry> vector = new Vector<Entry>();

        Set<String> keySet = hm.keySet();
        Iterator<String> it = keySet.iterator();

        while (it.hasNext()) {
            Entry entry = new Entry();
            String key = (String) it.next();
            entry.key = key;
            Vector<E> v = (Vector<E>) hm.get(key);
            String value = "";
            value = value + "[" + key + "]";
            for (E ee : v) {
                String slLemma = ee.getValueNoTags("L");
                value = value + slLemma + ":";
                for (S sE : ee.getSymbols("L")) {
                    if (this.tinyFilter.preserve(sE.getValue())) {
                        String tagName = this.tinyFilter.rename(sE.getValue());
                        value = value + tagName + ".";
                    }
                }
                value = value + "?";
                String tlLemma = ee.getValueNoTags("R");
                value = value + tlLemma + ":";
                for (S sE : ee.getSymbols("R")) {
                    if (this.tinyFilter.preserve(sE.getValue())) {
                        String tagName = this.tinyFilter.rename(sE.getValue());
                        value = value + tagName + ".";
                    }
                }
                value = value + ";";
            }
            value = value + "$\n";
            entry.value = value;
            vector.add(entry);
        }
        return vector;
    }

    private class Entry implements Comparable<Entry> {

        
    	public String key;
        
    	public String value;

        

        public int compareTo(Entry anotherEntry) {
            String lemma2 = anotherEntry.key;
            String lemma1 = this.key;

            if (lemma1 == null || lemma2 == null) {
                return 0;
            } else {
                if (lemma1.compareTo(lemma2) == 0) {
                    return 0;
                }
                if (lemma1.compareTo(lemma2) < 0) {
                    return -1;
                }
            }
            return 1;

        }
    }

    
    public class ZipIt {

        private String zipFileName;
        private Vector<String> files;

        public ZipIt(Vector<String> files, String zipFileName) {
            this.files = files;
            this.zipFileName = zipFileName;
        }

        public void zip() {
            try {

                File zipFile = new File(this.zipFileName);
                if (zipFile.exists()) {
                    this.deleteFile(this.zipFileName);

                }
                FileOutputStream fos = new FileOutputStream(zipFile);
                ZipOutputStream zos = new ZipOutputStream(fos);
                int bytesRead;
                byte[] buffer = new byte[1024];
                CRC32 crc = new CRC32();
                for (int i = 0, n = files.size(); i < n; i++) {
                    String name = files.elementAt(i);
                    //System.err.println("adding " + name);
                    File file = new File(name);
                    if (!file.exists()) {
                        System.err.println("Skipping: " + name);
                        continue;
                    }
                    BufferedInputStream bis = new BufferedInputStream(
                            new FileInputStream(file));
                    crc.reset();
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        crc.update(buffer, 0, bytesRead);
                    }
                    bis.close();
                    // Reset to beginning of input stream
                    bis = new BufferedInputStream(
                            new FileInputStream(file));
                    ZipEntry entry = new ZipEntry(name);
                    entry.setMethod(ZipEntry.STORED);
                    entry.setCompressedSize(file.length());
                    entry.setSize(file.length());
                    entry.setCrc(crc.getValue());
                    zos.putNextEntry(entry);
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }
                    bis.close();
                }
                zos.close();
            } catch (IOException ioe) {
            } finally {
                this.clean();
            }
        }

        private void clean() {
            for (int i = 0, n = files.size(); i < n; i++) {
                String name = files.elementAt(i);
                this.deleteFile(name);
            }
        }

        private void deleteFile(String fileName) {
            File f = new File(fileName);

            if (!f.exists()) {
                throw new IllegalArgumentException(
                        "Delete: no such file or directory: " + fileName);
            }

            if (!f.canWrite()) {
                throw new IllegalArgumentException("Delete: write protected: " + fileName);
            }

            if (f.isDirectory()) {
                String[] theFiles = f.list();
                if (theFiles.length > 0) {
                    throw new IllegalArgumentException(
                            "Delete: directory not empty: " + fileName);
                }
            }

            boolean success = f.delete();

            if (!success) {
                throw new IllegalArgumentException("Delete: deletion failed");
            }

        }
    }
}

