package misc.eoen.malnova;

import java.util.regex.Pattern;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.io.FileReader;
import java.util.HashMap;
import java.io.BufferedReader;
import java.util.regex.Matcher;

public class LeguAliajn {
    public static void main(String[] args) {
	//LeguAliajn legualiajn = new LeguAliajn();
    }


    private HashMap<String,String> eoDix = leguMonodix("apertium-eo-en.eo.dix");
    private HashMap<String,String> enDix = leguMonodix("apertium-eo-en.en.dix");
    private HashMap<String,Double> eoFreq = leguHitparadon("hitparade-eo.txt");
    private HashMap<String,Double> enFreq = leguHitparadon("hitparade-en.txt");

    public static HashMap<String,Double> leguHitparadon(String dosiernomo) {
	System.out.println("Legas "+dosiernomo);
	BufferedReader br;
	String linio;

	LinkedHashMap<String,Double> listo = new LinkedHashMap<String, Double>(50002);
	try {
	    br = new BufferedReader(new FileReader(dosiernomo));
	    double maxfreq = -1;
	    while ((linio = br.readLine()) != null) {
		String[] s = linio.trim().split("\\s+");
		if (s.length < 2)
		    continue;
		double freq = Integer.parseInt(s[0]);
		if (maxfreq == -1) {
		    maxfreq = freq;
		}
		listo.put(s[1], freq / maxfreq);

	    }
	    br.close();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
	//montruHashMap(listo);
	return listo;
    }



    public static HashMap<String,String> leguMonodix(String dosiernomp) {
	HashMap<String,String> listo = new HashMap<String,String>(50002);
	System.out.println("Legas "+dosiernomp);
	BufferedReader br;
	String linio;
	try {
	    br = new BufferedReader(new FileReader(dosiernomp));
/*
	    <e lm="karakterizi"><i>karakteriz</i><par n="verb__vblex"/></e>
	    <e lm="karaktero"><i>karaktero</i><par n="nom__n"/></e>

	    <e lm="pli da"><p><l>pli<b/>da</l><r>pli<b/>da<s n="det"/><s n="qnt"/><s n="sg"/><s n="nom"/></r></p></e>
	    <e r="RL" lm="pli da"><p><l>pli<b/>da</l><r>pli<b/>da<s n="det"/><s n="qnt"/><s n="pl"/><s n="nom"/></r></p></e>
   */
	Pattern pat = Pattern.compile("lm=\"([\\w\\s]+)\".*par n=\"(\\w+)__(\\w+)\"");

	while ((linio = br.readLine()) != null) {

	    Matcher m = pat.matcher(linio);

	    while (m.find()) {
		String vorto = m.group(1);
		String fleksisimala = m.group(2);
		String klaso = m.group(3);

		//System.out.println(vorto + " "+ klaso+"   "+fleksisimala   + "    "+linio);
		listo.put(vorto+"__"+klaso, linio);
	    }
	}
	br.close();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
	//montruHashMap(listo);
	return listo;
    }


}
