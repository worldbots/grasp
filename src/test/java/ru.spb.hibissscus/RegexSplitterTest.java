package ru.spb.hibissscus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import ru.spb.hibissscus.common.RegexSplitter;

public class RegexSplitterTest {

    // public static void run_tests() {
    String[][] test_cases = {
            // Limit cases:
            // 'null' to be splitted with regexp 'null' gives []
            { null, null },
            // '' to be splitted with regexp 'null' gives []
            { "", null },
            // 'null' to be splitted with regexp '' gives []
            { null, "" },
            // '' to be splitted with regexp '' gives []
            { "", "" },

            // Border cases:
            // 'abcd' to be splitted with regexp 'ab' gives [ab], 'cd', []
            { "abcd", "ab" },
            // 'abcd' to be splitted with regexp 'cd' gives [], 'ab', [cd]
            { "abcd", "cd" },
            // 'abcd' to be splitted with regexp 'abcd' gives [abcd]
            { "abcd", "abcd" },
            // 'abcd' to be splitted with regexp 'bc' gives [], 'a', [bc], 'd',
            // []
            { "abcd", "bc" },

            // Real cases:
            // 'abcd efg hi j' to be splitted with regexp '[ \t\n\r\f]+'
            // gives [], 'abcd', [ ], 'efg', [ ], 'hi', [ ], 'j', []
            { "abcd    efg  hi   j", "[ \\t\\n\\r\\f]+" },
            // ''ab','cd','eg'' to be splitted with regexp '\W+'
            // gives ['], 'ab', [','], 'cd', [','], 'eg', [']
            { "'ab','cd','eg'", "\\W+" },

            // Split-like cases:
            // 'boo:and:foo' to be splitted with regexp ':'
            // gives [], 'boo', [:], 'and', [:], 'foo', []
            { "boo:and:foo", ":" },
            // 'boo:and:foo' to be splitted with regexp 'o'
            // gives [], 'b', [o], '', [o], ':and:f', [o], '', [o]
            { "boo:and:foo", "o" },
            // 'boo:and:foo' to be splitted with regexp 'o+'
            // gives [], 'b', [oo], ':and:f', [oo]
            { "boo:and:foo", "o+" }
    };

    /**
     * Border Split tests
     */
    @Test
    public void testBorderSplit() throws Exception {

        List<TestCaseData> testList = new ArrayList<TestCaseData>();
        // 'abcd' to be splitted with regexp 'ab' gives [ab], 'cd', []
        testList.add(new TestCaseData("abcd", "ab", "ab", "cd"));
        // 'abcd' to be splitted with regexp 'cd' gives [], 'ab', [cd]
        testList.add(new TestCaseData("abcd", "cd", "ab", "cd"));
        // 'abcd' to be splitted with regexp 'abcd' gives [abcd]
        testList.add(new TestCaseData("abcd", "abcd", "abcd"));
        // 'abcd' to be splitted with regexp 'bc' gives [], 'a', [bc], 'd', []
        testList.add(new TestCaseData("abcd", "bc", "a", "d"));

        for (TestCaseData testCaseData : testList) {
            RegexSplitter splitter =
                    new RegexSplitter(testCaseData.pattern, true);

            Assert.assertEquals(testCaseData.expected,
                    splitter.splitText(testCaseData.text));
        }
    }

    private static class TestCaseData {
        public final String text;

        public final Pattern pattern;

        public final List<String> expected;

        private TestCaseData(String text, String patternStr, String... expected) {
            this.text = text;
            this.pattern = Pattern.compile(patternStr);
            this.expected = Arrays.asList(expected);
        }
    }

}
