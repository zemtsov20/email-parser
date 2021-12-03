package com.emailparser;

import java.util.Set;

public interface IExtractEmailsFromHtml {
    Set<String> emailsFromPage(String html);
}
