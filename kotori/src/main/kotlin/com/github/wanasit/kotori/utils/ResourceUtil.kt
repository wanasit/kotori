package com.github.wanasit.kotori.utils

import java.io.InputStream
import java.util.zip.GZIPInputStream

object ResourceUtil {

    /**
     * Read Java resource as a stream.
     * Also automatically detect and handle `.gz` extension
     */
    fun readResourceAsStream(namespace: String, filename: String): InputStream {

        var resourcePath = "$namespace/$filename"
        var stream = javaClass.getResourceAsStream(resourcePath)

        if (stream == null) {

            if (resourcePath.endsWith(".gz")) {
                resourcePath = resourcePath.removeSuffix(".gz")
                stream = javaClass.getResourceAsStream(resourcePath)
            } else {
                resourcePath += ".gz"
                stream = javaClass.getResourceAsStream(resourcePath)
            }
        }

        stream = stream ?: throw IllegalArgumentException("Can't locate resource: $namespace/$filename")
        return if (resourcePath.endsWith(".gz")) GZIPInputStream(stream) else stream
    }
}