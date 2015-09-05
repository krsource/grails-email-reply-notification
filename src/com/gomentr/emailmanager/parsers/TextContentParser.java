package com.gomentr.emailmanager.parsers;

/**
 * Created by Omar Addam on 2015-09-04.
 */
public class TextContentParser implements ContentParser {

    public String parse(String content) {
        String parsedContent = "";

        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line == "")
                continue;
            if (!line.startsWith(">"))
                parsedContent += line + "\n";
        }

        if (parsedContent.length() > 0 && parsedContent.charAt(parsedContent.length()-1)=='\n')
            parsedContent = parsedContent.substring(0, parsedContent.length() - 1);

        return parsedContent;
    }

}
