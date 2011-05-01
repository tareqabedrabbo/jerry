package jerry.general;

import jerry.Buffer;
import jerry.command.Interpreter;
import jerry.parse.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Tareq Abedrabbo
 */
public class GeneralCommandInterpreter implements Interpreter<String> {

    private Buffer buffer;

    private ExpressionParser expressionParser;

    private EvaluationContext evaluationContext;

    private List<String> generalCommands;


    @Autowired
    public void setEvaluationContext(EvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    @Autowired
    public void setBuffer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Resource(name = "generalCommands")
    public void setGeneralCommands(List<String> generalCommands) {
        this.generalCommands = generalCommands;
    }

    @Autowired
    public void setExpressionParser(ExpressionParser expressionParser) {
        this.expressionParser = expressionParser;
    }

    @Override
    public String interpret(List<Token> tokens) {
        String command = tokens.get(0).value;
        if (command.equals("quit") || command.equals("exit")) {
            System.exit(0);
        }

        if (command.equals("buffer")) {
            return buffer.toString();
        }

        if (command.equals("eval")) {
            return evaluate(tokens.get(1).value);
        }

        return null;

    }

    private String evaluate(String value) {
        Expression expression = expressionParser.parseExpression(value);
        return expression.getValue(evaluationContext).toString();
    }

    @Override
    public boolean supports(Token command) {
        return generalCommands.contains(command.value);
    }
}
