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

import dictools.enhancer.DictEnhancer;
import dictools.cross.DicCross;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import misc.DicFormatE1Line;
import misc.GetBilOmegawiki;
import misc.Misc;
import misc.enca.ConvertMF;
import misc.enes.CompleteTranslation;
import misc.enes.PrepareDic;
import misc.eoen.DicFormatE1LineAligned;
import misc.esca.AddSameGender;
import dics.elements.dtd.Dictionary;
import dictools.utils.DicOpts;
import dictools.apertiumizer.Apertiumizer;
import dictools.dix2trie.Dix2Trie;
import dictools.utils.DictionaryReader;
import dictools.speling.Speling;
import dictools.columnar.Columnar;
import dictools.AutorestrictBidix;
import dictools.guessparadigm.questions.Questions;
import dictools.guessparadigm.questions.SortedSetOfCandidates;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 *
 * @author Enrique Benimeli Bofarull
 *
 */
public class ProcessDics extends AbstractDictTool {

    /**
     * Método principal.
     *
     * @param args
     */
    public static void main(final String[] args) throws IOException {
      //com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOpt getopt = new com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOpt(args,null);
        
        //System.err.println(ProcessDics.class.getName()+".main(\"" + Arrays.toString(args).replaceAll(", ", "\", \"")+"\");");
        //System.err.println(System.getProperties());
        //System.err.println(System.getenv());
        
        ProcessDics ps = new ProcessDics();
        try {
            ps.processArguments(args);
        } catch (OutOfMemoryError oome) {
            ps.msg.err("Error occurred during initialization of VM");
            ps.msg.err("Too small initial heap for new size specified");
            ps.msg.err("Use, for example:");
            ps.msg.err("java -Xms200M -Xmx500M -jar path/to/crossdics.jar <task> [options]");
            System.exit(-1);
        }
       //System.err.println(Arrays.toString(State.freq));
    }

  private static String usage =  "Usage: apertium-dixtools ";


    /**
     * Processes generic arguments applyable to all tools, like -debug and -align
     * @param args
     * @return
     */
    private String[] processGenericArguments(String[] args) {
        ArrayList<String> unprocessed = new ArrayList<String>(args.length);
        //DicOpts opt = this;

        for (int i = 0; i < args.length; i++) {
          String arg = args[i].toLowerCase();

          boolean align=false, alignPardef=false;
          if (arg.equals("-debug")) {
            msg.setDebug(true);
          } else if (arg.equalsIgnoreCase("-noHeader")) {
            opt.noHeaderAtTop=true;
          } else if (arg.equalsIgnoreCase("-noProcComments")) {
            opt.noProcessingComments=true;
          } else if (arg.equalsIgnoreCase("-stripEmptyLines")) {
            opt.stripEmptyLines = true;
          } else  if (arg.startsWith("-alignmon")) { // -alignMonodix
            opt.copyAlignSettings(DicOpts.STD_ALIGNED_MONODIX);
          } else if (arg.startsWith("-alignbi")) { // -alignBidix
            opt.copyAlignSettings(DicOpts.STD_ALIGNED_BIDIX);
          } else  if (arg.equalsIgnoreCase("-align")) {
            align = opt.sectionElementsAligned = true;
            opt.detectAlignmentFromSource = false;
          } else if (arg.startsWith("-alignpar")) {
            alignPardef = opt.pardefElementsAligned = true;
            opt.detectAlignmentFromSource = false;
          } else if (arg.startsWith("-noalign")) {
            opt.copyAlignSettings(DicOpts.STD_NONALIGNED_XML);
            alignPardef = opt.pardefElementsAligned = true;
          } else if (arg.startsWith("-standard")) {
            opt.copyAlignSettings(DicOpts.STD_1_LINE);
            alignPardef = opt.pardefElementsAligned = false;
          } else if (arg.startsWith("-usetabs")) {
            opt.useTabs = true;
          } else {
            unprocessed.add(args[i]);
          }

          // see if two numbers of alignment follows...
          if (align || alignPardef) try {
            int align1 = Integer.parseInt(args[i+1]);
            int align2 = Integer.parseInt(args[i+2]);
            // OK,  two numbers follows. Interpret as aligment options
            DicOpts o = opt;
            if (alignPardef && o.pardefAlignOpts==null) {
                o.pardefAlignOpts = o.copy();
                o = o.pardefAlignOpts;
            }

            // OK, see if a 3rd number comes. In thas case the E align was also specified....
            try {
                int align3 = Integer.parseInt(args[i+3]);
                // Yes,  a number. Then both E, R and R alignment was specified.
                o.alignE = align1;
                o.alignP = align2;
                o.alignR = align3;
                i += 3;
            } catch (Exception e) {
                // No,  not a number. Then only R and R alignment was specified.
                o.alignP = align1;
                o.alignR = align2;
                i += 2;
            }

          } catch (Exception e) {
          }            
        }
        args = unprocessed.toArray(new String[unprocessed.size()]);
        return args;
    }
    
    
    private void processArguments(String[] args) throws IOException {
        opt.originalArguments = args;
        args = processGenericArguments(args);
        this.arguments = args;
        if (args.length == 0) {
            this.show_help();
            System.exit(-1);
        }
        String action = args[0];

        if (action.equals("help")) {
            this.show_help();
            System.exit(-1);
        }
        else if (action.equals("consistent")) {
            this.process_consistent();
        }
        else if (action.equals("autorestrict")) {
          new AutorestrictBidix().executeTool(opt, arguments);
        }
        else if (action.equals("autoconcord")) {
          new AutoconcordBidix().executeTool(opt, arguments);
        }
        else if (action.equals("merge")) {
            this.process_merge();
        }
        else if (action.equals("merge-morph")) {
            this.process_mergemorph();
        }
        else if (action.equals("merge-bil")) {
            this.process_mergebil();
        }
        else if (action.equals("cross")) {
            this.process_cross();
        }
        else if (action.equals("cross-param")) {
            this.process_cross_param();
        }
        else if (action.equals("reverse-bil")) {
            this.process_reverse();
        }
        else if (action.equals("fix")) {
            this.process_fix();
        }
        else if (action.equals("columnar")) {
            this.process_columnar();
        }
        else if (action.equals("stemcheck")) {
            this.process_stem_check();
        }
        else if (action.toLowerCase().startsWith("profilecreate")) {
            if (arguments.length<3) {
              msg.err(usage + "profilecreate language_dir direction [.dix files]\nF.eks: profilecreate apertium-eo-en eo-en\nprofilecreate apertium-eo-en en-eo apertium-eo-en.eo.dix.xml");
              System.exit(-1);
            }
            DicProfiler p = new DicProfiler();
            //p.createProfilerdirectory("../apertium-eo-en/", "en-eo", null);
            ArrayList<String> dixfiles = null;
            if (arguments.length>3) {
              dixfiles = new ArrayList<String>();
              for (int i = 3; i<arguments.length; i++) {
                dixfiles.add(arguments[i]);
              }
            }
            p.createProfilerdirectory(arguments[1], arguments[2], dixfiles);

            // Creating profile data for a single .dix currently not supported, as direction and insert_before must be deduced
            //Dictionary dic = new DictionaryReader(arguments[1]).readDic();
            //p.generateProfileData(dic);
            //dic.printXMLToFile(arguments[2],opt);
        }
        else if (action.equals("profilecollect")) {
            DicProfiler p = new DicProfiler();
            if (arguments.length>1) p.collectProfileData(arguments[1]);
            else p.collectProfileData("dixtools-profiledata.txt");
        }
        else if (action.equals("profileresult")) {
            DicProfiler p = new DicProfiler();
            p.createResultOfProfilingDix(
                arguments.length>1?arguments[1]:"dixtools-profilekeys.txt",
                arguments.length>2?arguments[2]:"dixtools-profiledata.txt",
                arguments.length>3?arguments[3]:"dixtools-profileresult.txt");
        }
        else if (action.equals("sort")) {
            this.process_sort();
        }
        else if (action.equals("get-bil-omegawiki")) {
            this.process_getbilomegawiki();
        }
        else if (action.equals("list") || action.equals("dic-reader")) {
            this.process_list();
        }
        else if (action.equals("expand-entry")) {
            this.process_expandentry();
        }
        else if (action.equals("equiv-paradigms")) {
            this.process_equivparadigms();
        }
        else if (action.equals("dix2trie")) {
            this.process_dix2trie();
        }
        else if (action.equals("apertiumize")) {
            this.process_apertiumize();
        }
        else if (action.equals("convert-mf")) {
            this.process_convertmf();
        }
        else if (action.equals("add-same-gender")) {
            this.process_addsamegender();
        }
        else if (action.equals("prepare-dic")) {
            this.process_preparedic();
        }
        else if (action.equals("complete-translation")) {
            this.process_completetranslation();
        }
        else if (action.equals("misc")) {
            this.process_misc();
        }
        else if (action.equals("filter")) {
            this.process_filter();
        }
        else if (action.equals("format-1line")) {
            this.process_format1line();
            //opt.copyAlignSettings(DicOpts.STD_1_LINE);
            //Dictionary dic = new DictionaryReader(args[1]).readDic();
            //dic.setXmlEncoding("UTF-8");
            //dic.printXMLToFile(args[2], opt);
        }
        else if (action.equals("cat") || action.equals("format")) {
            Dictionary dic = new DictionaryReader(args[1]).readDic();
            dic.printXMLToFile(args[2], opt);
        }
        else if (action.equals("dix2mdix")) {
            this.process_dix2mdix();
        }
        else if (action.equals("dix2cc")) {
            this.process_dix2cc();
        }
        else if (action.equals("dix2tiny")) {
            this.process_dix2tiny();
        }
        else if (action.equals("speling")) {
            this.process_speling();
        }
        else if (action.equals("speling-pruned")) {
            this.process_speling_pruned();
        }
        else if (action.equals("guessparadigm")) {
            this.process_guessparadigm();
        }
        else if (action.equals("shortenrestrictions")) {
            this.process_shortenrestrictions();
        }
        else if (action.equals("addrestrictionstosl")) {
            this.process_addrestrictionstosl();
        }
        else if (action.equals("enhance")) {
            this.process_enhanceDict();
        }
        else {
            this.show_help();
            System.exit(-1);
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
        msg.err("   sort:               sorts (and groups by category) a dictionary");
        msg.err("   enhance:            allows to interactively add new words to the dictionary");
        msg.err("");
        msg.err("More information: http://wiki.apertium.org/wiki/Apertium-dixtools");
        msg.err("");
    }

    
    private void process_consistent() {
        if (arguments.length < 8) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar consistent -bilAB [-r] <bilAB> -bilBC [-r] <bilBC> -monA <mon-A> -monC <monC>");
            System.exit(-1);
        } else {
            DicConsistent tool = new DicConsistent();
            tool.opt = opt;
            tool.arguments = arguments;
            tool.doConsistent();
        }
    }

    
    private void process_merge() {
        if (arguments.length < 8) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar merge -bilAB [-r] <bilAB> -bilAB2 [-r] <bilAB2> -monA <mon-A> - monA2 <monA2> -monB <monB> -monB2 <monB2>");
            System.exit(-1);
        } else {
            DicMerge tool = new DicMerge();
            tool.opt = opt;
            tool.arguments = arguments;
            tool.doMerge();
        }

    }

    
    private void process_mergemorph() {

      String args[] =arguments;
      //    System.err.println("arg2=" + Arrays.toString(arg));
      if (args.length==4) {

          String[] arg2x= new String[] {args[0], "-monA1", args[1],  "-monA2", args[2],  "-out", args[3]};
          System.err.println("xxx arg2=" + Arrays.toString(arg2x));
          args = arg2x;
        }

      if (args.length < 6) {
            //msg.err("Usage: java -jar path/to/apertium-dixtools.jar merge-morph -monA1 monA1.dix -monA2 monA2.dix -out merged.dix");
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar merge-morph  monA1.dix  monA2.dix merged.dix");
            System.exit(-1);
        } else {
            DicMerge tool = new DicMerge();
            tool.opt = opt;
            tool.arguments = args;
            tool.doMergeMorph();
        }

    }

    
    private void process_mergebil() {
        if (arguments.length < 6) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar merge-bil -bilAB1 bilAb1.dix -bilAB2 bilAB2.dix -out merged.dix");
            System.exit(-1);
        } else {
            DicMerge tool = new DicMerge();
            tool.opt = opt;
            tool.arguments = arguments;
            tool.doMergeBil();
        }

    }


    
    private void process_cross_param() {
      
      String arg[] =arguments;

        //System.err.println("arg=" + Arrays.toString(arg));
        //System.err.println("arg=" + arg.length);
      
        if (arg.length>=7 && arg.length<=8) {
          String crossmodel = null; 
          if (arg.length==8) crossmodel = arg[7];
          else crossmodel = System.getenv("CROSSDICS_PATH")+"/schemas/cross-model.xml";

          // java ${java_options} $task -bilAB $3 $4 -bilBC $5 $6 -monA $2 -monC $7 -cross-model $crossmodel $9

          String[] arg2 = new String[] {arg[0], "-bilAB", arg[2], arg[3], "-bilBC", arg[4], arg[5], "-monA", arg[1], "-monC", arg[6], "-cross-model",  crossmodel};
          // arg2=[cross-param, -bilAB, -r, apertium-es-ca.es-ca.dix, -bilBC, -r, apertium-en-ca.en-ca.dix, -monA, apertium-es-ca.es.dix, -monC, apertium-en-ca.en.metadix, -cross-model, ../../../../apertium-dixtools/schemas/cross-model.xml]
          // arg  =[cross-param, -bilAB, -r, apertium-es-ca.es-ca.dix, -bilBC, -r, apertium-en-ca.en-ca.dix, -monA, apertium-es-ca.es.dix, -monC, apertium-en-ca.en.metadix, -cross-model, ../../../../apertium-dixtools/schemas/cross-model.xml]          
          //System.err.println("arg2=" + Arrays.toString(arg2));
          arg = arg2;
        }
        
        if (arg.length < 8) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar cross-param -bilAB [-r] <bilAB> -bilBC [-r] <bilBC> -monA <mon-A> -monC <monC>");
            System.exit(-1);
        } 

        
        {
            DicCross tool = new DicCross();
            tool.opt = opt;
            tool.arguments = arg;
            tool.doCross();
        }

    }

    
    private void process_cross() {
        if (arguments.length < 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar cross -f <ling-resources.xml> <sl-tl>");
            System.exit(-1);
        } else {
            DicCross tool = new DicCross();
            tool.opt = opt;
            tool.arguments = arguments;
            tool.doCross();
        }
    }

    
    private void process_reverse() {
        if ((arguments.length > 3) || (arguments.length < 2)) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar reverse <bil> <bil-reversed>");
            System.exit(-1);
        } else {
            DicReverse tool = new DicReverse();
            tool.opt = opt;
            tool.arguments = arguments;
            tool.doReverse();
        }
    }

    
    private void process_fix() {
        if (arguments.length != 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar fix <dic> <dic-fixed>");
            System.exit(-1);
        } else {
            DicFix tool = new DicFix();
            tool.opt = opt;
            tool.arguments = arguments;
            tool.doFix();
        }

    }

    private void process_columnar() {
        if (arguments.length < 4|| arguments.length > 5) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar columnar [config] <monA> <monB> <bil> <input>");
            System.exit(-1);
        } else {
            Columnar tool = new Columnar();
            tool.opt = opt;
            tool.arguments = arguments;
            tool.doColumnar();
        }

    }

        private void process_stem_check() {
        if (arguments.length != 2) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar stemcheck <dic>");
            System.exit(-1);
        } else {
            DicStemCheck tool = new DicStemCheck();
            tool.opt = opt;
            tool.arguments = arguments;
            tool.doCheck();
        }

    }

    
    private void process_sort() {
        if (arguments.length < 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar sort  <inputdic> <outputdic>");
            System.exit(-1);
        } else {
            DicSort tool = new DicSort();
            tool.opt = opt;
            tool.arguments = arguments;
            tool.doSort();
        }
    }

    
    private void process_getbilomegawiki() {
        if (arguments.length != 4) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar get-bil-omegawiki <source-lang> <target-lang> <dic-out>");
            System.exit(-1);
        } else {
            GetBilOmegawiki tool = new GetBilOmegawiki(arguments[1], arguments[2]);
            tool.setOutFileName(arguments[3]);
            tool.printDictionary();
        }
    }

    
    private void process_format1line() {
        if (arguments.length == 3) {
            DicFormatE1Line tool = new DicFormatE1Line(arguments[1]);
            tool.printXML(arguments[2]);
        } else {
            try {
                if (arguments.length < 4) {
                    throw new IllegalArgumentException("Not enough arguments.");
                }
                Dictionary dic = new DictionaryReader(arguments[3]).readDic();
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

    
    private void process_list() {
        if (arguments.length < 3) {
            msg.err("Insufficient arguments: "+Arrays.toString(arguments));
            msg.err("");
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar list <action> <dic>");
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
            DicList tool = new DicList();
            tool.action = arguments[1];
            if (arguments[2].equals("-url")) {
                tool.setUrl(arguments[3]);
                System.err.println("URL: " + arguments[3]);
            } else {
                tool.setDic(arguments[2]);
            }
            tool.doit();
        }

    }

    private void process_expandentry() {
        if (arguments.length != 5) {
            msg.err("Wrong number of arguments: "+Arrays.toString(arguments));
            msg.err("");
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar expand-entry <stem> <paradigm> <dic>");
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

            DicElementsList tool = new DicElementsList();
            tool.action = arguments[1];
            if (arguments[2].equals("-url")) {
                tool.setUrl(arguments[3]);
                System.err.println("URL: " + arguments[3]);
                tool.stem=arguments[4];
                tool.paradigm=arguments[5];
            } else {
                tool.setDic(arguments[2]);
                tool.stem=arguments[3];
                tool.paradigm=arguments[4];
            }
            tool.doit();
        }

    }

    
    private void process_equivparadigms() {
        if (arguments.length != 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar equiv-paradigms <dic> <dic-out>");
            System.exit(-1);
        } else {
            DicFindEquivPar tool = new DicFindEquivPar(arguments[1]);
            Dictionary equiv = new Dictionary();
            //tool.outFileName = arguments[2];
            equiv = tool.findEquivalents();
            equiv.printXMLToFile(arguments[2], opt);
        }

    }

    private void process_speling() {
        if (arguments.length != 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar speling <speling> <dic-out>");
            System.exit(-1);
        } else {
            Speling speling = new Speling (arguments[1]);
            Dictionary dic = speling.read_speling();
            dic.printXMLToFile(arguments[2], opt);
        }

    }

    private void process_speling_pruned() {
        if (arguments.length != 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar speling-pruned <speling> <dic-out>");
            System.exit(-1);
        } else {
            Speling speling = new Speling (arguments[1]);
            Dictionary dic = speling.read_speling();
            DicFindEquivPar tool = new DicFindEquivPar(dic);
            Dictionary equiv = new Dictionary();
            //tool.outFileName = arguments[2];
            equiv = tool.findEquivalents();
            equiv.printXMLToFile(arguments[2], opt);
        }

    }
    
    private void process_dix2trie() {
        if (arguments.length < 3) {
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

    
    private void process_apertiumize() {
        if (arguments.length != 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar apertiumize <txt> <out>");
            System.exit(-1);
        } else {
            Apertiumizer tool = new Apertiumizer(arguments[1]);
            tool.setOutFileName(arguments[2]);
            tool.apertiumize();
        }
    }

    
    private void process_convertmf() {
        if (arguments.length != 4) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar convert-mf <morph-dic> <bil-dic> <out>");
            System.exit(-1);
        } else {
            ConvertMF tool = new ConvertMF(arguments[1], arguments[2]);
            tool.outFileName = arguments[3];
            tool.convert();
        }
    }

    
    private void process_addsamegender() {
        if (arguments.length < 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar add-same-gender <morph-dic> <bil-dic> <out>");
            System.exit(-1);
        } else {
            AddSameGender tool = new AddSameGender(arguments[1], arguments[2]);
            tool.addSameGender();
        }
    }

    
    private void process_preparedic() {
        if (arguments.length != 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar preparedic <bil-dic> <missing-entries>");
            System.exit(-1);
        } else {
            PrepareDic tool = new PrepareDic(arguments[2], arguments[1]);
            tool.prepare();
        }
    }

    
    private void process_completetranslation() {
        if (arguments.length != 4) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar complete-translation <bil> <src-dot> <trans-dot>");
            System.exit(-1);
        } else {
            CompleteTranslation tool = new CompleteTranslation(arguments[1], arguments[2], arguments[3]);
            tool.complete();
        }
    }

    
    private void process_misc() {
        if (arguments.length != 5) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar misc <dic1> <dic2> <dic3>");
            System.exit(-1);
        } else {
            Misc tool = new Misc(arguments[1], arguments[2], arguments[3], arguments[4]);
            tool.doMisc();
        }
    }

    
    private void process_filter() {
        if (arguments.length != 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar filter <dicA> <dicB>");
            System.exit(-1);
        } else {
            DicFilter tool = new DicFilter();
            tool.opt = opt;
            tool.arguments = this.arguments;
            tool.doFilter();
        }
    }

    
    private void process_dix2mdix() {
        if (arguments.length < 2) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar dix2mdix [options] <dic1> [<out>]");
            System.exit(-1);
        } else {
            Dix2MDix tool = new Dix2MDix();
            tool.arguments = this.arguments;
            tool.do_convert();
        }
    }

    
    private void process_dix2cc() {
        if (arguments.length < 2) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar dix2cc <dix> [<cc>]");
            System.exit(-1);
        } else {
            Dix2CC tool = new Dix2CC();
            tool.arguments = this.arguments;
            tool.do_convert();
        }
    }

    private void process_guessparadigm(){
        if (arguments.length < 5) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar guessparadigm dix -f file_with_word_to_check wordlist logfile");
            System.exit(-1);
        }
        else{
            //Building the suffix tree
            Dix2suffixtree d2s=new Dix2suffixtree();
            d2s.setDic(arguments[1]);
            SortedSetOfCandidates candidates=null;
            BufferedWriter pWriter=null;
            //If a file with words to insert is defined...
            if(arguments[2].equals("-f")){
                Set<String> words=new LinkedHashSet<String>();
                BufferedReader br;
                //Reading words to insert
                try {
                    br = new BufferedReader(new FileReader(arguments[3]));
                    String word;
                    while((word=br.readLine())!=null){
                        words.add(word);
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    System.exit(-1);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.exit(-1);
                }
                //Generating lexical forms
                d2s.getListOfLexicalForms();
                try {
                    pWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arguments[5]), "UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                    System.exit(-1);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    System.exit(-1);
                }
                for(String w: words){
                    System.out.println("WORD "+w);
                    candidates=d2s.CeckNewWord(w, arguments[4]);
                    //System.out.println(candidates.GetNumberOfDifferentCandidates());
                    try{
                        pWriter.append(w);
                        pWriter.append("\t");
                        pWriter.append(Integer.toString(candidates.getCandidates().size()));
                        Questions.AskQuestions(candidates,pWriter);
                        pWriter.newLine();
                        pWriter.flush();
                    } catch(IOException ex){
                        ex.printStackTrace();
                        System.exit(-1);
                    }
                }
                try {
                    pWriter.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.exit(-1);
                }
            }
            //If no file with words to insert is defined, the next argument is a word to be inserted
            else{
                try {
                    pWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arguments[4]), "UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                    System.exit(-1);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    System.exit(-1);
                }
                //d2s.getFilteredListOfLexicalForms(""+arguments[2].charAt(arguments[2].length()-1));
                d2s.getListOfLexicalForms();
                candidates=d2s.CeckNewWord(arguments[2], arguments[3]);
                try{
                    pWriter.append(arguments[2]);
                    pWriter.append("\t");
                    pWriter.append(Integer.toString(candidates.getCandidates().size()));
                    long start = System.currentTimeMillis();
                    Questions.AskQuestions(candidates,pWriter);
                    long elapsed = System.currentTimeMillis() - start;
                    pWriter.append(Long.toString(elapsed));
                    pWriter.append("\t");
                    pWriter.newLine();
                    pWriter.flush();
                    pWriter.close();
                } catch(IOException ex){
                    ex.printStackTrace();
                    System.exit(-1);
                }
                /*for(Candidate c:candidates.getCandidates()){
                    System.out.println(c.getScore()+"\t"+c.getSfs().getSteam()+"-"+c.getSfs().getParadigm().getName());
                }*/
            }
        }
    }

    private void process_dix2tiny() {
        if (arguments.length < 5) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar dix2tiny <dix> <lang-pair> <lang-pair-text> <platform> [<filter>]");
            msg.err("For example:");
            msg.err("   java -jar path/to/apertium-dixtools.jar dix2tiny apertium-es-ca.es-ca.dix es-ca Spanish-Catalan j2me [<filter-es-ca.xml>]");
            System.exit(-1);
        } else {
            Dix2Tiny tool = new Dix2Tiny();
            tool.opt = opt;
            tool.arguments = this.arguments;
            tool.do_tiny();
        }
    }

    private void process_shortenrestrictions() {
        ShortenRestrictions tool = new ShortenRestrictions();
        tool.opt = opt;
        tool.arguments = arguments;
        tool.doShorten();
    }
    
    private void process_addrestrictionstosl()
    {
        AddRestrictionsToSL tool = new AddRestrictionsToSL();
        tool.opt = opt;
        tool.arguments = arguments;
        tool.doAddRestrictions();
    }        
    
    private void process_enhanceDict()
    {
         if (arguments.length != 3) {
            msg.err("Usage: java -jar path/to/apertium-dixtools.jar enhance dict_to_enhance.dix new_name.dix");
            System.exit(-1);
        }
         
        DictEnhancer tool = new DictEnhancer();
        tool.opt = opt;
        tool.arguments = arguments;
        tool.doEnhance();
    }
}
