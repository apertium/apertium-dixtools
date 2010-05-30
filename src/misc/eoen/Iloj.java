package misc.eoen;


import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.text.*;
import java.util.*;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class Iloj {


    public static void main(String[] args) throws IOException {
    String l = alCxapeloj("cxgxhxjxsxux");
    for (int i = 0; i < l.length(); i++) {
        char c = l.charAt(i);
        System.out.println(Integer.toHexString(c) + " " + (int) c + " " + c);
    }
    }

    public static String deCxapeloj(Object o) {
    return deCxapeloj(""+o);
    }

    public static String deCxapeloj(String teksto) {
    teksto = teksto.replaceAll("ĉ", "cx");
    teksto = teksto.replaceAll("ĝ", "gx");
    teksto = teksto.replaceAll("ĥ", "hx");
    teksto = teksto.replaceAll("ĵ", "jx");
    teksto = teksto.replaceAll("ŝ", "sx");
    teksto = teksto.replaceAll("ŭ", "ux");
    teksto = teksto.replaceAll("Ĉ", "Cx");
    teksto = teksto.replaceAll("Ĝ", "Gx");
    teksto = teksto.replaceAll("Ĥ", "Hx");
    teksto = teksto.replaceAll("Ĵ", "Jx");
    teksto = teksto.replaceAll("Ŝ", "Sx");
    teksto = teksto.replaceAll("Ŭ", "Ux");
    return teksto;
    }



   private static final Pattern cx = Pattern.compile("cx");
   private static final Pattern gx = Pattern.compile("gx");
   private static final Pattern hx = Pattern.compile("hx");
   private static final Pattern jx = Pattern.compile("jx");
   private static final Pattern sx = Pattern.compile("sx");
   private static final Pattern ux = Pattern.compile("ux");
   private static final Pattern Cx = Pattern.compile("Cx");
   private static final Pattern Gx = Pattern.compile("Gx");
   private static final Pattern Hx = Pattern.compile("Hx");
   private static final Pattern Jx = Pattern.compile("Jx");
   private static final Pattern Sx = Pattern.compile("Sx");
   private static final Pattern Ux = Pattern.compile("Ux");
    
    public static final String alCxapeloj(String teksto) {
      if (teksto.indexOf('x')==-1) return teksto;
      /**/
      teksto = cx.matcher(teksto).replaceAll("ĉ");
      teksto = gx.matcher(teksto).replaceAll("ĝ");
      teksto = hx.matcher(teksto).replaceAll("ĥ");
      teksto = jx.matcher(teksto).replaceAll("ĵ");
      teksto = sx.matcher(teksto).replaceAll("ŝ");
      teksto = ux.matcher(teksto).replaceAll("ŭ");
      teksto = Cx.matcher(teksto).replaceAll("Ĉ");
      teksto = Gx.matcher(teksto).replaceAll("Ĝ");
      teksto = Hx.matcher(teksto).replaceAll("Ĥ");
      teksto = Jx.matcher(teksto).replaceAll("Ĵ");
      teksto = Sx.matcher(teksto).replaceAll("Ŝ");
      teksto = Ux.matcher(teksto).replaceAll("Ŭ");
      /* */
/*
  teksto = teksto.replaceAll("cx", "ĉ");
    teksto = teksto.replaceAll("gx", "ĝ");
    teksto = teksto.replaceAll("hx", "ĥ");
    teksto = teksto.replaceAll("jx", "ĵ");
    teksto = teksto.replaceAll("sx", "ŝ");
    teksto = teksto.replaceAll("ux", "ŭ");
    teksto = teksto.replaceAll("Cx", "Ĉ");
    teksto = teksto.replaceAll("Gx", "Ĝ");
    teksto = teksto.replaceAll("Hx", "Ĥ");
    teksto = teksto.replaceAll("Jx", "Ĵ");
    teksto = teksto.replaceAll("Sx", "Ŝ");
    teksto = teksto.replaceAll("Ux", "Ŭ");
/**/  
  return teksto;
    }


    public static String legu(File fil) throws IOException {
    FileChannel fc = new FileInputStream(fil).getChannel();
    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                     fil.length());
    //CharBuffer cb = Charset.forName("ISO-8859-1").decode(bb);
    CharBuffer cb = Charset.forName("UTF-8").decode(bb);
    return new String(cb.array());
    }


    public abstract static class Ek extends Thread {
    public abstract void ek() throws Exception;

    public void run() {
        try {
        ek();
        } catch (Exception ex) {
        ex.printStackTrace();
        System.exit( -1);
        }
    }

    public Ek() {
        start();
    }
    }


    public static ArrayList<String> leguNopaste(String nopasteUrl) throws IOException {
    ArrayList<String> linioj = new ArrayList<String>();
    BufferedReader br = new BufferedReader(new InputStreamReader(new
        URL(nopasteUrl).openStream()));
    System.out.println("Legas " + "URL");
    //ArrayList<String> words = new ArrayList<String>();
    String linio;
    while ((linio = br.readLine()) != null) {
        linio = linio.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
        if (linio.indexOf('@')!=-1) { System.out.println("Forjxetas "+ linio); continue; }
        //System.out.println(linio);
        if (linio.startsWith("^")) {
        linio = linio.replaceAll("(.([^<]+)(.+?)\\$.*)", "$2@$3@$0");
        linioj.add(linio);
        }
    }
    return linioj;
    }


    public static ArrayList<String> leguTekstDosieron(String nopasteUrl) throws IOException {

    ArrayList<String> linioj = new ArrayList<String>();
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(nopasteUrl)));
        System.out.println("Legas " + "URL");
        //ArrayList<String> words = new ArrayList<String>();
        String linio;
        while ((linio = br.readLine()) != null) {
            if (linio.length()>0) linioj.add(linio);
        }
    }
    return linioj;
    }

    public static ArrayList<String> exec(String _execstr) throws IOException {
        return exec(_execstr, false);
    }

    public static ArrayList<String> exec(String _execstr, boolean kontroluRetval) throws IOException {
    Process proces = Runtime.getRuntime().exec(_execstr);
    ArrayList<String> linioj = new ArrayList<String>(1000);

    BufferedReader br = new BufferedReader(new InputStreamReader(proces. getInputStream()));

    String linio;
    while ((linio = br.readLine()) != null) {
        //System.out.println("# æst: "+linio);
        linioj.add(linio);
    }

  
    proces.getInputStream().close();
    proces.getErrorStream().close();
    proces.getOutputStream().close();

    if (kontroluRetval) {
            try {
                proces.waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(Iloj.class.getName()).log(Level.SEVERE, null, ex);
            }
        if (proces.exitValue()!=0) throw new IllegalStateException(_execstr + " donis "+proces.exitValue());
     }



    return linioj;
  }


  
  private static ArrayList<String> forprenuNenecesajnEtikedojn(ArrayList<String> listo, Set<String> foriguEtikedojn) {
    if (listo==null || listo.size()==0) return listo;
    
    Set<String> novaListo = new LinkedHashSet<String>();
    
    for (String enlemma : listo) {
      enlemma = forprenuNenecesajnEtikedojn(enlemma, foriguEtikedojn);
      novaListo.add(enlemma);
    }
    
    
    //System.err.println("forprenuNenecesajnEtikedojn("+listo+" -> " + novaListo);
    
    return new ArrayList<String>(novaListo);
    //return new ArrayList<String>(listo);
  }
    

  public static final String forprenuNenecesajnEtikedojn(String enlemma, Set<String> foriguEtikedojn) {
      int i;
      while ((i= enlemma.lastIndexOf('<'))>0) {
        String etikedo = enlemma.substring(i);
        if (!foriguEtikedojn.contains(etikedo)) break;
        //forprenitajEtikedoj.add(etikedo);
        enlemma = enlemma.substring(0,i);
      }
    
    return enlemma;
  }
  
  public static final String elhakuEtikedon(String enlemma) {
      int i= enlemma.lastIndexOf('<');
      if (i<0) return null;
        enlemma = enlemma.substring(0,i);

    return enlemma;
  }


  
  public static LinkedHashMap<String,ArrayList<String>>[] leguDix(String dixkomando) throws IOException {
    //Set<String> s = Collections.emptySet();
    return leguDix(dixkomando, null);
  }

  public static LinkedHashMap<String,ArrayList<String>>[] leguDix(LinkedHashMap<String,ArrayList<String>>[] xxParoj, String dixkomando) throws IOException {
    return leguDix(xxParoj, dixkomando, null, false);
  }

  public static LinkedHashMap<String,ArrayList<String>>[] leguDix(String dixkomando, Set<String> foriguEtikedojn) throws IOException {
    return leguDix(null, dixkomando, null, false);
  }

  public static LinkedHashMap<String,ArrayList<String>>[] leguDix(LinkedHashMap<String,ArrayList<String>>[] xxParoj, String dixkomando, Set<String> foriguEtikedojn, boolean nedirektita) throws IOException {
    ArrayList<String> al = exec(dixkomando, true);

    if (xxParoj==null) xxParoj = new LinkedHashMap[2]; //LinkedHashMap<String,ArrayList<String>>[2];
    if (xxParoj[0] ==null) {
        xxParoj[0] = new LinkedHashMap<String,ArrayList<String>>(al.size());
        xxParoj[1] = new LinkedHashMap<String,ArrayList<String>>(al.size());
    }


    int n = 0;
  
    for (String l : al) {
      int i0 = l.indexOf(':');
      int i1 = l.lastIndexOf(':');
      String kv0 = (l.substring(0,i0));
      String kvi = (l.substring(i1+1));
      if (foriguEtikedojn!=null && !foriguEtikedojn.isEmpty()) {
          kv0 = forprenuNenecesajnEtikedojn(kv0,foriguEtikedojn);
          kvi = forprenuNenecesajnEtikedojn(kvi,foriguEtikedojn);
      }
      if (i0<i1 && !nedirektita) {
        char c = l.charAt(i0+1);
        //System.out.println("c = "+c);
        if (c=='>') add(kv0, kvi, xxParoj[0]);
        else add(kvi, kv0, xxParoj[1]);        
      } else {
        add(kv0, kvi, xxParoj[0]);
        add(kvi, kv0, xxParoj[1]);
      }
    }
    System.out.println("Finlegis "+dixkomando);
    return xxParoj;
  }

  private static void add(String key, String val, LinkedHashMap<String, ArrayList<String>> xxParo) {
    ArrayList<String> alp = xxParo.get(key);
    if (alp == null) xxParo.put(key, alp = new ArrayList<String>());
    if (!alp.contains(val)) alp.add(val);
  }


  private static String dosierujo = "tradukunet-generated";

  public static PrintWriter ekskribuHtml(String dosierNomo) {
    new File(dosierujo).mkdir();
    PrintWriter el = null;
    try {
        String fff = dosierujo + File.separator + dosierNomo;
        System.err.println("Skribas dosieron: "+fff);
        el = new PrintWriter(fff);
    } catch (FileNotFoundException ex) {
        ex.printStackTrace();
    }
    return el;
    }

    public static void skribu(LinkedHashSet set, String fn) {
        int n=0;
        PrintWriter out = Iloj.ekskribuHtml(fn);
        //out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        //out.println("<section id=\""+fn.replaceAll("[\\W]","_")+"\" type=\"standard\">");

        for (Object e : set) {
            out.println(e);
            //if (++n<100) System.out.println(fn+": "+n+Iloj.deCxapeloj(e));
        }
        //out.println("</section>");
        out.close();
    }


}
