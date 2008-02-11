/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package misc.esca;

import dics.elements.dtd.DictionaryElement;
import dics.elements.dtd.EElement;
import dics.elements.dtd.PardefElement;
import dics.elements.dtd.SElement;
import dictools.DictionaryReader;
import java.util.HashMap;

/**
 *
 * @author ebenimeli
 */
public class AddSameGender {

   /**
    * 
    */
   private String[] arguments;
   /**
    * 
    */
   private String morphDic;
   /**
    * 
    */
   private String bilDic;
   /**
    * 
    */
   private String outFileName;
   /**
    * 
    */
   private HashMap<String, PardefElement> pardefsNouns;
   /**
    * 
    */
   private HashMap<String, PardefElement> mfPardefsAdjs;

   /**
    * 
    * @param morphDic
    * @param bilDic
    */
   public AddSameGender(final String morphDic, final String bilDic) {
      this.morphDic = morphDic;
      this.bilDic = bilDic;

   }

   /**
    * 
    */
   public final void processArguments() {
      morphDic = arguments[1];
      bilDic = arguments[2];
   }

   /**
    * 
    * @param outFileName
    */
   public final void setOutFileName(final String outFileName) {
      this.outFileName = outFileName;
   }

   /**
    * 
    */
   public final void addSameGender() {

      DictionaryReader morphReader = new DictionaryReader(this.morphDic);
      DictionaryElement morph = morphReader.readDic();

      HashMap<String, String> parNameGender = new HashMap<String, String>();

      for (PardefElement pfe : morph.getPardefsElement().getPardefElements()) {
         if (pfe.hasCategory("n")) {
            if ((pfe.contains("m") && pfe.contains("f"))) {
               //parNameGender.put(pfe.getName(), "GD");
            } else {
               if (pfe.contains("mf")) {
                  parNameGender.put(pfe.getName(), "mf");
               }
               if (pfe.contains("m")) {
                  parNameGender.put(pfe.getName(), "m");
               }
               if (pfe.contains("f")) {
                  parNameGender.put(pfe.getName(), "f");
               }
            }
         }
      }

      HashMap<String, String> lemmaParName = new HashMap<String, String>();
      for (EElement ee : morph.getAllEntries()) {
         String lemma = ee.getLemma();
         String parName = ee.getParadigmValue();
         lemmaParName.put(lemma, parName);
      }

      DictionaryReader bilReader = new DictionaryReader(this.bilDic);
      DictionaryElement bil = bilReader.readDic();

      for (EElement ee : bil.getAllEntries()) {
         if (ee.is("L", "n")) {
            String value = ee.getLeft().getValueNoTags();
            if (ee.getLeft().contains("GD") || ee.getLeft().contains("m") || ee.getLeft().contains("f") || ee.getLeft().contains("mf")) {

            } else {
               String parValue = lemmaParName.get(value);
               if (parValue != null) {
                  //System.out.println("parValue: " +  parValue);
                  String genderValue = parNameGender.get(parValue);
                  if (genderValue != null) {
                     System.out.println(value + " is '" + genderValue + "'");
                     ee.getChildren("L").add(new SElement(genderValue));
                  }
               }
            }
         }
      }
      
      bil.printXML("dics/apertium-es-ca.es-ca-with-gender.dix");

   }
}
