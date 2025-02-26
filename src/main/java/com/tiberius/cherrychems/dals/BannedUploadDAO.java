package com.tiberius.cherrychems.dals;

import com.tiberius.cherrychems.util.CherryChemsLogger;
import com.tiberius.cherrychems.util.PropertyProvider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author tiberius
 */
public class BannedUploadDAO {

    private Integer id = 0;
    private Integer guildId;
    private Integer userId;
    private String mimeType;
    private String modified;
    private String dateEntered;

    // Constructors
    public BannedUploadDAO() {

    }

    public BannedUploadDAO(Integer id) {
        this.map(id);
    }

    public BannedUploadDAO(String guildDiscordId, String userDiscordId, String fileType) {
        this.mapByGuildMemberText(guildDiscordId, userDiscordId, fileType);
    }
    // CRUD

    public void map(Integer id) {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT * FROM bannedUpload WHERE id = ?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                this.id = rs.getInt("id");
                this.guildId = rs.getInt("guildId");
                this.userId = rs.getInt("userId");
                this.modified = rs.getString("modified");
                this.dateEntered = rs.getString("dateEntered");
            }
            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

    }

    public void mapByGuildMemberText(String guildDiscordId, String userDiscordId, String fileType) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT bannedUpload.* "
                    + "FROM bannedUpload "
                    + "LEFT JOIN user "
                    + "ON user.id = bannedUpload.userId "
                    + "LEFT JOIN guild "
                    + "ON guild.id = bannedUpload.guildId "
                    + "WHERE user.discordId = ? "
                    + "AND guild.discordId = ? "
                    + "AND bannedUpload.mimeType = ? ";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, userDiscordId);
            stmt.setString(2, guildDiscordId);
            stmt.setString(3, this.getMimeType(fileType));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                this.id = rs.getInt("id");
                this.guildId = rs.getInt("guildId");
                this.userId = rs.getInt("userId");
                this.mimeType = rs.getString("mimeType");
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
            String sql = "INSERT INTO bannedUpload (guildId, userId,mimeType) VALUES (?,?,?)";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, this.guildId);
            stmt.setInt(2, this.userId);
            stmt.setString(3, this.mimeType);
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
            String sql = "UPDATE bannedUpload SET guildId = ?, userId = ?, mimeType = ?, modified=?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);

            java.util.Date today = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String modified = sdf.format(today);

            stmt.setInt(1, this.guildId);
            stmt.setInt(2, this.userId);
            stmt.setString(3, this.mimeType);
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
            String sql = "DELETE FROM bannedUpload WHERE id = ?";

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
    public String getMimeType(String input) {
        String mimeType = "null";
        if (input == null) {
            input = "null";
        }
        switch (input.toLowerCase()) {
            case "mp4":
            case "mpeg":
            case "mpg":
                mimeType = "video/mpeg";
                break;
            case "ogv":
                mimeType = "video/ogg";
                break;
            case "webm":
                mimeType = "video/webm";
                break;
            case "mp3":
                mimeType = "audio/mpeg";
                break;
            case "oga":
                mimeType = "audio/ogg";
                break;
            case "wav":
                mimeType = "audio/wav";
                break;
            case "weba":
                mimeType = "audio/webm";
                break;
            case "jpg":
            case "jpeg":
                mimeType = "image/jpeg";
                break;
            case "webp":
                mimeType = "image/webp";
                break;
            case "gif":
                mimeType = "image/gif";
                break;
            case "png":
                mimeType = "image/png";
                break;
            case "ico":
                mimeType = "image/vnd.microsoft.icon";
                break;
            case "svg":
                mimeType = "image/svg+xml";
                break;
            case "tif":
            case "tiff":
                mimeType = "image/tiff";
                break;
            case "ics":
                mimeType = "text/calendar";
                break;
            case "txt":
                mimeType = "text/plain";
                break;
            case "csv":
                mimeType = "text/csv";
                break;
            case "css":
                mimeType = "text/css";
                break;
            case "htm":
            case "html":
                mimeType = "text/html";
                break;
            case "js":
                mimeType = "text/javascript";
                break;
            case "doc":
                mimeType = "application/msword";
                break;
            case "pdf":
                mimeType = "application/pdf";
                break;
            case "docx":
                mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                break;
            case "ppt":
                mimeType = "application/vnd.ms-powerpoint";
                break;
            case "rtf":
                mimeType = "application/rtf";
                break;
            case "sh":
                mimeType = "application/x-sh";
                break;
            case "swf":
                mimeType = "application/x-shockwave-flash";
                break;
            case "zip":
                mimeType = "application/zip";
                break;
            case "7z":
                mimeType = "application/x-7z-compressed";
                break;
        }
        return mimeType;
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

    public String getMimeType() {
        return mimeType;
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

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public void setDateEntered(String dateEntered) {
        this.dateEntered = dateEntered;
    }

}
