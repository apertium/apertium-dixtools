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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Index {

	private static final char[] MAPPING = {
		' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 
		' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 
		' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 
		' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 
		' ', ' ', ' ', ' ', ' ', ' ', ' ', '\'',
		' ', ' ', ' ', ' ', ' ', '-', ' ', ' ', 
		'0', '1', '2', '3', '4', '5', '6', '7', 
		'8', '9', ' ', ' ', ' ', ' ', ' ', ' ', 
		' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 
		'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 
		'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 
		'x', 'y', 'z', ' ', ' ', ' ', ' ', ' ', 
		' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 
		'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 
		'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 
		'x', 'y', 'z', ' ', ' ', ' ', '~', ' ', 
		' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 
		' ', ' ', 's', ' ', 'o', ' ', ' ', ' ', 
		' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 
		' ', ' ', 's', ' ', 'o', ' ', ' ', 'y', 
		' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 
		' ', ' ', 'a', ' ', ' ', ' ', ' ', ' ', 
		' ', ' ', '2', '3', ' ', 'm', ' ', ' ', 
		' ', '1', 'o', ' ', ' ', ' ', ' ', ' ', 
		'a', 'a', 'a', 'a', 'a', 'a', 'a', 'c', 
		'e', 'e', 'e', 'e', 'i', 'i', 'i', 'i', 
		'd', 'n', 'o', 'o', 'o', 'o', 'o', ' ', 
		'o', 'u', 'u', 'u', 'u', 'y', 't', 's', 
		'a', 'a', 'a', 'a', 'a', 'a', 'a', 'c', 
		'e', 'e', 'e', 'e', 'i', 'i', 'i', 'i', 
		'd', 'n', 'o', 'o', 'o', 'o', 'o', ' ', 
		'o', 'u', 'u', 'u', 'u', 'u', 't', 'y'
	};

	private final int language;
	List<IndexEntry> rootEntries = null;
	List<List<IndexEntry>> treeEntries = null;
	RecordEntry[] indexEntries = null;
	Map<String,RecordEntry> entryLookup = new HashMap<String,RecordEntry>();

	public Index(int language) {
		this.language=language;
	}

	public void addMapping(String word, RecordEntry target, Map<Phrase,RecordEntry> pool) {
		if (target.getLanguage() != language) throw new IllegalArgumentException();
		if (!entryLookup.containsKey(word)) {
			Phrase wordPhrase = new Phrase(language, word);
			RecordEntry re = pool.remove(wordPhrase);
			if (re == null) {
				re = new RecordEntry(wordPhrase);
			}
			entryLookup.put(word, re);
		}
		entryLookup.get(word).addIndexEntry(target);
	}

	public void placeIndexEntries(short[] offsets, List<RecordEntry> orphanedEntries) {
		RecordEntry moreEntry = null;
		if (indexEntries != null) throw new IllegalStateException();
		List<RecordEntry> allEntries = new ArrayList<RecordEntry>(entryLookup.values());
		for (int i = 0; i < allEntries.size(); i++) {
			RecordEntry entry =  allEntries.get(i);
			RecordEntry replacement = entry.prepareIndexEntry(allEntries, moreEntry);
			if (replacement == null) {
				moreEntry = new RecordEntry(new Phrase(language, "(+more, see ~ in index)"));
				orphanedEntries.add(moreEntry);
				i--;
			} else if (replacement != entry) {
				orphanedEntries.add(entry);
				allEntries.set(i, replacement);
				i--;
			}
		}
		indexEntries = new RecordEntry[allEntries.size()];
		indexEntries = allEntries.toArray(indexEntries);
		Arrays.sort(indexEntries, new Comparator<RecordEntry>() {
			public int compare(RecordEntry o1, RecordEntry o2) {
				String s1 = transformCase(o1.getPhrase().getPhrase());
				String s2 = transformCase(o2.getPhrase().getPhrase());
				return s1.compareTo(s2);
			}
		});
		for (int i = 0; i < indexEntries.length; i++) {
			indexEntries[i].place(offsets);
		}
	}

	public static String transformCase(String phrase) {
		char[] chars = new char[phrase.length()];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = MAPPING[phrase.charAt(i)];
		}
		return new String(chars);
	}

	public RecordEntry[] getEntries() {
		return indexEntries;
	}

	public void generateTreeRecords(short[] offsets) {
		if (treeEntries!=null) throw new IllegalStateException();
		if (indexEntries == null) throw new IllegalStateException();
		treeEntries = new ArrayList<List<IndexEntry>>();
		List<IndexEntry> workEntries = new ArrayList<IndexEntry>();
		short rec = indexEntries[0].getRecord();
		for (int i = 0; i < indexEntries.length; i++) {
			if (indexEntries[i].getRecord() != rec) {
				workEntries.add(new IndexEntry(indexEntries[i-1].getPhrase().getPhrase(), rec));
				rec = indexEntries[i].getRecord();
			}
		}
		workEntries.add(new IndexEntry(indexEntries[indexEntries.length-1].getPhrase().getPhrase(), rec));
		while(true) {
			int length = 0;
			for (IndexEntry e : workEntries) {
				length +=e.getLength();
				if (length > DictionaryWriter.RECORD_LENGTH)
					break;
			}
			if (length <= DictionaryWriter.RECORD_LENGTH)
				break;
			List<IndexEntry> newEntries = new ArrayList<IndexEntry>();
			length=0;
			List<IndexEntry> thisRecordEntries = new ArrayList<IndexEntry>();
			treeEntries.add(thisRecordEntries);
			for (int i = 0; i < workEntries.size(); i++) {
				if (length + workEntries.get(i).getLength() > DictionaryWriter.RECORD_LENGTH) {
					newEntries.add(new IndexEntry(workEntries.get(i-1).getWord(), ++offsets[0]));
					thisRecordEntries = new ArrayList<IndexEntry>();
					treeEntries.add(thisRecordEntries);				
					length = 0;
				}
				thisRecordEntries.add(workEntries.get(i));
				length += workEntries.get(i).getLength();
			}
			newEntries.add(new IndexEntry(workEntries.get(workEntries.size()-1).getWord(), ++offsets[0]));
			workEntries = newEntries;
		}
		rootEntries = workEntries;
	}

	public void writeRootRecord(DataOutputStream out) throws IOException {
		writeRecord(rootEntries, out);
	}

	public boolean hasNextRecord() {
		return !treeEntries.isEmpty();
	}
	
	public void writeNextRecord(DataOutputStream out) throws IOException {
		writeRecord(treeEntries.remove(0), out);
	}
	
	private void writeRecord(List<IndexEntry> entryList, DataOutputStream out) throws IOException {
		for (IndexEntry indexEntry : entryList) {
			indexEntry.write(out);
		}
	}
}
