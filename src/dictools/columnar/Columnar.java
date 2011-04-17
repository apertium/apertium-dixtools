/*
 * Copyright 2011 European Commission
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package dictools.columnar;

/**
 *
 * @author jimregan
 */
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.P;
import dics.elements.dtd.L;
import dics.elements.dtd.R;
import dics.elements.dtd.I;
import dics.elements.dtd.S;
import dics.elements.dtd.Par;
import dics.elements.dtd.Section;
import dics.elements.dtd.TextElement;
import dictools.AbstractDictTool;
import dictools.utils.DicTools;
import dictools.utils.DicOpts;
import dictools.utils.DictionaryReader;
import dictools.columnar.ParaConfigReader;
import misc.termcat.guessers.Guesser;
import java.io.IOException;

public class Columnar extends AbstractDictTool {

    Dictionary left;
    Dictionary right;
    Dictionary bil;

    String outLeft;
    String outRight;
    String outBil;
    String inLeft;
    String inRight;
    String inBil;
    
    ArrayList<E> leftElements;
    ArrayList<E> rightElements;
    ArrayList<E> bilElements;

    ParaConfig config = null;
    ParaConfigReader paraconfig = null;

    String input;

    public Columnar(){
        setupArgs();

        left = DicTools.readMonolingual(inLeft);
        right = DicTools.readMonolingual(inRight);
        bil = DicTools.readBilingual(inBil, false);

        leftElements = left.getEntriesInMainSection();
        rightElements = right.getEntriesInMainSection();
        bilElements = bil.getEntriesInMainSection();
    }

    /**
     * Get the parts of the template that apply with the current entry's
     * restrictions - if the input has no restrictions, template restrictions
     * apply; if it does, each template entry without a restriction is set to
     * match the entry and added; template entries with a restriction that
     * doesn't match the input are omitted.
     * @param parleft Left pardef name
     * @param parright Right pardef name
     * @param lRestrict Entry restriction
     * @return List of entries to add to the bidix
     */
    ArrayList<E> getTemplate(String parleft, String parright, String lRestrict) {
        ArrayList<E> tpl = new ArrayList<E>();
        if (config == null) {
            tpl.add(genEmptyE(parleft, parright, lRestrict));
        } else {
            ArrayList<E> cur = config.get(parleft, parright).getEntries(lRestrict);
            if (cur == null) {
                tpl.add(genEmptyE(parleft, parright, lRestrict));
            } else {
                if ("".equals(lRestrict)) {
                    tpl.addAll(cur);
                } else {
                    for (E e : cur) {
                        if (lRestrict.equals(e.restriction)) {
                            tpl.add(e);
                        } else if (e.restriction == null || "".equals(e.restriction)) {
                            e.restriction = lRestrict;
                            tpl.add(e);
                        }
                    }
                }
            }
        }
        return tpl;
    }

    /**
     * Populate the template with lemmas
     * FIXME: only handles simple l/r entries
     * @param tpl Template array
     * @param lemLeft Left lemma
     * @param lemRight Right lemma
     * @return Populated entries
     */
    ArrayList<E> populateEntries(ArrayList<E> tpl, String lemLeft, String lemRight) {
        ArrayList<E> bilEntries = new ArrayList<E>();
        for (E ent : tpl) {
            E outE = new E();
            outE.restriction = ent.restriction;
            L outL = new L();
            outL.children.add(new TextElement(lemLeft));
            outL.children.addAll(ent.getFirstPartAsL().getSymbols());
            R outR = new R();
            outR.children.add(new TextElement(lemRight));
            outR.children.addAll(ent.getFirstPartAsR().getSymbols());
            P outP = new P();
            outP.l = outL;
            outP.r = outR;
            outE.children.add(outP);
            bilEntries.add(outE);
        }
        return bilEntries;
    }

    /**
     * Set input dix names
     * @param l Left monodix
     * @param r Right monodix
     * @param b Bidix
     */
    void setInFiles(String l, String r, String b) {
        inLeft = l;
        inRight = r;
        inBil = b;
    }

    /**
     * Set input TSV filename
     * @param in file to read
     */
    void setInput (String in) {
        this.input = in;
    }

    void setupArgs() {
        if (arguments.length == 5) {
            paraconfig = new ParaConfigReader(arguments[0]);
            config = paraconfig.readParaConfig();
            setInput (arguments[4]);
            setInFiles (arguments[1], arguments[2], arguments[3]);
        } else {
            setInput (arguments[3]);
            setInFiles (arguments[0], arguments[1], arguments[2]);
        }
    }

    /**
     * Get the help string for command-line use
     * @return Help string
     */
    @Override
    public String toolHelp() {
        return "columnar [config] mono1 mono2 bil input\n\n" +
               "Inserts entries from a tab-delimited text file\n";
    }

    public void doColumnar() {
        proc();

        left.printXMLToFile(outLeft, opt);
        right.printXMLToFile(outRight, opt);
        bil.printXMLToFile(outBil, opt);
    }

    /**
     * Wrapper around proc_line, for exception handling etc.
     */
    private void proc() {
        try {
            FileInputStream fstream = new FileInputStream(this.input);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String strLine = "";


            while ((strLine = br.readLine()) != null) {
                if (!strLine.contains("\\t")) {
                    continue;
                }
                proc_line(strLine);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Process each line of input, creating relevant entries
     * @param line Line of tab delimited text
     */
    private void proc_line(String line) {
        String[] in = line.split("\\t");
        String lemLeft = in[0].trim();
        String parleft = in[1].trim();
        String lemRight = in[2].trim();
        String parright = in[3].trim();
        String restrictSym = in[4].trim();
        String lRestrict = "";
        String rRestrict = "";

        E lEntry = new E();
        E rEntry = new E();
        E bilEntry = new E();

        P bilPair = new P();

        if ("<".equals(restrictSym)) {
            lRestrict = "RL";
            rRestrict = "LR";
        } else if (">".equals(restrictSym)) {
            lRestrict = "LR";
            rRestrict = "RL";
        }

        lEntry = genMonoE(lemLeft, parleft, lRestrict);
        rEntry = genMonoE(lemRight, parright, rRestrict);

        ArrayList<E> tpl = getTemplate(parleft, parright, lRestrict);
        ArrayList<E> bilEntries = populateEntries(tpl, lemLeft, lemRight);

        leftElements.add(lEntry);
        rightElements.add(rEntry);
        bilElements.addAll(bilEntries);
    }

    /**
     * Generates a monodix entry, stemming the lemma and adding the
     * direction restriction
     * @param lem Lemma
     * @param par Pardef
     * @param restrict Restriction
     * @return New monodix entry
     */
    private E genMonoE(String lem, String par, String restrict) {
        E e = new E();
        String stem = Guesser.stemFromPardef(lem, par);
        e.lemma = lem;
        I i = new I();
        i.setValue(stem);
        e.children.add(i);
        e.children.add(new Par(par));
        e.restriction = restrict;
        return e;
    }

    /**
     * Create an entry using sdefs from pardef names
     * @param parleft left pardef
     * @param parright right pardef
     * @param restrict LR/RL restriction
     * @return
     */
    private E genEmptyE(String parleft, String parright, String restrict) {
        E e = new E();
        String lsdef = parleft.split("__")[1];
        String rsdef = parright.split("__")[1];
            
        e.restriction = restrict;
        L l = new L();
        l.children.add(new S(lsdef));
        R r = new R();
        r.children.add(new S(rsdef));

        return e;
    }
}
