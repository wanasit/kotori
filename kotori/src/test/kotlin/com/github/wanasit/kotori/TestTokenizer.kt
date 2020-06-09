import com.github.wanasit.kotori.Tokenizer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private val tokenizer = Tokenizer.createDefaultTokenizer()

class TestTokenizer{

    @Test fun testBasicTokenize() {
        val tokens = tokenizer.tokenize("そこではなしは終わりになった")

        assertNotNull(tokens)
        assertEquals(7, tokens.size)

        assertEquals("そこで", tokens[0].text)
        assertEquals("はなし", tokens[1].text)
        assertEquals("は", tokens[2].text)
        assertEquals("終わり", tokens[3].text)
        assertEquals("に", tokens[4].text)
        assertEquals("なっ", tokens[5].text)
        assertEquals("た", tokens[6].text)
    }

    @Test fun testWithEnglish() {
        val tokens = tokenizer.tokenize("GoogleがAndroid向け点字キーボードを発表")

        assertNotNull(tokens)
        assertEquals(8, tokens.size)

        assertEquals("Google", tokens[0].text)
        assertEquals("が", tokens[1].text)
        assertEquals("Android", tokens[2].text)
        assertEquals("向け", tokens[3].text)
        assertEquals("点字", tokens[4].text)
        assertEquals("キーボード", tokens[5].text)
        assertEquals("を", tokens[6].text)
        assertEquals("発表", tokens[7].text)
    }

    @Test fun testEndWithSpaces() {
        val tokens = tokenizer.tokenize("子は男の子2人。\t\n")

        assertNotNull(tokens)
        assertEquals(7, tokens.size)

        assertEquals("子", tokens[0].text)
        assertEquals("は", tokens[1].text)
        assertEquals("男の子", tokens[2].text)
        assertEquals("2", tokens[3].text)
        assertEquals("人", tokens[4].text)
        assertEquals("。", tokens[5].text)
        assertEquals("\t\n", tokens[6].text)
    }

    @Test fun testLongerText() {
        val tokens = tokenizer.tokenize("FMラジオ放送局、IT系での仕事人生活を経て、" +
                "フリーランスモノ書き。好きなものは、クラゲ、ジュゴン、宇宙、絵本、コドモ、ヘンテコなもの。" +
                "座右の銘は「明日地球がなくなるかもしれないから、今すぐ食べる」。" +
                "モノを書く以外にも、イラストレーターと合同でカフェでの作品展示など、形にとらわれない創作活動も。\n\n" +
                "木漏れ日の下で読書と昼寝をする生活と絵本に携わることを夢見て、日々生きています。子は男の子2人。\n")

        assertNotNull(tokens)
        assertTrue(tokens.isNotEmpty())
    }
}
