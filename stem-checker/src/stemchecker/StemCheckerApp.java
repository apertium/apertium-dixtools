/*
 * StemCheckerApp.java
 */

package stemchecker;

import dictools.utils.DictionaryReader;
import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.Section;
import java.io.File;
import java.util.ArrayList;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class StemCheckerApp extends SingleFrameApplication {

    private File dictFile;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new StemCheckerView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of StemCheckerApp
     */
    public static StemCheckerApp getApplication() {
        return Application.getInstance(StemCheckerApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(StemCheckerApp.class, args);
    }

    /**
     * Assigns a new File object for the dictionary file.
     * @param newDictFile
     */
    public void setDictFile(File newDictFile) {
        dictFile = newDictFile;
    }

    /**
     * Gets the current dictionary file.
     * @return The current dictionary filename as a String.
     */
    public File getDictFile() {
        return dictFile;
    }

    /**
     * Loads the file, parses it using dixtools, and returns a list of strings
     * that represent the lines where it found errors in the stems.
     */
    public ArrayList<String> checkDictStems() {
        ArrayList<String> badStemList = new ArrayList<String>();
        DictionaryReader dictRead = new DictionaryReader(dictFile.getPath());
        Dictionary dict = dictRead.readDic();
        for (Section section : dict.sections) {
            for (E element : section.elements) {
                if(element.getMainParadigmName().contains("/")) {
                    if(element.lemma.equals(element.getFirstI().getValueNoTags())) {
                        badStemList.add(element.toString());
                    }
                }
            }
        }
        return badStemList;
    }
}
