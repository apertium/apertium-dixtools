/*
 * Copyright 2010 Pasquale Minervini <p.minervini@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package misc.kvtml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DataSet {

	public static Set<String> getWords(String path) throws Exception {
		Set<String> ret = new TreeSet<String>();

		File file = new File(path);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

        db.setEntityResolver(new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId)
                    throws SAXException, IOException {
                //System.out.println("Ignoring " + publicId + ", " + systemId);
                return new InputSource(new StringReader(""));
            }
        });

		try {

			Document doc = db.parse(file);

			doc.getDocumentElement().normalize();

			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			javax.xml.xpath.XPathExpression expr = xpath.compile("//kvtml/e/o/text()");

			Object result = expr.evaluate(doc, XPathConstants.NODESET);

			NodeList nodes = (NodeList) result;

			for (int i = 0; i < nodes.getLength(); ++i) {
				ret.add(nodes.item(i).getNodeValue());
			}

		} catch (Exception e) {
			//e.printStackTrace();
		}

		return ret;
	}

	public static List<Translation> getTranslatedWords(String path) throws Exception {
		List<Translation> ret = new LinkedList<Translation>();

		File file = new File(path);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

        db.setEntityResolver(new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId)
                    throws SAXException, IOException {
                //System.out.println("Ignoring " + publicId + ", " + systemId);
                return new InputSource(new StringReader(""));
            }
        });

		try {

			Document doc = db.parse(file);

			doc.getDocumentElement().normalize();

			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			javax.xml.xpath.XPathExpression expr = xpath.compile("//kvtml/e");

			Object result = expr.evaluate(doc, XPathConstants.NODESET);

			NodeList nodes = (NodeList) result;

			for (int i = 0; i < nodes.getLength(); ++i) {
				Translation t = new Translation();

				Node e = nodes.item(i);
				NodeList ot = e.getChildNodes();

				for (int j = 0; j < ot.getLength(); ++j) {
					Node oort = ot.item(j);

					if (oort.getNodeName().equals("o")) {
						t.original = oort.getNodeValue();
					} else if (oort.getNodeName().equals("t")) {
						t.translation = oort.getNodeValue();
					}
				}

				ret.add(t);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public static boolean isInTranslated(String w, List<Translation> t) {
		boolean ret = false;

		for (Translation tr : t) {
			if (tr.original.equals(w)) {
				ret = true;
			}
		}

		return ret;
	}

	public static void printTranslated(List<Translation> t) {
		for (Translation tr : t) {
			System.out.println(tr.original + "\t\t\t" + tr.translation);
		}
	}

}

