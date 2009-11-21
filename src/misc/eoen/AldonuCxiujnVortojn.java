package misc.eoen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.*;

/**
 *
 * @author j
 */
public class AldonuCxiujnVortojn {

//  public static final boolean debug=false;
  public static boolean debug=false;

  /*
  private static Set<String> tenindiajEtikedoj = new HashSet<String>( Arrays.asList(
      "<n> <np> <vblex> <adj> <adv> <ant> <acr> <loc> <top> <pr>".split(" ")));
  */
  private static Set<String> forprenindiajEtikedoj = new HashSet<String>( Arrays.asList(
//      "<GD> <sg> <f> <pp> <subj> <imp> <sp> <gen> <def> <itg> <past> <mf> <p1> <pres> <sg> <obj> <inf> <m>  <pl> <ger> <tn> <p3>".split(" ")));
      "<GD> <sg> <pp> <subj> <imp> <sp> <gen> <def> <itg> <past> <mf> <p1> <pres> <sg> <obj> <inf>  <pl> <ger> <tn> <p3>".split(" ")));

    public static ArrayList<String> surfacoAlLemoj(String surfacFormo, LinkedHashMap<String, ArrayList<String>> surfacoAlLemoj) {
        ArrayList<String> lemoj=surfacoAlLemoj.get(surfacFormo);
        if (lemoj==null) {
            lemoj=surfacoAlLemoj.get(unuaLiteroMajuskla(surfacFormo));
        }
        if (lemoj==null) {
            lemoj=surfacoAlLemoj.get(surfacFormo.toLowerCase());
        }
        return lemoj;
    }

  static void aldonuParon(Paro p, Map<String, ArrayList<Paro>> aldonuParojnEn, Map<String, ArrayList<Paro>> aldonuParojnEo, ArrayList<Paro> aldonuParojn) {

    if (p.orgEn.equals("add")) {
//                dprintln(" "+p);
    }

    if (p.dir_enEo==null) {
      ArrayList<Paro> samaVorto=aldonuParojnEn.get(p.rootEn+"__"+p.apertiumWordType());
      if (samaVorto!=null) {
        for (Paro p0 : samaVorto) {
          if (p0!=null&&p0.dir_enEo==null) {
            if (p0.frango<=p.frango) {
              if (debug) {
                dprintln("Ne faras en->eo por "+p+" cxar jam ekzistas "+p0);
              }
              p.dir_enEo=p0;
            } else {
              if (debug) {
                dprintln("Ne faras en->eo por "+p0+" cxar nun venas "+p);
              }
              p0.dir_enEo=p;
            }
          }
        }
      }
    }


    if (p.dir_eoEn==null) {
      ArrayList<Paro> samaVorto=aldonuParojnEo.get(p.rootEo+"__"+p.apertiumWordType());
      if (samaVorto!=null) {
        for (Paro p0 : samaVorto) {
          if (p0!=null&&p0.dir_eoEn==null) {
            if (p0.frango<=p.frango) {
              if (debug) {
                dprintln("Ne faras eo->en por "+p+" cxar jam ekzistas "+p0);
              }
              p.dir_eoEn=p0;
            } else {
              if (debug) {
                dprintln("Ne faras en->eo por "+p0+" cxar nun venas "+p);
              }
              p0.dir_eoEn=p;
            }
          }
        }
      }
    }

    if (p.dir_eoEn==null||p.dir_enEo==null) {
      if (debug) {
        dprintln("Aldonas "+p.apertiumEoEn());
      }
      aldonuParojn.add(p);
      add(p.rootEo+"__"+p.apertiumWordType(), p, aldonuParojnEo);
      add(p.rootEn+"__"+p.apertiumWordType(), p, aldonuParojnEn);
    } else {
      if (debug) {
        dprintln("NE aldonas "+p.apertiumEoEn());
      }
    }
  }


  static void analizuEn(LinkedHashMap<String, ArrayList<String>> surfacoAlLemoj, Paro p, LinkedHashMap<String, ArrayList<String>> aperLemoAtTraduko) 
  {
      ArrayList<String> lemoj=null;

      if (p.adv()) // provu unue kun -ly sur la surfaca formo,
          lemoj=surfacoAlLemoj.get(p.orgEn+"ly");



      if (lemoj==null) lemoj=surfacoAlLemoj(p.orgEn, surfacoAlLemoj);


      if (lemoj==null) return;

    if (debug) dprintln("En:"+lemoj);
    p.comment+="enR2="+lemoj;
    String trovitaLemo = null;
    
    for (String lemo : lemoj) 
    {
      String apertiumWordTypeKrampoj=p.apertiumWordTypeKrampoj();
      int i=lemo.indexOf(apertiumWordTypeKrampoj);
      if (i>0) {
        //if (trovitaLemo!=null && debug) dprintln(p+"Pluraj lemoj trovitaj: "+trovitaLemo+" != "+lemo);
        trovitaLemo = lemo;
        p.rootEn=lemo.substring(0, i);
        p.setAliajTag(lemo.substring(i+apertiumWordTypeKrampoj.length()));
        //break;
      }
    }
    
      
    if (trovitaLemo != null) {
      String trovitaLemo2 = trovitaLemo;
      ArrayList<String> aperTraduko = aperLemoAtTraduko.get(trovitaLemo2);
      while (aperTraduko==null && trovitaLemo2!=null) {
          trovitaLemo2 = Iloj.elhakuEtikedon(trovitaLemo2);
          aperTraduko = aperLemoAtTraduko.get(trovitaLemo2);
      }

      if (aperTraduko!=null) {
        if (debug) dprintln("Ne faras eo->en cxar en Apertium jam estas: "+trovitaLemo+"   -> "+aperTraduko);
        p.dir_enEo=new ApertiumParo(aperTraduko.toString());
      }      
    }


    if (p.rootEn==null) {
      if (debug) System.out.println("ARGH, no root found for "+p.apertiumWordType()+" in  enRadikoj2="+lemoj);
      //p.rootEn=enRadikoj.iterator().next();
    }
  }

  
  /*
<preadv> 
 <predet> 
 <dem> 
 <comp> 
 <pos> 
 <vbdo> 

 <nt> 

 
 <sup> 
 <cnjadv> 
 <ind> 
 <num> 
 <ref>
 <qnt> 
 <det> 
 <sint> 
 <cnjcoo>
 <vaux>
 <vbser>
<ij>
<cnjsub> <vbhaver> 
  
<prn> 

 */  
  //private static Set<String> forprenitajEtikedoj = new HashSet<String>();


  static class ApertiumParo extends Paro {

    String eoEn;

    public ApertiumParo(String eoEn) {
      this.eoEn=eoEn;
    }

    public String toString() {
      return "A"+eoEn;
    }
  }

  public static void main(String[] args) throws Exception {
    
    final LeguTradukuNet legutradukunet=new LeguTradukuNet();
    Thread leguT=new Iloj.Ek() { public void ek() throws Exception { legutradukunet.leguTradukuNetDosieron();  } };
    leguT.setPriority(Thread.MAX_PRIORITY);
    
    long tempo = System.currentTimeMillis();

    LinkedHashMap<String, ArrayList<String>> aperEoDix[] = new LinkedHashMap[2]; //LinkedHashMap<String,ArrayList<String>>[2];
    LinkedHashMap<String, ArrayList<String>> aperEnDix[] = new LinkedHashMap[2];
    LinkedHashMap<String, ArrayList<String>> aperEoEnDix[] = new LinkedHashMap[2];

    Iloj.leguDix(aperEnDix,"lt-expand apertium-eo-en.en.dix",forprenindiajEtikedoj, true);
    Iloj.leguDix(aperEoEnDix,"lt-expand apertium-eo-en.eo-en.dix",forprenindiajEtikedoj, false);
    Iloj.leguDix(aperEoDix, "lt-expand apertium-eo-en.eo.dixtmp1",forprenindiajEtikedoj, true);

    Iloj.leguDix(aperEoEnDix,"lt-expand dev/ald_baze/ald_tradukunet.eo-en.dix",forprenindiajEtikedoj, false);
    Iloj.leguDix(aperEoDix, "lt-expand dev/ald_baze/ald_tradukunet.eo.dix",forprenindiajEtikedoj, true);


    /*
    Iloj.leguDix(aperEoDix,"lt-expand dev/ald_plurvortoj/ald_tradukunet.eo-en.dix",forprenindiajEtikedoj);
    Iloj.leguDix(aperEnDix,"lt-expand dev/ald_plurvortoj/ald_tradukunet.en.dix",forprenindiajEtikedoj);
    Iloj.leguDix(aperEnDix,"lt-expand dev/ald_plurvortoj/apertium-eo-en.en_aldonata-plurvortoj.dix",forprenindiajEtikedoj);


 */
    //Iloj.leguDix(aperEnDix, "lt-expand dev/apertium-eo-en.en_aldonata-freeling.dix",forprenindiajEtikedoj);



    Set<String> esperanto_nouns_with_gender = new HashSet<String>(Iloj.leguTekstDosieron("res/esperanto_nouns_with_gender.txt"));
    

    ArrayList<Paro> aldonuParojn=new ArrayList<Paro>(50000);
    Map<String,ArrayList<Paro>> aldonuParojnEo=new HashMap<String,ArrayList<Paro>>(10000);
    Map<String,ArrayList<Paro>> aldonuParojnEn=new HashMap<String,ArrayList<Paro>>(10000);

    System.err.println("dixoj legite post " + (System.currentTimeMillis()-tempo)*0.001);
    
    LinkedHashSet<String> mankantajEnVortoj = new LinkedHashSet<String>();
    
    leguT.join();


    System.err.println("tradukunet legite post " + (System.currentTimeMillis()-tempo)*0.001);

    int haltuPost=Integer.MAX_VALUE;

    int parojTraktis = 0;
    int parojTotale = 0;

    masxo:
    for (ArrayList<Paro> alp : legutradukunet.tradukuEnParoj.values()) {
      for (Paro p : alp) {
          parojTotale++;
        if (!p.problem() && p.orgEn.indexOf("&")==-1&&!p.plur&&p.unuVortoEo ) {//&& p.orgEn.indexOf(" ")==-1

          if (p.orgEo.startsWith("honorat")) {
              debug=true;
          } else
              debug=false;

          if (haltuPost--<0) {
            System.out.println("HALTIS");
            break masxo;
          }
          if (debug) dprintln("");
          if (debug) dprintln(p);

          if (p.noun() && esperanto_nouns_with_gender.contains(p.rootEo)) {
            //System.err.println("p gender = " + p);
            p.gender = true;
          }
          
          if (p.noun() &&p.rootEo.endsWith("ino")) {
            String s = p.rootEo.substring(0,p.rootEo.length()-3)+"o";
            //if (!esperanto_nouns_with_gender.contains(s)) System.err.println("p gender " + p+"     "+s);
          }

          //
          // Trovu la anglan lemon
          //
          LinkedHashMap<String, ArrayList<String>> enSurfacoAlLemoj = aperEnDix[0];
          LinkedHashMap<String, ArrayList<String>> eoSurfacoAlLemoj = aperEoDix[0];

          ArrayList<String> tmp_surfacoAlLemoj = surfacoAlLemoj(p.rootEo,  eoSurfacoAlLemoj);  // p.orgEo,
          p.radikoJamEkzistas_eo = (tmp_surfacoAlLemoj !=null);
          if (debug) dprintln("p.radikoJamEkzistas_eo=" +p.radikoJamEkzistas_eo+ "  surfacoAlLemoj("+p.rootEo+",  eoSurfacoAlLemoj)"+ tmp_surfacoAlLemoj);


          analizuEn(enSurfacoAlLemoj, p, aperEoEnDix[1]);
          p.radikoJamEkzistas_en = (p.rootEn!=null);
          if (debug) dprintln("p.radikoJamEkzistas_en=" +p.radikoJamEkzistas_eo+ "  p.rootEn="+p.rootEn);
        
          if (!p.radikoJamEkzistas_en) {
            mankantajEnVortoj.add(p.orgEn+"; "+p.apertiumWordType);
            p.radikoJamEkzistas_en = false;
            p.rootEn = p.orgEn;
            //enDixAldono.add(p.apertiumEn());
            continue; // Ne traktu anglajn vortojn kun nekonata paradigmo
          }

          
          //
          // Trovu la esperantan lemon
          //
          
          ArrayList<String> eolemmas=aperEoDix[0].get(p.rootEo);
          if (debug) dprintln("Eo:"+ eolemmas);
          if (eolemmas!=null) {
            for (String eolemma : eolemmas) {
              String orgEo=eolemma;
              ArrayList<String> eoEn;
              while ((eoEn=aperEoEnDix[0].get(eolemma))==null&&eolemma.lastIndexOf('>')>0) {  // &&!jamMontritaj.contains(eoEn)
                eolemma=eolemma.substring(0, eolemma.lastIndexOf('<'));
              }

              if (eoEn!=null) {
                if (debug) dprintln("Ne faras en->eo cxar en Apertium jam estas: "+eolemma+"   -> "+eoEn);
                p.dir_eoEn=new ApertiumParo(eoEn.toString());
              }
            }
          }


          //
          // Aldonu
          //
          parojTraktis++;
          aldonuParon(p, aldonuParojnEn, aldonuParojnEo, aldonuParojn);
        }
      }
    }
    
    System.err.println("Traktis "+parojTraktis+ " parojn de "+parojTotale+" (" +(parojTraktis*100/parojTotale)+"%) dum " + (System.currentTimeMillis()-tempo)*0.001);
    Iloj.skribu(mankantajEnVortoj, "tradukunet_missing_en_words.txt");

    System.err.println("mankantajEnVortoj skribite post " + (System.currentTimeMillis()-tempo)*0.001);
    
    LinkedHashSet<String> eoDixAldono=new LinkedHashSet<String>();
    LinkedHashSet<String> enDixAldono=new LinkedHashSet<String>();
    LinkedHashSet<String> eoEnDixAldono=new LinkedHashSet<String>();

    int radikojJamEkzistas_en = 0;
    int radikojJamEkzistas_eo = 0;
    int paroJamKovrite_en_eo = 0;

    for (Paro p : aldonuParojn) {
      if (p.dir_eoEn==null||p.dir_enEo==null) {

          if (!p.radikoJamEkzistas_en) {
            enDixAldono.add(p.apertiumEn());
          }

          if (!p.radikoJamEkzistas_eo) {
            eoDixAldono.add(p.apertiumEo());
          }
            eoEnDixAldono.add(p.apertiumEoEn());

      } else {
         paroJamKovrite_en_eo ++;
        if (debug) dprintln("NE aldonas2, cxar superflua: "+p.apertiumEoEn());      //System.out.dprintln(Iloj.deCxapeloj(p.apertiumEoEn()));
      }
    }

    Iloj.skribu(eoDixAldono, "ald_tradukunet.eo.dix");
    Iloj.skribu(enDixAldono, "ald_tradukunet.en.dix");
    Iloj.skribu(eoEnDixAldono, "ald_tradukunet.eo-en.dix");

    System.err.println("dosieroj skribite post " + (System.currentTimeMillis()-tempo)*0.001);
    
    //System.err.println("forprenitajEtikedoj = " + forprenitajEtikedoj);
  }


  private static void add(String key, Paro val, Map<String, ArrayList<Paro>> xxParo) {
    ArrayList<Paro> alp = xxParo.get(key);
    if (alp == null) xxParo.put(key, alp = new ArrayList<Paro>());
    alp.add(val);
  }
  
  
  public static void dprintln(Object arg) {
    System.out.println(Iloj.deCxapeloj(arg));
  }

  public static String unuaLiteroMajuskla(String orgEn) {
    if (orgEn==null ||orgEn.length()==0) {
      return orgEn;
    }
    return Character.toUpperCase(orgEn.charAt(0))+orgEn.substring(1);
  }
}
