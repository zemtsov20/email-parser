package emailparser;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;

import static emailparser.Patterns.FIRST_AND_SECOND_DOMAIN;

public class Parser {

    //https://en.wikipedia.org/wiki/Coupling_(computer_programming)
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
        System.out.println("adding: " + addToHref);//TODO: may vary based on the page
        urlQueue.push(node);
        //TODO: you can go by same links more than 2 times
        //TODO: you should know which pages you've already visited
        while (!urlQueue.isEmpty()) { // TODO see 68 str [done]
            var node1 = urlQueue.pollLast();
            System.out.println("Now you are in: " + node1.getUrl() + ", depth: " + node1.getDepth());
            try {
                htmlContent = IOUtils.toString(new URL(node1.getUrl()), StandardCharsets.UTF_8);
                //TODO: you transform string "https://en.wikipedia.org/" into "<!DOCTYPE html><html class="client-nojs" lang="en" dir="ltr">..."
                //TODO: java.util.function.Function
                //TODO: Function<Node, String> node2content = n -> IOUtils.toString(new URL(n.getUrl()), StandardCharsets.UTF_8);
            } catch (MalformedURLException e) {
                System.out.println("Wrong url! " + e.getMessage() + " for url " + node1.getUrl());
            }
            if (htmlContent == null) {
                continue;
            }

            // searching and adding emails on page
            Matcher emailMatcher = Patterns.EMAIL_ADDRESS.matcher(htmlContent);
            while (emailMatcher.find()) {
                //TODO: rework to java.util.function.Function<String, List<String>>
                allEmails.add(emailMatcher.group());
            }

            // searching urls on page, adding in queue
            if (node1.getDepth() == depth) {
                continue;
            }
            Matcher urlMatcher = Patterns.WEB_URL2.matcher(htmlContent);
            //TODO: rework to java.util.function.Function<String, List<String>>
            while (urlMatcher.find()) {
                //assert urlMatcher.group() != null;
//                if WEB_URL2 then urlMatcher.group(1)
                String tempUrl = urlMatcher.group(2);
                firstSecondDomain = FIRST_AND_SECOND_DOMAIN.matcher(tempUrl);
                if (firstSecondDomain.find() && firstSecondDomain.group(1).equals(siteToMatch)) {
                    //TODO: "firstSecondDomain.group(1).equals(siteToMatch)" - this is clause, filter
                    //TODO: try to rework to java.util.function.Predicate or some other filter
                    //Predicate<String> isKeep = str -> str.contains("www.wiki.org");
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
