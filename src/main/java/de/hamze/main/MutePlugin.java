package de.hamze.main;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import de.hamze.commands.MuteCommand;
import de.hamze.listener.ServerChatListener;
import de.hamze.utilities.Database;
import de.hamze.utilities.FileHandler;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Plugin(id = "muteplugin",
        name = "Mute Plugin",
        authors = "Hamza Al Maliky",
        version = "0.1.0")

public class MutePlugin {

    public final ProxyServer server;
    public final Logger logger;
    public final Path dataDirectory;

    public FileHandler fileHandler;
    public static MutePlugin plugin;
    public static Database database;

    public ArrayList<UUID> muteListeners = new ArrayList<>();

    @Inject
    public MutePlugin(ProxyServer server, @NotNull Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        logger.warning("Mute Plugin started!");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getEventManager().register(this, new ServerChatListener());
        muteCommand();

        MutePlugin.plugin = this;
        fileHandler = new FileHandler();

        getFileHandler().createFolder();
        getFileHandler().createHikariCPProperties();

        database = new Database();
        database.getMutedPlayer();
    }

    public static MutePlugin getPlugin() {
        return plugin;
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }

    public Database getDatabase() {
        return database;
    }

    public void muteCommand() {
        CommandManager commandManager = server.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("mute").plugin(this).build();

        SimpleCommand simpleCommand = new MuteCommand();
        commandManager.register(commandMeta, simpleCommand);
    }
}
