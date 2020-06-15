package com.github.wanasit.kotori.utils

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

object IO {

    /**
     *
     */
    object String {


    }


    object Int {

    }

}



object IOUtils {

    fun writeStringArray(outputStream: OutputStream, value: Array<String>, includeSize: Boolean = true) {
        val dataOutputStream = DataOutputStream(outputStream)
        if (includeSize) {
            dataOutputStream.writeInt(value.size)
        }

        val lengthArray = value.map { it.length }.toIntArray()
        writeIntArray(outputStream, lengthArray, includeSize = false)
        value.forEach {
            dataOutputStream.writeChars(it)
        }
    }

    fun writeIntArray(outputStream: OutputStream, value: IntArray, includeSize: Boolean = true) {
        val dataOutputStream = DataOutputStream(outputStream)
        if (includeSize) {
            dataOutputStream.writeInt(value.size)
        }


        value.forEach { dataOutputStream.writeInt(it) }
    }

    fun writeShortArray(outputStream: OutputStream, value: ShortArray, includeSize: Boolean = true) {
        val dataOutputStream = DataOutputStream(outputStream)
        if (includeSize) {
            dataOutputStream.writeInt(value.size)
        }

        value.forEach { dataOutputStream.writeShort(it.toInt()) }
    }

    fun writeInt(outputStream: OutputStream, value: Int) {
        val dataOutputStream = DataOutputStream(outputStream)
        dataOutputStream.writeInt(value)
    }

    fun readStringArray(inputStream: InputStream): Array<String>  {
        val dataInputStream = DataInputStream(inputStream)
        val size = dataInputStream.readInt()
        return readStringArray(inputStream, size)
    }

    fun readStringArray(inputStream: InputStream, size: Int): Array<String> {
        val lengthArray = readIntArray(inputStream, size)
        return readStringArray(inputStream, lengthArray)
    }

    fun readStringArray(inputStream: InputStream, stringLengthArray: IntArray): Array<String> {
        val output = Array(stringLengthArray.size) {
            val bytes = inputStream.readNBytes(stringLengthArray[it] * 2)
            String(bytes, Charsets.UTF_16)
        }

        return output
    }


    fun readShortArray(inputStream: InputStream): ShortArray {
        val dataInputStream = DataInputStream(inputStream)
        val size = dataInputStream.readInt()
        return readShortArray(inputStream, size)
    }

    fun readShortArray(inputStream: InputStream, size: Int): ShortArray {
        val dataInputStream = DataInputStream(inputStream)
        val output = ShortArray(size)
        ByteBuffer.wrap(dataInputStream.readNBytes(size * 2)).asShortBuffer().get(output)
        return output
    }

    fun readIntArray(inputStream: InputStream): IntArray {
        val dataInputStream = DataInputStream(inputStream)
        val size = dataInputStream.readInt()
        return readIntArray(inputStream, size)
    }

    fun readIntArray(inputStream: InputStream, size: Int): IntArray {
        val dataInputStream = DataInputStream(inputStream)
        val output = IntArray(size)
        ByteBuffer.wrap(dataInputStream.readNBytes(size * 4)).asIntBuffer().get(output)
        return output
    }

    fun readInt(inputStream: InputStream): Int {
        val dataInputStream = DataInputStream(inputStream)
        return dataInputStream.readInt()
    }
}