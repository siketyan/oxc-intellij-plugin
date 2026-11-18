package com.github.oxc.project.oxcintellijplugin.lsp

import com.intellij.openapi.util.io.OSAgnosticPathUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.io.URLUtil

/**
 * Converts a file to a `file:` URI for the language server, after mapping its path to the
 * server's target environment (e.g. WSL) with [toTargetPath].
 *
 * Since 2026.3 (263), `LspClientDescriptor.getFilePath` is internal API, so the path conversion is
 * done when building the URI instead. Returns null for files outside the local file system so that
 * the platform's default behavior is used for them.
 */
fun targetFileUri(file: VirtualFile, toTargetPath: (String) -> String): String? {
    if (!file.isInLocalFileSystem) return null
    return pathToFileUri(toTargetPath(file.path))
}

/**
 * Builds a `file:` URI from an absolute path in the same shape as the platform does for LSP:
 * Windows drive letters are lower-cased and their colon is percent-encoded
 * (e.g. `file:///c%3A/foo`), and UNC paths keep their authority (e.g. `file://server/share`).
 */
fun pathToFileUri(path: String): String {
    val normalized = path.replace('\\', '/')
    if (OSAgnosticPathUtil.startsWithWindowsDrive(normalized)) {
        return "file:///${normalized[0].lowercaseChar()}%3A${URLUtil.encodePath(normalized.substring(2))}"
    }

    val encodedPath = URLUtil.encodePath(normalized)
    if (normalized.startsWith("//")) return "file:$encodedPath"

    val url = VirtualFileManager.constructUrl(URLUtil.FILE_PROTOCOL, encodedPath)
    return VfsUtil.toUri(url)?.toString() ?: url
}
