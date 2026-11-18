package com.github.oxc.project.oxcintellijplugin.lsp

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class FileUriTest : BasePlatformTestCase() {

    fun testUnixPath() {
        assertEquals("file:///home/user/project/index.js",
            pathToFileUri("/home/user/project/index.js"))
    }

    fun testPathIsEncoded() {
        assertEquals("file:///home/user/my%20project/index.js",
            pathToFileUri("/home/user/my project/index.js"))
    }

    fun testWindowsDrivePath() {
        assertEquals("file:///c%3A/Users/user/project/index.js",
            pathToFileUri("C:/Users/user/project/index.js"))
        assertEquals("file:///c%3A/Users/user/project/index.js",
            pathToFileUri("C:\\Users\\user\\project\\index.js"))
    }

    fun testUncPath() {
        assertEquals("file://wsl.localhost/Ubuntu/home/user/index.js",
            pathToFileUri("//wsl.localhost/Ubuntu/home/user/index.js"))
    }
}
