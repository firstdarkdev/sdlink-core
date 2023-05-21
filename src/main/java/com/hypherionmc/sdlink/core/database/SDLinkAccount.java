/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.database;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

/**
 * @author HypherionSA
 * JSON based database to hold accounts the bot has interacted with.
 * This is used for Account Linking and Whitelisting
 */
@Document(collection = "accounts", schemaVersion = "1.0")
public class SDLinkAccount {
    @Id
    private String UUID;
    private String username;
    private String addedBy;
    private String discordID;
    private String accountLinkCode;
    private String whitelistCode;
    private boolean isWhitelisted;
    private boolean isOffline;

    public String getAccountLinkCode() {
        if (accountLinkCode == null)
            return "";
        return accountLinkCode;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public boolean isWhitelisted() {
        return isWhitelisted;
    }

    public String getDiscordID() {
        if (discordID == null)
            return "";
        return discordID;
    }

    public String getAddedBy() {
        if (addedBy == null)
            return "";
        return addedBy;
    }

    public String getUsername() {
        if (username == null)
            return "";
        return username;
    }

    public String getUUID() {
        if (UUID == null)
            return "";
        return UUID;
    }

    public String getWhitelistCode() {
        if (whitelistCode == null)
            return "";
        return whitelistCode;
    }

    public void setAccountLinkCode(String accountLinkCode) {
        this.accountLinkCode = accountLinkCode;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public void setDiscordID(String discordID) {
        this.discordID = discordID;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public void setWhitelistCode(String whitelistCode) {
        this.whitelistCode = whitelistCode;
    }

    public void setWhitelisted(boolean whitelisted) {
        isWhitelisted = whitelisted;
    }
}
