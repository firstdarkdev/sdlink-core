/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.managers;

import com.hypherionmc.sdlink.core.discord.BotController;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hypherionmc.sdlink.core.config.ConfigController.sdLinkConfig;

/**
 * @author HypherionSA
 * Load and Cache roles needed by the bot
 */
public class RoleManager {

    private static Role staffRole;
    private static Role whitelistedRole;
    private static Role linkedRole;
    private static final HashMap<String, Role> commandRoles = new HashMap<>();

    /**
     * Check and load the roles required by the bot
     * @param errCount
     * @param builder
     */
    public static void loadRequiredRoles(AtomicInteger errCount, StringBuilder builder) {
        if (!sdLinkConfig.botConfig.staffRole.isEmpty()) {
            staffRole = getRole(errCount, builder, "Staff", sdLinkConfig.botConfig.staffRole);
        }

        if (!sdLinkConfig.whitelistingAndLinking.whitelisting.autoWhitelistRole.isEmpty()) {
            whitelistedRole = getRole(errCount, builder, "Whitelist", sdLinkConfig.whitelistingAndLinking.whitelisting.autoWhitelistRole);
        }

        if (!sdLinkConfig.whitelistingAndLinking.accountLinking.linkedRole.isEmpty()) {
            linkedRole = getRole(errCount, builder, "Linked Account", sdLinkConfig.whitelistingAndLinking.accountLinking.linkedRole);
        }

        if (sdLinkConfig.linkedCommands.enabled) {
            commandRoles.clear();
            sdLinkConfig.linkedCommands.commands.forEach(cmd -> {
                if (!cmd.discordRole.isEmpty()) {
                    Role role = getRole(errCount, builder, cmd.discordCommand + " usage", cmd.discordRole);
                    if (role != null) {
                        commandRoles.putIfAbsent(cmd.discordCommand, role);
                    }
                }
            });
        }
    }

    /**
     * Load a role from either a Name or ID
     * @param errCount Counter holding the current error count
     * @param builder String builder that is used to build the error messages
     * @param roleIdentifier Log identifier for the role being loaded
     * @param roleID The ID or Name of the role to load
     * @return The role that matched or NULL
     */
    private static Role getRole(AtomicInteger errCount, StringBuilder builder, String roleIdentifier, String roleID) {
        Role role = BotController.INSTANCE.getJDA().getRoleById(roleID);

        if (role != null) {
            return role;
        } else {
            List<Role> roles = BotController.INSTANCE.getJDA().getRolesByName(roleID, true);
            if (!roles.isEmpty()) {
                return roles.get(0);
            }
        }

        errCount.incrementAndGet();
        builder.append(errCount.get())
                .append(") ")
                .append("Missing ")
                .append(roleIdentifier)
                .append(" Role. Role: ")
                .append(roleID)
                .append(" cannot be found in the server")
                .append("\r\n");

        return null;
    }

    public static Role getLinkedRole() {
        return linkedRole;
    }

    public static Role getStaffRole() {
        return staffRole;
    }

    public static Role getWhitelistedRole() {
        return whitelistedRole;
    }

    public static HashMap<String, Role> getCommandRoles() {
        return commandRoles;
    }
}
