package de.hamze.listener;

import com.velocitypowered.api.event.*;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import de.hamze.main.MutePlugin;
import de.hamze.utilities.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.sql.ResultSet;

public class ServerChatListener {

    @Subscribe
    @SuppressWarnings("deprecation")
    public void onChatEvent(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (MutePlugin.getPlugin().muteListeners.contains(player.getUniqueId())) {
            player.sendMessage(Prefix.prefix.append(Component.text("You are muted in this server!", NamedTextColor.GRAY)));
            event.setResult(PlayerChatEvent.ChatResult.denied());
        }
    }

}