/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dictools.guessparadigm.questions;

import dictools.guessparadigm.paradigms.SurfaceFormsSet;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author miquel
 */
public class Candidate {
    
    private double score;

    private SurfaceFormsSet sfs;

    private Stack<String> rejectedforms;

    public Candidate(double score, SurfaceFormsSet sfs) {
        this.score = score;
        this.sfs = sfs;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public SurfaceFormsSet getSfs() {
        return sfs;
    }

    public void setSfs(SurfaceFormsSet sfs) {
        this.sfs = sfs;
    }

    public void Reject(String form){
        sfs.getSurfaceforms().remove(form);
        rejectedforms.add(form);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.score) ^ (Double.doubleToLongBits(this.score) >>> 32));
        hash = 97 * hash + (this.sfs != null ? this.sfs.hashCode() : 0);
        hash = 97 * hash + (this.rejectedforms != null ? this.rejectedforms.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o){
        boolean exit;
        if(o instanceof Candidate){
            Candidate c=(Candidate)o;
            Set<String> forms1=this.sfs.getSurfaceforms();
            Set<String> forms2=c.sfs.getSurfaceforms();
            if(forms1.size()==forms2.size()){
                exit=true;
                for(String s: forms1){
                    if(!forms2.contains(s)){
                        exit=false;
                        break;
                    }
                }
            }
            else{
                exit=false;
            }
            return exit;
        }
        else
            return false;
    }
}
