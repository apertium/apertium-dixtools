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
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.TreeMap;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;

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
public class HitParade  {

    /**
     * Rank an array of words against a frequency dictionary
     * @param choices An array of words to be ranked
     * @return choices ranked by frequency. Most frequent will be first in the list
     */
    public String[] rankWords (String[] choices) {
        HashMap<Double, String> list = new HashMap<Double, String>();
        for (String s : choices) {
            Double d = this.frequencies.get(s);
            list.put(d, s);
        }
        TreeMap<Double, String> sorted = new TreeMap<Double, String>(list);
        ArrayList<String> al = new ArrayList(sorted.values());
        Collections.reverse(al);

        System.err.println("list = " + list);
        System.err.println("al = " + al);
        return (String[]) al.toArray(new String[al.size()]);
    }

    /**
     * All words and their frequencies (a hashmap of all words with their frequency)
     */
    public HashMap<String,Double> frequencies;

    public HitParade(String file) throws IOException {
      frequencies = readHitparadeFile(file);
    }



  public static HashMap<String, Double> readHitparadeFile(String dosiernomo) throws IOException {
    //System.out.println("Legas "+dosiernomo);
    BufferedReader br;
    String linio;

    LinkedHashMap<String, Double> listo=new LinkedHashMap<String, Double>(50002);
    br=new BufferedReader(new FileReader(dosiernomo));
    double maxfreq=-1;
    while ((linio=br.readLine())!=null) {
      linio = linio.trim();
      int n = linio.indexOf(' ');
      if (n==-1) continue;
      double freq=Integer.parseInt(linio.substring(0, n));
      if (maxfreq==-1) {
        maxfreq=freq;
      }
      listo.put(linio.substring(n+1), freq/maxfreq);

    }
    br.close();
    //montruHashMap(listo);
    return listo;
  }

}
