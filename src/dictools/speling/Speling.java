/*
 * Author: Jimmy O'Regan
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

package dictools.speling;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.Sdef;
import dics.elements.dtd.Sdefs;
import dics.elements.dtd.Pardefs;
import dics.elements.dtd.Section;
import dics.elements.dtd.TextElement;
import dictools.AbstractDictTool;
import dictools.DicSort;
import dictools.utils.DictionaryReader;


/**
 *
 * @author jimregan
 */

public class Speling extends AbstractDictTool {


    private String fileName;

    private String outFileName;

    private String last_lemma = "";
    private String last_pos = "";
    private String last_tags = "";

    private SpelingParadigm current;
    private ArrayList<SpelingParadigm> lemmata;

    private ArrayList<String> symbols;
    
    /**
     *
     * @param fileName
     */
    public Speling (String fileName) {
        this.fileName = fileName;
        current = new SpelingParadigm();
        lemmata = new ArrayList<SpelingParadigm>();
        symbols = new ArrayList<String>();
    }

    public Speling (String fileName, String outName) {
        this.fileName = fileName;
        this.outFileName = outName;
        current = new SpelingParadigm();
        lemmata = new ArrayList<SpelingParadigm>();
        symbols = new ArrayList<String>();
    }

    /**
     * Add to the list of symbols
     * @param symbols
     */
    private void add_symbols (String symbols) {
        String[] symbollist = symbols.split(".");
        for (String s : symbollist) {
            if (!this.symbols.contains(s))
                this.symbols.add(s);
        }
    }

    private Sdefs build_sdefs () {
        Sdefs sdefs = new Sdefs();
        for (String s : this.symbols) {
            Sdef sdef = new Sdef(s);
            sdefs.elements.add(sdef);
        }
        return sdefs;
    }

    private void proc_line(String line) {
        //System.err.println("proc_line: " + line);
        boolean readfirst = false;
        String[] input = line.split(";");
        String lemma = input[0].trim();
        String flexion = input[1].trim();
        String tags = input[2].trim();
        String pos = input[3].trim();
        String full = pos + "." + tags;
        add_symbols(full);

        if (readfirst) {
            if (last_lemma.equals(lemma) && last_pos.equals(pos)) {
                if (last_tags.equals(tags)) {
                    current.entries.add(new SpelingEntry(flexion, full, true));
                } else {
                    current.entries.add(new SpelingEntry(flexion, full));
                }
            } else {
                if (!current.lemma.equals("")) {
                    lemmata.add(current);
                }
                current.purge();
                current.lemma = lemma;
                current.pos = pos;
                current.entries.add(new SpelingEntry(flexion, full));
                last_lemma = lemma;
                last_pos = pos;
            }
        } else {
            readfirst = true;
            last_lemma = lemma;
            last_pos = pos;
        }

    }

    public void read_speling() {
        try {
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String strLine = "";

            Dictionary dic = new Dictionary();
            dic.xmlEncoding = "UTF-8";
            dic.sdefs = new Sdefs();
            Pardefs pardefs = new Pardefs();
            dic.pardefs = pardefs;
            Section section = new Section("main", "standard");
            dic.sections.add(section);

            while ((strLine = br.readLine()) != null) {
                System.err.println("Calling proc_line");
                if (!strLine.contains(";")) {
                    continue;
                }
                proc_line(strLine);
            }
            // Don't forget the last one!
            lemmata.add(current);

            for (SpelingParadigm p : lemmata) {
                System.err.println(p.lemma + " - " + p.pos);
                dic.pardefs.elements.add(p.toPardef());
                dic.sections.get(0).elements.add(p.toE());
            }
            dic.sdefs = build_sdefs();
            dic.printXML(outFileName, opt);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}