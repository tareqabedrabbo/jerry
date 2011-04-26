package jerry.parse;

import jerry.http.HttpCommand;

/**
 * @author Tareq Abedrabbo
 */
public interface Parser {

    HttpCommand parse(String input);
}
