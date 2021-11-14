import emailparser.Parser;

import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        HashSet<String> allEmails = new HashSet<>();
        String url;
        int depth;


        System.out.print("Enter url: ");
//        url = in.next();
        url = "https://en.wikipedia.org/wiki/Jakarta_Mail";
//        url = "https://mvideo.ru/";
        url = "https://www2.deloitte.com/";
//        url = ""
        System.out.print("Enter search depth: ");
        depth = in.nextInt();

        allEmails = Parser.parse(url, depth);

        System.out.println("Emails list:");
        for (String str : allEmails) {
            System.out.println(str);
        }

    }
}
