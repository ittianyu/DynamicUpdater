package com.ittianyu.dynamicupdater.loader.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static final int BUFFER_SIZE = 8192;

    public static boolean copyToFile(InputStream source, File file) {
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream sink = null;
        try {
            sink = new FileOutputStream(file);
            return copy(source, sink) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close(source);
            close(sink);
        }
    }

    private static void close(Closeable closeable) {
        if (closeable == null)
            return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long copy(InputStream source, OutputStream sink) throws IOException {
        long nread = 0L;
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = source.read(buf)) > 0) {
            sink.write(buf, 0, n);
            nread += n;
        }
        return nread;
    }

}
