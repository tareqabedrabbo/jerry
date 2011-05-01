package jerry.command;

import jerry.parse.Token;

import java.util.List;

/**
 * @author Tareq Abedrabbo
 */
public interface Interpreter<T> {

    T interpret(List<Token> tokens);

    boolean supports(Token commandToken);
}
