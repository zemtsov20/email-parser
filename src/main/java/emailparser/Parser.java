package emailparser;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static Document getPage(String url) {
        Document doc = null;
        UrlValidator urlValidator = new UrlValidator();
        try {
            if(urlValidator.isValid(url)){
                doc = Jsoup.connect(url).ignoreContentType(true).get();
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    public static void parse(HashSet<String> allEmails, String url, int depth) {
        Document doc;
        ArrayDeque<Node> urlQueue = new ArrayDeque<>();
        Node node = new Node(url, 0);
        Pattern pattern = Pattern.compile("\\w+\\.\\w+/");
        Matcher matcher = pattern.matcher(url);
        if(!matcher.find()){
            System.out.println("Wrong url!");
            return;
        }
        String searchString = url.substring(matcher.start(), matcher.end() - 1);
        urlQueue.push(node);
        while(!urlQueue.isEmpty() && urlQueue.peekLast().getDepth() <= depth) {
            int tempDepth = urlQueue.peekLast().getDepth();

            System.out.println("Now we are in: " + urlQueue.peekLast().getUrl() + ", depth: " + tempDepth);
            doc = getPage(urlQueue.pollLast().getUrl());
            if(doc == null){
                continue;
            }

            Elements refs = doc.select("a[href]");
            Elements areEmails = doc.select("span:contains(@), a[href]:contains(@)");

            // searching and adding emails on page
            for (Element isEmail : areEmails) {
                Pattern emailPattern = Pattern.compile("[\\w.]+@+\\w+\\.\\w+");
                Matcher emailMatcher = emailPattern.matcher(isEmail.text());
                if(emailMatcher.find()){
                    allEmails.add(isEmail.text().substring(emailMatcher.start(), emailMatcher.end()));
                }
            }

            // searching urls on page, adding in queue
            for (Element ref : refs) {
                if (ref.attr("abs:href").contains(searchString)) {
                    Node tempNode = new Node(ref.attr("abs:href"), tempDepth + 1);
                    urlQueue.push(tempNode);
                }
            }
        }
    }
}
