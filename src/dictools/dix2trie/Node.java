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
package dictools.dix2trie;

import dics.elements.dtd.E;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Node {

    /**
     * List of nodes
     */
    private NodeList nodeList;
    /**
     * List of words
     */
    private WordList wordList;
    /**
     * Node value
     */
    private String value;

    /**
     * Constructor
     */
    public Node() {
        nodeList = new NodeList();
        wordList = new WordList();
    }

    /**
     * Constructor with value
     * @param value
     */
    public Node(String value) {
        this.value = value;
        nodeList = new NodeList();
        wordList = new WordList();
    }

    /**
     * 
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 
     * @return The node value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * 
     * @return The list of nodes
     */
    public NodeList getNodeList() {
        return this.nodeList;
    }

    /**
     * Adds a child node
     * @param node
     * @param entry
     * @param i
     */
    public void addChildNode(Node node, Entry entry, int i) {
        String key = entry.getKey();
        if (i <= (key.length() + 1)) {
            if (nodeList.containsKey(node.getValue())) {
                node = nodeList.get(node.getValue()); // eNode = existingNode
            } else {
                this.nodeList.put(node);
            }
            String ptr = null;
            if (i > key.length()) {
                ptr = node.getValue();
            } else {
                ptr = key.substring(0, i);
            }
            Node childNode = new Node(ptr);
            node.addChildNode(childNode, entry, i + 1);
        }

        if (i > (key.length() + 1)) {
            Word word = new Word(entry);
            //Word word = new Word(entry.getKey(), entry.getValue());
            this.wordList.put(word);
        }
    }

    /**
     * Gets the list of words
     * @return The list of words
     */
    public WordList getWordList() {
        return this.wordList;
    }

    /**
     * Prints the node
     * @param tabs
     * @param osw
     */
    public void print(int tabs, OutputStreamWriter osw) {
        try {
            String sTabs = "";
            for (int i = 0; i < tabs; i++) {
                sTabs += " ";
            }
            if (this.getValue().equals("@root")) {
                osw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                osw.write(sTabs + "<root v=\"\">\n");
            } else {
                if (wordList.size() > 0) {
                    osw.write(sTabs + "<n v=\"" + E.escapeXmlAttr(this.getValue()) + "\">\n");
                }
            }

            if (this.wordList != null) {
                for (Word word : wordList) {
                    word.print(tabs + 1, osw);
                }
            }
            Set<?> keySet = nodeList.keySet();
            Iterator<?> it = keySet.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();

                //int l = key.length();
                boolean validKey = true;
                /*
                for(int i=0; i<l; i++) {
                Character c = key.charAt(i);
                if(!Character.isLetter(c)) {
                validKey = false;
                System.err.println("Key '" + key + "'contains '" + c + "' character");
                }
                }*/

                if (validKey) {
                    Node node = nodeList.get(key);
                    node.print(tabs + 1, osw);
                }
            }
            if (this.getValue().equals("@root")) {
                osw.write(sTabs + "</root>\n");
                osw.write("\n");
            } else {
                if (wordList.size() > 0) {
                    osw.write(sTabs + "</n>\n");
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }
}
