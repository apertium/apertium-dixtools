/*
 * Author: Jimmy O'Regan
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

package dictools.frequency;
import java.util.HashMap;
import java.util.TreeMap;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * FrequencyDict implementation for 'hitparade.txt'
 * ('hitparade.txt' is a common apertium convention for frequency lists,
 * based on a mail of May 17, 2007 by Mikel Forcada:
 * "It is quite easy to make a crude "hit parade" of words using a simple
 * Unix command sequence (a single line)
 *
 * cat mybigrepresentative.txt | tr ' ' '\012' | sort -f | uniq -c | sort -nr > hitparade.txt
 *
 * [I took this from Unix for Poets I think]"
 * Usually, Wikipedia dumps are used.
 * @author jimregan
 */
public class HitParade implements FrequencyDict {


    private HashMap<String, Double> freq;
    private BufferedReader br;


    void FrequencyDict() {
        freq = new HashMap<String, Double>();
    }

    @Override
    public void load (String filename) {
        try {
            this.br = new BufferedReader(new FileReader(filename));
       	} catch (IOException ex) {
	    ex.printStackTrace();
	}
        calcFrequencies();
    }

    /**
     *
     * @notimplemented
     */
    @Override
    public String[] rankall (String[] choices) {
        HashMap<Double, String> list = new HashMap<Double, String>();
        for (String s : choices) {
            Double d = this.freq.get(s);
            list.put(d, s);
        }
        TreeMap<Double, String> sorted = new TreeMap<Double, String>(list);
        return sorted.values().toArray(new String[sorted.size]);
    }

    @Override
    public HashMap<String, Double> getFrequencies() {
        return freq;
    }

    /**
     *
     * @author j
     */
    private void calcFrequencies() {
	String linio;

	LinkedHashMap<String,Double> listo = new LinkedHashMap<String, Double>(50002);
	try {
	    double maxfreq = -1;
	    while ((linio = br.readLine()) != null) {
		String[] s = linio.trim().split("\\s+");
		if (s.length < 2)
		    continue;
		double lfreq = Integer.parseInt(s[0]);
		if (maxfreq == -1) {
		    maxfreq = lfreq;
		}
		listo.put(s[1], lfreq / maxfreq);

	    }
	    br.close();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
	//montruHashMap(listo);
	this.freq = listo;
    }

}
