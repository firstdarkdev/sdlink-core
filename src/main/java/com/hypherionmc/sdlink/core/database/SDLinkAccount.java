package com.hypherionmc.sdlink.core.database;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "verifiedaccounts", schemaVersion = "1.0")
public class SDLinkAccount {

    @Id
    @Getter @Setter
    private String uuid;

    @Getter @Setter
    private String username;

    @Getter @Setter
    private String discordID;

    @Getter @Setter
    private String verifyCode;

    @Getter @Setter
    private boolean isOffline;

}
