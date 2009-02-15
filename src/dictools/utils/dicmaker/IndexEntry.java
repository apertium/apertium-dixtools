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

public class IndexEntry {
	
	private final String word;
	private final short destRecord;

	public IndexEntry(String word, short destRecord) {
		this.word = word;
		this.destRecord = destRecord;
	}
	
	public int getLength() {
		return word.length()+4;
	}
	
	public String getWord() {
		return word;
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeByte(word.length()+3);
		out.write(word.getBytes());
		out.writeByte(0);
		out.writeShort(destRecord);
	}
}
