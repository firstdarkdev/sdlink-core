/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.accounts;

import com.hypherionmc.sdlink.core.config.SDLinkConfig;
import com.hypherionmc.sdlink.core.services.SDLinkPlatform;

/**
 * @author HypherionSA
 * Represents a Message Author for messages sent from Minecraft to Discord
 */
public class DiscordAuthor {

    // User used for Server Messages
    public static final DiscordAuthor SERVER = new DiscordAuthor(SDLinkConfig.INSTANCE.channelsAndWebhooks.serverName, SDLinkConfig.INSTANCE.channelsAndWebhooks.serverAvatar, true);

    private final String username;
    private final String avatar;
    private final boolean isServer;

    /**
     * Internal. Use {@link #of(String, String)}
     * @param username The Username of the Author
     * @param avatar The avatar URL of the Author
     * @param isServer Is the Author the Minecraft Server
     */
    private DiscordAuthor(String username, String avatar, boolean isServer) {
        this.username = username;
        this.avatar = avatar;
        this.isServer = isServer;
    }

    /**
     * Create a new Discord Author
     * @param username The name/Username of the Author
     * @param uuid The Mojang UUID of the Author
     * @return A constructed {@link DiscordAuthor}
     */
    public static DiscordAuthor of(String username, String uuid) {
        return new DiscordAuthor(
                username,
                SDLinkConfig.INSTANCE.chatConfig.playerAvatarType.resolve(SDLinkPlatform.minecraftHelper.isOnlineMode() ? uuid : username),
                false
        );
    }

    public String getUsername() {
        return username;
    }

    public boolean isServer() {
        return isServer;
    }

    public String getAvatar() {
        return avatar;
    }
}
