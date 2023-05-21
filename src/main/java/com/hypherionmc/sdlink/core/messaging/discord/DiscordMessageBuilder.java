/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.messaging.discord;

import com.hypherionmc.sdlink.core.accounts.DiscordAuthor;
import com.hypherionmc.sdlink.core.messaging.MessageType;

/**
 * @author HypherionSA
 * Used to construct a {@link DiscordMessage} to be sent back to discord
 */
public final class DiscordMessageBuilder {

    private final MessageType messageType;
    private DiscordAuthor author;
    private String message;
    private Runnable afterSend;

    /**
     * Construct a discord message
     * @param messageType The type of message being sent
     */
    public DiscordMessageBuilder(MessageType messageType) {
        this.messageType = messageType;
    }

    /**
     * Add an Author to the message
     */
    public DiscordMessageBuilder author(DiscordAuthor author) {
        this.author = author;

        if (author.getUsername().equalsIgnoreCase("server")) {
            this.author = DiscordAuthor.SERVER;
        }

        return this;
    }

    /**
     * The Actual message that will be sent
     */
    public DiscordMessageBuilder message(String message) {
        message = message.replace("<@", "");
        message = message.replace("@everyone", "");
        message = message.replace("@here", "");
        this.message = message;
        return this;
    }

    public DiscordMessageBuilder afterSend(Runnable afterSend) {
        this.afterSend = afterSend;
        return this;
    }

    /**
     * Build a Discord Message ready to be sent
     */
    public DiscordMessage build() {
        if (this.author == null) {
            this.author = DiscordAuthor.SERVER;
        }

        if (this.message == null) {
            this.message = "";
        }

        return new DiscordMessage(this);
    }

    public String getMessage() {
        return message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public DiscordAuthor getAuthor() {
        return author;
    }

    public Runnable getAfterSend() {
        return afterSend;
    }
}
