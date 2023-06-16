package me.xjuppo.easymail.mail;

import java.util.ArrayList;
import java.util.List;

public class BlockPlayerList {

    String blockerUUID;

    List<String> blockedPlayers;

    public BlockPlayerList(String blockerUUID, List<String> blockedPlayers) {
        this.blockerUUID = blockerUUID;
        this.blockedPlayers = blockedPlayers;
    }
}
