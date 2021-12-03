package com.emailparser;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UrlToContent implements IExtractHtmlByUrl {
    @Override
    public String extractHtmlContentFromUrl(String url) {
        String htmlContent = null;
        try {
            htmlContent =  IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
        } catch (MalformedURLException e) {
            System.out.println("Wrong url! " + e.getMessage() + " for url " + url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return htmlContent;
    }
}
