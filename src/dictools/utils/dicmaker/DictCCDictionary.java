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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class DictCCDictionary extends WordTupleDictionary {
	
	public DictCCDictionary(InputStream in, String slFull, String tlFull) throws IOException {
		super(new String[] {slFull, tlFull}, new IndexBuilder[] {WordIndexBuilder.INSTANCE, WordIndexBuilder.INSTANCE});
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "ISO-8859-1"));
		String line = br.readLine();
		while(line.startsWith("# ")) {
			setDescription(getDescription()+line.substring(2)+"\n");
			line = br.readLine();
		}
		if (line.length() == 0) line = br.readLine();
		while (line != null) {
			parseLine(line);
			line = br.readLine();
		}
	}

	private void parseLine(String line) {
		String[] leftright = line.split("::");
		if (leftright.length != 2) {
			System.out.println("\tWarning: Unparseable line: "+line);
			return;
		}
		addTuple(leftright[0].trim(), leftright[1].trim());
	}
}
