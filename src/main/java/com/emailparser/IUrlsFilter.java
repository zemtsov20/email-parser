package com.emailparser;

import java.util.Deque;
import java.util.Set;

public interface IUrlsFilter {
    Deque<String> filter(Deque<String> sourceQueue, Set<String> visitedUrls);
    Deque<String> filter(Deque<String> sourceQueue, Set<String> visitedUrls, String siteToMatch);
}
