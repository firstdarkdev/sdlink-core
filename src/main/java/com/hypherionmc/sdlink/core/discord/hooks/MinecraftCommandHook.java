/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.hooks;

import com.hypherionmc.sdlink.core.config.SDLinkConfig;
import com.hypherionmc.sdlink.core.services.SDLinkPlatform;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MinecraftCommandHook {

    public static void discordMessageEvent(MessageReceivedEvent event) {
        if (!SDLinkConfig.INSTANCE.linkedCommands.enabled || SDLinkConfig.INSTANCE.linkedCommands.permissions.isEmpty())
            return;

        if (!event.getMessage().getContentRaw().startsWith(SDLinkConfig.INSTANCE.linkedCommands.prefix))
            return;

        Set<Long> roles = event.getMember().getRoles().stream().map(ISnowflake::getIdLong).collect(Collectors.toSet());
        roles.add(event.getMember().getIdLong());
        roles.add(0L);

        // TODO Verification

        Integer permLevel = SDLinkConfig.INSTANCE.linkedCommands.permissions.stream().filter(r -> roles.contains(Long.parseLong(r.role))).map(r -> r.permissionLevel).max(Integer::compareTo).orElse(-1);
        List<String> commands = SDLinkConfig.INSTANCE.linkedCommands.permissions.stream().filter(c -> roles.contains(Long.parseLong(c.role))).flatMap(c -> c.commands.stream()).filter(s -> !s.isEmpty()).toList();

        String raw = event.getMessage().getContentRaw().substring(SDLinkConfig.INSTANCE.linkedCommands.prefix.length());

        // TODO Verification
        if (commands.stream().anyMatch(raw::startsWith)) {
            SDLinkPlatform.minecraftHelper.executeMinecraftCommand(raw, Integer.MAX_VALUE, event, null);
        } else {
            SDLinkPlatform.minecraftHelper.executeMinecraftCommand(raw, permLevel, event, null);
        }
    }
}
