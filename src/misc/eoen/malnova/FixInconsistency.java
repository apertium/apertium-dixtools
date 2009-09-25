package misc.eoen.malnova;

import misc.eoen.Iloj;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class FixInconsistency {
    public static boolean debug = true;


    public static void main(String[] args) throws IOException {
	fixEoMonodix();
    }


    public static void fixEoMonodix() throws IOException {

	ArrayList<String> al = Iloj.leguTekstDosieron("mankantaj_vortoj.txt");

        LinkedHashSet<String> eoDixAldono = new LinkedHashSet<String>();

	for (String linio: al) {
	   try {
		String[] wc = linio.split(" -------> ");
		String vortoAlt = wc[1].substring(1, wc[1].length()-1);
		if (debug) System.out.println(Iloj.deCxapeloj("\n========= " + linio + " ======"));
		if (debug) System.out.println(Iloj.deCxapeloj("\n========= " + vortoAlt + " ======"));

		if (debug) System.out.println("vortoAlt = " + vortoAlt);
		String vorto = vortoAlt.substring(0, vortoAlt.indexOf('<'));
		String tipo = vortoAlt.substring(vortoAlt.indexOf('<')+1,vortoAlt.indexOf('>'));
		if (debug) System.out.println("vorto = " + vorto);
		if (debug) System.out.println("tipo = " + tipo);

		if (tipo.equals("np") && Character.isUpperCase(vorto.charAt(0))) {
                    if (vortoAlt.indexOf("<top>")!=-1) {
		      eoDixAldono.add("    <e lm=\""+vorto+"\"><i>"+vorto+"</i><par n=\"Barcelono__np\"/></e>");
                    } else if (vortoAlt.indexOf("<ant><m>")!=-1) {
                        eoDixAldono.add("    <e lm=\""+vorto+"\"><i>"+vorto+"</i><par n=\"Mark__np\"/></e>");
                    } else if (vortoAlt.indexOf("<ant><f>")!=-1) {
                        eoDixAldono.add("    <e lm=\""+vorto+"\"><i>"+vorto+"</i><par n=\"Mary__np\"/></e>");
                    } else if (linio.indexOf("<cog>")!=-1) {
                        System.out.println("Ignorerer, da der er noget galt med <cog> "+ linio);
                        eoDixAldono.add("    <e lm=\""+vorto+"\"><i>"+vorto+"</i><par n=\"Smith__np\"/></e>");
                    } else {
                        System.out.println("??? "+ linio);
                    }
                } else if (tipo.equals("vaux")) {
		} else if (tipo.equals("np")) {
		    System.out.println("??? "+ linio);
		} else if (tipo.equals("vblex")) {
		    eoDixAldono.add("    <e lm=\""+vorto+"\"><i>"+vorto.substring(0,vorto.length()-1)+"</i><par n=\"verb__vblex\"/></e>");
		} else if (tipo.equals("adj")) {
		    eoDixAldono.add("    <e lm=\""+vorto+"\"><i>"+vorto.substring(0,vorto.length()-1)+"</i><par n=\"adj__adj\"/></e>");
		} else if (tipo.equals("adv")) {
		    eoDixAldono.add("    <e lm=\""+vorto+"\"><i>"+vorto+"</i><par n=\"komence__adv\"/></e>");
		} else {
		    System.out.println("??? "+ linio);
		}

	    } catch (Exception e) {
		//e.printStackTrace();
		System.err.println(e+"for "+linio);
	    }
	}
        Iloj.skribu(eoDixAldono, "ald_tradukunet.eo.dix");
    }


    public static void fixBidixEodix(String[] args) throws IOException {

	//ArrayList<String> al = Iloj.exec("./check_inconsistency.sh | grep '> #' | tee xxx_missing_adj");
	//ArrayList<String> al = Iloj.exec("./check_inconsistency.sh | tee  xxx_inconsistency");

	//ArrayList<String> al = Iloj.exec("cat xxx_inconsistency");
	//ArrayList<String> al = Iloj.leguNopaste("http://www.nopaste.com/p/aytXh6Cqy");
	ArrayList<String> al = Iloj.leguTekstDosieron("mankantaj_vortoj.txt");


	ArrayList<String> bidix = new ArrayList<String>();

	for (String linio : al) {
	    if (linio.indexOf("#")==-1) continue;
	    //if (debug)
		System.out.println(linio);
	    if (1+3 > 2) continue;
	    String[] e = linio.split(" -------> ");
	    if (e.length!=3) { System.out.println("Problem "+e.length + " "+ linio); continue; }

	    e[2] = e[2].replaceAll("\\\\","");

	    String root = e[2].substring(1).replaceAll("(<[a-z]*>)","");
	    String klasse = e[2].replaceAll(".*?<(\\w+)>.*","$1");
	    String aklasse = apertiumKlasse(klasse);
	    if (e[1].startsWith("^<") || root.indexOf(">")!=-1) {
		System.out.println("Bidix: "+e[0] +" -> "+ e[1]);
		String s = e[0].substring(1,e[0].length()-1);
		bidix.add(s);
		continue;
	    }
	    /*
	    if (root.indexOf(">")!=-1) {
		System.out.println("Mon: "+e[2]);
		if (debug) System.out.println("<!-- "+linio+" -->");
		continue;
	    }*/
	    if (aklasse.indexOf("?")!=-1) {
		if (debug) System.out.println("<!-- "+linio+ aklasse+ " -->");
		continue;
	    }
	    if (debug) System.out.println(root + " "+ klasse);
	    root = root.substring(0,root.length()-1); // for adj og verb
	    System.out.println("<e lm=\""+ root +"\"><i>"+ root +"</i><par n=\""+aklasse+"\"/></e>");

	}


	System.out.println(bidix.toString().replaceAll("<adj>, ","\", \""));
    }


    public static String apertiumKlasse(String klasse) {
	if ("adj".equals(klasse)) return "adj__adj";
	/*
	if (noun) return "<e lm=\""+ root +"\"><i>"+ root +"</i><par n=\"nom__n\"/></e>";
	if (adj) return "<e lm=\""+ root +"\"><i>"+ root +"</i><par n=\"adj__adj\"/></e>";
	if (adv) return "<e lm=\""+ root +"\"><i>"+ root +"</i><par n=\"komence__adv\"/></e>";
	if (verb) return "<e lm=\""+ root +"\"><i>"+ root +"</i><par n=\"verb__vblex\"/></e>";
*/
	return "?????????";
    }
}
