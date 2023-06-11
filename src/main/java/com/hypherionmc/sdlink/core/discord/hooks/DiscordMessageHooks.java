/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.hooks;

import com.hypherionmc.sdlink.core.config.SDLinkConfig;
import com.hypherionmc.sdlink.core.discord.BotController;
import com.hypherionmc.sdlink.core.managers.ChannelManager;
import com.hypherionmc.sdlink.core.messaging.MessageDestination;
import com.hypherionmc.sdlink.core.services.SDLinkPlatform;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author HypherionSA
 * Hook class to handle messages the bot receives
 */
public class DiscordMessageHooks {

    /**
     * Chat messages to be sent back to discord
     */
    public static void discordMessageEvent(MessageReceivedEvent event) {
        try {
            if (event.getChannel().getIdLong() != ChannelManager.getDestinationChannel(MessageDestination.CHAT).getIdLong())
                return;

            if (event.getAuthor().isBot() && SDLinkConfig.INSTANCE.chatConfig.ignoreBots)
                return;

            if (SDLinkConfig.INSTANCE.generalConfig.debugging) {
                BotController.INSTANCE.getLogger().info("Sending Message from {}: {}", event.getAuthor().getName(), event.getMessage().getContentStripped());
            }
            SDLinkPlatform.minecraftHelper.discordMessageReceived(event.getMember().getEffectiveName(), event.getMessage().getContentRaw());
        } catch (Exception e) {
            if (SDLinkConfig.INSTANCE.generalConfig.debugging) {
                e.printStackTrace();
            }
        }
    }

}
