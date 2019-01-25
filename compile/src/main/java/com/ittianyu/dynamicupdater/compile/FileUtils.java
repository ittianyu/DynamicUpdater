package com.ittianyu.dynamicupdater.compile;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Deque;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    private static final int BUFFER_SIZE = 8192;

    public static String getFileNameWithoutExt(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1).replaceAll("\\..*", "");
    }

    public static boolean copyToFile(File srcFile, File file) {
        try {
            return copyToFile(new FileInputStream(srcFile), file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

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
        if (closeable == null) {
            return;
        }
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


    public static String readAll(File file) {
        String encoding = "UTF-8";
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(filecontent);
            return new String(filecontent, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean write(File file, String content) {
        if (file.exists()) {
            file.delete();
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void unzip(File zipFile, File directory) throws IOException {
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> entries = zfile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            File file = new File(directory, entry.getName());
            if (entry.isDirectory()) {
                file.mkdirs();
            } else {
                file.getParentFile().mkdirs();
                InputStream in = zfile.getInputStream(entry);
                FileOutputStream out = new FileOutputStream(file);
                try {
                    copy(in, out);
                } finally {
                    in.close();
                    out.close();
                }
            }
        }
    }

    public static void zip(File directory, File zipFile) throws IOException {
        URI base = directory.toURI();
        Deque<File> queue = new LinkedList<File>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(zipFile);
        Closeable res = out;
        try {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty()) {
                directory = queue.pop();
                for (File kid : directory.listFiles()) {
                    String name = base.relativize(kid.toURI()).getPath();
                    if (kid.isDirectory()) {
                        queue.push(kid);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    } else {
                        zout.putNextEntry(new ZipEntry(name));
                        FileInputStream in = new FileInputStream(kid);
                        copy(in, zout);
                        in.close();
                        zout.closeEntry();
                    }
                }
            }
        } finally {
            res.close();
        }
    }

    public static void main(String[] args) throws IOException {
        URL apkUrl = FileUtils.class.getClassLoader()
            .getResource("click_count-release-unsigned.apk");
        URL dirUrl = FileUtils.class.getClassLoader().getResource("apk");
        File apk = new File(apkUrl.getFile());
        File dir = new File(dirUrl.getFile());
        System.out.println(apk);
        System.out.println(dir);
        unzip(apk, dir);
        delete(dir, "res", "resources.arsc", "AndroidManifest.xml");
        zip(dir, new File(dir.getParentFile(), apk.getName()));
    }

    public static void delete(File file) {
        if (!file.isDirectory()) {
            file.delete();
            return;
        }
        delete(file, file.list());
    }

    public static void delete(File dir, String... names) {
        if (null == names) {
            return;
        }
        for (String name : names) {
            File file = new File(dir, name);
            if (file.isDirectory()) {
                delete(file, file.list());
            }
            file.delete();
        }
    }
}
