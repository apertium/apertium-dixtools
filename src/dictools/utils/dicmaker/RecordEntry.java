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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecordEntry {
	
	private final Phrase phrase;
	private RecordEntry indirectTarget=null;
	private short record=0;
	private short offset=0;
	private RecordEntry[] dicentry = null;
	private List<RecordEntry> indexResults = null;
	
	public RecordEntry(Phrase phrase) {
		this.phrase=phrase;
	}
	
	public int getLanguage() {
		return phrase.getLanguage();
	}
	
	public Phrase getPhrase() {
		return phrase;
	}
	
	public short getRecord() {
		if (indirectTarget != null) return indirectTarget.getRecord();
		if (record == 0) throw new IllegalStateException();
		return record;
	}
	
	public short getOffset() {
		if (indirectTarget != null) return indirectTarget.getOffset();
		if (record == 0) throw new IllegalStateException();
		return offset;
	}
	
	public boolean hasLinks() {
		if (indirectTarget != null) throw new IllegalStateException();
		return dicentry != null || indexResults != null;
	}
	
	public void makePointingTo(RecordEntry target) {
		if (indirectTarget != null) throw new IllegalStateException();
		if (dicentry != null || indexResults != null) throw new IllegalStateException();
		if (record != 0 || offset != 0) throw new IllegalStateException();
		if (!target.phrase.equals(phrase)) throw new IllegalArgumentException();
		indirectTarget = target;
	}
	
	public int getLength() {
		if (indirectTarget != null) throw new IllegalStateException();
		int result = phrase.getPhrase().length()+2;
		if (dicentry != null) {
			result += dicentry.length * 4;
		}
		if (indexResults != null) {
			result += indexResults.size() * 4;
		}
		if (result > 256) throw new RuntimeException(phrase.getPhrase()+"/"+(indexResults.size()) +"/"+(dicentry.length));
		return result;
	}

	public RecordEntry prepareIndexEntry(List<RecordEntry> indexEntries, RecordEntry moreEntry) {
		if (indirectTarget != null) throw new IllegalStateException();
		Collections.sort(indexResults, new Comparator<RecordEntry>() {
			public int compare(RecordEntry o1, RecordEntry o2) {
				String phrase1 = o1.getPhrase().getPhrase();
				String phrase2 = o2.getPhrase().getPhrase();
				int result = phrase1.length()-phrase2.length();
				if (result == 0)
					result = phrase1.compareTo(phrase2);
				return result;
			}
		});
		int len = phrase.getPhrase().length()+2;
		if (dicentry != null) {
			len += dicentry.length * 4;
		}
		int idxlen = indexResults.size() * 4;
		if (len+idxlen > 256) {
			if (dicentry != null) {
				// split record in dictionary entry and main entry
				RecordEntry replacement = new RecordEntry(phrase);
				for(RecordEntry idx : indexResults)
					replacement.addIndexEntry(idx);
				indexResults = null;
				return replacement;
			}
			if (moreEntry == null) return null;
			int maxIdxLen = (256 - len)/4 - 1;
			if (maxIdxLen < 1) throw new RuntimeException();
			String p = phrase.getPhrase();
			if (p.startsWith("~-"))
				throw new RuntimeException(p); 
			p = "~-"+p+"-";
			RecordEntry[] origIndexResults = indexResults.toArray(new RecordEntry[indexResults.size()]);
			indexResults.clear();
			for (int i = 0; i < maxIdxLen; i++) {
				indexResults.add(origIndexResults[i]);
			}
			addIndexEntry(moreEntry);
			int fileno=1;
			for (int next = maxIdxLen;next < origIndexResults.length; next += maxIdxLen) {
				String pp = p+fileno;
				maxIdxLen = (254 - pp.length())/4;
				if (maxIdxLen < 2) throw new RuntimeException();
				RecordEntry continued = new RecordEntry(new Phrase(phrase.getLanguage(), p+fileno));
				fileno++;
				for (int i = next; i < next+maxIdxLen && i < origIndexResults.length; i++) {
					continued.addIndexEntry(origIndexResults[i]);
				}
				indexEntries.add(continued);
			}
		}
		return this;
	}

	public void place(short[] offsets) {
		if (indirectTarget != null) throw new IllegalStateException();
		if (record != 0) throw new IllegalStateException();
		int l = getLength();
		if (offsets[1]+l > DictionaryWriter.RECORD_LENGTH) {
			offsets[1] = 0;
			offsets[0]++;
		}
		record = offsets[0];
		offset = offsets[1];
		offsets[1] += l;
	}

	public void setDictionaryEntry(RecordEntry[] dicentry) {
		if (indirectTarget != null) throw new IllegalStateException();		
		this.dicentry = dicentry;		
	}
	
	public void addIndexEntry(RecordEntry target) {
		if (indirectTarget != null) throw new IllegalStateException();
		if (target.getLanguage() != getLanguage()) throw new IllegalArgumentException();
		if (indexResults == null) indexResults = new ArrayList<RecordEntry>();
		indexResults.add(target);
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeByte(getLength()-1);
		out.write(phrase.getPhrase().getBytes("ISO-8859-1"));
		out.writeByte(0);
		if(dicentry != null) {
			for (RecordEntry dicrecord: dicentry) {
				out.writeShort(dicrecord.getRecord());
				out.writeShort((short)(dicrecord.getLanguage() <<12) + dicrecord.getOffset() + 1);
			}
		}
		if (indexResults != null) {
			for(RecordEntry idx : indexResults) {
				out.writeShort(idx.getRecord());
				out.writeShort(idx.getOffset());
			}
		}
	}
}
