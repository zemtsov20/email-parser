package emailparser;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;

import static emailparser.Patterns.FIRST_AND_SECOND_DOMAIN;

public class Parser {

    //https://en.wikipedia.org/wiki/Coupling_(computer_programming)
    public static HashSet<String> parse(String url, int depth) {
        ArrayDeque<Node> urlQueue = new ArrayDeque<>();
        Set<String> addedUrls = new HashSet<>();
        HashSet<String> allEmails = new HashSet<>();

        // func that takes url and returns html of this page
        Function <String, String> url2content = str -> {
            String htmlContent = null;
            try {
                htmlContent =  IOUtils.toString(new URL(str), StandardCharsets.UTF_8);
                //TODO: you transform string "https://en.wikipedia.org/" into "<!DOCTYPE html><html class="client-nojs" lang="en" dir="ltr">..."
                //TODO: java.util.function.Function
                //TODO: Function<Node, String> node2content = n -> IOUtils.toString(new URL(n.getUrl()), StandardCharsets.UTF_8);
            } catch (MalformedURLException e) {
                System.out.println("Wrong url! " + e.getMessage() + " for url " + str);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return htmlContent;
        };

        Matcher firstSecondDomain = FIRST_AND_SECOND_DOMAIN.matcher(url);
        if (!firstSecondDomain.find()) {
            throw new IllegalArgumentException("Your url '" + url + "' does not match pattern " + FIRST_AND_SECOND_DOMAIN);
        }
        final String siteToMatch = firstSecondDomain.group(1);
        System.out.println("top url: " + siteToMatch);

        Predicate <String> isMatchesSite = str -> str.equals(siteToMatch);

        urlQueue.push(new Node(url, 0));
        //TODO: you can go by same links more than 2 times [done]
        //TODO: you should know which pages you've already visited [done]
        while (!urlQueue.isEmpty()) {
            var node1 = urlQueue.pollLast();
            System.out.println("Now you are in: " + node1.getUrl() + ", depth: " + node1.getDepth());
            Matcher fullUrl = FIRST_AND_SECOND_DOMAIN.matcher(node1.getUrl());
            if (!fullUrl.find()) {
                throw new IllegalArgumentException("Your url '" + url + "' does not match pattern " + FIRST_AND_SECOND_DOMAIN);
            }
            String addToHref = fullUrl.group(0);
            System.out.println("\tadding: " + addToHref);//TODO: may vary based on the page [done]
            String htmlContent = url2content.apply(node1.getUrl());
            if (htmlContent == null) {
                continue;
            }

            // searching and adding emails on page
            Parser.emailsFromPage(allEmails, htmlContent);

            // searching urls on page, adding in queue
            if (node1.getDepth() == depth) {
                continue;
            }

            Matcher urlMatcher = Patterns.WEB_URL2.matcher(htmlContent);
            //TODO: rework to java.util.function.Function<String, List<String>>
            Parser.urlsFromPage(urlQueue, addedUrls, htmlContent, siteToMatch, addToHref, node1.getDepth());
        }

        return allEmails;
    }

    public static void emailsFromPage (HashSet<String> allEmails, String pageHtml) {
        Matcher emailMatcher = Patterns.EMAIL_ADDRESS.matcher(pageHtml);
        while (emailMatcher.find()) {
            //TODO: rework to java.util.function.Function<String, List<String>> [done?]
            allEmails.add(emailMatcher.group());
        }
    }

    public static void urlsFromPage (Deque<Node> urlQueue, Set<String> addedUrls,
                                     String htmlContent, String siteToMatch, String addToHref, int depth) {
        Predicate <String> isMatchesSite = str -> str.equals(siteToMatch);

        Matcher urlMatcher = Patterns.WEB_URL2.matcher(htmlContent);

        //TODO: rework to java.util.function.Function<String, List<String>>
        while (urlMatcher.find()) {
            String tempUrl = urlMatcher.group(2);
            Matcher firstSecondDomain = FIRST_AND_SECOND_DOMAIN.matcher(tempUrl);
            if (firstSecondDomain.find() && isMatchesSite.test(firstSecondDomain.group(1))) {
                //TODO: "firstSecondDomain.group(1).equals(siteToMatch)" - this is clause, filter
                //TODO: try to rework to java.util.function.Predicate or some other filter
                //Predicate<String> isKeep = str -> str.contains("www.wiki.org");
                // adding protocol
                tempUrl = urlMatcher.group(3).equals("//") ? "https:" + tempUrl : tempUrl;
                if(!addedUrls.contains(tempUrl)){
                    Node tempNode = new Node(tempUrl, depth + 1);
                    urlQueue.push(tempNode);
                    addedUrls.add(tempUrl);
                }
            } else if (urlMatcher.group(3).equals("/")) {
                if(!addedUrls.contains(addToHref + tempUrl)) {
                    Node tempNode = new Node(addToHref + tempUrl, depth + 1);
                    urlQueue.push(tempNode);
                    addedUrls.add(addToHref + tempUrl);
                }
            }
        }
    }
}
//
//class Temp {
//
//    class Node {
//        String name;
//    }
//
//    void example() {
//
//        Function<String, String> url2content = null;
//
//        Function<String, List<String>> content2urls = null;
//
//        Node root = null;
//
//        Queue<Node> todo = null;
//
//        do {
//            var content = url2content.apply(root.name);
//            var children = content2urls.apply(content);
//            visit(content);
//        } while (true);
//    }
//
//    private void visit(String content) {
//        //extract emails
//    }
//
//}
