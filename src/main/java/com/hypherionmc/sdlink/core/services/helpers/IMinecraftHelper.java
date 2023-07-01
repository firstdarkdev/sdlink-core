/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.services.helpers;

import com.hypherionmc.sdlink.core.accounts.MinecraftAccount;
import com.hypherionmc.sdlink.core.messaging.Result;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @author HypherionSA
 * Service to bridge communication between the Library and Minecraft
 */
public interface IMinecraftHelper {

    void discordMessageReceived(Member member, String message);
    Result checkWhitelisting();
    Result isPlayerWhitelisted(MinecraftAccount account);
    Result whitelistPlayer(MinecraftAccount account);
    Result unWhitelistPlayer(MinecraftAccount account);
    List<MinecraftAccount> getWhitelistedPlayers();
    Pair<Integer, Integer> getPlayerCounts();
    List<MinecraftAccount> getOnlinePlayers();
    long getServerUptime();
    String getServerVersion();
    Result executeMinecraftCommand(String command, String args);
    boolean isOnlineMode();

}
