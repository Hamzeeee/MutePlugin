package de.hamze.utilities;

import com.zaxxer.hikari.HikariConfig;
import de.hamze.main.MutePlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class FileHandler {
    public static String hikariConfigPath = MutePlugin.getPlugin().dataDirectory + "/hikari.properties";
    public Logger logger = MutePlugin.getPlugin().logger;

    public void createFolder() {
        File file = new File(String.valueOf(MutePlugin.getPlugin().dataDirectory));

        if (!file.exists()) {
            logger.info("Creating folder " + file.getAbsolutePath());
            file.mkdirs();
        }
    }

    public void createHikariCPProperties() {
        File file = new File(hikariConfigPath);
        if (!file.exists()) {
            try {
                logger.info("Creating hikari.properties file " + file.getAbsolutePath());
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
