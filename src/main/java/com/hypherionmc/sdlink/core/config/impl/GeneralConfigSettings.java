/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.config.impl;

import me.hypherionmc.moonconfig.core.conversion.Path;
import me.hypherionmc.moonconfig.core.conversion.SpecComment;
import com.hypherionmc.sdlink.core.config.ConfigController;

/**
 * @author HypherionSA
 * General Mod Settings config Structure
 */
public class GeneralConfigSettings {

    @Path("enabled")
    @SpecComment("Should the mod be enabled or not")
    public boolean enabled = true;

    @Path("debugging")
    @SpecComment("Enable Additional Logging. Used for Fault Finding. WARNING: CAUSES LOG SPAM!")
    public boolean debugging = false;

    @Path("configVersion")
    @SpecComment("Internal version control. DO NOT TOUCH!")
    public int configVersion = ConfigController.configVer;
}
