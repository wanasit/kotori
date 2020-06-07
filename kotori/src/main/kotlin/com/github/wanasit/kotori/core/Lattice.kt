package com.github.wanasit.kotori.core
import com.github.wanasit.kotori.ConnectionCost
import com.github.wanasit.kotori.TermEntry

data class LatticeNode(
        val location: Int,
        val termEntry: TermEntry,
        var totalCost: Int?,
        var previousNode: LatticeNode?
)

internal val BEGIN_NODE = LatticeNode(0, object : TermEntry {
    override val surfaceForm = ""
    override val leftId = 0
    override val rightId = 0
    override val cost = 0
}, 0, null)

class Lattice(
        length: Int
) {
    private val nodesByStartIndex: Array<MutableList<LatticeNode>> = Array(length) { mutableListOf<LatticeNode>() }
    private val nodesByEndIndex: Array<MutableList<LatticeNode>> = Array(length + 1) { mutableListOf<LatticeNode>() }
    init {
        nodesByEndIndex[0].add(BEGIN_NODE)
    }

    fun hasNodeStartAtIndex(index: Int) : Boolean = nodesByStartIndex[index].isNotEmpty()
    fun hasNodeEndAtIndex(index: Int) : Boolean = nodesByEndIndex[index].isNotEmpty()

    fun addNode(term: TermEntry, startIndex: Int, endIndex: Int = startIndex + term.surfaceForm.length) {
        val node = LatticeNode(location = startIndex, termEntry = term, totalCost = null, previousNode=null)
        nodesByStartIndex[startIndex].add(node)
        nodesByEndIndex[endIndex].add(node)
    }

    fun connectAndClose(connection: ConnectionCost) : List<LatticeNode>? {
        for (i in 1 until nodesByEndIndex.size) {
            nodesByEndIndex[i].forEach {
                var minPrevNode: LatticeNode? = null
                var minPrevCost = Int.MAX_VALUE

                for (prevNode in nodesByEndIndex[it.location]) {
                    if (prevNode.totalCost == null) {
                        continue
                    }

                    val cost = connection.lookup(prevNode.termEntry.rightId, it.termEntry.leftId)
                    if (cost != null) {
                        val prevTotalCost = prevNode.totalCost!! + cost
                        if (prevTotalCost < minPrevCost) {
                            minPrevCost = prevTotalCost
                            minPrevNode = prevNode
                        }
                    }
                }

                if (minPrevNode != null) {
                    it.previousNode = minPrevNode
                    it.totalCost = minPrevCost + it.termEntry.cost
                }
            }
        }


        var minPrevNode: LatticeNode? = null
        var minPrevCost = Int.MAX_VALUE

        for (prevNode in nodesByEndIndex.last()) {
            if (prevNode.totalCost == null) {
                continue
            }

            val connectionCost = connection.lookup(prevNode.termEntry.rightId, 0)
            if (connectionCost != null) {
                val prevTotalCost = prevNode.totalCost!! + connectionCost
                if (prevTotalCost < minPrevCost) {
                    minPrevCost = prevTotalCost
                    minPrevNode = prevNode
                }
            }
        }

        return minPrevNode?.transverse() ?: emptyList()
    }

    private fun LatticeNode.transverse() : List<LatticeNode> {

        val path = mutableListOf<LatticeNode>()
        var currentNode: LatticeNode = this
        while (currentNode.previousNode != null) {
            path.add(currentNode)
            currentNode = currentNode.previousNode!!
        }
        return path.reversed()
    }
}