package me.xjuppo.easymail.commands;

import me.xjuppo.easymail.items.MailBook;
import me.xjuppo.easymail.mail.MailManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class SendCommand extends SubCommand {
    public SendCommand(Plugin plugin, String name, String description, String usage, String permission) {
        super(plugin, name, description, usage, permission);
    }

    @Override
    public void executeCommand(CommandSender commandSender, Command command, List<String> args) {
        if (!(commandSender instanceof Player)) {
            Bukkit.getLogger().log(Level.INFO, "This command can only be used by a player");
            return;
        }

        Player player = commandSender.getServer().getPlayer(commandSender.getName());

        ItemStack itemHeld = player.getInventory().getItemInMainHand();

        if (itemHeld.getType() != Material.WRITTEN_BOOK) {
            commandSender.sendMessage(ChatColor.GRAY + "Use this command with a written book in your main hand");
            commandSender.sendMessage(ChatColor.GRAY + this.usage);
            return;
        }

        if (args.size() == 0) {
            commandSender.sendMessage(ChatColor.GRAY + this.usage);
            return;
        }

        MailBook mail = null;

        Player receiver = Bukkit.getPlayer(args.get(0));
        if (receiver == null) {
            OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
            for (OfflinePlayer offlinePlayer : offlinePlayers) {
                if (offlinePlayer.getName().equals(args.get(0))) {
                    mail = new MailBook(itemHeld, offlinePlayer.getUniqueId(), player.getUniqueId());
                    break;
                }
            }
        }
        else {
            mail = new MailBook(itemHeld, receiver.getUniqueId(), player.getUniqueId());
        }

        if (mail == null) {
            commandSender.sendMessage( ChatColor.GRAY + "This player doesn't exist");
            return;
        }

        if (commandSender.getName().equals(receiver.getName())) {
            commandSender.sendMessage(ChatColor.GRAY + "You can't send a mail to yourself");
            return;
        }

        if (MailManager.getSentMails(player.getUniqueId()).size() >= (int) this.plugin.getConfig().get("mailbox.max_sent_mails")) {
            commandSender.sendMessage(ChatColor.GRAY + "You already sent too many mails, delete some of them or wait until they get read");
            return;
        }
        if (MailManager.getReceivedMails(UUID.fromString(mail.getReceiverUUID())).size() >= (int) this.plugin.getConfig().get("mailbox.max_received_mails")) {
            commandSender.sendMessage(ChatColor.GRAY + "This player has already received too many mails");
            return;
        }

        if (MailManager.getBlocked(player.getUniqueId()).contains(receiver.getUniqueId().toString())) {
            commandSender.sendMessage(ChatColor.RED + String.format("%s blocked you, you can't send mails to him", receiver.getName()));
            return;
        }

        commandSender.sendMessage(ChatColor.AQUA + "Mail sent!");
        player.getInventory().remove(itemHeld);
        MailManager.registerMail(mail);
    }
}
