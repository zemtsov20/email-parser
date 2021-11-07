package emailparser;

import java.util.regex.Pattern;

public class Patterns {
    public static final Pattern FIRST_AND_SECOND_DOMAIN
            = Pattern.compile("(?:https://|http://|//)?" +
            "(?:www.)?[-a-zA-Z0-9@:%._+~#=]{1,128}\\.([a-zA-Z0-9()]{1,64}\\.[a-zA-Z0-9()]{1,64})",
            Pattern.CASE_INSENSITIVE);
    // https://[^/]*?(\w+\.\w+)/

    public static final Pattern EMAIL_ADDRESS
            = Pattern.compile(
            "[a-zA-Z0-9+._%\\-]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

//    public static final Pattern WEB_URL2 = Pattern.compile("<a\\W+[^>]*\\W+href\\W*=\\W*[\"']([^\"']+)[\"'][^>]*>");
    // gr 1 - tag, gr 2 - url, gr 3 - protocol
    public static final Pattern WEB_URL2
            = Pattern.compile("<(a|link|area)\\W?[^>]*\\W+href\\W*=\\W*[\"']((https://|http://|//|/)+[^\"']+)[\"'][^>]*>");
}
