package com.gomentr.emailmanager.parsers;

/**
 * Created by Omar Addam on 2015-09-04.
 */
public class HtmlContentParser implements ContentParser {

    static final String HEAD_TAG_WITH_CONTENT = "(?s)(<|&lt;)head.*(<|&lt;)/head(>|&gt)";
    static final String HTML_TAGS_ONLY = "(<html[^>]*>)|(<body[^>]*>)|(<span[^>]*>)|(</html>)|(</body>)|(</span>)";

    public String parse(String content) {

        String parsedContent = content;
        parsedContent = parsedContent.replaceAll(HEAD_TAG_WITH_CONTENT, "");
        parsedContent = parsedContent.replaceAll(HEAD_TAG_WITH_CONTENT, "");

        return parsedContent;
    }

}
