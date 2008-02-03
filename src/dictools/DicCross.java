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
package dictools;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import dics.elements.dtd.AlphabetElement;
import dics.elements.dtd.ContentElement;
import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.Element;
import dics.elements.dtd.HeaderElement;
import dics.elements.dtd.LElement;
import dics.elements.dtd.PElement;
import dics.elements.dtd.RElement;
import dics.elements.dtd.SElement;
import dics.elements.dtd.SdefElement;
import dics.elements.dtd.SdefsElement;
import dics.elements.dtd.SectionElement;
import dics.elements.dtd.TextElement;
import dics.elements.utils.DicSet;
import dics.elements.utils.DictionaryElementList;
import dics.elements.utils.EElementList;
import dics.elements.utils.EElementMap;
import dics.elements.utils.EHashMap;
import dics.elements.utils.ElementList;
import dics.elements.utils.Msg;
import dictools.crossmodel.Action;
import dictools.crossmodel.ActionSet;
import dictools.crossmodel.ConstantMap;
import dictools.crossmodel.CrossAction;
import dictools.crossmodel.CrossModel;
import dictools.crossmodel.CrossModelFST;
import dictools.crossmodel.CrossModelReader;
import dictools.crossmodel.Pattern;
import javax.swing.JProgressBar;

/**
 *
 * @author Enrique Benimeli Bofarull
 *
 */
public class DicCross {

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
    /**
     *
     */
    private final int NONE = 3;
    /**
     * Bilingual dictionary A-B
     */
    private DictionaryElement bilAB;
    /**
     * Bilingual dictionary B-C
     */
    private DictionaryElement bilBC;
    /**
     *
     */
    //private final boolean MATCH_CATEGORY = true;
    /**
     *
     */
    private EHashMap processed;
    /**
     *
     */
    private EHashMap regExProcessed;
    /**
     *
     */
    private CrossModel crossModel;
    /**
     *
     */
    private CrossModelFST crossModelFST;
    /**
     *
     */
    private String crossModelFileName;
    /**
     *
     */
    private HashMap<String, CrossAction> nDCrossActions;
    /**
     *
     */
    private CrossModel nDCrossModel;
    /**
     *
     */
    private String[] arguments;
    /**
     *
     */
    private DicSet dicSet;
    /**
     *
     */
    private int NDcounter;
    /**
     *
     */
    private HashMap<String, Integer> usedPatterns;
    /**
     *
     */
    private int taskOrder;
    /**
     *
     */
    private Msg msg;
    /**
     *
     */
    private String outDir = "dix/";
    /**
     * 
     */
    private int completed = 0;
    /**
     * 
     */
    private double nMinElements = 0;
    /**
     *
     */
    private double nCrossedElements = 0;
    /**
     * 
     */
    private JProgressBar progressBar;
    /**
     * 
     */
    private String bilCrossed_path;
    /**
     * 
     */
    private String monACrossed_path;
    /**
     * 
     */
    private String monCCrossed_path;

    /**
     *
     *
     */
    public DicCross() {
        msg = new Msg();
        msg.setLogFileName("cross.log");
        rMatrix = new int[3][3];
        fillOutRestrictionMatrix();
        processed = new EHashMap();
        regExProcessed = new EHashMap();
        nDCrossActions = new HashMap<String, CrossAction>();
        nDCrossModel = new CrossModel();
        usedPatterns = new HashMap<String, Integer>();
        taskOrder = 1;
    }

    /**
     *
     *
     */
    private final void readCrossModel() {
        try {
            msg.out("[" + (taskOrder++) + "] Reading cross model (" + getCrossModelFileName() + ")");
            final CrossModelReader cmr = new CrossModelReader(getCrossModelFileName());
            CrossModel cm = cmr.readCrossModel();
            cm.rename();
            cm.printXML("cm-renamed.xml");
            
            msg.out(" (" + cm.getCrossActions().size() + " patterns processed).");

            setCrossModel(cm);
            CrossModelFST fst = new CrossModelFST(getCrossModel());
            fst.setMsg(msg);
            setCrossModelFST(fst);
        } catch (final Exception e) {
            e.printStackTrace();
            msg.out("Error reading cross model");
            System.exit(-1);
        }
    }

    /**
     *
     *
     */
    private final void fillOutRestrictionMatrix() {
        // Note: B-A ^ B-C = A-C
        setRMatrixValue(LR, LR, NONE);
        setRMatrixValue(LR, RL, RL);
        setRMatrixValue(LR, BOTH, RL);

        setRMatrixValue(RL, LR, LR);
        setRMatrixValue(RL, RL, NONE);
        setRMatrixValue(RL, BOTH, LR);

        setRMatrixValue(BOTH, LR, LR);
        setRMatrixValue(BOTH, RL, RL);
        setRMatrixValue(BOTH, BOTH, BOTH);
    }

    /**
     * 
     * @param dicSet
     * @return Undefined
     */
    public DictionaryElement[] crossDictionaries(final DicSet dicSet) {
        final DictionaryElement[] dics = new DictionaryElement[2];
        readCrossModel();

        final DictionaryElement dic1 = dicSet.getBil1();
        final DictionaryElement dic2 = dicSet.getBil2();

        int nDic1 = dic1.getAllEntries().size();
        int nDic2 = dic2.getAllEntries().size();

        this.setNMinElements(nDic1 + nDic2);

        setBilAB(dic1);
        setBilBC(dic2);

        final DictionaryElement dic = new DictionaryElement();

        // encoding
        final String encoding = crossXmlEncodings(dic1.getXmlEncoding(), dic2.getXmlEncoding());
        dic.setXmlEncoding(encoding);

        // alphabet
        final AlphabetElement alphabet = crossAlphabets(dic1.getAlphabet(), dic2.getAlphabet());
        dic.setAlphabet(alphabet);

        // sdefs
        final SdefsElement sdefs = crossSdefs(dic1.getSdefs(), dic2.getSdefs());
        dic.setSdefs(sdefs);

        // sections
        for (final SectionElement section1 : dic1.getSections()) {
            final SectionElement section2 = dic2.getSection(section1.getID());
            final SectionElement[] sections = crossSections(section1, section2, sdefs);
            final SectionElement section = sections[0];
            dic.addSection(section);
        }

        msg.out("[" + (taskOrder++) + "] Sorting crossed dictionary...");
        Collections.sort(dic.getEntries());

        getNDCrossModel().printXML(this.getOutDir() + "patterns-not-detected.xml");
        dics[0] = dic;
        return dics;
    }

    /**
     *
     * @param encoding1
     * @param encoding2
     */
    private final String crossXmlEncodings(final String encoding1, final String encoding2) {
        return "UTF-8";
    }

    /**
     *
     * @param alphabet1
     * @param alphabet2
     * @return Undefined     
     */
    private final AlphabetElement crossAlphabets(final AlphabetElement alphabet1, final AlphabetElement alphabet2) {
        final AlphabetElement alphabet = new AlphabetElement();
        if (alphabet2 != null && alphabet2 != null) {


            final String a1 = alphabet1.getAlphabet();
            final String a2 = alphabet2.getAlphabet();

            final HashMap<Object, Object> a3 = new HashMap<Object, Object>();

            int i = 0;
            for (i = 0; i < a1.length(); i++) {
                final char c = a1.charAt(i);
                if (!a3.containsKey(c)) {
                    a3.put(c, null);
                }
            }

            int j = 0;
            for (j = 0; j < a1.length(); j++) {
                final char c = a2.charAt(j);
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
    private final SdefsElement crossSdefs(final SdefsElement sdefs1, final SdefsElement sdefs2) {
        final SdefsElement sdefs = new SdefsElement();
        if (sdefs1 != null && sdefs2 != null) {
            msg.out("[" + (taskOrder++) + "] Crossing definitions...");

            HashMap<String, SdefElement> sdefList = new HashMap<String, SdefElement>();

            for (final SdefElement sdef1 : sdefs1.getSdefsElements()) {
                if (!sdefList.containsKey(sdef1.getValue())) {
                    sdefList.put(sdef1.getValue(), sdef1);
                }
            }
            for (final SdefElement sdef2 : sdefs2.getSdefsElements()) {
                if (!sdefList.containsKey(sdef2.getValue())) {
                    sdefList.put(sdef2.getValue(), sdef2);
                } else {
                    SdefElement sdef1 = sdefList.get(sdef2.getValue());

                    if ((sdef1.getComment() != null) && (sdef2.getComment() == null)) {
                        sdef2.setComment(sdef1.getComment());
                    }

                    if ((sdef1.getComment() != null) && (sdef2.getComment() != null)) {
                        if (!sdef1.getComment().equals(sdef2.getComment())) {
                            sdef2.setComment(sdef1.getComment() + "/" + sdef2.getComment());
                        }
                    }
                    sdefList.put(sdef2.getValue(), sdef2);
                }
            }

            Set<String> keys = sdefList.keySet();
            Iterator<String> it = keys.iterator();

            while (it.hasNext()) {
                final String str = it.next();
                final SdefElement sdef = sdefList.get(str);
                sdefs.addSdefElement(sdef);
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
    private SectionElement[] crossSections(final SectionElement section1, final SectionElement section2, final SdefsElement sdefs) {

        msg.out("[" + (taskOrder++) + "] Crossing sections '" + section1.getID() + "' and '" + section2.getID() + "'");
        final SectionElement[] sections = new SectionElement[2];

        final SectionElement section = new SectionElement();
        section.setID(section1.getID());
        section.setType(section1.getType());
        final EElementList elements1 = section1.getEElements();
        final EElementList elements2 = section2.getEElements();
        EElementMap section1Map = DicTools.buildHash(elements1);
        EElementMap section2Map = DicTools.buildHash(elements2);
        crossSectionsAB(elements1, section2Map, section, getBilBC(), 0);
        section2Map = null;
        crossSectionsAB(elements2, section1Map, section, getBilAB(), 1);
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
    private void crossSectionsAB(final EElementList elements, final EElementMap sectionMap, final SectionElement section, final DictionaryElement bil, final int dir) {
        for (final EElement e : elements) {
            if (e.isRegEx()) {
                final String key = e.getRegEx().getValue();
                if (!getRegExProcessed().containsKey(key)) {
                    section.addEElement(e);
                    getRegExProcessed().put(e.getRegEx().getValue(), e);
                }
            } else {
                final EElementList candidates = getPairs(e, sectionMap);
                if (candidates != null) {
                    crossElementAndPairs(e, candidates, section, dir);
                }
            }
        }
    }

    /**
     * 
     * @param e1
     * @param candidates
     * @param section
     * @param dir
     */
    private final void crossElementAndPairs(final EElement e1, final EElementList candidates, final SectionElement section, final int dir) {
        try {
            for (EElement e2 : candidates) {
                EElementList actionEList = cross(e1, e2, dir);
                if (!actionEList.isEmpty()) {
                    for (EElement e : actionEList) {
                        final String str = e.getHash();
                        if (!getProcessed().containsKey(str)) {
                            section.addEElement(e);
                            getProcessed().put(str, e);
                            String actionID = e.getPatternApplied();
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
    private final void logUsedPattern(final String actionID) {
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
    private final EElementList cross(EElement e1, EElement e2, final int dir) {
        EElementList actionEList = new EElementList();
        this.incrementNCrossedElements();
        if (dir != 0) {
            EElement aux;
            aux = (EElement) e2.clone();
            e2 = (EElement) e1.clone();
            e1 = aux;
        }

        msg.log("crossing " + e1.getValue("R") + " and " + e2.getValue("R") + "\n");
        msg.msg("crossing " + e1.getValue("R") + " and " + e2.getValue("R") + "\n");

        CrossAction crossAction = new CrossAction();
        Pattern entriesPattern = new Pattern(e1.reverse(), e2);
        crossAction.setPattern(entriesPattern);
        ActionSet actionSet = getCrossModelFST().getActionSet(crossAction);
        if (actionSet != null) {
            String actionID = actionSet.getName();
            if (actionID.equals("default")) {
                insertNDCrossAction(crossAction);
            }
            e1.print("R", msg);
            e1.print("L", msg);
            e2.print("L", msg);
            e2.print("R", msg);
            actionEList = applyCrossAction(e1, e2, actionID, crossAction);
        }
        return actionEList;
    }

    /**
     *
     * @param e1
     * @param e2
     * @param actionID
     * @param crossAction
     * @return Undefined     
     */
    private final EElementList applyCrossAction(EElement e1, EElement e2, final String actionID, final CrossAction entries) {
        EElementList elementList = new EElementList();
        final CrossAction cA = getCrossModel().getCrossAction(actionID);
        HashMap<String, ElementList> tails = cA.getActionSet().getTails();

        for (Action action : cA.getActionSet()) {

            EElement eAction = action.getE();

            final int iR = resolveRestriction(e1.getRestriction(), e2.getRestriction());
            if (iR != NONE) {

                final String restriction = getRestrictionString(iR);
                EElement actionE = assignValues(e1, e2, eAction, entries, tails);
                actionE.setPatternApplied(actionID);

                if (eAction.hasRestriction()) {
                    // restriction indicated in cross pattern
                    actionE.setComments("\tforced '" + eAction.getRestriction() + "' restriction\n");
                    actionE.setRestriction(eAction.getRestriction());
                } else {
                    // automatically resolved restriction
                    actionE.setRestriction(restriction);
                }

                // author attribute
                final String author = mergeAttributes(e1.getAuthor(), e2.getAuthor());
                actionE.setAuthor(author);

                // comment attribute
                final String comment = mergeAttributes(e1.getComment(), e2.getComment());
                actionE.setComment(comment);

                // alt attribute
                final String alt = this.mergeAttributes(e1.getAlt(), e2.getAlt());
                actionE.setAlt(alt);

                actionE.addComments(actionID);
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
     * @param eAction
     * @param entries
     * @return Undefined     */
    private final EElement assignValues(EElement e1, EElement e2, EElement eAction, CrossAction entries, HashMap<String, ElementList> tails) {
        ConstantMap constants = entries.getConstants();
        EElement eCrossed = new EElement();

        PElement pE = new PElement();

        ContentElement lE = eAction.getSide("L");
        ContentElement rE = eAction.getSide("R");

        LElement lE2 = new LElement();
        RElement rE2 = new RElement();

        //System.err.println("Tails:");
        msg.log("Tails:\n");
        this.printTails(tails);

        assignValuesSide(lE2, lE, constants, e1, tails);
        assignValuesSide(rE2, rE, constants, e2, tails);

        pE.setLElement(lE2);
        pE.setRElement(rE2);
        eCrossed.addChild(pE);
        return eCrossed;
    }

    /**
     * 
     * @param tails
     */
    private final void printTails(HashMap<String, ElementList> tails) {
        if (tails != null) {
            Set keyset = tails.keySet();
            Iterator it = keyset.iterator();

            while (it.hasNext()) {
                String key = (String) it.next();
                ElementList eList = tails.get(key);
                //System.err.print(key + ": ");
                msg.log(key + ": ");
            //eList.printErr();
            }
        }
    }

    private final void assignValuesSide(ContentElement ceWrite, final ContentElement ceRead, final ConstantMap constants, final EElement ei, HashMap<String, ElementList> tails) {

        for (Element e : ceRead.getChildren()) {

            if (e instanceof TextElement) {
                String lemma = e.getValue();
                if (lemma.equals("l1") || lemma.equals("l4")) {
                    ceWrite.addChild(new TextElement(ei.getSide("R").getValue()));
                } else {
                    ceWrite.addChild(new TextElement(lemma));
                }
            }

            if (e instanceof SElement) {
                String v = e.getValue();

                if (!v.equals("0")) {
                    if (!constants.containsKey(e.getValue())) {
                        ceWrite.addChild(new SElement(e.getValue()));
                    } else {
                        String value = constants.get(e.getValue());
                        ceWrite.addChild(new SElement(value));
                    }
                } else {
                    SElement sE = (SElement) e;
                    if (!tails.containsKey(sE.getTemp())) {
                    // ceWrite.addChild(new SElement(e.getValue()));
                    } else {
                        ElementList tail = tails.get(sE.getTemp());
                        for (Element elem : tail) {
                            if (!constants.containsKey(elem.getValue())) {
                                ceWrite.addChild(new SElement(elem.getValue()));
                            } else {
                                String value = constants.get(elem.getValue());
                                ceWrite.addChild(new SElement(value));
                            }
                        // ceWrite.addChild(elem);
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param e
     * @param hm
     * @return Undefined     */
    private final EElementList getPairs(final EElement e, final EElementMap hm) {
        String lemma = e.getValue("L");
        lemma = DicTools.clearTags(lemma);
        EElementList pairs = hm.get(lemma);
        return pairs;
    }

    /**
     *
     * @param attr1
     * @param attr2
     * @return Undefined     
     */
    private final String mergeAttributes(final String attr1, final String attr2) {
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
    private final int resolveRestriction(final String r1, final String r2) {
        final int c1 = getRestrictionCode(r1);
        final int c2 = getRestrictionCode(r2);
        final int r = rMatrix[c1][c2];
        return r;
    }

    /**
     *
     * @param r
     * @return Undefined     */
    private final int getRestrictionCode(final String r) {
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
    private final String getRestrictionString(final int code) {
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
     *
     * @return Undefined     */
    private final CrossModel getCrossModel() {
        return crossModel;
    }

    /**
     *
     * @param crossModel
     */
    public final void setCrossModel(final CrossModel crossModel) {
        this.crossModel = crossModel;
    }

    /**
     * @return the processed
     */
    private final EHashMap getProcessed() {
        return processed;
    }

    /**
     * @return the regExProcessed
     */
    private final EHashMap getRegExProcessed() {
        return regExProcessed;
    }

    /**
     * @return the bilAB
     */
    private final DictionaryElement getBilAB() {
        return bilAB;
    }

    /**
     * @param bilAB
     *                the bilAB to set
     */
    private final void setBilAB(final DictionaryElement bilAB) {
        this.bilAB = bilAB;
    }

    /**
     * @return the bilBC
     */
    private final DictionaryElement getBilBC() {
        return bilBC;
    }

    /**
     * @param bilBC
     *                the bilBC to set
     */
    private final void setBilBC(final DictionaryElement bilBC) {
        this.bilBC = bilBC;
    }

    /**
     * @return the rMatrix
     */
    private final int[][] getRMatrix() {
        return rMatrix;
    }

    /**
     *
     * @param i
     * @param j
     * @param value
     */
    private final void setRMatrixValue(final int i, final int j, final int value) {
        getRMatrix()[i][j] = value;
    }

    /**
     * @return the crossModelFileName
     */
    private final String getCrossModelFileName() {
        return crossModelFileName;
    }

    /**
     * @param crossModelFileName
     *                the crossModelFileName to set
     */
    public final void setCrossModelFileName(final String crossModelFileName) {
        this.crossModelFileName = crossModelFileName;
    }

    /**
     * @return the ndCrossActions
     */
    private final void insertNDCrossAction(final CrossAction cA) {
        if (!nDCrossActions.containsKey(cA.getPattern().toString())) {
            nDCrossActions.put(cA.getPattern().toString(), cA);
            nDCrossModel.addCrossAction(cA);
        } else {
            CrossAction ca = nDCrossActions.get(cA.getPattern().toString());
            ca.incrementOccurrences();
            NDcounter++;
        }
    }

    /**
     * @return the nDCrossModel
     */
    private final CrossModel getNDCrossModel() {
        return nDCrossModel;
    }

    /**
     * @return the crossModelFST
     */
    private final CrossModelFST getCrossModelFST() {
        return crossModelFST;
    }

    /**
     * @param crossModelFST
     *                the crossModelFST to set
     */
    private final void setCrossModelFST(CrossModelFST crossModelFST) {
        this.crossModelFST = crossModelFST;
    }

    /**
     *
     *
     */
    public final void doCross() {
        processArguments();
        actionCross();
    }

    /**
     * 
     */
    public final void actionCross() {
        DicSet dicSet = getDicSet();
        actionConsistent(dicSet, "yes");
        setCrossModelFileName(getCrossModelFileName());

        final DictionaryElement[] bils = crossDictionaries(dicSet);
        final DictionaryElement bilCrossed = bils[0];
        final String sl = dicSet.getBil1().getRightLanguage();
        final String tl = dicSet.getBil2().getRightLanguage();
        bilCrossed.setType("BIL");
        bilCrossed.setLeftLanguage(sl);
        bilCrossed.setRightLanguage(tl);
        msg.out("[" + (taskOrder++) + "] Making morphological dicionaries consistent ...");

        final EElementList[] commonCrossedMons = DicTools.makeConsistent(bilCrossed, dicSet.getMon1(), dicSet.getMon2());
        final EElementList crossedMonA = commonCrossedMons[0];
        final EElementList crossedMonB = commonCrossedMons[1];

        final DictionaryElement monACrossed = new DictionaryElement(dicSet.getMon1());
        monACrossed.setMainSection(crossedMonA);
        final DictionaryElement monBCrossed = new DictionaryElement(dicSet.getMon2());
        monBCrossed.setMainSection(crossedMonB);

        final DictionaryElementList dicList = new DictionaryElementList();
        dicList.add(bilCrossed);
        dicList.add(monACrossed);
        dicList.add(monBCrossed);
        printXMLCrossed(dicList, sl, tl);
        msg.out("[" + (taskOrder++) + "] Done!");
        this.setCompleted(100);
    }

    /**
     *
     * @param del
     * @param sl
     * @param tl
     */
    private final void printXMLCrossed(final DictionaryElementList del, final String sl, final String tl) {
        final DictionaryElement bilCrossed = del.get(0);
        final DictionaryElement monACrossed = del.get(1);
        final DictionaryElement monBCrossed = del.get(2);

        int i = 0;
        String patterns = "";
        bilCrossed.addComments("");
        bilCrossed.addComments("Patterns applied:");
        msg.log("Patterns applied:");
        for (CrossAction cA : getCrossModel().getCrossActions()) {
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
                bilCrossed.addComments(mesg);
            }
        }
        taskOrder = taskOrder + 1;
        msg.log("[" + (taskOrder) + "] Patterns never applied: " + patterns + "\n");
        msg.out("[" + (taskOrder) + "] Generating crossed dictionaries ...");

        this.bilCrossed_path = this.getOutDir() + "apertium-" + sl + "-" + tl + "." + sl + "-" + tl + "-crossed.dix";
        bilCrossed.printXML(this.bilCrossed_path);

        this.monACrossed_path = this.getOutDir() + "apertium-" + sl + "-" + tl + "." + sl + "-crossed.dix";
        monACrossed.printXML(this.monACrossed_path);

        this.monCCrossed_path = this.getOutDir() + "apertium-" + sl + "-" + tl + "." + tl + "-crossed.dix";
        monBCrossed.printXML(this.monCCrossed_path);
    }

    /**
     *
     * @param dicSet
     * @param removeNotCommon
     * @return Undefined     */
    private final DicConsistent actionConsistent(final DicSet dicSet, final String removeNotCommon) {
        final DicConsistent dicConsistent = new DicConsistent(dicSet);
        dicConsistent.makeConsistentDictionaries(removeNotCommon);
        dicSet.printXML(this.getOutDir() + "consistent");
        return dicConsistent;
    }

    /**
     *
     * @param arguments
     */
    private void processArguments() {
        final int nArgs = getArguments().length;
        String sDicMonA;
        String sDicMonC;
        String sDicBilAB;
        String sDicBilBC;
        sDicMonA = sDicMonC = sDicBilAB = sDicBilBC = null;
        boolean bilABReverse;
        boolean bilBCReverse;
        bilABReverse = bilBCReverse = false;

        for (int i = 1; i < nArgs; i++) {
            String arg = getArguments()[i];
            if (arg.equals("-monA")) {
                i++;
                arg = getArguments()[i];
                sDicMonA = arg;
            //System.err.println("Monolingual A: '" + sDicMonA + "'");
            }

            if (arg.equals("-monC")) {
                i++;
                arg = getArguments()[i];
                sDicMonC = arg;
            //System.err.println("Monolingual C: '" + sDicMonC + "'");
            }

            if (arg.equals("-bilAB")) {
                i++;
                arg = getArguments()[i];
                if (arg.equals("-r")) {
                    bilABReverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilABReverse = false;
                    i++;
                }

                arg = getArguments()[i];
                sDicBilAB = arg;
            //System.err.println("Bilingual A-B: '" + sDicBilAB + "'");
            }

            if (arg.equals("-bilBC")) {
                i++;
                arg = getArguments()[i];

                if (arg.equals("-r")) {
                    bilBCReverse = true;
                    i++;
                }
                if (arg.equals("-n")) {
                    bilBCReverse = false;
                    i++;
                }
                arg = getArguments()[i];
                sDicBilBC = arg;
            //System.err.println("Bilingual B-C: '" + sDicBilBC + "'");
            }

            if (arg.equals("-cross-model")) {
                i++;
                arg = getArguments()[i];
                setCrossModelFileName(arg);
            //System.err.println("Cross model: " + arg);
            }

            if (arg.equals("-debug")) {
                i++;
                msg.setDebug(true);
                msg.out("debug: on");
            }
        }

        msg.out("[" + (taskOrder++) + "] Loading bilingual AB (" + sDicBilAB + ")");
        final DictionaryElement bil1 = DicTools.readBilingual(sDicBilAB, bilABReverse);
        msg.out("[" + (taskOrder++) + "] Loading bilingual BC (" + sDicBilBC + ")");
        final DictionaryElement bil2 = DicTools.readBilingual(sDicBilBC, bilBCReverse);
        msg.out("[" + (taskOrder++) + "] Loading monolingual A (" + sDicMonA + ")");
        final DictionaryElement mon1 = DicTools.readMonolingual(sDicMonA);
        msg.out("[" + (taskOrder++) + "] Loading monolingual C (" + sDicMonC + ")");
        final DictionaryElement mon2 = DicTools.readMonolingual(sDicMonC);

        DicSet dicSet = new DicSet(mon1, bil1, mon2, bil2);
        setDicSet(dicSet);
    }

    /**
     * @return the arguments
     */
    public final String[] getArguments() {
        return arguments;
    }

    /**
     * @param arguments
     *                the arguments to set
     */
    public final void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    /**
     * @return the dicSet
     */
    private final DicSet getDicSet() {
        return dicSet;
    }

    /**
     * @param dicSet
     *                the dicSet to set
     */
    public final void setDicSet(DicSet dicSet) {
        this.dicSet = dicSet;
    }

    /**
     * @return the msg
     */
    public final Msg getMsg() {
        return msg;
    }

    /**
     * @param msg
     *                the msg to set
     */
    public final void setMsg(Msg msg) {
        this.msg = msg;
    }

    /**
     *
     */
    public final void setOutDir(final String path) {
        this.outDir = path;
    }

    /**
     *
     */
    public final String getOutDir() {
        return this.outDir;
    }

    /**
     * 
     */
    public int getCompleted() {
        return this.completed;
    }

    /**
     * 
     */
    public final void setCompleted(int percent) {
        this.completed = percent;

        if (progressBar != null) {
            progressBar.setValue(percent);
        }
    }

    /**
     * 
     */
    public final void setNMinElements(double n) {
        this.nMinElements = n;
    }

    /**
     * 
     */
    public final double getNMinElements() {
        return this.nMinElements;
    }

    /**
     *
     */
    public final void incrementNCrossedElements() {
        this.nCrossedElements++;
        double compl = ((this.nCrossedElements / this.nMinElements)) * 100;
        int perc = (int) compl;
        this.setCompleted(perc);
    }

    /**
     * 
     */
    public final void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * 
     */
    public final String getBilCrossedPath() {
        return this.bilCrossed_path;
    }

    /**
     * 
     */
    public final String getMonACrossedPath() {
        return this.monACrossed_path;
    }

    /**
     * 
     */
    public final String getMonCCrossedPath() {
        return this.monCCrossed_path;
    }

    /**
     * This method is on process
     * @param dicSet
     * @return The common language
     */
    public final String getCommonLanguage(final DicSet dicSet) {
        // Sin tener en cuentra el orden. Se resuelve automáticamente
        if (isMetaInfoComplete(dicSet)) {
            HeaderElement d1 = this.dicSet.getMon1().getHeaderElement();
            HeaderElement d2 = this.dicSet.getMon2().getHeaderElement();
            HeaderElement d3 = this.dicSet.getBil1().getHeaderElement();
            HeaderElement d4 = this.dicSet.getBil2().getHeaderElement();

        }
        return null;
    }

    /**
     * 
     * @param dicSet
     * @return Checks whether the meta info is complete
     */
    public final boolean isMetaInfoComplete(final DicSet dicSet) {
        for (DictionaryElement dic : dicSet) {
            if (!dic.isHeaderDefined()) {
                return false;
            }
        }
        return true;
    }
}
