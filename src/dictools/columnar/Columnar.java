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
import dics.elements.dtd.R;
import dics.elements.dtd.L;
import dics.elements.dtd.I;
import dics.elements.dtd.DixElement;
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

    boolean cliMode = true;

    public Columnar () {
        cliMode = true;
    }

    Columnar (String l, String r, String bil) {
        inLeft = l;
        inRight = r;
        inBil = bil;
        cliMode = false;
    }

    private void init (){
        if (cliMode) {
            setupArgs();
        }

        left = DicTools.readMonolingual(inLeft);
        right = DicTools.readMonolingual(inRight);
        bil = DicTools.readBilingual(inBil, false);

        leftElements = left.getEntriesInMainSection();
        rightElements = right.getEntriesInMainSection();
        bilElements = bil.getEntriesInMainSection();
    }

    /**
     * Gets the parts of the template that apply with the current entry's
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
     * Check if this is a simple p entry (contains only a single p element)
     * @param e The entry to check
     * @return True if only contains a single p, false otherwise
     */
    boolean isSimplePEntry(E e) {
        return (e.children.size() == 1 &&
                e.children.get(0) instanceof P);
    }

    /**
     * Check if this is a simple i entry (contains only a single i element)
     * @param e The entry to check
     * @return True if only contains a single i, false otherwise
     */
    boolean isSimpleIEntry(E e) {
        return (e.children.size() == 1 &&
                e.children.get(0) instanceof I);
    }

    /**
     * Get the symbols from a simple I entry
     * @param e The entry to extract from
     * @return An ArrayList of symbols
     */
    ArrayList<S> getISymbols(E e) {
        ArrayList<S> sym = new ArrayList<S>();
        if (e == null)
            return null;
        if (!isSimpleIEntry(e))
            return null;
        for (DixElement d : e.children) {
            I i = (I) d;
            for (Object o : i.children) {
                if (o instanceof S) {
                    S s = (S) o;
                    sym.add(s);
                }
            }
        }
        return sym;
    }
    /**
     * Get the symbols from the L part of a simple P entry
     * @param e The entry to extract from
     * @return An ArrayList of symbols
     */
    ArrayList<S> getLSymbols(E e) {
        ArrayList<S> sym = new ArrayList<S>();
        if (!isSimplePEntry(e))
            return null;
        for (DixElement d : e.getFirstPartAsL().getSymbols()) {
            if (d instanceof S) {
                sym.add((S) d);
            }
        }
        return sym;
    }
    /**
     * Get the symbols from the R part of a simple P entry
     * @param e The entry to extract from
     * @return An ArrayList of symbols
     */
    ArrayList<S> getRSymbols(E e) {
        ArrayList<S> sym = new ArrayList<S>();
        if (!isSimplePEntry(e))
            return null;
        for (DixElement d : e.getFirstPartAsR().getSymbols()) {
            if (d instanceof S) {
                sym.add((S) d);
            }
        }
        return sym;
    }

    /**
     * Populates the template with lemmas
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
            if (isSimpleIEntry(ent) && lemLeft.equals(lemRight)) {
                I outI = new I();
                outI.children.add(new TextElement(lemLeft));
                outI.children.addAll(getISymbols(ent));
                outE.children.add(outI);
            } else if (isSimplePEntry(ent)) {
                L outL = new L();
                outL.children.add(new TextElement(lemLeft));
                outL.children.addAll(getLSymbols(ent));
                R outR = new R();
                outR.children.add(new TextElement(lemRight));
                outR.children.addAll(getRSymbols(ent));
                P outP = new P();
                outP.l = outL;
                outP.r = outR;
                outE.children.add(outP);
            } else if (isSimpleIEntry(ent) && !lemLeft.equals(lemRight)) {
                L outL = new L();
                outL.children.add(new TextElement(lemLeft));
                outL.children.addAll(getISymbols(ent));
                R outR = new R();
                outR.children.add(new TextElement(lemRight));
                outR.children.addAll(getISymbols(ent));
                P outP = new P();
                outP.l = outL;
                outP.r = outR;
                outE.children.add(outP);
            } else {
                outE = null;
            }

            //null check
            if (outE != null) {
                bilEntries.add(outE);
            }
        }
        return bilEntries;
    }

    /**
     * Sets input dix names
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
     * Sets input TSV filename
     * @param in file to read
     */
    void setInput (String in) {
        this.input = in;
    }

    void setConfig (String cfg) {
        paraconfig = new ParaConfigReader(cfg);
        config = paraconfig.readParaConfig();
    }

    void setupArgs() {
        if (arguments.length == 5) {
            setConfig (arguments[0]);
            setInput (arguments[4]);
            setInFiles (arguments[1], arguments[2], arguments[3]);
        } else {
            setInput (arguments[3]);
            setInFiles (arguments[0], arguments[1], arguments[2]);
        }
    }

    /**
     * Gets the help string for command-line use
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
     * Processes each line of input, creating relevant entries
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
