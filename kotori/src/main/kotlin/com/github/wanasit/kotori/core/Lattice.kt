package com.github.wanasit.kotori.core
import com.github.wanasit.kotori.ConnectionCost
import com.github.wanasit.kotori.TermID
import com.github.wanasit.kotori.optimized.arrays.IndexedIntArray
import java.util.ArrayList

interface LatticeBuilder {
    fun createLattice(connection: ConnectionCost, size: Int): Lattice
}

interface Lattice {
    fun addNode(node: LatticeNode)
    fun hasNodeStartingAtIndex(index: Int): Boolean
    fun hasNodeEndingAtIndex(index: Int): Boolean

    fun findPath(): List<LatticeNode>?
}

class LatticeNode (
        val startIndex: Int,
        val endIndex: Int,

        val termID: TermID,
        val leftId: Int,
        val rightId: Int,
        val cost: Int
)

typealias NodeId = Int

object Lattices : LatticeBuilder {

    override fun createLattice(connection: ConnectionCost, size: Int): Lattice {
        return FixedSizeLazyComputeLattice(connection, size)
    }

    private const val NODE_ID_NONE = -1
    private const val NODE_ID_BEGIN = -2

    /**
     * A lattice implementation that keep all inserted nodes (indexed by their start/end locations).
     * The implementation connects nodes and computes the cost once in findPath() by checking each i-th location.
     */
    class FixedSizeLazyComputeLattice(
            private val connection: ConnectionCost,
            private val length: Int
    ) : Lattice {
        private var nodes: MutableList<LatticeNode> = ArrayList(32)
        private val startLocationIndex: IndexedIntArray = IndexedIntArray(length)
        private val endLocationIndex: IndexedIntArray = IndexedIntArray(length + 1)
        init {
            endLocationIndex.insert(0, NODE_ID_BEGIN)
        }

        override fun hasNodeStartingAtIndex(index: Int): Boolean = startLocationIndex.hasMemberAtIndex(index)

        override fun hasNodeEndingAtIndex(index: Int): Boolean = endLocationIndex.hasMemberAtIndex(index)

        override fun addNode(node: LatticeNode) {
            val nodeId: NodeId = nodes.size
            nodes.add(node)
            startLocationIndex.insert(node.startIndex, nodeId)
            endLocationIndex.insert(node.endIndex, nodeId)
        }

        override fun findPath(): List<LatticeNode>? {
            val totalCosts = IntArray(nodes.size) { nodes[it].cost }
            val previousNodes = IntArray(nodes.size) { NODE_ID_NONE }
            initializeStartingNodes(totalCosts, previousNodes)

            for (i in 1 until length) {
                startLocationIndex.forEachNodeAt(i) { rightNodeId, rightNode ->
                    var minPrevNode: Int = NODE_ID_NONE
                    var minPrevCost = Int.MAX_VALUE

                    endLocationIndex.forEachNodeAt(i) { leftNodeId, leftNode ->
                        if (previousNodes[leftNodeId] != NODE_ID_NONE) {
                            val cost = connection.lookup(leftNode.rightId, rightNode.leftId)
                            val prevTotalCost = totalCosts[leftNodeId] + cost
                            if (prevTotalCost < minPrevCost) {
                                minPrevCost = prevTotalCost
                                minPrevNode = leftNodeId
                            }
                        }
                    }

                    if (minPrevNode != NODE_ID_NONE) {
                        previousNodes[rightNodeId] = minPrevNode
                        totalCosts[rightNodeId] += minPrevCost
                    }
                }
            }

            val endNodeId = findEndingNodeId(previousNodes, totalCosts)
            return transverse(previousNodes, endNodeId)
        }

        private fun findEndingNodeId(previousNodes: IntArray, totalCosts: IntArray) : Int {
            var minEndingNodeId: Int = NODE_ID_NONE
            var minEndingCost = Int.MAX_VALUE

            endLocationIndex.forEachNodeAt(length) { endingNodeId, endingNode ->
                if (previousNodes[endingNodeId] != NODE_ID_NONE) {
                    val cost = connection.lookup(endingNode.rightId, 0)
                    val prevTotalCost = totalCosts[endingNodeId] + cost
                    if (prevTotalCost < minEndingCost) {
                        minEndingCost = prevTotalCost
                        minEndingNodeId = endingNodeId
                    }
                }
            }

            return minEndingNodeId
        }

        private fun initializeStartingNodes(totalCosts: IntArray, previousNodes: IntArray) {
            startLocationIndex.forEachNodeAt(0) { rightNodeId, rightNode ->
                val cost = connection.lookup(0, rightNode.leftId)
                totalCosts[rightNodeId] += cost
                previousNodes[rightNodeId] = NODE_ID_BEGIN
            }
        }

        private fun transverse(prevNodes: IntArray, endNodeId: Int): List<LatticeNode>? {
            if (endNodeId == NODE_ID_NONE) {
                return null
            }

            val nodeIdInPath = IntArray(length)
            var nodeIdInPathCount = 0

            var currentNodeId = endNodeId
            while (prevNodes[currentNodeId] != NODE_ID_BEGIN) {
                nodeIdInPath[nodeIdInPathCount++] = currentNodeId
                currentNodeId = prevNodes[currentNodeId]
            }

            nodeIdInPath[nodeIdInPathCount] = currentNodeId
            return IntProgression.fromClosedRange(nodeIdInPathCount, 0, -1).map { nodes[nodeIdInPath[it]] }
        }

        private inline fun IndexedIntArray.forEachNodeAt(index: Int, apply: (NodeId, LatticeNode) -> Unit) {
            this.forEachMembersAt(index) { apply(it, nodes[it]) }
        }
    }
}