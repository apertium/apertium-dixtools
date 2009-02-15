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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordTupleDictionary implements Dictionary {

	private String description = "";
	private String[] languages;
	private IndexBuilder[] indices;
	private List<String[]> tuples = new ArrayList<String[]>();

	public WordTupleDictionary() {
	}

	public WordTupleDictionary(String[] languages, IndexBuilder[] indices) {
		if (indices.length > languages.length)
			throw new IllegalArgumentException();
		if (languages.length>15)
			throw new IllegalArgumentException();
		this.languages = languages;
		this.indices = indices;
	}

	public void setLanguages(String... languages) {
		if (tuples.size() > 0)
			throw new IllegalStateException();
		this.languages = languages;
	}

	public void setIndices(IndexBuilder... indices) {
		if (indices.length > languages.length)
			throw new IllegalArgumentException();
		this.indices = indices;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void addTuple(String... tuple) {
		if (tuple.length != languages.length)
			throw new IllegalArgumentException();
		tuples.add(tuple);
	}

	public String getDescription() {
		return description;
	}

	public List<DictionaryEntry> getDictionaryEntries() {
		List<DictionaryEntry> result = new ArrayList<DictionaryEntry>();
		for (int i = 0; i < indices.length; i++) {
			Map<String, List<Phrase>> translations = new HashMap<String, List<Phrase>>();
			for (String[] tuple : tuples) {
				String from = tuple[i];
				if (from == null)
					continue;
				if (!translations.containsKey(from)) {
					translations.put(from, new ArrayList<Phrase>());
				}
				List<Phrase> to = translations.get(from);
				for (int j = 0; j < tuple.length; j++) {
					if (j != i && tuple[j] != null) {
						Phrase toPhrase = new Phrase(j + 1, tuple[j]);
						if (!to.contains(toPhrase))
							to.add(toPhrase);
					}
				}
			}
			for (Map.Entry<String, List<Phrase>> translation : translations
					.entrySet()) {
				Collections.sort(translation.getValue(),
						new Comparator<Phrase>() {
							public int compare(Phrase o1, Phrase o2) {
								return o1.getPhrase().compareTo(o2.getPhrase());
							}
						});
				DictionaryEntry e = new DictionaryEntry(i + 1, translation
						.getKey());
				for (Phrase p : translation.getValue()) {
					e.addTranslation(p);
				}
				indices[i].addIndexEntries(e, translation.getKey());
				result.add(e);
			}
		}
		return result;
	}

	public int getIndexCount() {
		return indices.length;
	}

	public String[] getLanguageNames() {
		return languages;
	}
}
