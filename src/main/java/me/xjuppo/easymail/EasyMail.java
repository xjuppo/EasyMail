package me.xjuppo.easymail;

import me.xjuppo.easymail.commands.CommandManager;
import me.xjuppo.easymail.inventories.MailInventory;
import me.xjuppo.easymail.listeners.MailInventoryListener;
import me.xjuppo.easymail.listeners.PlayerListener;
import me.xjuppo.easymail.tabcompleters.BaseTabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class EasyMail extends JavaPlugin {

    public static final String configPath = "config/EasyMail/";

    public static HashMap<UUID, MailInventory> playerInventories = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Register events

        this.getServer().getPluginManager().registerEvents(new MailInventoryListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        // Register commands

        CommandManager commandManager = new CommandManager(this);
        BaseTabCompleter tabCompleter = new BaseTabCompleter(commandManager);
        this.getCommand("easymail").setExecutor(commandManager);
        this.getCommand("easymail").setTabCompleter(tabCompleter);

        // Config configuration

        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
