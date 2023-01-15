package cn.watchdog.accountprotection.utils;

import net.md_5.bungee.api.*;

import java.util.*;

/**
 * 字符串转换工具类
 */
public class StringUtils {
    private static String getColoredString(String string) {
        return string.replace("&", "§");
    }

    /**
     * 字符串颜色化
     *
     * @param string 文本
     * @return String 颜色化后的字符串
     */
    public static String translateColorCodes(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * 删除字符串的颜色
     *
     * @param string 颜色化字符串
     * @return String 删除颜色化后的字符串
     */
    public static String removeColorCodes(String string) {
        return ChatColor.stripColor(getColoredString(string));
    }

    /**
     * 字符串列表颜色化
     *
     * @param text 字符串列表
     * @return List<String> 颜色化后的字符串列表
     */
    public static List<String> translateColorCodes(List<String> text) {
        ArrayList<String> list = new ArrayList<>();
        for (String line : text) {
            list.add(translateColorCodes(line));
        }
        return list;
    }

    /**
     * 替换列表中的字符串
     *
     * @param text    目标字符串
     * @param oldChar 被替换字符串
     * @param newChar 需替换字符串
     * @return List<String> 替换完成后的字符串列表
     */
    public static List<String> replace(List<String> text, String oldChar, String newChar) {
        ArrayList<String> list = new ArrayList<>();
        for (String line : text) {
            list.add(line.replace(oldChar, newChar));
        }
        return list;
    }

    public static List<String> getStringList(String str) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            list.add(str);
        }
        return list;
    }

    /**
     * 返回一个字符串数组作为单个字符串。
     *
     * @param args  数组
     * @param start 开始的索引
     * @return 将数组作为字符串
     */
    public static String consolidateStrings(String[] args, int start) {
        String ret = args[start];
        if (args.length > (start + 1)) {
            for (int i = (start + 1); i < args.length; i++)
                ret = ret + " " + args[i];
        }
        return ret;
    }
}
