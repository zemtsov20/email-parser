package com.emailparser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;

public class UrlsFilter implements IUrlsFilter {
    @Override
    public Deque<String> filter(Deque<String> sourceQueue, Set<String> visitedUrls) {
        Deque<String> newFilteredUrls = new ArrayDeque<>();
        for (var url : sourceQueue) {
            if (visitedUrls.add(url)) {
                newFilteredUrls.push(url);
            }
        }

        return newFilteredUrls;
    }

    @Override
    public Deque<String> filter(Deque<String> sourceQueue, Set<String> visitedUrls, String siteToMatch) {
        Predicate<String> isMatchesSite = str -> str.equals(siteToMatch);

        Deque<String> newFilteredUrls = new ArrayDeque<>();
        for (var url : sourceQueue) {
            Matcher newFirstSecondDomain = Patterns.FIRST_AND_SECOND_DOMAIN.matcher(url);
            if (newFirstSecondDomain.find() &&
                    isMatchesSite.test(newFirstSecondDomain.group(1)) &&
                    visitedUrls.add(url)) {
                newFilteredUrls.push(url);
            }
        }

        return newFilteredUrls;
    }
}
