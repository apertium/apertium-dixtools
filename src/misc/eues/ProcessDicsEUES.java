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
package misc.eues;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class ProcessDicsEUES {

    /**
     * 
     */
    private String action;
    /**
     * 
     */
    private String[] arguments;

    /**
     * Método principal.
     * 
     * @param args
     */
    public static void main(String[] args) {
        ProcessDicsEUES ps = new ProcessDicsEUES(args);
        ps.go();
    }

    /**
     * 
     * @param args
     */
    public ProcessDicsEUES(String[] args) {
        setArguments(args);
    }

    /**
     * 
     * 
     */
    public void go() {
        checkAction();
    }

    /**
     * 
     * 
     */
    public void checkAction() {
        if (getArguments().length == 0) {
            System.err.println("Usage: java ProcessDics <action> [options]");
            System.exit(-1);

        }
        setAction(getArguments()[0]);

        if (getAction().equals("addgender")) {
            if (getArguments().length != 4) {
                System.err.println("Usage: java ProcessDics addgender <morph-source> <bil> <out>");
                System.exit(-1);
            } else {
                AddGender addGender = new AddGender();
                addGender.setArguments(arguments);
                addGender.doAddGender();
            }
        }

        if (getAction().equals("addgender-adj")) {
            if (getArguments().length != 4) {
                System.err.println("Usage: java ProcessDics addgenderadj <morph-source> <bil> <out>");
                System.exit(-1);
            } else {
                AddGenderAdj addGenderAdj = new AddGenderAdj();
                addGenderAdj.setArguments(arguments);
                addGenderAdj.doAddGender();
            }
        }

        if (getAction().equals("assignparadigm")) {
            if (getArguments().length != 4) {
                System.err.println("Usage: java ProcessDics assignparadigm <morph-source> <bil> <out>");
                System.exit(-1);
            } else {
                AssignParadigm a = new AssignParadigm();
                a.setArguments(arguments);
                a.doAssignParadigm();
            }
        }

    }

    /**
     * @return the action
     */
    private String getAction() {
        return action;
    }

    /**
     * @param action
     *                the action to set
     */
    private void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the arguments
     */
    private String[] getArguments() {
        return arguments;
    }

    /**
     * @param arguments
     *                the arguments to set
     */
    private void setArguments(String[] arguments) {
        this.arguments = arguments;
    }
}
