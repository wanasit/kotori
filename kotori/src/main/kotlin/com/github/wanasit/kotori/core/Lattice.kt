package com.github.wanasit.kotori.core
import com.github.wanasit.kotori.ConnectionCost
import com.github.wanasit.kotori.TermEntry

data class LatticeNode(
        val location: Int,
        val totalCost: Int,
        val termEntry: TermEntry,
        val previousNode: LatticeNode?
)

internal val BEGIN_NODE = LatticeNode(0, 0, object : TermEntry {
    override val surfaceForm = ""
    override val leftId = 0
    override val rightId = 0
    override val cost = 0
}, null)

class Lattice(
        private val connection: ConnectionCost
) {
    private val nodesAtEndIndex: MutableMap<Int, MutableList<LatticeNode>> = mutableMapOf()
    init {
        nodesAtEndIndex[0] = mutableListOf(BEGIN_NODE)
    }

    fun addNode(term: TermEntry, startIndex: Int,
                endIndex: Int = startIndex + term.surfaceForm.length) {
        
        val (prevNode, prevCost) = findPossibleConnections(startIndex, term.leftId)
                .minBy {(_, prevCost) -> prevCost}
                ?: return // If there is not possible connection just ignore

        nodesAtEndIndex.getOrPut(endIndex, { mutableListOf() })
                .add(LatticeNode(
                        location = startIndex,
                        totalCost = prevCost + term.cost,
                        termEntry = term,
                        previousNode = prevNode))
    }

    fun close(endIndex: Int) : List<LatticeNode>? {

        val (prevNode, _) = findPossibleConnections(endIndex, 0)
                .minBy {(_, prevCost) -> prevCost}

                ?: return null // If there is not possible connection just ignore

        return transversePath(prevNode)
    }

    private fun findPossibleConnections(location: Int, leftId: Int) : Iterable<Pair<LatticeNode, Int>>{

        val results: MutableList<Pair<LatticeNode, Int>> = mutableListOf()
        nodesAtEndIndex[location]?.forEach {
            val connectionCost = connection.lookup(it.termEntry.rightId, leftId)
            if (connectionCost != null) {
                val newTotalCost = it.totalCost + connectionCost
                results.add(it to newTotalCost)
            }
        }

        return results
    }

    private fun transversePath(node: LatticeNode) : List<LatticeNode> {
        val path = mutableListOf<LatticeNode>()
        var currentNode: LatticeNode = node
        while (currentNode.previousNode != null) {
            path.add(currentNode)
            currentNode = currentNode.previousNode!!
        }
        return path.reversed()
    }
}