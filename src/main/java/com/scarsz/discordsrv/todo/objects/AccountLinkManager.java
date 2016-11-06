package com.scarsz.discordsrv.todo.objects;

import com.google.gson.internal.LinkedTreeMap;
import com.scarsz.discordsrv.Legacy;
import com.scarsz.discordsrv.platforms.bukkit.BukkitPlatform;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"unchecked", "WeakerAccess", "unused"})
public class AccountLinkManager implements Listener {

    private File linkFile = new File(BukkitPlatform.plugin.getDataFolder(), "linkedaccounts.json");

    private HashMap<UUID, String> linkedAccounts = new HashMap<>();

    public AccountLinkManager() {
        Bukkit.getPluginManager().registerEvents(this, Legacy.plugin);
        load();
    }

    @EventHandler
    public void onPluginDisableEvent(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals(Legacy.plugin.getName())) save();
    }

    public void load() {
        if (!linkFile.exists()) return;
        linkedAccounts.clear();

        try {
            LinkedTreeMap<String, String> mapFromFile = Legacy.gson.fromJson(FileUtils.readFileToString(linkFile, Charset.defaultCharset()), LinkedTreeMap.class);
            mapFromFile.forEach((uuid, s) -> linkedAccounts.put(UUID.fromString(uuid), s));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void save() {
        try {
            HashMap<String, String> linkedAccountsStringMap = new HashMap<>();
            linkedAccounts.forEach((uuid, s) -> linkedAccountsStringMap.put(String.valueOf(uuid), s));
            FileUtils.writeStringToFile(linkFile, Legacy.gson.toJson(linkedAccountsStringMap));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getId(UUID uuid) {
        return linkedAccounts.containsKey(uuid) ? linkedAccounts.get(uuid) : null;
    }
    public UUID getUuid(String discordId) {
        UUID foundUuid = null;
        for (Map.Entry<UUID, String> entry : linkedAccounts.entrySet())
            if (entry.getValue().equals(discordId)) foundUuid = entry.getKey();
        return foundUuid;
    }

    public void link(UUID uuid, String id) {
        if (linkedAccounts.containsKey(uuid)) linkedAccounts.remove(uuid);
        linkedAccounts.put(uuid, id);
    }

    public void unlink(UUID uuid) {
        linkedAccounts.remove(uuid);
    }
    public void unlink(String id) {
        linkedAccounts.entrySet().stream().filter(entry -> entry.getValue().equals(id)).forEach(entry -> linkedAccounts.remove(entry.getKey()));
    }

}
