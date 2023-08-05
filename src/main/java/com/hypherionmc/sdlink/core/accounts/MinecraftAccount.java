/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.accounts;

import com.hypherionmc.sdlink.core.config.SDLinkConfig;
import com.hypherionmc.sdlink.core.database.SDLinkAccount;
import com.hypherionmc.sdlink.core.discord.BotController;
import com.hypherionmc.sdlink.core.managers.RoleManager;
import com.hypherionmc.sdlink.core.messaging.Result;
import com.hypherionmc.sdlink.core.services.SDLinkPlatform;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hypherionmc.sdlink.core.managers.DatabaseManager.sdlinkDatabase;

/**
 * @author HypherionSA
 * Represents a Minecraft Account. Used for communication between this library and minecraft
 */
public class MinecraftAccount {

    private final String username;
    private final UUID uuid;
    private final boolean isOffline;
    private final boolean isValid;

    /**
     * Internal. Use {@link #standard(String)} or {@link #offline(String)}
     * @param username The Username of the Player
     * @param uuid The UUID of the player
     * @param isOffline Is this an OFFLINE/Unauthenticated Account
     * @param isValid Is the account valid
     */
    private MinecraftAccount(String username, UUID uuid, boolean isOffline, boolean isValid) {
        this.username = username;
        this.uuid = uuid;
        this.isOffline = isOffline;
        this.isValid = isValid;
    }

    /**
     * Tries to convert a Username to an online user account. If it can not, it will return an offline user
     * @param username The username to search for
     */
    public static MinecraftAccount standard(String username) {
        Pair<String, UUID> player = fetchPlayer(username);

        if (player.getRight() == null) {
            return offline(username);
        }

        return new MinecraftAccount(
                player.getLeft(),
                player.getRight(),
                false,
                player.getRight() != null
        );
    }

    /**
     * Convert a username to an offline account
     * @param username The Username to search for
     */
    private static MinecraftAccount offline(String username) {
        Pair<String, UUID> player = offlinePlayer(username);
        return new MinecraftAccount(
                player.getLeft(),
                player.getRight(),
                true,
                true
        );
    }

    /**
     * Convert GameProfile to Minecraft account
     * @param profile The profile of the player
     */
    public static MinecraftAccount fromGameProfile(GameProfile profile) {
        return standard(profile.getName());
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean isOffline() {
        return isOffline;
    }

    /**
     * Link a Minecraft account to a discord account
     * @param member The discord user
     * @param guild The server the command is run from
     */
    public Result linkAccount(Member member, Guild guild) {
        if (getStoredAccount() == null) {
            return Result.error("We couldn't link your Minecraft and Discord Accounts together. If this error persists, please ask a staff member for help");
        }

        SDLinkAccount account = getStoredAccount();
        account.setDiscordID(member.getId());
        account.setAddedBy(member.getId());
        account.setAccountLinkCode("");

        try {
            sdlinkDatabase.upsert(account);

            String suffix = this.username;
            int availableChars = 32 - suffix.length();
            String nickname = member.getEffectiveName();

            if (nickname.length() > availableChars) {
                nickname = nickname.substring(0, availableChars - 3) + "...";
            }

            String finalnickname = SDLinkConfig.INSTANCE.whitelistingAndLinking.accountLinking.nicknameFormat.replace("%nick%", nickname).replace("%mcname%", suffix);

            if (SDLinkConfig.INSTANCE.whitelistingAndLinking.accountLinking.changeNickname) {
                try {
                    member.modifyNickname(finalnickname).queue();

                    try {
                        if (RoleManager.getLinkedRole() != null) {
                            guild.addRoleToMember(UserSnowflake.fromId(member.getId()), RoleManager.getLinkedRole()).queue();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    if (SDLinkConfig.INSTANCE.generalConfig.debugging) {
                        e.printStackTrace();
                    }
                }
            }

            return Result.success("Your Discord and MC accounts have been linked");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.error("Failed to complete account linking. Please inform the server owner");
    }

    /**
     * Unlink a previously linked Discord and Minecraft Account
     */
    public Result unlinkAccount(Member member, Guild guild) {
        SDLinkAccount account = getStoredAccount();
        if (account == null)
            return Result.error("No such account found in database");

        try {
            sdlinkDatabase.remove(account, SDLinkAccount.class);

            try {
                if (RoleManager.getLinkedRole() != null && member.getRoles().contains(RoleManager.getLinkedRole())) {
                    guild.removeRoleFromMember(member, RoleManager.getLinkedRole()).queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return Result.success("Your discord and Minecraft accounts are no longer linked");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.error("We could not unlink your discord and Minecraft accounts. Please inform the server owner");
    }

    /**
     * Check if account database contains linking information
     * and a valid discord user for this account
     */
    public boolean isAccountLinked() {
        SDLinkAccount account = getStoredAccount();

        if (account == null)
            return false;

        User discordUser = getDiscordUser();
        return discordUser != null;
    }

    /**
     * Whitelist a Player on Minecraft and store the info the database
     * @param member The Discord Member that executed the command
     * @param guild The Discord Server the command was executed in
     */
    public Result whitelistAccount(Member member, Guild guild) {
        if (getStoredAccount() == null) {
            return Result.error("We couldn't link your Minecraft and Discord Accounts together. If this error persists, please ask a staff member for help");
        }

        SDLinkAccount account = getStoredAccount();
        account.setAddedBy(member.getId());
        account.setWhitelistCode("");

        try {
            if (!SDLinkPlatform.minecraftHelper.whitelistPlayer(MinecraftAccount.standard(account.getUsername())).isError()) {
                account.setWhitelisted(true);
                sdlinkDatabase.upsert(account);

                // Auto Linking is enabled, so we link the Discord and MC accounts
                if (SDLinkConfig.INSTANCE.whitelistingAndLinking.whitelisting.linkedWhitelist) {
                    this.linkAccount(member, guild);
                }

                try {
                    if (RoleManager.getWhitelistedRole() != null) {
                        guild.addRoleToMember(member, RoleManager.getWhitelistedRole()).queue();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Result.success("Your account has been whitelisted");
            } else {
                return Result.error("Account is already whitelisted on the Minecraft server");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.error("Failed to complete whitelisting. Please inform the server owner");
    }

    /**
     * Remove a previously whitelisted account from Minecraft and the database
     */
    public Result unwhitelistAccount(Member member, Guild guild) {
        SDLinkAccount account = getStoredAccount();
        if (account == null)
            return Result.error("No such account found in database");

        try {
            MinecraftAccount minecraftAccount = MinecraftAccount.standard(account.getUsername());
            Result whitelistResult = SDLinkPlatform.minecraftHelper.unWhitelistPlayer(minecraftAccount);
            if (whitelistResult.isError()) {
                return whitelistResult;
            } else {
                account.setWhitelisted(false);
                sdlinkDatabase.upsert(account);

                // Auto Linking is enabled. So we unlink the account
                if (SDLinkConfig.INSTANCE.whitelistingAndLinking.whitelisting.linkedWhitelist) {
                    this.unlinkAccount(member, guild);
                }

                try {
                    if (RoleManager.getWhitelistedRole() != null && member.getRoles().contains(RoleManager.getWhitelistedRole())) {
                        guild.removeRoleFromMember(member, RoleManager.getWhitelistedRole()).queue();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return Result.success("Your account has been removed from the whitelist");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.error("We could not unwhitelist your account. Please inform the server owner");
    }

    /**
     * Check if the player is whitelisted on the MC server and if the database
     * contains an entry for this player
     */
    public boolean isAccountWhitelisted() {
        SDLinkAccount account = getStoredAccount();

        if (account == null)
            return false;

        User discordUser = getDiscordUser();

        if (discordUser != null) {
            Member m = BotController.INSTANCE.getJDA().getGuilds().get(0).getMemberById(discordUser.getId());
            if (m != null)
                return m.getRoles().stream().anyMatch(r -> RoleManager.getAutoWhitelistRoles().contains(r));
        }

        return !SDLinkPlatform.minecraftHelper.isPlayerWhitelisted(MinecraftAccount.standard(account.getUsername())).isError() && account.isWhitelisted();
    }

    /**
     * Retrieve the stored account from the database
     */
    public SDLinkAccount getStoredAccount() {
        return sdlinkDatabase.findById(this.uuid.toString(), SDLinkAccount.class);
    }

    /**
     * Construct a new Database Entry for this account.
     * Must only be used when a new entry is required
     */
    public SDLinkAccount newDBEntry() {
        SDLinkAccount account = new SDLinkAccount();
        account.setOffline(this.isOffline);
        account.setUUID(this.uuid.toString());
        account.setWhitelisted(false);
        account.setUsername(this.username);

        return account;
    }

    /**
     * Get the Discord Account name this player is linked to
     */
    public String getDiscordName() {
        SDLinkAccount storedAccount = sdlinkDatabase.findById(this.uuid, SDLinkAccount.class);
        if (storedAccount == null || storedAccount.getDiscordID() == null || storedAccount.getDiscordID().isEmpty())
            return "Unlinked";

        User discordUser = BotController.INSTANCE.getJDA().getUserById(storedAccount.getDiscordID());
        return discordUser == null ? "Unlinked" : discordUser.getName();
    }

    /**
     * Get the Discord User this player is linked to
     */
    public User getDiscordUser() {
        SDLinkAccount storedAccount = sdlinkDatabase.findById(this.uuid, SDLinkAccount.class);
        if (storedAccount == null || storedAccount.getDiscordID() == null || storedAccount.getDiscordID().isEmpty())
            return null;

        return BotController.INSTANCE.getJDA().getUserById(storedAccount.getDiscordID());
    }

    // Helper Methods
    private static Pair<String, UUID> fetchPlayer(String name) {
        try {
            BufferedReader read = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openStream()));
            JSONObject obj = new JSONObject(new JSONTokener(read));
            String uuid = "";
            String returnname = name;

            if (!obj.getString("name").isEmpty()) {
                returnname = obj.getString("name");
            }
            if (!obj.getString("id").isEmpty()) {
                uuid = obj.getString("id");
            }

            read.close();
            return Pair.of(returnname, uuid.isEmpty() ? null : mojangIdToUUID(uuid));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return Pair.of("", null);
    }

    private static UUID mojangIdToUUID(String id) {
        final List<String> strings = new ArrayList<>();
        strings.add(id.substring(0, 8));
        strings.add(id.substring(8, 12));
        strings.add(id.substring(12, 16));
        strings.add(id.substring(16, 20));
        strings.add(id.substring(20, 32));

        return UUID.fromString(String.join("-", strings));
    }

    private static Pair<String, UUID> offlinePlayer(String offlineName) {
        return Pair.of(offlineName, UUID.nameUUIDFromBytes(("OfflinePlayer:" + offlineName).getBytes(StandardCharsets.UTF_8)));
    }

}
