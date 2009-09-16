/*
 * Copyright (C) 2009 Universitat d'Alacant / Universidad de Alicante
 * Copyright (C) 2009 Enrique Benimeli Bofarull
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your soption) any later version.
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import dics.elements.dtd.Dictionary;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class TinyFilter {

    private String xmlFileName;
    private Properties props;
    private boolean ignoreNotListedTags = false;
    private HashMap<String, String> preservedTags;
    private HashMap<String, String> ignoredTags;
    private HashMap<String, String> removedCharacters;
    private HashMap<String, String> replacedCharactersWithBlank;
    private HashMap<String, String> renamed;

    public TinyFilter() {
        init();
    }

    public TinyFilter(String fileName) {
        this.xmlFileName = fileName;
        this.props = this.readProperties(this.xmlFileName);
        init();
        this.processProperties();
    }

    private void init() {
        preservedTags = new HashMap<String, String>();
        ignoredTags = new HashMap<String, String>();
        removedCharacters = new HashMap<String, String>();
        replacedCharactersWithBlank = new HashMap<String, String>();
        renamed = new HashMap<String, String>();

    }

    private Properties readProperties(String fileName) {
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(fileName);
            properties.loadFromXML(fis);
        } catch (IOException ioe) {
        }
        return properties;
    }

    /**
     * Dictionary pre-filter (optional)
     * @param dic
     * @return pre-filtered dictionary
     */
    public Dictionary doFilter(Dictionary dic) {
        //this.printProperties();
        return dic;
    }

    private final void processProperties() {
        Set<?> set = props.keySet();
        Iterator<?> it = set.iterator();

        while (it.hasNext()) {
            String key = (String) it.next();
            String value = (String) props.get(key);

            if (key.equals("ignoreNotListedTags")) {
                if (value.equals("no")) {
                    this.setIgnoreNotListedTags(false);
                } else {
                    this.setIgnoreNotListedTags(true);
                }
            }

            if (key.equals("preserveTags")) {
                this.storeTags(preservedTags, value);
            }
            if (key.equals("ignoreTags")) {
                this.storeTags(ignoredTags, value);
            }

            if (key.equals("removeCharacters")) {
                this.storeTags(removedCharacters, value);
            }

            if (key.equals("replaceCharactersWithBlank")) {
                this.storeTags(replacedCharactersWithBlank, value);
            }
            if (key.startsWith("rename_")) {
                String[] keyStr = key.split("_");
                String tagName = keyStr[1];
                this.renamed.put(tagName, value);
            }
        }
    }

    private final void storeTags(HashMap<String, String> list, String listOfTags) {
        String[] tags = listOfTags.split(",");
        for (int i = 0; i < tags.length; i++) {
            list.put(tags[i], tags[i]);
        }
    }

    public final boolean isTagPreserved(String tagName) {
        return (this.preservedTags.containsKey(tagName));
    }

    public final boolean isTagIgnored(String tagName) {
        return (this.ignoredTags.containsKey(tagName));
    }

    public final boolean isCharRemoved(String character) {
        return (this.removedCharacters.containsKey(character));
    }

    public final boolean isCharReplacedWithBlank(String character) {
        return (this.replacedCharactersWithBlank.containsKey(character));
    }

    public final boolean preserve(String tagName) {
        boolean preserve = isTagPreserved(tagName);
        boolean ignore = isTagIgnored(tagName);
        boolean ignoreNotListed = isIgnoreNotListedTags();
        return (preserve || (!ignore && !preserve && !ignoreNotListed));
    }

    public final String rename(String tagName) {
        if (this.renamed.containsKey(tagName)) {
            return this.renamed.get(tagName);
        } else {
            return tagName;
        }
    }

    public final String applyToLemma(String lemma) {
        Set<String> removeKeySet = this.removedCharacters.keySet();
        Iterator<String> removeIt = removeKeySet.iterator();
        while (removeIt.hasNext()) {
            String key = (String) removeIt.next();
            String v = this.removedCharacters.get(key);
            lemma = lemma.replaceAll(v, "");
        }

        Set<String> replaceKeySet = this.replacedCharactersWithBlank.keySet();
        Iterator<String> replaceIt = replaceKeySet.iterator();
        while (replaceIt.hasNext()) {
            String key = (String) replaceIt.next();
            String v = this.replacedCharactersWithBlank.get(key);
            lemma = lemma.replaceAll(v, " ");
        }
        return lemma;
    }

    public void printProperties() {
        Set<?> set = props.keySet();
        Iterator<?> it = set.iterator();

        while (it.hasNext()) {
            Object key = it.next();
            String value = (String) props.get(key);
            System.err.println("(" + key + "," + value + ")");
        }
    }

    /**
     * @return the ignoreNotListedTags
     */
    public boolean isIgnoreNotListedTags() {
        return ignoreNotListedTags;
    }

    /**
     * @param ignoreNotListedTags the ignoreNotListedTags to set
     */
    public void setIgnoreNotListedTags(boolean ignoreNotListedTags) {
        this.ignoreNotListedTags = ignoreNotListedTags;
    }
}
