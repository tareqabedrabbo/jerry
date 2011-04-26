package jerry.parse;

import jerry.http.Get;
import jerry.http.HttpCommand;
import jerry.http.Post;
import jerry.http.Put;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @author Tareq Abedrabbo
 */
public class DefaultParser implements Parser, ApplicationContextAware {

    private ExpressionParser expressionParser;

    private final static Pattern JSON_DATA = Pattern.compile("\\{.*\\}");

    private ApplicationContext applicationContext;

    private Map<String, Object> buffer;

    public void setExpressionParser(ExpressionParser expressionParser) {
        this.expressionParser = expressionParser;
    }

    public void setBuffer(Map<String, Object> buffer) {
        this.buffer = buffer;
    }

    public HttpCommand parse(String input) {
        Assert.hasText(input);
        Scanner scanner = new Scanner(input);
        String command = scanner.next();
        if (command.equals("get")) {
            String url = extractRequiredUrl(scanner, input);
            Get get = applicationContext.getBean(Get.class);
            get.setUrl(url);
            return get;
        }

        if (command.equals("put")) {
            String url = extractRequiredUrl(scanner, input);
            String body = extractData(scanner);

            Put put = applicationContext.getBean(Put.class);
            put.setUrl(url);
            put.setBody(body);
            return put;
        }

        if (command.equals("post")) {
            String url = extractRequiredUrl(scanner, input);
            String body = extractData(scanner);

            Post post = applicationContext.getBean(Post.class);
            post.setUrl(url);
            post.setBody(body);
            return post;
        }

        throw new ParsingException("unable to parse [" + input + "]");
    }

    private String extractData(Scanner scanner) {
        if (!scanner.hasNext()) {
            return null;
        }

        //json data
        if (scanner.hasNext(JSON_DATA)) {
            return scanner.next(JSON_DATA);
        }

        //try spel
        String next = scanner.next();
        return evaluateExpression(next);
    }

    private String evaluateExpression(String exp) {
        try {
            EvaluationContext context = new StandardEvaluationContext(buffer);
            if (buffer.containsKey("_response")) {
                context.setVariable("data", ((ResponseEntity<String>) buffer.get("_response")).getBody());
            }
            Expression expression = expressionParser.parseExpression(exp);
            return expression.getValue(context, String.class);
        } catch (ParseException e) {
            throw new ParsingException(e.getMessage());
        }
    }

    private String extractRequiredUrl(Scanner scanner, String input) {
        if (!scanner.hasNext()) {
            throw new ParsingException("incomplete command [" + input + "]");
        }

        String next = scanner.next();
        if (next.startsWith("http://")) {
            return next;
        }
        // try spel
        return evaluateExpression(next);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
