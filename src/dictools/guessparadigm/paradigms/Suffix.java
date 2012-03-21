package dictools.guessparadigm.paradigms;

import java.io.Serializable;
import java.util.List;

/**
 * Class which contains a string which is used as a suffix fo a word or set of
 * words.
 * @author Miquel Esplà i Gomis
 */
public class Suffix implements Serializable{
    /** The suffix */
    private String suffix;

    private List<String> lexinfo;

    /**
     * Overloaded constructor of the class
     * @param suffix New suffix
     */
    public Suffix(String suffix){
        this.suffix=suffix;
        this.lexinfo=null;
    }

    /**
     * Overloaded constructor of the class
     * @param suffix New suffix
     * @param lexinfo Lexical tags
     */
    public Suffix(String suffix, List<String> lexinfo){
        this.suffix=suffix;
        this.lexinfo=lexinfo;
    }

    /**
     * Method that returns the suffix contained by the class
     * @return Returns the suffix contained by the class
     */
    public String getSuffix() {
        return suffix;
    }

    public List<String> getLexInfo(){
        return this.lexinfo;
    }

    /**
     * Method that sets a new suffix
     * @param suffix Suffix to be set
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
