/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Author: Enrique Benimeli Bofarull
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

import misc.DicFormatE1Line;
import misc.GetTranslation;
import dics.elements.utils.Msg;
import dictools.DicConsistent;
import dictools.DicCross;
import dictools.DicFindEquivPar;
import dictools.DicFormat;
import dictools.DicGather;
import dictools.DicMerge;
import dictools.DicReader;
import dictools.DicReverse;
import dictools.DicSort;
import dictools.apertiumizer.Apertiumizer;
import dictools.dix2trie.Dix2Trie;
import misc.Misc;
import misc.enca.ConvertMF;
import misc.enes.CompleteTranslation;
import misc.enes.PrepareDic;
import misc.esca.AddSameGender;

/**
 *
 * @author Enrique Benimeli Bofarull
 *
 */
public class ProcessDics {

   /**
    *
    */
   private String action;
   /**
    *
    */
   private String[] arguments;
   /**
    *
    */
   private Msg msg;

   /**
    * Método principal.
    *
    * @param args
    */
   public static void main(final String[] args) {
      final ProcessDics ps = new ProcessDics(args);
      ps.go();
   }

   /**
    *
    * @param args
    */
   public ProcessDics(final String[] args) {
      msg = new Msg();
      setArguments(args);
   }

   /**
    *
    *
    */
   public final void go() {
      try {
         checkAction();
      } catch (OutOfMemoryError oome) {
         msg.err("Error occurred during initialization of VM");
         msg.err("Too small initial heap for new size specified");
         msg.err("Use, for example:");
         msg.err("java -Xms64M -Xmx200M -jar path/to/crossdics.jar <task> [options]");
         System.exit(-1);
      }
   }

   /**
    *
    *
    */
   public final void checkAction() {
      if (getArguments().length == 0) {
         this.show_help();
         System.exit(-1);
      }
      setAction(getArguments()[0]);

      if (getAction().equals("consistent")) {
         this.process_consistent();
      }
      if (getAction().equals("merge")) {
         this.process_merge();
      }
      if (getAction().equals("merge-morph")) {
         this.process_mergemorph();
      }
      if (getAction().equals("merge-bil")) {
         this.process_mergebil();
      }
      if (getAction().equals("cross")) {
         this.process_cross();
      }
      if (getAction().equals("cross2")) {
         this.process_cross2();
      }      
      if (getAction().equals("reverse")) {
         this.process_reverse();
      }
      if (getAction().equals("format")) {
         this.process_format();
      }
      if (getAction().equals("sort")) {
         this.process_sort();
      }
      if (getAction().equals("gather")) {
         this.process_gather();
      }
      if (getAction().equals("get-bil-omegawiki")) {
         this.process_getbilomegawiki();
      }
      if (getAction().equals("format-1line")) {
         this.process_format1line();
      }
      if (getAction().equals("dic-reader")) {
         this.process_dicreader();
      }
      if (getAction().equals("equiv-paradigms")) {
         this.process_equivparadigms();
      }
      if (getAction().equals("dix2trie")) {
         this.process_dix2trie();
      }
      if (getAction().equals("apertiumize")) {
         this.process_apertiumize();
      }
      if (getAction().equals("convert-mf")) {
         this.process_convertmf();
      }
      if (getAction().equals("add-same-gender")) {
         this.process_addsamegender();
      }
      if (getAction().equals("prepare-dic")) {
         this.process_preparedic();
      }
      if (getAction().equals("complete-translation")) {
         this.process_completetranslation();
      }
      if (getAction().equals("misc")) {
         this.process_misc();
      }

   }

   /**
    * Shows help
    */
   private final void show_help() {
      msg.err("");
      msg.err("Usage: java -jar path/to/crossdics.jar <task> [options]");
      msg.err("");
      msg.err("where <task> can be:");
      msg.err("   cross:              crosses a set of dictionaries");
      msg.err("   dic-reader:         reads elements from a dictionary");
      msg.err("   equiv-paradigms:    finds equivalent paradigms and updates references");
      msg.err("   format:             formats a dictionary");
      msg.err("   get-bil-omegawiki:  gets cheap bilingual dictionaries from Omegawiki.");
      msg.err("   merge-bil:          merges two bilingual dictionaries");
      msg.err("   merge-morph:        merges two morphological dictionaries");
      msg.err("   process-xincludes:  processes and expands all xincludes in the dictionary");
      msg.err("   reverse-bil:        reverses a bilingual dictionary");
      msg.err("   sort:               sorts (and groups by category) a dictionary");
      msg.err("");
      msg.err("More information: http://xixona.dlsi.ua.es/wiki/index.php/Crossdics");
      msg.err("");

   }

   /**
    * 
    */
   private final void process_consistent() {
      if (getArguments().length < 8) {
         msg.err("Usage: java ProcessDics consistent -bilAB [-r] <bilAB> -bilBC [-r] <bilBC> -monA <mon-A> -monC <monC>");
         System.exit(-1);
      } else {
         DicConsistent dicConsistent = new DicConsistent();
         dicConsistent.setArguments(getArguments());
         dicConsistent.doConsistent();
      }

   }

   /**
    * 
    */
   private final void process_merge() {
      if (getArguments().length < 8) {
         msg.err("Usage: java ProcessDics merge -bilAB [-r] <bilAB> -bilAB2 [-r] <bilAB2> -monA <mon-A> - monA2 <monA2> -monB <monB> -monB2 <monB2>");
         System.exit(-1);
      } else {
         DicMerge dicMerge = new DicMerge();
         dicMerge.setArguments(getArguments());
         dicMerge.doMerge();
      }

   }

   /**
    * 
    */
   private final void process_mergemorph() {
      if (getArguments().length < 6) {
         msg.err("Usage: java ProcessDics merge-morph -monA1 monA1.dix -monA2 monA2.dix -out merged.dix");
         System.exit(-1);
      } else {
         DicMerge dicMerge = new DicMerge();
         dicMerge.setArguments(getArguments());
         dicMerge.doMergeMorph();
      }

   }

   /**
    * 
    */
   private final void process_mergebil() {
      if (getArguments().length < 6) {
         msg.err("Usage: java ProcessDics merge-bil -bilAB1 bilAb1.dix -bilAB2 bilAB2.dix -out merged.dix");
         System.exit(-1);
      } else {
         DicMerge dicMerge = new DicMerge();
         dicMerge.setArguments(getArguments());
         dicMerge.doMergeBil();
      }

   }

   /**
    * 
    */
   private final void process_cross() {
      if (getArguments().length < 8) {
         msg.err("Usage: java ProcessDics cross -bilAB [-r] <bilAB> -bilBC [-r] <bilBC> -monA <mon-A> -monC <monC>");
         msg.err("Usage: java ProcessDics cross <-f|-url> <ling-resources.xml>");
         System.exit(-1);
      } else {
         DicCross dicCross = new DicCross();
         dicCross.setArguments(getArguments());
         dicCross.doCross();
      }

   }

   /**
    * 
    */
      private final void process_cross2() {
      if (getArguments().length < 3) {
         msg.err("Usage: java ProcessDics cross <-f|-url> <ling-resources.xml> <sl-tl>");
         System.exit(-1);
      } else {
         DicCross dicCross = new DicCross();
         dicCross.setArguments(getArguments());
         dicCross.doCross();
      }
   }

   /**
    * 
    */
   private final void process_reverse() {
      if ((getArguments().length > 3) || (getArguments().length < 2)) {
         msg.err("Usage: java ProcessDics reverse <bil> <bil-reversed>");
         System.exit(-1);
      } else {
         DicReverse dicReverse = new DicReverse();
         dicReverse.setArguments(arguments);
         dicReverse.doReverse();
      }

   }

   /**
    * 
    */
   private final void process_format() {
      if (getArguments().length != 4) {
         msg.err("Usage: java ProcessDics format <-mon|-bil> <dic> <dic-formatted>");
         System.exit(-1);
      } else {
         final DicFormat dicFormat = new DicFormat();
         dicFormat.setArguments(arguments);
         dicFormat.doFormat();
      }

   }

   /**
    * 
    */
   private final void process_sort() {
      if (getArguments().length != 4) {
         msg.err("Usage: java ProcessDics sort <-mon|-bil> <dic> <out>");
         System.exit(-1);
      } else {
         DicSort dicSort = new DicSort();
         dicSort.setArguments(arguments);
         dicSort.doSort();
      }
   }

   /**
    * 
    */
   private final void process_gather() {
      if (getArguments().length != 3) {
         msg.err("Usage: java ProcessDics gather <dic> <dic-sorted>");
         System.exit(-1);
      } else {
         DicGather dicGather = new DicGather(arguments[1], arguments[2]);
         // dicGather.setArguments(arguments);
         dicGather.doGather();
      }

   }

   /**
    * 
    */
   private final void process_getbilomegawiki() {
      if (getArguments().length != 4) {
         msg.err("Usage: java ProcessDics get-bil-omegawiki <source-lang> <target-lang> <dic-out>");
         System.exit(-1);
      } else {
         GetTranslation gt = new GetTranslation(arguments[1], arguments[2]);
         gt.setOutFileName(arguments[3]);
         gt.printDictionary();
      }
   }

   /**
    * 
    */
   private final void process_format1line() {
      if (getArguments().length != 3) {
         msg.err("Usage: java ProcessDics format-1line <dic> <dic-out>");
         System.exit(-1);
      } else {
         DicFormatE1Line dicFormat = new DicFormatE1Line(arguments[1]);
         dicFormat.printXML(arguments[2]);
      }
   }

   /**
    * 
    */
   private final void process_dicreader() {
      if (getArguments().length < 3) {
         msg.err("Usage: java ProcessDics dic-reader <action> [-url] <dic>");
         System.exit(-1);
      } else {
         DicReader dicReader = new DicReader();
         dicReader.setAction(arguments[1]);
         if (arguments[2].equals("-url")) {
            dicReader.setUrlDic(true);
            dicReader.setUrl(arguments[3]);
            System.out.println("URL: " + arguments[3]);
         } else {
            dicReader.setDic(arguments[2]);
         }
         dicReader.doit();
      }

   }

   /**
    * 
    */
   private final void process_equivparadigms() {
      if (getArguments().length != 3) {
         msg.err("Usage: java ProcessDics equiv-paradigms <dic> <dic-out>");
         System.exit(-1);
      } else {
         DicFindEquivPar finder = new DicFindEquivPar(arguments[1]);
         finder.setOutFileName(arguments[2]);
         finder.findEquivalents();
      }

   }

   /**
    * 
    */
   private final void process_dix2trie() {
      if (getArguments().length < 3) {
         msg.err("Usage: java ProcessDics dix2trie <dic> <lr|rl> [<out>]");
         System.exit(-1);
      } else {
         Dix2Trie dix2trie = new Dix2Trie(arguments[1], arguments[2]);
         if (arguments.length == 4) {
            dix2trie.setOutFileName(arguments[3]);
         }
         dix2trie.buildTrie();
      }
   }

   /**
    * 
    */
   private final void process_apertiumize() {
      if (getArguments().length != 3) {
         msg.err("Usage: java ProcessDics apertiumize <txt> <out>");
         System.exit(-1);
      } else {
         Apertiumizer apertiumizer = new Apertiumizer(arguments[1]);
         apertiumizer.setOutFileName(arguments[2]);
         apertiumizer.apertiumize();
      }
   }

   /**
    * 
    */
   private final void process_convertmf() {
      if (getArguments().length != 4) {
         msg.err("Usage: java ProcessDics convert-mf <morph-dic> <bil-dic> <out>");
         System.exit(-1);
      } else {
         ConvertMF convertMF = new ConvertMF(arguments[1], arguments[2]);
         convertMF.setOutFileName(arguments[3]);
         convertMF.convert();
      }
   }

   private final void process_addsamegender() {
      if (getArguments().length != 4) {
         msg.err("Usage: java ProcessDics add-same-gender <morph-dic> <bil-dic> <out>");
         System.exit(-1);
      } else {
         AddSameGender addSameGender = new AddSameGender(arguments[1], arguments[2]);
         addSameGender.setOutFileName(arguments[3]);
         addSameGender.addSameGender();
      }
   }

   private final void process_preparedic() {
      if (getArguments().length != 3) {
         msg.err("Usage: java ProcessDics preparedic <bil-dic> <missing-entries>");
         System.exit(-1);
      } else {
         PrepareDic pd = new PrepareDic(arguments[2], arguments[1]);
         pd.prepare();
      }
   }

   private final void process_completetranslation() {
      if (getArguments().length != 4) {
         msg.err("Usage: java ProcessDics complete-translation <bil> <src-dot> <trans-dot>");
         System.exit(-1);
      } else {
         CompleteTranslation ct = new CompleteTranslation(arguments[1], arguments[2], arguments[3]);
         ct.complete();
      }
   }

   private final void process_misc() {
      if (getArguments().length != 5) {
         msg.err("Usage: java ProcessDics misc <dic1> <dic2> <dic3>");
         System.exit(-1);
      } else {
         Misc misc = new Misc(arguments[1], arguments[2], arguments[3], arguments[4]);
         misc.doMisc();
      }
   }

   /**
    * @return the action
    */
   private final String getAction() {
      return action;
   }

   /**
    * @param action
    *                the action to set
    */
   private final void setAction(final String action) {
      this.action = action;
   }

   /**
    * @return the arguments
    */
   private final String[] getArguments() {
      return arguments;
   }

   /**
    * @param arguments
    *                the arguments to set
    */
   private final void setArguments(final String[] arguments) {
      this.arguments = arguments;
   }
}
