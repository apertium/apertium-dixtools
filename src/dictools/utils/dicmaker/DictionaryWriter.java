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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

import dictools.utils.jpalmdb.PDBFile;


public class DictionaryWriter {

	// we need one terminating zero byte at the end
	public static final short RECORD_LENGTH = 4095;
	
	private final String name;
	private final Dictionary dic;
	private Index[] indices = null;
	private List<RecordEntry> orphanedEntries = null;

	private static final byte[] CREATOR= new byte[] {
		'd', 'a', 't', 'a', 'O', 'D', 'i', 'c'
	};

	public DictionaryWriter(String name, Dictionary dic) {
		this.name = name;
		this.dic = dic;
	}

	private void parseDictionary() {
		if (indices != null) throw new IllegalStateException();
		indices = new Index[dic.getIndexCount()];
		for (int i = 0; i < indices.length; i++) {
			indices[i] = new Index(i+1);
		}
		System.out.println("Eliminating duplicate word references...");
		HashMap<Phrase,RecordEntry> pool = new HashMap<Phrase, RecordEntry>();
		orphanedEntries = new ArrayList<RecordEntry>();
		Map<RecordEntry,String[]> indexEntries = new HashMap<RecordEntry,String[]>();
		for (DictionaryEntry de : dic.getDictionaryEntries()) {
			List<RecordEntry> res = de.createRecordEntries();
			indexEntries.put(res.get(0), de.getIndexEntries());
			for(RecordEntry re : res) {
				if (pool.containsKey(re.getPhrase())) {
					RecordEntry oldOne = pool.get(re.getPhrase());
					if (!re.hasLinks()) {
						re.makePointingTo(oldOne);
					}  else if (!oldOne.hasLinks()) {
						oldOne.makePointingTo(re);
						// overwrite oldOne
						pool.put(re.getPhrase(), re);
					} else {
						orphanedEntries.add(re);
					}
				} else {
					pool.put(re.getPhrase(), re);
				}
			}
		}
		System.out.println("Building indices...");
		for (Map.Entry<RecordEntry, String[]> entry : indexEntries.entrySet()) {
			int language = entry.getKey().getLanguage();
			for (String word : entry.getValue()) {				
				indices[language-1].addMapping(word, entry.getKey(), pool);
			}
		}
		short[] offsets = {(short)indices.length, RECORD_LENGTH};
		for (int i=0; i<indices.length; i++) {
			System.out.println("Sorting "+dic.getLanguageNames()[i]+" index...");
			indices[i].placeIndexEntries(offsets, orphanedEntries);
			offsets[1]=RECORD_LENGTH; // force a new record
		}
		System.out.println("Sorting extra entries...");
		orphanedEntries.addAll(pool.values());
		Collections.sort(orphanedEntries, new Comparator<RecordEntry>() {
			public int compare(RecordEntry o1, RecordEntry o2) {
				return o1.getPhrase().getPhrase().compareTo(o2.getPhrase().getPhrase());
			}
		});
		for(RecordEntry orphan : orphanedEntries) {
			orphan.place(offsets);
		}
		for (Index idx : indices) {
			idx.generateTreeRecords(offsets);
		}
	}

	public PDBFile buildDictionary() throws IOException {
		parseDictionary();
		System.out.println("Generating metadata...");
		Date timestamp = new Date();
		PDBFile result = new PDBFile("ODic"+name, CREATOR, (short)1, timestamp, timestamp, timestamp);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DeflaterOutputStream dos = new DeflaterOutputStream(baos);
		// Record 0 : metadata
		String[] langs = dic.getLanguageNames();
		dos.write(langs.length);
		dos.write(dic.getIndexCount());
		for (int i = 0; i < langs.length; i++) {
			byte[] l = langs[i].getBytes("ISO-8859-1");
			dos.write(l);
			dos.write(0);
		}
		dos.write(dic.getDescription().getBytes("ISO-8859-1"));
		dos.write(0);
		dos.close(); 
		result.addRecord(baos.toByteArray());
		baos.reset();
		dos = new DeflaterOutputStream(baos);
		DataOutputStream dtos = new DataOutputStream(dos);
		
		// index root records
		for (int i = 0; i < indices.length; i++) {
			indices[i].writeRootRecord(dtos);
			dtos.writeByte(0);
			dtos.close();
			result.addRecord(baos.toByteArray());
			baos.reset();
			dos = new DeflaterOutputStream(baos);
			dtos = new DataOutputStream(dos);
		}
		short recordNumber = (short)(indices.length+1);

		// data records
		for (int i = 0; i <= indices.length; i++) {
			RecordEntry[] entries;
			if (i<indices.length) {
				System.out.println("Generating data records for language "+dic.getLanguageNames()[i]+"...");
				entries = indices[i].getEntries();
			} else {
				System.out.println("Generating data records for phrases...");
				entries = orphanedEntries.toArray(new RecordEntry[orphanedEntries.size()]);
			}
			for(int j=0; j<entries.length; j++) {
				RecordEntry entry = entries[j];
				if (recordNumber < entry.getRecord()) {
					dtos.writeByte(0);
					dtos.close();
					result.addRecord(baos.toByteArray());
					baos.reset();
					dos = new DeflaterOutputStream(baos);
					dtos = new DataOutputStream(dos);
					recordNumber++;
				}
				if (recordNumber != entry.getRecord()) throw new RuntimeException();
				entry.write(dtos);
			}
		}
		// index tree pointers
		System.out.println("Generating index records...");
		for (Index idx : indices) {
			while(idx.hasNextRecord()) {
				dtos.writeByte(0);
				dtos.close();
				result.addRecord(baos.toByteArray());
				baos.reset();
				dos = new DeflaterOutputStream(baos);
				dtos = new DataOutputStream(dos);
				idx.writeNextRecord(dtos);
			}
		}
		dtos.writeByte(0);
		dtos.close();
		result.addRecord(baos.toByteArray());
		return result;
	}

	public void writeToFile(File f) throws IOException {
		PDBFile pf = buildDictionary();
		FileOutputStream fos = new FileOutputStream(f);
		pf.generate(fos);
		fos.close();
		System.out.println("Finished.");
	}
}
