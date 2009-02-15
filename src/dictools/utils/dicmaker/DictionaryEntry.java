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
import java.util.List;


public class DictionaryEntry {
	private final Phrase sourcePhrase;
	private final List<Phrase> translations = new ArrayList<Phrase>();
	private final List<String> indexEntries = new ArrayList<String>();
	
	public DictionaryEntry(int language, String sourcePhrase) {
		this.sourcePhrase = new Phrase(language, sourcePhrase);
	}
	
	public void addTranslation(int language, String phrase) {
		translations.add(new Phrase(language, phrase));
	}
	
	public void addTranslation(Phrase phrase) {
		translations.add(phrase);
	}
	
	public void addIndexEntry(String indexEntry) {
		if (indexEntry.length() == 0) throw new IllegalArgumentException();
		if (!indexEntries.contains(indexEntry))
			indexEntries.add(indexEntry);
	}
	
	public List<RecordEntry> createRecordEntries() {
		List<RecordEntry> result = new ArrayList<RecordEntry>();
		RecordEntry source = new RecordEntry(sourcePhrase);
		result.add(source);
		RecordEntry[] dicentry = new RecordEntry[translations.size()];
		for (int i = 0; i < dicentry.length; i++) {
			dicentry[i] = new RecordEntry(translations.get(i));
			result.add(dicentry[i]);
		}
		source.setDictionaryEntry(dicentry);
		return result;
	}

	public String[] getIndexEntries() {
		return indexEntries.toArray(new String[indexEntries.size()]);
	}
}
