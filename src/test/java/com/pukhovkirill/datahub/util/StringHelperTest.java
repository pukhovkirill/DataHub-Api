package com.pukhovkirill.datahub.util;

import org.junit.jupiter.api.Test;

import static com.pukhovkirill.datahub.util.StringHelper.*;
import static org.junit.jupiter.api.Assertions.*;

class StringHelperTest {

    @Test
    public void testExtractName() {
        String filename = "file.txt";
        String path = "/some/path/to/"+filename;


        String result = extractName(path);


        assertNotNull(result);
        assertEquals(filename, result);
    }

    @Test
    public void testExtractNameWithEmptyString(){
        String path = "";


        String result = extractName(path);


        assertNotNull(result);
        assertEquals(path, result);
    }

    @Test
    public void testExtractNameWithInvalidPath(){
        String filename = "file.txt";
        String path = "\\/\\/\\/\\./some\\/path\\/to\\/"+filename;


        String result = extractName(path);


        assertNotNull(result);
        assertEquals(filename, result);
    }

    @Test
    public void testGenerateUniqueFilename() {
        String path = "/example/file.txt";
        String expected = "/example/file(1).txt";

        String result = generateUniqueFilename(path);

        assertEquals(expected, result);
    }

    @Test
    public void testGenerateUniqueFilenameWithNumberInBrackets() {
        String path = "/example/file(1).txt";
        String expected = "/example/file(2).txt";

        String result = generateUniqueFilename(path);

        assertEquals(expected, result);
    }

    @Test
    public void testGenerateUniqueFilenameWithMultipleDigitsInBrackets() {
        String path = "/example/file(123).txt";
        String expected = "/example/file(124).txt";

        String result = generateUniqueFilename(path);

        assertEquals(expected, result);
    }

    @Test
    public void testGenerateUniqueFilenameWithoutExtension() {
        String path = "/example/file";
        String expected = "/example/file(1)";

        String result = generateUniqueFilename(path);

        assertEquals(expected, result);
    }

    @Test
    public void testGenerateUniqueFilenameWithInvalidBrackets() {
        String path = "/example/file(abc).txt";
        String expected = "/example/file(abc)(1).txt";

        String result = generateUniqueFilename(path);

        assertEquals(expected, result);
    }

    @Test
    public void testGenerateUniqueFilenameWithEmptyPath() {
        String path = "";
        String result = generateUniqueFilename(path);

        assertEquals("", result);
    }

    @Test
    public void testGenerateUniqueFilenameInNestedDirectory() {
        String path = "/example/subfolder/file.txt";
        String expected = "/example/subfolder/file(1).txt";

        String result = generateUniqueFilename(path);

        assertEquals(expected, result);
    }
}