package jerry.parse;

import jerry.Buffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.ExpressionParser;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import javax.annotation.Resource;
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

    private EvaluationContext evaluationContext;

    private Buffer buffer;

    private List<String> allCommands;

    @Autowired
    public void setExpressionParser(ExpressionParser expressionParser) {
        this.expressionParser = expressionParser;
    }

    @Autowired
    public void setBuffer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Autowired
    public void setEvaluationContext(EvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    @Resource(name = "allCommands")
    public void setAllCommands(List<String> allCommands) {
        this.allCommands = allCommands;
    }

    @Override
    public List<Token> parse(String line) throws ParsingException {
        Assert.hasText(line);
        line = line.trim();
        return evaluate(tokenise(line));
    }

    private List<Token> tokenise(String line) throws ParsingException {
        List<Token> tokens = new ArrayList<Token>();
        Scanner scanner = new Scanner(line);
        String command = scanner.next();
        Token commandToken = new Token(Token.Type.COMMAND, command);
        if (!allCommands.contains(commandToken.value)) {
            throw new ParsingException("Unknown command " + commandToken.value);
        }
        tokens.add(commandToken);

        // eval is a special case
        if (commandToken.value.equals("eval")) {
            tokens.add(new Token(Token.Type.STRING, scanner.nextLine()));
            return tokens;
        }

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
            token.evaluated =
                    token.type == Token.Type.EXP ? evaluateExpression(token.value) : token.value;
        }
        return tokens;
    }

    private Object evaluateExpression(String exp) {
        try {
            bindVariables(evaluationContext);
            Expression expression = expressionParser.parseExpression(exp);
            return expression.getValue(evaluationContext);
        } catch (ExpressionException e) {
            throw new ParsingException(e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ParsingException(e.getMessage(), e);
        }
    }

    private void bindVariables(EvaluationContext context) {
        if (buffer.get("_response") != null) {
            context.setVariable("data", ((ResponseEntity<String>) buffer.get("_response")).getBody());
        }
    }
}
