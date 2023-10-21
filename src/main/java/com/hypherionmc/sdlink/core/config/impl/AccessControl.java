/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.config.impl;

import me.hypherionmc.moonconfig.core.conversion.Path;
import me.hypherionmc.moonconfig.core.conversion.SpecComment;

import java.util.ArrayList;
import java.util.List;

public class AccessControl {

    @Path("enabled")
    @SpecComment("Enable Access Control")
    public boolean enabled = false;

    @Path("requireDiscordMembership")
    @SpecComment("Does the player need to be a member of your discord to join")
    public boolean requireDiscordMembership = false;

    @Path("requiredRoles")
    @SpecComment("Optional: The player requires any of these roles to be able to join your server")
    public List<String> requiredRoles = new ArrayList<>();

    @Path("verifiedRole")
    @SpecComment("Optional: Role name or ID to assign to verified player accounts")
    public String verifiedRole = "";

    @Path("verificationMessages")
    @SpecComment("Configure messages shown to players when they don't meet verification requirements")
    public AccessMessages verificationMessages = new AccessMessages();

    public static class AccessMessages {

        @Path("accountVerification")
        @SpecComment("The message shown to players that are not verified")
        public String accountVerify = "This server requires account verification. Your verification code is: {code}. Please visit our discord server for instructions on how to verify your account.";

        @Path("nonMember")
        @SpecComment("Message to show to players that are not a member of your discord")
        public String nonMember = "Sorry, you need to be a member of our discord server to join this server";

        @Path("requireRoles")
        @SpecComment("Message to show when player doesn't have one of the required roles. Use {roles} to display the names of configured roles")
        public String requireRoles = "Sorry, but you require any of the following roles: {roles}";

    }

}
