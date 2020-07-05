package com.github.wanasit.kotori.dictionaries;


import com.github.wanasit.kotori.Dictionary;
import com.github.wanasit.kotori.Token;
import com.github.wanasit.kotori.Tokenizer;
import com.github.wanasit.kotori.dictionaries.Dictionaries;
import com.github.wanasit.kotori.mecab.MeCabLikeTermFeatures;
import com.github.wanasit.kotori.optimized.DefaultTermFeatures;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestTokenizerWithMeCabDictionary {

    final Dictionary<MeCabLikeTermFeatures> dictionary = Dictionaries.Mecab.loadIpadic();
    final Tokenizer<MeCabLikeTermFeatures> tokenizer = Tokenizer.create(dictionary);

    @Test
    public void testBasicTokenize() {

        final List<Token<MeCabLikeTermFeatures>> tokens = tokenizer.tokenize("GoogleがAndroid向け点字キーボードを発表");

        Assert.assertEquals(8, tokens.size());

        Assert.assertEquals("Google", tokens.get(0).getText());
        Assert.assertEquals("名詞", tokens.get(0).getFeatures().getPartOfSpeech());

        Assert.assertEquals("が", tokens.get(1).getText());
        Assert.assertEquals("助詞", tokens.get(1).getFeatures().getPartOfSpeech());

        Assert.assertEquals("Android", tokens.get(2).getText());
        Assert.assertEquals("名詞", tokens.get(2).getFeatures().getPartOfSpeech());

        Assert.assertEquals("向け", tokens.get(3).getText());
        Assert.assertEquals("名詞", tokens.get(3).getFeatures().getPartOfSpeech());
        Assert.assertEquals("接尾", tokens.get(3).getFeatures().getPartOfSpeechSubCategory1());
    }
}
