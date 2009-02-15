/*
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your soption) any later version.
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

package dictools.utils.dicmaker;



public class WordIndexBuilder implements IndexBuilder {

	public static final WordIndexBuilder INSTANCE = new WordIndexBuilder();

	public void addIndexEntries(DictionaryEntry target, String phrase) {
		int start=0;
		String mappedPhrase = Index.transformCase(phrase);
		for (int i = 0; i < phrase.length(); i++) {
			if (mappedPhrase.charAt(i) == ' ' || phrase.charAt(i) == '~') {
				if (start != i)
					target.addIndexEntry(phrase.substring(start, i));
				// skip annotations like {pl} or [med.]
				if ((phrase.charAt(i) == '{' || phrase.charAt(i) == '[')) {
					while(phrase.charAt(i) != '}' && phrase.charAt(i) != ']') {
						i++;
						if (i == phrase.length()) {
							System.out.println("\tWarning: Unclosed parentheses in phrase: "+phrase);
							i--;
							break;
						}
					}
				}
				start = i+1;
			}
		}
		if (start != phrase.length()) {
			target.addIndexEntry(phrase.substring(start));
		}
	}
}
