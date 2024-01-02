/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord;

import com.hypherionmc.sdlink.core.config.SDLinkConfig;
import com.hypherionmc.sdlink.core.discord.commands.CommandManager;
import com.hypherionmc.sdlink.core.discord.events.DiscordEventHandler;
import com.hypherionmc.sdlink.core.managers.DatabaseManager;
import com.hypherionmc.sdlink.core.managers.EmbedManager;
import com.hypherionmc.sdlink.core.managers.WebhookManager;
import com.hypherionmc.sdlink.core.services.SDLinkPlatform;
import com.hypherionmc.sdlink.core.util.EncryptionUtil;
import com.hypherionmc.sdlink.core.util.ThreadedEventManager;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author HypherionSA
 * The main Discord Bot class. This controls everything surrounding the bot itself
 */
public class BotController {

    // Public instance of this class that can be called anywhere
    public static BotController INSTANCE;

    // Thread Execution Manager
    public static final ScheduledExecutorService taskManager = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    // Required Variables
    private JDA _jda;
    private final EventWaiter eventWaiter = new EventWaiter();
    private final Logger logger;

    /**
     * Construct a new instance of this class
     * @param logger A constructed {@link Logger} that the bot will use
     */
    public static void newInstance(Logger logger) {
        if (INSTANCE != null) {
            INSTANCE.shutdownBot(false);
        }
        new BotController(logger);
    }

    /**
     * INTERNAL
     * @param logger A constructed {@link Logger} that the bot will use
     */
    private BotController(Logger logger) {
        INSTANCE = this;
        this.logger = logger;

        File newConfigDir = new File("./config/simple-discord-link");
        if (!newConfigDir.exists())
            newConfigDir.mkdirs();

        File oldConfig = new File("./config/simple-discord-link.toml");
        if (oldConfig.exists()) {
            try {
                FileUtils.moveFile(oldConfig, new File(newConfigDir.getAbsolutePath() + File.separator + "simple-discord-link.toml"));
            } catch (Exception e) {
                logger.error("Failed to move config file to new location", e);
            }
        }

        // Initialize Config
        new SDLinkConfig();

        // Initialize Account Storage
        DatabaseManager.initialize();

        // Initialize Webhook Clients
        WebhookManager.init();

        // Initialize Embeds
        EmbedManager.init();
    }

    /**
     * Start the bot and handle all the startup work
     */
    public void initializeBot() {
        if (SDLinkConfig.INSTANCE == null || !SDLinkConfig.hasConfigLoaded) {
            logger.error("Failed to load config. Check your log for errors");
            return;
        }

        if (SDLinkConfig.INSTANCE.botConfig.botToken.isEmpty()) {
            logger.error("Missing bot token. Mod will be disabled");
            return;
        }

        if (!SDLinkConfig.INSTANCE.generalConfig.enabled)
            return;

        try {
            String token = EncryptionUtil.INSTANCE.decrypt(SDLinkConfig.INSTANCE.botConfig.botToken);
            _jda = JDABuilder.createLight(
                            token,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_MESSAGE_REACTIONS
                    )
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setBulkDeleteSplittingEnabled(true)
                    .setEventManager(new ThreadedEventManager())
                    .build();

            // Setup Commands
            CommandClientBuilder clientBuilder = new CommandClientBuilder();
            clientBuilder.setOwnerId("354707828298088459");
            clientBuilder.setHelpWord("help");
            clientBuilder.useHelpBuilder(false);
            clientBuilder.setActivity(null);
            //clientBuilder.forceGuildOnly(750990873311051786L);

            CommandClient commandClient = clientBuilder.build();
            CommandManager.INSTANCE.register(commandClient);

            // Register Event Handlers
            _jda.addEventListener(commandClient, eventWaiter, new DiscordEventHandler());
            _jda.setAutoReconnect(true);

        } catch (Exception e) {
            logger.error("Failed to connect to discord", e);
        }
    }

    /**
     * Check if the bot is in a state to send messages to discord
     */
    public boolean isBotReady() {
        if (SDLinkConfig.INSTANCE == null)
            return false;

        if (!SDLinkConfig.INSTANCE.generalConfig.enabled)
            return false;

        if (_jda == null)
            return false;

        if (_jda.getStatus() == JDA.Status.SHUTTING_DOWN || _jda.getStatus() == JDA.Status.SHUTDOWN)
            return false;

        return _jda.getStatus() == JDA.Status.CONNECTED;
    }

    /**
     * Shutdown the Bot, without forcing a shutdown
     */
    public void shutdownBot() {
        this.shutdownBot(true);
    }

    /**
     * Shutdown the Bot, optionally forcing a shutdown
     * @param forced Should the shutdown be forced
     */
    public void shutdownBot(boolean forced) {
        if (_jda != null) {
            _jda.shutdown();
        }

        WebhookManager.shutdown();

        if (forced) {
            // Workaround for Bot thread hanging after server shutdown
            taskManager.schedule(() -> {
                taskManager.shutdownNow();
                System.exit(1);
            }, 10, TimeUnit.SECONDS);
        }
    }

    /**
     * Ensure that whitelisting is set up properly, so the bot can use the feature
     */
    public void checkWhiteListing() {
        if (SDLinkConfig.INSTANCE.accessControl.enabled && !SDLinkPlatform.minecraftHelper.checkWhitelisting().isError()) {
            getLogger().error("SDLink Access Control is enabled, but so is whitelisting on your server. You need to disable whitelisting to use the AccessControl feature");
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public JDA getJDA() {
        return this._jda;
    }

    public EventWaiter getEventWaiter() {
        return eventWaiter;
    }
}
