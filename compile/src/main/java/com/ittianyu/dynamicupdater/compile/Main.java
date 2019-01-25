package com.ittianyu.dynamicupdater.compile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("please input module name!");
            return;
        }

        String gradle = gradle();
        System.out.println("gradle:" + gradle);
        if (gradle == null) {
            System.out.println("can't find gradle, please run at project dir!");
            return;
        }

        String moduleName = args[0];
        // backup build.gradle
        File buildFile = new File(String.format("./%s/build.gradle", moduleName));
        File bakFile = new File(buildFile.getAbsoluteFile() + ".bak");
        System.out.println("backup:" + buildFile.getAbsolutePath());
        if (!FileUtils.copyToFile(buildFile, bakFile)) {
            System.out.println("backup failed!");
            return;
        }

        //  build.gradle: replace releaseCompileOnly to releaseImplementation
        String content = FileUtils.readAll(bakFile);
        String replacedContent = content.replaceAll("releaseCompileOnly", "releaseImplementation");
        FileUtils.write(buildFile, replacedContent);

        // sync and build apk
        try {
            run(String.format(gradle + " :%s:clean :%s:assembleRelease", moduleName, moduleName, moduleName));
        } catch (Exception e) {
            // 还原 build.gradle
            restoreBuildFile(buildFile, bakFile);
            e.printStackTrace();
            return;
        }

        // restore build.gradle
        restoreBuildFile(buildFile, bakFile);

        // sync and build apk
        try {
            run(String.format(gradle + " :%s:generateDebugSources :%s:assembleRelease -x preReleaseBuild -x processReleaseResources", moduleName, moduleName));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // unzip and remove res in apk, zip finally
        File apkDir = new File(String.format("./%s/build/outputs/apk/release/", moduleName));
        if (!apkDir.exists()) {
            System.out.println("dir is not exist:" + apkDir.getAbsolutePath());
            return;
        }
        File[] files = apkDir.listFiles();
        for (File file : files) {
            if (file.getAbsolutePath().endsWith(".apk")) {
                try {
                    deleteResFromApk(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("--------compile plugin apk success!---------");
    }

    private static void run(String cmd) throws IOException, InterruptedException {
        System.out.println(cmd);
        Process process = Runtime.getRuntime().exec(cmd, null, new File("./"));
        BufferedReader bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
        BufferedReader bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));

        String line = null;
        while ((line = bufrIn.readLine()) != null) {
            System.out.println(line);
        }
        while ((line = bufrError.readLine()) != null) {
            System.out.println(line);
        }
        process.waitFor();
    }

    private static boolean restoreBuildFile(File buildFile, File bakFile) {
        buildFile.delete();
        return bakFile.renameTo(buildFile);
    }

    private static void deleteResFromApk(File file) throws IOException {
        File dir = new File("apk");
        System.out.println("unzip apk");
        FileUtils.unzip(file, dir);
        System.out.println("delete res resources.arsc AndroidManifest.xml from apk");
        FileUtils.delete(dir, "res", "resources.arsc", "AndroidManifest.xml");
        System.out.println("package apk");
        FileUtils.zip(dir, file);
        System.out.println("clean tmp dir");
        FileUtils.delete(dir);
    }

    private static String gradle() {
        File file = null;
        if (isWin()) {
            file = new File("./gradlew.bat");
        } else {
            file = new File("./gradlew");
        }
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return null;
    }

    private static boolean isWin() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }

}
