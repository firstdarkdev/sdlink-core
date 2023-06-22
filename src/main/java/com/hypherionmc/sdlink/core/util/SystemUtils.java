/*
 * This file is part of sdlink-core, licensed under the MIT License (MIT).
 * Copyright HypherionSA and Contributors
 */
package com.hypherionmc.sdlink.core.util;

import com.hypherionmc.sdlink.core.managers.RoleManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SystemUtils {

    /**
     * Convert Bytes into a human-readable format, like 1GB
     * From https://stackoverflow.com/a/3758880
     * @param bytes The Size in Bytes
     * @return The size formatted in KB, MB, GB, TB, PB etc
     */
    public static String byteToHuman(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }

    // Time Conversion
    public static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));

    public static final List<String> timesString = Arrays.asList("year", "month", "day", "hour", "minute", "second");

    /**
     * Unix Timestamp to Duration
     * @param duration Unix Timestamp
     * @return Formatted Duration
     */
    public static String toDuration(long duration) {
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < times.size(); i++) {
            Long current = times.get(i);
            long temp = duration / current;
            if (temp > 0) {
                res.append(temp).append(" ").append(timesString.get(i)).append(temp != 1 ? "s" : "");
                break;
            }
        }
        if ("".equals(res.toString()))
            return "0 seconds ago";
        else
            return res.toString();
    }

    /**
     * Convert Seconds into a Timestamp
     * @param sec Input in seconds
     */
    public static String secondsToTimestamp(long sec) {
        long seconds = sec % 60;
        long minutes = (sec / 60) % 60;
        long hours = (sec / 3600) % 24;
        long days = sec / (3600 * 24);

        String timeString = String.format("%02d hour(s), %02d minute(s), %02d second(s)", hours, minutes, seconds);

        if (days > 0) {
            timeString = String.format("%d day(s), %s", days, timeString);
        }

        return timeString;
    }

    /**
     * Generate random verification code for Whitelisting and Account Linking
     */
    public static int generateRandomJoinCode() {
        return new Random().ints(1000, 9999).findFirst().getAsInt();
    }

    public static boolean isLong(String input) {
        try {
            Long.parseLong(input);
            return true;
        } catch (NumberFormatException ignored){}
        return false;
    }
}
