package com.emailparser;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

public class EmailsFromHtml implements IExtractEmailsFromHtml {
    @Override
    public Set<String> emailsFromPage(String pageHtml) {
        if(pageHtml == null)
            return null;
        Set<String> newEmails = new HashSet<>();
        Matcher emailMatcher = Patterns.EMAIL_ADDRESS.matcher(pageHtml);
        while (emailMatcher.find()) {
            newEmails.add(emailMatcher.group());
        }

        return newEmails;
    }
}
