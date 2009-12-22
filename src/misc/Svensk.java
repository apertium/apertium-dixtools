/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package misc;

import dics.elements.dtd.*;
import dictools.utils.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Jacob Nordfalk
 */
public class Svensk {

  public static void main(final String[] args) throws Exception {
    Dictionary dic = new DictionaryReader("/home/j/esperanto/a/apertium-sv-da/apertium-sv-da.sv.dix").readDic();

    for (Iterator<Pardef> pari =  dic.pardefs.elements.iterator(); pari.hasNext(); ) {
      Pardef par = pari.next();

      if (par.name.equals("S__case")) continue;
      // put <par n="S__case"/> på
      ydre:
      for (Iterator<E> ei =  par.elements.iterator(); ei.hasNext(); ) {
        E e1 = ei.next();
        for (E e2 : par.elements) {
          if (e1 == e2) continue;

            if (e2.children.size()==1 &&  (e1.getStreamContentForSide("L")).equals(e2.getStreamContentForSide("L")  + "s")) {

             ArrayList<S> sl1 =   e1.getSymbols("R");
             ArrayList<S> sl2 =   e2.getSymbols("R");
             S s1 =  sl1.remove(sl1.size()-1);
             S s2 =  sl2.remove(sl2.size()-1);
             if ((sl1.equals(sl2) && s1.is("gen")) && s2.is("nom")) {
                System.err.println("e1 = " + e1 + " " + e2);
                P p2 = (P) e2.children.get(0);
                p2.r.children.remove(p2.r.children.size()-1);
                e2.children.add(new Par("S__case"));
                ei.remove();
                continue ydre;
             }
            }
        }
      }

      ydre:
      for (Iterator<E> ei =  par.elements.iterator(); ei.hasNext(); ) {
        E e1 = ei.next();
        for (E e2 : par.elements) {
          if (e1 == e2) continue;

            // erstat sg pl  med sp
            if (e1.getStreamContentForSide("L").equals(e2.getStreamContentForSide("L"))) {

             ArrayList<S> sl1 =   e1.getSymbols("R");
             ArrayList<S> sl2 =   e2.getSymbols("R");
             
             if (sl1.contains(S.getInstance("ind")) && sl1.remove(S.getInstance("sg")) && sl2.remove(S.getInstance("pl")) && sl1.equals(sl2)) {
                System.err.println("e1 = " + e1 + " " + e2);
                System.err.println("e1 = " + sl1 + " " + sl2);
                for (DixElement el2 : e2.children) {
                  if (el2 instanceof P) {
                    ElementList elems = ((P) el2).r.children;
                    int i = elems.indexOf(S.getInstance("pl"));
                    elems.set(i, S.getInstance("sp"));
                  }
                }
                ei.remove();
                continue ydre;
             }
            }
        }
      }

//      System.out.println(par.elements.get(0).toString());

    }
      dic.printXMLToFile("/tmp/x.dix",DicOpts.STD_ALIGNED_MONODIX);
  }
}
