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

import java.io.DataOutputStream;
import java.io.IOException;

public class PDBRecord {
	private byte[] record;
	private int offset;
	
	public PDBRecord(byte[] data) {
		this(data, 0, data.length);
	}
	
	public PDBRecord(byte[] data, int offset, int length) {
		record = new byte[length];
		System.arraycopy(data, offset, record, 0, length);
	}

	public int setOffset(int ptr) {
		offset = ptr;
		return ptr + record.length;
	}

	public void writeHeader(DataOutputStream dos) throws IOException {
		dos.writeInt(offset);
		dos.writeInt(0x00); // Flags + ID
	}

	public void writeData(DataOutputStream dos) throws IOException {
		dos.write(record);
	}

	public byte[] getData() {
		return record;
	}
}
