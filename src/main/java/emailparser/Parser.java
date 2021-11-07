package emailparser;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.regex.Matcher;

import static emailparser.Patterns.FIRST_AND_SECOND_DOMAIN;

public class Parser {

    public static void parse(HashSet<String> allEmails, String url, int depth) throws IOException {
        ArrayDeque<Node> urlQueue = new ArrayDeque<>();
        Node node = new Node(url, 0);
        String htmlContent = null;

        Matcher firstSecondDomain = FIRST_AND_SECOND_DOMAIN.matcher(url);
        if (!firstSecondDomain.find()) {
            throw new IllegalArgumentException("Your url '" + url + "' does not match pattern " + FIRST_AND_SECOND_DOMAIN);
        }
        String siteToMatch = firstSecondDomain.group(1);
        String addToHref = firstSecondDomain.group(0);
        System.out.println("top url: " + siteToMatch);
        System.out.println("adding: " + addToHref);
        urlQueue.push(node);
        while (!urlQueue.isEmpty()) { // TODO see 68 str [done]
            var node1 = urlQueue.pollLast();
            System.out.println("Now you are in: " + node1.getUrl() + ", depth: " + node1.getDepth());
            try {
                htmlContent = IOUtils.toString(new URL(node1.getUrl()), StandardCharsets.UTF_8);
            } catch (MalformedURLException e) {
                System.out.println("Wrong url! " + e.getMessage() + " for url " + node1.getUrl());
            }
            if (htmlContent == null) {
                continue;
            }

            // searching and adding emails on page
            Matcher emailMatcher = Patterns.EMAIL_ADDRESS.matcher(htmlContent);
            while (emailMatcher.find()) {
                allEmails.add(emailMatcher.group());
            }

            // searching urls on page, adding in queue
            if (node1.getDepth() == depth) {
                continue;
            }
            Matcher urlMatcher = Patterns.WEB_URL2.matcher(htmlContent);
            while (urlMatcher.find()) {
                //assert urlMatcher.group() != null;
//                if WEB_URL2 then urlMatcher.group(1)
                String tempUrl = urlMatcher.group(2);
                firstSecondDomain = FIRST_AND_SECOND_DOMAIN.matcher(tempUrl);
                if (firstSecondDomain.find() && firstSecondDomain.group(1).equals(siteToMatch)) {
                    // adding protocol
                    tempUrl = urlMatcher.group(3).equals("//") ? "https:" + tempUrl : tempUrl;
                    Node tempNode = new Node(tempUrl, node1.getDepth() + 1);
                    urlQueue.push(tempNode);
                }
                else if(urlMatcher.group(3).equals("/")) {
                    Node tempNode = new Node(addToHref + tempUrl, node1.getDepth() + 1);
                    urlQueue.push(tempNode);
                }
            }
        }
    }
}
