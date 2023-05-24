package net.highskiesmc.progression.util;

import java.util.regex.Pattern;

public class ChatColorRemover {
    private static final Pattern CHAT_COLOR_PATTERN = Pattern.compile("(?i)&[0-9A-FK-OR]");

    public static String removeChatColors(String input) {
        return CHAT_COLOR_PATTERN.matcher(input).replaceAll("");
    }
}
