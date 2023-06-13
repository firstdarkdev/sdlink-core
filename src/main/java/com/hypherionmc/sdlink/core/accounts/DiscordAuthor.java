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
    public static final DiscordAuthor SERVER = new DiscordAuthor(SDLinkConfig.INSTANCE.channelsAndWebhooks.serverName, SDLinkConfig.INSTANCE.channelsAndWebhooks.serverAvatar, "server", true);

    private final String displayName;
    private final String avatar;
    private final boolean isServer;
    private final String username;

    /**
     * Internal. Use {@link #of(String, String, String)}
     * @param displayName The Username of the Author
     * @param avatar The avatar URL of the Author
     * @param isServer Is the Author the Minecraft Server
     */
    private DiscordAuthor(String displayName, String avatar, String username, boolean isServer) {
        this.displayName = displayName;
        this.avatar = avatar;
        this.username = username;
        this.isServer = isServer;
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
                false
        );
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRawUsername() {
        return username;
    }

    public boolean isServer() {
        return isServer;
    }

    public String getAvatar() {
        return avatar;
    }
}
