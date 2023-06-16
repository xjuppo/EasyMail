package me.xjuppo.easymail.commands;

import me.xjuppo.easymail.mail.MailManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class BlockCommand extends SubCommand {
    public BlockCommand(Plugin plugin, String name, String description, String usage, String permission) {
        super(plugin, name, description, usage, permission);
    }

    @Override
    public void executeCommand(CommandSender commandSender, Command command, List<String> args) {

        if (args.size() < 1) {
            commandSender.sendMessage(ChatColor.GRAY + "Correct usage: " + this.usage);
            return;
        }

        List<String> blockedPlayers = MailManager.getBlocked(((Player) commandSender).getPlayer().getUniqueId());

        OfflinePlayer playerToBlock = null;

        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();

        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (offlinePlayer.getName().equals(args.get(0))) {
                playerToBlock = offlinePlayer;
                break;
            }
        }

        if (playerToBlock == null) {
            commandSender.sendMessage(ChatColor.GRAY + "This player doesn't exist");
            return;
        }

        if (playerToBlock.getName().equals(commandSender.getName())) {
            commandSender.sendMessage(ChatColor.GRAY + "You can't block yourself");
            return;
        }

        if (blockedPlayers.contains(playerToBlock.getUniqueId().toString())) {
            commandSender.sendMessage(ChatColor.GRAY + String.format("You already blocked %s", playerToBlock.getName()));
            return;
        }

        MailManager.blockPlayer(((Player) commandSender).getUniqueId(), playerToBlock.getUniqueId());
        commandSender.sendMessage(ChatColor.AQUA + String.format("You successfully blocked %s", playerToBlock.getName()));
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, Command command, String label, List<String> args) {
        return super.tabComplete(commandSender, command, label, args);
    }
}
