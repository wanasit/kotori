package com.github.wanasit.kotori;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestTokenizer {

    final Tokenizer tokenizer = Tokenizer.createDefaultTokenizer();
    
    @Test
    public void testBasicTokenize() {
        
        final List<Token> tokens = tokenizer.tokenize("そこではなしは終わりになった");

        Assert.assertEquals(7, tokens.size());

        Assert.assertEquals("そこで", tokens.get(0).getText());
        Assert.assertEquals("はなし", tokens.get(1).getText());
        Assert.assertEquals("は", tokens.get(2).getText());
        Assert.assertEquals("終わり", tokens.get(3).getText());
        Assert.assertEquals("に", tokens.get(4).getText());
        Assert.assertEquals("なっ", tokens.get(5).getText());
        Assert.assertEquals("た", tokens.get(6).getText());
    }
}
