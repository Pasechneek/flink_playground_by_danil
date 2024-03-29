package org.debug.print;

public class DebugPrint {

    @org.jetbrains.annotations.NotNull
    @org.jetbrains.annotations.Contract(pure = true)
    public static String getStars() {
        return "\n**************************************************\n";
    }

    public static void deprint (String variable) {
        System.out.printf("%1$s%2$s%1$s%n", getStars(), variable);
    }

    public static void deprint(String variable, String comment) {
        System.out.printf("%1$s%3$s\n%2$s%1$s%n", getStars(), variable, comment);
    }
}
