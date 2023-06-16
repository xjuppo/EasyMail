package me.xjuppo.easymail.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MailBook {
    String receiverUUID;
    String senderUUID;
    String creationDate;
    String creationTime;
    String title;
    String author;
    List<String> pages;


    public MailBook(ItemStack mailBook, UUID receiverUUID, UUID senderUUID) {
        BookMeta bookMeta = (BookMeta) mailBook.getItemMeta();
        this.receiverUUID = receiverUUID.toString();
        this.senderUUID = senderUUID.toString();
        this.creationDate = LocalDate.now().toString();
        this.creationTime = LocalTime.now().toString();
        this.pages = bookMeta.getPages();
        this.author = bookMeta.getAuthor();
        this.title = bookMeta.getTitle();
    }

    public String getReceiverUUID() {
        return receiverUUID;
    }

    public String getSenderUUID() {
        return senderUUID;
    }

    public List<String> getPages() {
        return pages;
    }

    public String getTitle() {
        return title;
    }
    public LocalDate getCreationDate() {
        return LocalDate.parse(this.creationDate);
    }

    public LocalTime getCreationTime() {
        return LocalTime.parse(this.creationTime);
    }

    public ItemStack getItemStack() {
        ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
        bookMeta.setAuthor(author);
        bookMeta.setTitle(this.title);
        bookMeta.setDisplayName(this.title);
        bookMeta.setPages(this.pages);

        List<String> lore = new ArrayList<>();
        lore.add(String.format("From: %s", this.author));
        lore.add(String.format("To: %s", Bukkit.getOfflinePlayer(UUID.fromString(this.receiverUUID)).getName()));
        lore.add(String.format("At: %s", this.getCreationDate().toString()));
        bookMeta.setLore(lore);

        writtenBook.setItemMeta(bookMeta);

        return writtenBook;
    }
}
