import com.github.wanasit.kotori.Dictionary
import com.github.wanasit.kotori.core.LatticeBasedTokenizer
import com.github.wanasit.kotori.connectionTable
import com.github.wanasit.kotori.fakeTermDictionary
import com.github.wanasit.kotori.fakeTermDictionaryWithoutFeature
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TestTokenizerWithDictionary{

    @Test fun testTokenizer() {

        val termDictionary = fakeTermDictionaryWithoutFeature {
            term("そこで", CONJ, 10)
            term("そこ", NOUN, 40)
            term("で", VERB, 40)
            term("で", ADJ, 10)
            term("はなし", NOUN, 40)
            term("は", VERB, 10)
            term("なし", NOUN, 40)
            term("終わり", NOUN, 40)
            term("になった", VERB, 40)
            term("に", ADJ, 10)
            term("なった", VERB, 40)
        }

        val connectionCost = connectionTable {
            header(     END,    NOUN,   VERB,   ADJ,    CONJ)
            row(BEGIN,  0,      10,     10,     0,      10)
            row(NOUN,   10,     10,     40,     10,      0)
            row(VERB,   10,     10,     10,     0,      10)
            row(ADJ,    10,     10,     10,     10,     10)
            row(CONJ,   0,      10,     10,     0,      10)
        }

        val dictionary = Dictionary(termDictionary, connectionCost)
        val tokenizer = LatticeBasedTokenizer(dictionary)

        val tokens = tokenizer.tokenize("そこではなしは終わりになった")

        assertNotNull(tokens)
        assertEquals(6, tokens.size)

        assertEquals("そこで", tokens[0].text)
        assertEquals("はなし", tokens[1].text)
        assertEquals("は", tokens[2].text)
        assertEquals("終わり", tokens[3].text)
        assertEquals("に", tokens[4].text)
        assertEquals("なった", tokens[5].text)
    }
}
