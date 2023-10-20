/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.managers;

import com.hypherionmc.sdlink.core.discord.BotController;
import com.hypherionmc.sdlink.core.util.SystemUtils;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HypherionSA
 * Load and Cache roles needed by the bot
 */
public class RoleManager {

    private static final Set<Role> autoWhitelistRoles = new HashSet<>();

    /**
     * Check and load the roles required by the bot
     * @param errCount
     * @param builder
     */
    public static void loadRequiredRoles(AtomicInteger errCount, StringBuilder builder) {
        // TODO Verification Roles
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
        Role role = null;
        if (SystemUtils.isLong(roleID)) {
            role = BotController.INSTANCE.getJDA().getRoleById(roleID);
        }

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
}
