/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.config.impl;

import me.hypherionmc.moonconfig.core.conversion.Path;
import me.hypherionmc.moonconfig.core.conversion.SpecComment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HypherionSA
 * Main Config Structure to control Discord -> MC Commands
 */
public class LinkedCommandsConfig {

    @Path("enabled")
    @SpecComment("Should linked commands be enabled")
    public boolean enabled = false;

    @Path("commands")
    @SpecComment("Commands to be linked")
    public List<Command> commands = new ArrayList<>();

    public static class Command {
        @Path("mcCommand")
        @SpecComment("The Minecraft Command. Use %args% to pass everything after the discordCommand to Minecraft")
        public String mcCommand;

        @Path("discordCommand")
        @SpecComment("The command slug in discord. To be used as /mc slug")
        public String discordCommand;

        @Path("discordRole")
        @SpecComment("Discord Role Name of ID of the role that is allowed to execute this command. If empty, all players will be allowed to use this command")
        public String discordRole;
    }

}
