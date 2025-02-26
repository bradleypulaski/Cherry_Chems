package com.tiberius.cherrychems.dals;

import com.tiberius.cherrychems.util.CherryChemsLogger;
import com.tiberius.cherrychems.util.PropertyProvider;
import java.sql.*;

/**
 *
 * @author tiberius
 */
public class GuildDAO {

    private Integer id = 0;
    private String discordId;
    private String name;
    private String modified;
    private String dateEntered;

    // Constructors
    public GuildDAO() {

    }

    public GuildDAO(Integer id) {
        this.map(id);
    }

    public GuildDAO(String discordId) {
        this.mapByDiscordId(discordId);
    }

    public GuildDAO(String discordId, String name) {
        this.discordId = discordId;
        this.name = name;
    }

    // CRUD
    public void map(Integer id) {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT * FROM guild WHERE id = ?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                this.id = rs.getInt("id");
                this.discordId = rs.getString("discordId");
                this.name = rs.getString("name");
                this.modified = rs.getString("modified");
                this.dateEntered = rs.getString("dateEntered");
            }
            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

    }

    public void mapByDiscordId(String discordId) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT * FROM guild WHERE discordId = ?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, discordId);

            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                this.id = rs.getInt("id");
                this.discordId = rs.getString("discordId");
                this.name = rs.getString("name");
                this.modified = rs.getString("modified");
                this.dateEntered = rs.getString("dateEntered");
            }
            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }
    }

    public void insert() {
        if (this.id != 0) {
            return;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "INSERT INTO guild (discordId,name) VALUES (?,?)";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, this.discordId);
            stmt.setString(2, this.name);
            Integer rows = stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                this.id = generatedKeys.getInt(1);
            }
            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", "Guild Insert Error: " +  e.getMessage());
        }

    }

    public void update() {
        if (this.id == 0) {
            return;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "UPDATE guild SET discordId = ?, name = ?, modified=?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);

            java.util.Date today = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String modified = sdf.format(today);

            stmt.setString(1, this.discordId);
            stmt.setString(2, this.name);
            stmt.setString(3, modified);

            Integer rows = stmt.executeUpdate();
            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }
    }

    public void delete() {
        if (this.id == 0) {
            return;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "DELETE FROM guild WHERE id = ?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setInt(1, this.id);

            Integer rows = stmt.executeUpdate();
            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }
    }

    // Operations
    public static ResultSet getUserList() {

        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT * FROM guild";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);

            rs = stmt.executeQuery();
            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        return rs;

    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getDiscordId() {
        return discordId;
    }

    public String getName() {
        return name;
    }

    public String getModified() {
        return modified;
    }

    public String getDateEntered() {
        return dateEntered;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public void setDateEntered(String dateEntered) {
        this.dateEntered = dateEntered;
    }

}
