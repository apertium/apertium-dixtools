/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stemchecker;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Stephen
 */
public class DictFileFilter extends FileFilter {
    private static String dictSuffix = ".dix";

    /**
     * Accepts a file and returns whether or not this filter accepts the file.
     * Checks the end of the string for the dictSuffix and returns true if it
     * finds it.
     * @param f
     * The file to be checked.
     * @return true if accepted, false if not
     */
    @Override
    public boolean accept(File f) {
        /*
         * Without this, the user cannot navigate the filesystem, and is stuck
         * in the directory the dialog opens up at.
         */
        if (f.isDirectory()) {
            return true;
        }

        String filename = f.getName();
        if(filename.endsWith(dictSuffix)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getDescription() {
        return "Apertium dictionary XML files (*" + dictSuffix + ")";
    }

}
