/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.commands.slash;

import com.jagrosh.jdautilities.command.SlashCommand;

/**
 * @author HypherionSA
 * Extention of {@link SlashCommand} to implement our Permission handling
 */
public abstract class SDLinkSlashCommand extends SlashCommand {

    public SDLinkSlashCommand(boolean requiresPerms) {
        this.guildOnly = true;
    }

}
