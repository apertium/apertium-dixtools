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
 * <p>Description: MatchingPredicate defines interface for word matching.</p>
 * <p>Copyright: Copyright (C) 2004 I. Dan Melamed</p>
 * <p>Company: Department of Computer Science, New York University</p>
 * @author Luke Shen
 */

import java.util.Properties;
import java.util.List;

abstract interface MatchingPredicate {
  /**
   * Sets properties.
   * @param properties            properties
   */
  public void setProperties(Properties properties);

  /**
   * Checks whether two words match.
   * @param wordForMatch          word to match from
   * @param wordToMatch           word to match to
   * @return                      true if two words match
   */

  public boolean isMatch(List<?> wordForMatch, List<?> wordToMatch, boolean isXAxis);
}