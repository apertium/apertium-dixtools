package dictools;

/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Copyright (C) 2008 Enrique Benimeli Bofarull
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

import dics.elements.dtd.DictionaryElement;
import misc.DicFormatE1Line;
import misc.GetBilOmegawiki;
import dics.elements.utils.Msg;
import dictools.DicConsistent;
import dictools.DicCross;
import dictools.DicFilter;
import dictools.DicFindEquivPar;
import dictools.DicFormat;
import dictools.DicGather;
import dictools.DicMerge;
import dictools.DicReader;
import dictools.DicReverse;
import dictools.DicSort;
import dictools.xml.DictionaryReader;
import dictools.Dix2CC;
import dictools.Dix2MDix;
import dictools.Dix2Tiny;
import dictools.apertiumizer.Apertiumizer;
import dictools.cmproc.State;
import dictools.dix2trie.Dix2Trie;
import java.util.ArrayList;
import java.util.Arrays;
import misc.Misc;
import misc.enca.ConvertMF;
import misc.enes.CompleteTranslation;
import misc.enes.PrepareDic;
import misc.eoen.DicFormatE1LineAligned;
import misc.esca.AddSameGender;

/**
 *
 * @author Enrique Benimeli Bofarull
 *
 */
public class ProcessDics extends AbstractDictTool {

    /**
     *
     */
    private String action;


    /**
     * @return the action
     */
    private String getAction() {
        return action;
    }

    /**
     * @param action
     *                the action to set
     */
    private void setAction(String action) {
        this.action = action;
    }
    
    /**
     * Método principal.
     *
     * @param args
     */
    public static void main(final String[] args) {
        
        System.err.println(ProcessDics.class.getName()+".main(\"" + Arrays.toString(args).replaceAll(", ", "\", \"")+"\");");
        
        ProcessDics ps = new ProcessDics();
        String[] args2 = ps.processGenericArgiments(args);
        ps.setArguments(args2);
        try {
            ps.checkAction();
        } catch (OutOfMemoryError oome) {
            ps.msg.err("Error occurred during initialization of VM");
            ps.msg.err("Too small initial heap for new size specified");
            ps.msg.err("Use, for example:");
            ps.msg.err("java -Xms64M -Xmx200M -jar path/to/crossdics.jar <task> [options]");
            System.exit(-1);
        }
       //System.err.println(Arrays.toString(State.freq));
    }

    public String[] processGenericArgiments(String[] a) {
        ArrayList<String> unprocessed = new ArrayList<String>(a.length);

        for (int i = 0; i < a.length; i++) {
          String arg = a[i];

          if (arg.equals("-align")) {
            opt.sectionElementsAligned = true;
          } else if (arg.equalsIgnoreCase("-alignPardef")) {
            opt.pardefElementsAligned = true;
          } else {
            unprocessed.add(arg);
            continue;
          }

          // see if two numbers follows
          try {
            int align1 = Integer.parseInt(a[i+1]);
            int align2 = Integer.parseInt(a[i+2]);
            // OK,  two numbers follows. Interpret as aligment options
            opt.alignP = align1;
            opt.alignR = align2;
            i += 2;
          } catch (Exception e) {
          }            
        }
        a = unprocessed.toArray(new String[unprocessed.size()]);
        return a;
    }
    /**
     *
     *
     */
    public void checkAction() {
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
        if (getAction().equals("cross-param")) {
            this.process_cross_param();
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
        if (getAction().equals("filter")) {
            this.process_filter();
        }
        if (getAction().equals("dix2mdix")) {
            this.process_dix2mdix();
        }
        if (getAction().equals("dix2cc")) {
            this.process_dix2cc();
        }
        if (getAction().equals("dix2tiny")) {
            this.process_dix2tiny();
        }



    }

    /**
     * Shows help
     */
    private void show_help() {
        msg.err("");
        msg.err("Usage: java -jar path/to/apertium-dixtools.jar <task> [options]");
        msg.err("");
        msg.err("where <task> can be:");
        msg.err("   cross:              crosses a set of dictionaries");
        msg.err("   dic-reader:         reads elements from a dictionary");
        msg.err("   dix2trie:           creates a Trie from an existing bilingual dictionary");
        //msg.err("   equiv-paradigms:    finds equivalent paradigms and updates references");
        msg.err("   format:             formats a dictionary");
        msg.err("   format-1line:       formats a dictionary (1 element per line)");
        //msg.err("   get-bil-omegawiki:  gets cheap bilingual dictionaries from Omegawiki.");
        //msg.err("   merge-bil:          merges two bilingual dictionaries");
        msg.err("   merge-morph:        merges two morphological dictionaries");
        //msg.err("   process-xincludes:  processes and expands all xincludes in the dictionary");
        msg.err("   reverse-bil:        reverses a bilingual dictionary");
        msg.err("   sort:               sorts (and groups by category) a dictionary");
        msg.err("");
        msg.err("More information: http://wiki.apertium.org/wiki/Apertium-dixtools");
        msg.err("");
    }

    /**
     * 
     */
    private void process_consistent() {
        if (getArguments().length < 8) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar consistent -bilAB [-r] <bilAB> -bilBC [-r] <bilBC> -monA <mon-A> -monC <monC>");
            System.exit(-1);
        } else {
            DicConsistent tool = new DicConsistent();
            tool.setOpt(getOpt());
            tool.setArguments(getArguments());
            tool.doConsistent();
        }

    }

    /**
     * 
     */
    private void process_merge() {
        if (getArguments().length < 8) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar merge -bilAB [-r] <bilAB> -bilAB2 [-r] <bilAB2> -monA <mon-A> - monA2 <monA2> -monB <monB> -monB2 <monB2>");
            System.exit(-1);
        } else {
            DicMerge tool = new DicMerge();
            tool.setOpt(getOpt());
            tool.setArguments(getArguments());
            tool.doMerge();
        }

    }

    /**
     * 
     */
    private void process_mergemorph() {
        if (getArguments().length < 6) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar merge-morph -monA1 monA1.dix -monA2 monA2.dix -out merged.dix");
            System.exit(-1);
        } else {
            DicMerge tool = new DicMerge();
            tool.setOpt(getOpt());
            tool.setArguments(getArguments());
            tool.doMergeMorph();
        }

    }

    /**
     * 
     */
    private void process_mergebil() {
        if (getArguments().length < 6) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar merge-bil -bilAB1 bilAb1.dix -bilAB2 bilAB2.dix -out merged.dix");
            System.exit(-1);
        } else {
            DicMerge tool = new DicMerge();
            tool.setOpt(getOpt());
            tool.setArguments(getArguments());
            tool.doMergeBil();
        }

    }


    /**
     * 
     */
    private void process_cross_param() {
      
      String arg[] =getArguments();

        System.err.println("arg=" + Arrays.toString(arg));
        System.err.println("arg=" + arg.length);
      
        if (arg.length>=7 && arg.length<=8) {
          String crossmodel = null; 
          if (arg.length==8) crossmodel = arg[7];
          else crossmodel = System.getenv("CROSSDICS_PATH")+"/schemas/cross-model.xml";

          // java ${java_options} $task -bilAB $3 $4 -bilBC $5 $6 -monA $2 -monC $7 -cross-model $crossmodel $9

          String[] arg2 = new String[] {arg[0], "-bilAB", arg[2], arg[3], "-bilBC", arg[4], arg[5], "-monA", arg[1], "-monC", arg[6], "-cross-model",  crossmodel};
          // arg2=[cross-param, -bilAB, -r, apertium-es-ca.es-ca.dix, -bilBC, -r, apertium-en-ca.en-ca.dix, -monA, apertium-es-ca.es.dix, -monC, apertium-en-ca.en.metadix, -cross-model, ../../../../apertium-dixtools/schemas/cross-model.xml]
          // arg  =[cross-param, -bilAB, -r, apertium-es-ca.es-ca.dix, -bilBC, -r, apertium-en-ca.en-ca.dix, -monA, apertium-es-ca.es.dix, -monC, apertium-en-ca.en.metadix, -cross-model, ../../../../apertium-dixtools/schemas/cross-model.xml]          
          System.err.println("arg2=" + Arrays.toString(arg2));
          arg = arg2;
        }
        
        if (arg.length < 8) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar cross-param -bilAB [-r] <bilAB> -bilBC [-r] <bilBC> -monA <mon-A> -monC <monC>");
            System.exit(-1);
        } 

        
        {
            DicCross tool = new DicCross();
            tool.setOpt(getOpt());
            tool.setArguments(arg);
            tool.doCross();
        }

    }

    /**
     * 
     */
    private void process_cross() {
        if (getArguments().length < 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar cross -f <ling-resources.xml> <sl-tl>");
            System.exit(-1);
        } else {
            DicCross tool = new DicCross();
            tool.setOpt(getOpt());
            tool.setArguments(getArguments());
            tool.doCross();
        }
    }

    /**
     * 
     */
    private void process_reverse() {
        if ((getArguments().length > 3) || (getArguments().length < 2)) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar reverse <bil> <bil-reversed>");
            System.exit(-1);
        } else {
            DicReverse tool = new DicReverse();
            tool.setOpt(getOpt());
            tool.setArguments(arguments);
            tool.doReverse();
        }

    }

    /**
     * 
     */
    private void process_format() {
        if (getArguments().length != 4) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar format <-mon|-bil> <dic> <dic-formatted>");
            System.exit(-1);
        } else {
            DicFormat tool = new DicFormat();
            tool.setOpt(getOpt());
            tool.setArguments(arguments);
            tool.doFormat();
        }

    }

    /**
     * 
     */
    private void process_sort() {
        if (getArguments().length != 4) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar sort <-mon|-bil> <dic> <out>");
            System.exit(-1);
        } else {
            DicSort tool = new DicSort();
            tool.setOpt(getOpt());
            tool.setArguments(arguments);
            tool.doSort();
        }
    }

    /**
     * 
     */
    private void process_gather() {
        if (getArguments().length != 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar gather <dic> <dic-sorted>");
            System.exit(-1);
        } else {
            DicGather tool = new DicGather(arguments[1], arguments[2]);
            tool.setOpt(getOpt());
            // dicGather.setArguments(arguments);
            tool.doGather();
        }

    }

    /**
     * 
     */
    private void process_getbilomegawiki() {
        if (getArguments().length != 4) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar get-bil-omegawiki <source-lang> <target-lang> <dic-out>");
            System.exit(-1);
        } else {
            GetBilOmegawiki tool = new GetBilOmegawiki(arguments[1], arguments[2]);
            tool.setOutFileName(arguments[3]);
            tool.printDictionary();
        }
    }

    /**
     * 
     */
    private void process_format1line() {
        if (getArguments().length == 3) {
            DicFormatE1Line tool = new DicFormatE1Line(arguments[1]);
            tool.printXML(arguments[2]);
        } else {
            try {
                if (arguments.length < 4) {
                    throw new IllegalArgumentException("Not enough arguments.");
                }
                DictionaryElement dic = new DictionaryReader(arguments[3]).readDic();
                DicFormatE1LineAligned tool = new DicFormatE1LineAligned(dic);
                tool.setAlignP(Integer.parseInt(arguments[1]));
                tool.setAlignR(Integer.parseInt(arguments[2]));
                tool.printXML(arguments[4]);
            } catch (Exception e) {
                if (e instanceof NumberFormatException) {
                    msg.err("Error " + e.getLocalizedMessage() + " should be a number.");
                } else {
                    msg.err(e.getLocalizedMessage());
                }

                msg.err("");
                msg.err("Usage: format-1line [alignP alignR] <input-dic> <output-dic>");
                msg.err("       where alignP / alignR: column to align <p> and <r> entries. 0 = no indent.");
                msg.err("");
                msg.err("Example: ' format-1line old.dix new.dix '   will give indent a la");
                msg.err("<e><p><l>dum<s n=\"cnjadv\"/></l><r>whereas<s n=\"cnjadv\"/></r></p></e>");
                msg.err("");
                msg.err("Example: ' format-1line 10 50 old.dix new.dix '   will give indent a la");
                msg.err("<e>       <p><l>dum<s n=\"cnjadv\"/></l>            <r>whereas<s n=\"cnjadv\"/></r></p></e>");
                msg.err("");
                msg.err("Example: ' format-1line 0 50 old.dix new.dix '   will give indent a la");
                msg.err("<e><p><l>dum<s n=\"cnjadv\"/></l>                   <r>whereas<s n=\"cnjadv\"/></r></p></e>");
                msg.err("");
                msg.err("Example: ' format-1line 10 0 old.dix new.dix '   will give indent a la");
                msg.err("<e>       <p><l>dum<s n=\"cnjadv\"/></l><r>whereas<s n=\"cnjadv\"/></r></p></e>");
                System.exit(-1);
            }
        }
    }

    /**
     * 
     */
    private void process_dicreader() {
        if (getArguments().length < 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar dic-reader <action> <dic>");
            msg.err("   where <action> can be:");
            msg.err("   list-paradigms:   list of paradigms");
            msg.err("   list-lemmas:      list of lemmas");
            msg.err("   list-definitions: list of <sdef> elemenst");
            msg.err("   list-pairs:       list of pairs (for bilingual dictionaries)");
            msg.err("");
            msg.err("   More information: http://wiki.apertium.org/wiki/Dictionary_reader");
            msg.err("");
            System.exit(-1);
        } else {
            DicReader tool = new DicReader();
            tool.setAction(arguments[1]);
            if (arguments[2].equals("-url")) {
                tool.setUrlDic(true);
                tool.setUrl(arguments[3]);
                System.err.println("URL: " + arguments[3]);
            } else {
                tool.setDic(arguments[2]);
            }
            tool.doit();
        }

    }

    /**
     * 
     */
    private void process_equivparadigms() {
        if (getArguments().length != 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar equiv-paradigms <dic> <dic-out>");
            System.exit(-1);
        } else {
            DicFindEquivPar tool = new DicFindEquivPar(arguments[1]);
            tool.setOutFileName(arguments[2]);
            tool.findEquivalents();
        }

    }

    /**
     * 
     */
    private void process_dix2trie() {
        if (getArguments().length < 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar dix2trie <dic> <lr|rl> [<out>]");
            System.exit(-1);
        } else {
            Dix2Trie tool = new Dix2Trie(arguments[1], arguments[2]);
            if (arguments.length == 4) {
                tool.setOutFileName(arguments[3]);
            }
            tool.buildTrie();
        }
    }

    /**
     * 
     */
    private void process_apertiumize() {
        if (getArguments().length != 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar apertiumize <txt> <out>");
            System.exit(-1);
        } else {
            Apertiumizer tool = new Apertiumizer(arguments[1]);
            tool.setOutFileName(arguments[2]);
            tool.apertiumize();
        }
    }

    /**
     * 
     */
    private void process_convertmf() {
        if (getArguments().length != 4) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar convert-mf <morph-dic> <bil-dic> <out>");
            System.exit(-1);
        } else {
            ConvertMF tool = new ConvertMF(arguments[1], arguments[2]);
            tool.setOutFileName(arguments[3]);
            tool.convert();
        }
    }

    /**
     * 
     */
    private void process_addsamegender() {
        if (getArguments().length != 4) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar add-same-gender <morph-dic> <bil-dic> <out>");
            System.exit(-1);
        } else {
            AddSameGender tool = new AddSameGender(arguments[1], arguments[2]);
            tool.setOutFileName(arguments[3]);
            tool.addSameGender();
        }
    }

    /**
     * 
     */
    private void process_preparedic() {
        if (getArguments().length != 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar preparedic <bil-dic> <missing-entries>");
            System.exit(-1);
        } else {
            PrepareDic tool = new PrepareDic(arguments[2], arguments[1]);
            tool.prepare();
        }
    }

    /**
     * 
     */
    private void process_completetranslation() {
        if (getArguments().length != 4) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar complete-translation <bil> <src-dot> <trans-dot>");
            System.exit(-1);
        } else {
            CompleteTranslation tool = new CompleteTranslation(arguments[1], arguments[2], arguments[3]);
            tool.complete();
        }
    }

    /**
     * 
     */
    private void process_misc() {
        if (getArguments().length != 5) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar misc <dic1> <dic2> <dic3>");
            System.exit(-1);
        } else {
            Misc tool = new Misc(arguments[1], arguments[2], arguments[3], arguments[4]);
            tool.doMisc();
        }
    }

    /**
     * 
     */
    private void process_filter() {
        if (getArguments().length != 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar filter <dicA> <dicB>");
            System.exit(-1);
        } else {
            DicFilter tool = new DicFilter();
            tool.setOpt(getOpt());
            tool.setArguments(this.getArguments());
            tool.doFilter();
        }
    }

    /**
     * 
     */
    private void process_dix2mdix() {
        if (getArguments().length < 2) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar dix2mdix [options] <dic1> [<out>]");
            System.exit(-1);
        } else {
            Dix2MDix tool = new Dix2MDix();
            tool.setArguments(this.getArguments());
            tool.do_convert();
        }
    }

    /**
     * 
     */
    private void process_dix2cc() {
        if (getArguments().length < 2) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar dix2cc <dix> [<cc>]");
            System.exit(-1);
        } else {
            Dix2CC tool = new Dix2CC();
            tool.setArguments(this.getArguments());
            tool.do_convert();
        }
    }

    private void process_dix2tiny() {
        if (getArguments().length < 5) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar dix2tiny <dix> <lang-pair> <lang-pair-text> <platform>");
            msg.err("For example:");
            msg.err("   java -jar path/to/apertium-dixtools.jar dix2tiny apertium-es-ca.es-ca.dix es-ca Spanish-Catalan j2me");
            System.exit(-1);
        } else {
            Dix2Tiny tool = new Dix2Tiny();
            tool.setOpt(getOpt());
            tool.setArguments(this.getArguments());
            tool.do_tiny();
        }
    }
}
