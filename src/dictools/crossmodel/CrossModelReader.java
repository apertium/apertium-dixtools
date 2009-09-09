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
package dictools.crossmodel;

import org.w3c.dom.Element;

import dics.elements.dtd.E;
import dictools.xml.XMLReader;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class CrossModelReader extends XMLReader {

    /**
     * 
     * @param fileName
     */
    public CrossModelReader(String fileName) {
        super(fileName);
    }

    /**
     * 
     * @return Undefined         */
    public CrossModel readCrossModel() {
        analize();
        CrossModel crossModel = new CrossModel();
        Element root = getDocument().getDocumentElement();
        for (Element childElement : readChildren(root)) {
            String childElementName = childElement.getNodeName();
            if (childElementName.equals("cross-action")) {
                CrossAction crossAction = readCrossAction(childElement);
                crossModel.addCrossAction(crossAction);
            }
        }
        root = null;
        setDocument(null);
        return crossModel;
    }

    /**
     * 
     * @param e
     * @return Undefined
     */
    public CrossAction readCrossAction(Element e) {
        CrossAction crossAction = new CrossAction();
        String id = getAttributeValue(e, "id");
        crossAction.setId(id);

        for (Element childElement : readChildren(e)) {
            String childElementName = childElement.getNodeName();
            if (childElementName.equals("pattern")) {
                Pattern pattern = readPattern(childElement);
                crossAction.setPattern(pattern);
            }
            if (childElementName.equals("action-set")) {
                ActionSet actionSet = readActionSet(childElement);
                crossAction.setActionSet(actionSet);
            }

        }
        return crossAction;
    }

    /**
     * 
     * @param e
     * @return Undefined         
     */
    public Pattern readPattern(Element e) {
        int i = 0;
        Pattern pattern = new Pattern();

        for (Element childElement : readChildren(e)) {
            String childElementName = childElement.getNodeName();
            if (childElementName.equals("e")) {
                E eE = readEElement(childElement);
                if (i == 0) {
                    pattern.setAB(eE);
                }
                if (i == 1) {
                    pattern.setBC(eE);
                }
                i++;
            }
        }
        return pattern;
    }

    /**
     * 
     * @param e
     * @return Undefined         
     */
    public ActionSet readActionSet(Element e) {
        ActionSet actionSet = new ActionSet();
        for (Element childElement : readChildren(e)) {
            String childElementName = childElement.getNodeName();
            if (childElementName.equals("action")) {
                Action action = readAction(childElement);
                if (action.getE().hasRestriction()) {
                }
                actionSet.add(action);
            }
        }
        return actionSet;
    }

    /**
     * 
     * @param e
     * @return Undefined        
     */
    public Action readAction(Element e) {
        Action action = new Action();
        for (Element childElement : readChildren(e)) {
            String childElementName = childElement.getNodeName();
            if (childElementName.equals("e")) {
                E eE = readEElement(childElement);
                action.setAction(eE);
            }
        }
        return action;
    }
}
