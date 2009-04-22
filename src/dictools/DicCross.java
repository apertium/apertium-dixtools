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

import dics.elements.utils.DicTools;
import dictools.xml.DictionaryReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import dics.elements.dtd.AlphabetElement;
import dics.elements.dtd.BElement;
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
import dics.elements.utils.DicOpts;
import dics.elements.utils.DicSet;
import dics.elements.utils.DictionaryElementList;
import dics.elements.utils.EElementList;
import dics.elements.utils.EElementMap;
import dics.elements.utils.EHashMap;
import dics.elements.utils.Msg;
import dics.elements.utils.SElementList;
import dictools.crossmodel.Action;
import dictools.crossmodel.CrossAction;
import dictools.crossmodel.CrossModel;
import dictools.crossmodel.CrossModelReader;
import dictools.crossmodel.Pattern;
import dictools.cmproc.CrossActionData;
import dictools.cmproc.CrossModelProcessor;
import dictools.cmproc.Variables;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JProgressBar;
import lingres.LingResources;
import lingres.LingResourcesReader;
import lingres.Resource;

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
    private CrossModelProcessor crossModelProcessor;
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
    private void readCrossModel() {
        try {
            msg.out("[" + (taskOrder++) + "] Reading cross model (" + getCrossModelFileName() + ") ...\n");
            CrossModelReader cmr = new CrossModelReader(getCrossModelFileName());
            if (cmr == null) {
                System.err.println("cmr es null");
            }
            CrossModel cm = cmr.readCrossModel();
            msg.out("[" + (taskOrder++) + "] Validating cross model (" + getCrossModelFileName() + ") ... ");
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
                setCrossModel(cm);
                msg.out("[" + (taskOrder++) + "] Processing patterns in cross model (" + getCrossModelFileName() + ") ... ");
                msg.out(" (" + cm.getCrossActions().size() + " patterns processed).\n");
                CrossModelProcessor cmp = new CrossModelProcessor(this.getCrossModel(), msg);
                this.setCrossModelProcessor(cmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.err("Error reading cross model");
            System.exit(-1);
        }
    }

    /**
     *
     *
     */
    private void fillOutRestrictionMatrix() {
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
    public DictionaryElement[] crossDictionaries(DicSet dicSet) {
        DictionaryElement[] dics = new DictionaryElement[2];
        readCrossModel();

        DictionaryElement dic1 = dicSet.getBil1();
        DictionaryElement dic2 = dicSet.getBil2();

        int nDic1 = dic1.getEntries().size();
        int nDic2 = dic2.getEntries().size();

        this.setNMinElements(nDic1 + nDic2);

        setBilAB(dic1);
        setBilBC(dic2);

        DictionaryElement dic = new DictionaryElement();

        // encoding
        String encoding = crossXmlEncodings(dic1.getXmlEncoding(), dic2.getXmlEncoding());
        dic.setXmlEncoding(encoding);

        // alphabet
        AlphabetElement alphabet = crossAlphabets(dic1.getAlphabet(), dic2.getAlphabet());
        dic.setAlphabet(alphabet);

        // sdefs
        SdefsElement sdefs = crossSdefs(dic1.getSdefs(), dic2.getSdefs());
        dic.setSdefs(sdefs);

        // sections
        for (SectionElement section1 : dic1.getSections()) {
            SectionElement section2 = dic2.getSection(section1.getID());
            SectionElement[] sections = crossSections(section1, section2, sdefs);
            SectionElement section = sections[0];
            dic.addSection(section);
        }

        msg.out("[" + (taskOrder++) + "] Sorting crossed dictionary...\n");
        Collections.sort(dic.getEntries(), EElement.eElementComparatorL);

        getNDCrossModel().printXML(this.getOutDir() + "patterns-not-detected.xml",getOpt());
        dics[0] = dic;
        return dics;
    }

    /**
     *
     * @param encoding1
     * @param encoding2
     */
    private String crossXmlEncodings(String encoding1, String encoding2) {
        return "UTF-8";
    }

    /**
     *
     * @param alphabet1
     * @param alphabet2
     * @return Undefined     
     */
    private AlphabetElement crossAlphabets(AlphabetElement alphabet1, AlphabetElement alphabet2) {
        AlphabetElement alphabet = new AlphabetElement();
        if (alphabet2 != null && alphabet2 != null) {


            String a1 = alphabet1.getAlphabet();
            String a2 = alphabet2.getAlphabet();

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
    private SdefsElement crossSdefs(SdefsElement sdefs1, SdefsElement sdefs2) {
        SdefsElement sdefs = new SdefsElement();
        if (sdefs1 != null && sdefs2 != null) {
            msg.out("[" + (taskOrder++) + "] Crossing definitions...\n");

            HashMap<String, SdefElement> sdefList = new HashMap<String, SdefElement>();

            for (SdefElement sdef1 : sdefs1.getSdefsElements()) {
                if (!sdefList.containsKey(sdef1.getValue())) {
                    sdefList.put(sdef1.getValue(), sdef1);
                }
            }
            for (SdefElement sdef2 : sdefs2.getSdefsElements()) {
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
                String str = it.next();
                SdefElement sdef = sdefList.get(str);
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
    private SectionElement[] crossSections(SectionElement section1, SectionElement section2, SdefsElement sdefs) {

        msg.out("[" + (taskOrder++) + "] Crossing sections '" + section1.getID() + "' and '" + section2.getID() + "'\n");
        SectionElement[] sections = new SectionElement[2];

        SectionElement section = new SectionElement();
        section.setID(section1.getID());
        section.setType(section1.getType());
        EElementList elements1 = section1.getEElements();
        EElementList elements2 = section2.getEElements();
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
    private void crossSectionsAB(EElementList elements, EElementMap sectionMap, SectionElement section, DictionaryElement bil, int dir) {
        for (EElement e : elements) {
            if (e.isRegEx()) {
                String key = e.getRegEx().getValue();
                if (!getRegExProcessed().containsKey(key)) {
                    section.addEElement(e);
                    getRegExProcessed().put(e.getRegEx().getValue(), e);
                }
            } else {
                EElementList candidates = getPairs(e, sectionMap);
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
    private void crossElementAndPairs(EElement e1, EElementList candidates, SectionElement section, int dir) {
        try {
            for (EElement e2 : candidates) {
                EElementList actionEList = cross(e1, e2, dir);
                if (!actionEList.isEmpty()) {
                    for (EElement e : actionEList) {
                        String str = e.getHash();

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
    private EElementList cross(EElement e1, EElement e2, int dir) {
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
        CrossActionData cad = getCrossModelProcessor().getBestActionSet(crossAction);
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
    private EElementList applyCrossAction(EElement e1, EElement e2, CrossActionData cad) {
        EElementList elementList = new EElementList();
        CrossAction cA = cad.getCrossAction();

        for (Action action : cA.getActionSet()) {
            EElement eAction = action.getE();
            int iR = resolveRestriction(e1.getRestriction(), e2.getRestriction());
            if (iR != NONE) {
                EElement actionE = assignValues(e1, e2, eAction, cad.getVars());
                String actionID = cad.getCrossAction().getId();
                actionE.setPatternApplied(actionID);

                // author attribute
                //String author = mergeAttributes(e1.getAuthor(), e2.getAuthor());
                //actionE.setAuthor(author);

                // comment attribute
                String comment = mergeAttributes(e1.getComment(), e2.getComment());
                actionE.setComment(comment);

                // alt attribute
                String alt = mergeAttributes(e1.getAlt(), e2.getAlt());
                actionE.setAlt(alt);

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
    public EElement assignValues(EElement e1, EElement e2, EElement eAction, Variables vars) {
        EElement eCrossed = new EElement();
        //if (eAction.hasRestriction()) {
        if (!eAction.isRestrictionAuto()) {
            // restriction indicated in cross pattern
            eCrossed.setProcessingComments("\tforced '" + eAction.getRestriction() + "' restriction\n");
            eCrossed.setRestriction(eAction.getRestriction());
        } else {
            // automatically resolved restriction
            int iR = resolveRestriction(e1.getRestriction(), e2.getRestriction());
            String restriction = getRestrictionString(iR);
            eCrossed.setRestriction(restriction);
        }

        PElement pE = new PElement();
        ContentElement lE = eAction.getSide("L");
        ContentElement rE = eAction.getSide("R");
        LElement lE2 = new LElement();
        RElement rE2 = new RElement();

        assignValuesSide(lE2, lE, vars);
        assignValuesSide(rE2, rE, vars);

        pE.setLElement(lE2);
        pE.setRElement(rE2);
        eCrossed.addChild(pE);
        return eCrossed;
    }

    /**
     * 
     * @param ceWrite
     * @param ceRead
     * @param vars
     */
    private void assignValuesSide(ContentElement ceWrite, ContentElement ceRead, Variables vars) {
        for (Element e : ceRead.getChildren()) {
            if (e instanceof TextElement) {
                String x = e.getValue();
                if (vars.containsKey(x)) {
                    String sx = (String) vars.get(x);
                    ceWrite.addChild(new TextElement(sx));
                } else {
                    ceWrite.addChild(new TextElement(x));
                }
            }
            if (e instanceof BElement) {
                ceWrite.addChild(new BElement());
            }

            if (e instanceof SElement) {
                String x = ((SElement) e).getValue();
                if (vars.containsKey(x)) {
                    if (x.startsWith("X")) {
                        String sx = (String) vars.get(x);
                        ceWrite.addChild(new SElement(sx));
                    }
                    if (x.startsWith("S")) {
                        SElementList sxlist = (SElementList) vars.get(x);
                        for (SElement sE : sxlist) {
                            ceWrite.addChild(new SElement(sE));
                        }

                    }
                } else {
                    ceWrite.addChild(new SElement(e.getValue()));
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
    private EElementList getPairs(EElement e, EElementMap hm) {
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
     * @param r
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
     *
     * @return Undefined     */
    private CrossModel getCrossModel() {
        return crossModel;
    }

    /**
     *
     * @param crossModel
     */
    public void setCrossModel(CrossModel crossModel) {
        this.crossModel = crossModel;
    }

    /**
     * @return the processed
     */
    private EHashMap getProcessed() {
        return processed;
    }

    /**
     * @return the regExProcessed
     */
    private EHashMap getRegExProcessed() {
        return regExProcessed;
    }

    /**
     * @return the bilAB
     */
    private DictionaryElement getBilAB() {
        return bilAB;
    }

    /**
     * @param bilAB
     *                the bilAB to set
     */
    private void setBilAB(DictionaryElement bilAB) {
        this.bilAB = bilAB;
    }

    /**
     * @return the bilBC
     */
    private DictionaryElement getBilBC() {
        return bilBC;
    }

    /**
     * @param bilBC
     *                the bilBC to set
     */
    private void setBilBC(DictionaryElement bilBC) {
        this.bilBC = bilBC;
    }

    /**
     * @return the rMatrix
     */
    private int[][] getRMatrix() {
        return rMatrix;
    }

    /**
     *
     * @param i
     * @param j
     * @param value
     */
    private void setRMatrixValue(int i, int j, int value) {
        getRMatrix()[i][j] = value;
    }

    /**
     * @return the crossModelFileName
     */
    private String getCrossModelFileName() {
        return crossModelFileName;
    }

    /**
     * @param crossModelFileName
     *                the crossModelFileName to set
     */
    public void setCrossModelFileName(String crossModelFileName) {
        this.crossModelFileName = crossModelFileName;
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

    /**
     * @return the nDCrossModel
     */
    private CrossModel getNDCrossModel() {
        return nDCrossModel;
    }

    /**
     *
     *
     */
    public void doCross() {
            new File("dix").mkdir();

        processArguments();
        actionCross();
    }

    /**
     * 
     */
    public void actionCross() {
        DicSet dicSet = getDicSet();
        actionConsistent(dicSet, "yes");
        setCrossModelFileName(getCrossModelFileName());

        DictionaryElement[] bils = crossDictionaries(dicSet);
        DictionaryElement bilCrossed = bils[0];
        String sl = dicSet.getBil1().getRightLanguage();
        String tl = dicSet.getBil2().getRightLanguage();
        bilCrossed.setType(DictionaryElement.BIL);
        bilCrossed.setLeftLanguage(sl);
        bilCrossed.setRightLanguage(tl);
        msg.out("[" + (taskOrder++) + "] Making consistent morphological dicionaries ...\n");

        EElementList[] commonCrossedMons = DicTools.makeConsistent(bilCrossed, dicSet.getMon1(), dicSet.getMon2());
        EElementList crossedMonA = commonCrossedMons[0];
        EElementList crossedMonB = commonCrossedMons[1];

        DictionaryElement monACrossed = new DictionaryElement(dicSet.getMon1());
        monACrossed.setMainSection(crossedMonA);
        DictionaryElement monBCrossed = new DictionaryElement(dicSet.getMon2());
        monBCrossed.setMainSection(crossedMonB);

        DictionaryElementList dicList = new DictionaryElementList();
        dicList.add(bilCrossed);
        dicList.add(monACrossed);
        dicList.add(monBCrossed);
        printXMLCrossed(dicList, sl, tl,getOpt());
        msg.out("[" + (taskOrder++) + "] Done!\n");
        this.setCompleted(100);
    }

    /**
     *
     * @param del
     * @param sl
     * @param tl
     */
    private void printXMLCrossed(DictionaryElementList del, String sl, String tl, DicOpts opt) {
        DictionaryElement bilCrossed = del.get(0);
        DictionaryElement monACrossed = del.get(1);
        DictionaryElement monBCrossed = del.get(2);

        int i = 0;
        String patterns = "";
        bilCrossed.addProcessingComment("");
        bilCrossed.addProcessingComment("Patterns applied:");
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
                bilCrossed.addProcessingComment(mesg);
            }
        }
        taskOrder = taskOrder + 1;
        msg.log("[" + (taskOrder) + "] Patterns never applied: " + patterns + "\n");
        msg.out("[" + (taskOrder) + "] Generating crossed dictionaries ...\n");
        taskOrder = taskOrder + 1;
        this.bilCrossed_path = this.getOutDir() + "apertium-" + sl + "-" + tl + "." + sl + "-" + tl + "-crossed.dix";
        bilCrossed.printXML(this.bilCrossed_path, opt);

        this.monACrossed_path = this.getOutDir() + "apertium-" + sl + "-" + tl + "." + sl + "-crossed.dix";
        monACrossed.printXML(this.monACrossed_path, opt);

        this.monCCrossed_path = this.getOutDir() + "apertium-" + sl + "-" + tl + "." + tl + "-crossed.dix";
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
        dicSet.printXML(this.getOutDir() + "consistent",getOpt());
        return dicConsistent;
    }

    /**
     *
     * @param arguments
     */
    private void processArguments() {
        int nArgs = getArguments().length;
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
            String arg = getArguments()[i];

            if (arg.equals("-f")) {
                i++;
                arg = getArguments()[i];
                source = arg;
                type = LingResourcesReader.FILE;
                i++;
                sltl = getArguments()[i];
            }

            if (arg.equals("-url")) {
                i++;
                arg = getArguments()[i];
                source = arg;
                type = LingResourcesReader.URL;
                i++;
                sltl = getArguments()[i];
            }

            if (arg.equals("-monA")) {
                i++;
                arg = getArguments()[i];
                sDicMonA = arg;
            }

            if (arg.equals("-monC")) {
                i++;
                arg = getArguments()[i];
                sDicMonC = arg;
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
            }

            if (arg.equals("-cross-model")) {
                i++;
                arg = getArguments()[i];
                setCrossModelFileName(arg);
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
            setDicSet(theDicSet);
        } else {
            msg.out("[" + (taskOrder++) + "] Loading bilingual AB (" + sDicBilAB + ")\n");
            DictionaryElement bil1 = DicTools.readBilingual(sDicBilAB, bilABReverse);
            msg.out("[" + (taskOrder++) + "] Loading bilingual BC (" + sDicBilBC + ")\n");
            DictionaryElement bil2 = DicTools.readBilingual(sDicBilBC, bilBCReverse);
            msg.out("[" + (taskOrder++) + "] Loading monolingual A (" + sDicMonA + ")\n");
            DictionaryElement mon1 = DicTools.readMonolingual(sDicMonA);
            msg.out("[" + (taskOrder++) + "] Loading monolingual C (" + sDicMonC + ")\n");
            DictionaryElement mon2 = DicTools.readMonolingual(sDicMonC);
            DicSet theDicSet = new DicSet(mon1, bil1, mon2, bil2);
            setDicSet(theDicSet);
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

        DictionaryElement mA = null;
        DictionaryElement mC = null;
        DictionaryElement bAB = null;
        DictionaryElement bBC = null;

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
                    this.setCrossModelFileName(r.getSource());
                    CMok = true;
                }
                if (r.isMorphological() && r.isSL(sl)) {
                    if (mAok) {
                        msg.out("[!] Warning: alternative linguistic resource found (" + r.getSource() + ")\n");
                    } else {
                        msg.out("[" + (taskOrder++) + "] Loading monolingual A (" + r.getSource() + ")");
                        mA = this.readDic(r.getSource(), 0, false);
                        msg.out(" [" + mA.getNumberOfEntries() + "] entries\n");
                        mA.setLeftLanguage(sl);
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
                        mC.setLeftLanguage(tl);
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
                        bAB.setRightLanguage(sl);
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
                        bAB.setRightLanguage(sl);
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
                        bBC.setRightLanguage(tl);
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
                        bBC.setRightLanguage(tl);
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

        DictionaryElement mAn = getMainSectionDic(mA);
        mAn = addMissingLemmas(mAn);
        DictionaryElement bABn = getMainSectionDic(bAB);
        DictionaryElement bBCn = getMainSectionDic(bBC);
        DictionaryElement mCn = getMainSectionDic(mC);
        mCn = addMissingLemmas(mCn);

        DicSet dicSetC = new DicSet(mAn, bABn, mCn, bBCn);
        return dicSetC;
    }

    /**
     * 
     * @param dic
     * @return Dictionary with missing lemmas
     */
    public static DictionaryElement addMissingLemmas(DictionaryElement dic) {
        int c = 0;
        try {
            for (SectionElement s : dic.getSections()) {
                if (s.getType().equals("standard")) {
                    for (EElement ee : s.getEElements()) {
                        if (ee.getLemma() == null) {
                            String v = ee.getValueNoTags("L");
                            if (v != null) {
                                String pv = ee.getMainParadigmName();
                                if (pv != null) {
                                    String[] parts = pv.split("/");
                                    if (parts.length > 1) {
                                        String[] parts2 = parts[1].split("__");
                                        String suffix = parts2[0];
                                        String nLemma = v + suffix;
                                        c++;
                                        ee.setLemma(nLemma);
                                    } else {
                                        c++;
                                        ee.setLemma(v);
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
        //msg.out("[-] " + c + " missing 'lm' atrributes generated\n");
        return dic;
    }

    /**
     * 
     * @param dic
     * @return
     */
    private DictionaryElement getMainSectionDic(DictionaryElement dic) {
        ArrayList<SectionElement> sections = new ArrayList<SectionElement>();
        SectionElement mainSection = new SectionElement("main", "standard");
        for (SectionElement s : dic.getSections()) {
            if (s.getType().equals("standard")) {
                for (EElement ee : s.getEElements()) {
                    mainSection.addEElement(ee);
                }
            } else {
                sections.add(s);
            }
        }
        sections.add(mainSection);
        dic.setSections(sections);
        return dic;
    }

    /**
     * 
     * @param source
     * @return
     */
    private DictionaryElement readDic(String source, int type, boolean reverse) {
        DictionaryElement dic = null;
        if (source.startsWith("http://")) {
            try {
                URL url = new URL(source);
                InputStream is = url.openStream();
                DictionaryReader dicReader = new DictionaryReader();
                dicReader.setUrlDic(true);
                dicReader.setIs(is);
                dic = dicReader.readDic();
                dic.setFileName(source);
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
            dic.setFileName(source);
        }
        return dic;
    }


    /**
     * @return the dicSet
     */
    private DicSet getDicSet() {
        return dicSet;
    }

    /**
     * @param dicSet
     *                the dicSet to set
     */
    public void setDicSet(DicSet dicSet) {
        this.dicSet = dicSet;
    }

    /**
     *
     */
    public void setOutDir(String path) {
        this.outDir = path;
    }

    /**
     *
     */
    public String getOutDir() {
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
    public void setCompleted(int percent) {
        this.completed = percent;

        if (progressBar != null) {
            progressBar.setValue(percent);
        }
    }

    /**
     * 
     */
    public void setNMinElements(double n) {
        this.nMinElements = n;
    }

    /**
     * 
     */
    public double getNMinElements() {
        return this.nMinElements;
    }

    /**
     *
     */
    public void incrementNCrossedElements() {
        this.nCrossedElements++;
        double compl = ((this.nCrossedElements / this.nMinElements)) * 100;
        int perc = (int) compl;
        this.setCompleted(perc);
    }

    /**
     * 
     */
    public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * 
     */
    public String getBilCrossedPath() {
        return this.bilCrossed_path;
    }

    /**
     * 
     */
    public String getMonACrossedPath() {
        return this.monACrossed_path;
    }

    /**
     * 
     */
    public String getMonCCrossedPath() {
        return this.monCCrossed_path;
    }

    /**
     * This method is on process
     * @param dicSet
     * @return The common language
     */
    public String getCommonLanguage(DicSet dicSet) {
        // Sin tener en cuentra el orden. Se resuelve automáticamente
        if (isMetaInfoComplete(dicSet)) {
            Vector<DictionaryElement> dics = new Vector<DictionaryElement>();
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
    public boolean isMetaInfoComplete(DicSet dicSet) {
        for (DictionaryElement dic : dicSet) {
            if (!dic.isHeaderDefined()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @return The cross model processor
     */
    public CrossModelProcessor getCrossModelProcessor() {
        return crossModelProcessor;
    }

    /**
     * 
     * @param crossModelProcessor
     */
    public void setCrossModelProcessor(CrossModelProcessor crossModelProcessor) {
        this.crossModelProcessor = crossModelProcessor;
    }
}
