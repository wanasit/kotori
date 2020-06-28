package com.github.wanasit.kotori.dictionary.utils

import org.rauschig.jarchivelib.ArchiverFactory
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.net.URL
import java.nio.file.Paths


fun downloadIntoDirectory(dir: File, url: String): File {
    val filename = Paths.get(URI(url).path).fileName.toString()
    val downloadDestination = File(dir, filename)

    println("Downloading '${url}' into '${downloadDestination.path}'")
    URL(url).openStream().use { input ->
        FileOutputStream(downloadDestination).use { output ->
            input.copyTo(output)
        }
    }

    return downloadDestination
}

fun extractIntoDirectory(dir: File, archivedFile: File) {
    println("Extracting '${archivedFile.path}' into '${dir.path}'")
    val archiver = ArchiverFactory.createArchiver(archivedFile)
    archiver.extract(archivedFile, dir)
}