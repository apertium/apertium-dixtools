/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dictools.utils;


import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.Section;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.w3c.dom.Node;

/**
 * Source: Xerces DOM parser
http://old.nabble.com/Getting-line-number-while-traversing-DOM-using-Xerces-tc8045642.html#a8046291
http://kickjava.com/src/dom/DOMAddLines.java.htm
 *
 * This code illustrates:
 * - How to use the SAX Locator to return row position ( line number of DOM element).
 *
 *
 * @author Jacob Nordfalk, Xerces DOM parser
 */

public class KeepTrackOfLocationDOMParser extends DOMParser  {

   private XMLLocator locator;

   public KeepTrackOfLocationDOMParser() {
    try {
      setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
      //fNodeExpansion = FULL; // faster than: this.setFeature("http://apache.org/xml/features/defer-node-expansion", false);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
   }


  @Override
   public void startDocument(XMLLocator locator, String encoding,
                             NamespaceContext namespaceContext, Augmentations augs) {
     super.startDocument(locator, encoding, namespaceContext, augs);
     this.locator = locator;
     recordLocation(locator);
  }


  @Override
   public void startElement(QName elementQName, XMLAttributes attrList, Augmentations augs) {
      super.startElement(elementQName, attrList, augs);
      recordLocation(locator);
   }

  public final Map<Node,Integer> lineNumbers = new HashMap<Node,Integer>(10000);
  private int lastLineNo = -2;
  private Integer lastLineNoInteger;

  private void recordLocation(XMLLocator locator) {
    if (locator==null) return;
    try {
      Node node=(Node) this.getProperty("http://apache.org/xml/properties/dom/current-element-node");
      //System.err.println("node = "+node);
      //System.err.println("locator = "+locator.getCharacterOffset()+" ln:"+locator.getLineNumber());
      
      // cache and reuse instances so we don't litter memory exceedingly much
			int lineNo = locator.getLineNumber();
      if (lastLineNo != lineNo) lastLineNoInteger = lastLineNo = lineNo;
      lineNumbers.put(node, lastLineNoInteger);
      
    } catch (Exception ex) {
      ex.printStackTrace();
      locator=null; // swich off recording of location after 1st exception
    }
  }


   public static void main(String argv[]) throws Exception {
     /*
      KeepTrackOfLocationDOMParser parser = new KeepTrackOfLocationDOMParser();
      parser.parse("test/sample.eo-en.dix" );
      parser.getDocument();
      System.err.println("domAddExample.lineNumbers = " + parser.lineNumbers);
*/
      Dictionary dic=new DictionaryReader("test/sample.eo-en.dix").readDic();
      for (Section s : dic.sections) for (E e : s.elements)
        System.err.println("Line: " + e.lineNo+": "+e);

   } 


}