/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dictools;


import dics.elements.dtd.*;
import dics.elements.dtd.Par;
import dics.elements.dtd.S;
import dictools.utils.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Tool to fix a bidix' entries' e.g. nouns' gender and number so they concord with the monodices
 * Other word classes than nouns can be used
 * @author Jacob Nordfalk
 */
public class AutoconcordBidix extends AbstractDictTool {
  private static final String KEY_bidixhint="autoconcord:";


  public static void main(final String[] args) throws Exception {
    //new AutoconcordBidix().prepare(null, null, null, null, "/home/j/esperanto/a/apertium-sv-da/apertium-sv-da.sv-da.dix", null);
    new AutoconcordBidix().prepare(null, null, null, null, "/home/j/esperanto/a/incubator/apertium-eo-fr/apertium-eo-fr.eo-fr.dix", null);

    //prepareMonodixPardefs("/home/j/esperanto/a/apertium-sv-da/apertium-sv-da.da.dix", symbolPrefix);
    //prepareMonodixPardefs("/home/j/esperanto/a/apertium-sv-da/apertium-sv-da.sv.dix", symbolPrefix);
    //prepareBidixPardefs("/home/j/esperanto/a/apertium-sv-da/apertium-sv-da.sv-da.dix", symbolPrefix);
    //new AutoconcordBidix().updateBidix(null, null, null, null, "/home/j/esperanto/a/apertium-sv-da/apertium-sv-da.sv-da.dix", null);
  }

  @Override
  public String toolHelp() {
    return
        "autoconcord [-prefix symbol(s)] [-replace symbols]  [-leftMon mon1.dix] [-rightMon mon1.dix] bidix.dix [output.dix]\n"+
        "autoconcord -prepare [-leftMon mon1.dix] [-rightMon mon1.dix] bidix.dix\n\n"+
        "Automatically makes symbols (gender, number, ...) in the bidix agree with the monodices\n"+
        "in the cases where the concordance beyound doubt can be resolved automatically.\n"+
        " -leftMon and -rightMon specify the monodices file names. If not specified they will be guessed according to default naming schemes\n" +
        " -prefix Only concord entries starting with this list of comma-separated symbols. Default: -prefix n\n" +
        " -replace Replace (remove) these symbols during processing. Default: m,f,mf,ut,nt,un\n" +
        " -prepare attempts to detect and insert autoconcord data into the monodices, \n" +
        "\n" +
        "Examples:\n" +
        "autoconcord apertium-sv-da.sv-da.dix\n"+
        "autoconcord -prefix n -replace ut,nt,un apertium-sv-da.sv-da.dix apertium-sv-da.sv-da.dix.new\n"+
        "autoconcord -prepare -prefix n -replace m,f,mf,ut,nt,NUMBER:sgpl{sg+pl},NUMBER:sp apertium-sv-da.sv-da.dix\n"+
        ""
        ;
  }
// ut-nt  <-> ut
// sg-pl <-> sp
// m-f   <-> mf
// sint

  @Override
  public void executeTool() throws IOException {
    String mon1Filename=null, mon2Filename=null, bilFilename = null, output=null, prefix=null, replace=null;
    boolean prepare = false;

    for (int i=1;i<arguments.length; i++) {
      if (arguments[i].equalsIgnoreCase("-leftMon")) {
        mon1Filename = arguments[i+1];
        removeArgs(i, 2);
        i--;
      }
      else if (arguments[i].equalsIgnoreCase("-rightMon")) {
        mon2Filename = arguments[i+1];
        removeArgs(i, 2);
        i--;
      }
      else if (arguments[i].equalsIgnoreCase("-prefix")) {
        prefix = arguments[i+1];
        removeArgs(i, 2);
        i--;
      }
      else if (arguments[i].equalsIgnoreCase("-replace")) {
        replace = arguments[i+1];
        removeArgs(i, 2);
        i--;
      }
      else if (arguments[i].equalsIgnoreCase("-prepare")) {
        prepare = true;
        removeArgs(i, 1);
        i--;
      }
    }
    if (arguments.length < 2) failWrongNumberOfArguments(arguments);
    if (arguments.length > 3) failWrongNumberOfArguments(arguments);
    if (arguments.length == 3) output = arguments[2];
    bilFilename = arguments[1];


    if (!prepare) {
      updateBidix(prefix, replace, mon1Filename, mon2Filename, bilFilename, output);
    } else {
      prepare(prefix, replace, mon1Filename, mon2Filename, bilFilename, output);
    }
  }

  private void prepare(String prefix, String replace, String mon1Filename, String mon2Filename, String bilFilename, String output) throws IOException {
    if (mon1Filename == null) mon1Filename = guessMonFilenameFromBil(bilFilename, false);
    if (mon2Filename == null) mon2Filename = guessMonFilenameFromBil(bilFilename, true);
    ArrayList<S> symbolStartSequence=new ArrayList<S>();
    if (prefix!=null) {
      for (String s : prefix.split(",")) {
        symbolStartSequence.add(S.getInstance(s));
      }
    } else {
      symbolStartSequence.add(S.getInstance("n"));
    }
    if (replace==null) {
      replace="m,f,mf,ut,nt,n:sgpl{sg+pl},n:sp";
    }
    LinkedHashSet<String> hints1=prepareMonodixPardefs(mon1Filename, symbolStartSequence, replace);
    LinkedHashSet<String> hints2=prepareMonodixPardefs(mon2Filename, symbolStartSequence, replace);
    prepareBidixPardefs(bilFilename, symbolStartSequence, hints1, hints2, output);
  }



  private static String getHint(HashMap<String, LinkedHashSet<String>> sLemPardef, E e, String sLem, Dictionary sldix, HashMap<String, String> sPardefHint) {
    LinkedHashSet<String> slpardefs=sLemPardef.get(sLem);
    if (slpardefs==null) {
      //System.err.println(sldix.fileName+" does not contain lemma "+sLem);
      return null;
    }
    LinkedHashSet<String> hints = new LinkedHashSet<String>();
    for (String slpardef : slpardefs) {
      String slhint=sPardefHint.get(slpardef);
      if (slhint==null) {
        System.err.println(sldix.fileName+" does not contain hint for lemma "+sLem+" with paradigm "+slpardefs);
        return null;
      }
      hints.add(slhint);
    }
    if (hints.size() > 1) {
      System.err.println(sldix.fileName+" contains several possible paradigms "+slpardefs+" for lemma "+sLem);
      return null;
    }
    return hints.iterator().next();
  }


  private static HashMap<String, String> getPardefHintMap(Dictionary sldix) {
    HashMap<String, String> pardefHint=new HashMap<String, String>();
    for (Pardef par : sldix.pardefs.elements) {
      if (par.comment==null) {
        continue;
      }
      //System.err.println("par.comment = "+par.comment);
      int n=par.comment.indexOf(KEY_bidixhint);
      if (n<0) {
        continue;
      }
      String hint=par.comment.substring(n+KEY_bidixhint.length()).split(";")[0];
      //System.err.println("Paradigm "+par.name+" - hint = "+hint);
      pardefHint.put(par.name, hint);
    }
    return pardefHint;
  }


  private static String SEVERAL_PARADIGMS = "SEVERAL_PARADIGMS";
  private static HashMap<String, LinkedHashSet<String>> getLemmaPardefMap(Dictionary sldix, ArrayList<S> symbolPrefix) {
//    HashMap<String, String> pardefHint=getPardefHintMap(sldix);
    HashSet<String> goodPardefs=new HashSet<String>();
    pardef:
    for (Pardef par : sldix.pardefs.elements) {
      for (E e : par.elements) {
        if (!startsWith(e.getSymbols("R"), symbolPrefix)) continue pardef;
      }
      goodPardefs.add(par.name);
    }

    //System.err.println("symbolPrefix = " + symbolPrefix + " gives goodPardefs = " + goodPardefs);

    HashMap<String, LinkedHashSet<String>> lemmaHint=new HashMap<String, LinkedHashSet<String>>();
    for (Section s : sldix.sections) {
      element:
      for (E e : s.elements) {
        for (DixElement de : e.children) {
          if (de instanceof Par) {
            Par par = (Par) de;
            if (!goodPardefs.contains(par.name)) continue element;

            LinkedHashSet<String> set = lemmaHint.get(e.lemma);
            if (set ==null) {
              lemmaHint.put(e.lemma, set = new LinkedHashSet<String>());
            }
            set.add(par.name);
          }
        }
      }
    }

    return lemmaHint;
  }


  /**
   * Updates/creates pardefs with autoconcord hints
   */
  public void prepareBidixPardefs(String fn, ArrayList<S> symbolPrefix,
      LinkedHashSet<String> hints1, LinkedHashSet<String> hints2, String out) throws IOException {

    Dictionary bidix = new DictionaryReader(fn).readDic();
    LinkedHashMap<String, Pardef> pardefMap = new LinkedHashMap<String,Pardef>();
    for (Pardef pd : bidix.pardefs.elements) pardefMap.put(pd.name, pd);


    for (String hint1 : hints1) {
      String[] ha1 = hint1.split(",");
      for (String hint2 : hints2)
      {
          String[] ha2 = hint2.split(",");
          if (ha1.length != ha2.length) continue;
          for (int i=0; i<ha2.length; i++) {
            String name = "_"+ha1[i]+"_"+ha2[i];
            Pardef pd = pardefMap.get(name);
            if (pd == null) {
              pardefMap.put(name, pd = new Pardef(name));
              E e = new E();
              e.comment = "TODO: correct entry here";
              pd.elements.add(e);
              bidix.pardefs.elements.add(pd);
            }
            pd.comment = KEY_bidixhint+ha1[i]+"-"+ha2[i];
          }
      }
    }
    
    if (out==null) out = fn+".new";
    bidix.printXMLToFile(out, opt);
  }


  public LinkedHashSet<String> prepareMonodixPardefs(String fn, ArrayList<S> symbolPrefix, String replace) throws IOException {
    Dictionary sldix = new DictionaryReader(fn).readDic();
    LinkedHashSet<String> hints = new LinkedHashSet<String>();
    paradigm:
    for (Pardef par : sldix.pardefs.elements) {
      //if (par.comment!=null)  continue;
      
      HashSet<S> symbols = new LinkedHashSet<S>();
      for (E e : par.elements) {
        ArrayList<S> sym = e.getSymbols("R");
        boolean startsWith=startsWith(sym, symbolPrefix);
        if (!startsWith) continue paradigm;
        symbols.addAll(sym.subList(symbolPrefix.size(), sym.size()));
      }

      System.err.println("Paradigm "+par.name+" has symbols: " + symbols);
      // replace="m,f,mf,ut,nt,NUMBER:sgpl{sg+pl},NUMBER:sp";
      // syntax is here [key:]value and last value for each keys is the winner
      LinkedHashMap<String, String> syms = new LinkedHashMap<String,String>();
      for (String ps : replace.split(",")) {

        //System.err.println("ps = " + ps);
        String[] p = ps.split(":");
        String key = p.length==1? "GENDER" : p[0];

        //System.err.println("key = " + key + " for "+ps);
        String[] symcomplex = (p.length==1? p[0] : p[1]).split("[\\{\\+\\}]");
        // symcomplex[0] now contains the symbol or its alias (e.g. sgpl)
        for (String sym : symcomplex) {

          //System.err.println("sym = " + sym);
          if (symbols.contains(S.getInstance(sym))) syms.put(key, symcomplex[0]);
        }
      }
      System.err.println("syms = " + syms);
      if (syms.isEmpty()) continue;
      HashSet<String> genders = new HashSet<String>();

      /*
      boolean nt = symbols.contains(S.getInstance("nt"));
      boolean ut = symbols.contains(S.getInstance("ut"));
      boolean sp = symbols.contains(S.getInstance("sp"));

      if (nt && ut || !nt && !ut) {
        System.err.println("hmm... igoring " + par.name + " as I can't figure hint from "+symbols);
        continue;
      }
      String hint =  (nt?"nt":"ut") + "," + (sp?"sp":"sgpl");
       */
      String hint = "";
      for (String sym : syms.values()) hint += ","+sym;
      hint = hint.substring(1);
      hints.add(hint);

      //System.err.println("hint = " + hint + " for "+par);
      par.comment = KEY_bidixhint+hint;
      //System.err.println("par.comment = " + par.comment);
    }
    sldix.printXMLToFile(fn+".new", opt);

    System.err.println("hints = " + hints);
    return hints;
  }

  private static boolean startsWith(List<S> sym, List<S> symbolPrefix) {
    if (sym.size()<symbolPrefix.size()) {
      return false;
    }
    if (!sym.subList(0, symbolPrefix.size()).equals(symbolPrefix)) {
      return false;
    }
    return true;
  }

  public void updateBidix(String prefix, String replace, String mon1Filename, String mon2Filename, String bilFilename, String bilOutFilename) throws IOException {
    ArrayList<S> symbolStartSequence = new ArrayList<S>();
    if (prefix!=null) for (String s : prefix.split(",")) symbolStartSequence.add(S.getInstance(s));
    else symbolStartSequence.add(S.getInstance("n"));

    Set<DixElement> symbolsAndParsToReplace = new HashSet<DixElement>();
    if (replace==null) replace="m,f,mf,ut,nt,un";
    for (String s : replace.split(",")) symbolsAndParsToReplace.add(S.getInstance(s));


    msg.log(" ============== updateBidix() =============");
    if (mon1Filename == null) mon1Filename = guessMonFilenameFromBil(bilFilename, false);
    Dictionary sldix = new DictionaryReader(mon1Filename).readDic();
    HashMap<String, String> sPardefHint=getPardefHintMap(sldix);
    HashMap<String, LinkedHashSet<String>> sLemPardef=getLemmaPardefMap(sldix, symbolStartSequence);
    
    if (mon2Filename == null) mon2Filename = guessMonFilenameFromBil(bilFilename, true);
    Dictionary tldix = new DictionaryReader(mon2Filename).readDic();
    HashMap<String, String> tPardefHint=getPardefHintMap(tldix);
    HashMap<String, LinkedHashSet<String>> tLemPardef=getLemmaPardefMap(tldix, symbolStartSequence);

    Dictionary bidix = new DictionaryReader(bilFilename).readDic();
    HashMap<String, Par> biHintPardefMap = new HashMap<String, Par>();
    HashMap<String, String> bipardefHintMap = getPardefHintMap(bidix);

    // we need a map from name to pardef to be able to expand them
    HashMap<String, Pardef> bipardefnamePardefMap = new HashMap<String, Pardef>();
    for (Pardef par : bidix.pardefs.elements) bipardefnamePardefMap.put(par.name, par);

    LinkedHashSet<Par> biparsToBeExpanded = new LinkedHashSet<Par>();

    Par PAR_OMIT = new Par("OMIT");
    for (String parname : bipardefHintMap.keySet()) {
      String[] hint = bipardefHintMap.get(parname).split("/");
      if (hint.length==1) {
        biHintPardefMap.put(hint[0], new Par(parname));
      } else if (hint[1].equalsIgnoreCase("omit")) {
        biHintPardefMap.put(hint[0], PAR_OMIT);
      } else if (hint[1].equalsIgnoreCase("expand")) {
        Par par = new Par(parname);
        biHintPardefMap.put(hint[0], par);
        Pardef pardef = bipardefnamePardefMap.get(par.name);
        if (pardef.elements.size() != 1) {
          msg.log("Refusing to expand paradigm " + par.name+" as it doesent contain exactly 1 entry");
        } else {
          biparsToBeExpanded.add(par);
        }
      } else {
        Par par = new Par(parname);
        biHintPardefMap.put(hint[0], par);
        System.err.println("Ignoring unrecognized option " + hint[1] +" to hint in "+ par.name);
      }
    }

    msg.log("bipardefHintMap = " + bipardefHintMap);
    msg.log("biHintPardefMap = " + biHintPardefMap);
    //System.err.println("slHints = " + slHints);
    //System.err.println("tlHints = " + tlHints);


    // autoadded pars are always eligible for replacement
    symbolsAndParsToReplace.addAll(biHintPardefMap.values());


    for (Section s : bidix.sections) {
      entry:
      for (E e : s.elements) {
        if (E.maskNull(e.comment).contains("noconcord")) continue;

        // ignore entries not starting with correct symbols
        ArrayList<S> lSym = e.getSymbols("L");
        if (!startsWith(lSym,symbolStartSequence)) continue;

        ArrayList<S> rSym = e.getSymbols("R");
        if (!startsWith(rSym,symbolStartSequence)) continue;

        rSym.removeAll(symbolStartSequence);
        lSym.removeAll(symbolStartSequence);

        // see if the entry uses only symbols/paradigms contained in symbolsAndParsToReplace
        lSym.removeAll(symbolsAndParsToReplace);
        rSym.removeAll(symbolsAndParsToReplace);
        //System.err.println("syms = " + lSym+ " / " + rSym);
        if (!lSym.isEmpty() || !rSym.isEmpty()) continue; // alien symbol found -  don't touch

        for (DixElement de : e.children) {
          if (de instanceof Par && !symbolsAndParsToReplace.contains(de)) {

            msg.log("alien par found in entry "+e+"  won't touch");
              continue entry;// alien par found  -  don't touch
          }
        }

          //System.err.println("2syms = " + lSym+ " / " + rSym);

          // OK, regenerate all paradigms to ensure concordance
        String e0 = e.toString();
//        ElementList e_children = new ElementList(e.children);

        String slhint=getHint(sLemPardef, e, e.getLemmaForSide("L"), sldix, sPardefHint);
        String tlhint=getHint(tLemPardef, e, e.getLemmaForSide("R"), tldix, tPardefHint);
        if (slhint==null || tlhint==null) {
          //System.err.println("Missing hints for " +e+ ": source: "+ slhint+"  target: "+tlhint);
          continue;
        }

        if (slhint.split(",").length!=tlhint.split(",").length) {
          msg.log("Hints for " +e+ " does not have same number of elements: "+ slhint+" "+tlhint);
          continue;
        }


        // add new pars (and add symbols instead if par is to be expanded)
        // create new par list
        ArrayList<Par> newPars = new ArrayList<Par>();
        for (int n=0; n<slhint.split(",").length; n++) {
          String slp = slhint.split(",")[n];
          String tlp = tlhint.split(",")[n];
          String hintKey = slp+"-"+tlp;
          Par par = biHintPardefMap.get(hintKey);
          if (par==null) {
            msg.log("hintKey = " + hintKey+" gave no par! biHintPardefMap="+biHintPardefMap);
            continue entry;
          }
          if (par != PAR_OMIT) newPars.add(par);
        }

        // remove all old pars and symbols from entry
        P lastP = null;
        for (Iterator<DixElement> dei = e.children.iterator(); dei.hasNext(); ) {
          DixElement de = dei.next();
          if (de instanceof Par) {
            dei.remove();
          } else if (de instanceof I) {
            for (Iterator<DixElement> dei2 = ((I) de).children.iterator(); dei2.hasNext(); ) {
              DixElement de2 = dei2.next();
              if (symbolsAndParsToReplace.contains(de2)) {
                dei2.remove();
              }
            }
          } else if (de instanceof P) {
            lastP = (P) de;
            for (Iterator<DixElement> dei2 = ((P) de).l.children.iterator(); dei2.hasNext(); ) {
              DixElement de2 = dei2.next();
              if (symbolsAndParsToReplace.contains(de2)) {
                dei2.remove();
              }
            }
            for (Iterator<DixElement> dei2 = ((P) de).r.children.iterator(); dei2.hasNext(); ) {
              DixElement de2 = dei2.next();
              if (symbolsAndParsToReplace.contains(de2)) {
                dei2.remove();
              }
            }
          }
        }

        if (lastP == null) {
          lastP = new P(new L(), new R());
          e.children.add(lastP);
        }

        for (Par par : newPars) {
          if (biparsToBeExpanded.contains(par)) {
            Pardef pardef = bipardefnamePardefMap.get(par.name);
            lastP.l.children.addAll(pardef.elements.get(0).getSymbols("L"));
            lastP.r.children.addAll(pardef.elements.get(0).getSymbols("R"));
          }else {
            e.children.add(par);
          }
        }


        String e1 = e.toString();
        if (!e0.equals(e1)) {
          msg.log("before: " + e0);
          msg.log("now:    " + e1);
        }
      }
    }
    if (bilOutFilename==null) bilOutFilename = bidix.fileName+".new";
    bidix.printXMLToFile(bilOutFilename, opt);
  }

  /**
   * Guess a monodix' name from the bidix
   * @param bil filename of bidix
   * @param leftFalse_rightTrue false means left (source), true means right (target)
   * @return file name of monodix
   */
  private static String guessMonFilenameFromBil(String bil, boolean leftFalse_rightTrue) {
      String[] split = bil.split("\\.");
      String direction =  split[1]; // Gives eo-en for ex "apertium-eo-en.eo-en.dix"
      String mon = split[0]+'.'+direction.split("-")[ leftFalse_rightTrue?1:0 ]+'.'+split[2];
      if (new File(mon).exists()) return mon;
      if (split.length>3) {
        mon += split[3];
      }
      if (new File(mon).exists()) return mon;
      System.err.println("Could not guess monodix from bidix file name ("+bil+") - expected "+mon);
      return mon;
  }

  /*
  private static boolean removeSymbols(ContentElement contentElement, HashSet<S> removeSymbols) {
    boolean removed = false;
    for (Iterator<DixElement>dei = contentElement.children.iterator(); dei.hasNext();) {
      DixElement de = dei.next();
      if (de instanceof S && removeSymbols.contains(de))
        dei.remove();
        removed = true;
    }
    return removed;
  }
   */
}


/*
  private static Iterable<DixElement> flatList(final ElementList children) {
    return new Iterable<DixElement>() {

      @Override
      public Iterator<DixElement> iterator() {
        final Iterator<DixElement> iterator = children.iterator();
        return new Iterator<DixElement>() {

          Iterator<DixElement> iterator2 = null;
          @Override
          public boolean hasNext() {
            if (iterator2 != null && iterator2.hasNext()) return true;
            return iterator.hasNext();
          }

          @Override
          public DixElement next() {
            if (iterator2 != null) {
              DixElement de = iterator2.next();
              if (!iterator2.hasNext()) iterator2 = null;
              return de;
            }
            throw new UnsupportedOperationException("Not supported yet.");
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
          }

        }
      }

    };
  }
*/
