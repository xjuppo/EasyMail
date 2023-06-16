package me.xjuppo.easymail.commands;

import me.xjuppo.easymail.inventories.MailInventory;
import me.xjuppo.easymail.mail.MailManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.logging.Level;

public class OpenCommand extends SubCommand {

    public OpenCommand(Plugin plugin, String name, String description, String usage, String permission) {
        super(plugin, name, description, usage, permission);
    }

    @Override
    public void executeCommand(CommandSender commandSender, Command command, List<String> args) {
        if (!(commandSender instanceof Player)) {
            Bukkit.getLogger().log(Level.INFO, "This command can only be used by a player");
            return;
        }

        Player player = (Player) commandSender;
        MailInventory mailInventory = new MailInventory(player);
        player.openInventory(mailInventory.getInv());
    }
}
