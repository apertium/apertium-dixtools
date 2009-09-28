/*
 * Copyright (C) 2008 Dana Esperanta Junulara Organizo http://dejo.dk/
 * Author: Jacob Nordfalk
 * 
 * This program isFirstSymbol free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program isFirstSymbol distributed in the hope that it will be useful, but
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



import java.util.HashMap;
import java.util.Iterator;

import dics.elements.dtd.ContentElement;
import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.Section;
import dics.elements.utils.DicOpts;
import dictools.xml.DictionaryReader;

/**
 *
 * @author j
 */
public class SubstractBidix {


  public static void reviseRestrictions(Dictionary dic, boolean verbose, boolean alsoLiftUnneededRestrictions) {

    HashMap<String, E> hmLR=new HashMap<String, E>();
    HashMap<String, E> hmRL=new HashMap<String, E>();
    //HashSet<String> restrics = new HashSet<String>();
    for (Section section : dic.sections) {
      for (E e : section.elements) {
        //setYesIsAllowed(ee, "LR"); // XXXX
        //setYesIsAllowed(ee, "RL"); // XXXX
        if (!e.containsRegEx()) {
          ContentElement leftContent=e.getFirstPart("L");
          ContentElement rightContent=e.getFirstPart("R");
          //System.err.println("======="+ee.toString()+"========");
          // L -> R
          checkEarlierAndRestrict("LR", leftContent, hmLR, e);

          // R -> L
          checkEarlierAndRestrict("RL", rightContent, hmRL, e);
        }
      }

      for (E e : hmLR.values()) {
        if (!isAllowed("LR", e)) {
          if (verbose) {
            System.err.println("LR restic can be lifted "+e);
          }
          if (alsoLiftUnneededRestrictions) {
            setYesIsAllowed(e, "LR");
          }
        }
      }

      for (E ee : hmRL.values()) {
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
      Iterator<E> eei=section.elements.iterator();
      while (eei.hasNext()) {
        E ee=eei.next();
        if (!ee.containsRegEx()) {
          String reasonOfRestriction=ee.temp;
          ee.temp=null;
          if (reasonOfRestriction!=null) {
            if ("DELETE".equals(reasonOfRestriction)) {
              if (!ee.hasPrependorAppendData()) {
                eei.remove();
                   System.err.println("DELETE "+ee);
                } else {
                   System.err.println("Should DELETE "+ee+" but hasPrependorAppendData, so set to ignore instead");
                   ee.ignore="yes";
                }

            } else {
              if (ee.processingComments.length()>0) ee.processingComments += " ; ";

              ee.processingComments +="Already is "+reasonOfRestriction;
              System.err.println("x "+ee.processingComments);
            }
          }
        }
      }
    }

    //System.err.println("Updated morphological dictionary: '" + out + "'");
    //dic.printXML(out);
  }

  public static boolean isAllowed(String direction, E ee) {
    if ("yes".equals(ee.ignore)) return false;
    String restric = ee.restriction;    
    if (restric==null) return true;
    if (restric.equals(direction)) return true;
    return false;    
  }


  public  static String reverseDir(String direction) {
    return direction.equals("RL")?"LR":"RL";
  }
  
  public static void setYesIsAllowed(E ee, String direction) {
    boolean i = "yes".equals(ee.ignore);
    String restric = ee.restriction;    

    if (i) {
       if (restric!=null) throw new IllegalStateException(restric);
       ee.restriction=direction;
       ee.ignore=null;
       return;
    }

    if (reverseDir(direction).equals(restric)) {
       ee.restriction=null;
       return;
    }
  }


  public static void setNoIsNotAllowed(E ee, String direction) {
    boolean i = "yes".equals(ee.ignore);
    String restric = ee.restriction;    

    if (i) {
       return;
    }

    if (direction.equals(restric)) {
       ee.ignore="yes";
       ee.restriction=null;
       return;
    } else {
       ee.restriction=reverseDir(direction);      
    }      
  }

  
  
      
  private static void checkEarlierAndRestrict(String direction, ContentElement contentElement, HashMap<String, E> hmLR, E entry) {
    //if (isAllowed(direction, ee)) return;
    
    String key=contentElement.toString();

    E existingEntry=hmLR.get(key);
    if (existingEntry==null) {
      hmLR.put(key, entry);
      return;
    } 
    
    if (!isAllowed(direction, existingEntry) && isAllowed(direction, entry)) {
      hmLR.put(key, entry);
      return;
    } 
    
    if (!isAllowed(direction, entry)) return;

    String oldReasonOfRestriction=entry.temp;
    String existingEeStr=existingEntry.toString();

    //System.err.println("LR: Dobbelt indgang "+existingEe+"   "+ee);
    if (entry.restriction ==null) {
      assert (oldReasonOfRestriction==null);
      entry.temp=existingEeStr;
      //ee.restriction=reverseDir(direction));
    } else {
      if (oldReasonOfRestriction == null) {
        //ee.setProcessingComments("Already isFirstSymbol "+existingEeStr);
        entry.temp=existingEeStr;
        
      } else {
        String existingEeStrChop = existingEeStr.substring(existingEeStr.indexOf('<'));
        String oldReasonOfRestrictionChop = oldReasonOfRestriction.substring(oldReasonOfRestriction.indexOf('<'));
        

        System.err.println(existingEeStrChop+ ".equals? " +oldReasonOfRestrictionChop);

        if (existingEeStrChop.equals(oldReasonOfRestrictionChop)) {
          
          // Exactly the same entry has been before. Just delete
          entry.temp="DELETE";
        } else {
          //ee.setProcessingComments("Already are "+existingEeStr+" "+oldReasonOfRestriction);
          entry.temp=existingEeStr+" "+oldReasonOfRestriction;
        }
      }          
    }
    setNoIsNotAllowed(entry,direction);
  }
}
