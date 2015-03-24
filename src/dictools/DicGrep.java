/*
 * Author: Jacob Nordfalk
 *
 * This program is free software; you can redistribute it and/or
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


import dics.elements.dtd.*;
import dics.elements.dtd.Par;
import dics.elements.dtd.S;
import dictools.frequency.HitParade;
import dictools.utils.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Tool to do grep-like searches in a dix
 * @author Jacob Nordfalk
 */
public class DicGrep extends AbstractDictTool {


  public static void main(final String[] args) throws Exception {
    //new AutoconcordBidix().prepare(null, null, null, null, "/home/j/esperanto/a/apertium-sv-da/apertium-sv-da.sv-da.dix", null);
    new DicGrep().executeTool(DicOpts.STD, new String[] {
			"grep", 
//				"--lm", "óxido",
				"--par", "abismo__n",
				"../apertium-eo-es/apertium-eo-es.es.dix" });

  }
	private Pattern par;
	private Pattern lemma;
	private Pattern section;

  @Override
  public String toolHelp() {
    return
        "grep [--section section-id] [--lemma lemma-name] [--par paradigm-name] [input.dix] [output.dix]"
        +"\nFilter entries within a specific section, with a specific lemma or using a specific paradigm"
        +"\nOnly dictionary entries are filtered - all paradigms are left untouched"
        +"\nIf input.dix or output.dix is not specified then standard input and standard output is assumed"
        +"\nExamples:"
        +"\n\tgrep --par abismo__n apertium-eo-es.es.dix  will give a dix with all entries using abismo__n paradigm"
        +"\nRegular expressions are allowed:"
        +"\n\tgrep --lm 'ali.*' --par '.*__vblex'   make a dix with all verbs starting with 'ali'"
        ;
  }

  @Override
  public void executeTool() throws IOException {
//		System.err.println("arguments.length="+arguments.length);
		if (arguments.length==1) {
			failWrongNumberOfArguments(arguments);
			return;
		}
		for (int i=1;i<arguments.length; i++) {
			if (arguments[i].equals("--section")) {
				section = Pattern.compile(arguments[i+1]);
				removeArgs(i, 2);
				i--;
			}
			if (arguments[i].equals("--lm")) {
				lemma = Pattern.compile(arguments[i+1]);
				removeArgs(i, 2);
				i--;
			}
			if (arguments[i].equals("--par")) {
				par = Pattern.compile(arguments[i+1]);
				removeArgs(i, 2);
				i--;
			}
		}
		Dictionary dic = new DictionaryReader(arguments.length>1?arguments[1]:"-").readDic();

		for (Iterator<Section> si = dic.sections.iterator(); si.hasNext();) {
			Section s = si.next();
			if (removeIfNoMatch(section, s.id, si)) continue;

			for (Iterator<E> ee = s.elements.iterator(); ee.hasNext();) {
				E e = ee.next();
				if (removeIfNoMatch(lemma, e.lemma, ee)) continue;

				if (par!=null) {
					boolean remove = true;
					for (DixElement de : e.children) {
						if (de instanceof Par && par.matcher(((Par)de).name).matches()) {
							remove=false;
						}
					}
					if (remove) {
						ee.remove();
						continue;
					}
				}
			}
		}

		dic.printXMLToFile(arguments.length>2?arguments[2]:"-", opt);
  }

	private boolean removeIfNoMatch(Pattern pattern, String name, Iterator iterator) {
		if (pattern==null) return false;
		if (name==null) name = ""; // fix for grepping on a lemma on entries that might have a null lemma
		if (pattern.matcher(name).matches()) return false;
		iterator.remove();
		return true;
	}
}

