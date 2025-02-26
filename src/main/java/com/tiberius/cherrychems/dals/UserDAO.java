package com.tiberius.cherrychems.dals;

import com.tiberius.cherrychems.util.CherryChemsLogger;
import com.tiberius.cherrychems.util.PropertyProvider;
import java.sql.*;

public class UserDAO {

    private Integer id = 0;
    private String username;
    private String discordId;
    private String modified;
    private String dateEntered;

    // Constructors
    public UserDAO() {

    }

    public UserDAO(Integer id) {
        this.map(id);
    }

    public UserDAO(String discordId) {
        this.mapByDiscordId(discordId);
    }

    public UserDAO(String discordId, String username) {
        this.discordId = discordId;
        this.username = username;
    }

    // CRUD
    public void map(Integer id) {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT * FROM user WHERE id = ?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                this.id = rs.getInt("id");
                this.discordId = rs.getString("discordId");
                this.username = rs.getString("username");
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
            String sql = "SELECT * FROM user WHERE discordId = ?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, discordId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                this.id = rs.getInt("id");
                this.discordId = rs.getString("discordId");
                this.username = rs.getString("username");
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
            String sql = "INSERT INTO user (discordId,username) VALUES (?,?)";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, this.discordId);
            stmt.setString(2, this.username);
            Integer rows = stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                this.id = generatedKeys.getInt(1);
            }
            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
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
            String sql = "UPDATE user SET discordId = ?, username = ?, modified=?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);

            java.util.Date today = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String modified = sdf.format(today);

            stmt.setString(1, this.discordId);
            stmt.setString(2, this.username);
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
            String sql = "DELETE FROM user WHERE id = ?";

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
            String sql = "SELECT * FROM user";

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

    public String getUsername() {
        return username;
    }

    public String getDiscordId() {
        return discordId;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public void setDateEntered(String dateEntered) {
        this.dateEntered = dateEntered;
    }

}
