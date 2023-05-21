/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.commands;

import com.hypherionmc.sdlink.core.discord.commands.slash.general.HelpSlashCommand;
import com.hypherionmc.sdlink.core.discord.commands.slash.general.PlayerListSlashCommand;
import com.hypherionmc.sdlink.core.discord.commands.slash.general.ServerStatusSlashCommand;
import com.hypherionmc.sdlink.core.discord.commands.slash.linking.ConfirmAccountLinkSlashCommand;
import com.hypherionmc.sdlink.core.discord.commands.slash.linking.LinkAccountCommand;
import com.hypherionmc.sdlink.core.discord.commands.slash.linking.UnlinkAccountSlashCommand;
import com.hypherionmc.sdlink.core.discord.commands.slash.linking.ViewLinkedAccountsCommand;
import com.hypherionmc.sdlink.core.discord.commands.slash.whitelist.ConfirmWhitelistSlashCommand;
import com.hypherionmc.sdlink.core.discord.commands.slash.whitelist.UnWhitelistAccountSlashCommand;
import com.hypherionmc.sdlink.core.discord.commands.slash.whitelist.ViewWhitelistedAccountsSlashCommand;
import com.hypherionmc.sdlink.core.discord.commands.slash.whitelist.WhitelistAccountCommand;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.SlashCommand;

import java.util.HashSet;
import java.util.Set;

import static com.hypherionmc.sdlink.core.config.ConfigController.sdLinkConfig;

/**
 * @author HypherionSA
 * Command Manager class to control how commands are registered to discord
 */
public class CommandManager {

    public static final CommandManager INSTANCE = new CommandManager();

    private final Set<SlashCommand> commands = new HashSet<>();

    private CommandManager() {
        this.addCommands();
    }

    private void addCommands() {
        // Register Account Linking commands, if linking is enabled
        if (sdLinkConfig.whitelistingAndLinking.accountLinking.accountLinking) {
            commands.add(new LinkAccountCommand());
            commands.add(new ConfirmAccountLinkSlashCommand());
            commands.add(new UnlinkAccountSlashCommand());
            commands.add(new ViewLinkedAccountsCommand());
        }

        // Register Whitelist commands, if whitelisting is enabled
        if (sdLinkConfig.whitelistingAndLinking.whitelisting.whitelisting) {
            commands.add(new WhitelistAccountCommand());
            commands.add(new ConfirmWhitelistSlashCommand());
            commands.add(new ViewWhitelistedAccountsSlashCommand());
            commands.add(new UnWhitelistAccountSlashCommand());
        }

        // Enable the Server Status command
        if (sdLinkConfig.botCommands.allowServerStatus) {
            commands.add(new ServerStatusSlashCommand());
        }

        // Enable the Player List command
        if (sdLinkConfig.botCommands.allowPlayerList) {
            commands.add(new PlayerListSlashCommand());
        }

        // Enable the Help command
        if (sdLinkConfig.botCommands.allowHelpCommand) {
            commands.add(new HelpSlashCommand());
        }
    }

    /**
     * INTERNAL. Used to register slash commands
     * @param client The Discord Command Client instance
     */
    public void register(CommandClient client) {
        commands.forEach(client::addSlashCommand);
    }

    public Set<SlashCommand> getCommands() {
        return commands;
    }
}
