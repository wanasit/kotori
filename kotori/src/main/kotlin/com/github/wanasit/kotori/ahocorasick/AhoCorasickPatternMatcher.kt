package com.github.wanasit.kotori.ahocorasick

interface PatternMatcher<T>{
    fun processNextChar(char: Char): Iterable<T>
}

interface PatternMatchingStrategy<T> {

    fun matcher(): PatternMatcher<T>

    fun findAllMatches(text: String): Array<out List<T>> {
        val results: Array<MutableList<T>> = Array(text.length) { mutableListOf<T>() }
        val matcher = this.matcher()
        text.forEachIndexed { index, c ->
            results[index].addAll(matcher.processNextChar(c))
        }

        return results;
    }
}

class AhoCorasickPatternMatcher<T> (
        private val patternEntries: List<Pair<String, T>>
) : PatternMatchingStrategy<T> {

    private val ahoCorasick: AhoCorasick
    private val outputTable: Map<State, IntArray>

    init {
        val builder = AhoCorasick.Builder()

        val outputTable: MutableMap<State, MutableSet<Int>> = mutableMapOf()
        patternEntries.forEachIndexed{ patternIndex, (pattern, _) ->
            val inputSeq = pattern.chars().toArray()
            val state = builder.put(*inputSeq);
            outputTable.getOrPut(state, { mutableSetOf() })
                    .add(patternIndex)
        }

        this.ahoCorasick = builder.build()
        this.outputTable = IntRange(0, ahoCorasick.size() - 1)
                .map { it to collectFallbackOutput(ahoCorasick, outputTable, it).sorted().toIntArray() }
                .toMap()
    }


    override fun matcher(): PatternMatcher<T> {
        var currentState = DFA.ROOT;

        return object : PatternMatcher<T> {
            override fun processNextChar(char: Char): Iterable<T> {
                currentState = ahoCorasick.nextState(currentState, char.toInt())
                return outputTable[currentState]?.map { patternEntries[it].second } ?: emptyList()
            }
        }
    }
}

private fun collectFallbackOutput(ahoCorasick: AhoCorasick, outputTable:Map<State, Set<Int>>, state: State): Set<Int> {
    val outputs = outputTable[state]?.toMutableSet() ?: mutableSetOf()

    var currentState = state
    while (currentState != DFA.ROOT) {
        currentState = ahoCorasick.fallback(currentState)
        outputs.addAll(outputTable[currentState] ?: listOf())
    }
    return outputs
}