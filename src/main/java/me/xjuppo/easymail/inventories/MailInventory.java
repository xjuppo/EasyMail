package me.xjuppo.easymail.inventories;

import me.xjuppo.easymail.EasyMail;
import me.xjuppo.easymail.items.MailBook;
import me.xjuppo.easymail.mail.MailManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.logging.Level;

public class MailInventory {

    Player player;
    Inventory inv;
    ItemStack placeHolder;

    ItemStack navigationButton;

    ItemStack clearButton;

    public int sentStart = 0, receivedStart = 0;

    public List<MailBook> receivedMails, sentMails;

    public MailInventory(Player player) {
        this.player = player;

        this.inv = Bukkit.createInventory(player, 36, "MailBox");

        setupItems();

        updateInventory();

        EasyMail.playerInventories.put(player.getUniqueId(), this);
    }

    public void setupItems() {
        placeHolder = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemMeta placeHolderMeta = placeHolder.getItemMeta();
        placeHolder.setItemMeta(placeHolderMeta);

        placeHolderMeta.setDisplayName("Received");
        placeHolder.setItemMeta(placeHolderMeta);

        for (int i = 0; i < 9; i++) {
            this.inv.setItem(i, placeHolder);
        }

        placeHolderMeta.setDisplayName("Sent");
        placeHolder.setItemMeta(placeHolderMeta);

        for (int i = 18; i < 27; i++) {
            this.inv.setItem(i, placeHolder);
        }

        this.navigationButton = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta navigationButtonMeta = navigationButton.getItemMeta();

        navigationButtonMeta.setDisplayName(">>");
        navigationButton.setItemMeta(navigationButtonMeta);

        this.inv.setItem(8, navigationButton);
        this.inv.setItem(26, navigationButton);

        navigationButtonMeta.setDisplayName("<<");
        navigationButton.setItemMeta(navigationButtonMeta);

        this.inv.setItem(0, navigationButton);
        this.inv.setItem(18, navigationButton);

        this.clearButton = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemMeta clearButtonMeta = clearButton.getItemMeta();
        clearButtonMeta.setDisplayName("Pick all");
        clearButton.setItemMeta(clearButtonMeta);

        this.inv.setItem(4, clearButton);
        this.inv.setItem(22, clearButton);
    }

    public void updateInventory() {
        List<MailBook> receivedMails = MailManager.getReceivedMails(player.getUniqueId());
        List<MailBook> sentMails = MailManager.getSentMails(player.getUniqueId());

        this.receivedMails = receivedMails;
        this.sentMails = sentMails;

        List<MailBook> receivedMailsSub;

        if ((receivedMails.size() - 1 - this.receivedStart) < 9) {
            receivedMailsSub = receivedMails.subList(this.receivedStart, receivedMails.size());
        }
        else {
            receivedMailsSub = receivedMails.subList(this.receivedStart, this.receivedStart+9);
        }

        List<MailBook> sentMailsSub;

        if ((sentMails.size() - 1 - this.sentStart) < 9) {
            sentMailsSub = sentMails.subList(this.sentStart, sentMails.size());
        }
        else {
            sentMailsSub = sentMails.subList(this.sentStart, this.sentStart+9);
        }

        receivedMailsSub.forEach((mail) -> {
            ItemStack book = mail.getItemStack();
            book.setAmount(receivedMails.indexOf(mail)+1);
            this.inv.setItem(9 + receivedMailsSub.indexOf(mail), book);
        });

        sentMailsSub.forEach((mail) -> {
            ItemStack book = mail.getItemStack();
            book.setAmount(sentMails.indexOf(mail)+1);
            this.inv.setItem(27 + sentMailsSub.indexOf(mail), book);
        });
    }

    public Inventory getInv() {
        return inv;
    }
}
