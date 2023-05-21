/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.managers;

import com.hypherionmc.sdlink.core.database.SDLinkAccount;
import io.jsondb.JsonDBTemplate;

/**
 * @author HypherionSA
 * Helper class to initialize the JSON database
 */
public class DatabaseManager {

    public static final JsonDBTemplate sdlinkDatabase = new JsonDBTemplate("sdlinkstorage", "com.hypherionmc.sdlink.core.database");

    public static void initialize() {
        if (!sdlinkDatabase.collectionExists(SDLinkAccount.class)) {
            sdlinkDatabase.createCollection(SDLinkAccount.class);
        }
    }
}
