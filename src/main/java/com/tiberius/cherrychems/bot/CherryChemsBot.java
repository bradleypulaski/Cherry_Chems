package com.tiberius.cherrychems.bot;

import com.tiberius.cherrychems.dals.BannedLinkDAO;
import com.tiberius.cherrychems.dals.GlobalBannedLinkDAO;
import com.tiberius.cherrychems.dals.GuildDAO;
import com.tiberius.cherrychems.dals.PunishedUserDAO;
import com.tiberius.cherrychems.dals.RoleDAO;
import com.tiberius.cherrychems.dals.UserDAO;
import com.tiberius.cherrychems.util.CherryChemsLogger;
import com.tiberius.cherrychems.util.PropertyProvider;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

/**
 *
 * @author tiberius
 */
public class CherryChemsBot {

    // dynamic state
    private static Integer intoxicationLevel = 0;
    private static HashMap<String, HashMap<String, List<String>>> bannedLinksMap; // parent key guild discord id then val is hashmap with user discord id is key, string array with links are values
    private static HashMap<String, List<String>> globalBannedLinksMap; // key guild, value string list with domains
// awaits
    private static String awaitUserDiscordId;
    private static Integer awaitPunishedUserId;
    private static String awaitNewRoleDiscordId;

    // Constructer/s
    public CherryChemsBot() {
        this.init();
    }

    // Initialization
    public void init() {
        this.DBSetup();
        BannedLinksSetup();
    }

    public void DBSetup() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://" + PropertyProvider.getProperty("db.host") + ":3306/" + PropertyProvider.getProperty("db.name"), PropertyProvider.getProperty("db.user"), PropertyProvider.getProperty("db.password"));
            Statement stm = con.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS user ("
                    + "    id INT(11) UNSIGNED AUTO_INCREMENT PRIMARY KEY,"
                    + "    username VARCHAR(128)  NULL,"
                    + "    discordId VARCHAR(128) NOT NULL,"
                    + "    modified DATETIME  NULL,"
                    + "    dateEntered TIMESTAMP"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            stm.execute(sql);
            sql = "CREATE INDEX  IF NOT EXISTS  discordId ON user (discordId);";
            stm.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS guild ("
                    + "    id INT(11) UNSIGNED AUTO_INCREMENT PRIMARY KEY,"
                    + "    discordId VARCHAR(128)  NULL,"
                    + "    name VARCHAR(128) NOT NULL,"
                    + "    modified DATETIME  NULL,"
                    + "    dateEntered TIMESTAMP"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            stm.execute(sql);
            sql = "CREATE INDEX IF NOT EXISTS  discordId ON guild (discordId);";
            stm.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS role ("
                    + "    id INT(11) UNSIGNED AUTO_INCREMENT PRIMARY KEY,"
                    + "    discordId VARCHAR(128)  NULL,"
                    + "    name VARCHAR(128) NOT NULL,"
                    + "    modified DATETIME  NULL,"
                    + "    dateEntered TIMESTAMP"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            stm.execute(sql);
            sql = "CREATE INDEX  IF NOT EXISTS discordId ON role (discordId);";
            stm.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS punishedUser ("
                    + "    id INT(11) UNSIGNED AUTO_INCREMENT PRIMARY KEY,"
                    + "    guildId INT(11)  NULL,"
                    + "    roleId INT(11)  NULL,"
                    + "    userId INT(11) NOT NULL,"
                    + "    modified DATETIME  NULL,"
                    + "    dateEntered TIMESTAMP"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            stm.execute(sql);
            sql = "CREATE INDEX IF NOT EXISTS  guildId ON punishedUser (guildId);";
            stm.execute(sql);
            sql = "CREATE INDEX IF NOT EXISTS  roleId ON punishedUser (roleId);";
            stm.execute(sql);
            sql = "CREATE INDEX IF NOT EXISTS  userId ON punishedUser (userId);";
            stm.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS bannedLink ("
                    + "    id INT(11) UNSIGNED AUTO_INCREMENT PRIMARY KEY,"
                    + "    guildId INT(11)  NOT NULL,"
                    + "    userId INT(11) NOT NULL,"
                    + "    linkText VARCHAR(256) NOT NULL,"
                    + "    modified DATETIME  NULL,"
                    + "    dateEntered TIMESTAMP"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            stm.execute(sql);
            sql = "CREATE INDEX IF NOT EXISTS  guildId ON bannedLink (guildId);";
            stm.execute(sql);
            sql = "CREATE INDEX IF NOT EXISTS  userId ON bannedLink (userId);";
            stm.execute(sql);
            sql = "CREATE INDEX IF NOT EXISTS  linkText ON bannedLink (linkText);";
            stm.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS globalBannedLink ("
                    + "    id INT(11) UNSIGNED AUTO_INCREMENT PRIMARY KEY,"
                    + "    guildId INT(11)  NOT NULL,"
                    + "    linkText VARCHAR(256) NOT NULL,"
                    + "    modified DATETIME  NULL,"
                    + "    dateEntered TIMESTAMP"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            stm.execute(sql);
            sql = "CREATE INDEX IF NOT EXISTS  guildId ON globalBannedLink (guildId);";
            stm.execute(sql);
            sql = "CREATE INDEX IF NOT EXISTS  linkText ON globalBannedLink (linkText);";
            stm.execute(sql);
            con.close();
        } catch (SQLException e) {
            CherryChemsLogger.log("C", e.getMessage());
        }
    }

    public static void BannedLinksSetup() {
        bannedLinksMap = null;
        bannedLinksMap = BannedLinkDAO.getBannedLinks();
        globalBannedLinksMap = null;
        globalBannedLinksMap = GlobalBannedLinkDAO.getBannedLinks();
    }

    // Social
    public static void receiveDrugs(Message message, String drug) {
        MessageChannel channel = message.getChannel();

        switch (drug) {
            case "dash":
                addIntoxication(10);
                say(channel, "thanks for the Dash!");
                System.out.println(intoxicationLevel.toString());
                break;
            case "buck":
                addIntoxication(8);
                say(channel, "thanks for the Buck!");
                System.out.println(intoxicationLevel.toString());
                break;
            case "mintals":
                addIntoxication(14);
                say(channel, "thanks for the Mint-als!");
                System.out.println(intoxicationLevel.toString());
                break;
            case "stamptede":
                addIntoxication(6);
                say(channel, "thanks for the Stampede!");
                System.out.println(intoxicationLevel.toString());
                break;
        }
    }

    public static void say(MessageChannel channel, String message) {

        MessageChannel ch = channel;
        Integer intoxication = intoxicationLevel;
        if (intoxication != 0) {
            message = transformMessage(intoxication, message);
        }
        String msg = message;

        ch.sendTyping().queue((m) -> {
            ch.sendMessage(msg).queueAfter(5, TimeUnit.SECONDS);
        });

    }

    public static void responseCheck(Message message) {
        MessageChannel channel = message.getChannel();
        User author = message.getAuthor();
        String msg = message.getContentRaw();
        String[] msgArray = msg.toLowerCase().split(" ");
        List<String> msgList = Arrays.asList(msgArray);

        if (author.getId().equals(PropertyProvider.getProperty("discord.jinx"))) {
            if (msgList.contains("honor")) {
                CherryChemsBot.say(channel, "is seppuku honorable?");
            }
        }
        if (msgList.contains("drugs")) {
            CherryChemsBot.say(channel, "DRUGS!! WHERE!??");
        }
        if (msgList.contains("luna")) {
            CherryChemsBot.say(channel, "HEIL LUNA!!! NEIGHUS VULT!!! AVA MARE'IA");
        }
    }

    // Operational
    public static String transformMessage(Integer intoxication, String message) {

        Integer factor = Math.round(intoxication / 8);
        String alphabet = "QWERTYUIOPLKJHGFDSAZXCBNMqwertyuiopasdfghjkl;'zxcvbnm,,./1234567890!@#$%^&*()_+=-";

        String[] withdrawalResponses = {" *whimpers* ", " yawn... ", " *rubs eyes* ", " *mlems* ", " *eyes droop* ", " *eyes droop* ", " ...ugh *hooves head* ",};

        Random rand = new Random();

        if (intoxication < 0) {
            message = withdrawalResponses[rand.nextInt(withdrawalResponses.length)] + " " + message;
        } else {
            Integer i = 0;
            while (i < factor) {
                Integer length = message.length();
                Integer pointer = rand.nextInt(length);
                Integer nextPointer = pointer;
                if (pointer != length) {
                    nextPointer++;
                }
                if (pointer != 0) {
                    pointer--;
                }
                message = message.substring(0, pointer) + alphabet.charAt(rand.nextInt(alphabet.length())) + message.substring(nextPointer);
                i++;
            }
        }

        return message;
    }

    public static void addIntoxication(Integer num) {

        Integer nextWithdrawal = 0;

        if (intoxicationLevel + num <= 50) {
            intoxicationLevel += num;
            nextWithdrawal = num * 2;
        } else {
            nextWithdrawal = (50 - intoxicationLevel) * 2;
            intoxicationLevel = 50;
        }

        CherryChemsBot.withdraw(nextWithdrawal);

    }

    public static void withdraw(Integer num) {

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        final Future handler = executor.submit(new Callable() {
            @Override
            public Object call() throws Exception {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        executor.schedule(new Runnable() {
            public void run() {
                intoxicationLevel -= num;
                System.out.println("Cherry Chems Intoxication Level: " + intoxicationLevel);
            }
        }, 5, TimeUnit.MINUTES);

        ScheduledExecutorService executor2 = Executors.newScheduledThreadPool(2);
        final Future handler2 = executor.submit(new Callable() {
            @Override
            public Object call() throws Exception {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        executor2.schedule(new Runnable() {
            public void run() {
                intoxicationLevel += num / 2;
                System.out.println("Cherry Chems Intoxication Level: " + intoxicationLevel);
            }
        }, 10, TimeUnit.MINUTES);

    }

    public static void help(Message message) {

        MessageChannel channel = message.getChannel();
        // reference: https://www.tablesgenerator.com/text_tables/load to make utf8 discord tables
        String help = "```╔═══════════════════════════════╦═════════════════════════════════════════════════════════════╗\n"
                + "║ ~help, ~h                     ║ -returns list of commands                                   ║\n"
                + "╠═══════════════════════════════╬═════════════════════════════════════════════════════════════╣\n"
                + "║ ~gib<Drug>                    ║ options: dash,buck,mintals,stampede                         ║\n"
                + "║                               ║ -gets cherry high ~~she might withdraw~~                    ║\n"
                + "║                               ║ -example ~gibdash                                           ║\n"
                + "╠═══════════════════════════════╬═════════════════════════════════════════════════════════════╣\n"
                + "║ ~punish <@Member> <@Role>     ║ -auto-roles member when they rejoin the server              ║\n"
                + "║                               ║ -can only be used by members with role privileges           ║\n"
                + "╠═══════════════════════════════╬═════════════════════════════════════════════════════════════╣\n"
                + "║ ~unpunish <@Member>           ║ -removes auto-role for member                               ║\n"
                + "║                               ║ -can only be used by members with role privileges           ║```";
        String help2 = "```╠═══════════════════════════════╬═════════════════════════════════════════════════════════════╣\n"
                + "║ ~banlink <Domain> <@Member>   ║ -bans member from posting links from domain                 ║\n"
                + "║                               ║ -can only be used by members with message delete privileges ║\n"
                + "╠═══════════════════════════════╬═════════════════════════════════════════════════════════════╣\n"
                + "║ ~unbanlink <Domain> <@Member> ║ -removes banned domain links for member                     ║\n"
                + "║                               ║ -can only be used by members with message delete privileges ║\n"
                + "╠═══════════════════════════════╬═════════════════════════════════════════════════════════════╣\n"
                + "║ ~unbanlinks <@Member>         ║ -removes all banned domain links for member                 ║\n"
                + "║                               ║ -can only be used by members with message delete privileges ║\n"
                + "╠═══════════════════════════════╬═════════════════════════════════════════════════════════════╣\n"
                + "║ ~hasbannedlinks <@Member>     ║ -returns all banned domains for member                      ║\n"
                + "║                               ║ -can only be used by members with message delete privileges ║```";
        String help3 = "```╠═══════════════════════════════╬═════════════════════════════════════════════════════════════╣\n"
                + "║ ~version, ~v                  ║ -returns cherry chem's current version                      ║\n"
                + "╚═══════════════════════════════╩═════════════════════════════════════════════════════════════╝```";

        channel.sendTyping().queue((m) -> {
            channel.sendMessage(help).queueAfter(5, TimeUnit.SECONDS, (m2) -> {
                channel.sendMessage(help2).queue((m3) -> {
                    channel.sendMessage(help3).queue();
                });
            });
        });

    }

    public static void version(Message message) {
        MessageChannel channel = message.getChannel();
        String version = PropertyProvider.getProperty("bot.version");
        channel.sendTyping().queue((m) -> {
            channel.sendMessage("CherryChems Bot Version: " + version).queueAfter(5, TimeUnit.SECONDS);
        });
    }

    // Punishment System
    public static void punish(Message message) {
        User author = message.getAuthor();
        String discordId = author.getId();
        Member authorMember = message.getMember();
        MessageChannel channel = message.getChannel();

        if (authorMember.hasPermission(Permission.MANAGE_ROLES) == false || discordId.equals(PropertyProvider.getProperty("bot.master")) == false) {
            channel.sendTyping().queue((m) -> {
                channel.sendMessage("nice try lmao").queueAfter(5, TimeUnit.SECONDS);
            });
        } else {
            List<Role> roles = message.getMentionedRoles();
            List<User> users = message.getMentionedUsers();
            Role role = roles.get(0);
            User user = users.get(0);
            Guild guild = message.getGuild();

            String guildDiscordId = guild.getId();
            String roleDiscordId = role.getId();
            String userDiscordId = user.getId();
            String guildName = guild.getName();
            String roleName = role.getName();
            String username = user.getName();

            PunishedUserDAO punishedUser = new PunishedUserDAO();
            punishedUser.punish(message, guildDiscordId, guildName, roleDiscordId, roleName, userDiscordId, username);
        }
    }

    public static void unPunish(Message message) {
        User author = message.getAuthor();
        String discordId = author.getId();
        Member authorMember = message.getMember();

        MessageChannel channel = message.getChannel();

        if (authorMember.hasPermission(Permission.MANAGE_ROLES) == false || discordId.equals(PropertyProvider.getProperty("bot.master")) == false) {
            channel.sendTyping().queue((m) -> {
                channel.sendMessage("nice try lmao").queueAfter(5, TimeUnit.SECONDS);
            });
        } else {
            List<User> users = message.getMentionedUsers();
            User user = users.get(0);
            Guild guild = message.getGuild();

            String guildDiscordId = guild.getId();
            String userDiscordId = user.getId();
            String guildName = guild.getName();
            String username = user.getName();

            PunishedUserDAO punishedUser = new PunishedUserDAO();
            punishedUser.unPunish(message, guildDiscordId, guildName, userDiscordId, username);
        }
    }

    // Banned Links System
    public static void addBannedLink(Message message) {
        try {
            MessageChannel channel = message.getChannel();
            Member author = message.getMember();
            List<User> users = message.getMentionedUsers();
            User user = users.get(0);

            if (author.hasPermission(Permission.MESSAGE_MANAGE) || author.getId().equals(PropertyProvider.getProperty("bot.master"))) {
                String msg = message.getContentRaw();
                String[] msgArray = msg.split(" ");
                if (msgArray[2] == null || user == null || msgArray[1] == null) {
                    channel.sendTyping().queue((m) -> {
                        channel.sendMessage("type that correctly please").queueAfter(5, TimeUnit.SECONDS);
                    });
                } else {
                    Guild guild = message.getGuild();
                    GuildDAO guildDAO = new GuildDAO(guild.getId());
                    if (guildDAO.getId() == 0) {
                        guildDAO.setDiscordId(guild.getId());
                        guildDAO.setName(guild.getName());
                        guildDAO.insert();
                    }
                    UserDAO userDAO = new UserDAO(user.getId());
                    if (userDAO.getId() == 0) {
                        userDAO.setDiscordId(user.getId());
                        userDAO.setUsername(user.getName());
                        userDAO.insert();
                    }
                    BannedLinkDAO bannedLink = new BannedLinkDAO(guild.getId(), user.getId(), msgArray[1]);
                    if (bannedLink.getId() == 0) {
                        bannedLink.setGuildId(guildDAO.getId());
                        bannedLink.setUserId(userDAO.getId());
                        bannedLink.setLinkText(msgArray[1]);
                        bannedLink.insert();
                        CherryChemsBot.BannedLinksSetup();
                        channel.sendTyping().queue((m) -> {
                            channel.sendMessage("banned link added").queueAfter(5, TimeUnit.SECONDS);
                        });
                    } else {
                        channel.sendTyping().queue((m) -> {
                            channel.sendMessage("link is already banned for this shlub on this guild").queueAfter(5, TimeUnit.SECONDS);
                        });
                    }
                }
            } else {
                channel.sendTyping().queue((m) -> {
                    channel.sendMessage("nice try").queueAfter(5, TimeUnit.SECONDS);
                });
            }
        } catch (Exception e) {
            CherryChemsLogger.log("W", e.getMessage());
        }
    }

    public static void dropBannedLink(Message message) {
        try {
            MessageChannel channel = message.getChannel();
            Member author = message.getMember();
            List<User> users = message.getMentionedUsers();
            User user = users.get(0);

            if (author.hasPermission(Permission.MESSAGE_MANAGE) || author.getId().equals(PropertyProvider.getProperty("bot.master"))) {
                String msg = message.getContentRaw();
                String[] msgArray = msg.split(" ");
                if (msgArray[2] == null || user == null || msgArray[1] == null) {
                    channel.sendTyping().queue((m) -> {
                        channel.sendMessage("type that correctly please").queueAfter(5, TimeUnit.SECONDS);
                    });
                } else {
                    Guild guild = message.getGuild();
                    GuildDAO guildDAO = new GuildDAO(guild.getId());
                    if (guildDAO.getId() == 0) {
                        guildDAO.setDiscordId(guild.getId());
                        guildDAO.setName(guild.getName());
                        guildDAO.insert();
                    }
                    UserDAO userDAO = new UserDAO(user.getId());
                    if (userDAO.getId() == 0) {
                        userDAO.setDiscordId(user.getId());
                        userDAO.setUsername(user.getName());
                        userDAO.insert();
                    }
                    BannedLinkDAO bannedLink = new BannedLinkDAO(guild.getId(), user.getId(), msgArray[1]);
                    if (bannedLink.getId() != 0) {
                        bannedLink.delete();
                        CherryChemsBot.BannedLinksSetup();
                        channel.sendTyping().queue((m) -> {
                            channel.sendMessage("done").queueAfter(5, TimeUnit.SECONDS);
                        });
                    } else {
                        channel.sendTyping().queue((m) -> {
                            channel.sendMessage("user isnt banned from posting that link on this guild").queueAfter(5, TimeUnit.SECONDS);
                        });
                    }
                }
            } else {
                channel.sendTyping().queue((m) -> {
                    channel.sendMessage("nice try").queueAfter(5, TimeUnit.SECONDS);
                });
            }
        } catch (Exception e) {
            CherryChemsLogger.log("W", e.getMessage());
        }
    }

    public static void dropBannedLinks(Message message) {
        try {
            MessageChannel channel = message.getChannel();
            Member author = message.getMember();
            List<User> users = message.getMentionedUsers();
            User user = users.get(0);

            if (author.hasPermission(Permission.MESSAGE_MANAGE) || author.getId().equals(PropertyProvider.getProperty("bot.master"))) {
                String msg = message.getContentRaw();
                String[] msgArray = msg.split(" ");
                if (user == null || msgArray[1] == null) {
                    channel.sendTyping().queue((m) -> {
                        channel.sendMessage("type that correctly please").queueAfter(5, TimeUnit.SECONDS);
                    });
                } else {
                    Guild guild = message.getGuild();
                    GuildDAO guildDAO = new GuildDAO(guild.getId());
                    if (guildDAO.getId() == 0) {
                        guildDAO.setDiscordId(guild.getId());
                        guildDAO.setName(guild.getName());
                        guildDAO.insert();
                    }
                    UserDAO userDAO = new UserDAO(user.getId());
                    if (userDAO.getId() == 0) {
                        userDAO.setDiscordId(user.getId());
                        userDAO.setUsername(user.getName());
                        userDAO.insert();
                    }
                    String guildDiscordId = message.getGuild().getId();
                    String userDiscordId = user.getId();
                    if (BannedLinkDAO.isBannedLinksByGuildMember(guildDiscordId, userDiscordId)) {
                        BannedLinkDAO.deleteBannedLinksByGuildMember(guildDiscordId, userDiscordId);
                        CherryChemsBot.BannedLinksSetup();
                        channel.sendTyping().queue((m) -> {
                            channel.sendMessage("done").queueAfter(5, TimeUnit.SECONDS);
                        });
                    } else {
                        channel.sendTyping().queue((m) -> {
                            channel.sendMessage("user isn't banned from posting any links on this guild").queueAfter(5, TimeUnit.SECONDS);
                        });
                    }
                }
            } else {
                channel.sendTyping().queue((m) -> {
                    channel.sendMessage("nice try").queueAfter(5, TimeUnit.SECONDS);
                });
            }
        } catch (Exception e) {
            CherryChemsLogger.log("W", e.getMessage());
        }

    }

    public static void bannedLinkCheck(Message message) throws URISyntaxException {
        Guild guild = message.getGuild();
        User user = message.getAuthor();
        String msg = message.getContentRaw();
        String[] msgArray = msg.split(" ");
        List<String> msgList = Arrays.asList(msgArray);
        if (bannedLinksMap.containsKey(guild.getId())) {
            HashMap guildMap = (HashMap) bannedLinksMap.get(guild.getId());
            if (guildMap.containsKey(user.getId())) {
                List<String> links = (List<String>) guildMap.get(user.getId());
                HashSet<String> tmp = new HashSet<String>();
                Boolean delete = false;
                for (String el : msgList) {
                    tmp.add(getDomainName(el));
                }
                for (String el : links) {
                    if (tmp.contains(getDomainName(el))) {
                        delete = true;
                    }
                }
                if (delete) {
                    message.delete().queue();
                }
            }
        }
    }

    public static void hasBannedLinks(Message message) {
        MessageChannel channel = message.getChannel();
        Guild guild = message.getGuild();
        List<User> users = message.getMentionedUsers();
        User user = users.get(0);
        String msgPrint = "User has the following links banned:\n";

        if (bannedLinksMap.containsKey(guild.getId())) {
            HashMap guildMap = bannedLinksMap.get(guild.getId());
            if (guildMap.containsKey(user.getId())) {
                List<String> links = (List<String>) guildMap.get(user.getId());
                Iterator it = links.iterator();
                while (it.hasNext()) {
                    String link = (String) it.next();
                    msgPrint += link + "\n";
                }
                channel.sendTyping().complete();
                channel.sendMessage(msgPrint).completeAfter(5, TimeUnit.SECONDS);
            } else {
                channel.sendTyping().queue((m) -> {
                    channel.sendMessage("no link bans for this guild member").queueAfter(5, TimeUnit.SECONDS);
                });
            }
        } else {
            channel.sendTyping().queue((m) -> {
                channel.sendMessage("no link bans on this guild").queueAfter(5, TimeUnit.SECONDS);
            });
        }

    }

    //Global Banned Links System
    public static void addGlobalBannedLink(Message message) {
        try {
            MessageChannel channel = message.getChannel();
            Member author = message.getMember();

            if (author.hasPermission(Permission.MESSAGE_MANAGE) || author.getId().equals(PropertyProvider.getProperty("bot.master"))) {
                String msg = message.getContentRaw();
                String[] msgArray = msg.split(" ");
                if (msgArray[1] == null) {
                    channel.sendTyping().queue((m) -> {
                        channel.sendMessage("type that correctly please").queueAfter(5, TimeUnit.SECONDS);
                    });
                } else {
                    Guild guild = message.getGuild();
                    GuildDAO guildDAO = new GuildDAO(guild.getId());
                    if (guildDAO.getId() == 0) {
                        guildDAO.setDiscordId(guild.getId());
                        guildDAO.setName(guild.getName());
                        guildDAO.insert();
                    }
                    GlobalBannedLinkDAO bannedLink = new GlobalBannedLinkDAO(guild.getId(), msgArray[1]);
                    if (bannedLink.getId() == 0) {
                        bannedLink.setGuildId(guildDAO.getId());
                        bannedLink.setLinkText(msgArray[1]);
                        bannedLink.insert();
                        CherryChemsBot.BannedLinksSetup();
                        channel.sendTyping().queue((m) -> {
                            channel.sendMessage("guild banned link added").queueAfter(5, TimeUnit.SECONDS);
                        });
                    } else {
                        channel.sendTyping().queue((m) -> {
                            channel.sendMessage("link is already banned on this guild").queueAfter(5, TimeUnit.SECONDS);
                        });
                    }
                }
            } else {
                channel.sendTyping().queue((m) -> {
                    channel.sendMessage("nice try").queueAfter(5, TimeUnit.SECONDS);
                });
            }
        } catch (Exception e) {
            CherryChemsLogger.log("W", e.getMessage());
        }
    }

    public static void dropGlobalBannedLink(Message message) {
        try {
            MessageChannel channel = message.getChannel();
            Member author = message.getMember();

            if (author.hasPermission(Permission.MESSAGE_MANAGE) || author.getId().equals(PropertyProvider.getProperty("bot.master"))) {
                String msg = message.getContentRaw();
                String[] msgArray = msg.split(" ");
                if (msgArray[1] == null) {
                    channel.sendTyping().queue((m) -> {
                        channel.sendMessage("type that correctly please").queueAfter(5, TimeUnit.SECONDS);
                    });
                } else {
                    Guild guild = message.getGuild();
                    GuildDAO guildDAO = new GuildDAO(guild.getId());
                    if (guildDAO.getId() == 0) {
                        guildDAO.setDiscordId(guild.getId());
                        guildDAO.setName(guild.getName());
                        guildDAO.insert();
                    }

                    GlobalBannedLinkDAO bannedLink = new GlobalBannedLinkDAO(guild.getId(), msgArray[1]);
                    if (bannedLink.getId() != 0) {
                        bannedLink.delete();
                        CherryChemsBot.BannedLinksSetup();
                        channel.sendTyping().queue((m) -> {
                            channel.sendMessage("done").queueAfter(5, TimeUnit.SECONDS);
                        });
                    } else {
                        channel.sendTyping().queue((m) -> {
                            channel.sendMessage("domain isn't banned on this guild").queueAfter(5, TimeUnit.SECONDS);
                        });
                    }
                }
            } else {
                channel.sendTyping().queue((m) -> {
                    channel.sendMessage("nice try").queueAfter(5, TimeUnit.SECONDS);
                });
            }
        } catch (Exception e) {
            CherryChemsLogger.log("W", e.getMessage());
        }
    }

    public static void dropGlobalBannedLinks(Message message) {
        try {
            MessageChannel channel = message.getChannel();
            Member author = message.getMember();

            if (author.hasPermission(Permission.MESSAGE_MANAGE) || author.getId().equals(PropertyProvider.getProperty("bot.master"))) {
                String msg = message.getContentRaw();
                String[] msgArray = msg.split(" ");
                if (msgArray[0] == null) {
                    channel.sendTyping().queue((m) -> {
                        channel.sendMessage("type that correctly please").queueAfter(5, TimeUnit.SECONDS);
                    });
                } else {
                    Guild guild = message.getGuild();
                    GuildDAO guildDAO = new GuildDAO(guild.getId());
                    if (guildDAO.getId() == 0) {
                        guildDAO.setDiscordId(guild.getId());
                        guildDAO.setName(guild.getName());
                        guildDAO.insert();
                    }
                    String guildDiscordId = message.getGuild().getId();
                    if (GlobalBannedLinkDAO.isBannedLinksByGuild(guildDiscordId)) {
                        GlobalBannedLinkDAO.deleteBannedLinksByGuild(guildDiscordId);
                        CherryChemsBot.BannedLinksSetup();
                        channel.sendTyping().queue((m) -> {
                            channel.sendMessage("done").queueAfter(5, TimeUnit.SECONDS);
                        });
                    } else {
                        channel.sendTyping().queue((m) -> {
                            channel.sendMessage("no domain links banned on this guild").queueAfter(5, TimeUnit.SECONDS);
                        });
                    }
                }
            } else {
                channel.sendTyping().queue((m) -> {
                    channel.sendMessage("nice try").queueAfter(5, TimeUnit.SECONDS);
                });
            }
        } catch (Exception e) {
            CherryChemsLogger.log("W", e.getMessage());
        }

    }

    public static void globalBannedLinkCheck(Message message) throws URISyntaxException {
        Guild guild = message.getGuild();
        User user = message.getAuthor();
        String msg = message.getContentRaw();
        String[] msgArray = msg.split(" ");
        List<String> msgList = Arrays.asList(msgArray);
        if (globalBannedLinksMap.containsKey(guild.getId())) {
            List<String> links = (List<String>) globalBannedLinksMap.get(guild.getId());
            HashSet<String> tmp = new HashSet<String>();
            Boolean delete = false;
            for (String el : msgList) {
                tmp.add(getDomainName(el));
            }
            for (String el : links) {
                if (tmp.contains(getDomainName(el))) {
                    delete = true;
                }
            }
            if (delete) {
                message.delete().queue();
            }
        }
    }

    public static void hasGlobalBannedLinks(Message message) {
        MessageChannel channel = message.getChannel();
        Guild guild = message.getGuild();

        String msgPrint = "Guild has the following links banned:\n";

        if (globalBannedLinksMap.containsKey(guild.getId())) {
            List<String> links = (List<String>) globalBannedLinksMap.get(guild.getId());
            Iterator it = links.iterator();
            while (it.hasNext()) {
                String link = (String) it.next();
                msgPrint += link + "\n";
            }
            channel.sendTyping().complete();
            channel.sendMessage(msgPrint).completeAfter(5, TimeUnit.SECONDS);
        } else {
            channel.sendTyping().queue((m) -> {
                channel.sendMessage("no global domain link bans on this guild").queueAfter(5, TimeUnit.SECONDS);
            });
        }
    }

    // Awaits
    public static void awaitResponse(String responseType, String[] arguments) {
        switch (responseType) {
            case "awaitPunishOverwrite":
                awaitPunishOverwrite(arguments);
                break;
        }
    }

    public static void awaitPunishOverwrite(String[] arguments) {
        awaitUserDiscordId = arguments[0];
        awaitPunishedUserId = Integer.valueOf(arguments[1]);
        awaitNewRoleDiscordId = arguments[2];

        System.out.println("punishid" + awaitPunishedUserId);
    }

    public static void satisfyPunishOverwrite(Message message) {
        if (awaitPunishedUserId == 0) {
            MessageChannel channel = message.getChannel();
            channel.sendTyping().queue((m) -> {
                channel.sendMessage("sorry I dont remember asking you about overwriting a punishment....").queueAfter(5, TimeUnit.SECONDS);
            });
        } else {
            User author = message.getAuthor();
            if (author.getId().equals(awaitUserDiscordId)) {
                PunishedUserDAO punishedUser = new PunishedUserDAO(awaitPunishedUserId);
                RoleDAO role = new RoleDAO(awaitNewRoleDiscordId);
                punishedUser.setRoleId(role.getId());
                punishedUser.update();
                awaitUserDiscordId = "";
                awaitPunishedUserId = 0;
                awaitNewRoleDiscordId = "";
                MessageChannel channel = message.getChannel();
                channel.sendTyping().queue((m) -> {
                    channel.sendMessage("sending user to a new camp").queueAfter(5, TimeUnit.SECONDS);
                });
            } else {
                MessageChannel channel = message.getChannel();
                channel.sendTyping().queue((m) -> {
                    channel.sendMessage("I don't remember asking you...").queueAfter(5, TimeUnit.SECONDS);
                });
            }
        }
    }

    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = null;
        String domain = "N/AAAAA";
        try {
            if (!url.startsWith("http") && !url.startsWith("https")) {
                url = "http://" + url;
            }
            uri = new URI(url);
            domain = uri.getHost();
        } catch (URISyntaxException e) {
            domain = "N/AAAAA";
            throw e;
        } catch (NullPointerException e) {
            domain = "N/AAAAA";
        }
        if (domain == null) {
            domain = "N/AAAAA";
        }
        return domain.startsWith("www.") ? domain.substring(4) : domain;

    }

    // Getters
    // Setters
}
