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

package dictools.utils.jpalmdb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PDBFile {
	
	private List<PDBRecord> records = new ArrayList<PDBRecord>();
	private final byte[] typeandcreator;
	private final Date created;
	private final Date modified;
	private final Date backedUp;
	private final String pdbName;
	private final short version;
	
	public short getVersion() {
		return version;
	}
	
	public String getPDBName() {
		return pdbName;
	}
	
	public String getTypeAndCreator() throws IOException {
		return new String(typeandcreator, "ISO-8859-1");
	}
	
	public PDBFile(String pdbName, byte[] typeandcreator, short version) {
		this(pdbName, typeandcreator, version, null, null, null);
	}
	
	public PDBFile(byte[] data) throws IOException {
		this(new ByteArrayInputStream(data), data.length);
	}
	
	public PDBFile(File f) throws IOException {
		this(new FileInputStream(f), (int)f.length());
	}
	
	public PDBFile(InputStream in, int length) throws IOException {
		DataInputStream dis = new DataInputStream(in);
		
		byte[] tmp = new byte[32];
		dis.readFully(tmp);
		int tmpint = 32;
		short tmpshort;
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i] == 0) {
				tmpint = i;
				break;
			}
		}
		pdbName = new String(tmp, 0, tmpint, "ISO-8859-1");
		tmpshort = dis.readShort(); //flags
		if (tmpshort != 0x0008) throw new IOException("Unsupported DB format");
		version = dis.readShort();
		tmpint = dis.readInt();
		created = new Date(tmpint * 1000);
		tmpint = dis.readInt();
		modified = new Date(tmpint * 1000);
		tmpint = dis.readInt();
		backedUp = new Date(tmpint * 1000);
		tmpint = dis.readInt(); // modification number
		if (tmpint != 0) throw new IOException("Unsupported DB format");
		tmpint = dis.readInt(); // AppInfoData
		if (tmpint != 0) throw new IOException("Unsupported DB format");
		tmpint = dis.readInt(); // SortInfoData
		if (tmpint != 0) throw new IOException("Unsupported DB format");
		typeandcreator = new byte[8];
		dis.readFully(typeandcreator);
		tmpint = dis.readInt(); // UID seed
		if (tmpint != 0) throw new IOException("Unsupported DB format");
		tmpint = dis.readInt(); // reserved
		if (tmpint != 0) throw new IOException("Unsupported DB format");
		short recordCount = dis.readShort();
		int offs = 78; // number of bytes already read
		
		int[] recordOffsets = new int[recordCount];
		for (int i = 0; i < recordCount; i++) {
			recordOffsets[i] = dis.readInt();
			tmpint = dis.readInt(); // flags + ID
			if (tmpint != 0) throw new IOException("Unsupported DB format");
			offs += 8;
		}
		while (offs < recordOffsets[0]) {
			offs++;
			dis.readByte();
		}
		if (offs > recordOffsets[0]) throw new IOException("Unsupported DB format");
		System.out.println("Record count: "+recordCount);
		for (int i = 0; i < recordCount; i++) {
			int end;
			if (i < recordCount-1)
				end = recordOffsets[i+1];
			else
				end = length;
			byte[] rec = new byte[end-recordOffsets[i]];
			dis.readFully(rec);
			addRecord(rec);
			System.out.println("Loaded record of "+rec.length+" bytes.");
		}
		if (dis.read() != -1) throw new IllegalArgumentException("Incorrect length");
		dis.close();
	}
	
	public PDBFile(String pdbName, byte[] typeandcreator, short version, Date created, Date modified, Date backedUp) {
		if (typeandcreator.length != 8) 
			throw new IllegalArgumentException("Invalid Type and Creator ID");
		if (pdbName.length() > 31)
			throw new IllegalArgumentException("PDB name too long");
		Date now = new Date();
		if (created == null) created = now;
		if (modified == null) modified = now;
		if (backedUp == null) backedUp = new Date(0);
		this.pdbName = pdbName;
		this.typeandcreator = typeandcreator;
		this.version = version;
		this.created = created;
		this.modified = modified;
		this.backedUp = backedUp;
	}
	
	public int getRecordCount() {
		return records.size();
	}
	
	public PDBRecord getRecord(int index) {
		return records.get(index);
	}
	
	public void addRecord(PDBRecord r) {
		records.add(r);
	}
	
	public void addRecord(byte[] record) {
		addRecord(new PDBRecord(record));
	}
	
	public void addRecords(byte[] data, int chunksize) {
		int offs = 0;
		while (offs < data.length - chunksize) {
			addRecord(new PDBRecord(data, offs, chunksize));
			offs += chunksize;
		}
		addRecord(new PDBRecord(data, offs, data.length - offs));
	}
	
	public byte[] generate() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		generate(baos);
		return baos.toByteArray();
	}
	
	public void generate(OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		
		byte[] name = pdbName.getBytes("ISO-8859-1");
		dos.write(name);
		dos.write(new byte[32 - name.length]);
		dos.writeShort(0x0008); // backup DB
		dos.writeShort(version);
		dos.writeInt((int)(created.getTime()/1000)); // Created
		dos.writeInt((int)(modified.getTime()/1000)); // Last modified
		dos.writeInt((int)(backedUp.getTime()/1000)); // Not backupped yet
		dos.writeInt(0); // Modification Number
		dos.writeInt(0); // No AppInfoArea
		dos.writeInt(0); // No SortInfoArea
		dos.write(typeandcreator);
		dos.writeInt(0); // UID Seed
		dos.writeInt(0); // Reserved
		dos.writeShort(records.size());
		int ptr = 78 + records.size() * 8;
		for (PDBRecord r : records) {
			ptr = r.setOffset(ptr);
			r.writeHeader(dos);

		}
		for (PDBRecord r : records) {
			r.writeData(dos);
		}
		dos.close();
	}
}
