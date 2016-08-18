/*
 * MIT License
 *
 * Copyright (c) 2009-2011 Jim O'Regan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
import java.io.IOException;

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

    @Override
    public String toolHelp() {
        return "speling input.txt output.dix\n\n" +
                "Converts a file in speling format to a monolingual dictionary\n";
    }

    @Override
    public void executeTool() throws IOException {
        if (arguments.length != 3) failWrongNumberOfArguments(arguments);
        
        Dictionary dic = new Speling(arguments[1]).read_speling();
        dic.printXMLToFile(arguments[2], opt);
    }
    

    public Speling () {
        current = new SpelingParadigm();
        lemmata = new ArrayList<SpelingParadigm>();
        symbols = new ArrayList<String>();
        alpha = new ArrayList<Character>();
        readfirst = false;        
    }
    
    public Speling (String fileName) {
        this();
        this.fileName = fileName;
    }


    /**
     * Add to the list of symbols
     * @param symbols
     */
    private void add_symbols (String symbols) {
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
                    alpha.add(Character.toLowerCase(c));
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
        String[] input = line.split(";");
        String lemma = input[0].trim();
        String flexion = input[1].trim();
        String tags = input[2].trim();
        String pos = input[3].trim();
        String full = pos + "." + tags;
        System.out.println("Lemma: " + lemma);
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
                    current.entries.add(new SpelingEntry(flexion, full, true));
                    last_tags = tags;
                } else {
                    current.entries.add(new SpelingEntry(flexion, full));
                    last_tags = tags;
                }
            } else {
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
            lemmata.add(current);

            for (SpelingParadigm p : lemmata) {
                p.setStem();
                p.setSuffixes();
                pardefs.elements.add(p.toPardef());
                section.elements.add(p.toE());
            }
            dic.alphabet = getAlpha();
            dic.pardefs = pardefs;
            dic.sections.add(section);
            dic.sdefs = build_sdefs();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return dic;
    }
}
