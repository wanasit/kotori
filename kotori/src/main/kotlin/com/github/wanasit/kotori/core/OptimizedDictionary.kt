package com.github.wanasit.kotori.core

import com.github.wanasit.kotori.ConnectionCost

object OptimizedDictionary {

    class ConnectionCostArray(
            private val fromIdCardinality: Int,
            private val toIdCardinality: Int
    ) : ConnectionCost {

        private val table: IntArray = IntArray(fromIdCardinality * toIdCardinality) { 0 }

        override fun lookup(fromRightId: Int, toLeftId: Int): Int? {
            return this.table[index(fromRightId, toLeftId)]
        }

        fun put(fromId: Int, toId: Int, cost: Int) {
            table[index(fromId, toId)] = cost
        }

        private fun index(fromId: Int, toId: Int) : Int {
            return fromIdCardinality * toId + fromId
        }
    }
}