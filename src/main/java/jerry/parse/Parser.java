package jerry.parse;

import java.util.List;

/**
 * @author Tareq Abedrabbo
 */
public interface Parser {

    List<Token> parse(String line);
}
