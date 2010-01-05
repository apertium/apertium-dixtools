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
import dictools.utils.*;
import java.io.*;
import java.util.Iterator;
import java.util.Hashtable;

/**
*
* @author jimregan
*/

public class lextorindexer {
      
	private Dictionary dic;
	/**
	 * Hash of slr entries
	 * String is lemma+pos, Integer is count
	 */
	private Hashtable<String, Integer> left;
	/**
	 * Hash of srl entries
	 * String is lemma+pos, Integer is count
	 */
	private Hashtable<String, Integer> right;
	
	public lextorindexer() {
		this.left = new Hashtable<String, Integer>();
		this.right = new Hashtable<String, Integer>();
	}
	
	public lextorindexer (String file) {
		this();
		this.dic = new DictionaryReader(file).readDic();
	}

	public void index() {
		Section S = this.dic.getSection("main");
		for (Iterator<E> elem = S.elements.iterator(); elem.hasNext(); ) {
			E e = elem.next();
			if (!e.slr.equals(null) && e.srl.equals(null)) {
				// Do stuff around this point
				if (e.slr.endsWith(" D")) {
					// Index is 0
				} else {
					// Index is non-0
					// Add 
				}
			} else if (!e.srl.equals(null) && e.slr.equals(null)) {
				// Do stuff around this point
				if (e.srl.endsWith(" D")) {
					// Index is 0
				} else {
					// Index is non-0
				}
			}	
		}
	}
	
}
