package com.scarsz.discordsrv.util;

import com.scarsz.discordsrv.Legacy;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

public class DebugHandler {

    public static String run() {
        List<String> info = new LinkedList<>();

        List<String> plugins = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
            plugins.add(plugin.getName() + " v" + plugin.getDescription().getVersion());
        Collections.sort(plugins);

        info.add("DiscordSRV debug report - generated " + new Date());
        info.add("");
        info.add("Server name: " + ChatColor.stripColor(Bukkit.getServerName()));
        info.add("Server MOTD: " + ChatColor.stripColor(Bukkit.getMotd()));
        info.add("Server players: " + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
        info.add("Server plugins: " + plugins);
        info.add("");
        info.add("Config version: " + Legacy.plugin.getConfig().getString("ConfigVersion"));
        info.add("Plugin version: " + Legacy.plugin.getDescription().getVersion() + " snapshot " + Legacy.snapshotId);
        info.add("Version: " + Bukkit.getVersion());
        info.add("Bukkit version: " + Bukkit.getBukkitVersion());
        info.add("");

        // system properties
        info.add("System properties:");
        ManagementFactory.getRuntimeMXBean().getSystemProperties().forEach((key, value) -> info.add(key + "= " + value));
        info.add("");

        // total number of processors or cores available to the JVM
        info.add("Available processors (cores): " + Runtime.getRuntime().availableProcessors());
        info.add("");

        // memory
        info.add("Free memory (MB): " + Runtime.getRuntime().freeMemory() / 1024 / 1024);
        info.add("Maximum memory (MB): " + (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "no limit" : Runtime.getRuntime().maxMemory() / 1024 / 1024));
        info.add("Total memory available to JVM (MB): " + Runtime.getRuntime().totalMemory() / 1024 / 1024);
        info.add("");

        // drive space
        File[] roots = File.listRoots();
        for (File root : roots) {
            info.add("File system " + root.getAbsolutePath());
            info.add("- Total space (MB): " + root.getTotalSpace() / 1024 / 1024);
            info.add("- Free space (MB): " + root.getFreeSpace() / 1024 / 1024);
            info.add("- Usable space (MB): " + root.getUsableSpace() / 1024 / 1024);
        }
        info.add("");

        // config.yml
        info.add("config.yml");
        FileConfiguration config = Legacy.plugin.getConfig();
        info.addAll(config.getKeys(true).stream().filter(s -> !s.equals("BotToken")).map(s -> s + ": " + config.get(s)).collect(Collectors.toList()));
        info.add("");

        // channels
        info.add("channels");
        info.add(String.valueOf(Legacy.channels));
        info.add("");

        // channel permissions
        info.add("channel permissions");
        List<TextChannel> channelsToShowPermissionInfoOf = new ArrayList<>();
        Legacy.channels.values().forEach(channelsToShowPermissionInfoOf::add);
        if (Legacy.consoleChannel != null) channelsToShowPermissionInfoOf.add(Legacy.consoleChannel);
        for (TextChannel textChannel : channelsToShowPermissionInfoOf) {
            List<String> permissions = new ArrayList<>();
            if (textChannel.checkPermission(Legacy.jda.getSelfInfo(), Permission.MESSAGE_READ)) permissions.add("read");
            if (textChannel.checkPermission(Legacy.jda.getSelfInfo(), Permission.MESSAGE_WRITE)) permissions.add("write");
            if (textChannel.checkPermission(Legacy.jda.getSelfInfo(), Permission.MANAGE_CHANNEL)) permissions.add("manage-channel");
            if (textChannel.checkPermission(Legacy.jda.getSelfInfo(), Permission.MESSAGE_MANAGE)) permissions.add("manage-messages");
            info.add(textChannel + ": " + permissions);
        }
        info.add("");

        // channels.json
        info.add("channels.json");
        try {
            FileReader fr = new FileReader(new File(Legacy.plugin.getDataFolder(), "channels.json"));
            BufferedReader br = new BufferedReader(fr);
            boolean done = false;
            while (!done)
            {
                String line = br.readLine();
                if (line != null) {
                    if (!line.startsWith("/")) info.add(line);
                } else
                    done = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        info.add("");

        // discordsrv info
        info.add("discordsrv info");
        info.add("consoleChannel: " + Legacy.consoleChannel);
        info.add("mainChatChannel: " + Legacy.chatChannel);
        info.add("unsubscribedPlayers: " + Legacy.unsubscribedPlayers);
        info.add("colors: " + Legacy.colors);
        info.add("threads: " + Arrays.asList(
                "channelTopicUpdater -> alive: " + (Legacy.channelTopicUpdater != null && Legacy.channelTopicUpdater.isAlive()),
                "serverLogWatcher -> alive: " + (Legacy.serverLogWatcher != null && Legacy.serverLogWatcher.isAlive())));
        info.add("updateIsAvailable: " + Legacy.updateIsAvailable);
        info.add("hookedPlugins: " + Legacy.hookedPlugins);
        info.add("");

        // latest.log lines
        try {
            FileReader fr = new FileReader(new File(new File("."), "logs/latest.log"));
            BufferedReader br = new BufferedReader(fr);
            info.add("Lines for DiscordSRV from latest.log:");
            boolean done = false;
            while (!done)
            {
                String line = br.readLine();
                if (line == null) done = true;
                if (line != null && line.toLowerCase().contains("discordsrv")) info.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // upload to hastebin
        StringBuilder response = new StringBuilder();
        try {
            URLConnection conn = new URL("http://hastebin.com/documents").openConnection();
            conn.setDoOutput(true);
            String str = String.join("\n", info);
            byte[] outputInBytes = str.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) response.append(inputLine);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "http://hastebin.com/" + response.toString().split("\"")[3];
    }

}
