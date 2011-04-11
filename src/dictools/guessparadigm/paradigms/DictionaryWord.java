/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dictools.guessparadigm.paradigms;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that contains a word in the dictionary, it is, a stem related to a paradigm
 * from which all the possible inflections can be obtained.
 * @author Miquel Esplà i Gomis
 */
public class DictionaryWord {

    /** Stem of the word */
    private String steam;

    /** Profile of the word, consisting in a list of inflections and the number of aparitions in a monolingual corpus */
    private Map<String,Integer> profile;

    /**
     * Overloaded constructor of the class
     * @param steam Stem of the word
     */
    public DictionaryWord(String steam){
        this.steam=steam;
    }

    /**
     * Method that returns the profile of a stem and a paradigm
     * @param steam Stem of the word
     * @param paradigm Paradigm which generates the inflections
     * @param wordlist Word list in which the inflections should be searched
     * @return Returns the normalized profile of a word
     */
    public Map<String,Integer> GetNormalizedProfile(String steam, Paradigm paradigm, Map<String,Integer> wordlist){
        Map<String,Integer> hits=new HashMap<String, Integer>();
        for(Suffix s: paradigm.getSuffixes()){
            if(wordlist.containsKey(steam+s.getSuffix()))
                hits.put(s.getSuffix(), 1);
            else
                hits.put(s.getSuffix(), 0);
        }
        return hits;
    }

    /**
     * Method that computes the profile of a word and saves it in the variable {@link #profile}
     * @param paradigm Paradigm which generates the inflections
     * @param wordlist Word list in which the inflections should be searched
     */
    public void setProfile(Paradigm paradigm, Map<String,Integer> wordlist){
        this.profile=GetNormalizedProfile(steam, paradigm, wordlist);
    }

    /**
     * Method that returns a saved profile
     * @return A saved profile
     */
    public Map<String, Integer> getProfile() {
        return profile;
    }

    /**
     * Method that returns a profile
     * @return Returns a profile
     */
    public String getSteam() {
        return steam;
    }

    /**
     * Method that sets the stem of the object
     * @param steam New stem to be set
     */
    public void setSteam(String steam) {
        this.steam = steam;
    }

    /**
     * Method that returns the percentage of inflections of the word appearing in the monolingual corpus
     * @return Returns the percentage of inflections of the word appearing in the monolingual corpus
     */
    public double getPercent(){
        int total=0;
        for(int i: this.profile.values())
            total+=i;
        return (double)total/this.profile.values().size();
    }
}
