/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.accounts;

import com.hypherionmc.sdlink.core.config.SDLinkConfig;
import com.hypherionmc.sdlink.core.database.SDLinkAccount;
import com.hypherionmc.sdlink.core.managers.CacheManager;
import com.hypherionmc.sdlink.core.managers.RoleManager;
import com.hypherionmc.sdlink.core.messaging.Result;
import com.hypherionmc.sdlink.core.util.Profiler;
import com.hypherionmc.sdlink.core.util.SDLinkUtils;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.UserSnowflake;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public static SDLinkAccount getStoredFromUUID(String uuid) {
        sdlinkDatabase.reloadCollection("verifiedaccounts");
        return sdlinkDatabase.findById(uuid, SDLinkAccount.class);
    }

    public boolean isAccountVerified() {
        SDLinkAccount account = getStoredAccount();

        if (account == null)
            return false;

        return !SDLinkUtils.isNullOrEmpty(account.getDiscordID());
    }

    public SDLinkAccount getStoredAccount() {
        Profiler profiler = Profiler.getProfiler("getStoredAccount");
        profiler.start("Load Stored Account");
        sdlinkDatabase.reloadCollection("verifiedaccounts");
        SDLinkAccount account = sdlinkDatabase.findById(this.uuid.toString(), SDLinkAccount.class);

        profiler.stop();
        return account == null ? newDBEntry() : account;
    }

    @NotNull
    public SDLinkAccount newDBEntry() {
        SDLinkAccount account = new SDLinkAccount();
        account.setUsername(this.username);
        account.setUuid(this.uuid.toString());
        account.setDiscordID(null);
        account.setVerifyCode(null);
        account.setOffline(this.isOffline);

        sdlinkDatabase.upsert(account);
        sdlinkDatabase.reloadCollection("verifiedaccounts");

        return account;
    }

    @NotNull
    public String getDiscordName() {
        SDLinkAccount account = getStoredAccount();
        if (account == null || SDLinkUtils.isNullOrEmpty(account.getDiscordID()))
            return "Unlinked";

        DiscordUser user = getDiscordUser();

        return user == null ? "Unlinked" : user.getEffectiveName();
    }

    @Nullable
    public DiscordUser getDiscordUser() {
        Profiler profiler = Profiler.getProfiler("getDiscordUser");
        profiler.start("Loading Discord User");
        SDLinkAccount storedAccount = getStoredAccount();
        if (storedAccount == null || SDLinkUtils.isNullOrEmpty(storedAccount.getDiscordID()))
            return null;

        if (CacheManager.getDiscordMembers().isEmpty())
            return null;

        Optional<Member> member = CacheManager.getDiscordMembers().stream().filter(m -> m.getId().equalsIgnoreCase(storedAccount.getDiscordID())).findFirst();
        profiler.stop();
        return member.map(value -> DiscordUser.of(value.getEffectiveName(), value.getEffectiveAvatarUrl(), value.getIdLong(), value.getAsMention())).orElse(null);
    }

    public Result verifyAccount(Member member, Guild guild) {
        SDLinkAccount account = getStoredAccount();

        if (account == null)
            return Result.error("We couldn't find your Minecraft account. Please ask the staff for assistance");

        account.setDiscordID(member.getId());
        account.setVerifyCode(null);

        try {
            sdlinkDatabase.upsert(account);
            sdlinkDatabase.reloadCollection("verifiedaccounts");
        } catch (Exception e) {
            if (SDLinkConfig.INSTANCE.generalConfig.debugging) {
                e.printStackTrace();
            }
        }

        if (RoleManager.getVerifiedRole() != null) {
            try {
                guild.addRoleToMember(UserSnowflake.fromId(member.getId()), RoleManager.getVerifiedRole()).queue();
            } catch (Exception e) {
                if (SDLinkConfig.INSTANCE.generalConfig.debugging) {
                    e.printStackTrace();
                }
            }
        }

        return Result.success("Your account has been verified");
    }

    public Result unverifyAccount(Member member, Guild guild) {
        SDLinkAccount account = getStoredAccount();

        if (account == null)
            return Result.error("We couldn't find your Minecraft account. Please ask the staff for assistance");

        account.setDiscordID(null);
        account.setVerifyCode(null);

        try {
            sdlinkDatabase.upsert(account);
            sdlinkDatabase.reloadCollection("verifiedaccounts");
        } catch (Exception e) {
            if (SDLinkConfig.INSTANCE.generalConfig.debugging) {
                e.printStackTrace();
            }
        }

        if (RoleManager.getVerifiedRole() != null) {
            try {
                guild.removeRoleFromMember(UserSnowflake.fromId(member.getId()), RoleManager.getVerifiedRole()).queue();
            } catch (Exception e) {
                if (SDLinkConfig.INSTANCE.generalConfig.debugging) {
                    e.printStackTrace();
                }
            }
        }

        return Result.success("Your account has been un-verified");
    }

    public Result checkAccessControl() {
        if (!SDLinkConfig.INSTANCE.accessControl.enabled) {
            return Result.success("pass");
        }

        SDLinkAccount account = getStoredAccount();
        if (account == null)
            return Result.error("notFound");

        if (SDLinkUtils.isNullOrEmpty(account.getDiscordID()))
            return Result.error("notVerified");

        if (SDLinkConfig.INSTANCE.accessControl.requireDiscordMembership) {
            DiscordUser user = getDiscordUser();
            if (user == null)
                return Result.error("memberNotFound");
        }


        if (!SDLinkConfig.INSTANCE.accessControl.requiredRoles.isEmpty() && !RoleManager.getVerificationRoles().isEmpty()) {
            Profiler profiler = Profiler.getProfiler("checkRequiredRoles");
            profiler.start("Checking Required Roles");
            AtomicBoolean anyFound = new AtomicBoolean(false);

            Optional<Member> member = CacheManager.getDiscordMembers().stream().filter(m -> m.getId().equals(account.getDiscordID())).findFirst();
            member.ifPresent(m -> m.getRoles().forEach(r -> {
                if (RoleManager.getVerificationRoles().stream().anyMatch(role -> role.getIdLong() == r.getIdLong())) {
                    if (!anyFound.get()) {
                        anyFound.set(true);
                    }
                }
            }));
            profiler.stop();

            if (!anyFound.get())
                return Result.error("rolesNotFound");

            if (member.isEmpty())
                return Result.error("memberNotFound");
        }

        return Result.success("pass");
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

    //<editor-fold desc="Helper Methods">
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
    //</editor-fold>
}
