package com.gomentr.emailmanager.parsers;

/**
 * Created by Omar Addam on 2015-09-04.
 */
public class GmailContentParser implements ContentParser {

    static final String GMAIL_EXTRA_DIV_TAG_WITH_CONTENT = "(?s)(<|&lt;)div class=\"gmail_extra\".*";

    public String parse(String content) {

        String parsedContent = content;
        parsedContent = parsedContent.replaceAll(GMAIL_EXTRA_DIV_TAG_WITH_CONTENT, "");

        return parsedContent;
    }

}