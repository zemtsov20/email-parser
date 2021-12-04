package com.emailparser;

import java.util.Deque;

public interface IExtractUrlsFromHtml {
    Deque<String> urlsFromHtml(String pageHtml, String currUrl);
}
