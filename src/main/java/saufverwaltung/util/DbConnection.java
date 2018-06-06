package saufverwaltung.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Datenbankanbindung. Funktioniert mit HSQLDB.
 *
 */

public class DbConnection {
    private String url;
    private Connection con = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;

    /**
     * Eröffnet Verbindung zur DB. Diese muss nach der Datenbankoperation wieder mit
     * closeConnection() geschlossen werden!!
     */

    private boolean openConnection() {

        url = "jdbc:hsqldb:file:data/data.db";

        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e1) {
            System.err.println("Couldn't register JDBC driver :(");
            e1.printStackTrace();
            return false;
        }

        try {
            con = DriverManager.getConnection(url, "", "");
        } catch (SQLException e1) {
            System.err.println("Couldn't establish Connection to Database :(");
            e1.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Existiert das Mitglied? Was bedeutet exisitieren in diesem Universum eigentlich?
     *
     */

    public boolean doesMemberExist(String name) {
        openConnection();
        boolean fnd = false;
        name = name.trim();
        try {
            st = con.prepareStatement("SELECT * FROM mitglieder WHERE name=?");
            st.setString(1, name);
            rs = st.executeQuery();
            fnd = rs.next();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        closeConnection();
        return fnd;
    }

    /**
     * Erstellt Mitglied in Datenbank mit spezifiziertem namen und guthaben. Geht mal davon aus,
     * dass kein bereits vorhandener name neu hinzugef�gt wird.
     *
     */
    public void createMember(String name, double startGh) {
        openConnection();
        try {
            st = con.prepareStatement(
                            "INSERT INTO mitglieder(name, guthaben, alk, antalk, show) VALUES (?, ?, 0, 0, true)");
            st.setString(1, name);
            st.setDouble(2, startGh);
            st.executeUpdate();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        closeConnection();
    }

    /**
     * Toggelt das Attribut visible des angebenenen Mitgliedes. Befindet sich der angegebene name
     * nicht in der DB, so passiert nichts.
     * 
     * @param name
     */

    public void toggleVisible(String name) {
        openConnection();
        boolean vis = false;
        try {
            st = con.prepareStatement("SELECT show FROM mitglieder WHERE name = ?");
            st.setString(1, name);
            rs = st.executeQuery();
            rs.next();
            vis = rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            st = con.prepareStatement("UPDATE mitglieder SET show = ? WHERE name = ?");
            st.setBoolean(1, !vis);
            st.setString(2, name);
            st.executeUpdate();

        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        closeConnection();
    }

    /**
     * Geld auf Konto einzahlen.
     * 
     * @param name
     * @param cash Wieviel Geld eingezahlt werden soll (in Euro!)
     */

    public void deposit(String name, double cash) {
        openConnection();

        try {
            st = con.prepareStatement("UPDATE mitglieder SET guthaben = guthaben+? WHERE name = ?");
            st.setDouble(1, cash);
            st.setString(2, name);
            st.executeUpdate();

        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        closeConnection();
    }

    /**
     * L�scht Spieler
     * 
     * @param name
     * @throws SQLException
     */

    public void deleteMember(String name) throws SQLException {
        openConnection();

        st = con.prepareStatement("DELETE FROM mitglieder WHERE name = ?");
        st.setString(1, name);
        st.executeUpdate();

        closeConnection();
    }

    /**
     * Methode zum Eintragen neu Konsumierter Getr�nke
     * 
     * @param name
     * @param alk
     * @param antalk
     * @return
     */

    public void updateStats(String name, int alk, int antalk) {
        openConnection();

        try {
            st = con.prepareStatement("UPDATE mitglieder SET alk = alk+? WHERE name = ?");
            st.setInt(1, alk);
            st.setString(2, name);
            st.executeUpdate();

            st = con.prepareStatement(
                            "UPDATE mitglieder SET guthaben = guthaben-(1.5*?) WHERE name = ?");
            st.setInt(1, alk);
            st.setString(2, name);
            st.executeUpdate();

            st = con.prepareStatement("UPDATE mitglieder SET antalk = antalk+? WHERE name = ?");
            st.setInt(1, antalk);
            st.setString(2, name);
            st.executeUpdate();

            st = con.prepareStatement("UPDATE mitglieder SET guthaben = guthaben-? WHERE name = ?");
            st.setInt(1, antalk);
            st.setString(2, name);
            st.executeUpdate();

        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        closeConnection();
    }

    /**
     * Zum Debugging.
     *
     */
    public void printMembers() {
        openConnection();
        try {
            st = con.prepareStatement("SELECT * FROM mitglieder");
            rs = st.executeQuery();
            System.out.println(String.format("%-10s | %-15s | %-15s | %-15s | %-15s", "name",
                            "guthaben", "alk", "antalk", "show"));
            System.out.println(
                            "===========|=================|=================|=================|================");
            while (rs.next()) {
                String pname = rs.getString(1);
                double guth = rs.getDouble(2);
                int alk = rs.getInt(3);
                int analk = rs.getInt(4);
                boolean show = rs.getBoolean(5);
                System.out.println(String.format("%-10s | %-15s | %-15s | %-15s | %-15s", pname,
                                guth, alk, analk, show));
            }
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        closeConnection();
    }

    public void makeNewMembersTable() {
        openConnection();
        try {
            st = con.prepareStatement(
                            "CREATE TABLE mitglieder (name varchar(255), guthaben double, alk int, antalk int, show boolean)");
            st.execute();
            System.out.println("Created new table \"mitglieder\"");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        closeConnection();
    }

    /**
     * Gesamte "member" Tabelle in Listenform
     * 
     * @return
     * @throws SQLException
     */

    public List<Member> getMembers() throws SQLException {
        openConnection();
        List<Member> retList = new ArrayList<>();

        try {
            st = con.prepareStatement("SELECT * FROM mitglieder");
            rs = st.executeQuery();
            while (rs.next()) {
                String pname = rs.getString(1);
                double guth = rs.getDouble(2);
                int alk = rs.getInt(3);
                int antalk = rs.getInt(4);
                boolean show = rs.getBoolean(5);
                retList.add(new Member(pname, guth, alk, antalk, show));
            }
        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            closeConnection();
        }
        return retList;
    }

    public void renameUser(String oldname, String newname) {
        openConnection();
        try {
            st = con.prepareStatement("UPDATE mitglieder SET name = ? WHERE name = ?");
            st.setString(1, newname);
            st.setString(2, oldname);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    /**
     * Schlie�t die verbindung zur DB.
     */

    private boolean closeConnection() {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (con != null) {
                con.close();
                con = null;
            }
            if (st != null) {
                st.close();
                st = null;
            }
        } catch (SQLException e1) {
            System.err.println("Error closing resscources.");
            e1.printStackTrace();
            return false;
        }
        return true;
    }

    public void testPath() {
        System.out.println(getClass().getResource("..//strichliste.txt"));
    }

}
