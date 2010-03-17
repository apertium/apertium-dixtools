/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dictools;


import dics.elements.dtd.*;
import dics.elements.dtd.S;
import dictools.utils.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Tool to fix a bidix' entries' e.g. nouns' gender and number so they concord with the monodices
 * Other word classes than nouns can be used
 * @author Jacob Nordfalk
 */
public class AutoconcordBidix {
  private static final String KEY_bidixhint="autoconcord:";

  private static String getHint(HashMap<String, LinkedHashSet<String>> sLemPardef, E e, String sLem, Dictionary sldix, HashMap<String, String> sPardefHint) {
    LinkedHashSet<String> slpardefs=sLemPardef.get(sLem);
    if (slpardefs==null) {
      System.err.println(sldix.fileName+" does not contain lemma "+sLem);
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
      System.err.println(sldix.fileName+" contains several possible hints: "+hints+" for lemma "+sLem+" with paradigms "+slpardefs);
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
      System.err.println("Paradigm "+par.name+" - hint = "+hint + (hint.endsWith("*")?" (omittable)":""));
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

    System.err.println("symbolPrefix = " + symbolPrefix + " gives goodPardefs = " + goodPardefs);

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


  public static void main(final String[] args) throws Exception {
    ArrayList<S> symbolPrefix = new ArrayList<S>();
    symbolPrefix.add(S.getInstance("n"));
    //prepareMonodix("/home/j/esperanto/a/apertium-sv-da/apertium-sv-da.da.dix", symbolPrefix);
    //prepareMonodix("/home/j/esperanto/a/apertium-sv-da/apertium-sv-da.sv.dix", symbolPrefix);
    //prepareBidix("/home/j/esperanto/a/apertium-sv-da/apertium-sv-da.sv-da.dix", symbolPrefix);
    updateBidix(symbolPrefix);
  }
  /**
   * Currently only treats nounts  with e..g. <s n="n"/><s n="ut"/>
   * ant outputs
   * <e>       <p><l>certifikat</l>                         <r>certifikat</r></p><par n="n"/><par n="_nt_nt"/><par n="sp_sgpl__n"/></e>
   * @param fn
   * @throws Exception
   */
  public static void prepareBidix(String fn, ArrayList<S> symbolPrefix) throws Exception {
    Dictionary bidix = new DictionaryReader(fn).readDic();
    
    for (Section s : bidix.sections) {
      element:
      for (E ee : s.elements) {
        List<S> ls = ee.getSymbols("L");
        List<S> rs = ee.getSymbols("R");

        if (!startsWith(ls, symbolPrefix)) continue element;
        if (!startsWith(rs, symbolPrefix)) continue element;

        ls = ls.subList(symbolPrefix.size(), ls.size());
        rs = rs.subList(symbolPrefix.size(), rs.size());

        if (ls.size() != 1 || ls.size() != rs.size()) {
          System.err.println("wrong number of symbols on " + ee);
          continue;
        }

        if (!ls.get(0).is("nt") && !ls.get(0).is("ut")) continue;
        if (!rs.get(0).is("nt") && !rs.get(0).is("ut")) continue;
        //System.err.println("ls = " + ls);
        HashSet<S> removeSymbols = new HashSet<S> ();
        //removeSymbols.add(S.getInstance("n"));
        removeSymbols.add(ls.get(0));
        removeSymbols.add(rs.get(0));
        //Par addPar1 = new Par("n");
        Par addPar2 = new Par("_"+ls.get(0).name+"_"+rs.get(0).name);

        for (int n1=0; n1<ee.children.size(); n1++) {
          DixElement e = ee.children.get(n1);
          if (e instanceof P) {
            P p = (P) e;
            p.l.children.removeAll(removeSymbols);
            boolean rem = p.r.children.removeAll(removeSymbols);
            if (rem) {
              // add new paradigms and stop
              ee.children.add(n1+1, addPar2);
              //ee.children.add(n1+1, addPar1);
              break;
            }
          }
        }
      }
    }
    bidix.printXMLToFile(fn+"s", DicOpts.STD);
  }


  public static void prepareMonodix(String fn, ArrayList<S> symbolPrefix) throws Exception {
    Dictionary sldix = new DictionaryReader(fn).readDic();
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
      boolean nt = symbols.contains(S.getInstance("nt"));
      boolean ut = symbols.contains(S.getInstance("ut"));
      boolean sp = symbols.contains(S.getInstance("sp"));

      if (nt && ut || !nt && !ut) {
        System.err.println("hmm... igoring " + par.name + " as I can't figure hint from "+symbols);
        continue;
      }
      String hint =  (nt?"nt":"ut") + "," + (sp?"sp":"sgpl");


      //System.err.println("hint = " + hint + " for "+par);
      par.comment = KEY_bidixhint+hint;
      //System.err.println("par.comment = " + par.comment);
    }
    sldix.printXMLToFile(fn+"s", DicOpts.STD);
  }

  private static boolean startsWith(List<S> sym, List<S> symbolPrefix) {
    boolean startsWith=true;
    if (sym.size()<symbolPrefix.size()) {
      startsWith=false;
    }
    if (!sym.subList(0, symbolPrefix.size()).equals(symbolPrefix)) {
      startsWith=false;
    }
    return startsWith;
  }

  public static void updateBidix(ArrayList<S> symbolPrefix) throws Exception {
    System.err.println(" ============== updateBidix() =============");
    Dictionary sldix = new DictionaryReader("/home/j/esperanto/a/apertium-sv-da/apertium-sv-da.sv.dixs").readDic();
    HashMap<String, String> sPardefHint=getPardefHintMap(sldix);
    HashMap<String, LinkedHashSet<String>> sLemPardef=getLemmaPardefMap(sldix, symbolPrefix);

    Dictionary tldix = new DictionaryReader("/home/j/esperanto/a/apertium-sv-da/apertium-sv-da.da.dixs").readDic();
    HashMap<String, String> tPardefHint=getPardefHintMap(tldix);
    HashMap<String, LinkedHashSet<String>> tLemPardef=getLemmaPardefMap(tldix, symbolPrefix);

    Dictionary bidix = new DictionaryReader("/home/j/esperanto/a/apertium-sv-da/apertium-sv-da.sv-da.dixs").readDic();
    HashMap<String, Par> biHintPardefMap = new HashMap<String, Par>();
    HashMap<String, String> bipardefHintMap = getPardefHintMap(bidix);

    Par PAR_OMIT = new Par("OMIT");
    for (String pardef : bipardefHintMap.keySet()) {
      String hint = bipardefHintMap.get(pardef);
      if (hint.endsWith("*")) {
        biHintPardefMap.put(hint.substring(0,hint.length()-1), PAR_OMIT);
      } else {
        biHintPardefMap.put(hint, new Par(pardef));
      }
    }


    System.err.println("biHintPardefMap = " + biHintPardefMap);
    //System.err.println("slHints = " + slHints);
    //System.err.println("tlHints = " + tlHints);

    for (Section s : bidix.sections) {
      entry:
      for (E e : s.elements) {
        // See if this entry should be automatically fixed; either 'autofix' is in its comment or it uses only paradigms starting with an _
        boolean autofix = false;
        if (e.maskNull(e.comment).contains("autofix")) autofix = true;
        else for (DixElement de : e.children) {
          if (de instanceof Par) {
            if (((Par) de).name.startsWith("_")) {
              autofix = true;
            } else {
              autofix = false;
              break;
            }
          }
        }

        if (!autofix) continue;

        // OK, regenerate all paradigms to ensure concordance
        String e0 = e.toString();
        //System.err.println("e0 = " + e);
        String slhint=getHint(sLemPardef, e, e.getLemmaForSide("L"), sldix, sPardefHint);
        String tlhint=getHint(tLemPardef, e, e.getLemmaForSide("R"), tldix, tPardefHint);
        if (slhint==null || tlhint==null) {
          //System.err.println("Missing hints for " +e+ ": source: "+ slhint+"  target: "+tlhint);
          continue;
        }

        if (slhint.split(",").length!=tlhint.split(",").length) {
          System.err.println("Hints for " +e+ " does not have same number of elements: "+ slhint+" "+tlhint);
          continue;
        }

        // create new par list
        ArrayList<Par> newPars = new ArrayList<Par>();
        for (int n=0; n<slhint.split(",").length; n++) {
          String slp = slhint.split(",")[n];
          String tlp = tlhint.split(",")[n];
          String hintKey = slp+"-"+tlp;
          Par par = biHintPardefMap.get(hintKey);
          if (par==null) {
            System.err.println("hintKey = " + hintKey+" gave no par! biHintPardefMap="+biHintPardefMap);
            continue entry;
          }
          if (par != PAR_OMIT) newPars.add(par);
        }

        // remove old par list
        for (Iterator<DixElement> dei = e.children.iterator(); dei.hasNext(); ) {
          DixElement de = dei.next();
          if (de instanceof Par) {
            dei.remove();
          }
        }

        e.children.addAll(newPars);

        String e1 = e.toString();
        if (!e0.equals(e1)) {
          System.err.println("before: " + e0);
          System.err.println("now:    " + e1);
        }
      }
    }
    bidix.printXMLToFile(bidix.fileName+"s", DicOpts.STD);
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
