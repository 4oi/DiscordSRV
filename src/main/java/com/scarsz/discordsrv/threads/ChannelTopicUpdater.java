package com.scarsz.discordsrv.threads;

import com.scarsz.discordsrv.DiscordSRV;
import com.scarsz.discordsrv.objects.Lag;
import com.scarsz.discordsrv.util.MemUtil;
import net.dv8tion.jda.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChannelTopicUpdater extends Thread {

    public void run() {
        int rate = DiscordSRV.plugin.getConfig().getInt("ChannelTopicUpdaterRateInSeconds") * 1000;

        while (!isInterrupted())
        {
            try {
                String chatTopic = applyFormatters(DiscordSRV.plugin.getConfig().getString("ChannelTopicUpdaterChatChannelTopicFormat"));
                String consoleTopic = applyFormatters(DiscordSRV.plugin.getConfig().getString("ChannelTopicUpdaterConsoleChannelTopicFormat"));

                if ((DiscordSRV.chatChannel == null && DiscordSRV.consoleChannel == null) || (chatTopic.isEmpty() && consoleTopic.isEmpty())) interrupt();
                if (DiscordSRV.jda == null || DiscordSRV.jda.getSelfInfo() == null) continue;

                if (!chatTopic.isEmpty() && DiscordSRV.chatChannel != null && !DiscordSRV.chatChannel.checkPermission(DiscordSRV.jda.getSelfInfo(), Permission.MANAGE_CHANNEL))
                    DiscordSRV.plugin.getLogger().warning("Unable to update chat channel; no permission to manage channel");
                if (!consoleTopic.isEmpty() && DiscordSRV.consoleChannel != null && !DiscordSRV.consoleChannel.checkPermission(DiscordSRV.jda.getSelfInfo(), Permission.MANAGE_CHANNEL))
                    DiscordSRV.plugin.getLogger().warning("Unable to update console channel; no permission to manage channel");

                if (!chatTopic.isEmpty() && DiscordSRV.chatChannel != null && DiscordSRV.chatChannel.checkPermission(DiscordSRV.jda.getSelfInfo(), Permission.MANAGE_CHANNEL))
                    DiscordSRV.chatChannel.getManager().setTopic(chatTopic).update();
                if (!consoleTopic.isEmpty() && DiscordSRV.consoleChannel != null && DiscordSRV.consoleChannel.checkPermission(DiscordSRV.jda.getSelfInfo(), Permission.MANAGE_CHANNEL))
                    DiscordSRV.consoleChannel.getManager().setTopic(consoleTopic).update();
            } catch (NullPointerException ignored) {}

            try { Thread.sleep(rate); } catch (InterruptedException ignored) {}
        }
    }

    @SuppressWarnings({"SpellCheckingInspection", "ConstantConditions"})
    private String applyFormatters(String input) {
        if (DiscordSRV.plugin.getConfig().getBoolean("PrintTiming")) DiscordSRV.plugin.getLogger().info("Format start: " + input);
        long startTime = System.nanoTime();

        Map<String, String> mem = MemUtil.get();

        input = input
                .replace("%playercount%", Integer.toString(DiscordSRV.getOnlinePlayers().size()))
                .replace("%playermax%", Integer.toString(Bukkit.getMaxPlayers()))
                .replace("%date%", new Date().toString())
                .replace("%totalplayers%", Bukkit.getWorlds().size() != 0 ? Integer.toString(new File(Bukkit.getWorlds().get(0).getWorldFolder().getAbsolutePath(), "/playerdata").listFiles(f -> {return f.getName().endsWith(".dat");}).length) : String.valueOf(0))
                .replace("%uptimemins%", Long.toString(TimeUnit.NANOSECONDS.toMinutes(System.nanoTime() - DiscordSRV.startTime)))
                .replace("%uptimehours%", Long.toString(TimeUnit.NANOSECONDS.toHours(System.nanoTime() - DiscordSRV.startTime)))
                .replace("%motd%", ChatColor.stripColor(Bukkit.getMotd().replaceAll("&([0-9a-qs-z])", "")))
                .replace("%serverversion%", Bukkit.getBukkitVersion())
                .replace("%freememory%", mem.get("freeMB"))
                .replace("%usedmemory%", mem.get("usedMB"))
                .replace("%totalmemory%", mem.get("totalMB"))
                .replace("%maxmemory%", mem.get("maxMB"))
                .replace("%freememorygb%", mem.get("freeGB"))
                .replace("%usedmemorygb%", mem.get("usedGB"))
                .replace("%totalmemorygb%", mem.get("totalGB"))
                .replace("%maxmemorygb%", mem.get("maxGB"))
                .replace("%tps%", Lag.getTPSString())
        ;

        if (DiscordSRV.plugin.getConfig().getBoolean("PrintTiming")) DiscordSRV.plugin.getLogger().info("Format done in " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + "ms: " + input);

        return input;
    }
}