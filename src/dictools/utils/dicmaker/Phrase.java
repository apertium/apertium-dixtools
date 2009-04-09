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

public class Phrase {
	private final int language;
	private final String phrase;

	public Phrase(int language, String phrase) {
		if (phrase == null) throw new IllegalArgumentException();
		this.language = language;
		this.phrase = phrase;
	}
	
	public int getLanguage() {
		return language;
	}
	
	public String getPhrase() {
		return phrase;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + language;
		result = prime * result + phrase.hashCode();
		return result;
	}

@Override
	public String toString() {
    return getPhrase();
  }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Phrase other = (Phrase) obj;
		if (language != other.language)
			return false;
		if (!phrase.equals(other.phrase))
			return false;
		return true;
	}
}
