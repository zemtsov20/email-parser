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
//            System.out.println("Wrong url!");
            throw new IllegalArgumentException("Your url '" + url + "' does not match pattern " + FIRST_AND_SECOND_DOMAIN);
//            return;
        }
        String site = firstSecondDomain.group(1);
        System.out.println("top url: " + site);
        urlQueue.push(node);
        while (!urlQueue.isEmpty()) { // TODO see 68 str [done]
            var node1 = urlQueue.pollLast();
            System.out.println("Now you are in: " + node1.getUrl() + ", depth: " + node1.getDepth());
            try {
                htmlContent = IOUtils.toString(new URL(node1.getUrl()), StandardCharsets.UTF_8); // TODO rework with IOUtils [done]
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
            Matcher urlMatcher = Patterns.WEB_URL.matcher(htmlContent);
            while (urlMatcher.find()) {
//                if WEB_URL2 then urlMatcher.group(1)
                if (urlMatcher.group() != null && urlMatcher.group().contains(site)) {           // TODO use reg. exp. for urls [done]
                    Node tempNode = new Node(urlMatcher.group(), node1.getDepth() + 1);   // TODO don't put if no need [done]
                    urlQueue.push(tempNode);
                }
            }
        }
    }
}
