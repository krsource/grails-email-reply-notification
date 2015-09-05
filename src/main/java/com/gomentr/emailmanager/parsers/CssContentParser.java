package com.gomentr.emailmanager.parsers;

/**
 * Created by Omar Addam on 2015-09-04.
 */
public class CssContentParser implements ContentParser {

    static final String CSS_TAG_PATTERN = "(?s)(<|&lt;)style.*(<|&lt;)/style(>|&gt)";
    static final String CSS_ATTRIBUTES_PATTERN = "( class=\"[^\"]*\")|( style=\"[^\"]*\")";

    public String parse(String content) {

        String parsedContent = content;
        parsedContent = parsedContent.replaceAll(CSS_TAG_PATTERN, "");
        parsedContent = parsedContent.replaceAll(CSS_ATTRIBUTES_PATTERN, "");

        return parsedContent;
    }

}
