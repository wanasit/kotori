import com.github.wanasit.kotori.Token
import com.github.wanasit.kotori.Tokenizer
import com.github.wanasit.kotori.kuromoji.Kuromoji
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestCompareWithKuromoji{

    private val tokenizer = Tokenizer.createDefaultTokenizer()
    private val baseLineTokenizer = Kuromoji.loadTokenizer()

    @Test fun testBasicTokenize() {
        val text = "そこではなしは終わりになった"
        val tokens = tokenizer.tokenize(text)
        val baseLineTokens = baseLineTokenizer.tokenize(text)
        assertTokensEqual(baseLineTokens, tokens)
    }

    @Test fun testWithEnglish() {
        val text = "GoogleがAndroid向け点字キーボードを発表"
        val tokens = tokenizer.tokenize(text)
        val baseLineTokens = baseLineTokenizer.tokenize(text)
        assertTokensEqual(baseLineTokens, tokens)
    }

    @Test fun testWithPunctuations() {
        val text = "...形にとらわれない創作活動も。 \n\n...子は男の子2人。\n"
        val tokens = tokenizer.tokenize(text)
        val baseLineTokens = baseLineTokenizer.tokenize(text)
        assertTokensEqual(baseLineTokens, tokens)
    }

    @Test fun testWithMorePunctuations() {
        val text = "好きなものは... \t\n　　子は男の子2人。"
        val tokens = tokenizer.tokenize(text)
        val baseLineTokens = baseLineTokenizer.tokenize(text)
        assertTokensEqual(baseLineTokens, tokens)
    }

    @Test fun testWithRandomText() {
        val text = "FMラジオ放送局、IT系での仕事人生活を経て、" +
                "フリーランスモノ書き。好きなものは、クラゲ、ジュゴン、宇宙、絵本、コドモ、ヘンテコなもの。" +
                "座右の銘は「明日地球がなくなるかもしれないから、今すぐ食べる」。" +
                "モノを書く以外にも、イラストレーターと合同でカフェでの作品展示など、形にとらわれない創作活動も。\n\n" +
                "木漏れ日の下で読書と昼寝をする生活と絵本に携わることを夢見て、日々生きています。子は男の子2人。\n"
        val tokens = tokenizer.tokenize(text)
        val baseLineTokens = baseLineTokenizer.tokenize(text)
        assertTokensEqual(baseLineTokens, tokens)
    }

    private fun assertTokensEqual(baseLineTokens: List<Token>, tokens: List<Token>) {
        assertEquals(baseLineTokens.map { it.text }, tokens.map { it.text })
    }
}
