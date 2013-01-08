/*
 * Copyright (C) 2008 Dana Esperanta Junulara Organizo http://dejo.dk/
 * Author: Jacob Nordfalk
 *
 * This program hasSymbol free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program hasSymbol distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package misc.eoen;


import dics.elements.dtd.ContentElement;
import java.io.IOException;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.Section;
import dictools.utils.DictionaryReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author j
 */
public class KompletiguEoDix {
/*
  private static Set<String> forprenindiajEtikedoj = new HashSet<String>( Arrays.asList(
      ("<prn> <det> <pr> <cnjcoo> <cnjadv> <acc> <nom> <num> <pos> <preadv> "+
       "<qnt> <ind> <cni> <pp2> <pp3> <fts> <gerpast> <rel> <an> <vbaux> <ij> <vbser> <fti> <cnjsub> <dem> "+
       "<GD> <sg> <f> <pp> <subj> <imp> <sp> <gen> <def> <itg> <past> <mf> <p1> <pres> <sg> <obj> <inf> <m> "+
       "<nt> <ref> <pl> <ger> <tn> <p1> <p2> <p3>").split(" ")));
  */
  private static Set<String> vortKlasoj = new LinkedHashSet<String>( Arrays.asList(
      "<adj> <adv> <n><m> <n> <vblex> <np><top> <np><al> <np><ant><m> <np><ant><f> <np><cog> <np>".split(" ")));

  private static Set<String> vortKlasojUzataj = new TreeSet<String>();
  private static Set<String> vortKlasojNeUzataj = new TreeSet<String>();


  public static void aldonuLemon(String s, Map<String, String> monodixEoLemoj) {
    System.err.println("aldonuLemon(String s = " + s);

    int i=s.indexOf('<');
    if (i==-1) return;

      if (s.indexOf('>')<i) return; // ne traktu vortojn kiel #vblex\>\<vblex\>\<inf\>
      String etidedoj=s.substring(i);
      //if (etidedoj.contains(" ")) return; // ne traktu plur-vortojn nun
      for (String kl : vortKlasoj) {
        if (etidedoj.startsWith(kl)) {
          String gl=monodixEoLemoj.put(s.substring(0, i+kl.length()), s);
          //if (gl != null) System.err.println("Hmm.... gl = " + gl + " for "+s.substring(0,i+kl.length()) +"  , "+ s);
          vortKlasojUzataj.add(etidedoj);
          etidedoj = "";
          return;
        }
      }
      // Ne estas en la listo de vortklasoj. Registru la tutan aron da etikedoj
      String gl=monodixEoLemoj.put(s, s);

      vortKlasojNeUzataj.add(etidedoj);
  }



  public static void main(final String[] args) throws IOException {
    Map<String, String> testvoc_mankas_eo_dix=new LinkedHashMap<String, String>();
    Map<String, String> testvoc_mankas_bidix=new LinkedHashMap<String, String>();


    String antauxaStr="";

// XXXXXXXXXX HVORFOR KOMMER <e lm="vblex>"><i>vblex</i><par n="verb__vblex"/></e ????


    for (String s : Iloj.exec("./testvoc2_en-eo.sh")) {
      char ch = s.charAt(0);
      if (ch != '#' && ch != '\\') continue;
      s = s.replaceAll("\\\\", "").trim();

      System.err.println("s = " + s +  "    antauxaStr="+antauxaStr);
      if (ch=='#') aldonuLemon(s.substring(1), testvoc_mankas_eo_dix);
      else aldonuLemon(s.substring(1), testvoc_mankas_bidix);

      //System.err.println("s = " + s);
      antauxaStr = s;
    }

    System.err.println("vortKlasojUzataj = " + vortKlasojUzataj);
    System.err.println("vortKlasojNeUzataj = " + vortKlasojNeUzataj);


    System.err.println("testvoc_mankas_en_eodix = " + testvoc_mankas_eo_dix);
    System.err.println("testvoc_mankas_en_bidix = " + testvoc_mankas_bidix);

    Map<String, String> eodix_aldono=new TreeMap<String, String>();
    Set<String> esperanto_nouns_with_gender = new HashSet<String>(Iloj.leguTekstDosieron("res/esperanto_nouns_with_gender.txt"));
    for (String str : testvoc_mankas_eo_dix.keySet()) {

      int i = str.indexOf('<');
      if (i==-1) {
        System.err.println("HM!!!! str = " + str);
      } else {
        Paro p = new Paro();
        p.rootEo = str.substring(0,i);
        String tags = str.substring(i);
//       "<adj> <adv> <n> <vblex> <np><top> <np><al> <np><ant><m> <np><ant><f> <np><cog> <np>".split(" ")));
        if (tags.equals("<adj>")) p.setKlasoTag(p.ADJ);
        else if (tags.equals("<adv>")) p.setKlasoTag(p.ADV);
        else if (tags.equals("<pr>")) p.setKlasoTag(p.PR);
        else if (tags.equals("<n><m>")) { p.setKlasoTag(p.N); p.gender = true; }
        else if (tags.equals("<n>")) p.setKlasoTag(p.N);
        else if (tags.equals("<vblex>")) p.setKlasoTag(p.VBLEX);
        else if (tags.startsWith("<np>")) { p.setKlasoTag(p.NP); p.setAliajTag(tags.substring(4)); }
        //else if (tags.startsWith("<det><ord>")) { p.setEoPardef(""); }
        else {
            System.err.println("neniu klaso por  " + tags);
            p.setKlasoTag(p.OTHER); p.setAliajTag(tags);
        }

        if (p.noun() && esperanto_nouns_with_gender.contains(p.rootEo) && !p.gender) System.err.println("FEJL !p.gender " + p);
        if (p.noun() && !esperanto_nouns_with_gender.contains(p.rootEo) && p.gender) System.err.println("FEJL p.gender " + p);

        String s = p.apertiumEo();
        int parpos = s.lastIndexOf("<par n=");
        String k = parpos>0? s.substring(parpos)+s : s;

        System.err.println("k = " + k + "  antauxaStr="+antauxaStr);
        eodix_aldono.put(k, s);
      }
      antauxaStr = str;
    }

    PrintWriter ald_eo = Iloj.ekskribuHtml("ald_eo.dix");
    for (String s : eodix_aldono.values()) {
      System.out.println(s);
      ald_eo.println(s);
    }
    ald_eo.close();

  }











  public static void mainx(final String[] args) throws IOException {
    //Dictionary eodix = new DictionaryReader("../apertium-eo-en/apertium-eo-en.eo.dix.xml").readDic();

    //Set<String> esperanto_nouns_with_gender = new HashSet<String>(Iloj.leguTekstDosieron("res/esperanto_nouns_with_gender.txt"));


    //System.err.println("forprenindiajEtikedoj = " + forprenindiajEtikedoj);
    LinkedHashMap<String, ArrayList<String>> aperEoDix[] =Iloj.leguDix("lt-expand apertium-eo-en.eo.dixtmp1");

    Map<String, String> monodixEoLemoj=new LinkedHashMap<String, String>(aperEoDix[1].keySet().size() ) ;
    Map<String, String> bidixEoLemoj=new LinkedHashMap<String, String>(aperEoDix[1].keySet().size() ) ;

    for (String s : aperEoDix[1].keySet()) {
      aldonuLemon(s, monodixEoLemoj);
    }


    System.err.println("vortKlasoj = " + vortKlasoj);
    //System.err.println("monodixEoLemoj = " + monodixEoLemoj);

    Dictionary bidix = new DictionaryReader("../apertium-eo-en/apertium-eo-en.eo-en.dix").readDic();

    for (Section section : bidix.sections) {
      for (E ee : section.elements) {
        if (!ee.containsRegEx()) {
          ContentElement l=ee.getFirstPart("L");
          ContentElement r=ee.getFirstPart("R");

          String str = "";
          for (DixElement e : l.children) {
              String v = e.toString();

              //System.err.println("v = " + v+ "  " +e.getClass());
              str += v;
          }

          str = str.replace("<b/>", " ");
          aldonuLemon(str, bidixEoLemoj);
        }
      }
    }


    //System.err.println("bidixEoLemoj = " + bidixEoLemoj);

    Set<String> inBidixNotInMonodix = new LinkedHashSet(bidixEoLemoj.keySet()); inBidixNotInMonodix.removeAll(monodixEoLemoj.keySet());
    Set<String> inMonodixNotInBidix = new LinkedHashSet(monodixEoLemoj.keySet()); inMonodixNotInBidix.removeAll(bidixEoLemoj.keySet());


    //System.err.println("inBidixNotInMonodix = " + inBidixNotInMonodix);

    //System.err.println("inMonodixNotInBidix = " + inMonodixNotInBidix);
  /*
    for (String str : inBidixNotInMonodix) {

      int i = str.indexOf('<');
      if (i==-1) {
        System.err.println("HM!!!! str = " + str);
      } else {
        Paro p = new Paro();
        p.rootEo = str.substring(0,i);
        String tags = str.substring(i);
        if (tags.equals("<adj>")) p.setKlasoTag(p.ADJ);
        else if (tags.equals("<adv>")) p.setKlasoTag(p.ADV);
        else if (tags.equals("<n>")) p.setKlasoTag(p.N);
        else if (tags.equals("<vblex>")) p.setKlasoTag(p.VBLEX);
        else if (tags.startsWith("<np>")) { p.setKlasoTag(p.NP); p.setAliajTag(tags.substring(4)); }
        else
          System.err.println("FEJL tags = " + tags);
          System.out.println(p.apertiumEo() + "  fra "+str);
      }
    }
    */
/*
    for (Section section : bidix.sections) {
      Iterator<E> eei=section.getEElements().iterator();
      while (eei.hasNext()) {
        E ee=eei.next();
        if (!ee.containsRegEx()) {
          Par par = ee.getFirstParadigm();
          ContentElement l=ee.getFirstPart("L");
          ContentElement r=ee.getFirstPart("R");

          String k = l.toString() + r.toString();

          E exEe = hm.get(k);
        }
      }
    }

*/


    //AutorestrictBidix.reviseRestrictions(dic, false, true);




    //System.out.println("Updated morphological dictionary: '" + out + "'");
    //dic.printXML(out);

    //new DicFormatE1LineAligned(eodix).setAlignP(10).setAlignR(55).printXML("eo-nova.dix");
  }

}
