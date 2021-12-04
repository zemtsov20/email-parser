package emailparser;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;

import static emailparser.Patterns.FIRST_AND_SECOND_DOMAIN;

@FunctionalInterface
interface FunctionOfThree<U, T, V> {
    V apply(U u, T t, V v);
}

interface IParser<F, T>{
    T parse(F from);
}

class ContextToLinksParser implements IParser<String, Set<String>>{

    @Override
    public Set<String> parse(String from) {
        return null;
    }
}

public class Parser {
    public static Set<String> parse(String url, int depth) {
        Deque<String> urlQueue = new ArrayDeque<>();
        Set<String> addedUrls = new HashSet<>();
        Set<String> allEmails = new HashSet<>();

        // func that takes url and returns html of this page
        java.util.function.Function<String, String> url2content = str -> {
            String htmlContent = null;
            try {
                htmlContent =  IOUtils.toString(new URL(str), StandardCharsets.UTF_8);
            } catch (MalformedURLException e) {
                System.out.println("Wrong url! " + e.getMessage() + " for url " + str);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return htmlContent;
        };

        // func that returns set of emails on page
        // parses content and returns emails
        // Parser Transformer Mapper
        java.util.function.Function<String, Set<String>> getEmails = pageHtml -> {
            if(pageHtml == null)
                return null;
            Set<String> newEmails = new HashSet<>();
            Matcher emailMatcher = Patterns.EMAIL_ADDRESS.matcher(pageHtml);
            while (emailMatcher.find()) {
                newEmails.add(emailMatcher.group());
            }

            return newEmails;
        };

        Matcher firstSecondDomain = FIRST_AND_SECOND_DOMAIN.matcher(url);
        if (!firstSecondDomain.find()) {
            throw new IllegalArgumentException("Your url '" + url + "' does not match pattern " + FIRST_AND_SECOND_DOMAIN);
        }
        final String siteToMatch = firstSecondDomain.group(1);
        System.out.println("top url: " + siteToMatch);

        // urls adding function
        FunctionOfThree<String, String, Deque<String>> getUrls = (currUrl, pageHtml, newUrls) -> {
            //newUrls.poll();
            if(pageHtml == null)
                return null;

            Predicate<String> isMatchesSite = str -> str.equals(siteToMatch);

            Matcher urlMatcher = Patterns.WEB_URL2.matcher(pageHtml);

            Matcher fullUrl = FIRST_AND_SECOND_DOMAIN.matcher(currUrl);
            if (!fullUrl.find()) {
                throw new IllegalArgumentException("Your url '" + currUrl + "' does not match pattern " + FIRST_AND_SECOND_DOMAIN);
            }
            String addToHref = fullUrl.group(0);

            while (urlMatcher.find()) {
                String tempUrl = urlMatcher.group(2);
                Matcher newFirstSecondDomain = FIRST_AND_SECOND_DOMAIN.matcher(tempUrl);
                if (newFirstSecondDomain.find() && isMatchesSite.test(newFirstSecondDomain.group(1))) {
                    tempUrl = urlMatcher.group(3).equals("//") ? "https:" + tempUrl : tempUrl;
                    if(!addedUrls.contains(tempUrl)){
                        newUrls.push(tempUrl);
                        addedUrls.add(tempUrl);
                    }
                } else if (urlMatcher.group(3).equals("/")) {
                    if(!addedUrls.contains(addToHref + tempUrl)) {
                        newUrls.push(addToHref + tempUrl);
                        addedUrls.add(addToHref + tempUrl);
                    }
                }
            }


            return newUrls;
        };

        urlQueue.push(url);
        do {
            urlQueue.forEach(e -> allEmails.addAll(getEmails.apply(url2content.apply(e))));
            if(depth > 0)
                urlQueue.forEach(e -> getUrls.apply(e, url2content.apply(e), urlQueue));
        } while (depth-- > 0);

        return allEmails;
    }
}
