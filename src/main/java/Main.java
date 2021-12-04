import com.emailparser.Parser;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Set<String> allEmails;
        String url;
        int depth;


//        System.out.print("Enter url: ");
//        url = in.next();
//        url = "https://en.wikipedia.org/wiki/Jakarta_Mail";
        url = "https://www.mvideo.ru/"; // not working
//        url = "https://www2.deloitte.com/";
//        url = ""
        System.out.print("Enter search depth: ");
        depth = in.nextInt();
        allEmails = new Parser().extract(url, depth);
        System.out.println("Emails list:");
        for (String str : allEmails) {
            System.out.println(str);
        }

    }
}
