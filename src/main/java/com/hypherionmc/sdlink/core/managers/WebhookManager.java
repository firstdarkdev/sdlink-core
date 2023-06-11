/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.managers;

import club.minnced.discord.webhook.WebhookClient;
import com.hypherionmc.sdlink.core.config.SDLinkConfig;
import com.hypherionmc.sdlink.core.discord.BotController;
import com.hypherionmc.sdlink.core.messaging.MessageDestination;
import com.hypherionmc.sdlink.core.messaging.SDLinkWebhookClient;
import com.hypherionmc.sdlink.core.util.EncryptionUtil;

import java.util.HashMap;

/**
 * @author HypherionSA
 * Load and cache Webhook clients for later use
 */
public class WebhookManager {

    private static WebhookClient chatWebhookClient, eventWebhookClient, consoleWebhookClient;
    private static final HashMap<MessageDestination, WebhookClient> clientMap = new HashMap<>();

    /**
     * Load configured webhook clients
     * Webhooks that are not configured, will use their Channel ID instead
     */
    public static void init() {
        clientMap.clear();

        if (SDLinkConfig.INSTANCE == null || !SDLinkConfig.INSTANCE.channelsAndWebhooks.webhooks.enabled)
            return;

        if (!SDLinkConfig.INSTANCE.generalConfig.enabled)
            return;

        if (!SDLinkConfig.INSTANCE.channelsAndWebhooks.webhooks.chatWebhook.isEmpty()) {
            chatWebhookClient = new SDLinkWebhookClient(
                    "Chat",
                    EncryptionUtil.INSTANCE.decrypt(SDLinkConfig.INSTANCE.channelsAndWebhooks.webhooks.chatWebhook)
            ).build();
            BotController.INSTANCE.getLogger().info("Using Webhook for Chat Messages");
        }

        if (!SDLinkConfig.INSTANCE.channelsAndWebhooks.webhooks.eventsWebhook.isEmpty()) {
            eventWebhookClient = new SDLinkWebhookClient(
                    "Events",
                    EncryptionUtil.INSTANCE.decrypt(SDLinkConfig.INSTANCE.channelsAndWebhooks.webhooks.eventsWebhook)
            ).build();
            BotController.INSTANCE.getLogger().info("Using Webhook for Event Messages");
        }

        if (!SDLinkConfig.INSTANCE.channelsAndWebhooks.webhooks.consoleWebhook.isEmpty()) {
            consoleWebhookClient = new SDLinkWebhookClient(
                    "Console",
                    EncryptionUtil.INSTANCE.decrypt(SDLinkConfig.INSTANCE.channelsAndWebhooks.webhooks.consoleWebhook)
            ).build();
            BotController.INSTANCE.getLogger().info("Using Webhook for Console Messages");
        }

        if (chatWebhookClient != null) {
            clientMap.put(MessageDestination.CHAT, chatWebhookClient);
        }

        clientMap.put(MessageDestination.EVENT, eventWebhookClient);
        clientMap.put(MessageDestination.CONSOLE, consoleWebhookClient);
    }

    public static WebhookClient getWebhookClient(MessageDestination destination) {
        return clientMap.get(destination);
    }

    public static void shutdown() {
        if (chatWebhookClient != null) {
            chatWebhookClient.close();
        }
        if (eventWebhookClient != null) {
            eventWebhookClient.close();
        }
        if (consoleWebhookClient != null) {
            consoleWebhookClient.close();
        }
    }
}
