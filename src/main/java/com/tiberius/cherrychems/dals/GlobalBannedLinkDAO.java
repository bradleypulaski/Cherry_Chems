package com.tiberius.cherrychems.dals;

import com.tiberius.cherrychems.util.CherryChemsLogger;
import com.tiberius.cherrychems.util.PropertyProvider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author tiberius
 */
public class GlobalBannedLinkDAO {

    private Integer id = 0;
    private Integer guildId;
    private String linkText;
    private String modified;
    private String dateEntered;

    // Constructors
    public GlobalBannedLinkDAO() {

    }

    public GlobalBannedLinkDAO(Integer id) {
        this.map(id);
    }

    public GlobalBannedLinkDAO(String guildDiscordId, String linkText) {
        this.mapByGuildText(guildDiscordId, linkText);
    }

    // CRUD
    public void map(Integer id) {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT * FROM globalBannedLink WHERE id = ?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                this.id = rs.getInt("id");
                this.guildId = rs.getInt("guildId");
                this.linkText = rs.getString("linkText");
                this.modified = rs.getString("modified");
                this.dateEntered = rs.getString("dateEntered");
            }
            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

    }

    public void mapByGuildText(String guildDiscordId, String linkText) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT globalBannedLink.* "
                    + "FROM globalBannedLink "
                    + "LEFT JOIN guild "
                    + "ON guild.id = globalBannedLink.guildId "
                    + "WHERE guild.discordId = ? "
                    + "AND linkText = ? ";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, guildDiscordId);
            stmt.setString(2, linkText);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                this.id = rs.getInt("id");
                this.guildId = rs.getInt("guildId");
                this.linkText = rs.getString("linkText");
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
            String sql = "INSERT INTO globalBannedLink (guildId, linkText) VALUES (?,?)";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, this.guildId);
            stmt.setString(2, this.linkText);
            Integer rows = stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                this.id = generatedKeys.getInt(1);
            }
            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", "BannedLink Insert Error: " + e.getMessage());
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
            String sql = "UPDATE globalBannedLink SET  linkText = ?, modified=?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);

            java.util.Date today = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String modified = sdf.format(today);

            stmt.setString(1, this.linkText);
            stmt.setString(2, modified);

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
            String sql = "DELETE FROM globalBannedLink WHERE id = ?";

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
    public static boolean isBannedLinksByGuild(String guildDiscordId) {
        boolean exists = false;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT COUNT(*) as count  "
                    + "FROM globalBannedLink "
                    + "LEFT JOIN guild "
                    + "ON guild.id = globalBannedLink.guildId "
                    + "WHERE guild.discordId = ? ";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, guildDiscordId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer count = rs.getInt("count");
                if (count > 0) {
                    exists = true;
                }
            }
            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }
        return exists;
    }

    public static void deleteBannedLinksByGuild(String guildDiscordId) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "DELETE  globalBannedLink "
                    + "FROM globalBannedLink "
                    + "LEFT JOIN guild "
                    + "ON guild.id = globalBannedLink.guildId "
                    + "WHERE guild.discordId = ? ";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, guildDiscordId);

            stmt.executeUpdate();

            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }
    }

    public static HashMap<String, List<String>> getBannedLinks() {
        HashMap<String, List<String>> map = new HashMap();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT guild.discordId as guildDiscordId, globalBannedLink.linkText "
                    + "FROM globalBannedLink "
                    + "LEFT JOIN guild "
                    + "ON guild.id = globalBannedLink.guildId ";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (map.containsKey(rs.getString("guildDiscordId"))) {
                    List<String> links = (List<String>) map.get(rs.getString("guildDiscordId"));
                    links.add(rs.getString("linkText"));
                    map.put(rs.getString("guildDiscordId"), links);
                } else {
                    List<String> links = new ArrayList<String>();
                    links.add(rs.getString("linkText"));
                    map.put(rs.getString("guildDiscordId"), links);
                }
            }
            con.close();
        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        return map;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getGuildId() {
        return guildId;
    }

    public String getLinkText() {
        return linkText;
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

    public void setGuildId(Integer guildId) {
        this.guildId = guildId;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public void setDateEntered(String dateEntered) {
        this.dateEntered = dateEntered;
    }

}
