package com.emailparser;

import java.util.*;
import java.util.regex.Matcher;

import static com.emailparser.Patterns.FIRST_AND_SECOND_DOMAIN;

public class Parser implements IExtractByUrlWithDepth {
    @Override
    public Set<String> extract(String url, int depth) {
        Deque<String> urlQueue = new ArrayDeque<>();
        Set<String> addedUrls = new HashSet<>();
        Set<String> allEmails = new HashSet<>();

        Matcher firstSecondDomain = FIRST_AND_SECOND_DOMAIN.matcher(url);
        if (!firstSecondDomain.find()) {
            throw new IllegalArgumentException("Your url '" + url + "' does not match pattern " + FIRST_AND_SECOND_DOMAIN);
        }
        final String siteToMatch = firstSecondDomain.group(1);
        System.out.println("top url: " + siteToMatch);

        urlQueue.push(url);
        do {
            urlQueue.forEach(e -> allEmails.addAll(
                    new EmailsFromHtml().emailsFromPage(new UrlToContent().extractHtmlContentFromUrl(e))));
            if (depth > 0) {
                Deque<String> newUrlQueue = new ArrayDeque<>();
                urlQueue.forEach(e -> newUrlQueue.addAll(
                        new UrlsFilter().filter(
                                new UrlsFromHtml().urlsFromHtml(
                                        new UrlToContent().extractHtmlContentFromUrl(e), e), addedUrls, siteToMatch
                        )));
                urlQueue.clear();
                urlQueue.addAll(newUrlQueue);
            }
        } while (depth-- > 0);

        return allEmails;
    }
}
