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
 * Config Structure to control Whitelisting and Account Linking
 */
public class LinkAndWhitelistConfigSettings {

    @Path("whiteListing")
    @SpecComment("Control how the bot handles Whitelisting Players, if at all")
    public Whitelisting whitelisting = new Whitelisting();

    @Path("accountLinking")
    @SpecComment("Control how the bot handles Discord -> MC Account Linking, if at all")
    public AccountLinking accountLinking = new AccountLinking();

    public static class AccountLinking {
        @Path("accountlinking")
        @SpecComment("Allow users to Link their MC and Discord accounts")
        public boolean accountLinking = false;

        @Path("linkedRole")
        @SpecComment("If a role ID (or name) is defined here, it will be assigned to players when their MC and Discord accounts are linked")
        public String linkedRole = "";

        @Path("requireLinking")
        @SpecComment("Require users to link their Discord and Minecraft accounts before joining the server")
        public boolean requireLinking = false;

        @Path("changeNickname")
        @SpecComment("Allow nickname changes on account linking")
        public boolean changeNickname = true;

        @Path("nicknameFormat")
        @SpecComment("The nickname format to use for linked accounts, when changeNickname is enabled. %nick% for current nickname/name and %mcname% for the minecraft name")
        public String nicknameFormat = "%nick% [MC:%mcname%]";
    }

    public static class Whitelisting {
        @Path("whitelisting")
        @SpecComment("Should the bot be allowed to whitelist/un-whitelist players.")
        public boolean whitelisting = false;

        @Path("linkedWhitelist")
        @SpecComment("Automatically link Minecraft and Discord Accounts when a user is whitelisted")
        public boolean linkedWhitelist = false;

        @Path("autoWhitelistRoles")
        @SpecComment("Users with linked discord accounts, that contain any of these roles will be automatically whitelisted")
        public List<String> autoWhitelistRoles = new ArrayList<>();

        @Path("autoWhitelistRole")
        @SpecComment("If a role ID (or name) is defined here, it will be assigned to players when they are whitelisted")
        public String autoWhitelistRole = "";
    }
}
