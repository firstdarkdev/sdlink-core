/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.commands;

import com.hypherionmc.sdlink.core.discord.commands.slash.general.HelpSlashCommand;
import com.hypherionmc.sdlink.core.discord.commands.slash.general.PlayerListSlashCommand;
import com.hypherionmc.sdlink.core.discord.commands.slash.general.ServerStatusSlashCommand;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.SlashCommand;

import java.util.HashSet;
import java.util.Set;

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
        // TODO Verification

        // Enable the Server Status command
        commands.add(new ServerStatusSlashCommand());

        // Enable the Player List command
        commands.add(new PlayerListSlashCommand());

        // Enable the Help command
        commands.add(new HelpSlashCommand());
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
