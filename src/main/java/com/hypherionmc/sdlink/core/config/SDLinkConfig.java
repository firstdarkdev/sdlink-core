/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.config;

import me.hypherionmc.moonconfig.core.conversion.Path;
import me.hypherionmc.moonconfig.core.conversion.SpecComment;
import com.hypherionmc.sdlink.core.config.impl.*;

/**
 * @author HypherionSA
 * The main mod config Structure
 */
public class SDLinkConfig {

    @Path("general")
    @SpecComment("General Mod Config")
    public GeneralConfigSettings generalConfig = new GeneralConfigSettings();

    @Path("botConfig")
    @SpecComment("Config specific to the discord bot")
    public BotConfigSettings botConfig = new BotConfigSettings();

    @Path("channelsAndWebhooks")
    @SpecComment("Config relating to the discord channels and webhooks to use with the mod")
    public ChannelWebhookConfig channelsAndWebhooks = new ChannelWebhookConfig();

    @Path("chat")
    @SpecComment("Configure which types of messages are delivered to Minecraft/Discord")
    public ChatSettingsConfig chatConfig = new ChatSettingsConfig();

    @Path("messageFormatting")
    @SpecComment("Change the format in which messages are displayed")
    public MessageFormatting messageFormatting = new MessageFormatting();

    @Path("messageDestinations")
    @SpecComment("Change in which channel messages appear")
    public MessageChannelConfig messageDestinations = new MessageChannelConfig();

    @Path("whitelistingAndLinking")
    @SpecComment("Configure Whitelisting and Account Linking through the bot")
    public LinkAndWhitelistConfigSettings whitelistingAndLinking = new LinkAndWhitelistConfigSettings();

    @Path("botCommands")
    @SpecComment("Enable or Disable certain bot commands")
    public BotCommandsConfig botCommands = new BotCommandsConfig();

    @Path("linkedCommands")
    @SpecComment("Execute Minecraft commands in Discord")
    public LinkedCommandsConfig linkedCommands = new LinkedCommandsConfig();
}
