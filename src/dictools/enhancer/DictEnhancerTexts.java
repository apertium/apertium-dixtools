/*
 * Copyright (C) 2013 
 * Author: Xavier Ivars i Ribes
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
package dictools.enhancer;

import dics.elements.dtd.E;
import java.util.Scanner;

public class DictEnhancerTexts {
    
    private Scanner _scanner;
    
    public DictEnhancerTexts(Scanner scanner) {
        _scanner = scanner;
    }
    
    public boolean askConfirmation() {
        System.out.println("Is this correct? (y/n)");
        String answer;
        while (_scanner.hasNextLine()) {
            answer = _scanner.nextLine();
            if (answer.equalsIgnoreCase("y")) {
                return true;
            }
            if (answer.equalsIgnoreCase("n")) {
                return false;
            }
            System.out.println("Incorrect answer");
            System.out.println("Is this correct? (y/n)");
        }
        return false;
    }

    public boolean askForOldWord(E element) {
        System.out.println("Element found:");
        System.out.println("\t" + element);
        return askConfirmation();
    }

    public void askForNewWord() {
        System.out.println("Enter the word you want to add to the dictionaries,");
        System.out.println("followed by a word already in the dictionaries separated by ','");
        System.out.println();
        System.out.println("(enter --exit to finish)");
    }

    public void wordNotFound() {
        System.out.println("The existing word you added does not exist in the current dictionary");
        hr();
    }

    public void incorrectWordFormat() {
        System.out.println("Wrong format");
    }

    public void printResult(E newElement) {
        System.out.println("Result\t" + newElement.toString());
    }
    
    public void elementAdded(E element) {
        System.out.println("Element added: ("+element.toString()+")");
        System.out.println();
        hr();
    }
    
    private void hr() {
        System.out.println("--------------------------------------------------------------------");
    }
}
