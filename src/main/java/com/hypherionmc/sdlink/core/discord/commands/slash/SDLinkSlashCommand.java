/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.discord.commands.slash;

import com.hypherionmc.sdlink.core.managers.RoleManager;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.Permission;

/**
 * @author HypherionSA
 * Extention of {@link SlashCommand} to implement our Permission handling
 */
public abstract class SDLinkSlashCommand extends SlashCommand {

    public SDLinkSlashCommand(boolean requiresPerms) {
        if (requiresPerms) {
            if (RoleManager.getStaffRole() != null) {
                this.requiredRole = RoleManager.getStaffRole().getName();
            } else {
                this.userPermissions = new Permission[] { Permission.ADMINISTRATOR, Permission.KICK_MEMBERS };
            }
        }
        this.guildOnly = true;
    }

}
