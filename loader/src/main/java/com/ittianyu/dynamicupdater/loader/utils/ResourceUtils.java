package com.ittianyu.dynamicupdater.loader.utils;

import android.content.Context;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ResourceUtils {

    private static final List<Dpi> DPIS = Arrays.asList(
        new Dpi(640, "xxxhdpi"),
        new Dpi(480, "xxhdpi"),
        new Dpi(320, "xhdpi"),
        new Dpi(240, "hdpi"),
        new Dpi(160, "mdpi"),
        new Dpi(120, "ldpi")
    );
    private static final String STRINGS = "strings.json";
    private static final String COLORS = "colors.json";
    private static final String DRAWABLE = "drawable";
    public static final String VALUES = "values";

    public static String getLanguage() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage();
    }

    public static int getDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static List<String> getStringsPathList() {
        return getValuePathList(STRINGS);
    }

    public static List<String> getColorsPathList() {
        return getValuePathList(COLORS);
    }

    public static List<String> getDrawablePathList(Context context) {
        int dpi = ResourceUtils.getDpi(context);
        int index = ResourceUtils.getMatchedDpiIndex(dpi);
        if (index == -1) {
            return Arrays.asList(getDrawablePath(null));
        }

        List<Dpi> dpiList = getDpiList();
        List<String> result = new ArrayList<>(dpiList.size() + 1);
        // 向上找高密度
        for (int i = index; i >= 0; i--) {
            result.add(getDrawablePath(dpiList.get(i)));
        }
        // 默认密度
        result.add(getDrawablePath(null));
        // 向下找低密度
        for (int i = index + 1; i < dpiList.size(); i++) {
            result.add(getDrawablePath(dpiList.get(i)));
        }
        return result;
    }

    public static String getDrawablePath(Dpi dpi) {
        if (null == dpi) {
            return DRAWABLE;
        }
        return DRAWABLE + "-" + dpi.name;
    }

    public static List<Dpi> getDpiList() {
        return DPIS;
    }

    public static int getMatchedDpiIndex(int dpi) {
        List<Dpi> list = getDpiList();
        for (int i = 0; i < list.size(); i++) {
            if (dpi >= list.get(i).dpi)
                return i;
        }
        return -1;
    }

    public static Dpi getMatchedDpi(int dpi) {
        int index = getMatchedDpiIndex(dpi);
        if (index == -1) {
            return null;
        }
        return getDpiList().get(index);
    }

    public static class Dpi {
        public final int dpi;
        public final String name;

        public Dpi(int dpi, String name) {
            this.dpi = dpi;
            this.name = name;
        }
    }

    public static List<String> getValuePathList(String name) {
        String language = getLanguage();
        return Arrays.asList(VALUES + "-" + language + File.separator + name, VALUES + File.separator + name);
    }

    public static String toString(InputStream in) {
        if (null == in) {
            return null;
        }
        try {
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
