package com.gomentr.emailmanager.parsers;

/**
 * Created by Omar Addam on 2015-09-04.
 */
public class ScriptContentParser implements ContentParser {

    static final String SCRIPT_TAG_PATTERN = "(?s)(<|&lt;)script.*(<|&lt;)/script(>|&gt)";

    public String parse(String content) {

        String parsedContent = content;
        parsedContent = parsedContent.replaceAll(SCRIPT_TAG_PATTERN, "");

        return parsedContent;
    }

}
