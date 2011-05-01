package jerry.general;

import jerry.Buffer;
import jerry.Settings;
import jerry.Utils;
import jerry.command.Interpreter;
import jerry.parse.ParsingException;
import jerry.parse.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionException;
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

    private Settings settings;

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

    @Autowired
    public void setSettings(Settings settings) {
        this.settings = settings;
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
            String expression = Utils.getRequiredString(tokens, 1);
            return evaluate(expression);
        }

        if (command.equals("set")) {
            String key = Utils.getRequiredString(tokens, 1);
            Object value = Utils.getRequired(tokens, 2);
            buffer.put(key, value);
            return String.valueOf(value);
        }

        if (command.equals("details")) {
            String option = Utils.getString(tokens, 1);
            if (option == null) {
                settings.printResponseDetails = !settings.printResponseDetails;
                return Boolean.toString(settings.printResponseDetails);
            } else {
                Boolean booleanValue = Boolean.valueOf(option);
                settings.printResponseDetails = booleanValue;
                return booleanValue.toString();
            }
        }

        return null;

    }

    private String evaluate(String value) throws ParsingException {
        try {
            Expression expression = expressionParser.parseExpression(value);
            return expression.getValue(evaluationContext).toString();
        } catch (ExpressionException e) {
            throw new ParsingException(e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(Token command) {
        return generalCommands.contains(command.value);
    }
}
