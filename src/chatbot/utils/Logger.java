package chatbot.utils;

public class Logger {

    public static boolean debugMode = true;

    public static void println(String s) {
        if (!debugMode)
            return;

        System.out.println(s);
    }

    public static void printf(String format, Object... args) {
        if (!debugMode)
            return;

        System.out.printf(format, args);
    }

}
