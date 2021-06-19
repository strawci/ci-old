package com.strawci.ci.utils;

import java.io.File;

public class SystemUtils {
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

    public static boolean isSolaris() {
        return OS.contains("sunos");
    }

    public static File getApplicationDirectory () {
        if (SystemUtils.isUnix()) {
            return new File("/etc/strawci");
        }

        else {
            return new File(System.getProperty("user.dir"));
        }
    }

    public static File getConfigurationDirectory () {
        return new File(SystemUtils.getApplicationDirectory(), "config");
    }

    public static File getPluginsDirectory () {
        return new File(SystemUtils.getApplicationDirectory(), "plugins");
    }

    public static void createDirectories () {
        if (!SystemUtils.getApplicationDirectory().exists()) {
            SystemUtils.getApplicationDirectory().mkdir();
        }

        if (!SystemUtils.getPluginsDirectory().exists()) {
            SystemUtils.getPluginsDirectory().mkdir();
        }

        if (!SystemUtils.getConfigurationDirectory().exists()) {
            SystemUtils.getConfigurationDirectory().mkdir();
        }
    }
}
