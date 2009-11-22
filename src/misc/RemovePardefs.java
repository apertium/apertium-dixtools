/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package misc;

import dics.elements.dtd.*;
import dictools.utils.*;
import java.io.*;
import java.util.Iterator;

/**
 *
 * @author Jacob Nordfalk
 */
public class RemovePardefs {

  public static void main(final String[] args) throws Exception {
    Dictionary dic = new DictionaryReader("/home/j/esperanto/apertium/apertium-sl-mk/apertium-sl-mk.sl.dix").readDic();

    

    for (Iterator<Pardef> pari =  dic.pardefs.elements.iterator(); pari.hasNext(); ) {
      Pardef par = pari.next();
      System.out.println(par.elements.get(0).toString());

      if (par.name.endsWith("__adv")) pari.remove();
    }
      dic.printXMLToFile("/home/j/esperanto/apertium/apertium-sl-mk/apertium-sl-mk.sl.dix2",DicOpts.STD_ALIGNED_MONODIX);
  }
}
