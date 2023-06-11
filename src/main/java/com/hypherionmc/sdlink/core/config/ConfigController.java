/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.config;

/**
 * @author HypherionSA
 * Main Config class for Loading, Saving and Upgrading configs
 */
/*@NoConfigScreen
public class ConfigController extends ModuleConfig {

    // Private internal variables
    public static transient int configVer = 1;

    // Instance of the currently loaded config
    public static transient SDLinkConfig sdLinkConfig;

    public ConfigController() {
        super("sdlink", "simple-discord-link");
        initConfig();
    }


    @Override
    public void registerAndSetup(ModuleConfig conf) {
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

        if (this.getConfigPath().exists() && this.getConfigPath().length() >= 2L) {
            this.migrateConfig(conf);
        } else {
            saveConfig(new SDLinkConfig());
            performEncryption();
        }

        com.hypherionmc.craterlib.core.config.ConfigController.register_config(this);
        this.configReloaded();
    }


    private void loadConfig() {
        ObjectConverter converter = new ObjectConverter();
        CommentedFileConfig config = CommentedFileConfig.builder(configFile).build();
        config.load();
        sdLinkConfig = converter.toObject(config, SDLinkConfig::new);
        config.close();
    }


    public void saveConfig(Object conf) {
        ObjectConverter converter = new ObjectConverter();
        CommentedFileConfig config = CommentedFileConfig.builder(configFile).build();

        converter.toConfig(conf, config);
        config.save();
        config.close();
    }


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
}*/
