/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.events;

import com.hypherionmc.sdlink.core.discord.BotController;
import com.hypherionmc.sdlink.core.discord.commands.slash.general.ServerStatusSlashCommand;
import com.hypherionmc.sdlink.core.discord.hooks.BotReadyHooks;
import com.hypherionmc.sdlink.core.discord.hooks.DiscordMessageHooks;
import com.hypherionmc.sdlink.core.managers.ChannelManager;
import com.hypherionmc.sdlink.core.managers.PermissionChecker;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author HypherionSA
 * Class to provide Hooks for Discord Events, such as message received, and login
 * NOTE TO DEVELOPERS: Don't add ANY LOGIC IN HERE. Rather implement it in a seperate class,
 * and use these hooks to trigger that code
 */
public class DiscordEventHandler extends ListenerAdapter {

    /**
     * The bot received a message
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isWebhookMessage())
            return;

        if (event.getAuthor() == event.getJDA().getSelfUser())
            return;

        if (!event.isFromGuild())
            return;

        DiscordMessageHooks.discordMessageEvent(event);
    }

    /**
     * The bot is connected to discord and ready to begin sending messages
     */
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (event.getJDA().getStatus() == JDA.Status.CONNECTED) {
            BotController.INSTANCE.getLogger().info("Successfully connected to discord");

            PermissionChecker.checkBotSetup();
            ChannelManager.loadChannels();
            BotReadyHooks.startActivityUpdates(event);
            BotReadyHooks.startTopicUpdates();
        }
    }

    /**
     * A button was clicked.
     */
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().equals("sdrefreshbtn")) {
            event.getMessage().editMessageEmbeds(ServerStatusSlashCommand.runStatusCommand()).queue();
            event.reply("Success!").setEphemeral(true).queue();
        }
    }
}
