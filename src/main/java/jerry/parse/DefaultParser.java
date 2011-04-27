package jerry.parse;

import jerry.Buffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @author Tareq Abedrabbo
 */
public class DefaultParser implements Parser {

    private final static Pattern JSON = Pattern.compile("\\{.*\\}");
    private final static Pattern URL = Pattern.compile("http://.*");
    private final static Pattern EXP = Pattern.compile("(#|\\[).*");

    private ExpressionParser expressionParser;

    private Buffer buffer;

    @Autowired
    public void setExpressionParser(ExpressionParser expressionParser) {
        this.expressionParser = expressionParser;
    }

    @Autowired
    public void setBuffer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public List<Token> parse(String line) {
        Assert.hasText(line);
        return evaluate(tokenise(line));
    }

    private List<Token> tokenise(String line) {
        List<Token> tokens = new ArrayList<Token>();
        Scanner scanner = new Scanner(line);
        String command = scanner.next();
        tokens.add(new Token(Token.Type.COMMAND, command));

        while (scanner.hasNext()) {
            if (scanner.hasNext(URL)) {
                tokens.add(new Token(Token.Type.URL, scanner.next(URL)));
            } else if (scanner.hasNext(JSON)) {
                tokens.add(new Token(Token.Type.JSON, scanner.next(JSON)));
            } else if (scanner.hasNext(EXP)) {
                tokens.add(new Token(Token.Type.EXP, scanner.next(EXP)));
            } else {
                tokens.add(new Token(Token.Type.STRING, scanner.next()));
            }
        }

        return tokens;
    }

    private List<Token> evaluate(List<Token> tokens) {
        for (Token token : tokens) {
            if (token.type == Token.Type.EXP) {
                token.value = evaluateExpression(token.value);
            }
        }
        return tokens;
    }

    private String evaluateExpression(String exp) {
        try {
            EvaluationContext context = new StandardEvaluationContext(buffer);
            bindVariables(context);
            Expression expression = expressionParser.parseExpression(exp);
            return expression.getValue(context, String.class);
        } catch (ParseException e) {
            throw new ParsingException(e.getMessage(), e);
        }
    }

    private void bindVariables(EvaluationContext context) {
        if (buffer.containsKey("_response")) {
            context.setVariable("data", ((ResponseEntity<String>) buffer.get("_response")).getBody());
        }
    }
}
