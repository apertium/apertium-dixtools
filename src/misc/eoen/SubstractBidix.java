/*
 * Copyright (C) 2008 Dana Esperanta Junulara Organizo http://dejo.dk/
 * Author: Jacob Nordfalk
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

package misc.eoen;



import dics.elements.dtd.ContentElement;
import java.util.HashMap;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.LElement;
import dics.elements.dtd.PElement;
import dics.elements.dtd.ParElement;
import dics.elements.dtd.RElement;
import dics.elements.dtd.SectionElement;
import dics.elements.dtd.TextElement;
import dics.elements.utils.DicOpts;
import dictools.xml.DictionaryReader;
import java.util.HashSet;
import java.util.Iterator;
import misc.DicFormatE1Line;

/**
 *
 * @author j
 */
public class SubstractBidix {

      public static void main(final String[] args) {
        DicOpts opt  = DicOpts.STD_ALIGNED;
        //DictionaryElement dic = new DictionaryReader("../apertium-eo-en/apertium-lille.eo-en.dix").readDic();
        DictionaryElement dic = new DictionaryReader("../apertium-eo-en/apertium-eo-en.eo-en.dix").readDic();
        //DictionaryElement dic = new DictionaryReader("../apertium-eo-en/slet.eo-en.dix").readDic();
        dic.reportMetrics();
        dic.printXML("before-clean.dix", opt);
        
        boolean verbose = true;
        boolean liftUnneededRestrictions = true;
        SubstractBidix.reviseRestrictions(dic, verbose, liftUnneededRestrictions);
               
        //System.err.println("Updated morphological dictionary: '" + out + "'");
        //dic.printXML(out);
        
        dic.printXML("after-clean.dix", opt);
      }

  public static void reviseRestrictions(DictionaryElement dic, boolean verbose, boolean alsoLiftUnneededRestrictions) {

    HashMap<String, EElement> hmLR=new HashMap<String, EElement>();
    HashMap<String, EElement> hmRL=new HashMap<String, EElement>();
    //HashSet<String> restrics = new HashSet<String>();
    for (SectionElement section : dic.getSections()) {
      for (EElement ee : section.getEElements()) {
        //setYesIsAllowed(ee, "LR"); // XXXX
        //setYesIsAllowed(ee, "RL"); // XXXX
        if (!ee.isRegEx()) {
          ContentElement l=ee.getSide("L");
          ContentElement r=ee.getSide("R");
          //System.err.println("======="+ee.toString()+"========");
          // L -> R
          checkEarlierAndRestrict("LR", l, hmLR, ee);

          // R -> L
          checkEarlierAndRestrict("RL", r, hmRL, ee);
        }
      }

      for (EElement ee : hmLR.values()) {
        if (!isAllowed("LR", ee)) {
          if (verbose) {
            System.err.println("LR restic can be lifted "+ee);
          }
          if (alsoLiftUnneededRestrictions) {
            setYesIsAllowed(ee, "LR");
          }
        }
      }

      for (EElement ee : hmRL.values()) {
        if (!isAllowed("RL", ee)) {
          if (verbose) {
            System.err.println("RL restic can be lifted "+ee);
          }
          if (alsoLiftUnneededRestrictions) {
            setYesIsAllowed(ee, "RL");
          }
        }
      }



      // Transfer reasons from temp to comment
      Iterator<EElement> eei=section.getEElements().iterator();
      while (eei.hasNext()) {
        EElement ee=eei.next();
        if (!ee.isRegEx()) {
          String reasonOfRestriction=ee.getTemp();
          ee.setTemp(null);
          if (reasonOfRestriction!=null) {
            if ("DELETE".equals(reasonOfRestriction)) {
              if (!ee.hasPrependorAppendData()) {
                eei.remove();
                   System.err.println("DELETE "+ee);
                } else {
                   System.err.println("Should DELETE "+ee+" but hasPrependorAppendData, so set to ignore instead");
                   ee.setIgnore("yes");
                }

            } else {
              String c="Already is "+reasonOfRestriction;
              System.err.println("x "+c);
              if (ee.getProcessingComments()!=null && !ee.getProcessingComments().trim().isEmpty()) {
                c=c+" ; "+ee.getProcessingComments();
              }
              System.err.println("x "+c);
              ee.setProcessingComments(c);
            }
          }
        }
      }
    }

    //System.err.println("Updated morphological dictionary: '" + out + "'");
    //dic.printXML(out);
  }

  public static boolean isAllowed(String direction, EElement ee) {
    if ("yes".equals(ee.getIgnore())) return false;
    String restric = ee.getRestriction();    
    if (restric==null) return true;
    if (restric.equals(direction)) return true;
    return false;    
  }


  public  static String reverseDir(String direction) {
    return direction.equals("RL")?"LR":"RL";
  }
  
  public static void setYesIsAllowed(EElement ee, String direction) {
    boolean i = "yes".equals(ee.getIgnore());
    String restric = ee.getRestriction();    

    if (i) {
       if (restric!=null) throw new IllegalStateException(restric);
       ee.setRestriction(direction);
       ee.setIgnore(null);
       return;
    }

    if (reverseDir(direction).equals(restric)) {
       ee.setRestriction(null);
       return;
    }
  }


  public static void setNoIsNotAllowed(EElement ee, String direction) {
    boolean i = "yes".equals(ee.getIgnore());
    String restric = ee.getRestriction();    

    if (i) {
       return;
    }

    if (direction.equals(restric)) {
       ee.setIgnore("yes");
       ee.setRestriction(null);
       return;
    } else {
       ee.setRestriction(reverseDir(direction));      
    }      
  }

  
  
      
  public static void checkEarlierAndRestrict(String direction, ContentElement l, HashMap<String, EElement> hmLR, EElement ee) {
    //if (isAllowed(direction, ee)) return;
    
    String key=l.toString();
    EElement existingEe=hmLR.get(key);
    if (existingEe==null) {
      hmLR.put(key, ee);
      return;
    } 
    
    if (!isAllowed(direction, existingEe) && isAllowed(direction, ee)) {
      hmLR.put(key, ee);
      return;
    } 
    
    if (!isAllowed(direction, ee)) return;

    String oldReasonOfRestriction=ee.getTemp();
    String existingEeStr=existingEe.toString();
    //System.err.println("LR: Dobbelt indgang "+existingEe+"   "+ee);
    if (ee.getRestriction() ==null) {
      assert (oldReasonOfRestriction==null);
      ee.setTemp(existingEeStr);
      //ee.setRestriction(reverseDir(direction));
    } else {
      if (oldReasonOfRestriction == null) {
        //ee.setProcessingComments("Already is "+existingEeStr);
        ee.setTemp(existingEeStr);
        
      } else {
        String existingEeStrChop = existingEeStr.substring(existingEeStr.indexOf('<'));
        String oldReasonOfRestrictionChop = oldReasonOfRestriction.substring(oldReasonOfRestriction.indexOf('<'));
        

        System.err.println(existingEeStrChop+ ".equals? " +oldReasonOfRestrictionChop);

        if (existingEeStrChop.equals(oldReasonOfRestrictionChop)) {
          
          // Exactly the same entry has been before. Just delete
          ee.setTemp("DELETE");
        } else {
          //ee.setProcessingComments("Already are "+existingEeStr+" "+oldReasonOfRestriction);
          ee.setTemp(existingEeStr+" "+oldReasonOfRestriction);
        }
      }          
    }
    setNoIsNotAllowed(ee,direction);
  }
}
