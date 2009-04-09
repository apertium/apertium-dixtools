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

import dics.elements.utils.DicOpts;
import dics.elements.utils.Msg;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author j
 */
public class AbstractDictTool {

    /**
     * Options
     */
    protected DicOpts opt = DicOpts.STD;

    /**
     * Options
     */
    public DicOpts getOpt() {
        return opt;
    }

    /**
     * Options
     */
    public void setOpt(DicOpts opt) {
        this.opt = opt;
    }
    protected Msg msg = new Msg();
    /**
     * 
     */
    protected String[] arguments;

    /**
     * @return the arguments
     */
    public String[] getArguments() {
        return arguments;
    }

    /**
     * @param arguments
     *                the arguments to set
     */
    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }
    /*
    -align 10 55
    -pardef
     */
}
