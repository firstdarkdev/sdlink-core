/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.config;

import me.hypherionmc.moonconfig.core.CommentedConfig;
import me.hypherionmc.moonconfig.core.Config;
import me.hypherionmc.moonconfig.core.conversion.ObjectConverter;
import me.hypherionmc.moonconfig.core.file.CommentedFileConfig;
import com.hypherionmc.sdlink.core.util.EncryptionUtil;

import java.io.File;

/**
 * @author HypherionSA
 * Main Config class for Loading, Saving and Upgrading configs
 */
public class ConfigController {

    // Private internal variables
    private final File configFile;
    public static int configVer = 1;

    // Instance of the currently loaded config
    public static SDLinkConfig sdLinkConfig;

    public ConfigController() {
        File path = new File("config/");
        if (!path.exists())
            path.mkdirs();

        this.configFile = new File(path.getAbsolutePath() + File.separator + "simple-discord-link.toml");
        initConfig();
    }

    /**
     * Set up the Config File as needed.
     * This will either Create, Upgrade or load an existing config file
     */
    private void initConfig() {
        Config.setInsertionOrderPreserved(true);
        if (!configFile.exists() || configFile.length() < 10) {
            SDLinkConfig config = new SDLinkConfig();
            saveConfig(config);
            performEncryption();
        } else {
            configUpgrade();
            performEncryption();
        }
        loadConfig();
    }

    /**
     * Serialize an existing config file into an instance of {@link SDLinkConfig}
     */
    private void loadConfig() {
        ObjectConverter converter = new ObjectConverter();
        CommentedFileConfig config = CommentedFileConfig.builder(configFile).build();
        config.load();
        sdLinkConfig = converter.toObject(config, SDLinkConfig::new);
        config.close();
    }

    /**
     * Serialize an instance of {@link SDLinkConfig} to the config file
     * @param conf An instance of the config to save
     */
    public void saveConfig(Object conf) {
        ObjectConverter converter = new ObjectConverter();
        CommentedFileConfig config = CommentedFileConfig.builder(configFile).build();

        converter.toConfig(conf, config);
        config.save();
        config.close();
    }

    /**
     * Handle config structure changes between version changes
     */
    private void configUpgrade() {
        CommentedFileConfig oldConfig = CommentedFileConfig.builder(configFile).build();
        CommentedFileConfig newConfig = CommentedFileConfig.builder(configFile).build();

        newConfig.load();
        newConfig.clear();
        oldConfig.load();

        if (oldConfig.getInt("general.configVersion") == configVer) {
            newConfig.close();
            oldConfig.close();
            return;
        }

        ObjectConverter objectConverter = new ObjectConverter();
        objectConverter.toConfig(new SDLinkConfig(), newConfig);

        oldConfig.valueMap().forEach((key, value) -> {
            if (value instanceof CommentedConfig commentedConfig) {
                commentedConfig.valueMap().forEach((subKey, subValue) -> {
                    if (newConfig.contains(key + "." + subKey)) {
                        newConfig.set(key + "." + subKey, subValue);
                    }
                });
            } else {
                if (newConfig.contains(key)) {
                    newConfig.set(key, value);
                }
            }
        });

        configFile.renameTo(new File(configFile.getAbsolutePath().replace(".toml", ".old")));
        newConfig.set("general.configVersion", configVer);
        newConfig.save();
        newConfig.close();
        oldConfig.close();
    }

    /**
     * Apply encryption to Bot-Token and Webhook URLS
     */
    private void performEncryption() {
        CommentedFileConfig oldConfig = CommentedFileConfig.builder(configFile).build();
        oldConfig.load();

        String botToken = oldConfig.getOrElse("botConfig.botToken", "");
        String chatWebhook = oldConfig.getOrElse("channelsAndWebhooks.webhooks.chatWebhook", "");
        String eventsWebhook = oldConfig.getOrElse("channelsAndWebhooks.webhooks.eventsWebhook", "");
        String consoleWebhook = oldConfig.getOrElse("channelsAndWebhooks.webhooks.consoleWebhook", "");

        if (!botToken.isEmpty()) {
            botToken = EncryptionUtil.INSTANCE.encrypt(botToken);
            oldConfig.set("botConfig.botToken", botToken);
        }

        if (!chatWebhook.isEmpty()) {
            chatWebhook = EncryptionUtil.INSTANCE.encrypt(chatWebhook);
            oldConfig.set("channelsAndWebhooks.webhooks.chatWebhook", chatWebhook);
        }

        if (!eventsWebhook.isEmpty()) {
            eventsWebhook = EncryptionUtil.INSTANCE.encrypt(eventsWebhook);
            oldConfig.set("channelsAndWebhooks.webhooks.eventsWebhook", eventsWebhook);
        }

        if (!consoleWebhook.isEmpty()) {
            consoleWebhook = EncryptionUtil.INSTANCE.encrypt(consoleWebhook);
            oldConfig.set("channelsAndWebhooks.webhooks.consoleWebhook", consoleWebhook);
        }

        oldConfig.save();
        oldConfig.close();
    }
}
