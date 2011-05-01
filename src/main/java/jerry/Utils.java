package jerry;

import jerry.parse.ParsingException;
import jerry.parse.Token;

import java.util.List;

/**
 * @author Tareq Abedrabbo
 */
public abstract class Utils {

    public static Object getOptional(List<Token> tokens, int position) {
        if (tokens.size() <= position) {
            return null;
        }
        return tokens.get(position).evaluated;
    }

    public static Object getRequired(List<Token> tokens, int position) throws ParsingException {
        validatePosition(tokens, position);
        return tokens.get(position).evaluated;
    }

    public static String getRequiredString(List<Token> tokens, int position) throws ParsingException {
        validatePosition(tokens, position);
        return String.valueOf(tokens.get(position).evaluated);
    }

    public static String getString(List<Token> tokens, int position) throws ParsingException {
        if (tokens.size() <= position) {
            return null;
        }
        return String.valueOf(tokens.get(position).evaluated);
    }

    private static void validatePosition(List<Token> tokens, int position) {
        if (tokens.size() < position + 1) {
            throw new ParsingException("Required option not found at position [" + position + "] for command ["
                    + tokens.get(0).value + "]");
        }
    }

}
