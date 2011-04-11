package dictools.guessparadigm.paradigms;

/**
 * Class which contains a string which is used as a suffix fo a word or set of
 * words.
 * @author Miquel Esplà i Gomis
 */
public class Suffix {
    /** The suffix */
    private String suffix;

    /**
     * Overloaded constructor of the class
     * @param suffix New suffix
     */
    public Suffix(String suffix){
        this.suffix=suffix;
    }

    /**
     * Method that returns the suffix contained by the class
     * @return Returns the suffix contained by the class
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Method that sets a new suffix
     * @param suffix Suffix to be set
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
