/*
 * Copyright 2010 Jimmy O'Regan
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

package misc.gma.train;
import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

/**
 *
 * @author jimregan
 * based on monoxmark Perl script in gma-train
 */
public class MonoXMark {
    private BigInteger INFINITY =  new BigInteger("9999999999");
    private double TINY = 0.0000001;

    private List<Double> x;
    private List<Double> y;

    private MonoXMark () {
        this.x = new ArrayList<Double>();
        this.y = new ArrayList<Double>();
    }

    void fromFile (String filename) throws IOException {
        //try {
        
    }
}
