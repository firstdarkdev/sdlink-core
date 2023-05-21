/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.hypherionmc.sdlink.core.discord.BotController;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.menu.EmbedPaginator;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author HypherionSA
 * Util classes to help manage certain discord message actions
 */
public class MessageUtil {

    /**
     * Create an Embed Paginator for use with Slash Commands
     * @param event The event of the executed command
     */
    public static EmbedPaginator.Builder defaultPaginator(SlashCommandEvent event) {
        return new EmbedPaginator.Builder()
                .setTimeout(1, TimeUnit.MINUTES)
                .setEventWaiter(BotController.INSTANCE.getEventWaiter())
                .waitOnSinglePage(true)
                .setFinalAction(m -> {
                    try {
                        m.clearReactions().queue();
                        m.delete().queue();
                    } catch(PermissionException ex) {
                        ex.printStackTrace();
                        event.reply(ex.getMessage()).setEphemeral(true).queue();
                    }
                })
                .setText((BiFunction<Integer, Integer, String>) null);
    }

    /**
     * Split a large list of items into smaller sublists. This is to help with Discord limits on pagination
     * @param source The list of objects to split
     * @param length How many entries are allowed per sub-list
     */
    public static <T> Stream<List<T>> listBatches(List<T> source, int length) {
        if (length <= 0)
            throw new IllegalArgumentException("length = " + length);
        int size = source.size();
        if (size <= 0)
            return Stream.empty();
        int fullChunks = (size - 1) / length;
        return IntStream.range(0, fullChunks + 1).mapToObj(
                n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
    }

    /**
     * Same as {@link #listBatches(List, int)}, but for HashMaps
     */
    public static <K, V> List<Map<K, V>> splitMap(Map<K, V> map, int size) {
        List<List<Map.Entry<K, V>>> list = Lists.newArrayList(Iterables.partition(map.entrySet(), size));

        return list.stream()
                .map(entries ->
                        entries.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                )
                .collect(Collectors.toList());
    }

}
