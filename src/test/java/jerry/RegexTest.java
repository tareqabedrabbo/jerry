package jerry;

import org.junit.Test;

import java.util.Scanner;

/**
 * @author Tareq Abedrabbo
 */
public class RegexTest {

    @Test
    public void put() throws Exception {
        Scanner scanner = new Scanner("put http://127.0.0.1:5984/test/123 {\"message\":\"hello\"} #abc");
        String command = scanner.next();
        String url = scanner.next();
        String body = scanner.next("\\{.*\\}");
        String spel = scanner.next("#.*");
        System.out.println(spel);

//        String line = "@1+2";
//        String expressionString = line.substring(line.indexOf('@') + 1).trim();
//        System.out.println(expressionString);
    }
}
