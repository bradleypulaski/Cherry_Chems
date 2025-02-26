package com.tiberius.cherrychems;

import com.tiberius.cherrychems.bot.CherryChemsBot;
import com.tiberius.cherrychems.listeners.GuildMemberJoinListener;
import com.tiberius.cherrychems.listeners.MessageListener;
import com.tiberius.cherrychems.util.CherryChemsLogger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import com.tiberius.cherrychems.util.PropertyProvider;

/**
 *
 * @author tiberius
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    private static JDA JDAGlobal;

    public static void main(String[] args) {
        CherryChemsLogger.init();
        CherryChemsLogger.log("debug", "cherry chems started....");
        PropertyProvider.init();
        
        String token = PropertyProvider.getProperty("bot.token.production");
        if (PropertyProvider.getProperty("bot.environment").equals("development")) {
            token = PropertyProvider.getProperty("bot.token.development");
        }

        CherryChemsBot cherrychems = new CherryChemsBot();

        try {
            JDA jda = new JDABuilder(AccountType.BOT).setToken(token).build();
            
            jda.addEventListener(new MessageListener());
            jda.addEventListener(new GuildMemberJoinListener());
            JDAGlobal = jda;
            System.out.println("Finished Building JDA!");
        } catch (LoginException e) {
            CherryChemsLogger.log("critical", e.getMessage());
        }
    }

    // Getters
    public static JDA getJDA() {
        return JDAGlobal;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            CherryChemsLogger.log("debug", "cherry chems shutting down....");
        } finally {
            super.finalize();
        }
    }

}
