/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Author: Enrique Benimeli Bofarull
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
package dictools.cmproc;

import dictools.crossmodel.CrossAction;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class CrossActionData {

    
    private CrossAction crossAction;
    
    private Variables vars;

    /**
     * 
     * @param crossAction
     * @param vars
     */
    public CrossActionData(CrossAction crossAction, Variables vars) {
        this.crossAction = crossAction;
        this.vars = vars;
    }

    /**
     * 
     * @return The cross action
     */
    public CrossAction getCrossAction() {
        return crossAction;
    }

    /**
     * 
     * @param crossAction
     */
    public void setCrossAction(CrossAction crossAction) {
        this.crossAction = crossAction;
    }

    /**
     * 
     * @return The variables
     */
    public Variables getVars() {
        return vars;
    }

    /**
     * 
     * @param vars
     */
    public void setVars(Variables vars) {
        this.vars = vars;
    }
}
