package com.ittianyu.dynamicupdater.loader.utils;

import android.content.Context;
import android.os.Environment;
import java.io.File;

public class DirUtils {

    public static String getDirPath(Context context, String dirName) {
        if (hasExternalStorage()) {
            File file = null;
            File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (dir != null) {
                file = new File(dir.getPath() + File.separator + dirName);
                file.mkdirs();
            }

            if (file != null) {
                return file.getAbsolutePath();
            }
        }

        return getDirPathInner(context, dirName);
    }

    private static boolean hasExternalStorage() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
            || Environment.isExternalStorageRemovable();
    }

    private static String getDirPathInner(Context context, String dirName) {
        File file = new File(context.getFilesDir().getPath() + File.separator + dirName);
        file.mkdirs();
        return file.getAbsolutePath();
    }

}
