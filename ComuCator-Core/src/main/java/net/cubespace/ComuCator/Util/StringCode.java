package net.cubespace.ComuCator.Util;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class StringCode {
    public static long getStringCode(String string) {
        long hash = 31;

        for (int i = 0; i < string.length(); i++) {
            hash += hash * 7 + string.charAt(i);
        }

        return hash;
    }
}
