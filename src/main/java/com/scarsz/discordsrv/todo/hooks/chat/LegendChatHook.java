package com.scarsz.discordsrv.todo.hooks.chat;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import br.com.devpaulo.legendchat.channels.types.Channel;
import com.scarsz.discordsrv.Legacy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class LegendChatHook implements Listener {

    public LegendChatHook(){
        Legacy.hookedPlugins.add("legendchat");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMessage(ChatMessageEvent event) {
        // make sure chat channel is registered
        if (!Legacy.chatChannelIsLinked(event.getChannel().getName())) return;

        // make sure chat channel is linked to discord channel
        if (Legacy.getTextChannelFromChannelName(event.getChannel().getName()) == null) return;

        // make sure message isn't blank
        if (event.getMessage().replace(" ", "").isEmpty()) return;

        Legacy.processChatEvent(false, event.getSender().getPlayer(), event.getMessage(), event.getChannel().getName());
    }

    public static void broadcastMessageToChannel(String channelName, String message, String rawMessage) {
        Channel chatChannel = Legendchat.getChannelManager().getChannelByName(channelName);
        if (chatChannel == null) return; // no suitable channel found
        chatChannel.sendMessage(ChatColor.translateAlternateColorCodes('&', Legacy.plugin.getConfig().getString("ChatChannelHookMessageFormat")
                .replace("%channelcolor%", chatChannel.getColor())
                .replace("%channelname%", chatChannel.getName())
                .replace("%channelnickname%", chatChannel.getNickname())
                .replace("%message%", message)));

        // notify players
        List<Player> playersToNotify = new ArrayList<>();
        chatChannel.getPlayersWhoCanSeeChannel().forEach(playersToNotify::add);
        Legacy.notifyPlayersOfMentions(playersToNotify, rawMessage);
    }
    
}
