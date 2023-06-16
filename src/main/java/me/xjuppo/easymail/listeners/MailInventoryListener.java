package me.xjuppo.easymail.listeners;

import me.xjuppo.easymail.EasyMail;
import me.xjuppo.easymail.inventories.MailInventory;
import me.xjuppo.easymail.items.MailBook;
import me.xjuppo.easymail.mail.MailManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class MailInventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!EasyMail.playerInventories.keySet().contains(event.getWhoClicked().getUniqueId())) {
            return;
        }
        if (EasyMail.playerInventories.get(event.getWhoClicked().getUniqueId()).getInv() != event.getInventory()) {
            return;
        }

        event.setCancelled(true);

        ItemStack item = event.getClickedInventory().getItem(event.getSlot());
        Player player = (Player) event.getWhoClicked();

        if (item.getType() == Material.WRITTEN_BOOK) {
            MailBook clickedMailBook = MailManager.getMailBook(item, player);
            if (clickedMailBook == null) {
                return;
            }
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(ChatColor.GRAY + "Your inventory is full!");
                player.closeInventory();
                return;
            }

            MailManager.removeMailBook(clickedMailBook);
            event.getClickedInventory().remove(item);
            item.setAmount(1);
            event.getWhoClicked().getInventory().addItem(item);

            event.getWhoClicked().closeInventory();
        }

        if (item.getType() == Material.GREEN_STAINED_GLASS_PANE) {
            MailInventory inv = EasyMail.playerInventories.get(event.getWhoClicked().getUniqueId());
            if (event.getSlot() == 0) {
                if (inv.receivedStart > 0) {
                    inv.receivedStart -= 1;
                }
            }
            if (event.getSlot() == 8) {
                if (inv.receivedMails.size()-inv.receivedStart > 9) {
                    inv.receivedStart += 1;
                }
            }

            if (event.getSlot() == 18) {
                if (inv.sentStart > 0) {
                    inv.sentStart -= 1;
                }
            }
            if (event.getSlot() == 26) {
                if (inv.sentMails.size()-inv.sentStart > 9) {
                    inv.sentStart += 1;
                }
            }

            inv.updateInventory();
        }

        if (item.getType() == Material.RED_STAINED_GLASS_PANE) {
            MailInventory inv = EasyMail.playerInventories.get(event.getWhoClicked().getUniqueId());
            if (event.getSlot() == 4) {
                for (MailBook mailBook : inv.receivedMails) {
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(ChatColor.GRAY + "Your inventory is full!");
                        player.closeInventory();
                        return;
                    }
                    else {
                        player.getInventory().addItem(mailBook.getItemStack());
                    }
                    MailManager.removeMailBook(mailBook);
                }
                player.closeInventory();
            }
            if (event.getSlot() == 22) {
                for (MailBook mailBook : inv.sentMails) {
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(ChatColor.GRAY + "Your inventory is full!");
                        player.closeInventory();
                        return;
                    }
                    else {
                        player.getInventory().addItem(mailBook.getItemStack());
                    }
                    MailManager.removeMailBook(mailBook);
                }
                player.closeInventory();
            }
        }
    }
}
