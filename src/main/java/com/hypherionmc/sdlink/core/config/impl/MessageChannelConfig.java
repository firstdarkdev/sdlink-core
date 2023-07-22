/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.config.impl;

import com.hypherionmc.sdlink.core.messaging.MessageDestination;
import me.hypherionmc.moonconfig.core.conversion.Path;
import me.hypherionmc.moonconfig.core.conversion.SpecComment;

/**
 * @author HypherionSA
 * Config Structure to control the destinations of messages
 */
public class MessageChannelConfig {

    @Path("chat")
    @SpecComment("Control where CHAT messages are delivered")
    public DestinationObject chat = DestinationObject.of(MessageDestination.CHAT, false, "default");

    @Path("startStop")
    @SpecComment("Control where START/STOP messages are delivered")
    public DestinationObject startStop = DestinationObject.of(MessageDestination.EVENT, false, "default");

    @Path("joinLeave")
    @SpecComment("Control where JOIN/LEAVE messages are delivered")
    public DestinationObject joinLeave = DestinationObject.of(MessageDestination.EVENT, false, "default");

    @Path("advancements")
    @SpecComment("Control where ADVANCEMENT messages are delivered")
    public DestinationObject advancements = DestinationObject.of(MessageDestination.EVENT, false, "default");

    @Path("death")
    @SpecComment("Control where DEATH messages are delivered")
    public DestinationObject death = DestinationObject.of(MessageDestination.EVENT, false, "default");

    @Path("commands")
    @SpecComment("Control where COMMAND messages are delivered")
    public DestinationObject commands = DestinationObject.of(MessageDestination.EVENT, false, "default");

    @Path("custom")
    @SpecComment("Control where messages that match none of the above are delivered")
    public DestinationObject custom = DestinationObject.of(MessageDestination.EVENT, false, "default");

    public static class DestinationObject {
        @Path("channel")
        @SpecComment("The Channel the message will be delivered to. Valid entries are CHAT, EVENT, CONSOLE")
        public MessageDestination channel;

        @Path("useEmbed")
        @SpecComment("Should the message be sent using EMBED style messages")
        public boolean useEmbed;

        @Path("embedLayout")
        @SpecComment("Embed Layout to use")
        public String embedLayout;

        DestinationObject(MessageDestination destination, boolean useEmbed, String embedLayout) {
            this.channel = destination;
            this.useEmbed = useEmbed;
            this.embedLayout = embedLayout;
        }

        public static DestinationObject of(MessageDestination destination, boolean useEmbed, String embedLayout) {
            return new DestinationObject(destination, useEmbed, embedLayout);
        }
    }
}
