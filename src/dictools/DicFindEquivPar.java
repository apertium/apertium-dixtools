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
package dictools;

import dictools.xml.DictionaryReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.Par;
import dics.elements.dtd.Pardef;
import dics.elements.dtd.Pardefs;
import dics.elements.dtd.Section;
import dics.elements.utils.EElementList;
import dics.elements.utils.Msg;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class DicFindEquivPar  extends AbstractDictTool {

    /**
     * 
     */
    private Dictionary dic;
    /**
     * 
     */
    private String outFileName;

    /**
     * 
     * 
     */
    public DicFindEquivPar() {
    }

    /**
     * 
     * 
     */
    public DicFindEquivPar(String fileName) {
        DictionaryReader dicReader = new DictionaryReader(fileName);
        Dictionary dic = dicReader.readDic();
        setDic(dic);
    }

    /**
     * 
     * 
     */
    public void findEquivalents() {

        ArrayList<Pardef> pardefs = dic.getPardefsElement().getPardefElements();

        HashMap<String, Pardef> name2pardef = new HashMap<String, Pardef>();
        HashMap<String, String> name2replacementName = new HashMap<String, String>();
        HashMap<String, Integer> usagecounter = new HashMap<String, Integer>();
        String replacementName;

        for (Iterator<Pardef> pi =  pardefs.iterator(); pi.hasNext(); ) {
            Pardef par = pi.next();

            // remove duplicate names
            if (name2pardef.containsKey(par.getName())) {
                Pardef first = name2pardef.get(par.getName());
                if (par.contentEquals(first)) {
                    msg.err("Subsequent instance of " +par.getName() + " deleted");
                    first.getEElements().addAll(par.getEElements());
                } else {
                    msg.err("WARNING: Subsequent instance of " +par.getName() + " has other contents than original!\nMerging the 2 paradigms (the same way as lt-toolbox would)");
                    E firstEe = par.getEElements().get(0);
                    if (firstEe!=null) {
                        firstEe.addProcessingComment("Below is content of a subsequent definition of "+par.getName());
                        first.getEElements().addAll(par.getEElements());
                    }
                }
                pi.remove();
                continue;
            }
            // this pararigm will be retained
            name2pardef.put(par.getName(), par);
        }

        // now, start over with a fresh data structure, adding paradigms as we meet them
        name2pardef.clear();

        pardefLoop:
        for (Iterator<Pardef> pi =  pardefs.iterator(); pi.hasNext(); ) {
            Pardef par = pi.next();

            // seach for existing pardef with same content and remove
            for (Pardef existingPardef : name2pardef.values()) {
                if (par.contentEquals(existingPardef)) {
                    if (name2replacementName.containsKey(existingPardef.getName())) {
                        replacementName = name2replacementName.get(existingPardef.getName());
                    } else {
                        replacementName = existingPardef.getName();
                    }
                    msg.err(par.getName() + " will be replaced with "+replacementName);
                    name2replacementName.put(par.getName(), replacementName);
                    pi.remove();
                    continue pardefLoop;
                }
            }

            // this pararigm will be retained
            name2pardef.put(par.getName(), par);
        }



        // Iterate throught all paradigm definitions and sections and replace paradigms
        ArrayList<E> allElements = new ArrayList<E>();

        for (Pardef pardef : dic.getPardefsElement().getPardefElements()) {
            allElements.addAll(pardef.getEElements());
        }
        for (Section section : dic.getSections()) {
            allElements.addAll(section.getEElements());
        }

        // Now replace paradigms
        HashMap<String, Integer> replacementcounter = new HashMap<String, Integer>();
        for (E ee : allElements) {
            for (DixElement e : ee.getChildren()) {
                if (e instanceof Par) {
                    Par parE = (Par) e;
                    String name = parE.getValue();
                    String newName = name2replacementName.get(name);
                    if (newName != null) {
                        parE.setValue(newName);
                        parE.addProcessingComment(name + "' was replaced");

                        Integer count = replacementcounter.get(name);
                        replacementcounter.put(name, count==null? 1 : count + 1);
                        name = newName; // for use below
                    }

                    Integer count = usagecounter.get(name);
                    usagecounter.put(name, count==null? 1 : count + 1);
                }
            }
        }

        if (replacementcounter.isEmpty()) msg.err("\nNo replacements were made");
        else for (String key : replacementcounter.keySet()) {
            msg.err("'" + key + "' has been replaced " + replacementcounter.get(key) + " times.");
        }

        // find unused pardefs and delete them
        for (Iterator<Pardef> pi =  pardefs.iterator(); pi.hasNext(); ) {
            Pardef par = pi.next();
        // Wah! We don't want this to happen!
        /*
            PardefElement par = pi.next();
            if (usagecounter.get(par.getName())==null) {
                msg.err("Unused paradigm  " +par.getName() + " deleted");
                pi.remove();
            }
         */
        }

        dic.printXML(getOutFileName(),getOpt());

    }


    /**
     * @return the dic
     */
    private Dictionary getDic() {
        return dic;
    }

    /**
     * @param dic
     *                the dic to set
     */
    private void setDic(Dictionary dic) {
        this.dic = dic;
    }

    /**
     * @return the outFileName
     */
    public String getOutFileName() {
        return outFileName;
    }

    /**
     * @param outFileName
     *                the outFileName to set
     */
    public void setOutFileName(String outFileName) {
        this.outFileName = outFileName;
    }
}
