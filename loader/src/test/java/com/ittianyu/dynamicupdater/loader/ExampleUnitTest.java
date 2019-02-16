package com.ittianyu.dynamicupdater.loader;

import com.ittianyu.dynamicupdater.loader.utils.FileUtils;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_FileUtils_getFileNameWithoutExt() {
        String fileName = "c.aaa";
        String path = "aaa" + File.separator + "bbb" + File.separator + fileName + ".txt";

        assertEquals(fileName, FileUtils.getFileNameWithoutExt(path));
    }


}