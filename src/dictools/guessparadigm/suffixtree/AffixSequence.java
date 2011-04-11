package dictools.guessparadigm.suffixtree;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a sequence of affixes which compones a complete word.
 * @author miquel
 */
public class AffixSequence {
    private StringBuilder sb;

    private List<Pair<Integer,String>> suffixes;

    public void SetSuffixes(List<Pair<Integer,String>> li){
        suffixes=li;
    }

    public AffixSequence(){
        sb=new StringBuilder("");
        suffixes=new LinkedList<Pair<Integer,String>>();
    }

    public AffixSequence(AffixSequence as){
        sb=new StringBuilder(as.sb);
        suffixes=new LinkedList<Pair<Integer,String>>(as.suffixes);
    }

    public void addSteam(String steam){
        sb.append(steam);
    }

    public void addAffix(String affix, String paradigmname){
        suffixes.add(new Pair(sb.toString().length(),paradigmname));
        sb.append(affix);
    }

    public String getSequence(){
        return sb.toString();
    }

    public List<Pair<Integer,String>> getSuffixStartingPosition(){
        return suffixes;
    }

    public void addAffixSequence(AffixSequence as){
        for(Pair<Integer,String> newpoints: as.suffixes){
            suffixes.add(new Pair<Integer,String>((newpoints.getFirst()+this.sb.toString().length()),newpoints.getSecond()));
        }
        sb.append(as.sb);
    }
}
