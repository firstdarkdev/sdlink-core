/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.messaging.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.hypherionmc.sdlink.core.accounts.DiscordAuthor;
import com.hypherionmc.sdlink.core.config.SDLinkConfig;
import com.hypherionmc.sdlink.core.config.impl.MessageChannelConfig;
import com.hypherionmc.sdlink.core.discord.BotController;
import com.hypherionmc.sdlink.core.managers.ChannelManager;
import com.hypherionmc.sdlink.core.managers.WebhookManager;
import com.hypherionmc.sdlink.core.messaging.MessageType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import org.apache.commons.lang3.tuple.Triple;

/**
 * @author HypherionSA
 * Represents a message sent from Minecraft to Discord
 * This ensures the message is properly formatted and configured
 */
public final class DiscordMessage {

    private final MessageType messageType;
    private final DiscordAuthor author;
    private final String message;
    private final Runnable afterSend;

    /**
     * Private instance. Use {@link DiscordMessageBuilder} to create an instance
     */
    DiscordMessage(DiscordMessageBuilder builder) {
        this.messageType = builder.getMessageType();
        this.author = builder.getAuthor();
        this.message = builder.getMessage();
        this.afterSend = builder.getAfterSend();
    }

    /**
     * Try to send the message to discord
     */
    public void sendMessage() {
        if (!BotController.INSTANCE.isBotReady())
            return;

        try {
            if (messageType == MessageType.CONSOLE) {
                sendConsoleMessage();
            } else {
                sendNormalMessage();
            }
        } catch (Exception e) {
            if (SDLinkConfig.INSTANCE.generalConfig.debugging) {
                BotController.INSTANCE.getLogger().error("Failed to send Discord Message", e);
            }
        }
    }

    /**
     * Send a Non Console relay message to discord
     */
    private void sendNormalMessage() {
        Triple<StandardGuildMessageChannel, WebhookClient, Boolean> channel = resolveDestination();

        // Check if a webhook is configured, and use that instead
        if (channel.getMiddle() != null && SDLinkConfig.INSTANCE.channelsAndWebhooks.webhooks.enabled) {
            WebhookMessageBuilder builder = new WebhookMessageBuilder();
            builder.setUsername(this.author.getUsername());
            if (!this.author.getAvatar().isEmpty()) {
                builder.setAvatarUrl(this.author.getAvatar());
            }

            // Message must be an Embed
            if (channel.getRight()) {
                EmbedBuilder eb = buildEmbed(false);
                WebhookEmbed web = WebhookEmbedBuilder.fromJDA(eb.build()).build();
                builder.addEmbeds(web);
            } else {
                builder.setContent(message);
            }

            channel.getMiddle().send(builder.build()).thenRun(() -> {
                if (afterSend != null)
                    afterSend.run();
            });
        } else {
            // Use the configured channel instead
            if (channel.getRight()) {
                EmbedBuilder eb = buildEmbed(true);
                channel.getLeft().sendMessageEmbeds(eb.build()).queue(success -> {
                    if (afterSend != null)
                        afterSend.run();
                });
            } else {
                channel.getLeft().sendMessage(
                                this.messageType == MessageType.CHAT ?
                                        SDLinkConfig.INSTANCE.messageFormatting.chat.replace("%player%", author.getUsername()).replace("%message%", message)
                                        : message)
                        .queue(success -> {
                            if (afterSend != null)
                                afterSend.run();
                        });
            }
        }
    }

    /**
     * Only used for console relay messages
     */
    private void sendConsoleMessage() {
        try {
            if (!BotController.INSTANCE.isBotReady() || !SDLinkConfig.INSTANCE.chatConfig.sendConsoleMessages)
                return;

            StandardGuildMessageChannel channel = ChannelManager.getConsoleChannel();
            if (channel != null) {
                channel.sendMessage(this.message).queue();
            }
        } catch (Exception e) {
            if (SDLinkConfig.INSTANCE.generalConfig.debugging) {
                BotController.INSTANCE.getLogger().error("Failed to send console message", e);
            }
        }

        if (afterSend != null)
            afterSend.run();
    }

    /**
     * Build an embed with the supplied information
     * @param withAuthor Should the author be appended to the embed. Not used for Webhooks
     */
    private EmbedBuilder buildEmbed(boolean withAuthor) {
        EmbedBuilder builder = new EmbedBuilder();

        if (withAuthor) {
            builder.setAuthor(
                    this.author.getUsername(),
                    null,
                    this.author.getAvatar().isEmpty() ? null : this.author.getAvatar()
            );
        }

        builder.setDescription(message);
        return builder;
    }

    /**
     * Figure out where the message must be delivered to, based on the config values
     */
    private Triple<StandardGuildMessageChannel, WebhookClient, Boolean> resolveDestination() {
        switch (messageType) {
            case CHAT -> {
                MessageChannelConfig.DestinationObject chat = SDLinkConfig.INSTANCE.messageDestinations.chat;
                return Triple.of(
                        ChannelManager.getDestinationChannel(chat.channel),
                        WebhookManager.getWebhookClient(chat.channel),
                        chat.useEmbed
                );
            }
            case START_STOP -> {
                MessageChannelConfig.DestinationObject startStop = SDLinkConfig.INSTANCE.messageDestinations.startStop;
                return Triple.of(
                        ChannelManager.getDestinationChannel(startStop.channel),
                        WebhookManager.getWebhookClient(startStop.channel),
                        startStop.useEmbed
                );
            }
            case JOIN_LEAVE -> {
                MessageChannelConfig.DestinationObject joinLeave = SDLinkConfig.INSTANCE.messageDestinations.joinLeave;
                return Triple.of(
                        ChannelManager.getDestinationChannel(joinLeave.channel),
                        WebhookManager.getWebhookClient(joinLeave.channel),
                        joinLeave.useEmbed
                );
            }
            case ADVANCEMENT -> {
                MessageChannelConfig.DestinationObject advancement = SDLinkConfig.INSTANCE.messageDestinations.advancements;
                return Triple.of(
                        ChannelManager.getDestinationChannel(advancement.channel),
                        WebhookManager.getWebhookClient(advancement.channel),
                        advancement.useEmbed
                );
            }
            case DEATH -> {
                MessageChannelConfig.DestinationObject death = SDLinkConfig.INSTANCE.messageDestinations.death;
                return Triple.of(
                        ChannelManager.getDestinationChannel(death.channel),
                        WebhookManager.getWebhookClient(death.channel),
                        death.useEmbed
                );
            }
            case COMMAND -> {
                MessageChannelConfig.DestinationObject command = SDLinkConfig.INSTANCE.messageDestinations.commands;
                return Triple.of(
                        ChannelManager.getDestinationChannel(command.channel),
                        WebhookManager.getWebhookClient(command.channel),
                        command.useEmbed
                );
            }
        }

        // This code should never be reached, but it's added here as a fail-safe
        MessageChannelConfig.DestinationObject chat = SDLinkConfig.INSTANCE.messageDestinations.chat;
        return Triple.of(ChannelManager.getDestinationChannel(chat.channel), WebhookManager.getWebhookClient(chat.channel), chat.useEmbed);
    }
}
