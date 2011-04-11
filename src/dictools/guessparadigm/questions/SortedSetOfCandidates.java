/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dictools.guessparadigm.questions;

import dictools.guessparadigm.paradigms.SurfaceFormsSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

/**
 *
 * @author miquel
 */
public class SortedSetOfCandidates {
    public class CandidateComparator implements Comparator{
        @Override
        public int compare(Object o1, Object o2){
            Candidate c1=(Candidate)o1;
            Candidate c2=(Candidate)o2;

            if(c1.getScore()>c2.getScore())
                return -1;
            else if(c1.getScore()<c2.getScore())
                return 1;
            else{
                if(c1.getSfs().getSurfaceforms().size()>c2.getSfs().getSurfaceforms().size())
                    return -1;
                else if(c1.getSfs().getSurfaceforms().size()<c2.getSfs().getSurfaceforms().size())
                    return 1;
                else
                    return 0;
            }
        }
    }

    public class CandidateComparatorBySize implements Comparator{
        @Override
        public int compare(Object o1, Object o2){
            Candidate c1=(Candidate)o1;
            Candidate c2=(Candidate)o2;
            if(c1.getSfs().getSurfaceforms().size()>c2.getSfs().getSurfaceforms().size())
                return -1;
            else if(c1.getSfs().getSurfaceforms().size()<c2.getSfs().getSurfaceforms().size())
                return 1;
            else
                return 0;
        }
    }

    class Operation{
        boolean rejected;

        String surfaceform;

        Set<Candidate> removedcandidates;

        public boolean isRejected() {
            return rejected;
        }

        public Set<Candidate> getRemovedcandidates() {
            return removedcandidates;
        }

        public String getSurfaceform() {
            return surfaceform;
        }

        public Operation(boolean rejected, String surfaceform){
            this.rejected=rejected;
            this.surfaceform=surfaceform;
            this.removedcandidates=new LinkedHashSet<Candidate>();
        }

        public void addCandidate(Candidate c){
            this.removedcandidates.add(c);
        }
    }

    List<Candidate> candidateslist;

    Stack<Operation> operations;

    Set<Candidate> possiblesolutions;

    public SortedSetOfCandidates(){
        candidateslist=new LinkedList<Candidate>();
        operations=new Stack<Operation>();
    }

    public void addCandidate(double score, SurfaceFormsSet sfs){
        candidateslist.add(new Candidate(score,sfs));
        Collections.sort(candidateslist,new CandidateComparator());
    }

    public List<Candidate> getCandidates(){
        return this.candidateslist;
    }

    public String getFirstCandidateBestSurfaceForm(){
        Map<String,Integer> seenforms=new HashMap<String,Integer>();
        if(this.candidateslist==null || this.candidateslist.isEmpty()){
            return null;
        }
        else{
            Candidate firstcandidate=this.candidateslist.get(0);
            for(int index=1; index<this.candidateslist.size();index++){
                Candidate currentcandidate=this.candidateslist.get(index);
                for(String form: firstcandidate.getSfs().getSurfaceforms()){
                    if(currentcandidate.getSfs().getSurfaceforms().contains(form)){
                        Integer i=seenforms.get(form);
                        if(i==null)
                            seenforms.put(form, 1);
                        else
                            seenforms.put(form, i+1);
                    }
                    else if(!seenforms.containsKey(form))
                        seenforms.put(form, 0);
                }
            }
            if(this.possiblesolutions!=null){
                Candidate currentcandidate = this.possiblesolutions.iterator().next();
                for(String form: currentcandidate.getSfs().getSurfaceforms())
                    seenforms.remove(form);
            }

            int min=0;
            if(seenforms.size()>0){
                SortedSet<Integer> ss;
                if(this.possiblesolutions==null)
                    ss=new TreeSet<Integer>(seenforms.values());
                else{
                    ss=new TreeSet<Integer>(Collections.reverseOrder());
                    ss.addAll(seenforms.values());
                }
                //ss=new TreeSet<Integer>();
                min=ss.first();
            }
            System.out.println("Lowest value "+min);
            if(this.possiblesolutions==null){
                if(min==this.candidateslist.size()-1){
                    this.possiblesolutions=new LinkedHashSet<Candidate>();
                    int nsurfaceforms=firstcandidate.getSfs().getSurfaceforms().size();
                    for(Candidate c: this.candidateslist){
                        if(nsurfaceforms==c.getSfs().getSurfaceforms().size()){
                            this.possiblesolutions.add(c);
                        }
                    }
                    for(Candidate c: this.possiblesolutions){
                        this.candidateslist.remove(c);
                    }
                    return getFirstCandidateBestSurfaceForm();
                }
                else{
                    String exit=null;
                    double higherscore=-1;
                    for(Entry<String,Integer> entry: seenforms.entrySet()){
                        double score=firstcandidate.getSfs().getSurfaceFormScore(entry.getKey());
                        if(entry.getValue()==min && score>higherscore){
                            exit=entry.getKey();
                            higherscore=score;
                        }
                    }
                    return exit;
                }
            }
            else{
                String exit=null;
                double higherscore=-1;
                for(Entry<String,Integer> entry: seenforms.entrySet()){
                    double score=firstcandidate.getSfs().getSurfaceFormScore(entry.getKey());
                    if(entry.getValue()==min && score>higherscore){
                        exit=entry.getKey();
                        higherscore=score;
                    }
                }
                return exit;
            }
        }
    }

    public void RejectForm(String form){
        //this.candidateslist.get(0).Reject(form);
        Operation o=new Operation(true, form);
        for(Candidate c:this.candidateslist){
            if(c.getSfs().getSurfaceforms().contains(form)){
                o.addCandidate(c);
            }
        }
        for(Candidate c: o.getRemovedcandidates())
            this.candidateslist.remove(c);
        this.operations.add(o);
    }

    public void AcceptForm(String form){
        Operation o=new Operation(false, form);
        for(Candidate c:this.candidateslist){
            if(!c.getSfs().getSurfaceforms().contains(form)){
                o.addCandidate(c);
            }
        }
        for(Candidate c: o.getRemovedcandidates())
            this.candidateslist.remove(c);
        if(this.possiblesolutions!=null){
            for(Candidate c:this.possiblesolutions){
                if(!c.getSfs().getSurfaceforms().contains(form)){
                    o.addCandidate(c);
                }
            }
            for(Candidate c: o.getRemovedcandidates())
                this.possiblesolutions.remove(c);
            if(this.possiblesolutions.isEmpty())
                this.possiblesolutions=null;
        }
        this.operations.add(o);
    }

    public void GoBack(){
        Operation lastoperation=this.operations.pop();
        this.candidateslist.addAll(lastoperation.removedcandidates);
        Collections.sort(candidateslist,new CandidateComparator());
    }

    public Set<Candidate> getPossibleSolutions(){
        return this.possiblesolutions;
    }

    public int GetNumberOfDifferentCandidates(){
        if(this.candidateslist!=null){
            Set<Candidate> sc=new LinkedHashSet<Candidate>();
            for(Candidate c: this.candidateslist){
                boolean found=false;
                for(Candidate t: sc){
                    if(t.equals(c)){
                        found=true;
                        break;
                    }
                }
                if(!found)
                    sc.add(c);
            }
            return sc.size();
        }
        else{
            return 0;
        }
    }
}
