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
package dics.elements.dtd;

import dics.elements.utils.DicOpts;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dics.elements.utils.EElementList;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class Pardef extends DixElement {

    /**
     * 
     */
    private EElementList eElements;
    /**
     * 
     */
    private String n;

    /**
     * 
     * @param value
     */
    public Pardef(String value) {
        n = value;
        eElements = new EElementList();
    }

    /**
     * Duplicates paradigm definition
     * 
     * @param orig
     * @param name
     */
    public Pardef(Pardef orig, String name) {
        n = name;
        eElements = new EElementList();

        for (E e : orig.eElements) {
            E e2 = (E) e.clone();
            eElements.add(e2);
        }
    }

    /**
     * 
     * @return Undefined         */
    public String getName() {
        return n;
    }

    // Used for renaming paradigms when merging
    public void setName(String newName) {
        n = newName;
    }

    /**
     * 
     * @param value
     */
    public void addEElement(E value) {
        eElements.add(value);
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    public void printXML(Appendable dos, DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        dos.append(prependCharacterData);
        if (!opt.noProcessingComments) dos.append(makeCommentIfData(processingComments));

        dos.append((opt.nowAlign?"":tab(2))+ "<pardef n=\"" + n + "\">"+justInsideStartTagCharacterData+"\n");
        for (E e : eElements) {
            e.printXML(dos, opt);
        }
        dos.append((opt.nowAlign?"":tab(2)) + "</pardef>"+appendCharacterData.trim()+"\n");
    }

    /**
     * Equals method. Check on equality of pardef elements, regardless to order.
     * @param pardef2
     * @return Undefined         */
    public boolean contentEquals(Pardef pardef2) {
        EElementList eList1 = getEElements();
        EElementList eList2 = pardef2.getEElements();

        if (eList1.size() != eList2.size()) {
            return false;
        }

        HashMap<String, E> elementsPardef1 = new HashMap<String, E>();

        for (E element1 : eList1) {
            elementsPardef1.put(element1.toStringAll(), element1);
        }

        for (E element2 : eList2) {
            if (!elementsPardef1.containsKey(element2.toStringAll())) {
                return false;
            }
        }
        return true;
    }


    /**
     * 
     */
    @Override
    public String toString() {
        String str = "";
        str += "<" + getName() + ">";
        for (E e : eElements) {
            str += e.toString();
        }
        return str;
    }

    /**
     * 
     * @return Undefined         */
    public String toStringNoParName() {
        String str = "";
        for (E e : eElements) {
            str += e.toStringAll();
        }
        return str;
    }

    /**
     * 
     * @param category
     * @return Undefined         */
    public boolean hasCategory(String category) {
        return (n.endsWith("__" + category));
    }

    /**
     * 
     * @param def
     * @return true if the paradigm contains certain definition ('adj', 'm', etc.)
     */
    public boolean contains(String def) {
        for (E e : eElements) {
            if (e.contains(def)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param e
     * @param mon
     * @param category
     * @param equivCategory
     * @return Undefined         
    public static Pardef duplicateParadigm(E e,
            DictionaryElement mon, String category,
            String equivCategory) {
        // Hay que cambiar la categoria (primer "s") en cada elemento 'e' en la
        // definicin del paradigma
        Pardef dupPardefE = null;
        String paradigmValue = e.getMainParadigmName();
        String dupParadigmValue = e.getMainParadigmName();
        dupParadigmValue = dupParadigmValue.replaceAll("__" + equivCategory,
                "__" + category);

        Pardef parDefInMon = mon.getParadigmDefinition(dupParadigmValue);

        if (parDefInMon == null) {
            Pardef pardefE = mon.getParadigmDefinition(paradigmValue);
            dupPardefE = new Pardef(pardefE, dupParadigmValue);
            dupPardefE.addProcessingComment("equivalent to '" + paradigmValue + "'");
            dupPardefE.changeCategory(category);
        } else {
            parDefInMon.changeCategory(category);
            return parDefInMon;
        }
        return dupPardefE;
    }
*/

    /**
     * 
     * @param newCategory
     */
    private void changeCategory(String newCategory) {
        for (E e : eElements) {
            e.changeCategory("R", newCategory);
        }
    }

    /**
     * @return the eElements
     */
    public EElementList getEElements() {
        return eElements;
    }

    /**
     * @param elements
     *                the eElements to set
     */
    public void setEElements(EElementList elements) {
        eElements = elements;
    }
}
