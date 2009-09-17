/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Author: Enrique Benimeli Bofarull
 *
 * This program firstSymbolIs free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program firstSymbolIs distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received author copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package dictools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JProgressBar;

import lingres.LingResources;
import lingres.LingResourcesReader;
import lingres.Resource;
import dics.elements.dtd.Alphabet;
import dics.elements.dtd.B;
import dics.elements.dtd.ContentElement;
import dics.elements.dtd.Dictionary;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.E;
import dics.elements.dtd.HeaderElement;
import dics.elements.dtd.L;
import dics.elements.dtd.P;
import dics.elements.dtd.R;
import dics.elements.dtd.S;
import dics.elements.dtd.Sdef;
import dics.elements.dtd.Sdefs;
import dics.elements.dtd.Section;
import dics.elements.dtd.TextElement;
import dics.elements.utils.DicOpts;
import dics.elements.utils.DicSet;
import dics.elements.utils.DicTools;
import dictools.cmproc.CrossActionData;
import dictools.cmproc.CrossModelProcessor;
import dictools.cmproc.Variables;
import dictools.crossmodel.Action;
import dictools.crossmodel.CrossAction;
import dictools.crossmodel.CrossModel;
import dictools.crossmodel.CrossModelReader;
import dictools.crossmodel.Pattern;
import dictools.xml.DictionaryReader;

/**
 *
 * @author Enrique Benimeli Bofarull
 *
 */
public class DicCross  extends AbstractDictTool{

    /**
     * Restrictions for crossing
     */
    private int[][] rMatrix;
    /**
     * Left-Right restriction
     */
    private final int LR = 0;
    /**
     * Right-Left restriction
     */
    private final int RL = 1;
    /**
     * Left-Right and Right-Left restrictions.
     */
    private final int BOTH = 2;
    
    private final int NONE = 3;
    /**
     * Bilingual dictionary A-B
     */
    private Dictionary bilAB;
    /**
     * Bilingual dictionary B-C
     */
    private Dictionary bilBC;
    
    private HashMap<String, E> processed;
    
    private HashMap<String, E> regExProcessed;
    
    private CrossModel crossModel;
    
    private CrossModelProcessor crossModelProcessor;
    
    private String crossModelFileName;
    
    private HashMap<String, CrossAction> nDCrossActions;
    
    private CrossModel nDCrossModel;
    
    private DicSet dicSet;
    
    private int NDcounter;
    
    private HashMap<String, Integer> usedPatterns;
    
    private int taskOrder;
    
    private String outDir = "dix/";
    
    private int completed = 0;
    
    private double nMinElements = 0;
    
    private double nCrossedElements = 0;
    
    private JProgressBar progressBar;
    
    private String bilCrossed_path;
    
    private String monACrossed_path;
    
    private String monCCrossed_path;

    
    public DicCross() {
        msg.setLogFileName("cross.log");
        rMatrix = new int[3][3];
        fillOutRestrictionMatrix();
        processed = new HashMap<String, E>();
        regExProcessed = new HashMap<String, E>();
        nDCrossActions = new HashMap<String, CrossAction>();
        nDCrossModel = new CrossModel();
        usedPatterns = new HashMap<String, Integer>();
        taskOrder = 1;
    }

    
    private void readCrossModel() {
        try {
            msg.out("[" + (taskOrder++) + "] Reading cross model (" + crossModelFileName + ") ...\n");
            CrossModelReader cmr = new CrossModelReader(crossModelFileName);
            if (cmr == null) {
                System.err.println("cmr es null");
            }
            CrossModel cm = cmr.readCrossModel();
            msg.out("[" + (taskOrder++) + "] Validating cross model (" + crossModelFileName + ") ... ");
            if (!cm.isValid()) {
                msg.err("\nCross model is not valid!");
                System.exit(-1);
                ;
            } else {
                msg.out(" [OK]\n");
            }
            if (cm != null) {
                cm.rename();
                //cm.printXML("cm-renamed.xml");
                this.crossModel = cm;
                msg.out("[" + (taskOrder++) + "] Processing patterns in cross model (" + crossModelFileName + ") ... ");
                msg.out(" (" + cm.getCrossActions().size() + " patterns processed).\n");
                CrossModelProcessor cmp = new CrossModelProcessor(this.crossModel, msg);
                this.crossModelProcessor = cmp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.err("Error reading cross model");
            System.exit(-1);
        }
    }

    
    private void fillOutRestrictionMatrix() {
        // Note: B-A ^ B-C = A-C
        rMatrix[LR][LR] = NONE;
        rMatrix[LR][RL] = RL;
        rMatrix[LR][BOTH] = RL;

        rMatrix[RL][LR] = LR;
        rMatrix[RL][RL] = NONE;
        rMatrix[RL][BOTH] = LR;

        rMatrix[BOTH][LR] = LR;
        rMatrix[BOTH][RL] = RL;
        rMatrix[BOTH][BOTH] = BOTH;
    }

    /**
     * 
     * @param dicSet
     * @return Undefined
     */
    public Dictionary[] crossDictionaries(DicSet dicSet) {
        Dictionary[] dics = new Dictionary[2];
        readCrossModel();

        Dictionary dic1 = dicSet.bil1;
        Dictionary dic2 = dicSet.bil2;

        int nDic1 = dic1.getEntriesInMainSection().size();
        int nDic2 = dic2.getEntriesInMainSection().size();

        this.nMinElements = nDic1 + nDic2;

        this.bilAB = dic1;
        this.bilBC = dic2;

        Dictionary dic = new Dictionary();

        // encoding
        String encoding = "UTF-8";
        dic.xmlEncoding = encoding;

        // alphabet
        Alphabet alphabet = crossAlphabets(dic1.alphabet, dic2.alphabet);
        dic.alphabet = alphabet;

        // sdefs
        Sdefs sdefs = crossSdefs(dic1.sdefs, dic2.sdefs);
        dic.sdefs = sdefs;

        // sections
        for (Section section1 : dic1.sections) {
            Section section2 = dic2.getSection(section1.id);
            Section[] sections = crossSections(section1, section2, sdefs);
            Section section = sections[0];
            dic.sections.add(section);
        }

        msg.out("[" + (taskOrder++) + "] Sorting crossed dictionary...\n");
        Collections.sort(dic.getEntriesInMainSection(), E.eElementComparatorL);

        nDCrossModel.printXML(this.outDir + "patterns-not-detected.xml",opt);
        dics[0] = dic;
        return dics;
    }

    /**
     *
     * @param alphabet1
     * @param alphabet2
     * @return Undefined     
     */
    private Alphabet crossAlphabets(Alphabet alphabet1, Alphabet alphabet2) {
        Alphabet alphabet = new Alphabet();
        if (alphabet2 != null && alphabet2 != null) {


            String a1 = alphabet1.alphabet;
            String a2 = alphabet2.alphabet;

            HashMap<Object, Object> a3 = new HashMap<Object, Object>();

            int i = 0;
            for (i = 0; i < a1.length(); i++) {
                char c = a1.charAt(i);
                if (!a3.containsKey(c)) {
                    a3.put(c, null);
                }
            }

            int j = 0;
            for (j = 0; j < a1.length(); j++) {
                char c = a2.charAt(j);
                if (!a3.containsKey(c)) {
                    a3.put(c, null);
                }
            }
        }
        return alphabet;
    }

    /**
     * Crossed two <code>&lt;sdefs&gt;</code> elements.
     *
     * @param sdefs1
     * @param sdefs2
     * @return Undefined     
     */
    private Sdefs crossSdefs(Sdefs sdefs1, Sdefs sdefs2) {
        Sdefs sdefs = new Sdefs();
        if (sdefs1 != null && sdefs2 != null) {
            msg.out("[" + (taskOrder++) + "] Crossing definitions...\n");

            HashMap<String, Sdef> sdefList = new HashMap<String, Sdef>();

            for (Sdef sdef1 : sdefs1.elements) {
                if (!sdefList.containsKey(sdef1.getValue())) {
                    sdefList.put(sdef1.getValue(), sdef1);
                }
            }
            for (Sdef sdef2 : sdefs2.elements) {
                if (!sdefList.containsKey(sdef2.getValue())) {
                    sdefList.put(sdef2.getValue(), sdef2);
                } else {
                    Sdef sdef1 = sdefList.get(sdef2.getValue());

                    if ((sdef1.comment != null) && (sdef2.comment == null)) {
                        sdef2.comment=sdef1.comment;
                    }

                    if ((sdef1.comment != null) && (sdef2.comment != null)) {
                        if (!sdef1.comment.equals(sdef2.comment)) {
                            sdef2.comment=sdef1.comment + "/" + sdef2.comment;
                        }
                    }
                    sdefList.put(sdef2.getValue(), sdef2);
                }
            }

            Set<String> keys = sdefList.keySet();
            Iterator<String> it = keys.iterator();

            while (it.hasNext()) {
                String str = it.next();
                Sdef sdef = sdefList.get(str);
                sdefs.elements.add(sdef);
            }
            sdefList = null;
            it = null;
            keys = null;
        }
        return sdefs;
    }

    /**
     * Crossed two <code>&lt;section&gt;</code> elements.
     *
     * @param section1
     * @param section2
     * @return Undefined     */
    private Section[] crossSections(Section section1, Section section2, Sdefs sdefs) {

        msg.out("[" + (taskOrder++) + "] Crossing sections '" + section1.id + "' and '" + section2.id + "'\n");
        Section[] sections = new Section[2];

        Section section = new Section();
        section.id = section1.id;
        section.type = section1.type;
        ArrayList<E> elements1 = section1.elements;
        ArrayList<E> elements2 = section2.elements;
        HashMap<String, ArrayList<E>> section1Map = DicTools.buildHash(elements1);
        HashMap<String, ArrayList<E>> section2Map = DicTools.buildHash(elements2);
        crossSectionsAB(elements1, section2Map, section, bilBC, 0);
        section2Map = null;
        crossSectionsAB(elements2, section1Map, section, bilAB, 1);
        section1Map = null;
        sections[0] = section;
        return sections;
    }

    /**
     * 
     * @param elements
     * @param sectionMap
     * @param section
     * @param bil
     * @param dir
     */
    private void crossSectionsAB(ArrayList<E> elements, HashMap<String, ArrayList<E>> sectionMap, Section section, Dictionary bil, int dir) {
        for (E e : elements) {
            if (e.containsRegEx()) {
                String key = e.getFirstRegEx().getValue();
                if (!regExProcessed.containsKey(key)) {
                    section.elements.add(e);
                    regExProcessed.put(e.getFirstRegEx().getValue(), e);
                }
            } else {
                ArrayList<E> candidates = getPairs(e, sectionMap);
                if (candidates != null) {
                    crossElementAndPairs(e, candidates, section, dir);
                }
            }
        }
    }


    public String getSElementsString(E e, String side) {
        String str = "";
            for (S s : e.getSymbols(side)) {
                str += "<s n=\"" + s.getValue() + "\"/>";
            }
        return str;
    }

    public String getHash(E e) {
        String str = "";
        if (e.hasRestriction()) {
            str += str + e.restriction + "---";
        }
        str += e.getValue("L") + "---" + getSElementsString(e,"L") + "---" + e.getValue("R") + "---" + getSElementsString(e,"R");
        return str;
    }


    /**
     * 
     * @param e1
     * @param candidates
     * @param section
     * @param dir
     */
    private void crossElementAndPairs(E e1, ArrayList<E> candidates, Section section, int dir) {
        try {
            for (E e2 : candidates) {
                ArrayList<E> actionEList = cross(e1, e2, dir);
                if (!actionEList.isEmpty()) {
                    for (E e : actionEList) {
                        String str = getHash(e);

                        if (!processed.containsKey(str)) {
                            section.elements.add(e);
                            processed.put(str, e);
                            String actionID = e.patternApplied;
                            logUsedPattern(actionID);
                        }
                    }
                }
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    /**
     * 
     * @param actionID
     */
    private void logUsedPattern(String actionID) {
        if (!usedPatterns.containsKey(actionID)) {
            usedPatterns.put(actionID, new Integer(1));
        } else {
            Integer times = usedPatterns.get(actionID);
            int t = times.intValue();
            t++;
            usedPatterns.put(actionID, new Integer(t));
        }
    }

    /**
     * 
     * @param e1
     * @param e2
     * @param dir
     * @return
     */
    private ArrayList<E> cross(E e1, E e2, int dir) {
        ArrayList<E> actionEList = new ArrayList<E>();
        this.incrementNCrossedElements();
        if (dir != 0) {
            E aux;
            aux = (E) e2.clone();
            e2 = (E) e1.clone();
            e1 = aux;
        }

        msg.log("crossing " + e1.getValue("R") + " and " + e2.getValue("R") + "\n");

        CrossAction crossAction = new CrossAction();
        Pattern entriesPattern = new Pattern(e1.reverse(), e2);
        crossAction.setPattern(entriesPattern);
        CrossActionData cad = crossModelProcessor.getBestActionSet(crossAction);
        if (cad != null) {
            String actionID = cad.getCrossAction().getId();
            if (actionID.equals("default")) {
                insertNDCrossAction(crossAction);
            }
            e1.print("R", msg);
            e1.print("L", msg);
            e2.print("L", msg);
            e2.print("R", msg);
            actionEList = applyCrossAction(e1, e2, cad);
        }
        return actionEList;
    }

    /**
     * 
     * @param e1
     * @param e2
     * @param cad
     * @return
     */
    private ArrayList<E> applyCrossAction(E e1, E e2, CrossActionData cad) {
        ArrayList<E> elementList = new ArrayList<E>();
        CrossAction cA = cad.getCrossAction();

        for (Action action : cA.getActionSet()) {
            E eAction = action.getE();
            int iR = resolveRestriction(e1.restriction, e2.restriction);
            if (iR != NONE) {
                E actionE = assignValues(e1, e2, eAction, cad.getVars());
                String actionID = cad.getCrossAction().getId();
                actionE.patternApplied = (actionID);

                // author attribute
                //String author = mergeAttributes(e1.author, e2.author);
                //actionE.setAuthor(author);

                // comment attribute
                actionE.comment=mergeAttributes(e1.comment, e2.comment);

                // alt attribute
                actionE.alt = mergeAttributes(e1.alt, e2.alt);

                actionE.addProcessingComment(actionID);
                msg.log("Pattern (winner): " + actionID + "\n");
                actionE.print("L", msg);
                actionE.print("R", msg);
                elementList.add(actionE);
            }
        }
        return elementList;
    }

    /**
     * 
     * @param e1
     * @param e2
     * @param eAction
     * @param vars
     * @return New 'e' element
     */
    public E assignValues(E e1, E e2, E eAction, Variables vars) {
        E eCrossed = new E();
        //if (eAction.hasRestriction()) {
        if (!eAction.isRestrictionAuto()) {
            // restriction indicated in cross pattern
            eCrossed.setProcessingComments("\tforced '" + eAction.restriction + "' restriction\n");
            eCrossed.restriction=eAction.restriction;
        } else {
            // automatically resolved restriction
            int iR = resolveRestriction(e1.restriction, e2.restriction);
            String restriction = getRestrictionString(iR);
            eCrossed.restriction=restriction;
        }

        P pE = new P();
        ContentElement lE = eAction.getSide("L");
        ContentElement rE = eAction.getSide("R");
        L lE2 = new L();
        R rE2 = new R();

        assignValuesSide(lE2, lE, vars);
        assignValuesSide(rE2, rE, vars);

        pE.l = (lE2);
        pE.r = (rE2);
        eCrossed.children.add(pE);
        return eCrossed;
    }

    /**
     * 
     * @param ceWrite
     * @param ceRead
     * @param vars
     */
    private void assignValuesSide(ContentElement ceWrite, ContentElement ceRead, Variables vars) {
        for (DixElement e : ceRead.children) {
            if (e instanceof TextElement) {
                String x = e.getValue();
                if (vars.containsKey(x)) {
                    String sx = (String) vars.get(x);
                    ceWrite.children.add(new TextElement(sx));
                } else {
                    ceWrite.children.add(new TextElement(x));
                }
            }
            if (e instanceof B) {
                ceWrite.children.add(new B());
            }

            if (e instanceof S) {
                String x = ((S) e).getValue();
                if (vars.containsKey(x)) {
                    if (x.startsWith("X")) {
                        String sx = (String) vars.get(x);
                        ceWrite.children.add(new S(sx));
                    }
                    if (x.startsWith("S")) {
                        ArrayList<S> sxlist = (ArrayList<S>) vars.get(x);
                        for (S sE : sxlist) {
                            ceWrite.children.add(new S(sE));
                        }

                    }
                } else {
                    ceWrite.children.add(new S(e.getValue()));
                }

            }
        }
    }

    /**
     *
     * @param e
     * @param hm
     * @return Undefined     
     */
    private ArrayList<E> getPairs(E e, HashMap<String, ArrayList<E>> hm) {
        String lemma = e.getValue("L");
        lemma = DicTools.clearTags(lemma);
        ArrayList<E> pairs = hm.get(lemma);
        return pairs;
    }

    /**
     *
     * @param attr1
     * @param attr2
     * @return Undefined     
     */
    private String mergeAttributes(String attr1, String attr2) {
        String attr = null;
        if ((attr1 != null) && (attr2 != null)) {
            attr = attr1 + "/" + attr2;
            return attr;
        } else {
            if ((attr1 == null) && (attr2 != null)) {
                return attr2;
            }
            if ((attr1 != null) && (attr2 == null)) {
                return attr1;
            }
        }
        return attr;
    }

    /**
     *
     * @param r1
     * @param r2
     * @return Undefined     */
    private int resolveRestriction(String r1, String r2) {
        int c1 = getRestrictionCode(r1);
        int c2 = getRestrictionCode(r2);
        int r = rMatrix[c1][c2];
        return r;
    }

    /**
     *
     * @param restriction
     * @return Undefined     */
    private int getRestrictionCode(String r) {
        if (r != null) {
            if (r.equals("LR")) {
                return LR;
            } else {
                if (r.equals("RL")) {
                    return RL;
                } else {
                    return NONE;
                }
            }
        } else {
            return BOTH;
        }
    }

    /**
     *
     * @param code
     * @return Undefined    
     */
    private String getRestrictionString(int code) {
        switch (code) {
            case LR:
                return "LR";
            case RL:
                return "RL";
            case BOTH:
                return null;
            case NONE:
                return null;
            default:
                return null;
        }
    }

    /**
     * @return the ndCrossActions
     */
    private void insertNDCrossAction(CrossAction cA) {
        if (!nDCrossActions.containsKey(cA.getPattern().toString())) {
            nDCrossActions.put(cA.getPattern().toString(), cA);
            nDCrossModel.addCrossAction(cA);
        } else {
            CrossAction ca = nDCrossActions.get(cA.getPattern().toString());
            ca.incrementOccurrences();
            NDcounter++;
        }
    }

    public void doCross() {
            new File("dix").mkdir();

        processArguments();
        actionCross();
    }

    
    public void actionCross() {
        actionConsistent(dicSet, "yes");

        Dictionary[] bils = crossDictionaries(dicSet);
        Dictionary bilCrossed = bils[0];
        String sl = dicSet.bil1.rightLanguage;
        String tl = dicSet.bil2.rightLanguage;
        bilCrossed.type = Dictionary.BIL;
        bilCrossed.leftLanguage = sl;
        bilCrossed.rightLanguage = tl;
        msg.out("[" + (taskOrder++) + "] Making consistent morphological dicionaries ...\n");

        ArrayList<E>[] commonCrossedMons = DicTools.makeConsistent(bilCrossed, dicSet.mon1, dicSet.mon2);
        ArrayList<E> crossedMonA = commonCrossedMons[0];
        ArrayList<E> crossedMonB = commonCrossedMons[1];

        Dictionary monACrossed = new Dictionary(dicSet.mon1);
        monACrossed.setMainSection(crossedMonA);
        Dictionary monBCrossed = new Dictionary(dicSet.mon2);
        monBCrossed.setMainSection(crossedMonB);

        ArrayList<Dictionary> dicList = new ArrayList<Dictionary>();
        dicList.add(bilCrossed);
        dicList.add(monACrossed);
        dicList.add(monBCrossed);
        printXMLCrossed(dicList, sl, tl,opt);
        msg.out("[" + (taskOrder++) + "] Done!\n");
        this.completed = 100;
		
		if (this.progressBar != null) {
		    this.progressBar.setValue(100);
		}
    }

    /**
     *
     * @param del
     * @param sl
     * @param tl
     */
    private void printXMLCrossed(ArrayList<Dictionary> del, String sl, String tl, DicOpts opt) {
        Dictionary bilCrossed = del.get(0);
        Dictionary monACrossed = del.get(1);
        Dictionary monBCrossed = del.get(2);

        int i = 0;
        String patterns = "";
        bilCrossed.addProcessingComment("");
        bilCrossed.addProcessingComment("Patterns applied:");
        msg.log("Patterns applied:");
        for (CrossAction cA : crossModel.getCrossActions()) {
            String cAName = cA.getId();
            if (!usedPatterns.containsKey(cAName)) {
                if (i != 0) {
                    patterns += ", " + cAName;
                } else {
                    patterns += cAName;
                }
                i++;
            } else {
                String mesg = "\t" + cAName + " (" + usedPatterns.get(cAName) + " times)";
                msg.log(mesg);
                bilCrossed.addProcessingComment(mesg);
            }
        }
        taskOrder = taskOrder + 1;
        msg.log("[" + (taskOrder) + "] Patterns never applied: " + patterns + "\n");
        msg.out("[" + (taskOrder) + "] Generating crossed dictionaries ...\n");
        taskOrder = taskOrder + 1;
        this.bilCrossed_path = this.outDir + "apertium-" + sl + "-" + tl + "." + sl + "-" + tl + "-crossed.dix";
        bilCrossed.printXML(this.bilCrossed_path, opt);

        this.monACrossed_path = this.outDir + "apertium-" + sl + "-" + tl + "." + sl + "-crossed.dix";
        monACrossed.printXML(this.monACrossed_path, opt);

        this.monCCrossed_path = this.outDir + "apertium-" + sl + "-" + tl + "." + tl + "-crossed.dix";
        monBCrossed.printXML(this.monCCrossed_path, opt);
    }

    /**
     *
     * @param dicSet
     * @param removeNotCommon
     * @return Undefined     */
    private DicConsistent actionConsistent(DicSet dicSet, String removeNotCommon) {
        DicConsistent dicConsistent = new DicConsistent(dicSet);
        dicConsistent.makeConsistentDictionaries(removeNotCommon);
        dicSet.printXML(this.outDir + "consistent",opt);
        return dicConsistent;
    }

    /**
     *
     * @param arguments
     */
    private void processArguments() {
        int nArgs = arguments.length;
        String sDicMonA;
        String sDicMonC;
        String sDicBilAB;
        String sDicBilBC;
        sDicMonA = sDicMonC = sDicBilAB = sDicBilBC = null;
        boolean bilABReverse;
        boolean bilBCReverse;
        bilABReverse = bilBCReverse = false;

        String source = "";
        int type = -1;
        String sltl = "";

        for (int i = 1; i < nArgs; i++) {
            String arg = arguments[i];

            if (arg.equals("-f")) {
                i++;
                arg = arguments[i];
                source = arg;
                type = LingResourcesReader.FILE;
                i++;
                sltl = arguments[i];
            }

            if (arg.equals("-url")) {
                i++;
                arg = arguments[i];
                source = arg;
                type = LingResourcesReader.URL;
                i++;
                sltl = arguments[i];
            }

            if (arg.equals("-monA")) {
                i++;
                arg = arguments[i];
                sDicMonA = arg;
            }

            if (arg.equals("-monC")) {
                i++;
                arg = arguments[i];
                sDicMonC = arg;
            }

            if (arg.equals("-bilAB")) {
                i++;
                arg = arguments[i];
                if (arg.equals("-r")) {
                    bilABReverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilABReverse = false;
                    i++;
                }

                arg = arguments[i];
                sDicBilAB = arg;
            }

            if (arg.equals("-bilBC")) {
                i++;
                arg = arguments[i];

                if (arg.equals("-r")) {
                    bilBCReverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilBCReverse = false;
                    i++;
                }
                arg = arguments[i];
                sDicBilBC = arg;
            }

            if (arg.equals("-cross-model")) {
                i++;
                arg = arguments[i];
                this.crossModelFileName = arg;
            }

            if (arg.equals("-debug")) {
                i++;
                msg.setDebug(true);
                msg.out("Debug mode on\n");
            }
        }

        if (type != -1) {
            msg.out("[" + (taskOrder++) + "] Reading linguistic resources file (" + source + ")\n");
            LingResourcesReader lrr = new LingResourcesReader(source, type);
            LingResources lingRes = lrr.readLingResources();
            DicSet theDicSet = this.getDicSetForCrossing(lingRes, sltl);
            this.dicSet = theDicSet;
        } else {
            msg.out("[" + (taskOrder++) + "] Loading bilingual AB (" + sDicBilAB + ")\n");
            Dictionary bil1 = DicTools.readBilingual(sDicBilAB, bilABReverse);
            msg.out("[" + (taskOrder++) + "] Loading bilingual BC (" + sDicBilBC + ")\n");
            Dictionary bil2 = DicTools.readBilingual(sDicBilBC, bilBCReverse);
            msg.out("[" + (taskOrder++) + "] Loading monolingual A (" + sDicMonA + ")\n");
            Dictionary mon1 = DicTools.readMonolingual(sDicMonA);
            msg.out("[" + (taskOrder++) + "] Loading monolingual C (" + sDicMonC + ")\n");
            Dictionary mon2 = DicTools.readMonolingual(sDicMonC);
            DicSet theDicSet = new DicSet(mon1, bil1, mon2, bil2);
            this.dicSet = theDicSet;
        }
    }

    /**
     * 
     * @param lingRes
     * @param sltl
     * @return A set of dictionaries
     */
    private DicSet getDicSetForCrossing(LingResources lingRes, String sltl) {
        String[] pair = sltl.split("-");
        String sl = pair[0];
        String tl = pair[1];
        msg.out("[" + (taskOrder++) + "] New language pair: " + sl + "-" + tl + "\n");
        ArrayList<Resource> resources = lingRes.getResourceList();

        Dictionary mA = null;
        Dictionary mC = null;
        Dictionary bAB = null;
        Dictionary bBC = null;

        boolean mAok = false;
        boolean mCok = false;
        boolean bABok = false;
        boolean bBCok = false;
        boolean CMok = false;

        String clAB = null; // common language AB
        String clBC = null; // common language BC

        for (Resource r : resources) {
            if (r.isUseForCrossing()) {
                if (r.isCrossModel() && r.isSL(sl) && r.isTL(tl)) {
                    msg.out("[" + (taskOrder++) + "] Loading cross model (" + r.getSource() + ")\n");
                    this.crossModelFileName = r.getSource();
                    CMok = true;
                }
                if (r.isMorphological() && r.isSL(sl)) {
                    if (mAok) {
                        msg.out("[!] Warning: alternative linguistic resource found (" + r.getSource() + ")\n");
                    } else {
                        msg.out("[" + (taskOrder++) + "] Loading monolingual A (" + r.getSource() + ")");
                        mA = this.readDic(r.getSource(), 0, false);
                        msg.out(" [" + mA.getNumberOfEntries() + "] entries\n");
                        mA.leftLanguage = sl;
                        mAok = true;
                    }
                }
                if (r.isMorphological() && r.isSL(tl)) {
                    if (mCok) {
                        msg.out("[!] Warning: alternative linguistic resource found (" + r.getSource() + ")\n");
                    } else {
                        msg.out("[" + (taskOrder++) + "] Loading monolingual C (" + r.getSource() + ")");
                        mC = readDic(r.getSource(), 0, false);
                        msg.out(" [" + mC.getNumberOfEntries() + "] entries\n");
                        mC.leftLanguage = tl;
                        mCok = true;
                    }
                }
                if (r.isBilingual() && r.isTL(sl)) {
                    if (bABok) {
                        msg.out("[!] Warning: alternative linguistic resource found (" + r.getSource() + ")\n");
                    } else {
                        msg.out("[" + (taskOrder++) + "] Loading bilingual AB (" + r.getSource() + ")");
                        bAB = readDic(r.getSource(), 1, false);
                        msg.out(" [" + bAB.getNumberOfEntries() + "] entries\n");
                        bAB.rightLanguage = sl;
                        bABok = true;
                        if (clAB == null) {
                            clAB = new String(r.getSL());
                        }
                    }
                }
                if (r.isBilingual() && r.isSL(sl)) {
                    if (bABok) {
                        msg.out("[!] Warning: alternative linguistic resource found (" + r.getSource() + ")\n");
                    } else {
                        msg.out("[" + (taskOrder++) + "] Loading bilingual AB (" + r.getSource() + ") [reversed]");
                        bAB = this.readDic(r.getSource(), 1, true);
                        msg.out(" [" + bAB.getNumberOfEntries() + "] entries\n");
                        bAB.rightLanguage = sl;
                        bABok = true;
                        if (clAB == null) {
                            clAB = new String(r.getTL());
                        }
                    }
                }
                if (r.isBilingual() && r.isTL(tl)) {
                    if (bBCok) {
                        msg.out("[!] Warning: alternative linguistic resource found (" + r.getSource() + ")\n");
                    } else {
                        msg.out("[" + (taskOrder++) + "] Loading bilingual BC (" + r.getSource() + ")");
                        bBC = readDic(r.getSource(), 1, false);
                        msg.out(" [" + bBC.getNumberOfEntries() + "] entries\n");
                        bBC.rightLanguage = tl;
                        bBCok = true;
                        if (clBC == null) {
                            clBC = new String(r.getSL());
                        }
                    }
                }
                if (r.isBilingual() && r.isSL(tl)) {
                    if (bBCok) {
                        msg.out("[!] Warning: alternative linguistic resource found (" + r.getSource() + ")\n");
                    } else {
                        msg.out("[" + (taskOrder++) + "] Loading bilingual BC (" + r.getSource() + ") [reversed]");
                        bBC = this.readDic(r.getSource(), 1, true);
                        msg.out(" [" + bBC.getNumberOfEntries() + "] entries\n");
                        bBC.rightLanguage = tl;
                        bBCok = true;
                        if (clBC == null) {
                            clBC = new String(r.getTL());
                        }
                    }
                }
            }
        }
        if (!CMok) {
            System.err.println("Error: could not find a cross model file to get '" + sl + "-" + tl + "'");
            System.exit(-1);
        }
        if (!mAok) {
            System.err.println("Error: could not find a morphological dictionary for '" + sl + "'");
            System.exit(-1);
        }
        if (!mCok) {
            System.err.println("Error: could not find a morphological dictionary for '" + tl + "'");
            System.exit(-1);
        }
        if (!bABok) {
            System.err.println("Error: could not find a bilingual dictionary like '" + sl + "-??' or '??-" + sl + "'");
            System.exit(-1);
        }
        if (!bBCok) {
            System.err.println("Error: could not find a bilingual dictionary like '??-" + tl + "' or '" + tl + "-??'");
            System.exit(-1);
        }
        if (!clAB.equals(clBC)) {
            System.err.println("Error: could not find a common language B for " + sl + "-B B-" + tl);
            System.exit(-1);
        }

        Dictionary mAn = getMainSectionDic(mA);
        mAn = addMissingLemmas(mAn);
        Dictionary bABn = getMainSectionDic(bAB);
        Dictionary bBCn = getMainSectionDic(bBC);
        Dictionary mCn = getMainSectionDic(mC);
        mCn = addMissingLemmas(mCn);

        DicSet dicSetC = new DicSet(mAn, bABn, mCn, bBCn);
        return dicSetC;
    }

    /**
     * 
     * @param dic
     * @return Dictionary with missing lemmas
     */
    public static Dictionary addMissingLemmas(Dictionary dic) {
        int c = 0;
        try {
            for (Section s : dic.sections) {
                if (s.type.equals("standard")) {
                    for (E ee : s.elements) {
                        if (ee.lemma == null) {
                            String v = ee.getValueNoTags("L");
                            if (v != null) {
                                String pv = ee.getMainParadigmName();
                                if (pv != null) {
                                    String[] parts = pv.split("/");
                                    if (parts.length > 1) {
                                        String[] parts2 = parts[1].split("__");
                                        String suffix = parts2[0];
                                        ee.lemma = v + suffix;
                                        c++;
                                    } else {
                                        c++;
                                        ee.lemma = v;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        //msg.out("[-] " + comment + " missing 'lemma' atrributes generated\n");
        return dic;
    }

    /**
     * 
     * @param dic
     * @return
     */
    private Dictionary getMainSectionDic(Dictionary dic) {
        ArrayList<Section> sections = new ArrayList<Section>();
        Section mainSection = new Section("main", "standard");
        for (Section s : dic.sections) {
            if (s.type.equals("standard")) {
                for (E ee : s.elements) {
                    mainSection.elements.add(ee);
                }
            } else {
                sections.add(s);
            }
        }
        sections.add(mainSection);
        dic.sections = sections;
        return dic;
    }

    /**
     * 
     * @param source
     * @return
     */
    private Dictionary readDic(String source, int type, boolean reverse) {
        Dictionary dic = null;
        if (source.startsWith("http://")) {
            try {
                URL url = new URL(source);
                InputStream is = url.openStream();
                DictionaryReader dicReader = new DictionaryReader();
                dicReader.urlDic = true;
                dicReader.is = is;
                dic = dicReader.readDic();
                dic.fileName = source;
            } catch (MalformedURLException mfue) {
                System.err.println("Error: malformed URL exception!");
                System.exit(-1);
            } catch (IOException ioe) {
                System.err.println("Error: I/O exception!");
                System.exit(-1);
            }
        } else {
            if (type == 0) {
                dic = DicTools.readMonolingual(source);
            }

            if (type == 1) {
                dic = DicTools.readBilingual(source, reverse);
            }
            //DictionaryReader dicReader = new DictionaryReader(source);
            //dic = dicReader.readDic();
            dic.fileName = source;
        }
        return dic;
    }


    public void incrementNCrossedElements() {
        this.nCrossedElements++;
        double compl = ((this.nCrossedElements / this.nMinElements)) * 100;
        int perc = (int) compl;
        this.completed = perc;
		
		if (this.progressBar != null) {
		    this.progressBar.setValue(perc);
		}
    }

    
    /**
     * 
     * @param dicSet
     * @return Checks whether the meta info firstSymbolIs complete
     */
    public boolean isMetaInfoComplete(DicSet dicSet) {
        for (Dictionary dic : dicSet) {
            if (!dic.isHeaderDefined()) {
                return false;
            }
        }
        return true;
    }
}
