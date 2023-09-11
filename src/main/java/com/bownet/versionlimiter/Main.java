package com.bownet.versionlimiter;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.List;

public final class Main extends Plugin implements Listener {

    public Configuration config;
    @Override
    public void onEnable() {
        // Plugin startup logic
        ProxyServer.getInstance().getPluginManager().registerListener(this, this);
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(
                    loadResource(this, "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File loadResource(Plugin plugin, String resource) {
        File folder = plugin.getDataFolder();
        if (!folder.exists())
            folder.mkdir();
        File resourceFile = new File(folder, resource);
        try {
            if (!resourceFile.exists()) {
                resourceFile.createNewFile();
                try (InputStream in = plugin.getResourceAsStream(resource);
                     OutputStream out = new FileOutputStream(resourceFile)) {
                    ByteStreams.copy(in, out);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceFile;
    }

    @EventHandler
    public void PlayerJoin(LoginEvent e) {
        int ClientVersion = e.getConnection().getVersion();
        String kickMessage = ChatColor.translateAlternateColorCodes('&', config.getString("KickMessage"));
        boolean UseWhitelist = config.getBoolean("UseAsWhitelist");
        List<Integer> versionList = config.getIntList("Versions");
        if (versionList.contains(ClientVersion)) {
            if (!UseWhitelist) {
                e.getConnection().disconnect(kickMessage);
            }
        } else {
            if (UseWhitelist) {
                e.getConnection().disconnect(kickMessage);
            }
        }
    }
}
