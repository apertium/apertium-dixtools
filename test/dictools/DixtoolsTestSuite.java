/*
 * Copyright (C) 2008 Dana Esperanta Junulara Organizo http://dejo.dk/
 * Author: Jacob Nordfalk
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

package dictools;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author j
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({dictools.ReadAndWriteMonodixTest.class, dictools.ReadAndWriteBidixTest.class, misc.eoen.SubstractBidixTest.class, dictools.ZCommandLineTest.class})
//@Suite.SuiteClasses({dictools.ReadAndWriteMonodixTest.class})
public class DixtoolsTestSuite {
  static boolean onlyCLI = false;

}