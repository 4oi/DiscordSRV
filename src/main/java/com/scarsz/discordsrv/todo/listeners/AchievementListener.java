package com.scarsz.discordsrv.todo.listeners;

import com.scarsz.discordsrv.Legacy;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

import java.util.LinkedList;
import java.util.List;

public class AchievementListener implements Listener {

    @EventHandler
    public void PlayerAchievementAwardedEvent(PlayerAchievementAwardedEvent event) {
        // return if achievement messages are disabled
        if (!Legacy.plugin.getConfig().getBoolean("MinecraftPlayerAchievementMessagesEnabled")) return;

        // return if achievement or player objects are fucking knackered
        if (event == null || event.getAchievement() == null || event.getPlayer() == null) return;

        // turn "SHITTY_ACHIEVEMENT_NAME" into "Shitty Achievement Name"
        List<String> achievementNameParts = new LinkedList<>();
        for (String s : event.getAchievement().toString().toLowerCase().split("_"))
            achievementNameParts.add(s.substring(0, 1).toUpperCase() + s.substring(1));
        String achievementName = String.join(" ", achievementNameParts);

        Legacy.sendMessage(Legacy.chatChannel, ChatColor.stripColor(Legacy.plugin.getConfig().getString("MinecraftPlayerAchievementMessagesFormat")
            .replace("%username%", event.getPlayer().getName())
            .replace("%displayname%", event.getPlayer().getDisplayName())
            .replace("%world%", event.getPlayer().getWorld().getName())
            .replace("%achievement%", achievementName)
        ));
    }
}
