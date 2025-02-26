package com.tiberius.cherrychems.listeners;

import com.tiberius.cherrychems.bot.CherryChemsBot;
import com.tiberius.cherrychems.util.CherryChemsLogger;
import com.tiberius.cherrychems.util.PrivateMessageOperator;
import com.tiberius.cherrychems.util.PropertyProvider;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

    public void executeCommand(Message message) {
        String msg = message.getContentRaw();
        String[] msgComponents = msg.split(" ");
        String command = msgComponents[0];

        command = command.substring(1);
        switch (command) {
            case "help":
            case "h":
                CherryChemsBot.help(message);
                break;
            case "gibdash":
                CherryChemsBot.receiveDrugs(message, "dash");
                break;
            case "gibbuck":
                CherryChemsBot.receiveDrugs(message, "buck");
                break;
            case "gibmintals":
                CherryChemsBot.receiveDrugs(message, "mintals");
                break;
            case "gibstampede":
                CherryChemsBot.receiveDrugs(message, "stampede");
                break;
            case "punish":
                CherryChemsBot.punish(message);
                break;
            case "unpunish":
                CherryChemsBot.unPunish(message);
                break;
            case "punishoverwrite":
                CherryChemsBot.satisfyPunishOverwrite(message);
                break;
            case "banlink":
                CherryChemsBot.addBannedLink(message);
                break;
            case "unbanlink":
                CherryChemsBot.dropBannedLink(message);
                break;
            case "unbanlinks":
                CherryChemsBot.dropBannedLinks(message);
                break;
            case "hasbannedlinks":
                CherryChemsBot.hasBannedLinks(message);
                break;
            case "banguildlink":
                CherryChemsBot.addGlobalBannedLink(message);
                break;
            case "unbanguildlink":
                CherryChemsBot.dropGlobalBannedLink(message);
                break;
            case "unbanguildlinks":
                CherryChemsBot.dropGlobalBannedLinks(message);
                break;
            case "bannedguildlinks":
                CherryChemsBot.hasGlobalBannedLinks(message);
                break;
            case "version":
            case "v":
                CherryChemsBot.version(message);
                break;
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User author = event.getAuthor();               
        Message message = event.getMessage();           
        MessageChannel channel = event.getChannel();    
        String msg = message.getContentDisplay();             
        boolean bot = author.isBot();                   
        if (!bot) {
            if (event.isFromType(ChannelType.PRIVATE)) 
            {
                PrivateChannel privateChannel = event.getPrivateChannel();
                PrivateMessageOperator.process(privateChannel, message);
            }
            if (event.isFromType(ChannelType.TEXT)) 
            {
                // Commands
                String firstCharacter = msg.substring(0, 1);
                String separator = PropertyProvider.getProperty("bot.prefix.production");
                if (PropertyProvider.getProperty("bot.environment").equals("development")) {
                    separator = PropertyProvider.getProperty("bot.prefix.development");
                }
                if (firstCharacter.equals(separator)) {
                    this.executeCommand(message);
                } else {
                    // Banned Links
                    try {
                        CherryChemsBot.globalBannedLinkCheck(message);
                    } catch (URISyntaxException e) {
                        CherryChemsLogger.log("C", e.getMessage());
                    }
                    try {
                        CherryChemsBot.bannedLinkCheck(message);
                    } catch (URISyntaxException e) {
                        CherryChemsLogger.log("C", e.getMessage());
                    }
                    // Social
                    CherryChemsBot.responseCheck(message);
                }
            }
        }
    }
}
