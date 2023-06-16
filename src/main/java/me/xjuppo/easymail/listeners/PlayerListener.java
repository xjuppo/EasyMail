package me.xjuppo.easymail.listeners;

import me.xjuppo.easymail.items.MailBook;
import me.xjuppo.easymail.mail.MailManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        List<MailBook> receivedMails = MailManager.getReceivedMails(event.getPlayer().getUniqueId());
        if (receivedMails.size() > 0) {
            event.getPlayer().sendMessage(String.format("You have %s mail to read! Use the command /easymail open to check them out!", receivedMails.size()));
        }
    }
}
