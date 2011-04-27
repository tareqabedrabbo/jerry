package jerry.parse;

import jerry.http.HttpCommand;

/**
 * @author Tareq Abedrabbo
 */
public interface Interpreter {

    HttpCommand interpret(String input);
}
