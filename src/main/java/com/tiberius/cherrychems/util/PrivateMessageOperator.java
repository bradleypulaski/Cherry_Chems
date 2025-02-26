package com.tiberius.cherrychems.util;

import com.tiberius.cherrychems.Main;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author tiberius
 */
public class PrivateMessageOperator {

    private static Guild selectedGuild;
    private static TextChannel selectedChannel;
    private static String currentSelector;

    public static void process(PrivateChannel channel, Message message) {
        User author = message.getAuthor();

        String msg = message.getContentDisplay();
        String[] array = msg.split(" ");
        String argument = array[0];

        if (author.getId().equals(PropertyProvider.getProperty("bot.master")) && argument.substring(0, 1).equals("~")) {

            if (StringUtils.isNumeric(argument.replace("~", ""))) {
                Integer option = Integer.parseInt(argument.replace("~", ""));
                runSelector(channel, option);
            }

            switch (argument) {
                case "~guild":
                    reportGuilds(channel);
                    break;
                case "~channel":
                    reportChannels(channel);
                    break;
                case "~say":
                    say(channel, array);
                    break;
            }
        }
    }

    private static void runSelector(PrivateChannel channel, Integer option) {
        String selector = currentSelector;
        JDA jda = Main.getJDA();
        List<Guild> guilds = jda.getGuilds();

        switch (selector) {
            case "Guild":
                selectedGuild = guilds.get(option - 1);
                reportChannels(channel);
                break;
            case "Channel":
                List<TextChannel> channels = selectedGuild.getTextChannels();
                selectedChannel = channels.get(option - 1);
                channel.sendMessage("ok " + selectedChannel.getName() + " was selected").queue();
                break;
        }
    }

    private static void reportGuilds(PrivateChannel channel) {
        JDA jda = Main.getJDA();
        List<Guild> guilds = jda.getGuilds();
        Iterator it = guilds.iterator();
        Integer i = 1;
        channel.sendMessage("Please Select Guild").queue();
        currentSelector = "Guild";

        String response = "";

        while (it.hasNext()) {
            Guild guild = (Guild) it.next();
            response += "~" + i + ": " + guild.getName() + "\n";
            i++;
        }
        channel.sendMessage(response).queue();
    }

    private static void reportChannels(PrivateChannel channel) {
        try {
            List<TextChannel> channels = selectedGuild.getTextChannels();
            Iterator it = channels.iterator();
            channel.sendMessage("Please Select Channel").queue();
            currentSelector = "Channel";
            Integer i = 1;
            String response = "";
            while (it.hasNext()) {
                TextChannel ch = (TextChannel) it.next();
                response += "~" + i + ": " + ch.getName() + "\n";
                i++;
            }
            channel.sendMessage(response).queue();
        } catch (Exception e) {
            CherryChemsLogger.log("W", "Error: no guild selected. no channels found");
        }
    }

    private static void say(PrivateChannel channel, String[] array) {

        String[] truncated = Arrays.copyOfRange(array, 1, array.length);

        String message = String.join(" ", truncated);

        selectedChannel.sendTyping().queue((m) -> {
            selectedChannel.sendMessage(message).queueAfter(5, TimeUnit.SECONDS);
        });

        channel.sendMessage("sure thing boss").queue();
    }

}
