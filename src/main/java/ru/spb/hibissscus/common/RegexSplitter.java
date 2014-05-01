package ru.spb.hibissscus.common;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexSplitter {

    private static final Logger LOG = LoggerFactory.getLogger(RegexSplitter.class);

    private Pattern pattern;
    private boolean keepDelimiters;

    /**
     * Constructs a new Splitter object.
     *
     * @param pattern        Pattern to use
     * @param keepDelimiters Flag to keep delimiters
     */
    public RegexSplitter(Pattern pattern, boolean keepDelimiters) {
        this.pattern = pattern;
        this.keepDelimiters = keepDelimiters;
    }

    /**
     * Special string pattern to split big text
     * Note: ! text should be concat with " "
     * todo: numeric
     */
    final static String strSentencesPattern = "(\\S.{1,98}[,\\.\\?!\\$])|(\\S.{1,98}[\\s\\$])|([^\\s].{1,98}[^\\s])";

    /**
     * Compiled pattern to splitting
     */
    final static Pattern sentencesPattern = Pattern.compile(strSentencesPattern, Pattern.MULTILINE);

    /**
     * Splits a text using the pattern.
     */
    public static List<String> splitText(String text) {
        List<String> result = new ArrayList<String>();

        /** test
         Stuttgart is spread across a variety of hills (some of them vineyards), valleys and parks – unusual for a German city[4] and often a source of surprise to visitors who primarily associate the city with its industrial reputation as the 'cradle of the automobile'.
         Stuttgart has the status of Stadtkreis, a type of self-administrating urban county?
         It is also the seat of the state legislature, the regional parliament, local council and the Protestant State Church in Württemberg as well as one of the two co-seats of the bishop of the Roman Catholic Diocese of Rottenburg-Stuttgart.
         */
        Matcher m = sentencesPattern.matcher(text.concat(" "));
        LOG.info(StringUtils.leftPad("", 100, "-"));

        while (m.find()) {
            result.add(m.group());
        }

        for (String s : result) {
            LOG.info(s);
        }

        return result;
    }
}
