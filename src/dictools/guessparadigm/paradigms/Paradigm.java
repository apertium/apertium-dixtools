package dictools.guessparadigm.paradigms;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.DixElement;
import dics.elements.dtd.E;
import dics.elements.dtd.P;
import dics.elements.dtd.Par;
import dics.elements.dtd.Pardef;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Class which represents a paradigm from a dictionary of Apertium
 * @author Miquel Esplà i Gomis
 */
public class Paradigm {
    /** Name of the paradigm */
    private String name;

    /** List of suffixes generated by the paradigm */
    private Set<Suffix> suffixes;

    /**
     * Overloaded constructor of the class
     * @param par Pardef object from which the paradigm is read
     * @param dic Dictionary from which the paradigm is read
     */
    public Paradigm(Pardef par, Dictionary dic){
        this.name=par.name;
        suffixes=new LinkedHashSet<Suffix>();
        Set<StringBuilder> lstmp=new LinkedHashSet<StringBuilder>();
        lstmp.add(new StringBuilder());
        Set<String> sufftmp=BuildSuffixes(par.elements,lstmp,dic);
        for(String s: sufftmp){
            Suffix suf=new Suffix(s);
            this.suffixes.add(suf);
        }
    }

    /**
     * Method that generates all the possible suffixes
     * @param elements List of elements from which the suffixes are read
     * @param currentLexicalForms List of paradigms pre-generated
     * @param dic Dictionary from which the paradigm is read
     * @return Returns the list of suffixes generated by the paradigm
     */
    private Set<String> BuildSuffixes(List<E> elements, Set<StringBuilder> currentLexicalForms, Dictionary dic)
    {
         List<String> localList=new LinkedList<String>();
         for(E element: elements)
         {
             Set<StringBuilder> listgeneratedByElement=new LinkedHashSet<StringBuilder>();
             for (DixElement e: element.children)
             {
                 if (e instanceof P){
                     if(listgeneratedByElement.isEmpty())
                         listgeneratedByElement.add(new StringBuilder(""));
                     for(StringBuilder b: listgeneratedByElement)
                        b.append(((P)e).l.getValueNoTags());
                 }
                 else if (e instanceof Par)
                 {
                     List<E> parElements=dic.pardefs.getParadigmDefinition(((Par)e).name).elements;
                     Set<String> resultList=BuildSuffixes(parElements, listgeneratedByElement, dic);
                     listgeneratedByElement.clear();
                     for(String r: resultList)
                         listgeneratedByElement.add(new StringBuilder(r));
                 }
             }
             for(StringBuilder b: listgeneratedByElement)
                 localList.add(b.toString());
         }

         //Combine lists
         Set<String> finalList = new LinkedHashSet<String>();
         for(StringBuilder lexHead: currentLexicalForms)
             for(String lexTail: localList)
                 finalList.add(lexHead+lexTail);

         return finalList;
    }

    /**
     * Method that returns the name of the paradigm
     * @return Returns the name of the paradigm
     */
    public String getName() {
        return name;
    }

    /**
     * Method that returns the list of suffixes generated by the paradigm
     * @return Returns the list of suffixes generated by the paradigm
     */
    public Set<Suffix> getSuffixes(){
        return this.suffixes;
    }
}