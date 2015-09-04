package com.gomentr.emailmanager.parsers;

import java.util.regex.Pattern;

/**
 * Created by Omar Addam on 2015-09-04.
 */
public class QuoteContentParser implements ContentParser {

    /** general html tag for quotes */
    static final String BLOCKQUOTE_TAG_WITH_CONTENT = "(?s)(<|&lt;)blockquote.*(<|&lt;)/blockquote(>|&gt)";

    /** general spacers for time and date */
    static final String QUOTE_SPACERS = "[\\s,/\\.\\-]";
    /** matches times */
    static final String TIME_PATTERN = "(?:[0-2])?[0-9]:[0-5][0-9](?::[0-5][0-9])?(?:(?:\\s)?[AP]M)?";
    /** matches day of the week */
    static final String DAY_PATTERN = "(?:(?:Mon(?:day)?)|(?:Tue(?:sday)?)|(?:Wed(?:nesday)?)|(?:Thu(?:rsday)?)|(?:Fri(?:day)?)|(?:Sat(?:urday)?)|(?:Sun(?:day)?))";
    /** matches months (numeric and text) */
    static final String MONTH_PATTERN = "(?:(?:Jan(?:uary)?)|(?:Feb(?:uary)?)|(?:Mar(?:ch)?)|(?:Apr(?:il)?)|(?:May)|(?:Jun(?:e)?)|(?:Jul(?:y)?)" +
            "|(?:Aug(?:ust)?)|(?:Sep(?:tember)?)|(?:Oct(?:ober)?)|(?:Nov(?:ember)?)|(?:Dec(?:ember)?)|(?:[0-1]?[0-9]))";
    /** matches years (only 1000's and 2000's, because we are matching emails) */
    static final String YEAR_PATTERN = "(?:[1-2]?[0-9])[0-9][0-9]";

    /** matches day of the month (number and st, nd, rd, th) */
    static final String DAY_OF_MONTH_PATTERN = "[0-3]?[0-9]" + QUOTE_SPACERS + "*(?:(?:th)|(?:st)|(?:nd)|(?:rd))?";
    /** matches a full date */
    static final String DATE_PATTERN  = "(?:" + DAY_PATTERN + QUOTE_SPACERS + "+)?(?:(?:" + DAY_OF_MONTH_PATTERN + QUOTE_SPACERS + "+" + MONTH_PATTERN + ")|" +
            "(?:" + MONTH_PATTERN + QUOTE_SPACERS + "+" + DAY_OF_MONTH_PATTERN + "))" +
            QUOTE_SPACERS + "+" + YEAR_PATTERN;
    /** matches a date and time combo (in either order) */
    static final String DATE_TIME_PATTERN = "((?:" + DATE_PATTERN + "[\\s,]*((?:(?:at)|(?:@))?\\s*" + TIME_PATTERN + "))?|" +
            "(?:" + TIME_PATTERN + "[\\s,]*(?:on)?\\s*"+ DATE_PATTERN + "))";

    public String parse(String content) {

        String parsedContent = content;
        parsedContent = parsedContent.replaceAll(BLOCKQUOTE_TAG_WITH_CONTENT, "");


        /** matches a leading line such as
         * ----Original Message----
         * or simply
         * ------------------------
         */
        String leadInLine    = "-+\\s*(?:Original(?:\\sMessage)?)?\\s*-+";

        /** matches a header line indicating the date */
        String dateLine    = "(?:(?:date)|(?:sent)|(?:time)):\\s*"+ DATE_TIME_PATTERN + ".*";

        /** matches a subject or address line */
        String subjectOrAddressLine    = "((?:from)|(?:subject)|(?:b?cc)|(?:to)):.*((?:from)|(?:subject)|(?:b?cc)|(?:to)):.*";

        /** matches gmail style quoted text beginning, i.e.
         * On Mon Jun 7, 2010 at 8:50 PM, Simon wrote:
         */
        String gmailQuotedTextBeginning = "(On\\s+" + DATE_TIME_PATTERN + ".*wrote:)";


        /** matches the start of a quoted section of an email */
        Pattern QUOTED_TEXT_BEGINNING = Pattern.compile("(?i)(?:(?:" + leadInLine + ")?" +
                        "(?:(?:" +subjectOrAddressLine + ")|(?:" + dateLine + ")){2,6})|(?:" + gmailQuotedTextBeginning + ")"
        );

        String patternString = "(" + QUOTED_TEXT_BEGINNING.toString() + ").*";
        parsedContent = parsedContent.replaceAll(patternString, "");

        return parsedContent;
    }

}
