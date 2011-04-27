package jerry.parse;

/**
 * @author Tareq Abedrabbo
 */
public class Token {

    Type type;
    String value;

    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }

    static enum Type {
        COMMAND, JSON, EXP, URL, STRING;
    }
}
