package com.tiberius.cherrychems.dals;

import com.tiberius.cherrychems.bot.CherryChemsBot;
import com.tiberius.cherrychems.util.CherryChemsLogger;
import com.tiberius.cherrychems.util.PropertyProvider;
import java.sql.*;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

/**
 *
 * @author tiberius
 */
public class PunishedUserDAO {

    private Integer id = 0;
    private Integer guildId;
    private Integer roleId;
    private Integer userId;
    private String modified;
    private String dateEntered;

    // Constructors
    public PunishedUserDAO() {

    }

    public PunishedUserDAO(Integer id) {
        this.map(id);
    }

    // CRUD
    public void map(Integer id) {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT * FROM punishedUser WHERE id = ?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                this.id = rs.getInt("id");
                this.guildId = rs.getInt("guildId");
                this.roleId = rs.getInt("roleId");
                this.userId = rs.getInt("userId");
                this.modified = rs.getString("modified");
                this.dateEntered = rs.getString("dateEntered");
            }
            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

    }

    public void mapByGuildUser(Integer guildId, Integer userId) {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }

        try {
            String sql = "SELECT * FROM punishedUser WHERE guildId = ? AND userId = ?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, guildId);
            stmt.setInt(2, userId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                this.id = rs.getInt("id");
                this.guildId = rs.getInt("guildId");
                this.roleId = rs.getInt("roleId");
                this.userId = rs.getInt("userId");
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
            String sql = "INSERT INTO punishedUser (guildId,roleId,userId) VALUES (?,?,?)";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, this.guildId);
            stmt.setInt(2, this.roleId);
            stmt.setInt(3, this.userId);

            Integer rows = stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                this.id = generatedKeys.getInt(1);
            }
            CherryChemsLogger.log("D", "user added to a punished table");

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
            String sql = "UPDATE punishedUser SET guildId = ?, roleId = ?, userId = ?, modified=?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);

            java.util.Date today = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String modified = sdf.format(today);

            stmt.setInt(1, this.guildId);
            stmt.setInt(2, this.roleId);
            stmt.setInt(3, this.userId);
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
            String sql = "DELETE FROM punishedUser WHERE id = ?";

            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setInt(1, this.id);

            Integer rows = stmt.executeUpdate();
            CherryChemsLogger.log("D", "user removed from a punished table");

            con.close();

        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }
    }

    // Operations
    public void punish(Message message, String guildDiscordId, String guildName, String roleDiscordId, String roleName, String userDiscordId, String username) {
        GuildDAO guild = new GuildDAO(guildDiscordId);
        RoleDAO role = new RoleDAO(roleDiscordId);
        UserDAO user = new UserDAO(userDiscordId);

        if (guild.getId() == 0) {
            guild.setDiscordId(guildDiscordId);
            guild.setName(guildName);
            guild.insert();
        }

        if (role.getId() == 0) {
            role.setDiscordId(roleDiscordId);
            role.setName(roleName);
            role.insert();
        }

        if (user.getId() == 0) {
            user.setDiscordId(userDiscordId);
            user.setUsername(username);
            user.insert();
        }

        this.guildId = guild.getId();
        this.roleId = role.getId();
        this.userId = user.getId();

        this.mapByGuildUser(guild.getId(), user.getId()); // if record exists it will set the id to not be 0 and then insert will not run in order to avoid duplicates
        if (this.id != 0) {
            MessageChannel channel = message.getChannel();
            String[] arguments = {message.getAuthor().getId(), String.valueOf(this.id), roleDiscordId};
            CherryChemsBot.awaitResponse("awaitPunishOverwrite", arguments);
            String separator = PropertyProvider.getProperty("bot.prefix.production");
            if (PropertyProvider.getProperty("bot.environment").equals("development")) {
                separator = PropertyProvider.getProperty("bot.prefix.development");
            }
            String sp = separator;
            channel.sendTyping().queue((m) -> {
                channel.sendMessage("user already punished. do you want to overwrite the existing punishment? if so say " + sp + "punishoverwrite").queueAfter(5, TimeUnit.SECONDS);
            });
        } else {
            MessageChannel channel = message.getChannel();
            channel.sendTyping().queue((m) -> {
                channel.sendMessage("sending to the camp...").queueAfter(5, TimeUnit.SECONDS);
            });
        }
        this.insert();

    }

    public void unPunish(Message message, String guildDiscordId, String guildName, String userDiscordId, String username) {
        GuildDAO guild = new GuildDAO(guildDiscordId);
        UserDAO user = new UserDAO(userDiscordId);

        if (guild.getId() == 0) {
            guild.setDiscordId(guildDiscordId);
            guild.setName(guildName);
            guild.insert();
        }

        if (user.getId() == 0) {
            user.setDiscordId(userDiscordId);
            user.setUsername(username);
            user.insert();
        }

        this.mapByGuildUser(guild.getId(), user.getId());
        if (this.id == 0) {
            MessageChannel channel = message.getChannel();
            channel.sendTyping().queue((m) -> {
                channel.sendMessage("member is not punished in this guild").queueAfter(5, TimeUnit.SECONDS);
            });
        } else {
            MessageChannel channel = message.getChannel();
            channel.sendTyping().queue((m) -> {
                channel.sendMessage("released from the camp").queueAfter(5, TimeUnit.SECONDS);
            });
        }
        this.delete();
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getGuildId() {
        return guildId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public Integer getUserId() {
        return userId;
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

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public void setDateEntered(String dateEntered) {
        this.dateEntered = dateEntered;
    }

}
