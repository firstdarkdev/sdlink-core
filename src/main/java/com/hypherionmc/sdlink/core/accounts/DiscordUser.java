/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.accounts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
public class DiscordUser {

    @Getter
    @Setter
    private String effectiveName;

    @Getter
    @Setter
    private String avatarUrl;

    @Getter
    @Setter
    private long userId;

    @Getter
    @Setter
    private String asMention;

}
