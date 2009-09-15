/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Author: Enrique Benimeli Bofarull
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
package misc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

import dics.elements.dtd.Dictionary;
import dics.elements.dtd.E;
import dics.elements.dtd.L;
import dics.elements.dtd.P;
import dics.elements.dtd.R;
import dics.elements.dtd.S;
import dics.elements.dtd.Section;
import dics.elements.dtd.TextElement;
import dictools.AbstractDictTool;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class GetBilOmegawiki  extends AbstractDictTool {

    
    private String dbName;
    
    private String user;
    
    private String password;
    
    private Connection con;
    
    private String sl = "es";
    
    private String tl = "eu";
    
    private String outFileName;

    
    public GetBilOmegawiki(String sl, String tl) {
        if (sl.compareTo(tl) > 0) {
            setSl(tl);
            setTl(sl);
        } else {
            setSl(sl);
            setTl(tl);
        }

        msg.err("source lang: " + getSl());
        msg.err("target lang: " + getTl());

        connectDB("omegawiki", "omegawiki", "");
    }

    
    private void connectDB(String db, String u, String p) {
        setDbName(db);
        setUser(u);
        setPassword(p);

        try {
            Class.forName("org.gjt.mm.mysql.Driver").newInstance();
            String url = "jdbc:mysql://localhost:3306/" + getDbName();
            Properties props = new Properties();
            props.setProperty("user", getUser());
            props.setProperty("password", getPassword());
            con = DriverManager.getConnection(url, props);
            msg.err("Database connection established");
        } catch (ClassNotFoundException cnfe) {
            msg.err("Error: could not find MySQL driver (org.gjt.mm.mysql.Driver)");
            System.exit(-1);
        } catch (SQLException sqle) {
            msg.err("Error SQL!");
            sqle.printStackTrace();
            System.exit(-1);
        } catch (Exception e) {
            msg.err("Error!");
            e.printStackTrace();
            System.exit(-1);
        } finally {
        }

    }

    
    public void printDictionary() {
        Dictionary dic = new Dictionary();
        Section section = new Section();
        dic.addSection(section);

        try {
            Statement stmt = con.createStatement();

            // Old query for retrieving translations
	    /*
             * String query = "select t.defined_meaning_id as id, e.spelling
             * as expression, l.wikimedia_key as lang "; query += "from
             * uw_expression_ns as e, uw_syntrans as t, language as l ";
             * query += "where t.expression_id=e.expression_id and
             * l.language_id=e.language_id "; query += "and
             * (l.wikimedia_key='" + sl + "' or l.wikimedia_key='" + tl +
             * "') "; query += "order by t.defined_meaning_id ASC,
             * l.wikimedia_key ASC";
             */

            String query = "select t.defined_meaning_id as id, e.spelling as expression, l.wikimedia_key as lang, oav.option_id as pos ";
            query += "from uw_defined_meaning as dm, uw_expression_ns as e, uw_syntrans as t, language as l, uw_option_attribute_values as oav, uw_option_attribute_options as oao ";
            query += "where t.expression_id=e.expression_id and l.language_id=e.language_id and ";
            query += "(l.wikimedia_key='es' or l.wikimedia_key='pt') and ";
            query += "dm.defined_meaning_id = t.defined_meaning_id and ";
            query += "t.syntrans_sid = oav.object_id ";
            query += "order by t.defined_meaning_id ASC, l.wikimedia_key ASC";

            ResultSet rs = stmt.executeQuery(query);

            String query2 = "select option_id,spelling,l.wikimedia_key from uw_option_attribute_options,uw_defined_meaning,uw_expression_ns, language as l where attribute_id = '409106' and  uw_option_attribute_options.language_id = l.language_id and  (l.wikimedia_key='es' or l.wikimedia_key='pt') and uw_defined_meaning.defined_meaning_id = option_mid and uw_expression_ns.expression_id = uw_defined_meaning.expression_id";
            Statement stmt2 = con.createStatement();
            ResultSet rs2 = stmt2.executeQuery(query2);

            HashMap<String, String> posInfo = new HashMap<String, String>();

            while (rs2.next()) {
                Integer i = rs2.getInt("option_id");
                String pos = rs2.getString("spelling");
                String lang = rs2.getString("wikimedia_key");
                System.err.println("(" + i + ") " + pos + " (" + lang + ")");
                if (pos.equals("adjective")) {
                    pos = "adj";
                }

                if (pos.equals("verb")) {
                    pos = "vblex";
                }

                if (pos.equals("adverb")) {
                    pos = "adv";
                }

                if (pos.equals("noun")) {
                    pos = "n";
                }

                if (pos.equals("preposition")) {
                    pos = "pr";
                }

                if (pos.equals("pronoun")) {
                    pos = "prn";
                }

                posInfo.put(i.toString(), pos);
            }

            String sltext = "";
            String tltext = "";

            String slPoS = "";
            String tlPoS = "";

            int c = 0;
            while (rs.next()) {
                Integer i = rs.getInt("id");
                String expr = rs.getString("expression");
                String lang = rs.getString("lang");
                Integer pos = rs.getInt("pos");

                String posTag = posInfo.get(pos.toString());

                if (lang.equals(sl)) {
                    sltext = expr;
                    slPoS = posTag;
                    c = i;
                }
                if (lang.equals(tl) && (i == c)) {
                    tltext = expr;
                    tlPoS = posTag;
                    sltext = sltext.replaceAll(" ", "<b/>");
                    tltext = tltext.replaceAll(" ", "<b/>");
                    E e = buildEElement(sltext, tltext, slPoS, tlPoS);
                    section.addEElement(e);
                }
            }
        } catch (SQLException sqle) {
            System.err.println("Error SQL!");
            sqle.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error access!");
        } finally {
            if (con != null) {
                try {
                    con.close();
                    System.err.println("Database connection terminated");
                } catch (Exception e) {
                /* ignore close errors */
                }
            }
        }
        // dic.printXML("apertium-" + sl + "-" + tl + ".dix");
        dic.printXML(getOutFileName(),getOpt());
    }

    /**
     * 
     * @param sl
     * @param tl
     * @return
     */
    private E buildEElement(String sl, String tl,
            String slPoS, String tlPoS) {
        E e = new E();
        L l = new L();
        TextElement lt = new TextElement(sl);
        l.addChild(lt);
        S sL = new S(slPoS);
        l.addChild(sL);

        R r = new R();
        TextElement rt = new TextElement(tl);
        r.addChild(rt);
        S sR = new S(tlPoS);
        r.addChild(sR);

        P p = new P();
        p.l = (l);
        p.r = (r);
        e.addChild(p);
        return e;
    }

    /**
     * @return the sl
     */
    public String getSl() {
        return sl;
    }

    /**
     * @param sl
     *                the sl to set
     */
    public void setSl(String sl) {
        this.sl = sl;
    }

    /**
     * @return the dbName
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @param dbName
     *                the dbName to set
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user
     *                the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *                the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the tl
     */
    public String getTl() {
        return tl;
    }

    /**
     * @param tl
     *                the tl to set
     */
    public void setTl(String tl) {
        this.tl = tl;
    }

    /**
     * @return the outFileName
     */
    public String getOutFileName() {
        return outFileName;
    }

    /**
     * @param outFileName
     *                the outFileName to set
     */
    public void setOutFileName(String outFileName) {
        this.outFileName = outFileName;
    }
}
