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
public class BannedLinkDAO implements java.io.Serializable {

    private Integer id = 0;
    private Integer guildId;
    private Integer userId;
    private String linkText;
    private String modified;
    private String dateEntered;

    // Constructors
    public BannedLinkDAO() {

    }

    public BannedLinkDAO(Integer id) {
        this.map(id);
    }

    public BannedLinkDAO(String guildDiscordId, String userDiscordId, String linkText) {
        this.mapByGuildMemberText(guildDiscordId, userDiscordId, linkText);
    }

    // CRUD
    public void map(Integer id) {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT * FROM bannedLink WHERE id = ?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                this.id = rs.getInt("id");
                this.guildId = rs.getInt("guildId");
                this.userId = rs.getInt("userId");
                this.linkText = rs.getString("linkText");
                this.modified = rs.getString("modified");
                this.dateEntered = rs.getString("dateEntered");
            }
            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

    }

    public void mapByGuildMemberText(String guildDiscordId, String userDiscordId, String linkText) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT bannedLink.* "
                    + "FROM bannedLink "
                    + "LEFT JOIN user "
                    + "ON user.id = bannedLink.userId "
                    + "LEFT JOIN guild "
                    + "ON guild.id = bannedLink.guildId "
                    + "WHERE user.discordId = ? "
                    + "AND guild.discordId = ? "
                    + "AND linkText = ? ";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, userDiscordId);
            stmt.setString(2, guildDiscordId);
            stmt.setString(3, linkText);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                this.id = rs.getInt("id");
                this.guildId = rs.getInt("guildId");
                this.userId = rs.getInt("userId");
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
            String sql = "INSERT INTO bannedLink (guildId, userId,linkText) VALUES (?,?,?)";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, this.guildId);
            stmt.setInt(2, this.userId);
            stmt.setString(3, this.linkText);
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
            String sql = "UPDATE bannedLink SET guildId = ?, userId = ?, linkText = ?, modified=?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);

            java.util.Date today = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String modified = sdf.format(today);

            stmt.setInt(1, this.guildId);
            stmt.setInt(2, this.userId);
            stmt.setString(3, this.linkText);
            stmt.setString(4, modified);

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
            String sql = "DELETE FROM bannedLink WHERE id = ?";

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
    public static HashMap<String, HashMap<String, List<String>>> getBannedLinks() {
        HashMap<String, HashMap<String, List<String>>> map = new HashMap();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT guild.discordId as guildDiscordId, user.discordId as userDiscordId, bannedLink.linkText "
                    + "FROM bannedLink "
                    + "LEFT JOIN user "
                    + "ON user.id = bannedLink.userId "
                    + "LEFT JOIN guild "
                    + "ON guild.id = bannedLink.guildId ";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (map.containsKey(rs.getString("guildDiscordId"))) {
                    HashMap guildMap = (HashMap) map.get(rs.getString("guildDiscordId"));
                    if (guildMap.containsKey(rs.getString("userDiscordId"))) {
                        List<String> links = (List<String>) guildMap.get(rs.getString("userDiscordId"));
                        links.add(rs.getString("linkText"));
                        guildMap.remove(rs.getString("userDiscordId"));
                        guildMap.put(rs.getString("userDiscordId"), links);
                        map.put("guildDiscordId", guildMap);
                    } else {
                        List<String> links = new ArrayList<String>();
                        links.add(rs.getString("linkText"));
                        guildMap.put(rs.getString("userDiscordId"), links);
                        map.put("guildDiscordId", guildMap);
                    }
                } else {
                    HashMap guildMap = new HashMap();
                    List<String> links = new ArrayList<String>();
                    links.add(rs.getString("linkText"));
                    guildMap.put(rs.getString("userDiscordId"), links);
                    map.put(rs.getString("guildDiscordId"), guildMap);
                }
            }
            con.close();
        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }
        return map;
    }

    public static void deleteBannedLinksByGuildMember(String guildDiscordId, String userDiscordId) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "DELETE  bannedLink "
                    + "FROM bannedLink "
                    + "LEFT JOIN user "
                    + "ON user.id = bannedLink.userId "
                    + "LEFT JOIN guild "
                    + "ON guild.id = bannedLink.guildId "
                    + "WHERE user.discordId = ? "
                    + "AND guild.discordId = ? ";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, userDiscordId);
            stmt.setString(2, guildDiscordId);

            stmt.executeUpdate();

            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }
    }

    public static boolean isBannedLinksByGuildMember(String guildDiscordId, String userDiscordId) {
        boolean exists = false;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT COUNT(*) as count  "
                    + "FROM bannedLink "
                    + "LEFT JOIN user "
                    + "ON user.id = bannedLink.userId "
                    + "LEFT JOIN guild "
                    + "ON guild.id = bannedLink.guildId "
                    + "WHERE user.discordId = ? "
                    + "AND guild.discordId = ? ";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, userDiscordId);
            stmt.setString(2, guildDiscordId);

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

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getGuildId() {
        return guildId;
    }

    public Integer getUserId() {
        return userId;
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

    public void setUserId(Integer userId) {
        this.userId = userId;
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

