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

import dics.elements.dtd.*;
import dictools.AbstractDictTool;
import dictools.guessparadigm.suffixtree.Pair;
import dictools.utils.DictionaryReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DictEnhancer extends AbstractDictTool{
    
    private Dictionary _dic;
    private boolean interactive;
    private String out;
    private Scanner scanner;
    private boolean exit;
    private DictEnhancerTexts _texts;
    private StemGuesser _stemGuesser;
    
    public DictEnhancer() {
        msg.setLogFileName("enhancer.log");
    }
    
    public void doEnhance() {
        processArguments();
        actionEnhancement();
    }
    
    private void actionEnhancement() {
         enhance();
         _dic.printXMLToFile(out,opt);
    }
    
    private void enhance() {
        
        System.out.println("'enhance' method");

        _texts.askForNewWord();
        while(scanner.hasNextLine()) {
            Pair<String,String> pair = handleNewOldWord();
            
            if(pair != null) {
                E element = findNewElement(pair);
                if(element != null) {
                    addElementToDictionary(element);   
                }
            } else {
                if(exit) {
                    return;
                }
                _texts.incorrectWordFormat();
            }
            
            _texts.askForNewWord();
        }
    }
    
    private void addElementToDictionary(E element) {
        for(Section section : _dic.sections) {
            if (!section.id.equals("main")) {
                continue;
            }
            
            section.elements.add(element);

            if(element.lemma.contains("l·")) {

                String newStem = element.getFirstPartAsL().getValue().replace("l·", "ŀ");

                E clonedElement = cloneElementForAnalysis(newStem, element);

                section.elements.add(clonedElement);
            }

            _texts.elementAdded(element);
        }
    }
    
    private List<E> findOldWord(String word) {
        List<E> newElements = new ArrayList<E>();
        for(Section section : _dic.sections) {
            if (!section.id.equals("main")) {
                continue;
            }
            
            for(E element : section.elements) {
                if(element.lemma != null) {
                    if(element.lemma.equals(word)) {
                        I i = element.getFirstI();
                        if(i!=null) {
                            newElements.add(element);
                        }
                    }
                } else {
                    System.out.println(element);
                }
            }
        }
        return newElements;
    }
    
    private void processArguments() {
        int i=1;
        while (i<arguments.length) {
            String a = arguments[i].toLowerCase();
            if (a.startsWith("-interactive")) {
                interactive = true;
            } else break; // not recognized, must be file name then
            i++;
        }
        
        _dic = getDictionary(arguments[i]);
        out = arguments[i+1];
        scanner = new Scanner(System.in);
        _texts = new DictEnhancerTexts(scanner);
        _stemGuesser = new StemGuesser(_dic, _texts);
    }

    private Pair<String, String> handleNewOldWord() {
        String line = scanner.nextLine();
        if(line.equals("--exit")) {
            exit = true;
            return null;
        }
        
        String [] parts = line.split(",");
        
        if(parts.length != 2) {
            return null;
        }
        
        for(int i=0; i< parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        
        
        return new Pair<String, String>(parts[0],parts[1]);
    }

    private E findNewElement(Pair<String, String> pair) {
        E newElement = null;
        
        String newWord = pair.getFirst();
        String oldWord = pair.getSecond();
        
        List<E> elements = findOldWord(oldWord);
        if(!elements.isEmpty()) {
            for(E element : elements) {
                
                if(_texts.askForOldWord(element)) {
                    String newStem = _stemGuesser.GetStem(newWord, element);
                    
                    if(newStem != null) {
                        newElement = createElement(newWord, newStem, element);
                    } else {
                        newElement = null;
                    }
                }
            }
        } else {
            _texts.wordNotFound();
        }
        
        return newElement;
    }

    private E createElement(String newLemma, String newStem,E oldElement) {
        E newElement = oldElement.copy();
        newElement.lemma = newLemma;
        ContentElement l = newElement.getFirstPart("L");
        l.setValue(newStem);
        l.children.get(0).setValue(newStem);
        _texts.printResult(newElement);
        
        return newElement;
    }

    private E cloneElementForAnalysis(String newStem, E oldElement) {
        E newElement = oldElement.copy();

        R newR = new R(oldElement.getFirstPartAsR());
        L newL = new L(oldElement.getFirstPartAsL());
        newL.setValue(newStem);

        P newP = new P();
        newP.l = newL;
        newP.r = newR;

        newElement.children = new ArrayList<DixElement>();
        newElement.children.add(newP);
        newElement.children.add(oldElement.getFirstParadigm());

        newElement.restriction = "LR";

        return newElement;
    }

    private Dictionary getDictionary(String dictionaryName) {
        DictionaryReader dicReader = new DictionaryReader(dictionaryName);
        Dictionary dictionary = dicReader.readDic();
        dictionary.fileName = dictionaryName;
        
        return dictionary;
    }
}
