package com.tiberius.cherrychems.listeners;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 *
 * @author tiberius
 */
public class GuildVoiceMoveListener extends ListenerAdapter {

    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        // TODO: log voice channel moves.. hopefully discord and JDA gets this feature soon
    }
}
