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
import java.util.ArrayList;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.Alphabet;
import dics.elements.dtd.Sdef;
import dics.elements.dtd.Sdefs;
import dics.elements.dtd.Pardefs;
import dics.elements.dtd.Section;
import dictools.AbstractDictTool;
import dictools.DicSort;
import dictools.utils.DictionaryReader;
import java.lang.Character;

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
    private ArrayList<Character> alpha;
    private boolean readfirst;
    
    /**
     *
     * @param fileName
     */
    public Speling (String fileName) {
        this.fileName = fileName;
        current = new SpelingParadigm();
        lemmata = new ArrayList<SpelingParadigm>();
        symbols = new ArrayList<String>();
        alpha = new ArrayList<Character>();
        readfirst = false;
    }

    public Speling (String fileName, String outName) {
        this.fileName = fileName;
        this.outFileName = outName;
        current = new SpelingParadigm();
        lemmata = new ArrayList<SpelingParadigm>();
        symbols = new ArrayList<String>();
        alpha = new ArrayList<Character>();
        readfirst = false;
    }

    /**
     * Add to the list of symbols
     * @param symbols
     */
    private void add_symbols (String symbols) {
        //System.err.println("Symbols: " + symbols);
        for (String s : symbols.split("\\.")) {
            if (!this.symbols.contains(s)) {
                this.symbols.add(s);
            }
        }
    }

    private Sdefs build_sdefs () {
        Sdefs sdefs = new Sdefs();
        for (String s : this.symbols) {
            Sdef sdef = new Sdef(s);
            sdefs.elements.add(sdef);
        }
        //System.err.println("sdefs: " + sdefs.toString());
        return sdefs;
    }

    private void collectAlpha (String s) {
        for (Character c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                if (!alpha.contains(c)) {
                    alpha.add(c);
                }
                if (Character.isLowerCase(c) && !alpha.contains(Character.toUpperCase(c))) {
                    alpha.add(Character.toUpperCase(c));
                } else if (!alpha.contains(Character.toLowerCase(c))) {
                    alpha.add(Character.toUpperCase(c));
                }
            } else {
                continue;
            }
        }
    }

    private Alphabet getAlpha () {
        String alphabet = "";
        for (Character c : alpha) {
            alphabet += c;
        }
        Alphabet a = new Alphabet(alphabet);
        return a;
    }

    private void proc_line(String line) {
        //System.err.println("proc_line: " + line);
        String[] input = line.split(";");
        String lemma = input[0].trim();
        String flexion = input[1].trim();
        String tags = input[2].trim();
        String pos = input[3].trim();
        //System.err.println(lemma + "/" + flexion + "/" + tags +"/" + pos);
        String full = pos + "." + tags;
        if (!lemma.equals("")) {
            collectAlpha(lemma);
        }
        if (!flexion.equals("")) {
            collectAlpha(flexion);
        }
        add_symbols(full);

        if (readfirst) {
            if (last_lemma.equals(lemma) && last_pos.equals(pos)) {
                if (last_tags.equals(tags)) {
                    //System.err.println ("Same tags as last: " + tags + "/" + last_tags);
                    current.entries.add(new SpelingEntry(flexion, full, true));
                    last_tags = tags;
                } else {
                    //System.err.println ("Same lemma/pos: " + lemma + "/" + pos);
                    current.entries.add(new SpelingEntry(flexion, full));
                    last_tags = tags;
                }
            } else {
                //System.err.println ("New lemma/pos: " + lemma + "/" + pos);
                //System.err.println ("lemmata.add() " + current.lemma);
                lemmata.add(current);
                current = new SpelingParadigm();
                current.lemma = lemma;
                current.pos = pos;
                current.entries.add(new SpelingEntry(flexion, full));
                last_lemma = lemma;
                last_pos = pos;
                last_tags = tags;
            }
        } else {
            // Reading first line
            //System.err.println ("First line: " + lemma + "/" + pos);
            readfirst = true;
            last_lemma = lemma;
            last_pos = pos;
            last_tags = tags;
            current.lemma = lemma;
            current.pos = pos;
            current.entries.add(new SpelingEntry(flexion, full));
        }

    }

    public Dictionary read_speling() {
        Dictionary dic = new Dictionary();
        dic.xmlEncoding = "UTF-8";
        Pardefs pardefs = new Pardefs();
        Section section = new Section("main", "standard");

        try {
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String strLine = "";


            while ((strLine = br.readLine()) != null) {
                if (!strLine.contains(";")) {
                    continue;
                }
                proc_line(strLine);
            }
            // Don't forget the last one!
            //System.err.println ("lemmata.add() " + current.lemma);
            lemmata.add(current);

            for (SpelingParadigm p : lemmata) {
                p.setStem();
                p.setSuffixes();
                pardefs.elements.add(p.toPardef());
                section.elements.add(p.toE());
                //System.err.println("entries: " + " - " + p.toE().toString());
            }
            dic.alphabet = getAlpha();
            dic.pardefs = pardefs;
            //System.err.println("pardefs: " + " - " + pardefs.toString());
            dic.sections.add(section);
            //System.err.println("section: " + " - " + section.toString());
            dic.sdefs = build_sdefs();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return dic;
    }

    public void write(Dictionary dic) {
        dic.printXMLToFile(outFileName, opt);
    }
}
