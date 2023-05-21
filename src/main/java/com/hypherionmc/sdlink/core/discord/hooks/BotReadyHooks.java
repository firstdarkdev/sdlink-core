/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.hooks;

import com.hypherionmc.sdlink.core.discord.BotController;
import com.hypherionmc.sdlink.core.managers.ChannelManager;
import com.hypherionmc.sdlink.core.messaging.MessageDestination;
import com.hypherionmc.sdlink.core.services.SDLinkPlatform;
import com.hypherionmc.sdlink.core.util.SystemUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;

import java.util.concurrent.TimeUnit;

import static com.hypherionmc.sdlink.core.config.ConfigController.sdLinkConfig;

/**
 * @author HypherionSA
 * Hooks to run when the bot is ready
 */
public class BotReadyHooks {

    /**
     * Update the bot activity
     * @param event The {@link ReadyEvent}
     */
    public static void startActivityUpdates(ReadyEvent event) {
        BotController.taskManager.scheduleAtFixedRate(() -> {
            try {
                if (event.getJDA().getStatus() == JDA.Status.CONNECTED) {
                    Activity act = Activity.of(sdLinkConfig.botConfig.botStatus.botStatusType, sdLinkConfig.botConfig.botStatus.botStatus
                            .replace("%players%", String.valueOf(SDLinkPlatform.minecraftHelper.getPlayerCounts().getLeft()))
                            .replace("%maxplayers%", String.valueOf(SDLinkPlatform.minecraftHelper.getPlayerCounts().getRight())));

                    if (sdLinkConfig.botConfig.botStatus.botStatusType == Activity.ActivityType.STREAMING) {
                        act = Activity.of(sdLinkConfig.botConfig.botStatus.botStatusType, sdLinkConfig.botConfig.botStatus.botStatus
                                        .replace("%players%", String.valueOf(SDLinkPlatform.minecraftHelper.getPlayerCounts().getLeft()))
                                        .replace("%maxplayers%", String.valueOf(SDLinkPlatform.minecraftHelper.getPlayerCounts().getRight())),
                                sdLinkConfig.botConfig.botStatus.botStatusStreamingURL);
                    }

                    event.getJDA().getPresence().setActivity(act);
                }
            } catch (Exception e) {
                if (sdLinkConfig.generalConfig.debugging) {
                    BotController.INSTANCE.getLogger().info(e.getMessage());
                }
            }
        }, sdLinkConfig.botConfig.statusUpdateInterval, sdLinkConfig.botConfig.statusUpdateInterval, TimeUnit.SECONDS);
    }

    /**
     * Update the Chat Channel topic, if enabled
     */
    public static void startTopicUpdates() {
        if (!sdLinkConfig.botConfig.channelTopic.doTopicUpdates)
            return;

        BotController.taskManager.scheduleAtFixedRate(() -> {
            try {
                if (BotController.INSTANCE.isBotReady() && (sdLinkConfig.botConfig.channelTopic.channelTopic != null && !sdLinkConfig.botConfig.channelTopic.channelTopic.isEmpty())) {
                    StandardGuildMessageChannel channel = ChannelManager.getDestinationChannel(MessageDestination.CHAT);
                    if (channel != null) {
                        String topic = sdLinkConfig.botConfig.channelTopic.channelTopic
                                .replace("%players%", String.valueOf(SDLinkPlatform.minecraftHelper.getPlayerCounts().getLeft()))
                                .replace("%maxplayers%", String.valueOf(SDLinkPlatform.minecraftHelper.getPlayerCounts().getRight()))
                                .replace("%uptime%", SystemUtils.secondsToTimestamp(SDLinkPlatform.minecraftHelper.getServerUptime()));
                        channel.getManager().setTopic(topic).queue();
                    }
                }
            } catch (Exception e) {
                if (sdLinkConfig.generalConfig.debugging) {
                    BotController.INSTANCE.getLogger().info(e.getMessage());
                }
            }
        }, 6, 6, TimeUnit.MINUTES);
    }
}
