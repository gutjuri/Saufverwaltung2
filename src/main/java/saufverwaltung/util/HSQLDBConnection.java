package saufverwaltung.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of a database connection which uses HSQLDB.
 * 
 * @author Juri Dispan
 *
 */
public class HSQLDBConnection implements DbConnection {
    private final String tableName = "members";

    private String url;
    private Connection con = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;

    @Override
    public boolean doesMemberExist(String name) {
        openConnection();
        boolean fnd = false;
        name = name.trim();
        try {
            st = con.prepareStatement("SELECT * FROM " + tableName + " WHERE name=?");
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

    @Override
    public void createMember(String name, int startCredits) {
        openConnection();
        try {
            st = con.prepareStatement("INSERT INTO" + tableName
                            + "(name, guthaben, alk, antalk, show) VALUES (?, ?, 0, 0, true)");
            st.setString(1, name);
            st.setDouble(2, startCredits);
            st.executeUpdate();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        closeConnection();

    }

    @Override
    public void toggleVisible(String name) {
        openConnection();
        boolean vis = false;
        try {
            st = con.prepareStatement("SELECT show FROM " + tableName + " WHERE name = ?");
            st.setString(1, name);
            rs = st.executeQuery();
            rs.next();
            vis = rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            st = con.prepareStatement("UPDATE " + tableName + " SET show = ? WHERE name = ?");
            st.setBoolean(1, !vis);
            st.setString(2, name);
            st.executeUpdate();

        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        closeConnection();

    }

    @Override
    public void deposit(String name, int cash) {
        openConnection();

        try {
            st = con.prepareStatement(
                            "UPDATE " + tableName + " SET guthaben = guthaben+? WHERE name = ?");
            st.setDouble(1, cash);
            st.setString(2, name);
            st.executeUpdate();

        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        closeConnection();

    }

    @Override
    public void deleteMember(String name) {
        openConnection();
        try {
            st = con.prepareStatement("DELETE FROM " + tableName + " WHERE name = ?");
            st.setString(1, name);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        closeConnection();

    }

    @Override
    public void updateStats(String name, int alk, int antalk) {
        openConnection();

        try {
            st = con.prepareStatement("UPDATE " + tableName + " SET alk = alk+? WHERE name = ?");
            st.setInt(1, alk);
            st.setString(2, name);
            st.executeUpdate();

            st = con.prepareStatement("UPDATE " + tableName
                            + " SET guthaben = guthaben-(1.5*?) WHERE name = ?");
            st.setInt(1, alk);
            st.setString(2, name);
            st.executeUpdate();

            st = con.prepareStatement(
                            "UPDATE " + tableName + " SET antalk = antalk+? WHERE name = ?");
            st.setInt(1, antalk);
            st.setString(2, name);
            st.executeUpdate();

            st = con.prepareStatement(
                            "UPDATE " + tableName + " SET guthaben = guthaben-? WHERE name = ?");
            st.setInt(1, antalk);
            st.setString(2, name);
            st.executeUpdate();

        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        closeConnection();

    }

    @Override
    public String getMembersString() {
        openConnection();

        StringBuilder sb = new StringBuilder();

        try {
            st = con.prepareStatement("SELECT * FROM" + tableName);
            rs = st.executeQuery();
            sb.append(String.format("%-10s | %-15s | %-15s | %-15s | %-15s", "name", "guthaben",
                            "alk", "antalk", "show"));
            sb.append("===========|=================|=================|=================|================");
            while (rs.next()) {
                String pname = rs.getString(1);
                double guth = rs.getDouble(2);
                int alk = rs.getInt(3);
                int analk = rs.getInt(4);
                boolean show = rs.getBoolean(5);
                sb.append(String.format("%-10s | %-15s | %-15s | %-15s | %-15s", pname, guth, alk,
                                analk, show));
            }
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        closeConnection();

        return sb.toString();
    }

    @Override
    public boolean doesTableExist() {
        openConnection();

        boolean exists = true;

        try {
            st = con.prepareStatement("SELECT * FROM" + tableName);
            st.executeQuery();
        } catch (SQLException e) {
            exists = false;
        }

        closeConnection();

        return exists;
    }

    @Override
    public void createEmptyTable() {
        openConnection();
        try {
            st = con.prepareStatement("CREATE TABLE " + tableName
                            + " (name varchar(255), guthaben int, alk int, antalk int, show boolean)");
            st.execute();
            System.out.println("Created new table \"" + tableName + "\"");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        closeConnection();

    }

    @Override
    public List<Member> getMembers() {
        openConnection();
        List<Member> retList = new ArrayList<>();

        try {
            st = con.prepareStatement("SELECT * FROM" + tableName);
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
            e.printStackTrace();
        }
        closeConnection();
        return retList;
    }

    @Override
    public void renameMember(String oldName, String newName) {
        openConnection();
        try {
            st = con.prepareStatement("UPDATE " + tableName + " SET name = ? WHERE name = ?");
            st.setString(1, newName);
            st.setString(2, oldName);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();

    }

    /**
     * Opens a connection to the database. The connection needs to be closed.
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
            System.err.println("Couldn't establish connection to database :(");
            e1.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Closes the connection to the database.
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
            System.err.println("Error closing resources.");
            e1.printStackTrace();
            return false;
        }
        return true;
    }
}
