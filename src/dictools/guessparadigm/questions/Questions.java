/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dictools.guessparadigm.questions;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author miquel
 */
public class Questions {
    static public void AskQuestions(SortedSetOfCandidates candidates, BufferedWriter pWriter){
        int numberofquestions=0;
        long start = System.currentTimeMillis();

        if(candidates==null){
            System.out.println("No possible paradigm found");
            try {
                pWriter.append("\t\t0");
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(-1);
            }
        }
        else{
            System.out.println(candidates.getCandidates().size()+" possible paradigms.");
            int counter=1;
            String formtoask;
            while((formtoask=candidates.getFirstCandidateBestSurfaceForm())!=null){
                try {
                    boolean incorrect=true;
                    numberofquestions++;
                    System.out.print("Is the word '"+formtoask+"' possible? (y=yes, n=no, b=go back): ");
                    while(incorrect){
                        char answer=(char)System.in.read();
                        switch(answer){
                            case 'y':
                                candidates.AcceptForm(formtoask);
                                System.out.println(formtoask+" accepted ("+candidates.getCandidates().size()+" remaining)");
                                incorrect=false;
                            break;
                            case 'n':
                                candidates.RejectForm(formtoask);
                                System.out.println(formtoask+" discarded ("+candidates.getCandidates().size()+" remaining)");
                                incorrect=false;
                            break;
                            case 'b':
                                counter--;
                                numberofquestions--;
                                candidates.GoBack();
                                incorrect=false;
                            break;
                            default:
                                System.out.print("You have to type an answer y (yes), n (no) or b (go back):");
                            break;
                        }
                        answer=(char)System.in.read();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                counter++;
            }
            try {
                pWriter.append("\t");
                for(Candidate c: candidates.getPossibleSolutions()){
                    System.out.println("Chosen steam-paradigm: "+c.getSfs().getSteam()+"-"+c.getSfs().getParadigm().getName());
                    pWriter.append(c.getSfs().getSteam());
                    pWriter.append("|");
                    pWriter.append(c.getSfs().getParadigm().getName());
                    pWriter.append(";");
                }
                pWriter.append("\t");
                pWriter.append(Integer.toString(numberofquestions));
                pWriter.append("\t");
                long elapsed = System.currentTimeMillis() - start;
                pWriter.append(Long.toString(elapsed));
                pWriter.append("\t");
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(-1);
            }
        }
    }
}
