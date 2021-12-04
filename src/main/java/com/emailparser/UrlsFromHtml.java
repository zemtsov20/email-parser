package com.emailparser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;

public class UrlsFromHtml implements IExtractUrlsFromHtml {
    @Override
    public Deque<String> urlsFromHtml(String pageHtml, String currUrl) {
        if(pageHtml == null)
            return null;
        Deque<String> newUrls = new ArrayDeque<>();
        Matcher urlMatcher = Patterns.WEB_URL2.matcher(pageHtml);
        Matcher fullUrl = Patterns.FIRST_AND_SECOND_DOMAIN.matcher(currUrl);
        if (!fullUrl.find()) {
            throw new IllegalArgumentException("Your url '" + currUrl + "' does not match pattern " + Patterns.FIRST_AND_SECOND_DOMAIN);
        }
        String addToHref = fullUrl.group(0);

        while (urlMatcher.find()) {
            String tempUrl = urlMatcher.group(2);
            Matcher newFirstSecondDomain = Patterns.FIRST_AND_SECOND_DOMAIN.matcher(tempUrl);
            if (newFirstSecondDomain.find()) {
                tempUrl = urlMatcher.group(3).equals("//") ? "https:" + tempUrl : tempUrl;
                newUrls.push(tempUrl);
            } else if (urlMatcher.group(3).equals("/")) {
                newUrls.push(addToHref + tempUrl);
            }
        }

        return newUrls;
    }
}
