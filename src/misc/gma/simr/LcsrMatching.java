/*
 * Geometric Mapping and Alignment (GMA) software
 *
 * COPYRIGHT (C) 1996-2004 by I. Dan Melamed
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
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

package misc.gma.simr;


/**
 * <p>Title: </p>
 * <p>Description: LcsrMatching represents longest common string matching.</p>
 * <p>Copyright: Copyright (C) 2004 I. Dan Melamed</p>
 * <p>Company: Department of Computer Science, New York University</p>
 * @author Luke Shen
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

public class LcsrMatching implements MatchingPredicate {

  //constant in the property file
  public static final String LCSR_THRESHOLD = "lcsrThreshold";
  public static final String MINIMUM_WORD_LENGTH = "minimumWordLength";

  private float lcsrThreshold;  //threshold in checking whether two words matches
  private int minimumWordLength;  //minimum length of word for matching

  /**
   * Sets properties.
   * @param properties            properties
   */
  public void setProperties(Properties properties) {
    lcsrThreshold = Float.parseFloat(properties.getProperty(LCSR_THRESHOLD));
    minimumWordLength = Integer.parseInt(properties.getProperty(MINIMUM_WORD_LENGTH));
  }

  /**
   * Checks whether two words matches.
   * @param wordForMatch          word to match from
   * @param wordToMatch           word to match to
   * @return                      true if two words match
   */
  public boolean isMatch(List wordForMatch, List wordToMatch, boolean isXAxis) {

    if (wordForMatch.equals(wordToMatch)) {
      return true;
    }

    //if (wordForMatch.length() < minimumWordLength || wordToMatch.length() < minimumWordLength) {
    if (wordForMatch.size() < minimumWordLength || wordToMatch.size() < minimumWordLength) {
      return false;
    }

    int lcsLength = computeLcs(wordForMatch, wordToMatch);
    float ratio;
    if (wordForMatch.size() > wordToMatch.size()) {
	ratio = (float)lcsLength / wordForMatch.size();
    } else {
	ratio = (float)lcsLength / wordToMatch.size();
    }
    return ratio > lcsrThreshold;
  }

  /**
   * Computes longest common string.
   * @param wordForMatch              word to match from
   * @param wordToMatch               word to match to
   * @return                          length of longest common string
   */
  public int computeLcs(List wordForMatch, List wordToMatch) {

    int wordLengthForMatch = wordForMatch.size();
    int wordLengthToMatch = wordToMatch.size();
    int[][] table = new int[wordLengthForMatch][wordLengthToMatch];

    if (wordForMatch.get(0).equals(wordToMatch.get(0))) {
      table[0][0] = 1;
    } else {
      table[0][0] = 0;
    }

    for (int index = 1; index < wordLengthForMatch; index++) {
	if (wordForMatch.get(index).equals(wordToMatch.get(0))) {
        table[index][0] = 1;
      } else {
        table[index][0] = table[index - 1][0];
      }
    }

    for (int index = 1; index < wordLengthToMatch; index++) {
	if (wordForMatch.get(0).equals(wordToMatch.get(index))) {
        table[0][index] = 1;
      } else {
        table[0][index] = table[0][index - 1];
      }
    }

    for (int outerIndex = 1; outerIndex < wordLengthForMatch; outerIndex++) {
      for (int innerIndex = 1; innerIndex < wordLengthToMatch; innerIndex++) {
	  if (wordForMatch.get(outerIndex).equals(wordToMatch.get(innerIndex))) {
          table[outerIndex][innerIndex] = table[outerIndex - 1][innerIndex - 1] + 1;
        } else {
          if (table[outerIndex][innerIndex - 1] > table[outerIndex -1][innerIndex]) {
            table[outerIndex][innerIndex] = table[outerIndex][innerIndex - 1];
          } else {
            table[outerIndex][innerIndex] = table[outerIndex -1][innerIndex];
          }
        }
      }
    }

    return table[wordLengthForMatch - 1][wordLengthToMatch - 1];
  }

}