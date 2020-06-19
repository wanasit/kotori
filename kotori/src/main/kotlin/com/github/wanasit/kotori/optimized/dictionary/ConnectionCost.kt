package com.github.wanasit.kotori.optimized.dictionary

import com.github.wanasit.kotori.ConnectionCost
import com.github.wanasit.kotori.TermEntry
import com.github.wanasit.kotori.utils.IOUtils
import java.io.InputStream
import java.io.OutputStream
import java.lang.UnsupportedOperationException

class ConnectionCostArray(
        val fromIdCardinality: Int,
        val toIdCardinality: Int,
        internal val table: ShortArray = ShortArray(fromIdCardinality * toIdCardinality) { 0 }
) : ConnectionCost {

    override fun lookup(fromRightId: Int, toLeftId: Int): Int {
        return this.table[index(fromRightId, toLeftId)].toInt()
    }

    fun put(fromId: Int, toId: Int, cost: Int) {
        if (cost<Short.MIN_VALUE || cost > Short.MAX_VALUE) {
            throw UnsupportedOperationException()
        }

        table[index(fromId, toId)] = cost.toShort()
    }

    private fun index(fromId: Int, toId: Int) : Int {
        return fromIdCardinality * toId + fromId
    }

    companion object {

        fun copyOf(entries: Iterable<TermEntry>, connectionCost: ConnectionCost) : ConnectionCostArray {
            val maxLeftId = entries.map { it.leftId }.max()!!
            val maxRightId = entries.map { it.rightId }.max()!!
            return copyOf(maxRightId + 1, maxLeftId + 1, connectionCost)
        }

        fun copyOf(fromIdCardinality: Int, toIdCardinality: Int, connectionCost: ConnectionCost) : ConnectionCostArray {
            val array = ConnectionCostArray(fromIdCardinality, toIdCardinality)
            for (i in 0 until fromIdCardinality) {
                for (j in 0 until toIdCardinality) {
                    array.put(i, j, connectionCost.lookup(i, j))
                }
            }

            return array
        }

        fun readFromInputStream(inputStream: InputStream) : ConnectionCostArray {
            return ConnectionCostArray(
                    IOUtils.readInt(inputStream), IOUtils.readInt(inputStream), IOUtils.readShortArray(inputStream))
        }
    }

    fun writeToOutputStream(outputStream: OutputStream) {
        IOUtils.writeInt(outputStream, fromIdCardinality)
        IOUtils.writeInt(outputStream, toIdCardinality)
        IOUtils.writeShortArray(outputStream, table)
    }
}