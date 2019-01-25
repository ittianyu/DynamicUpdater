package com.ittianyu.dynamicupdater.loader.utils;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.view.View;
import com.ittianyu.dynamicupdater.loader.Loader;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoaderUtils {
    public static final String DEX = "dex_";

    public static Loader getLoaderByConfig(ClassLoader classLoader) {
        InputStream in = classLoader.getResourceAsStream("loader.properties");
        Properties properties = new Properties();
        try {
            properties.load(in);
            String loaderClass = properties.getProperty("loader");
            return (Loader) classLoader.loadClass(loaderClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Loader getLoaderByConfig(String filePath, Context context) {
        ClassLoader classLoader = loadDex(context, filePath);
        return getLoaderByConfig(classLoader);
    }

    public static View loadByConfig(ClassLoader classLoader, Context context, Lifecycle lifecycle) {
        return getLoaderByConfig(classLoader).render(context, lifecycle);
    }

    public static View loadByConfig(String filePath, Context context, Lifecycle lifecycle) {
        return getLoaderByConfig(filePath, context).render(context, lifecycle);
    }

    public static Loader getLoaderByClass(ClassLoader classLoader, String className) {
        try {
            return (Loader) classLoader.loadClass(className).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Loader getLoaderByClass(String filePath, Context context, String className) {
        try {
            return (Loader) loadDex(context, filePath).loadClass(className).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static View loadByClass(ClassLoader classLoader, Context context, Lifecycle lifecycle, String className) {
        return getLoaderByClass(classLoader, className).render(context, lifecycle);
    }

    public static View loadByClass(String filePath, Context context, Lifecycle lifecycle, String className) {
        return getLoaderByClass(filePath, context, className).render(context, lifecycle);
    }

    public static boolean cleanDex(Context context, String filePath) {
        String id = FileUtils.getFileNameWithoutExt(filePath);
        final File optimizedDexOutputPath = context.getDir(DEX + id, Context.MODE_PRIVATE);
        return DirUtils.deleteDir(optimizedDexOutputPath);
    }

    public static ClassLoader loadDex(Context context, String filePath) {
        String id = FileUtils.getFileNameWithoutExt(filePath);
        final File optimizedDexOutputPath = context.getDir(DEX + id, Context.MODE_PRIVATE);
        return new DexClassLoader(filePath, optimizedDexOutputPath.getAbsolutePath(), null, context.getClassLoader());
    }

}
