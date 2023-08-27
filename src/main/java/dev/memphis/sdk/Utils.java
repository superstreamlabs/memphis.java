package dev.memphis.sdk;

public class Utils {
    /**
     * Sanitizes station names by converting to lowercase and
     * replacing periods with pound signs.
     *
     * @param name public station name
     * @return sanitized internal name
     */
    public static String getInternalName(String name) {
        name = name.toLowerCase();
        return name.replace('.', '#');
    }
}
