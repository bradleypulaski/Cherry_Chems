package com.tiberius.cherrychems.listeners;

import com.tiberius.cherrychems.Main;
import com.tiberius.cherrychems.bot.CherryChemsBot;
import com.tiberius.cherrychems.dals.GuildDAO;
import com.tiberius.cherrychems.dals.PunishedUserDAO;
import com.tiberius.cherrychems.dals.RoleDAO;
import com.tiberius.cherrychems.dals.UserDAO;
import com.tiberius.cherrychems.util.PrivateMessageOperator;
import com.tiberius.cherrychems.util.PropertyProvider;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMemberJoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        try {
            Guild guild = event.getGuild();
            Member member = event.getMember();

            GuildDAO guildDAO = new GuildDAO(guild.getId());
            UserDAO userDAO = new UserDAO(member.getId());

            if (guildDAO.getId() == 0) {
                guildDAO.setDiscordId(guild.getId());
                guildDAO.setName(guild.getName());
                guildDAO.insert();
            }

            if (userDAO.getId() == 0) {
                userDAO.setDiscordId(member.getId());
                userDAO.setUsername(member.getUser().getName());
                userDAO.insert();
            }

            PunishedUserDAO punishedUser = new PunishedUserDAO();

            punishedUser.mapByGuildUser(guildDAO.getId(), userDAO.getId());

            if (punishedUser.getId() != 0) {
                JDA jda = Main.getJDA();
                RoleDAO role = new RoleDAO(punishedUser.getRoleId());
                guild.addRoleToMember(event.getMember(), jda.getRoleById(role.getDiscordId())).complete();
            }
        } catch (HierarchyException e) {
            MessageChannel channel = event.getGuild().getDefaultChannel();
            channel.sendTyping().queue((m) -> {
                channel.sendMessage("looks like i am not powerful enough").queueAfter(5, TimeUnit.SECONDS);
            });
        }

    }
}
