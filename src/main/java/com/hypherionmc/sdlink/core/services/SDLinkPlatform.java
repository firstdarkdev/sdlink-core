/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.services;

import com.hypherionmc.sdlink.core.discord.BotController;
import com.hypherionmc.sdlink.core.services.helpers.IMinecraftHelper;

import java.util.ServiceLoader;

/**
 * @author HypherionSA
 * Service loader for library services
 */
public class SDLinkPlatform {

    public static IMinecraftHelper minecraftHelper = load(IMinecraftHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        BotController.INSTANCE.getLogger().debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }

}
