/*
 * Author: Jimmy O'Regan
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
package misc.lextor2indexed;

import dics.elements.dtd.*;
import dictools.AbstractDictTool;
import dictools.utils.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Hashtable;

import misc.eoen.malnova.LeguAliajn;

/**
*
* @author jimregan
*/

public class lextorindexer extends AbstractDictTool {
      
	private Dictionary dic;
	/**
	 * Hash of slr entries
	 * String is lemma+pos, Integer is count
	 */
	private Hashtable<E, Integer> left;
	/**
	 * Hash of srl entries
	 * String is lemma+pos, Integer is count
	 */
	private Hashtable<E, Integer> right;
	
	public lextorindexer() {
		this.left = new Hashtable<E, Integer>();
		this.right = new Hashtable<E, Integer>();
	}
	
	public lextorindexer (String file) {
		this();
		this.dic = new DictionaryReader(file).readDic();
	}

	public void index() {
		Section S = this.dic.getSection("main");
		for (Iterator<E> elem = S.elements.iterator(); elem.hasNext(); ) {
			ArrayList<S> s = new ArrayList<S>();
			E e = elem.next();
			if (!e.slr.equals(null)) {
				if (!"LR".equals(e.restriction)) {
					// Add the original, RL
					E newR = new E();
					newR = e;
					newR.restriction = "RL";
					S.elements.add(newR);
				}
				if (e.slr.endsWith(" D")) {
					// Index is 0
					E newL = new E();
					newL = e;
					newL.restriction = "LR";
					s = e.getFirstPartAsR().getSymbols();
					if ("adj".equals(s.get(0).name) && "sint".equals(s.get(1).name))
						s.add(2, new S(":0"));
					else
						s.add(1, new S(":0"));
				} else {
					if (!this.left.containsKey(e)) {
						this.left.put(e, 1);
						E newL = new E();
						newL = e;
						newL.restriction = "LR";
						s = e.getFirstPartAsR().getSymbols();
						if ("adj".equals(s.get(0).name) && "sint".equals(s.get(1).name))
							s.add(2, new S(":1"));
						else
							s.add(1, new S(":1"));
					} else {
						int index = this.left.get(e);
						index++;
						this.left.put(e, index);
						E newL = new E();
						newL = e;
						newL.restriction = "LR";
						s = e.getFirstPartAsR().getSymbols();
						if ("adj".equals(s.get(0).name) && "sint".equals(s.get(1).name))
							s.add(2, new S(":" + index));
						else
							s.add(1, new S(":" + index));						
					}
				}
			} else if (!e.srl.equals(null)) {
				if (!"RL".equals(e.restriction)) {
					// Add the original, LR
					E newR = new E();
					newR = e;
					newR.restriction = "LR";
					S.elements.add(newR);
				}

				if (e.srl.endsWith(" D")) {
					// Index is 0
					E new_d = new E();
					new_d = e;
					new_d.restriction = "RL";
					s = e.getFirstPartAsR().getSymbols();
					if ("adj".equals(s.get(0).name) && "sint".equals(s.get(1).name))
						s.add(2, new S(":0"));
					else
						s.add(1, new S(":0"));
					if (!this.right.containsKey(e)) {
						this.right.put(e, 1);
						E new_srl_1 = new E();
						new_srl_1 = e;
						new_srl_1.restriction = "RL"; 
						s = e.getFirstPartAsR().getSymbols();
						if ("adj".equals(s.get(0).name) && "sint".equals(s.get(1).name))
							s.add(2, new S(":1"));
						else
							s.add(1, new S(":1"));
					} else {
						int index = this.right.get(e);
						index++;
						this.right.put(e, index);
						E new_srl_r = new E();
						new_srl_r = e;
						new_srl_r.restriction = "RL";
						s = e.getFirstPartAsR().getSymbols();
						if ("adj".equals(s.get(0).name) && "sint".equals(s.get(1).name))
							s.add(2, new S(":" + index));
						else
							s.add(1, new S(":" + index));						
					}
				}
			}	
		}
	}
	

}
