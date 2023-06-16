package me.xjuppo.easymail.mail;

import com.google.gson.Gson;
import me.xjuppo.easymail.items.MailBook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

public class MailManager {

    public static final String mailsBasePath = "easymail/mails/";
    public static final String blocksBasePath = "easymail/blocks/";

    public static void registerMail(MailBook mailBook) {

        File receiverDir = new File(mailsBasePath + mailBook.getReceiverUUID());
        File senderDir = new File(mailsBasePath + mailBook.getSenderUUID());

        if (!receiverDir.exists()) {
            receiverDir.mkdirs();
        }
        if (!senderDir.exists()) {
            senderDir.mkdirs();
        }

        File mailReceivedFile = new File(mailsBasePath + mailBook.getReceiverUUID()
                + String.format("/%s_%s.json", mailBook.getCreationDate(), mailBook.getCreationTime().toSecondOfDay()));
        File mailSentFile = new File(mailsBasePath + mailBook.getSenderUUID()
                + String.format("/%s_%s.json", mailBook.getCreationDate(), mailBook.getCreationTime().toSecondOfDay()));

        try {
            mailSentFile.createNewFile();
            mailReceivedFile.createNewFile();
        }
        catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't save mail: " + mailBook.getTitle());
            return;
        }

        Gson gson = new Gson();
        String jsonString = gson.toJson(mailBook);

        try {
            FileWriter sentWriter = new FileWriter(mailSentFile);
            FileWriter receivedWriter = new FileWriter(mailReceivedFile);

            sentWriter.write(jsonString);
            receivedWriter.write(jsonString);

            sentWriter.close();
            receivedWriter.close();
        }
        catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't save mail: " + mailBook.getTitle());
            return;
        }

    }

    public static List<MailBook> getMails(UUID playerUUID) {
        File mailDir = new File(mailsBasePath + playerUUID.toString() + "/");
        if (!mailDir.exists()) {
            mailDir.mkdirs();
        }
        String[] mails = mailDir.list();
        List<MailBook> mailBooks = new ArrayList<>();
        for (String fileName : mails) {
            File mailFile = new File(String.format(mailsBasePath + "%s/%s", playerUUID, fileName));
            StringBuilder content = new StringBuilder();
            try {
                Scanner scanner = new Scanner(mailFile);
                while (scanner.hasNextLine()) {
                    content.append(scanner.nextLine());
                }
                scanner.close();
            }
            catch (FileNotFoundException e) {
                continue;
            }
            Gson gson = new Gson();
            mailBooks.add(gson.fromJson(content.toString(), MailBook.class));
        }

        return mailBooks;
    }

    public static List<MailBook> getReceivedMails(UUID playerUUID) {
        List<MailBook> mailBooks = getMails(playerUUID);
        List<MailBook> receivedMails = new ArrayList<>();
        for (MailBook mailBook : mailBooks) {
            if (mailBook.getReceiverUUID().equals(playerUUID.toString())) {
                receivedMails.add(mailBook);
            }
        }
        return receivedMails;
    }

    public static List<MailBook> getSentMails(UUID playerUUID) {
        List<MailBook> mailBooks = getMails(playerUUID);
        List<MailBook> sentMails = new ArrayList<>();
        for (MailBook mailBook : mailBooks) {
            if (mailBook.getSenderUUID().equals(playerUUID.toString())) {
                sentMails.add(mailBook);
            }
        }
        return sentMails;
    }

    public static MailBook getMailBook(ItemStack itemStack, Player player) {
        List<MailBook> mailBooks = MailManager.getMails(player.getUniqueId());

        MailBook clickedMailBook = null;
        for (MailBook mail : mailBooks) {
            BookMeta mailMeta = (BookMeta) mail.getItemStack().getItemMeta();
            BookMeta itemMeta = (BookMeta) itemStack.getItemMeta();
            if (mailMeta.getPages().equals(itemMeta.getPages())) {
                clickedMailBook = mail;
            }
        }

        return clickedMailBook;
    }

    public static void removeMailBook(MailBook mailBook) {
        File senderFile = new File(String.format(mailsBasePath + "%s/%s_%s.json", mailBook.getSenderUUID(),
                mailBook.getCreationDate().toString(), mailBook.getCreationTime().toSecondOfDay()));
        File receiverFile = new File(String.format(mailsBasePath + "%s/%s_%s.json", mailBook.getReceiverUUID(),
                mailBook.getCreationDate().toString(), mailBook.getCreationTime().toSecondOfDay()));

        senderFile.delete();
        receiverFile.delete();
    }

    public static void blockPlayer(UUID blocker, UUID blocked) {
        File playerDir = new File(blocksBasePath);
        if (!playerDir.exists()) {
            playerDir.mkdirs();
        }
        File playerFile = new File(blocksBasePath + String.format("%s.json", blocker.toString()));

        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            }
            catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Couldn't create file for player with UUID " + blocker.toString());
                return;
            }
        }

        StringBuilder jsonContent = new StringBuilder();

        try {
            Scanner playerFileScanner = new Scanner(playerFile);
            while (playerFileScanner.hasNextLine()) {
                jsonContent.append(playerFileScanner.nextLine());
            }
            playerFileScanner.close();
        }
        catch (FileNotFoundException e) {
            Bukkit.getLogger().log(Level.WARNING, "Couldn't read file " + playerFile.getName());
            return;
        }

        Gson gson = new Gson();

        BlockPlayerList blockPlayerList = gson.fromJson(jsonContent.toString(), BlockPlayerList.class);

        if (blockPlayerList == null) {
            blockPlayerList = new BlockPlayerList(blocker.toString(), new ArrayList<>());
        }

        if (!blockPlayerList.blockedPlayers.contains(blocked.toString())) {
            blockPlayerList.blockedPlayers.add(blocked.toString());
        }

        try {
            FileWriter fileWriter = new FileWriter(playerFile);
            fileWriter.write(gson.toJson(blockPlayerList));
            fileWriter.close();
        }
        catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, String.format("Couldn't write to file %s", playerFile.getName()));
        }
    }

    public static void unblockPlayer(UUID player, UUID playerToUnblock) {
        File file = new File(blocksBasePath + String.format("%s.json", player));

        if (!file.exists()) {
            return;
        }

        StringBuilder jsonString = new StringBuilder();
        try {
            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNextLine()) {
                jsonString.append(fileScanner.nextLine());
            }
            fileScanner.close();
        }
        catch (FileNotFoundException e) {
            Bukkit.getLogger().log(Level.WARNING, "Couldn't read file " + player);
            return;
        }

        Gson gson = new Gson();
        BlockPlayerList blockPlayerList = gson.fromJson(jsonString.toString(), BlockPlayerList.class);

        if (blockPlayerList.blockedPlayers.contains(playerToUnblock.toString())) {
            blockPlayerList.blockedPlayers.remove(playerToUnblock.toString());
        }

        String jsonToWrite = gson.toJson(blockPlayerList);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonToWrite);
            fileWriter.close();
        }
        catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Couldn't write to file " + file.getName());
        }
    }

    public static List<String> getBlocked(UUID playerUUID) {
        File file = new File(String.format(blocksBasePath + "%s.json", playerUUID.toString()));

        if (!file.exists()) {
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        StringBuilder jsonString = new StringBuilder();
        try {
            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNextLine()) {
                jsonString.append(fileScanner.nextLine());
            }
            fileScanner.close();
        }
        catch (FileNotFoundException e) {
            Bukkit.getLogger().log(Level.WARNING, "Couldn't read file " + playerUUID);
            return new ArrayList<>();
        }

        BlockPlayerList blockPlayerList = gson.fromJson(jsonString.toString(), BlockPlayerList.class);

        if (blockPlayerList == null) {
            return new ArrayList<>();
        }

        return  blockPlayerList.blockedPlayers;
    }
}
