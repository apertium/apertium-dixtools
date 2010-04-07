/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package misc;


import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Scanner;

/**
 * Vidu dokumentadon cxe: http://wiki.apertium.org/wiki/Kompletigi_vortaron
 * @author Jacob Nordfalk
 */
public class Kompletigu  {


  public static void main(final String[] args) throws Exception {
    ArrayList<String[]> reguloj = new ArrayList<String[]>();
    {
      Scanner sc = new Scanner(new File(args.length>=2? args[0]:"/home/j/esperanto/a/incubator/apertium-eo-fr/kompletigaj-reguloj-eo.txt"));
      while (sc.hasNextLine()) {
          String regexp = sc.nextLine().trim();
          if (regexp.isEmpty()) continue;
          if (regexp.startsWith("#")) continue;

          String entry = sc.nextLine();
          String[] r = new String[]{ "^"+regexp+"$", entry };
          reguloj.add(r);

          System.err.println("regulo: " + r[0] + "  --> " + r[1]);
      }
    }

    LinkedHashSet<String> output = new LinkedHashSet<String>();

    Scanner sc = new Scanner(new File(args.length>=2? args[1]:"/home/j/esperanto/a/incubator/apertium-eo-fr/mankas-eo.txt"));
    vorto:
    while (sc.hasNextLine()) {
        String linio = sc.nextLine().trim();

        //System.err.println("linio = " + linio);
        for (String r[] : reguloj) {

          String res = linio.replaceFirst(r[0], r[1]);
          // se estis trafo de la regulo, ni aldonu gxin:
          if (!res.equals(linio)) {

            if (!res.trim().isEmpty()) {
              System.err.println("Regulo "+r[0]+" donas: "+ linio + "   -->  " + res);
              output.add(res);
            }
            continue vorto;
          }
        }
        System.err.println("Neniu regulo trafis por " + linio);
      }

    for (String l : output) {
      System.out.println(l);
    }
  }


}

