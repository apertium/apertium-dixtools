/*
 * Copyright 2011 European Commission
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package dictools.columnar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.ArrayList;
import dics.elements.dtd.E;
import dics.elements.dtd.P;
import dics.elements.dtd.R;
import dics.elements.dtd.L;
import dics.elements.dtd.S;
import static org.junit.Assert.*;

/**
 *
 * @author jimregan
 */
public class ParaConfigReaderTest {

    ArrayList<ParadigmPair> pairs;
    E e, e2;
    ParaConfig pcTest;
    ArrayList<E> test1;
    ArrayList<E> test2;

    public ParaConfigReaderTest() {
        pairs = new ArrayList<ParadigmPair>();
        e = new E();
        e2 = new E();
        pcTest = new ParaConfig();
        test1 = new ArrayList<E>();
        test2 = new ArrayList<E>();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        L l = new L();
        l.children.add(new S("n"));
        R r = new R();
        r.children.add(new S("n"));
        r.children.add(new S("m"));
        P p = new P();
        p.l = l;
        p.r = r;
        e.children.add(p);

        L l2 = new L();
        l2.children.add(new S("n"));
        R r2 = new R();
        r2.children.add(new S("n"));
        r2.children.add(new S("f"));
        P p2 = new P();
        p2.l = l2;
        p2.r = r2;
        e2.children.add(p2);

        test1.add(e);
        test2.add(e2);

        pairs.add(new ParadigmPair("house__n", "alb/atros__n", test1));
        pairs.add(new ParadigmPair("house__n", "t/oner__n", test1));
        pairs.add(new ParadigmPair("house__n", "m/áster__n", test1));
        pairs.add(new ParadigmPair("bab/y__n", "alb/atros__n", test1));
        pairs.add(new ParadigmPair("bab/y__n", "t/oner__n", test1));
        pairs.add(new ParadigmPair("bab/y__n", "m/áster__n", test1));

        pcTest.addAll(pairs);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of readParaConfig method, of class ParaConfigReader.
     */
    @Test
    public void testReadParaConfig() {
        System.out.println("readParaConfig");
        ParaConfigReader instance = new ParaConfigReader("regression_test_data/para_config/mapping-en-es.xml");
        ParaConfig pc = instance.readParaConfig();

        boolean check = pcTest.isSameAs(pc);
        assertEquals(check, false);

        pcTest.add(new ParadigmPair("house__n", "abeja__n", test2));
        pcTest.add(new ParadigmPair("bab/y__n", "abeja__n", test2));

        check = pcTest.isSameAs(pc);
        assertEquals(check, true);
    }

}