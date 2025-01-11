package de.hamze.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.command.VelocityBrigadierMessage;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;
import de.hamze.main.MutePlugin;
import de.hamze.utilities.Database;
import de.hamze.utilities.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MuteCommand implements SimpleCommand {

    @Override
    public void execute(@NotNull Invocation invocation) {
        CommandSource player = invocation.source();

        ProxyServer server = MutePlugin.getPlugin().server;
        String[] args = invocation.arguments();

        if (args.length != 1) {
            player.sendMessage(Prefix.prefix.append(Component.text("Usage: /mute <player>", NamedTextColor.RED)));
            return;
        }

        @SuppressWarnings("unused")
        ScheduledTask task = server.getScheduler().buildTask(MutePlugin.getPlugin(), (selfTask) -> {
            server.getPlayer(args[0]).ifPresentOrElse(target -> commandLogic(target, player), () -> {
                player.sendMessage(Prefix.prefix.append(Component.text("Player not found", NamedTextColor.RED)));
            });
        }).schedule();
    }

    public void commandLogic(@NotNull Player target, CommandSource source) {
        Database database = MutePlugin.getPlugin().getDatabase();
        UUID uuid = target.getUniqueId();

        if (!database.playerExists(uuid)) {
            database.createPlayer(uuid);
            database.mutePlayer(uuid, true);

            target.sendMessage(Prefix.prefix.append(Component.text("You have been muted!", NamedTextColor.GREEN)));
            source.sendMessage(Prefix.prefix.append(Component.text("You have muted this player", NamedTextColor.GOLD)));

            MutePlugin.getPlugin().muteListeners.add(uuid);
            return;
        }

        boolean isMuted = database.isMuted(uuid);
        if (isMuted) {
            database.mutePlayer(uuid, false);
            target.sendMessage(Prefix.prefix.append(Component.text("You have been un-muted.", NamedTextColor.GREEN)));
            source.sendMessage(Prefix.prefix.append(Component.text("You have un-muted this player.", NamedTextColor.DARK_GREEN)));
            MutePlugin.getPlugin().muteListeners.remove(uuid);
            return;
        }

        database.mutePlayer(uuid, true);
        MutePlugin.getPlugin().muteListeners.add(uuid);
        target.sendMessage(Prefix.prefix.append(Component.text("You have been muted.", NamedTextColor.RED)));
        source.sendMessage(Prefix.prefix.append(Component.text("You have muted this player", NamedTextColor.GOLD)));
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        ProxyServer server = MutePlugin.getPlugin().server;
        List<String> players = new ArrayList<>();
        for (Player player : server.getAllPlayers()) {
            players.add(player.getUsername());
        }
        return CompletableFuture.completedFuture(players);
    }

}
