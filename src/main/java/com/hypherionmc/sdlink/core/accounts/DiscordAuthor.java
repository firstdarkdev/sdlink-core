/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.accounts;

import com.hypherionmc.sdlink.core.config.SDLinkConfig;
import com.hypherionmc.sdlink.core.services.SDLinkPlatform;
import lombok.Getter;

/**
 * @author HypherionSA
 * Represents a Message Author for messages sent from Minecraft to Discord
 */
public class DiscordAuthor {

    // User used for Server Messages
    public static final DiscordAuthor SERVER = new DiscordAuthor(SDLinkConfig.INSTANCE.channelsAndWebhooks.serverName, SDLinkConfig.INSTANCE.channelsAndWebhooks.serverAvatar, "server", true, "");

    @Getter
    private final String displayName;

    @Getter
    private final String avatar;

    @Getter
    private final boolean isServer;

    @Getter
    private final String username;

    @Getter
    private final String uuid;

    /**
     * Internal. Use {@link #of(String, String, String)}
     * @param displayName The Username of the Author
     * @param avatar The avatar URL of the Author
     * @param isServer Is the Author the Minecraft Server
     */
    private DiscordAuthor(String displayName, String avatar, String username, boolean isServer, String uuid) {
        this.displayName = displayName;
        this.avatar = avatar;
        this.username = username;
        this.isServer = isServer;
        this.uuid = uuid;
    }

    /**
     * Create a new Discord Author
     * @param displayName The name/Username of the Author
     * @param uuid The Mojang UUID of the Author
     * @return A constructed {@link DiscordAuthor}
     */
    public static DiscordAuthor of(String displayName, String uuid, String username) {
        return new DiscordAuthor(
                displayName,
                SDLinkConfig.INSTANCE.chatConfig.playerAvatarType.resolve(SDLinkPlatform.minecraftHelper.isOnlineMode() ? uuid : username),
                username,
                false,
                SDLinkPlatform.minecraftHelper.isOnlineMode() ? uuid : username
        );
    }

    public static DiscordAuthor of(String displayName, String avatar, String username, boolean server) {
        return new DiscordAuthor(
                displayName,
                avatar,
                username,
                server,
                username
        );
    }
}
