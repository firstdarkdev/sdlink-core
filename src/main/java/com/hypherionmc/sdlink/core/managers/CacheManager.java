/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.managers;

import com.hypherionmc.sdlink.core.discord.BotController;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CacheManager {

    private static final HashMap<String, String> serverChannels = new HashMap<>();
    private static final HashMap<String, String> serverRoles = new HashMap<>();
    private static final HashMap<String, String> userCache = new HashMap<>();
    private static Set<Member> discordMembers = new HashSet<>();

    public static void loadCache() {
        loadChannelCache();
        loadRoleCache();
        loadUserCache();
    }

    public static void loadChannelCache() {
        serverChannels.clear();

        JDA jda = BotController.INSTANCE.getJDA();

        if (jda.getGuilds().isEmpty())
            return;

        jda.getGuilds().get(0).getChannels(false).forEach(c -> {
            if (c.getType() != ChannelType.CATEGORY) {
                serverChannels.put("#" + c.getName(), c.getAsMention());
            }
        });
    }

    public static void loadRoleCache() {
        serverRoles.clear();

        JDA jda = BotController.INSTANCE.getJDA();

        if (jda.getGuilds().isEmpty())
            return;

        jda.getGuilds().get(0).getRoles().forEach(r -> {
            if (r.isMentionable() && !r.isManaged()) {
                serverRoles.put("@" + r.getName(), r.getAsMention());
            }
        });
    }

    public static void loadUserCache() {
        userCache.clear();
        discordMembers.clear();

        JDA jda = BotController.INSTANCE.getJDA();

        if (jda.getGuilds().isEmpty())
            return;

        jda.getGuilds().get(0).getMembers().forEach(r -> {
            userCache.put("@" + r.getEffectiveName(), r.getAsMention());
            discordMembers.add(r);
        });
    }

    public static HashMap<String, String> getServerChannels() {
        return serverChannels;
    }

    public static HashMap<String, String> getServerRoles() {
        return serverRoles;
    }

    public static HashMap<String, String> getUserCache() {
        return userCache;
    }

    public static Set<Member> getDiscordMembers() {
        return discordMembers;
    }
}
