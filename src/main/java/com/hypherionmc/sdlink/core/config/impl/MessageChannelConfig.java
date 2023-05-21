/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.config.impl;

import me.hypherionmc.moonconfig.core.conversion.Path;
import me.hypherionmc.moonconfig.core.conversion.SpecComment;
import com.hypherionmc.sdlink.core.messaging.MessageDestination;

/**
 * @author HypherionSA
 * Config Structure to control the destinations of messages
 */
public class MessageChannelConfig {

    @Path("chat")
    @SpecComment("Control where CHAT messages are delivered")
    public DestinationObject chat = DestinationObject.of(MessageDestination.CHAT, false);

    @Path("startStop")
    @SpecComment("Control where START/STOP messages are delivered")
    public DestinationObject startStop = DestinationObject.of(MessageDestination.EVENT, false);

    @Path("joinLeave")
    @SpecComment("Control where JOIN/LEAVE messages are delivered")
    public DestinationObject joinLeave = DestinationObject.of(MessageDestination.EVENT, false);

    @Path("advancements")
    @SpecComment("Control where ADVANCEMENT messages are delivered")
    public DestinationObject advancements = DestinationObject.of(MessageDestination.EVENT, false);

    @Path("death")
    @SpecComment("Control where DEATH messages are delivered")
    public DestinationObject death = DestinationObject.of(MessageDestination.EVENT, false);

    @Path("commands")
    @SpecComment("Control where COMMAND messages are delivered")
    public DestinationObject commands = DestinationObject.of(MessageDestination.EVENT, false);

    public static class DestinationObject {
        @Path("channel")
        @SpecComment("The Channel the message will be delivered to. Valid entries are CHAT, EVENT, CONSOLE")
        public MessageDestination channel;

        @Path("useEmbed")
        @SpecComment("Should the message be sent using EMBED style messages")
        public boolean useEmbed;

        DestinationObject(MessageDestination destination, boolean useEmbed) {
            this.channel = destination;
            this.useEmbed = useEmbed;
        }

        public static DestinationObject of(MessageDestination destination, boolean useEmbed) {
            return new DestinationObject(destination, useEmbed);
        }
    }
}
