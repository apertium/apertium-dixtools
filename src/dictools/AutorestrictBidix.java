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

package dictools;



import dictools.utils.DicOpts;
import java.util.HashMap;
import java.util.Iterator;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.Section;
import dictools.AbstractDictTool;
import dictools.frequency.HitParade;
import dictools.utils.DictionaryReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author j
 */
public class AutorestrictBidix extends AbstractDictTool {




//bunzip2 -c corpa/eowiki-granda.crp.txt.bz2 | apertium-destxt | lt-proc eo-en.automorf.bin | tr '^' '\012' | cut -d/ -f2 | cut -d'<' -f1 | sed -e 's/\W//g' | sort | uniq -c | sort -nr > hitparade-eo-granda.txt




//bunzip2 -c corpa/enwiki.crp.txt.bz2 | apertium-destxt | lt-proc en-eo.automorf.bin | tr '^' '\012' | cut -d/ -f2 | cut -d'<' -f1 | sed -e 's/[^[:alpha:][:space:]]//g' | sort | uniq -c | sort -nr > hitparade-en-granda.txt


//(zcat corpa/en.crp.txt.gz_2; bunzip2 -c corpa/enwiki.crp.txt.bz2; zcat corpa/en.crp.txt.gz_org_reuters) | apertium-destxt | lt-proc en-eo.automorf.bin | apertium-pretransfer | tr '^' '\012' | sed -e 's/^.*\///g' | tr '$' '<' | cut -d'<' -f1 | sed -e 's/\*//g' | sed -e 's/\W*$//g' | sed -e 's/# / /g' | sort | uniq -c | sort -nr > hitparade-en-granda.txt


  @Override
  public String toolHelp() {
    return "autorestrict [-l hitparade-left.txt] [-r hitparade-right.txt] [-lift] input.dix output.dix\n\n"+
        "Restricts lexical ambiguities in a bidix to avoid multiple possible translations, \n"+
        "by using frequency list in the target language.\n"+
        " -l and -r specify directions. Hitparade files may be empty (/dev/null)\n" +
        " -redo will remove restrictions in the relevant direction, and redo them according to hitparade\n" +
        " -lift will also lift (remove) unneeded restrictions in the relevant direction(s)\n" +
        " -nocomments Suppress comments in output.dix about reasons for restricted entries\n" +
        " -commentsbefore Put the comments about reasons for restriction on the line above the entriy\n" +
        "\n" +
        "Examples:\n" +
        " autorestrict -l /dev/null -r hitparade-en.txt  ambigious.eo-en.dix  out.dix\n"+
        "         restricts in both directions. Esperanto hitparade is empty, so topmost entries will be used.\n"+
        " autorestrict -l hitparade-eo.txt   ambigious.eo-en.dix  out.dix\n"+
        "         restricts in English->Esperanto direction\n"+
        ""
        ;
  }

  public HitParade leftLanguageHitparade = null;
  public HitParade rightLanguageHitparade = null;
  public boolean alsoLiftUnneededRestrictions = false;
  public boolean redoRestrictions = false;
  public boolean noComments = false;
  public boolean verbose = true;
  public boolean commentsbefore = false;

  @Override
  public void executeTool() throws IOException {

      for (int i=1;i<arguments.length; i++) {
        if (arguments[i].equals("-l")) {
          leftLanguageHitparade = new HitParade(arguments[i+1]);
          removeArgs(i, 2);
          i--;
        }
        else if (arguments[i].equals("-r")) {
          rightLanguageHitparade = new HitParade(arguments[i+1]);
          removeArgs(i, 2); 
          i--;
        }
        else if (arguments[i].equals("-lift")) {
          alsoLiftUnneededRestrictions = true;
          removeArgs(i, 1);
          i--;
        }
        else if (arguments[i].equalsIgnoreCase("-redo")) {
          redoRestrictions = true;
          removeArgs(i, 1);
          i--;
        }
        else if (arguments[i].startsWith("-nocomment")) {
          noComments = true;
          removeArgs(i, 1);
          i--;
        }
        else if (arguments[i].equalsIgnoreCase("-commentsbefore")) {
          commentsbefore = true;
          removeArgs(i, 1);
          i--;
        }
      }
      if (arguments.length != 3) failWrongNumberOfArguments(arguments);

      Dictionary dic = new DictionaryReader(arguments[1]).readDic();

      reviseRestrictions(dic);

      dic.printXMLToFile(arguments[2], opt);
  }


  public void reviseRestrictions(Dictionary dic, boolean verbose, boolean alsoLiftUnneededRestrictions) {
      this.alsoLiftUnneededRestrictions = alsoLiftUnneededRestrictions;
      this.verbose = verbose;
      reviseRestrictions(dic);
  }

  public void reviseRestrictions(Dictionary dic) {

    HashMap<String, E> emapLtoR=new HashMap<String, E>();
    HashMap<String, E> emapRtoL=new HashMap<String, E>();
    //HashSet<String> restrics = new HashSet<String>();


    for (Section section : dic.sections) {
      for (E e : section.elements) {

        if (!e.containsRegEx()) {
          //System.err.println("======="+e.toString()+"========");
          // L -> R
          if (rightLanguageHitparade!=null)
            checkEarlierAndRestrict("LR",emapLtoR, e, rightLanguageHitparade, redoRestrictions);

          // R -> L
          if (leftLanguageHitparade!=null)
            checkEarlierAndRestrict("RL",emapRtoL, e, leftLanguageHitparade, redoRestrictions);
        }
      }

      for (E e : emapLtoR.values()) {
        if (!isAllowed("LR", e)) {
          if (verbose) {
            System.err.println("LR can be allowed "+e);
          }
          if (alsoLiftUnneededRestrictions) {
            setYesIsAllowed(e, "LR");
          }
        }
      }

      for (E e : emapRtoL.values()) {
        if (!isAllowed("RL", e)) {
          if (verbose) {
            System.err.println("RL can be allowed "+e);
          }
          if (alsoLiftUnneededRestrictions) {
            setYesIsAllowed(e, "RL");
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
              String txt = " autorestricted ("+reasonOfRestriction+").";
              System.err.println("Restricted  "+ee+" due to: "+reasonOfRestriction);
              if (!noComments) {
                if (commentsbefore) {
                  if (ee.processingComments.length()>0) ee.processingComments += " ; ";
                  ee.processingComments += txt;
                } else {
                  ee.appendCharacterData = "<!-- "+txt +"-->"+ee.appendCharacterData;;
                }
              }
            }
          }
        }
      }
    }
  }

  public static boolean isAllowed(String direction, E ee) {
    if ("yes".equals(ee.ignore)) return false;
    if("LR".equals(direction) && ee.isAnyLR()) return false;
    if("RL".equals(direction) && ee.isAnyRL()) return false;
    String restric = ee.restriction;
    if (restric==null) return true;
    if (restric.equals(restric)) return true;
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


  public static boolean setNoIsNotAllowed(E ee, String direction) {
    if ("yes".equals(ee.ignore)) return false;

    if (direction.equals(ee.restriction)) {
       ee.ignore="yes";
       ee.restriction=null;
       return true;
    } else {
      String res = reverseDir(direction);
      if (res.equals(ee.restriction)) return false;
      ee.restriction=res;
      return true;
    }      
  }

  
  
  /**
   * Check if
   * @param direction eg LR - left to right
   * @param entryMap
   * @param entry
   * @param hitparade
   * @param alsoLiftAllRestrictions
   */
  private static void checkEarlierAndRestrict(String direction, HashMap<String, E> entryMap,
      E entry, HitParade targetHitparade, boolean redoRestrictions) {
    

    String sourceSide = direction.substring(0, 1);
    String targetSide = direction.substring(1, 2);
    String key =  entry.getStreamContentForSide(sourceSide);

    E oldEntry=entryMap.get(key);

    if (oldEntry==null) {
      entryMap.put(key, entry);
      return;  // No former entry - nothing to bother
    } 

    // existing entry restricted in this direction - nothing to bother
    if (!redoRestrictions && !isAllowed(direction, oldEntry) && isAllowed(direction, entry)) {
      entryMap.put(key, entry); 
      return;
    } 

    // new entry restricted in this direction - nothing to bother
    if (!redoRestrictions && !isAllowed(direction, entry)) return;

    String entrylem = entry.getLemmaForSide(targetSide);
    String oldEntrylem = oldEntry.getLemmaForSide(targetSide);
    Double entryFreq = targetHitparade.frequencies.get(entrylem);
    Double oldEntryFreq = targetHitparade.frequencies.get(oldEntrylem);

    System.err.println(entrylem +" entryFreq = " + entryFreq + "   vs "+oldEntryFreq+" " + oldEntrylem);

    if (entryFreq==null || oldEntryFreq != null && oldEntryFreq>=entryFreq) {
      restrictAndUnrestrict(entry, oldEntry,direction); // restrict new entry
    } else {
      restrictAndUnrestrict(oldEntry,entry, direction); // restrict old entry
      entryMap.put(key, entry);  // new entry is preferred, so put it in the map in case there are more words that needs to compare with it
    }
  }




  private static void restrictAndUnrestrict(E entryToBeRemoved, E entryToRetain, String direction) {

    setYesIsAllowed(entryToRetain, direction); // needed if redoRestrictions==true


    System.err.println("      entryToBeRemoved = " + entryToBeRemoved);
    boolean changed = setNoIsNotAllowed(entryToBeRemoved, direction);

    System.err.println(changed+" entryToBeRemoved = " + entryToBeRemoved);
    if (changed) {
      String oldReasonOfRestriction=entryToBeRemoved.temp;
      String existingEeStr=entryToRetain.toString();
      //System.err.println("LR: Dobbelt indgang "+existingEe+"   "+ee);
      if (entryToBeRemoved.restriction==null) {
        assert (oldReasonOfRestriction==null);
        entryToBeRemoved.temp=existingEeStr;
        //ee.restriction=reverseDir(direction));
      } else {
        if (oldReasonOfRestriction==null) {
          entryToBeRemoved.temp=existingEeStr;
        } else {
          String existingEeStrChop=existingEeStr.substring(existingEeStr.indexOf('<'));
          String oldReasonOfRestrictionChop=oldReasonOfRestriction.substring(oldReasonOfRestriction.indexOf('<'));
          //System.err.println(existingEeStrChop+ ".equals? " +oldReasonOfRestrictionChop);
          if (existingEeStrChop.equals(oldReasonOfRestrictionChop)) {
            // Exactly the same entry has been before. Just delete it
            entryToBeRemoved.temp="DELETE";
          } else {
            entryToBeRemoved.temp=existingEeStr+" "+oldReasonOfRestriction;
          }
        }
      }
    }
  }

  public static void main(String[] a) throws IOException {
    dictools.ProcessDics.main(new String[] {"autorestrict", "-l", "test/hitparade-eo.txt",  "-r", "test/hitparade-en.txt", "test/missing_restrictions.eo-en.dix", "-"});
    //dictools.ProcessDics.main(new String[] {"autorestrict",  "-r", "test/hitparade-en.txt", "test/missing_restrictions.eo-en.dix", "-"});
  }
}
