/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dics.elements.dtd;

/**
 *
 * @author j
 */
public interface CharacterDataNeighbour {
    /**
     * XML processingComments, blanks and newlines originating from a loaded file. Will be added before the XML elemen (before processingComments)
     */
    public void setPrependCharacterData(String prependCharacterData);

    /**
     * XML processingComments, blanks and newlines originating from a loaded file. Will be added after the XML elemen (before processingComments)
     */
    public void setAppendCharacterData(String appendCharacterData);
}
