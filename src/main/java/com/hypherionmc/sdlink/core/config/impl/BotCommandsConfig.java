/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.config.impl;

import me.hypherionmc.moonconfig.core.conversion.Path;
import me.hypherionmc.moonconfig.core.conversion.SpecComment;

/**
 * @author HypherionSA
 * Config Structure to allow disabling some bot commands
 */
public class BotCommandsConfig {

    @Path("allowPlayerList")
    @SpecComment("Enable/Disable the Player List command")
    public boolean allowPlayerList = true;

    @Path("allowServerStatus")
    @SpecComment("Enable/Disable the Server Status command")
    public boolean allowServerStatus = true;

    @Path("allowHelpCommand")
    @SpecComment("Enable/Disable the Help command")
    public boolean allowHelpCommand = true;

}
